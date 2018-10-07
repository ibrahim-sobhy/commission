package com.gatewayless.core.commission.model;

import lombok.*;
import lombok.experimental.Accessors;

@RequiredArgsConstructor
@Accessors(fluent = true)
@Getter
public class CommissionStep {
  private final @NonNull
  StepType type;
  private final @NonNull
  Double amount;
  private final Long account;
  @Setter
  @Builder.Default
  private boolean isRootAccont = false;

  public enum StepType {
    FIXED, PERCENTAGE
  }
}
