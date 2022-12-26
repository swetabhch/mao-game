package edu.brown.cs.student.maoExceptions;

/**
 * Exception thrown when a Settings object has internally incompatible parameters.
 */
public class InconsistentSettingsException extends Exception {

  /**
   * Constructor to pass error message as string.
   * @param s Error message to pass.
   */
  public InconsistentSettingsException(String s) {
    super(s);
  }

}
