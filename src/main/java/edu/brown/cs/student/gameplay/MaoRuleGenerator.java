package edu.brown.cs.student.gameplay;

import edu.brown.cs.student.cards.Rank;
import edu.brown.cs.student.cards.Suit;
import edu.brown.cs.student.csvParser.StringCSVParser;
import edu.brown.cs.student.maoExceptions.IncompatibleRuleException;
import edu.brown.cs.student.rules.RuleGenerator;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * A rule generator specifically for the game of Mao. Implements the RuleGenerator
 * interface for MaoRules.
 */
public class MaoRuleGenerator implements RuleGenerator<MaoRule> {

  private final String phrasesFilename;
  private MaoRuleStorage ruleStorage;
  private Map<String, String> phraseHintMapping;

  /**
   * A constructor that directly assigns the filename of the database of rules.
   * @param filename The filename pointing towards the database of rules.
   */
  public MaoRuleGenerator(String filename) {
    this.phrasesFilename = filename;
    this.ruleStorage = new MaoRuleStorage(new ArrayList<>());
    this.phraseHintMapping = new HashMap<>();
  }

  /**
   * A constructor that sets the default filename of the database of rules to an empty String.
   */
  public MaoRuleGenerator() {
    this("");
  }

  // for future development -- possibility of picking rules from a database curated with
  //  traditional / interesting rules instead of auto-generated ones.

  //  private void setRuleDB() {
  //  }
  //  private Collection<MaoRule> generateRulesFromDB(int numRules) {
  //    return new ArrayList<>();
  //  }

  /**
   * Reads in the CSV file of phrases and hints, and sets up the phraseHintMapping
   * instance variable such that it maps from these phrases to their corresponding hints.
   *
   * @throws Exception If the CSV file is not found or is malformed.
   */
  private void setupPhraseHintMapping() throws Exception {
    StringCSVParser parser = new StringCSVParser(this.phrasesFilename);
    parser.loadCSVData();
    Map<String, List<String>> origDataStringMap = parser.getDataStringMap();
    for (String phrase: origDataStringMap.keySet()) {
      this.phraseHintMapping.put(phrase, origDataStringMap.get(phrase).get(0));
    }
  }

  /**
   * Obtains a random phrase to say from a text file containing such phrases separated
   * by newlines.
   *
   * @return A String representing a phrase to be associated with a rule.
   * @throws FileNotFoundException If the text file with phrases is not found.
   */
  private String getRandomPhrase() throws FileNotFoundException, Exception {
    setupPhraseHintMapping();
    Random rand = new Random();
    List<String> phraseArray = new ArrayList<>(phraseHintMapping.keySet());
    return phraseArray.get(rand.nextInt(phraseArray.size()));
  }

  @Override
  public Collection<MaoRule> generateRules(int numRules) throws Exception {
    setupPhraseHintMapping();
    Random rand = new Random();
    this.ruleStorage = new MaoRuleStorage(new ArrayList<>());
    int i = 0;

    while (i < numRules) {
      int numRanks = rand.nextInt(Rank.values().length) + 1;
      int numSuits = (rand.nextInt(Suit.values().length) / 2) + 1;
      List<Rank> shuffledRanks = Arrays.asList(Rank.values());
      Collections.shuffle(shuffledRanks);
      List<Suit> shuffledSuits = Arrays.asList(Suit.values());
      Collections.shuffle(shuffledSuits);
      List<Rank> ranks = shuffledRanks.subList(0, numRanks);
      List<Suit> suits = shuffledSuits.subList(0, numSuits);
      MaoRuleType type = MaoRuleType.values()[rand.nextInt(MaoRuleType.values().length)];
      if (type == MaoRuleType.SILENCE) {
        continue;
      }
      String message = this.getRandomPhrase();
      String hint = this.phraseHintMapping.get(message);
      try {
        this.ruleStorage.add(new MaoRule(type, ranks, suits, message, hint));
      } catch (IncompatibleRuleException e) {
        continue;
      }
      i++;
    }
    return this.ruleStorage.getStoredRules();
  }
}
