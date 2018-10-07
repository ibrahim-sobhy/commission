package com.gatewayless.core.commission.service;

import com.gatewayless.core.commission.model.CommissionDistribution;
import com.gatewayless.core.commission.model.CommissionProfileDefinition;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface CommissionDistributor {
  List<CommissionDistribution> distribute(CommissionProfileDefinition profileDefinition,
                                          Double commission);

  List<CommissionDistribution> distributeFullyToSourceAccount(
      CommissionProfileDefinition profileDefinition, Double commission);
}
