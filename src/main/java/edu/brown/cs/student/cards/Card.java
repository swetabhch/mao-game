package edu.brown.cs.student.cards;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * A class representing a traditional playing card, characterized by a numerical
 * rank and a suit enum.
 */
public class Card {

  private final Rank rank;
  private final Suit suit;

  /**
   * A constructor that copies another existing card.
   * @param c The card to be copied.
   */
  public Card(Card c) {
    this.rank = c.getRank();
    this.suit = c.getSuit();
  }

  /**
   * A constructor that directly assigns rank and suit.
   * @param rank The rank of the card.
   * @param suit The suit of the card.
   */
  public Card(Rank rank, Suit suit) {
    this.rank = rank;
    this.suit = suit;
  }

  /**
   * Returns the Rank of the given playing card.
   * @return The Rank of this playing card.
   */
  public Rank getRank() {
    return this.rank;
  }

  /**
   * Returns the Suit of the given playing card.
   * @return The Suit of this playing card.
   */
  public Suit getSuit() {
    return this.suit;
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.rank, this.suit);
  }

  @Override
  public boolean equals(Object obj) {
    if (!(obj instanceof Card)) {
      return false;
    }
    Card otherCard = (Card) obj;
    return (this.rank == otherCard.getRank()) && (this.suit == otherCard.getSuit());
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder("Rank: ");
    sb.append(this.rank);
    sb.append(", Suit: ");
    sb.append(this.suit);
    return sb.toString();
  }

  /**
   * Generates a standard deck of cards.
   * @return A List containing all the cards in a standard deck of cards.
   */
  public static List<Card> generateDeck() {
    List<Card> deck = new ArrayList<>();
    for (Suit suit : Suit.values()) {
      for (Rank rank : Rank.values()) {
        deck.add(new Card(rank, suit));
      }
    }
    return deck;
  }

}
