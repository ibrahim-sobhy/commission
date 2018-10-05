package com.gatewayless.core.commission.model;

import lombok.Builder;
import lombok.ToString;

import java.time.LocalDateTime;

@Builder
@ToString
public class CommissionDistribution {
  private Long account;
  private Double commission;
  private LocalDateTime at;
}
