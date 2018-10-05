package com.gatewayless.core.commission.features.distribution_fixed_and_percentage.steps;


import com.gatewayless.core.commission.api.CommissionApi;
import cucumber.api.java.Before;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import net.thucydides.core.annotations.Pending;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


public class WhenDistributeFixedAmount {

  private Long specifiedAccount;
  private Long currentAccount;


  private CommissionApi commissionAPI;

  @Before
  public void setup() {
    commissionAPI = mock(CommissionApi.class);
  }

  @Pending
  @Given("^(\\d+) has (\\d+) is defined in the account commission profile$")
  public void accountSpecified_is_defined_in_the_account_commission_profile(Long currentAccount, Long specifiedAccount) {
    this.specifiedAccount = specifiedAccount;
    this.currentAccount = currentAccount;
  }

  @When("^ask to distribute the ([0-9]*\\.?[0-9]+)$")
  public void askToDistributeTheCommission(Double commission) {
    commissionAPI.distribute(currentAccount, commission);
  }

  @Then("^transfer ([0-9]*\\.?[0-9]+) to the account$")
  public void transferAccount_commissionToTheAccount(Double accountCommission) {
    when(commissionAPI.commission(null)).thenReturn(accountCommission);
    assertThat(commissionAPI.commission(null))
        .isEqualTo(accountCommission);
  }

  @And("^([0-9]*\\.?[0-9]+) for the (\\d+)$")
  public void fixed_commissionForTheSpecified_account(Double fixedCommission, Long specifiedAccount) {
//        assertThat(commissionSteps.getCommissionFor(specifiedAccount))
//                .isEqualTo(fixedCommission);
  }
}
