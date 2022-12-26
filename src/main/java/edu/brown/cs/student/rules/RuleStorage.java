package edu.brown.cs.student.rules;

import edu.brown.cs.student.maoExceptions.IncompatibleRuleException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.NoSuchElementException;

/**
 * The abstract RuleStorage class serves as a backbone for more specific RuleStorage classes
 * by providing all other necessary functionality and letting users implement their own rule
 * validity and consistency checks. This class stores rules in a Collection, enables adding and
 * removing rules, and checking for consistency between them.
 * @param <R> A class implementing the Rule interface, specific to a particular domain.
 */
public abstract class RuleStorage<R extends Rule> {

  private Collection<R> storedRules;

  public RuleStorage() {
    // empty constructor for making copies
  }

  /**
   * A constructor that directly assigns the rule collection.
   * @param rules The rules to initialize the RuleStorage object with.
   */
  public RuleStorage(Collection<R> rules) {
    this.storedRules = rules;
  }

  /**
   * Determines whether a given rule is consistent with the rules stored in
   * this RuleStorage object.
   *
   * @param rule The rule we check is valid with respect to the other stored rules.
   * @return A boolean indicating whether the input rule is valid with respect to
   * the other rules in the RuleStorage object.
   */
  public abstract boolean isValid(R rule);

  /**
   * Indicates whether rules stored in the RuleStorage object are logically
   * consistent with each other.
   *
   * @return A boolean indicating whether all rules in the stored list of Rules are
   * consistent with each other.
   */
  public boolean storedRulesAreConsistent() {
    for (R rule : this.storedRules) {
      if (!this.isValid(rule)) {
        return false;
      }
    }
    return true;
  }

  /**
   * Returns a collection of the rules stored in the RuleStorage object.
   *
   * @return A defensive copy of the collection of rules stored in this RuleStorage object.
   */
  public Collection<R> getStoredRules() {
    return new ArrayList<>(this.storedRules);
  }

  /**
   * Adds a Rule to the stored rules.
   *
   * @param rule The rule to be added to the stored collection of rules.
   */
  public void add(R rule) throws IncompatibleRuleException {
    if (this.isValid(rule)) {
      this.storedRules.add(rule);
    } else {
      throw new IncompatibleRuleException("ERROR: New rule is logically"
          + "inconsistent with existing rules.");
    }
  }

  /**
   * Removes a particular rule from the stored collection of rules, and returns a boolean
   * indicating whether such a rule was found in this collection.
   *
   * @param rule The rule to be removed from the collection of rules.
   * @return A boolean indicating whether the specified rule was present in storedRules.
   */
  public boolean remove(R rule) {
    return this.storedRules.remove(rule);
  }

  /**
   * Clears the stored collection of rules.
   */
  public void resetRules() {
    this.storedRules.clear();
  }

}
