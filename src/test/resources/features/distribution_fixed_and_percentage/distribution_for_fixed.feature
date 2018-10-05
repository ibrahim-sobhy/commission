# new feature
# Tags: optional

Feature: Distribute commission with fixed amount for specific account
  In order to distribute the commission to target accounts
  As a Revenue Manager
  I want to distribute the commission between the account and specific account

  Scenario Outline: A fixed amount of commission to be transferred like franchiser
    Given 11111 has <specified_account> is defined in the account commission profile
    When ask to distribute the <commission>
    Then transfer <account_commission> to the account
    And <fixed_commission> for the <specified_account>

    Examples: Different profiles with different commissions
      | profile | commission | fixed_commission | account_commission | specified_account |
      | 1       | 500        | 50               | 450                | 123456            |
      | 2       | 300        | 60               | 240                | 123456            |
      | 3       | 50         | 0                | 50                 | 123456            |