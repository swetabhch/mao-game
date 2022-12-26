package edu.brown.cs.student.gameplayTests;

import edu.brown.cs.student.cards.Card;
import edu.brown.cs.student.cards.Rank;
import edu.brown.cs.student.cards.Suit;
import edu.brown.cs.student.gameplay.MaoPlayAction;
import org.junit.Test;

import static org.junit.Assert.*;

public class MaoPlayActionTest {

  @Test
  public void testNotNull() {
    MaoPlayAction mpa = new MaoPlayAction(new Card(Rank.JACK, Suit.SPADE), "giga");
    assertNotNull(mpa);
  }

  @Test
  public void testEquals() {
    MaoPlayAction mpa1 = new MaoPlayAction(new Card(Rank.JACK, Suit.SPADE), "giga");
    MaoPlayAction mpa2 = new MaoPlayAction(new Card(Rank.JACK, Suit.SPADE), "giga");
    MaoPlayAction mpa3 = new MaoPlayAction(new Card(Rank.FOUR, Suit.CLUB), "me ga");
    assertEquals(mpa1, mpa2);
    assertEquals(mpa3, mpa3);
    assertNotEquals(mpa1, mpa3);
  }

  @Test
  public void testGetCard() {
    MaoPlayAction mpa1 = new MaoPlayAction(new Card(Rank.JACK, Suit.SPADE), "giga");
    MaoPlayAction mpa2 = new MaoPlayAction(new Card(Rank.FOUR, Suit.CLUB), "me ga");
    Card card1 = new Card(Rank.JACK, Suit.SPADE);
    Card card2 = new Card(Rank.FOUR, Suit.CLUB);
    assertEquals(card1, mpa1.getCard());
    assertEquals(card2, mpa2.getCard());
  }

  @Test
  public void testGetMessage() {
    MaoPlayAction mpa1 = new MaoPlayAction(new Card(Rank.JACK, Suit.SPADE), "g1gA");
    MaoPlayAction mpa2 = new MaoPlayAction(new Card(Rank.FOUR, Suit.CLUB), "");
    assertEquals("g1gA", mpa1.getMessage());
    assertEquals("", mpa2.getMessage());
  }

  @Test
  public void testEqualImpliesSameHashCode() {
    MaoPlayAction mpa1 = new MaoPlayAction(new Card(Rank.JACK, Suit.SPADE), "g1gA");
    MaoPlayAction mpa2 = new MaoPlayAction(new Card(Rank.JACK, Suit.SPADE), "g1gA");
    assertEquals(mpa1.hashCode(), mpa2.hashCode());
  }

}
