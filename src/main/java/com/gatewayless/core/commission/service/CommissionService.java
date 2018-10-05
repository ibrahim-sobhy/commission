package com.gatewayless.core.commission.service;

import com.gatewayless.core.commission.model.CommissionDistribution;
import com.gatewayless.core.commission.model.CommissionProfileDefinition;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CommissionService {

  public List<CommissionDistribution> distribute(CommissionProfileDefinition build, Double commission) {
    throw new UnsupportedOperationException("Not Implemented yet.");
  }

  public List<CommissionDistribution> lookupHistory(Long sourceAccount) {
    throw new UnsupportedOperationException("Not Implemented yet.");
  }

  public List<CommissionDistribution> lookupHistory(Long sourceAccount, LocalDateTime from, LocalDateTime to) {
    throw new UnsupportedOperationException("Not Implemented yet.");
  }
}
