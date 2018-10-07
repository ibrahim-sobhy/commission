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

    when(commissionDistributor.isCommissionSmallerThanDistribution(profile, commission))
        .thenReturn(true);

    List<CommissionDistribution> actualCommissionDistributions = commissionService.distribute(profile, commission);
    assertThat(actualCommissionDistributions)
        .extracting(CommissionDistribution::commission)
        .containsExactly(DOUBLE_ZERO, commission);
  }

  @Test
  public void shouldDistributeFullCommissionIfCommissionLessThanAllParentWithFixed() {
    Long sourceAccount = 1111L;
    Long parentAccount1 = 2222L;
    Long parentAccount2 = 3333L;
    Double commission = 50.0;
    CommissionStep sourceAccountStep = new CommissionStep(PERCENTAGE, 100.0, sourceAccount);
    sourceAccountStep.isRootAccont(true);
    CommissionProfileDefinition profile = CommissionProfileDefinition.builder()
        .step(new CommissionStep(FIXED, 30.0, parentAccount1))
        .step(new CommissionStep(FIXED, 40.0, parentAccount1))
        .step(sourceAccountStep)
        .build();

    when(commissionDistributor.distributeFullyToSourceAccount(profile, commission)).thenReturn(asList(
        CommissionDistribution.builder()
            .account(parentAccount1)
            .commission(DOUBLE_ZERO)
            .at(LocalDateTime.now())
            .build(),
        CommissionDistribution.builder()
            .account(parentAccount2)
            .commission(DOUBLE_ZERO)
            .at(LocalDateTime.now())
            .build(),
        CommissionDistribution.builder()
            .account(sourceAccount)
            .commission(commission)
            .at(LocalDateTime.now())
            .build()
    ));

    when(commissionDistributor.isCommissionSmallerThanDistribution(profile, commission))
        .thenReturn(true);

    List<CommissionDistribution> actualCommissionDistributions = commissionService.distribute(profile, commission);
    assertThat(actualCommissionDistributions)
        .extracting(CommissionDistribution::commission)
        .containsExactly(DOUBLE_ZERO, DOUBLE_ZERO, commission);
  }

  @Test
  public void shouldDistributeFullCommissionIfCommissionLessThanAllParents() {
    Long sourceAccount = 1111L;
    Long parentAccount1 = 2222L;
    Long parentAccount2 = 3333L;
    Double commission = 50.0;
    CommissionStep sourceAccountStep = new CommissionStep(PERCENTAGE, 100.0, sourceAccount);
    sourceAccountStep.isRootAccont(true);
    CommissionProfileDefinition profile = CommissionProfileDefinition.builder()
        .step(new CommissionStep(FIXED, 40.0, parentAccount1))
        .step(new CommissionStep(PERCENTAGE, 50.0, parentAccount1))
        .step(sourceAccountStep)
        .build();

    when(commissionDistributor.distributeFullyToSourceAccount(profile, commission)).thenReturn(asList(
        CommissionDistribution.builder()
            .account(parentAccount1)
            .commission(DOUBLE_ZERO)
            .at(LocalDateTime.now())
            .build(),
        CommissionDistribution.builder()
            .account(parentAccount2)
            .commission(DOUBLE_ZERO)
            .at(LocalDateTime.now())
            .build(),
        CommissionDistribution.builder()
            .account(sourceAccount)
            .commission(commission)
            .at(LocalDateTime.now())
            .build()
    ));

    when(commissionDistributor.isCommissionSmallerThanDistribution(profile, commission))
        .thenReturn(true);

    List<CommissionDistribution> actualCommissionDistributions = commissionService.distribute(profile, commission);
    assertThat(actualCommissionDistributions)
        .extracting(CommissionDistribution::commission)
        .containsExactly(DOUBLE_ZERO, DOUBLE_ZERO, commission);
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

    when(commissionDistributor.isCommissionSmallerThanDistribution(profile, commission))
        .thenReturn(false);

    List<CommissionDistribution> actualCommissionDistributions = commissionService.distribute(profile, commission);
    assertThat(actualCommissionDistributions)
        .extracting(CommissionDistribution::commission)
        .containsExactly(parentCommission, sourceAccountCommission);
  }

  @Test
  public void shouldDistributeToParentsAndRemainingToSourceAccount() {
    Long sourceAccount = 1111L;
    Long parentAccount1 = 2222L;
    Long parentAccount2 = 3333L;
    Double commission = 150.0;
    Double parent1Commission = 100.0;
    Double parent2Commission = commission * 10 / 100.0;
    Double sourceAccountCommission = commission - parent1Commission - parent2Commission;
    CommissionStep sourceAccountStep = new CommissionStep(PERCENTAGE, 100.0, sourceAccount);
    sourceAccountStep.isRootAccont(true);
    CommissionProfileDefinition profile = CommissionProfileDefinition.builder()
        .step(new CommissionStep(FIXED, 100.0, parentAccount1))
        .step(new CommissionStep(PERCENTAGE, 10.0, parentAccount1))
        .step(sourceAccountStep)
        .build();

    when(commissionDistributor.distribute(profile, commission)).thenReturn(asList(
        CommissionDistribution.builder()
            .account(parentAccount1)
            .commission(parent1Commission)
            .at(LocalDateTime.now())
            .build(),
        CommissionDistribution.builder()
            .account(parentAccount2)
            .commission(parent2Commission)
            .at(LocalDateTime.now())
            .build(),
        CommissionDistribution.builder()
            .account(sourceAccount)
            .commission(commission - parent1Commission - parent2Commission)
            .at(LocalDateTime.now())
            .build()
    ));

    when(commissionDistributor.isCommissionSmallerThanDistribution(profile, commission))
        .thenReturn(false);

    List<CommissionDistribution> actualCommissionDistributions = commissionService.distribute(profile, commission);
    assertThat(actualCommissionDistributions)
        .extracting(CommissionDistribution::commission)
        .containsExactly(parent1Commission, parent2Commission, sourceAccountCommission);
  }
}