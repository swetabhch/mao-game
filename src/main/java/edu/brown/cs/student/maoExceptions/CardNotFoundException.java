package edu.brown.cs.student.maoExceptions;

/**
 * Exception thrown when a Card is not found in a given hand or deck of cards.
 */
public class CardNotFoundException extends Exception {

  /**
   * Constructor to pass error message as string.
   * @param s Error message to pass.
   */
  public CardNotFoundException(String s) {
    super(s);
  }

}
