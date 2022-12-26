package edu.brown.cs.student.rules;

import java.util.Collection;

/**
 * The RuleGenerator interface considers a particular Rule implementation and ensures
 * functionality for randomly generating a number of rules.
 * @param <R> A generic type implementation of the Rule interface particular to a game.
 */
public interface RuleGenerator<R extends Rule> {

  /**
   * Randomly selects a given number of rules from the connected database of Rules.
   * @param numRules The number of rules to be generated.
   * @return A Collection of `numRules` randomly selected rules.
   * @throws Exception if file not found or malformed.
   */
  Collection<R> generateRules(int numRules) throws Exception;

}
