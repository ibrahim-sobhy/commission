package com.gatewayless.core.commission.api;

import com.gatewayless.core.commission.model.CommissionDistribution;
import com.gatewayless.core.commission.service.CommissionProfile;
import com.gatewayless.core.commission.service.CommissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.springframework.util.ObjectUtils.isEmpty;

@RestController
public class CommissionApi {

  @Autowired
  private CommissionService commissionService;

  @Autowired
  private CommissionProfile commissionProfile;

  /**
   * Distribute the commission among the accounts defined int the commission profile.
   *
   * @param sourceAccount where to find the commission profile.
   * @param commission    the required commission.
   * @return the outcome of distributions between the accounts based on the commission profile.
   */
  public List<CommissionDistribution> distribute(@NotNull Long sourceAccount,
                                                 @NotNull Double commission) {
    if (isEmpty(sourceAccount) || !isPositive(sourceAccount)) {
      throw new IllegalArgumentException("account should not be empty");
    }
    if (isEmpty(commission) || !isPositive(commission)) {
      throw new IllegalArgumentException("commission should not be empty");
    }

    return commissionService.distribute(
        commissionProfile.findByAccount(sourceAccount),
        commission
    );
  }

  public Double commission(@Nullable Long account) {
    //TODO: implement
    throw new UnsupportedOperationException("No implemented yet.");
  }

  /**
   * Fetch the commission history of specific account.
   *
   * @param sourceAccount where to find the history.
   * @param from          fetch history from
   * @param to            fetch history to
   * @return the history within the specified dates
   */
  public List<CommissionDistribution> history(Long sourceAccount,
                                              LocalDateTime from,
                                              LocalDateTime to) {
    if (isEmpty(from) || isEmpty(to)) {
      return commissionService.lookupHistory(sourceAccount);
    }
    if (from.isBefore(to)) {
      return Collections.emptyList();
    }
    return commissionService.lookupHistory(sourceAccount, from, to);
  }

  private <T extends Number> boolean isPositive(T number) {
    if (number instanceof Double) {
      return (Double) number > 0.0;
    } else if (number instanceof Long) {
      return (Long) number > 0L;
    } else {
      return false;
    }
  }
}
