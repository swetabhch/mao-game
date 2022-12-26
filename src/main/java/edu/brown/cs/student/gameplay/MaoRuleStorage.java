package edu.brown.cs.student.gameplay;

import edu.brown.cs.student.cards.Card;
import edu.brown.cs.student.cards.Rank;
import edu.brown.cs.student.cards.Suit;
import edu.brown.cs.student.rules.Action;
import edu.brown.cs.student.rules.RuleStorage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * The MaoRuleStorage class is an extension of the RuleStorage class for the game of Mao.
 * It checks for consistency among rules by checking for conflicting Mao rule types, like
 * silence and verbal penalty rules.
 */
public class MaoRuleStorage extends RuleStorage<MaoRule> {

  public MaoRuleStorage() {
    // empty constructor for copying purposes
  }

  /**
   * A constructor that directly assigns the Collection of rules.
   *
   * @param rules The Collection of rules to be stored.
   */
  public MaoRuleStorage(Collection<MaoRule> rules) {
    super(rules);
  }

  @Override
  public boolean isValid(MaoRule rule) {
    Collection<MaoRule> rules = super.getStoredRules();
    for (MaoRule mr : rules) {
      List<Card> mrCards = new ArrayList<>();
      List<Card> ruleCards = new ArrayList<>();
      for (Rank rank : mr.getRanks()) {
        for (Suit suit : mr.getSuits()) {
          mrCards.add(new Card(rank, suit));
        }
      }
      for (Rank rank : rule.getRanks()) {
        for (Suit suit : rule.getSuits()) {
          ruleCards.add(new Card(rank, suit));
        }
      }
      // iterate over cards covered by both rules to check for conflict
      List<Card> commonCards = this.listIntersection(mrCards, ruleCards);
      if (!commonCards.isEmpty()) {
        if (((mr.getType() == MaoRuleType.SILENCE)
            && (rule.getType() == MaoRuleType.VERBAL_PENALTY))
            || (mr.getType() == MaoRuleType.VERBAL_PENALTY)
            && (rule.getType() == MaoRuleType.SILENCE)) {
          return false;
        }
      }
    }
    return true;
  }

  /**
   * Obtains a List of elements common to two Lists of the same type.
   *
   * @param l1  The first List to examine.
   * @param l2  The second List to examine.
   * @param <T> A generic type parameter representing the type of elements in either List.
   * @return A List of elements common to both input Lists.
   */
  private <T> List<T> listIntersection(List<T> l1, List<T> l2) {
    List<T> result = new ArrayList<>();
    for (T elem : l1) {
      if (l2.contains(elem)) {
        result.add(elem);
      }
    }
    return result;
  }

}
