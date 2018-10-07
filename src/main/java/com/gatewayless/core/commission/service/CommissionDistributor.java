package com.gatewayless.core.commission.service;

import com.gatewayless.core.commission.model.CommissionDistribution;
import com.gatewayless.core.commission.model.CommissionProfileDefinition;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.gatewayless.core.commission.model.CommissionStep.StepType.FIXED;
import static com.gatewayless.core.commission.model.CommissionStep.StepType.PERCENTAGE;
import static org.apache.commons.lang.math.NumberUtils.DOUBLE_ZERO;

/**
 * Distribute the commission among all accounts based on the commission profile.
 *
 * @author Ibrahim Sobhy
 */
@Service
public interface CommissionDistributor {
  /**
   * Distribute the commissions based on the commission profile.
   *
   * @param profileDefinition the commission profile definition.
   * @param commission        to be distributed.
   * @return the distributed commission steps
   */
  List<CommissionDistribution> distribute(CommissionProfileDefinition profileDefinition,
                                          Double commission);

  /**
   * Distribute the fully commission amount to the source account.
   *
   * @param profileDefinition the commission profile definition.
   * @param commission        to be distributed.
   * @return the distributed commission steps where all steps has zero value except the source.
   */
  List<CommissionDistribution> distributeFullyToSourceAccount(
      CommissionProfileDefinition profileDefinition, Double commission);

  /**
   * Check if the commission amount is smaller than what is defined in the commission profile.
   *
   * @param profileDefinition the commission profile definition.
   * @param commission        to be distributed.
   * @return true if commission amount is smaller that distributions amounts.
   */
  default boolean isCommissionSmallerThanDistribution(CommissionProfileDefinition profileDefinition,
                                                      Double commission) {
    Double totalFixedCommission = profileDefinition.steps().stream()
        .filter(step -> !step.isRootAccont())
        .map(step -> {
          if (FIXED == step.type()) {
            return step.amount();
          } else if (PERCENTAGE == step.type()) {
            return commission * step.amount() / 100;
          } else {
            return DOUBLE_ZERO;
          }
        })
        .reduce(DOUBLE_ZERO, Double::sum);
    return commission < totalFixedCommission;
  }
}
