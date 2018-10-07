package com.gatewayless.core.commission.service;

import com.gatewayless.core.commission.model.CommissionDistribution;
import com.gatewayless.core.commission.model.CommissionProfileDefinition;
import com.gatewayless.core.commission.model.CommissionStep;

import java.time.LocalDateTime;
import java.util.List;

import static com.gatewayless.core.commission.model.CommissionStep.StepType.FIXED;
import static com.gatewayless.core.commission.model.CommissionStep.StepType.PERCENTAGE;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang.math.NumberUtils.DOUBLE_ZERO;


public class CommissionDistributorDefault implements CommissionDistributor {

  @Override
  public List<CommissionDistribution> distribute(CommissionProfileDefinition profileDefinition,
                                                 Double commission) {
    List<CommissionDistribution> commissionDistributions =
        getCommissionDistributions(profileDefinition, commission);
    Double totalDistributionAmount = totalDistributionAmount(commissionDistributions);

    // Add remaining commission to source account
    profileDefinition.steps().stream()
        .filter(CommissionStep::isRootAccont)
        .findFirst()
        .ifPresent(step ->
            commissionDistributions.add(buildSourceAccountStep(step.account(),
                commission, totalDistributionAmount)));

    return commissionDistributions;
  }

  @Override
  public List<CommissionDistribution> distributeFullyToSourceAccount(
      CommissionProfileDefinition profileDefinition, Double commission) {
    return profileDefinition.steps().stream()
        .map(commissionStep -> {
              if (commissionStep.isRootAccont()) {
                return CommissionDistribution.builder()
                    .account(commissionStep.account())
                    .commission(commission)
                    .at(LocalDateTime.now())
                    .build();
              } else {
                return CommissionDistribution.builder()
                    .account(commissionStep.account())
                    .commission(0.0)
                    .at(LocalDateTime.now())
                    .build();
              }
            }
        ).collect(toList());
  }

  private List<CommissionDistribution> getCommissionDistributions(
      CommissionProfileDefinition profileDefinition, Double commission) {
    return profileDefinition.steps().stream()
        .filter(step -> !step.isRootAccont())
        .map(step -> {
          if (step.type() == FIXED) {
            return CommissionDistribution.builder()
                .account(step.account())
                .commission(step.amount())
                .at(LocalDateTime.now())
                .build();
          } else if (step.type() == PERCENTAGE) {
            return CommissionDistribution.builder()
                .account(step.account())
                .commission(commission * step.amount() / 100.0)
                .at(LocalDateTime.now())
                .build();
          } else {
            return CommissionDistribution.builder().build();
          }
        }).collect(toList());
  }


  private CommissionDistribution buildSourceAccountStep(Long account, Double commission,
                                                        Double totalDistributionAmount) {
    return CommissionDistribution.builder()
        .account(account)
        .commission(commission - totalDistributionAmount)
        .at(LocalDateTime.now())
        .build();
  }

  private Double totalDistributionAmount(List<CommissionDistribution> commissionDistributions) {
    return commissionDistributions.stream()
        .map(CommissionDistribution::commission)
        .reduce(DOUBLE_ZERO, Double::sum);
  }
}
