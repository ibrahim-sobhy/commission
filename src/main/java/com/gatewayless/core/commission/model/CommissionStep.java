package com.gatewayless.core.commission.model;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

@RequiredArgsConstructor
@Accessors(fluent = true)
@Getter
public class CommissionStep {
  private final @NonNull
  StepType type;
  private final @NonNull
  Double amount;

  public enum StepType {
    FIXED, PERCENTAGE
  }
}
