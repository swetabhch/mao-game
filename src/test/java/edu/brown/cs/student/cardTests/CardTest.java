package edu.brown.cs.student.cardTests;

import edu.brown.cs.student.cards.Card;
import edu.brown.cs.student.cards.Rank;
import edu.brown.cs.student.cards.Suit;

import org.junit.Test;

import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

public class CardTest {

  @Test
  public void testNotNull() {
    Card testCard = new Card(Rank.NINE, Suit.DIAMOND);
    assertNotNull(testCard);
  }

  @Test
  public void testGetRank() {
    Card testCard1 = new Card(Rank.NINE, Suit.DIAMOND);
    Card testCard2 = new Card(Rank.ACE, Suit.HEART);
    Card testCard3 = new Card(Rank.QUEEN, Suit.CLUB);
    assertEquals(Rank.NINE, testCard1.getRank());
    assertEquals(Rank.ACE, testCard2.getRank());
    assertEquals(Rank.QUEEN, testCard3.getRank());
  }

  @Test
  public void testGetSuit() {
    Card testCard1 = new Card(Rank.NINE, Suit.DIAMOND);
    Card testCard2 = new Card(Rank.ACE, Suit.HEART);
    Card testCard3 = new Card(Rank.QUEEN, Suit.CLUB);
    assertEquals(Suit.DIAMOND, testCard1.getSuit());
    assertEquals(Suit.HEART, testCard2.getSuit());
    assertEquals(Suit.CLUB, testCard3.getSuit());
  }

  @Test
  public void testEquals() {
    Card equalCard1 = new Card(Rank.NINE, Suit.DIAMOND);
    Card equalCard2 = new Card(Rank.NINE, Suit.DIAMOND);
    Card testCard = new Card(Rank.ACE, Suit.HEART);
    assertEquals(testCard, testCard);
    assertEquals(equalCard1, equalCard2);
    assertNotEquals(testCard, equalCard2);
  }

  @Test
  public void testToString() {
    Card testCard1 = new Card(Rank.NINE, Suit.DIAMOND);
    Card testCard2 = new Card(Rank.ACE, Suit.HEART);
    Card testCard3 = new Card(Rank.QUEEN, Suit.CLUB);
    assertEquals("Rank: NINE, Suit: DIAMOND", testCard1.toString());
    assertEquals("Rank: ACE, Suit: HEART", testCard2.toString());
    assertEquals("Rank: QUEEN, Suit: CLUB", testCard3.toString());
  }

  @Test
  public void testEqualsImpliesSameHashCode() {
    Card equalCard1 = new Card(Rank.NINE, Suit.DIAMOND);
    Card equalCard2 = new Card(Rank.NINE, Suit.DIAMOND);
    assertEquals(equalCard1.hashCode(), equalCard2.hashCode());
  }

  @Test
  public void testGenerateDeck() {
    List<Card> deck = Card.generateDeck();
    List<Card> nines = deck.stream().filter(card -> card.getRank() == Rank.NINE)
        .collect(Collectors.toList());
    List<Card> clubs = deck.stream().filter(card -> card.getSuit() == Suit.CLUB)
        .collect(Collectors.toList());
    assertEquals(52, deck.size());
    assertEquals(4, nines.size());
    assertEquals(13, clubs.size());
  }

}
