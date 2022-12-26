package edu.brown.cs.student.maoExceptions;

/**
 * Exception thrown when a Rule is found to be logically inconsistent with other stored Rules.
 */
public class IncompatibleRuleException extends Exception {

  /**
   * Constructor to pass error message as string.
   * @param s Error message to pass.
   */
  public IncompatibleRuleException(String s) {
    super(s);
  }

}
