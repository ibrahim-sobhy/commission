package com.gatewayless.core.commission.api;

import com.gatewayless.core.commission.model.CommissionDistribution;
import com.gatewayless.core.commission.model.CommissionProfileDefinition;
import com.gatewayless.core.commission.model.CommissionStep;
import com.gatewayless.core.commission.service.CommissionProfile;
import com.gatewayless.core.commission.service.CommissionService;
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
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.Mockito.*;

@RunWith(SpringRunner.class)
public class CommissionAPITest {

  @Mock
  CommissionProfile commissionProfile;

  @Mock
  CommissionService commissionService;

  @InjectMocks
  private CommissionApi commissionApi;
  private Long sourceAccount;

  @Before
  public void setup() {
    sourceAccount = 1234L;
  }

  @Test
  public void shouldThrowIllegalArgumentExceptionIfSourceAccountIsMissing() {
    sourceAccount = null;
    Double commission = 0.0;
    Throwable thrown = catchThrowable(() -> commissionApi.distribute(sourceAccount, commission));
    assertThat(thrown).isInstanceOf(IllegalArgumentException.class)
        .hasNoCause()
        .hasMessageStartingWith("account should not be empty");
  }

  @Test
  public void shouldThrowIllegalArgumentExceptionIfAccountIsEmpty() {
    sourceAccount = 0L;
    Double commission = 5.0;
    Throwable thrown = catchThrowable(() -> commissionApi.distribute(sourceAccount, commission));
    assertThat(thrown).isInstanceOf(IllegalArgumentException.class)
        .hasNoCause()
        .hasMessageStartingWith("account should not be empty");
  }

  @Test
  public void shouldThrowIllegalArgumentExceptionIfCommmissionIsMissing() {
    Double commission = null;
    Throwable thrown = catchThrowable(() -> commissionApi.distribute(sourceAccount, commission));
    assertThat(thrown).isInstanceOf(IllegalArgumentException.class)
        .hasNoCause()
        .hasMessageStartingWith("commission should not be empty");
  }

  @Test
  public void shouldThrowIllegalArgumentExceptionIfCommmissionIsEmpty() {
    Double commission = 0.0;
    Throwable thrown = catchThrowable(() -> commissionApi.distribute(sourceAccount, commission));
    assertThat(thrown).isInstanceOf(IllegalArgumentException.class)
        .hasNoCause()
        .hasMessageStartingWith("commission should not be empty");
  }

  @Test
  public void shouldDistributeTheFullCommissionToTheAccountIfCommissionSmallerThanParent() {
    Double commission = 50.0;
    List<CommissionDistribution> expectedCommissions = asList(CommissionDistribution.builder()
        .account(sourceAccount)
        .commission(commission)
        .build()
    );
    CommissionProfileDefinition profile = CommissionProfileDefinition.builder()
        .step(new CommissionStep(FIXED, 100.0))
        .build();
    when(commissionProfile
        .findByAccount(sourceAccount))
        .thenReturn(profile);
    when(commissionService
        .distribute(profile, commission))
        .thenReturn(expectedCommissions);
    assertThat(commissionApi.distribute(sourceAccount, commission))
        .containsExactlyElementsOf(expectedCommissions);
    verify(commissionService, times(1)).distribute(profile, commission);
  }

  @Test
  public void shouldDistributeBetweenAccountAndHisParentIfDefined() {
    Double commission = 200.0;
    Double parentPercentage = 25.0;
    Double parentCommission = commission * 25 / 100;
    Double sourceAccountCommission = commission - parentCommission;
    Long parentAccount = 1111L;
    List<CommissionDistribution> expectedCommissions = asList(
        CommissionDistribution.builder()
            .account(sourceAccount)
            .commission(sourceAccountCommission)
            .build(),
        CommissionDistribution.builder()
            .account(parentAccount)
            .commission(parentCommission)
            .build()
    );
    CommissionProfileDefinition profile = CommissionProfileDefinition.builder()
        .step(new CommissionStep(PERCENTAGE, parentPercentage))
        .build();
    when(commissionProfile
        .findByAccount(sourceAccount))
        .thenReturn(profile);
    when(commissionService
        .distribute(profile, commission))
        .thenReturn(expectedCommissions);
    assertThat(commissionApi.distribute(sourceAccount, commission))
        .containsExactlyElementsOf(expectedCommissions);
    verify(commissionService, times(1)).distribute(profile, commission);
  }

  @Test
  public void shouldFetchCommissionHistoryForAccountWithinDates() {
    LocalDateTime from = LocalDateTime.now().minusDays(5);
    LocalDateTime to = LocalDateTime.now().minusDays(10);

    List<CommissionDistribution> expectedCommissions = asList(
        CommissionDistribution.builder()
            .account(sourceAccount)
            .commission(50.0)
            .at(LocalDateTime.now().minusDays(5))
            .build(),
        CommissionDistribution.builder()
            .account(sourceAccount)
            .commission(70.0)
            .at(LocalDateTime.now().minusDays(7))
            .build(),
        CommissionDistribution.builder()
            .account(sourceAccount)
            .commission(30.0)
            .at(LocalDateTime.now().minusDays(9))
            .build()
    );

    when(commissionService.lookupHistory(sourceAccount, from, to))
        .thenReturn(expectedCommissions);
    List<CommissionDistribution> actualCommissions = commissionApi.history(sourceAccount, from, to);
    assertThat(actualCommissions)
        .containsExactlyElementsOf(expectedCommissions);
    verify(commissionService, times(1)).lookupHistory(sourceAccount, from, to);
  }

  @Test
  public void shouldFetchCommissionHistoryForAccountForLastDayIfThereIsNoDates() {

    List<CommissionDistribution> expectedCommissions = asList(
        CommissionDistribution.builder()
            .account(sourceAccount)
            .commission(50.0)
            .at(LocalDateTime.now().minusDays(5))
            .build(),
        CommissionDistribution.builder()
            .account(sourceAccount)
            .commission(70.0)
            .at(LocalDateTime.now().minusDays(7))
            .build(),
        CommissionDistribution.builder()
            .account(sourceAccount)
            .commission(30.0)
            .at(LocalDateTime.now().minusDays(9))
            .build()
    );

    when(commissionService.lookupHistory(sourceAccount))
        .thenReturn(expectedCommissions);
    List<CommissionDistribution> actualCommissions = commissionApi.history(sourceAccount, null, null);
    assertThat(actualCommissions)
        .containsExactlyElementsOf(expectedCommissions);
    verify(commissionService, times(1)).lookupHistory(sourceAccount);
  }

  @Test
  public void shouldReturnEmptyHistoryIfToDateBeforeFromDate() {
    LocalDateTime from = LocalDateTime.now().minusDays(4);
    LocalDateTime wrongTo = LocalDateTime.now().minusDays(2);

    System.out.println(from);
    System.out.println(wrongTo);
    if (from.isBefore(wrongTo)) {
      System.out.println("Date are wrong");
    }

    List<CommissionDistribution> actualCommissions = commissionApi.history(sourceAccount, from, wrongTo);
    assertThat(actualCommissions)
        .isEmpty();
    verify(commissionService, never()).lookupHistory(sourceAccount, from, wrongTo);
  }
}
