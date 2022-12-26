package edu.brown.cs.student.gameplayTests;

import edu.brown.cs.student.cards.Card;
import edu.brown.cs.student.cards.Rank;
import edu.brown.cs.student.cards.Suit;
import edu.brown.cs.student.gameplay.Board;
import edu.brown.cs.student.gameplay.MaoPlayer;
import edu.brown.cs.student.gameplay.MaoRule;
import edu.brown.cs.student.gameplay.Settings;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.junit.Assert.*;

/**
 * testing for functionality that doesn't involve sockets -- initializing with null sockets
 */
public class BoardTest {

  private List<MaoPlayer> players;
  private Board board;

  @Before
  public void setUp() {
    List<Card> cards1 = new ArrayList<>(List.of(new Card(Rank.QUEEN, Suit.DIAMOND)));
    List<Card> cards2 = new ArrayList<>();
    cards2.add(new Card(Rank.ACE, Suit.DIAMOND));
    cards2.add(new Card(Rank.FOUR, Suit.HEART));
    List<Card> cards3 = new ArrayList<>();
    cards3.add(new Card(Rank.SEVEN, Suit.HEART));
    cards3.add(new Card(Rank.FOUR, Suit.HEART));
    cards3.add(new Card(Rank.NINE, Suit.CLUB));

    MaoPlayer player1 = new MaoPlayer("first", true, null, cards1);
    MaoPlayer player2 = new MaoPlayer("2nd", false, null, cards2);
    MaoPlayer player3 = new MaoPlayer("_3_", false, null, cards3);
    List<MaoPlayer> players = new ArrayList<>(List.of(player1, player2, player3));
    this.players = players;
    String lobbyCode = "234234";
    this.board = new Board(players, new Settings(), lobbyCode);
  }

  @After
  public void tearDown() {
    this.players = null;
    this.board = null;
  }

  @Test
  public void testGetters() {
    setUp();
    assertEquals(0, this.board.getTurnIndex());
    assertEquals(this.players.get(0), this.board.getCurrentPlayer());
    assertNull(this.board.getLastPlayed());
    assertEquals(3, this.board.numPlayers());
    Collection<MaoRule> rules = this.board.getRules();
    // change the number 3 according to the private NUM_AUTO_RULES variable
    assertEquals(3, this.board.getRuleStorage().getStoredRules().size());
    assertEquals(3, this.board.getRules().size());
    tearDown();
  }

  @Test
  public void testAdvanceTurn() {
    setUp();
    int initTurnIndex = this.board.getTurnIndex();
    this.board.advanceTurn();
    assertEquals(initTurnIndex + 1, this.board.getTurnIndex());
    tearDown();
  }

  @Test
  public void testPointOfOrder() {
    setUp();
    assertFalse(this.board.isPointOfOrderOn());
    tearDown();
  }

  @Test
  public void testGameplayReversed() {
    setUp();
    assertFalse(this.board.isGameplayReversed());
    this.board.reversePlayerOrder();
    assertTrue(this.board.isGameplayReversed());
    tearDown();
  }

}
