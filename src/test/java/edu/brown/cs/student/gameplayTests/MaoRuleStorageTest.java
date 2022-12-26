package edu.brown.cs.student.gameplayTests;

import edu.brown.cs.student.cards.Card;
import edu.brown.cs.student.cards.Rank;
import edu.brown.cs.student.cards.Suit;
import edu.brown.cs.student.gameplay.MaoRule;
import edu.brown.cs.student.gameplay.MaoRuleStorage;
import edu.brown.cs.student.gameplay.MaoRuleType;
import edu.brown.cs.student.maoExceptions.IncompatibleRuleException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;

public class MaoRuleStorageTest {

  private MaoRuleStorage store;

  @Before
  public void setUp() {
    store = new MaoRuleStorage(new ArrayList<>());
  }

  @After
  public void tearDown() {
    store = null;
  }

  @Test
  public void testAddRuleWhenConsistent() throws IncompatibleRuleException {
    setUp();
    Collection<MaoRule> compareWith = new ArrayList<>();
    MaoRule verbalRule1 = new MaoRule(MaoRuleType.VERBAL_PENALTY,
        new Card(Rank.NINE, Suit.DIAMOND), "ch3 ck");
    MaoRule verbalRule2 = new MaoRule(MaoRuleType.VERBAL_PENALTY,
        new ArrayList<>(List.of(Rank.NINE, Rank.QUEEN)),
        new ArrayList<>(List.of(Suit.DIAMOND, Suit.SPADE, Suit.HEART)), "He!!o");
    MaoRule revRule1 = new MaoRule(MaoRuleType.REVERSE_GAMEPLAY,
        new Card(Rank.NINE, Suit.DIAMOND), "");
    store.add(verbalRule1);
    compareWith.add(verbalRule1);
    Collection<MaoRule> oneRule = store.getStoredRules();

    for (MaoRule rule: oneRule) {
      assertTrue(compareWith.contains(rule));
    }

    store.add(verbalRule2);
    compareWith.add(verbalRule2);
    store.add(revRule1);
    compareWith.add(revRule1);
    Collection<MaoRule> multipleRules = store.getStoredRules();
    for (MaoRule rule: multipleRules) {
      assertTrue(compareWith.contains(rule));
    }

    tearDown();
  }

  @Test
  public void testAddRuleWhenInconsistent() {
    setUp();
    MaoRule verbalRule1 = new MaoRule(MaoRuleType.VERBAL_PENALTY,
        new Card(Rank.NINE, Suit.DIAMOND), "ch3 ck");
    MaoRule verbalRule2 = new MaoRule(MaoRuleType.VERBAL_PENALTY,
        new ArrayList<>(List.of(Rank.NINE, Rank.QUEEN)),
        new ArrayList<>(List.of(Suit.DIAMOND, Suit.SPADE, Suit.HEART)), "He!!o");
    MaoRule revRule1 = new MaoRule(MaoRuleType.SILENCE,
        new Card(Rank.NINE, Suit.DIAMOND), "");

    try {
      store.add(verbalRule1);
      store.add(verbalRule2);
    } catch (IncompatibleRuleException e) {
      // this should not occur
      assertTrue(false);
    }
    try {
      store.add(revRule1);
    } catch (IncompatibleRuleException e) {
      String out = e.getMessage();
      assertEquals("ERROR: New rule is logically"
          + "inconsistent with existing rules.", out);
    }
    tearDown();
  }

  @Test
  public void testIsValid() {
    setUp();
    MaoRule verbalRule1 = new MaoRule(MaoRuleType.VERBAL_PENALTY,
        new Card(Rank.NINE, Suit.DIAMOND), "ch3 ck");
    MaoRule verbalRule2 = new MaoRule(MaoRuleType.VERBAL_PENALTY,
        new ArrayList<>(List.of(Rank.NINE, Rank.QUEEN)),
        new ArrayList<>(List.of(Suit.DIAMOND, Suit.SPADE, Suit.HEART)), "He!!o");
    MaoRule silenceRule1 = new MaoRule(MaoRuleType.SILENCE,
        new Card(Rank.NINE, Suit.DIAMOND), "");
    MaoRule silenceRule2 = new MaoRule(MaoRuleType.SILENCE,
        new Card(Rank.EIGHT, Suit.DIAMOND), "");

    try {
      store.add(verbalRule1);
      store.add(verbalRule2);
    } catch (IncompatibleRuleException e) {
      System.out.println("Should not get here! -- these rules are compatible.");
    }
    assertFalse(store.isValid(silenceRule1));
    assertTrue(store.isValid(silenceRule2));
    assertTrue(store.isValid(verbalRule1));
    tearDown();
  }

