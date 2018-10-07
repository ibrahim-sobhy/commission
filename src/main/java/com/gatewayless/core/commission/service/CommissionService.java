package com.gatewayless.core.commission.service;

import com.gatewayless.core.commission.model.CommissionDistribution;
import com.gatewayless.core.commission.model.CommissionProfileDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Represent first layer of availiable services for commission.
 *
 * @author Ibrahim Sobhy
 */
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
    if (commissionDistributor.isCommissionSmallerThanDistribution(profileDefinition, commission)) {
      return commissionDistributor.distributeFullyToSourceAccount(profileDefinition, commission);
    }
    return commissionDistributor.distribute(profileDefinition, commission);
  }

  /**
   * Lookup history for account for last day transactions.
   *
   * @param sourceAccount which want to get history.
   * @return the commission history for the last day transactions.
   */
  public List<CommissionDistribution> lookupHistory(Long sourceAccount) {
    throw new UnsupportedOperationException("Not Implemented yet.");
  }

  /**
   * Lookup history for account transactions with dates.
   *
   * @param sourceAccount which want to get history.
   * @param from          since which date.
   * @param to            till which date.
   * @return he commission history transactions within the specified date range.
   */
  public List<CommissionDistribution> lookupHistory(Long sourceAccount, LocalDateTime from,
                                                    LocalDateTime to) {
    throw new UnsupportedOperationException("Not Implemented yet.");
  }
}
