package edu.brown.cs.student.gameplay;

import edu.brown.cs.student.cards.Card;
import edu.brown.cs.student.maoExceptions.CardNotFoundException;
import org.eclipse.jetty.websocket.api.Session;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

/**
 * A Player object represents a player in a card game that holds a set of cards
 * that they can play. This includes functionality to add, remove, and show cards.
 */
public class MaoPlayer implements Player {

  private static int incrementingId = 0;

  private final int id;
  private final String username;
  private final boolean isHost;
  private final Collection<Card> cards;
  private final transient Session socket;

  public MaoPlayer(String username, boolean isHost, Session socket, Collection<Card> cards) {
    this.id = getAndIncrementId();
    this.username = username;
    this.isHost = isHost;
    this.socket = socket;
    this.cards = cards;
  }

  public MaoPlayer(String username, boolean isHost, Session socket) {
    this(username, isHost, socket, new ArrayList<>());
  }

  public static int getAndIncrementId() {
    return incrementingId++;
  }

  @Override
  public int getId() {
    return id;
  }

  @Override
  public String getUsername() {
    return username;
  }

  public boolean isHost() {
    return isHost;
  }

  public Session getSocket() {
    return socket;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    MaoPlayer maoPlayer = (MaoPlayer) o;
    return id == maoPlayer.id && Objects.equals(username, maoPlayer.username);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, username);
  }

  @Override
  public Collection<Card> getCards() {
    return new ArrayList<>(this.cards);
  }

  @Override
  public void addCard(Card card) {
    this.cards.add(card);
  }

  @Override
  public void removeCard(Card card) throws CardNotFoundException {
    if (!this.cards.remove(card)) {
      throw new CardNotFoundException("ERROR: Player does not have the given card.");
    }
  }
}
