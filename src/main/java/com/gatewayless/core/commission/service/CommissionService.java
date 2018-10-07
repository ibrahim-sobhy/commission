package com.gatewayless.core.commission.service;

import com.gatewayless.core.commission.model.CommissionDistribution;
import com.gatewayless.core.commission.model.CommissionProfileDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

import static com.gatewayless.core.commission.model.CommissionStep.StepType.FIXED;

@Service
public class CommissionService {

  @Autowired
  private CommissionDistributor commissionDistributor;

  /**
   * Distribute the commission based on the commission profile.
   *
   * @param profileDefinition the profile definition.
   * @param commission        to be distributed.
   * @return the commission distributions.
   */
  public List<CommissionDistribution> distribute(CommissionProfileDefinition profileDefinition,
                                                 Double commission) {
    if (isCommissionSmallerThanDistribution(profileDefinition, commission)) {
      return commissionDistributor.distributeFullyToSourceAccount(profileDefinition, commission);
    }
    return commissionDistributor.distribute(profileDefinition, commission);
  }


  private boolean isCommissionSmallerThanDistribution(CommissionProfileDefinition profileDefinition,
                                                      Double commission) {
    return profileDefinition.steps().stream()
        .filter(step -> !step.isRootAccont())
        .filter(step -> step.type() == FIXED)
        .anyMatch(step -> step.amount() >= commission);
  }

  public List<CommissionDistribution> lookupHistory(Long sourceAccount) {
    throw new UnsupportedOperationException("Not Implemented yet.");
  }

  public List<CommissionDistribution> lookupHistory(Long sourceAccount, LocalDateTime from,
                                                    LocalDateTime to) {
    throw new UnsupportedOperationException("Not Implemented yet.");
  }
}
