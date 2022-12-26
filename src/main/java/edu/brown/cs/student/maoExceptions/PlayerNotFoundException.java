package edu.brown.cs.student.maoExceptions;

/**
 * Exception thrown when a Player is not found in a given collection of players.
 */
public class PlayerNotFoundException extends Exception {

  /**
   * Constructor to pass error message as string.
   * @param s Error message to pass.
   */
  public PlayerNotFoundException(String s) {
    super(s);
  }

}
