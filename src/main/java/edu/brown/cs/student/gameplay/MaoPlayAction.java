package edu.brown.cs.student.gameplay;

import edu.brown.cs.student.cards.Card;
import edu.brown.cs.student.rules.Action;

import java.util.Objects;

/**
 * Represents the action a player makes in the game of Mao while playing
 * a card with an associated message. Implements the Action interface, so can
 * be used with interfaces and classes associated with Rules.
 */
public class MaoPlayAction implements Action {

  private final Card card;
  private final String message;

  public MaoPlayAction(Card card, String message) {
    this.card = card;
    this.message = message;
  }

  @Override
  public Card getCard() {
    return this.card;
  }

  @Override
  public String getMessage() {
    return this.message;
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.card, this.message);
  }

  @Override
  public boolean equals(Object obj) {
    if (!(obj instanceof MaoPlayAction)) {
      return false;
    }
    MaoPlayAction otherMpa = (MaoPlayAction) obj;
    return this.card.equals(otherMpa.getCard()) && this.message.equals(otherMpa.getMessage());
  }
}
