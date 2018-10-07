package com.gatewayless.core.commission.service;

import com.gatewayless.core.commission.model.CommissionDistribution;
import com.gatewayless.core.commission.model.CommissionProfileDefinition;
import com.gatewayless.core.commission.model.CommissionStep;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDateTime;
import java.util.List;

import static com.gatewayless.core.commission.model.CommissionStep.StepType.FIXED;
import static com.gatewayless.core.commission.model.CommissionStep.StepType.PERCENTAGE;
import static java.util.Arrays.asList;
import static org.apache.commons.lang.math.NumberUtils.DOUBLE_ZERO;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
public class CommissionServiceTest {

  @Mock
  private CommissionDistributor commissionDistributor;

  @InjectMocks
  private CommissionService commissionService;

  @Before
  public void setUp() {
  }

  @Test
  public void shouldDistributeFullCommissionIfCommissionLessThanParentFixed() {
    Long sourceAccount = 1111L;
    Long parentAccount = 2222L;
    Double commission = 50.0;
    CommissionStep sourceAccountStep = new CommissionStep(PERCENTAGE, 100.0, sourceAccount);
    sourceAccountStep.isRootAccont(true);
    CommissionProfileDefinition profile = CommissionProfileDefinition.builder()
        .step(new CommissionStep(FIXED, 100.0, parentAccount))
        .step(sourceAccountStep)
        .build();

    when(commissionDistributor.distributeFullyToSourceAccount(profile, commission)).thenReturn(asList(
        CommissionDistribution.builder()
            .account(parentAccount)
            .commission(DOUBLE_ZERO)
            .at(LocalDateTime.now())
            .build(),
        CommissionDistribution.builder()
            .account(sourceAccount)
            .commission(commission)
            .at(LocalDateTime.now())
            .build()
    ));

    List<CommissionDistribution> actualCommissionDistributions = commissionService.distribute(profile, commission);
    assertThat(actualCommissionDistributions)
        .extracting(CommissionDistribution::commission)
        .containsExactly(DOUBLE_ZERO, commission);
  }

  @Test
  public void shouldDistributeToParentFixedAndRemainingToSourceAccount() {
    Long sourceAccount = 1111L;
    Long parentAccount = 2222L;
    Double commission = 150.0;
    Double parentCommission = 100.0;
    Double sourceAccountCommission = commission - parentCommission;
    CommissionStep sourceAccountStep = new CommissionStep(PERCENTAGE, 100.0, sourceAccount);
    sourceAccountStep.isRootAccont(true);
    CommissionProfileDefinition profile = CommissionProfileDefinition.builder()
        .step(new CommissionStep(FIXED, 100.0, parentAccount))
        .step(sourceAccountStep)
        .build();

    when(commissionDistributor.distribute(profile, commission)).thenReturn(asList(
        CommissionDistribution.builder()
            .account(parentAccount)
            .commission(parentCommission)
            .at(LocalDateTime.now())
            .build(),
        CommissionDistribution.builder()
            .account(sourceAccount)
            .commission(commission - parentCommission)
            .at(LocalDateTime.now())
            .build()
    ));

    List<CommissionDistribution> actualCommissionDistributions = commissionService.distribute(profile, commission);
    assertThat(actualCommissionDistributions)
        .extracting(CommissionDistribution::commission)
        .containsExactly(parentCommission, sourceAccountCommission);
  }
}