# new feature
# Tags: optional
    
Feature: Distribute commission for upper tree in the account structure
In order to distribute the commission to target accounts
As a Revenu Manager
I want to distrubute the commission between the account and his parent
    
Scenario Outline: distribute commission to simple entity which have single parent
    Given the account has 1 parent in his hierarchy
    And commission profile define <parent_percentage> to parent
    When ask to distribute the <commission>
    Then transfer <account_commission> to the account and <parent_commission> for the parent account

Examples: Different profiles with different commissions
    | profile   | parent_percentage | commission    | account_commission    | parent_commission     |
    | 1         | 50%               | 700           | 350                   | 350                   |
    | 2         | 75%               | 800           | 600                   | 200                   |
    | 3         | 25%               | 400           | 300                   | 100                   |
