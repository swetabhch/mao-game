package edu.brown.cs.student.gameplay;

import edu.brown.cs.student.cards.Card;
import edu.brown.cs.student.maoExceptions.CardNotFoundException;

import java.util.Collection;

/**
 * An interface to describe real and computer-generated players for the game of Mao.
 */
public interface Player {

  /**
   * Gets the ID of the given Player implementation.
   * @return An int representing the ID of the given Player implementation.
   */
  int getId();

  /**
   * Gets the username of the given Player.
   * @return A String representing the username of this Player.
   */
  String getUsername();

  /**
   * Obtains the hand of cards that the Player possesses.
   * @return A Collection of Cards representing the cards the Player has.
   */
  Collection<Card> getCards();

  /**
   * Adds a given card to the Player's hand of cards.
   * @param card The Card to be added.
   */
  void addCard(Card card);

  /**
   * Removes a given card from the Player's hand of cards, if such a card exists
   * in that hand.
   * @param card The Card to remove from the Player's hand of cards.
   * @throws CardNotFoundException If the specified card is not present in the Player's hand.
   */
  void removeCard(Card card) throws CardNotFoundException;

}
