package edu.brown.cs.student.rules;

import java.util.function.Predicate;

/**
 * A Rule represents a rule in a game like that of Mao, where there is a trigger for the
 * rule based on an action <EXPAND> and a penalty associated with violating the rule.
 */
public interface Rule {

  /**
   * Returns the type of the rule.
   * @return A RuleType indicating the type of the rule.
   */
  RuleType getType();

  /**
   * Returns a Predicate that represents the condition for which the penalty associated
   * with the rule is imposed.
   * @return A Predicate representing the condition for which the penality associated with
   * the rule is imposed.
   */
  Predicate<Action> getSituation();

  /**
   * Represents the trigger at which the Rule should be recognized, i.e. returns true
   * if the penalty dictated by the Rule should be imposed.
   * @param action The Action to check.
   * @return A boolean representing whether the Rule's penalty should be imposed.
   */
  boolean ruleViolated(Action action);
}
