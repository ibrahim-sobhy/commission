# new feature
# Tags: optional

Feature: Distribution of commission among different accounts with fixed and percentage amounts
  In order to have a complex distribtuon of commission
  As a Revenu Manager
  I want to distribute the commission based percentage or even fixed amount for different accounts

  Scenario: Distribute a commission between accounts where the total distribution is within the commssion amount
    Given the account 123456 has 2 parents with percentage amounts
    And the first parent will have 10%
    And second parent will have 5%
    And there is public beneficiary 44445555 to get fixed amount 50
    When ask to distribute the 200 between these accounts
    Then first parent will have 20
    And second parent will have 10
    And the public beneficiary will have 50
    And the account 123456 will have the remaining 120


  Scenario: Distribute a commission between accounts where the total distribution is greater than the commssion amount
    Given the account 123456 has 2 parents with percentage amounts
    And the first parent will have 20%
    And second parent will have 20%
    And there is public beneficiary 44445555 to get fixed amount 50
    And the minimum amount for account is 50%
    When ask to distribute the 250 between these accounts
    Then deduct the fixed amount first then distribute the remaining to other
    And first parent will have 40
    And second parent will have 40
    And the public beneficiary will have 50
    And the account 123456 will have the remaining 120

  Scenario: Distribute a commission between accounts where the total fixed distribution is greater than the commssion amount
    Given the account 123456 has 2 parents with percentage amounts
    And the first parent will have 20%
    And second parent will have 20%
    And there is public beneficiary 44445555 to get fixed amount 50
    And the minimum amount for account is 50%
    When ask to distribute 90 between these accounts
    Then first parent will have 18
    And second parent will have 18
    And the public beneficiary will have 0
    And the account 123456 will have the remaining 54