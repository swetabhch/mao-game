package edu.brown.cs.student.gameplay;

import edu.brown.cs.student.cards.Card;
import edu.brown.cs.student.cards.Rank;
import edu.brown.cs.student.cards.Suit;
import edu.brown.cs.student.rules.Action;
import edu.brown.cs.student.rules.Rule;
import edu.brown.cs.student.rules.RuleType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

/**
 * The MaoRule class describes a Rule specific to the game of Mao. This draws
 * from the MaoRuleType enum to exert different effects on a Board in such a game.
 */
public class MaoRule implements Rule {

  // rule applies to all cards whose rank is in the rank list and suit is in the suit list
  private final MaoRuleType type;
  private final List<Rank> ranks;
  private final List<Suit> suits;
  private final String verbal;
  private final String hint;

  /**
   * A constructor that directly assigns the value of each parameter.
   * @param type The type of the rule in Mao.
   * @param ranks The Ranks associated with the rule.
   * @param suits The Suits associated with the rule.
   * @param verbal The message associated with the Mao rule, if it is a verbal rule.
   * @param hint The hint sent to players when the rule is violated.
   */
  public MaoRule(MaoRuleType type, List<Rank> ranks, List<Suit> suits, String verbal, String hint) {
    this.type = type;
    this.ranks = ranks;
    this.suits = suits;
    this.verbal = verbal;
    this.hint = hint;
  }

  /**
   * A constructor that sets the hint parameter to the empty string.
   * @param type The type of the rule in Mao.
   * @param ranks The Ranks associated with the rule.
   * @param suits The Suits associated with the rule.
   * @param verbal The message associated with the Mao rule, if it is a verbal rule.
   */
  public MaoRule(MaoRuleType type, List<Rank> ranks, List<Suit> suits, String verbal) {
    this(type, ranks, suits, verbal, "");
  }

  /**
   * A constructor that sets the lists of Ranks and Suits to singleton lists from a
   * given Card.
   * @param type The type of the rule in Mao.
   * @param card The Card associated with the rule.
   * @param verbal The message associated with the Mao rule, if it is a verbal rule.
   */
  public MaoRule(MaoRuleType type, Card card, String verbal) {
    this(type, new ArrayList<>(List.of(card.getRank())),
        new ArrayList<>(List.of(card.getSuit())), verbal);
  }

  /**
   * A constructor that sets the verbal parameter to null.
   * @param type The type of the rule in Mao (assumed not verbal).
   * @param ranks The Ranks associated with the rule.
   * @param suits The Suits associated with the rule.
   */
  public MaoRule(MaoRuleType type, List<Rank> ranks, List<Suit> suits) {
    this(type, ranks, suits, null);
  }

  @Override
  public Predicate<Action> getSituation() {
    return action -> {
      Card actionCard = action.getCard();
      if (ranks.contains(actionCard.getRank()) && suits.contains(actionCard.getSuit())) {
        return !verbal.equals(action.getMessage());
      } else {
        return false;
      }
    };
  }


  @Override
  public RuleType getType() {
    return this.type;
  }

  public String getHint() {
    return hint;
  }

  /**
   * Consumes an action and determines if it violates this rule.
   * @param action The Action to check.
   * @return Whether the action violates this rule.
   */
  // changes to ruleViolated must also be applied to ruleEffect
  @Override
  public boolean ruleViolated(Action action) {
    Card actionCard = action.getCard();
    boolean ruleApplies = ranks.contains(actionCard.getRank())
        && suits.contains(actionCard.getSuit());
    if (!ruleApplies) {
      return false;
    }

    String[] inputs = Arrays.stream(action.getMessage().strip().toLowerCase().split(";"))
        .map(String::strip).toArray(String[]::new);
    boolean messageFound = false;
    for (String inp : inputs) {
      if (inp.equals(this.verbal.strip().toLowerCase())) {
        messageFound = true;
        break;
      }
    }
    if (type == MaoRuleType.VERBAL_PENALTY) {
      return !messageFound;
    } else if (type == MaoRuleType.SILENCE) {
      return !verbal.equals("");
    } else {
      return false;
    }
  }

  /**
   * Determines whether a rule should check for further violation conditions given a
   * particular card.
   * @param card The card on which to check whether the rule applies.
   * @return A boolean representing whether the rule applies to the given card.
   */
  public boolean ruleAppliesToCard(Card card) {
    return ranks.contains(card.getRank())
        && suits.contains(card.getSuit());
  }

  /**
   * Consumes a Board and an Action, and depending on this rule's type and how it operates,
   * affects the Board's state too.
   * @param board The Board in which this Rule is being applied.
   * @param action The Action that this Rule is being checked with.
   */
  public void ruleEffect(Board board, Action action) {
    Card actionCard = action.getCard();
    boolean ruleApplies = ranks.contains(actionCard.getRank())
        && suits.contains(actionCard.getSuit());
    if (!ruleApplies) {
      return;
    }

    String[] inputs = Arrays.stream(action.getMessage().strip().toLowerCase().split(";"))
        .map(String::strip).toArray(String[]::new);
    boolean messageFound = false;
    for (String inp : inputs) {
      if (inp.equals(this.verbal.strip().toLowerCase())) {
        messageFound = true;
        break;
      }
    }
    if (type == MaoRuleType.VERBAL_PENALTY && !messageFound) {
      board.penaltyOnCurrent();
    } else if (type == MaoRuleType.SILENCE && !verbal.equals("")) {
      board.penaltyOnCurrent();
    } else if (type == MaoRuleType.REVERSE_GAMEPLAY) {
      board.reversePlayerOrder();
    } else if (type == MaoRuleType.SKIP_NEXT_PLAYER) {
      board.skipTurn();
    }
  }

  /**
   * Returns the ranks associated with this particular MaoRule.
   * @return The Ranks associated with this MaoRule.
   */
  public List<Rank> getRanks() {
    return ranks;
  }

  /**
   * Returns the suits associated with this particular MaoRule.
   * @return The Suits associated with this MaoRule.
   */
  public List<Suit> getSuits() {
    return suits;
  }

  /**
   * Returns the verbal message associated with this particular MaoRule. If
   * there is no such message, returns an empty String.
   * @return A String representing the message associated with this MaoRule.
   */
  public String getVerbal() {
    return verbal;
  }

  @Override
  public String toString() {
    StringBuilder rankString = new StringBuilder("[");
    for (Rank rank: this.ranks) {
      rankString.append(rank.toString());
      rankString.append(", ");
    }
    rankString.append("]");
    StringBuilder suitString = new StringBuilder("[");
    for (Suit suit: this.suits) {
      suitString.append(suit.toString());
      suitString.append(", ");
    }
    suitString.append("]");
    StringBuilder rule = new StringBuilder("Type: ");
    rule.append(this.type);
    rule.append("\nRanks: ");
    rule.append(rankString);
    rule.append("\nSuits: ");
    rule.append(suitString);
    rule.append("\nMessage: ");
    rule.append(this.verbal);
    rule.append("\nHint: ");
    rule.append(this.hint);

    return rule.toString();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    MaoRule maoRule = (MaoRule) o;
    return type == maoRule.getType() && ranks.equals(maoRule.getRanks())
        && suits.equals(maoRule.getSuits()) && Objects.equals(verbal, maoRule.getVerbal())
        && Objects.equals(hint, maoRule.getHint());
  }

  @Override
  public int hashCode() {
    return Objects.hash(type, ranks, suits, verbal, hint);
  }
}
