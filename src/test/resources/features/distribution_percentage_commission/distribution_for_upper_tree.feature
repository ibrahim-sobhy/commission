# new feature
# Tags: optional
    
Feature: Distribute commission for upper tree in the account structure
In order to distribute the commission to target accounts
As a Revenu Manager
I want to distrubute the commission between the account and his parents
    
Scenario Outline: distribute commission to account heirarchy based on percentage defined in the profile
    Given the account has 3 levels in his hierarchy
    And commission profile define <level_1_commission>
    And commission profile define <level_2_commission>
    And commission profile define <level_3_commission>
    When ask to distribute the <commission>
    Then transfer <account_commission> to the account
    And transfer <level_1_commission_amount> to <level_1> account
    And transfer <level_2_commission_amount> to <level_2> account
    And transfer <level_3_commission>_amount to <level_3> account

Examples: Different profiles with different commissions
    | profile | commission | account_commission | level_1_commission | level_1_commission_amount | level_2_commission | level_2_commission_amount | level_3_commission | level_3_commission_amount |
    | 1       | 200        | 150                | 10%                | 20                        | 5%                 | 10                        | 10%                | 20                        |
    | 1       | 400        | 200                | 20%                | 80                        | 10%                | 40                        | 20%                | 80                        |