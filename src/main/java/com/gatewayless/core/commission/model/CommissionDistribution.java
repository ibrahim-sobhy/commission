package com.gatewayless.core.commission.model;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

@Builder
@Accessors(fluent = true)
@ToString
@Getter
public class CommissionDistribution {
  private Long account;
  private Double commission;
  private LocalDateTime at;
}
