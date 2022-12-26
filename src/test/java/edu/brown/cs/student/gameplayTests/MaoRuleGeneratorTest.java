package edu.brown.cs.student.gameplayTests;

import edu.brown.cs.student.gameplay.MaoPlayAction;
import edu.brown.cs.student.gameplay.MaoRule;
import edu.brown.cs.student.gameplay.MaoRuleGenerator;
import edu.brown.cs.student.gameplay.MaoRuleStorage;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.FileNotFoundException;
import java.util.Collection;

import static org.junit.Assert.*;

public class MaoRuleGeneratorTest {

  private MaoRuleGenerator mrg;

  @Before
  public void setUp() {
    this.mrg = new MaoRuleGenerator("data/phrases.csv");
  }

  @After
  public void tearDown() {
    this.mrg = null;
  }

  @Test
  public void testGenerateRulesForIncorrectFile() {
    this.mrg = new MaoRuleGenerator("data/notPhrases.txt");
    try {
      this.mrg.generateRules(5);
      assertTrue(false);
    } catch (Exception e) {
      assertTrue(true);
    }
    tearDown();
  }

  @Test
  public void testGenerateRules() throws Exception {
    setUp();
    Collection<MaoRule> emptyRules = this.mrg.generateRules(0);
    assertTrue(emptyRules.isEmpty());
    Collection<MaoRule> singleRule = this.mrg.generateRules(1);
    assertEquals(1, singleRule.size());
    Collection<MaoRule> multiRules = this.mrg.generateRules(10);
    assertEquals(10, multiRules.size());
    MaoRuleStorage store = new MaoRuleStorage(multiRules);
    for (MaoRule rule: multiRules) {
      System.out.println(rule);
    }
    assertTrue(store.storedRulesAreConsistent());
    tearDown();
  }

}
