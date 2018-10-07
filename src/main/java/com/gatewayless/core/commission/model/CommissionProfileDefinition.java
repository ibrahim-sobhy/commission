package com.gatewayless.core.commission.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Singular;
import lombok.experimental.Accessors;

import java.util.List;

@Builder
@Accessors(fluent = true)
public class CommissionProfileDefinition {
  @Singular
  @Getter
  private List<CommissionStep> steps;
}
