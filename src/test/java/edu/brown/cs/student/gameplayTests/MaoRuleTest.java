package edu.brown.cs.student.gameplayTests;

import edu.brown.cs.student.cards.Card;
import edu.brown.cs.student.cards.Rank;
import edu.brown.cs.student.cards.Suit;
import edu.brown.cs.student.gameplay.MaoPlayAction;
import edu.brown.cs.student.gameplay.MaoRule;
import edu.brown.cs.student.gameplay.MaoRuleType;
import edu.brown.cs.student.rules.Action;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import static org.junit.Assert.*;

public class MaoRuleTest {

  @Test
  public void testNotNull() {
    MaoRule maoRule = new MaoRule(MaoRuleType.VERBAL_PENALTY,
        new Card(Rank.NINE, Suit.DIAMOND), "ch3ck");
    assertNotNull(maoRule);
  }

  @Test
  public void testGetType() {
    MaoRule verbalMaoRule = new MaoRule(MaoRuleType.VERBAL_PENALTY,
        new Card(Rank.NINE, Suit.DIAMOND), "ch3ck");
    MaoRule reverseGameplayRule = new MaoRule(MaoRuleType.REVERSE_GAMEPLAY,
        new Card(Rank.ACE, Suit.HEART), "");
    assertEquals(MaoRuleType.VERBAL_PENALTY, verbalMaoRule.getType());
    assertEquals(MaoRuleType.REVERSE_GAMEPLAY, reverseGameplayRule.getType());
  }

  @Test
  public void testGetRanks() {
    MaoRule singleRankMaoRule = new MaoRule(MaoRuleType.VERBAL_PENALTY,
        new Card(Rank.NINE, Suit.DIAMOND), "ch3ck");
    List<Rank> ranks = new ArrayList<>();
    ranks.add(Rank.NINE);
    ranks.add(Rank.QUEEN);
    MaoRule multipleRanksMaoRule = new MaoRule(MaoRuleType.REVERSE_GAMEPLAY,
        ranks, new ArrayList<>(List.of(Suit.DIAMOND, Suit.SPADE, Suit.HEART)), "pOfOrder");
    List<Rank> singleRank = singleRankMaoRule.getRanks();
    List<Rank> multipleRanks = multipleRanksMaoRule.getRanks();
    assertEquals(new ArrayList<>(List.of(Rank.NINE)), singleRank);
    assertEquals(ranks, multipleRanks);
  }

  @Test
  public void testGetSuits() {
    MaoRule singleSuitMaoRule = new MaoRule(MaoRuleType.VERBAL_PENALTY,
        new Card(Rank.NINE, Suit.DIAMOND), "ch3ck");
    List<Suit> suits = new ArrayList<>();
    suits.add(Suit.DIAMOND);
    suits.add(Suit.SPADE);
    suits.add(Suit.HEART);
    MaoRule multipleSuitsMaoRule = new MaoRule(MaoRuleType.REVERSE_GAMEPLAY,
        new ArrayList<>(List.of(Rank.NINE, Rank.QUEEN)), suits, "pOfOrder");
    List<Suit> singleSuit = singleSuitMaoRule.getSuits();
    List<Suit> multipleSuits = multipleSuitsMaoRule.getSuits();
    assertEquals(new ArrayList<>(List.of(Suit.DIAMOND)), singleSuit);
    assertEquals(suits, multipleSuits);
  }

  @Test
  public void testGetVerbal() {
    MaoRule maoRule1 = new MaoRule(MaoRuleType.VERBAL_PENALTY,
        new Card(Rank.NINE, Suit.DIAMOND), "ch3ck");
    MaoRule maoRule2 = new MaoRule(MaoRuleType.REVERSE_GAMEPLAY,
        new Card(Rank.NINE, Suit.DIAMOND), "");
    assertEquals("ch3ck", maoRule1.getVerbal());
    assertEquals("", maoRule2.getVerbal());
  }

