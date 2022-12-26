package edu.brown.cs.student.rules;

import edu.brown.cs.student.cards.Card;

/**
 * An Action represents something a player does during their turn in a turn-based card
 * game like Mao, and is characterized by a Card played and an auxiliary effect.
 */
public interface Action {

  /**
   * Returns the Card with which the action is associated.
   * @return The Card with which the action is associated.
   */
  Card getCard();

  /**
   * Obtains the message associated with an action.
   * @return A String representing the message associated with an action.
   */
  String getMessage();

}
