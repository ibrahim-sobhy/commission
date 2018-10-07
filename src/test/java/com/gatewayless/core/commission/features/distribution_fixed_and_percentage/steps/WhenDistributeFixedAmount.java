package com.gatewayless.core.commission.features.distribution_fixed_and_percentage.steps;


import com.gatewayless.core.commission.api.CommissionApi;
import com.gatewayless.core.commission.model.CommissionDistribution;
import cucumber.api.java.Before;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import net.thucydides.core.annotations.Pending;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;


public class WhenDistributeFixedAmount {

  private Long specifiedAccount;
  private Long currentAccount;


  private CommissionApi commissionAPI;
  private List<CommissionDistribution> commissionDistributions;

  @Before
  public void setup() {
    //TODO: should be replaced with real HTTP call to commission API
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
    commissionDistributions = commissionAPI.distribute(currentAccount, commission);
  }

  @Then("^transfer ([0-9]*\\.?[0-9]+) to the account$")
  public void transferAccount_commissionToTheAccount(Double accountCommission) {
    commissionDistributions.stream()
        .filter(step -> step.account().equals(currentAccount))
        .findFirst()
        .ifPresent(step -> assertThat(step.commission())
            .isEqualTo(accountCommission));
  }

  @And("^([0-9]*\\.?[0-9]+) for the (\\d+)$")
  public void fixed_commissionForTheSpecified_account(Double fixedCommission, Long specifiedAccount) {
    commissionDistributions.stream()
        .filter(step -> step.account().equals(specifiedAccount))
        .findFirst()
        .ifPresent(step -> assertThat(step.commission())
            .isEqualTo(fixedCommission));
  }
}