  @Test
  public void testRuleViolated() {
    MaoRule singleCardMaoRule = new MaoRule(MaoRuleType.VERBAL_PENALTY,
        new Card(Rank.NINE, Suit.DIAMOND), "ch3 ck");
    MaoRule multipleCardMaoRule = new MaoRule(MaoRuleType.VERBAL_PENALTY,
        new ArrayList<>(List.of(Rank.NINE, Rank.QUEEN)),
        new ArrayList<>(List.of(Suit.DIAMOND, Suit.SPADE, Suit.HEART)), "He!!o");
    MaoPlayAction singleCardFaultyAction = new MaoPlayAction(
        new Card(Rank.NINE, Suit.DIAMOND), "che ck");
    MaoPlayAction singleCardCorrectAction = new MaoPlayAction(
        new Card(Rank.NINE, Suit.DIAMOND), "ch3 ck");
    MaoPlayAction multiCardFaultyAction = new MaoPlayAction(
        new Card(Rank.QUEEN, Suit.HEART), "");
    MaoPlayAction multiCardCorrectAction = new MaoPlayAction(
        new Card(Rank.QUEEN, Suit.HEART), "He!!o");

    assertTrue(singleCardMaoRule.ruleViolated(singleCardFaultyAction));
    assertFalse(singleCardMaoRule.ruleViolated(singleCardCorrectAction));
    assertFalse(singleCardMaoRule.ruleViolated(multiCardFaultyAction));
    //assertTrue(multipleCardMaoRule.ruleViolated(multiCardFaultyAction));
    System.out.println(multipleCardMaoRule.ruleViolated(multiCardFaultyAction));
    assertFalse(multipleCardMaoRule.ruleViolated(multiCardCorrectAction));
  }

  @Test
  public void testGetSituation() {
    MaoRule singleCardMaoRule = new MaoRule(MaoRuleType.VERBAL_PENALTY,
        new Card(Rank.NINE, Suit.DIAMOND), "ch3 ck");
    MaoRule multipleCardMaoRule = new MaoRule(MaoRuleType.VERBAL_PENALTY,
        new ArrayList<>(List.of(Rank.NINE, Rank.QUEEN)),
        new ArrayList<>(List.of(Suit.DIAMOND, Suit.SPADE, Suit.HEART)), "He!!o");
    MaoPlayAction singleCardFaultyAction = new MaoPlayAction(
        new Card(Rank.NINE, Suit.DIAMOND), "che ck");
    MaoPlayAction singleCardCorrectAction = new MaoPlayAction(
        new Card(Rank.NINE, Suit.DIAMOND), "ch3 ck");
    MaoPlayAction multiCardFaultyAction = new MaoPlayAction(
        new Card(Rank.QUEEN, Suit.HEART), "");
    MaoPlayAction multiCardCorrectAction = new MaoPlayAction(
        new Card(Rank.QUEEN, Suit.HEART), "He!!o");

    Predicate<Action> singleCardSituation = singleCardMaoRule.getSituation();
    Predicate<Action> multiCardSituation = multipleCardMaoRule.getSituation();

    assertTrue(singleCardSituation.test(singleCardFaultyAction));
    assertFalse(singleCardSituation.test(singleCardCorrectAction));
    assertFalse(singleCardSituation.test(multiCardFaultyAction));
    assertTrue(multiCardSituation.test(multiCardFaultyAction));
    assertFalse(multiCardSituation.test(multiCardCorrectAction));
  }

  @Test
  public void testEqualsImpliesSameHashCode() {
    MaoRule multipleCardMaoRule = new MaoRule(MaoRuleType.VERBAL_PENALTY,
        new ArrayList<>(List.of(Rank.NINE, Rank.QUEEN)),
        new ArrayList<>(List.of(Suit.DIAMOND, Suit.SPADE, Suit.HEART)), "He!!o");
    MaoRule multipleCardMaoRuleCopy = new MaoRule(MaoRuleType.VERBAL_PENALTY,
        new ArrayList<>(List.of(Rank.NINE, Rank.QUEEN)),
        new ArrayList<>(List.of(Suit.DIAMOND, Suit.SPADE, Suit.HEART)), "He!!o");
    assertEquals(multipleCardMaoRuleCopy.hashCode(), multipleCardMaoRule.hashCode());
  }

}
