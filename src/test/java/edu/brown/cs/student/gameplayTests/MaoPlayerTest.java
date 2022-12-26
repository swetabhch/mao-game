package edu.brown.cs.student.gameplayTests;

import edu.brown.cs.student.cards.Card;
import edu.brown.cs.student.cards.Rank;
import edu.brown.cs.student.cards.Suit;
import edu.brown.cs.student.gameplay.MaoPlayer;
import edu.brown.cs.student.maoExceptions.CardNotFoundException;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * testing for functionality that doesn't involve sockets -- initializing with null sockets
 */
public class MaoPlayerTest {

  @Test
  public void testGetters() {
    List<Card> cards = new ArrayList<>(List.of(new Card(Rank.EIGHT, Suit.HEART)));
    cards.add(new Card(Rank.KING, Suit.SPADE));
    MaoPlayer noCardsPlayer = new MaoPlayer("bl3 p", true, null);
    MaoPlayer someCardsPlayer = new MaoPlayer("A", false, null, cards);
    assertEquals("bl3 p", noCardsPlayer.getUsername());
    assertNull(someCardsPlayer.getSocket());
    assertFalse(someCardsPlayer.isHost());
    assertEquals(new ArrayList<>(), noCardsPlayer.getCards());
    assertEquals(cards, someCardsPlayer.getCards());
    assertTrue(noCardsPlayer.getId() < someCardsPlayer.getId());
  }

  @Test
  public void testAddCard() {
    MaoPlayer player = new MaoPlayer("bl3 p", true, null);
    player.addCard(new Card(Rank.KING, Suit.SPADE));
    player.addCard(new Card(Rank.EIGHT, Suit.HEART));
    List<Card> cards = new ArrayList<>();
    cards.add(new Card(Rank.KING, Suit.SPADE));
    cards.add(new Card(Rank.EIGHT, Suit.HEART));
    assertEquals(cards, player.getCards());
  }

  @Test
  public void testRemoveCard() throws CardNotFoundException {
    List<Card> cards = new ArrayList<>();
    cards.add(new Card(Rank.KING, Suit.SPADE));
    cards.add(new Card(Rank.EIGHT, Suit.HEART));
    MaoPlayer player = new MaoPlayer("bl3 p", true, null, cards);
    player.removeCard(new Card(Rank.KING, Suit.SPADE));
    assertEquals(new ArrayList<>(List.of(new Card(Rank.EIGHT, Suit.HEART))), player.getCards());
    String out = "";
    try {
      player.removeCard(new Card(Rank.KING, Suit.SPADE));
    } catch (CardNotFoundException e) {
      out = e.getMessage();
    }
    assertEquals("ERROR: Player does not have the given card.", out);
  }

  @Test
  public void testGetAndIncrementId() {
    MaoPlayer player1 = new MaoPlayer("bl3 p", true, null);
    int initId = player1.getId();
    MaoPlayer.getAndIncrementId();
    MaoPlayer player2 = new MaoPlayer("bl4 p", true, null);
    int nextId = player2.getId();
    assertEquals(nextId, initId + 2);
  }

}