  @Test
  public void testRemove() throws IncompatibleRuleException {
    setUp();
    MaoRule verbalRule1 = new MaoRule(MaoRuleType.VERBAL_PENALTY,
        new Card(Rank.NINE, Suit.DIAMOND), "ch3 ck");
    MaoRule verbalRule2 = new MaoRule(MaoRuleType.VERBAL_PENALTY,
        new ArrayList<>(List.of(Rank.NINE, Rank.QUEEN)),
        new ArrayList<>(List.of(Suit.DIAMOND, Suit.SPADE, Suit.HEART)), "He!!o");
    MaoRule silenceRule1 = new MaoRule(MaoRuleType.SILENCE,
        new Card(Rank.EIGHT, Suit.DIAMOND), "");
    MaoRule verbalRule1Copy = new MaoRule(MaoRuleType.VERBAL_PENALTY,
        new Card(Rank.NINE, Suit.DIAMOND), "ch3 ck");
    List<Rank> ranks = new ArrayList<>();
    ranks.add(Rank.NINE);
    ranks.add(Rank.QUEEN);
    MaoRule otherRule = new MaoRule(MaoRuleType.REVERSE_GAMEPLAY,
        ranks, new ArrayList<>(List.of(Suit.DIAMOND, Suit.SPADE, Suit.HEART)), "pOfOrder");
    store.add(verbalRule1);
    store.add(verbalRule2);
    store.add(silenceRule1);
    assertTrue(store.remove(verbalRule1Copy));
    assertFalse(store.remove(otherRule));
    Collection<MaoRule> compareWith = new ArrayList<>();
    compareWith.add(verbalRule2);
    compareWith.add(silenceRule1);
    for (MaoRule rule: store.getStoredRules()) {
      assertTrue(compareWith.contains(rule));
    }
    tearDown();
  }

  @Test
  public void testResetRules() throws IncompatibleRuleException {
    setUp();
    MaoRule verbalRule1 = new MaoRule(MaoRuleType.VERBAL_PENALTY,
        new Card(Rank.NINE, Suit.DIAMOND), "ch3 ck");
    MaoRule verbalRule2 = new MaoRule(MaoRuleType.VERBAL_PENALTY,
        new ArrayList<>(List.of(Rank.NINE, Rank.QUEEN)),
        new ArrayList<>(List.of(Suit.DIAMOND, Suit.SPADE, Suit.HEART)), "He!!o");
    MaoRule silenceRule1 = new MaoRule(MaoRuleType.SILENCE,
        new Card(Rank.EIGHT, Suit.DIAMOND), "");

    store.add(verbalRule1);
    store.add(verbalRule2);
    store.add(silenceRule1);

    assertFalse(store.getStoredRules().isEmpty());
    store.resetRules();
    assertTrue(store.getStoredRules().isEmpty());
    tearDown();
  }

  @Test
  public void testStoredRulesAreConsistent() {
    setUp();
    MaoRule verbalRule1 = new MaoRule(MaoRuleType.VERBAL_PENALTY,
        new Card(Rank.NINE, Suit.DIAMOND), "ch3 ck");
    MaoRule verbalRule2 = new MaoRule(MaoRuleType.VERBAL_PENALTY,
        new ArrayList<>(List.of(Rank.NINE, Rank.QUEEN)),
        new ArrayList<>(List.of(Suit.DIAMOND, Suit.SPADE, Suit.HEART)), "He!!o");
    MaoRule silenceRule1 = new MaoRule(MaoRuleType.SILENCE,
        new Card(Rank.EIGHT, Suit.DIAMOND), "");
    MaoRule silenceRule2 = new MaoRule(MaoRuleType.SILENCE,
        new Card(Rank.NINE, Suit.DIAMOND), "");
    Collection<MaoRule> maoRules = new ArrayList<>();
    maoRules.add(verbalRule1);
    maoRules.add(verbalRule2);
    maoRules.add(silenceRule1);
    MaoRuleStorage consistentStore = new MaoRuleStorage(maoRules);
    assertTrue(consistentStore.storedRulesAreConsistent());
    maoRules.add(silenceRule2);
    MaoRuleStorage inconsistentStore = new MaoRuleStorage(maoRules);
    assertFalse(inconsistentStore.storedRulesAreConsistent());
    tearDown();
  }

}
