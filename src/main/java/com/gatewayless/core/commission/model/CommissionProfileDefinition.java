package com.gatewayless.core.commission.model;

import lombok.Builder;
import lombok.Singular;

import java.util.List;

@Builder
public class CommissionProfileDefinition {
  @Singular
  List<CommissionStep> steps;
}
