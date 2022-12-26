package edu.brown.cs.student.gameplay;

import com.google.gson.Gson;
import com.google.common.collect.ImmutableMap;
import edu.brown.cs.student.cards.Card;
import edu.brown.cs.student.cards.Rank;
import edu.brown.cs.student.cards.Suit;
import edu.brown.cs.student.maoExceptions.CardNotFoundException;
import edu.brown.cs.student.maoExceptions.IncompatibleRuleException;
import edu.brown.cs.student.maoExceptions.InconsistentSettingsException;
import edu.brown.cs.student.maoExceptions.PlayerNotFoundException;
import edu.brown.cs.student.server.ServerMessage;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

/**
 * The Board class represents the overall game board for Mao, which includes
 * the players, the deck of cards, turn information, game rules, and penalty
 * maintenance. This is the central class for the game and has the functionality
 * to run the game.
 */
public class Board {

  private static final Gson GSON = new Gson();
  private static final int NUM_CARDS_TO_START = 7;
  private static final int NUM_AUTO_RULES = 3;

  private static final String POINT_OF_ORDER_MESSAGE = "point of order";
  private static final String END_POINT_OF_ORDER_MESSAGE = "end point of order";
  private static final String PLAYED_OUT_OF_TURN_HINT = "Played out of turn.";
  private static final String NO_TALKING_HINT = "Excessive talking.";
  private static final String RANK_SUIT_MISMATCH_HINT = "Neither suit nor rank matches last card.";
  private static final String EXTRANEOUS_SPEECH_WITH_CARD_HINT = "Excessive talking.";

  private final List<Player> players;
  private Map<Integer, Player> idPlayerMap;

  // first elements of deck and playedPile are "on top"
  private final LinkedList<Card> deck;
  // should never be empty
  private LinkedList<Card> playedPile;

  private MaoRuleStorage rules;
  private int numAIPlayers;
  private boolean gameplayReversed;
  private int turnIndex;
  private boolean pointOfOrderOn;
  private String lobbyCode;

  public Board(List<MaoPlayer> maoPlayers, Settings settings, String lobbyCode) {
    this.players = new ArrayList<>();
    this.idPlayerMap = new HashMap<>();
    this.lobbyCode = lobbyCode;
    for (MaoPlayer mp : maoPlayers) {
      this.players.add(mp);
      this.idPlayerMap.put(mp.getId(), mp);
    }

    if (settings.isUsingAIPlayers()) {
      // add AI players
      int numAI = settings.getNumPlayers() - players.size();
      for (int i = 0; i < numAI; i++) {
        AiPlayer aiPlayer = new AiPlayer("AI Player " + (i + 1),
            settings.getAIDifficulty(), this.lobbyCode);
        this.players.add(aiPlayer);
        this.idPlayerMap.put(aiPlayer.getId(), aiPlayer);
        sendAIInit(aiPlayer);
      }
      this.numAIPlayers = numAI;
    } else {
      this.numAIPlayers = 0;
    }

    // create deck from numDecks
    this.deck = new LinkedList<>();
    for (int i = 0; i < settings.getNumDecks(); i++) {
      // adds cards to the end of the LinkedList
      this.deck.addAll(Card.generateDeck());
    }
    // assign custom rules if usingCustomRules else generate rules
    if (settings.isUsingCustomRules()) {
      this.rules = new MaoRuleStorage(settings.getCustomRules());
    } else {
      MaoRuleGenerator rg = new MaoRuleGenerator("data/phrases.csv");
      try {
        this.rules = new MaoRuleStorage(rg.generateRules(NUM_AUTO_RULES));
      } catch (Exception fe) {
        // should not get here
        System.out.println(fe.getMessage());
      }
    }
    this.gameplayReversed = false;
    this.turnIndex = 0;
    this.pointOfOrderOn = false;
    this.playedPile = new LinkedList<>();
    // this.startGame(); -- uncomment if you want starting to coincide with creation
  }

  /**
   * Constructor for empty board with no players, default settings.
   *
   * @param lobbyCode code for the lobby this board is in
   */
  public Board(String lobbyCode) throws InconsistentSettingsException {
    this(new ArrayList<>(), new Settings(), lobbyCode);
  }

  /**
   * A constructor that directly assigns each parameter of the board.
   *
   * @param deck      The deck of cards to draw from.
   * @param players   The list of players involved in the game.
   * @param turnIndex The player whose turn it is initially.
   * @param rules     The rules that the given board plays with.
   */
  public Board(LinkedList<Card> deck, List<Player> players, int turnIndex,
               MaoRuleStorage rules) {
    this.deck = deck;
    this.players = players;
    this.turnIndex = turnIndex;
    this.rules = rules;
    this.pointOfOrderOn = false;
  }

  /**
   * A constructor that only takes in the game's rules and newly initializes
   * every other parameter.
   *
   * @param rules The Collection of Rules that this board considers for its games.
   */
  public Board(MaoRuleStorage rules) {
    this(new LinkedList<>(), new ArrayList<>(), 0, rules);
  }

  /**
   * Obtains the player whose turn it is currently.
   *
   * @return The Player whose turn it is currently.
   */
  public Player getCurrentPlayer() {
    return this.players.get(this.turnIndex);
  }

  /**
   * Obtains the List of Players for the current Board.
   *
   * @return A defensive copy of the stored List of Players in the current Board.
   */
  public List<Player> getPlayers() {
    return new ArrayList<>(players);
  }

  /**
   * Returns the number of players involved in the given Board.
   *
   * @return An int representing the number of players involved in the given Board.
   */
  public int numPlayers() {
    return this.players.size();
  }

  /**
   * Removes a given Player from the Board if the Board accounts for them; throws an error
   * if this player isn't found.
   *
   * @param p The Player to remove from the Board.
   * @throws PlayerNotFoundException When the given player is not found in the Board.
   */
  public void removePlayer(Player p) throws PlayerNotFoundException {
    try {
      // shuffle cards back into the deck
      this.addShuffledCardsToDeck(new ArrayList<>(p.getCards()));

      // advance turn if it's the player's turn
      if (p.getId() == this.getCurrentPlayer().getId()) {
        this.skipTurn();
      }
      Player currentPlayer = this.getCurrentPlayer();

      // warn AI if the lobby is going to be deleted
      if (((MaoPlayer) p).isHost()) {
        try {
          URL serverURL = new URL(AiPlayer.SERVER_URL + "/removeLobby");
          HttpURLConnection connPy = (HttpURLConnection) serverURL.openConnection();
          connPy.setRequestMethod("POST");
          connPy.setRequestProperty("content-type", "application/json");
          connPy.setRequestProperty("Accept", "application/json");
          connPy.setDoOutput(true);

          String jsonInputString = "{\"lobby\": \"" + this.lobbyCode + "\"}";

          try (OutputStream os = connPy.getOutputStream()) {
            byte[] input = jsonInputString.getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
          }

          try (BufferedReader br = new BufferedReader(
              new InputStreamReader(connPy.getInputStream(), StandardCharsets.UTF_8))) {
            StringBuilder response = new StringBuilder();
            String responseLine;
            while ((responseLine = br.readLine()) != null) {
              response.append(responseLine.trim());
            }
            System.out.println(response);
          }
        } catch (Exception e) {
          e.printStackTrace();
        }
      }

      // remove player
      this.players.remove(p);
      this.idPlayerMap.remove(p.getId());

      // recompute turn index
      this.setTurnIndex(this.players.indexOf(currentPlayer));

      // send message to update frontend
      String playerLeaveJson = GSON.toJson(ImmutableMap.of(
          "type", ServerMessage.PLAYER_LEAVE_BOARD.ordinal(),
          "payload", ImmutableMap.of(
              "player", p,
              "players", this.players,
              "turnIndex", this.turnIndex
          )
      ));

      sendToAllPlayers(playerLeaveJson);
    } catch (Exception e) {
      throw new PlayerNotFoundException("ERROR: Player to remove not found in board.");
    }
  }

  /**
   * Obtains the index of the player whose turn it is.
   *
   * @return An int representing the index of the player whose turn it is.
   */
  public int getTurnIndex() {
    return turnIndex;
  }

  /**
   * Set the Board's turn index to the given index.
   *
   * @param turnIndex The index of the Player whose turn is to arrive.
   */
  private void setTurnIndex(int turnIndex) {
    this.turnIndex = turnIndex;
  }

  /**
   * Obtains the card that was previously played. If no such card exists, returns null.
   *
   * @return The Card that was last played.
   */
  public Card getLastPlayed() {
    return this.playedPile.peekFirst();
  }

  /**
   * Returns the top Card of the deck and removes it from the deck.
   *
   * @return The top Card from the deck.
   * @throws NoSuchElementException When the deck is empty.
   */
  public Card popTopCard() throws NoSuchElementException {
    if (this.deck.isEmpty()) {
      // throws exception if neither deck nor playedPile has cards
      Card topCardFromPlayed = this.playedPile.pollFirst();
      this.addShuffledCardsToDeck(this.playedPile);
      if (topCardFromPlayed == null) {
        throw new NoSuchElementException("ERROR: No cards in deck or playedPile.");
      } else {
        this.playedPile = new LinkedList<>(List.of(topCardFromPlayed));
      }
    }

    // deck is theoretically still empty if the play pile only has one card
    // simply create an ace of spades to prevent this from crashing the game
    return (!this.deck.isEmpty() ? this.deck.pollFirst() : new Card(Rank.ACE, Suit.SPADE));
  }

  /**
   * Shuffles the current deck of cards.
   */
  public void shuffleDeck() {
    Collections.shuffle(this.deck);
  }

  /**
   * Shuffles a List of Cards and adds them to the bottom of the deck.
   *
   * @param newCards The new Cards to be added to the deck.
   */
  public void addShuffledCardsToDeck(List<Card> newCards) {
    // adds cards to the end of the LinkedList
    Collections.shuffle(newCards);
    this.deck.addAll(newCards);
  }

  /**
   * Obtains the RuleStorage object containing the Rules that this Board uses for gameplay.
   *
   * @return The RuleStorage object that this Board uses for gameplay.
   */
  public MaoRuleStorage getRuleStorage() {
    return new MaoRuleStorage(this.rules.getStoredRules());
  }

  /**
   * Obtains the Collection of Rules that this Board uses for gameplay.
   *
   * @return The Collection of Rules that this Board uses for gameplay.
   */
  public Collection<MaoRule> getRules() {
    return this.rules.getStoredRules();
  }

  /**
   * Adds a Rule to the Board's current RuleStorage.
   *
   * @param newRule The new Rule to be added.
   */
  public void addRule(MaoRule newRule) throws IncompatibleRuleException {
    this.rules.add(newRule);
  }

  public void startGame() {
    this.shuffleDeck();
    this.distributeCardsToPlayers();
    Card startingCard = this.popTopCard();
    this.playedPile.addFirst(startingCard);

    String startGameJson = GSON.toJson(ImmutableMap.of(
        "type", ServerMessage.START_GAME.ordinal(),
        "payload", ImmutableMap.of(
            "players", this.getPlayers(),
            "turnIndex", this.getTurnIndex(),
            "formedRules", this.getRules(),
            "startingCard", startingCard
        )
    ));
    sendToAllPlayers(startGameJson);
  }

  /**
   * To be used at the start of the game; distributes NUM_CARDS_TO_START cards to each player.
   */
  private void distributeCardsToPlayers() {
    for (Player maoPlayer : this.players) {
      for (int i = 0; i < NUM_CARDS_TO_START; i++) {
        maoPlayer.addCard(this.popTopCard());
      }
    }
  }

  /**
   * Applies a penalty to a given player and sends the resulting message to the rest
   * of the players.
   *
   * @param playerId The id of the player that incurs the penalty.
   * @param hint     The hint shown if the rule is violated.
   * @throws NoSuchElementException If the player on whom the penalty is to be applied
   *                                does not exist in the Board.
   */
  public void applyPenalty(int playerId, String hint) throws NoSuchElementException {
    Player maoPlayer = idPlayerMap.get(playerId);
    this.penalty(maoPlayer);
    String playerPenaltyJson = GSON.toJson(ImmutableMap.of(
        "type", ServerMessage.PENALTY_INCURRED.ordinal(),
        "payload", ImmutableMap.of(
            "username", maoPlayer.getUsername(),
            "players", this.players,
            "hint", hint
        )
    ));
    sendToAllPlayers(playerPenaltyJson);
  }

  /**
   * Takes in input representing an action in the chat, and deals with it according
   * to point of order rules or imposes a penalty.
   *
   * @param playerId    The id of the player that sent the message.
   * @param chatMessage The message sent by the player in the chat.
   */
  public void chatMessage(int playerId, String chatMessage) {
    String sanitizedMessage = chatMessage.strip().toLowerCase();
    if (!pointOfOrderOn && sanitizedMessage.equalsIgnoreCase(POINT_OF_ORDER_MESSAGE)) {
      startPointOfOrder();
      return;
    } else if (pointOfOrderOn && sanitizedMessage.equalsIgnoreCase(END_POINT_OF_ORDER_MESSAGE)) {
      endPointOfOrder();
      return;
    }

    // might not be best to send two messages in succession
    Player maoPlayer = idPlayerMap.get(playerId);
    String chatMessageJson = GSON.toJson(ImmutableMap.of(
        "type", ServerMessage.CHAT_MESSAGE.ordinal(),
        "payload", ImmutableMap.of(
            "username", maoPlayer.getUsername(),
            "message", chatMessage
        )
    ));
    sendToAllPlayers(chatMessageJson);
    if (!pointOfOrderOn) {
      applyPenalty(playerId, NO_TALKING_HINT);
    }
  }

  /**
   * Draws a card from the deck and gives it to a player with a given ID.
   *
   * @param playerId An integer representing the ID of the player.
   */
  public void drawAndAdvanceTurn(int playerId) {
    Player player = idPlayerMap.get(playerId);
    player.addCard(this.popTopCard());
    String cardDrawnJson = GSON.toJson(ImmutableMap.of(
        "type", ServerMessage.CARD_PLAYED.ordinal(),
        "payload", ImmutableMap.builder()
            .put("username", player.getUsername())
            .put("card", this.getLastPlayed())
            .put("message", "")
            .put("players", this.players)
            .put("turnIndex", this.nextTurnIndex())
            .put("hints", new ArrayList<>())
            .build()
    ));
    sendToAllPlayers(cardDrawnJson);
    this.advanceTurn();
  }

  /**
   * Gets the current player to play a card, exerts the effect of that play by comparing
   * it to the stored rules in the board, applying penalties if needed, and advancing the turn.
   *
   * @param playerId The id of the player playing this move.
   * @param card     The card being played.
   * @param message  The message associated with the card.
   * @return True if the game ended as a result of playing the card.
   * @throws CardNotFoundException If the card being played does not belong to the current
   *                               player's hand.
   */
  public boolean playCardWithMessage(int playerId, Card card, String message)
      throws CardNotFoundException {
    Player player = idPlayerMap.get(playerId);
    Collection<Card> hand = player.getCards();

    if (!hand.contains(card)) {
      // should never get here if correctly implemented
      throw new CardNotFoundException("ERROR: Player does not have the required card.");
    } else if (this.getCurrentPlayer().getId() != player.getId()) {
      // if someone plays out of turn, apply penalty, don't advance turn, and don't play card
      this.applyPenalty(playerId, PLAYED_OUT_OF_TURN_HINT);
      return false;
    }
    Collection<String> hints = this.checkRulesAndGetHints(playerId, card, message);
    boolean violatedFlag = !hints.isEmpty();

    if (!violatedFlag) {
      // play the card
      player.removeCard(card);
      this.playedPile.addFirst(card);
      // check win condition whenever a card is successfully played
      if (checkWinCondition(player, message)) {
        return true;
      }
      // only send successful human actions to the AI
      if (this.numAIPlayers > 0) {
        sendAIHumanAction(card, message, player.getCards().size() + 1);
      }
    }

    String cardPlayedWithMessage = GSON.toJson(ImmutableMap.of(
        "type", ServerMessage.CARD_PLAYED.ordinal(),
        "payload", ImmutableMap.builder()
            .put("username", player.getUsername())
            .put("card", this.getLastPlayed())
            .put("message", message)
            .put("players", this.players)
            .put("turnIndex", this.nextTurnIndex())
            .put("hints", hints)
            .build()
    ));
    sendToAllPlayers(cardPlayedWithMessage);

    // extraneous speech check
    if (this.extraneousSpeechCheck(playerId, card, message)) {
      this.applyPenalty(playerId, EXTRANEOUS_SPEECH_WITH_CARD_HINT);
    }

    this.advanceTurn();
    return false;
  }

  /**
   * Checks whether the player included text beyond what was necessary to satisfy rules
   * while playing their card.
   *
   * @param playerId The ID of the player currently playing.
   * @param card     The card played by the player.
   * @param message  The message sent by the player.
   * @return A boolean representing whether or not the player said more than what was necessary.
   */
  private boolean extraneousSpeechCheck(int playerId, Card card, String message) {
    Player player = idPlayerMap.get(playerId);
    Collection<Card> hand = player.getCards();
    MaoPlayAction action = new MaoPlayAction(card, message);

    // split message into verbal components; used in extraneous speech check
    String[] verbalsArray = message.strip().toLowerCase().split(";");
    List<String> verbalsList = Arrays.stream(verbalsArray).map(String::strip)
        .filter(s -> !s.equals("")).collect(Collectors.toList());

    boolean violatedFlag = false;
    for (MaoRule mr : this.rules.getStoredRules()) {
      // remove from verbalsList if verbal and not yet violated
      if (mr.ruleViolated(action)) {
        violatedFlag = true;
      }
      if (!violatedFlag && mr.ruleAppliesToCard(card) && mr.getType() == MaoRuleType.VERBAL_PENALTY) {
        String ruleVerbal = mr.getVerbal().strip().toLowerCase();
        verbalsList.remove(ruleVerbal);
      }
    }

    boolean finalStep = (hand.isEmpty()) && (verbalsList.equals(new ArrayList<>(List.of("mao"))));
    return !verbalsList.isEmpty() && !finalStep;
  }

  /**
   * Iterates over the stored rules, applies their relevant effects if violated, and compiles
   * a list of their hints to return.
   *
   * @param playerId The ID of the player currently playing the card with message.
   * @param card     The card the player plays.
   * @param message  A String representing the message sent by the player.
   * @return A Collection of Strings representing the hints gathered due to rule violations.
   */
  private Collection<String> checkRulesAndGetHints(int playerId, Card card, String message) {
    Player player = idPlayerMap.get(playerId);
    Collection<Card> hand = player.getCards();
    Collection<String> hints = new ArrayList<>();

    // check for Uno rule: current card's suit/rank matches last card's suit/rank
    // if broken, treat it like a normal rule (apply penalty, advance turn, and don't play card)
    if (card.getSuit() != this.getLastPlayed().getSuit()
        && card.getRank() != this.getLastPlayed().getRank()) {
      this.penalty(player);
      hints.add(RANK_SUIT_MISMATCH_HINT);
    } else {
      // enforce/check against rules and collect hints
      MaoPlayAction action = new MaoPlayAction(card, message);
      for (MaoRule mr : this.rules.getStoredRules()) {
        // ignore silence rules on last turn when saying "mao"
        if (hand.size() == 1 && mr.getType() == MaoRuleType.SILENCE && message.equals("mao")) {
          continue;
        }
        mr.ruleEffect(this, action);
        if (mr.ruleViolated(action)) {
          hints.add(mr.getHint());
        }
      }
    }
    return hints;
  }

  /**
   * Applies a penalty to the player whose turn it is.
   */
  public void penaltyOnCurrent() throws NoSuchElementException {
    this.penalty(this.getCurrentPlayer());
  }

  /**
   * Initiates point of order state.
   */
  public void startPointOfOrder() {
    String startPointOfOrderJson = GSON.toJson(ImmutableMap.of(
        "type", ServerMessage.START_POINT_OF_ORDER.ordinal(),
        "payload", ImmutableMap.of()
    ));
    sendToAllPlayers(startPointOfOrderJson);
    this.pointOfOrderOn = true;
  }

  /**
   * Suspends point of order state.
   */
  public void endPointOfOrder() {
    String endPointOfOrderJson = GSON.toJson(ImmutableMap.of(
        "type", ServerMessage.END_POINT_OF_ORDER.ordinal(),
        "payload", ImmutableMap.of()
    ));
    sendToAllPlayers(endPointOfOrderJson);
    this.pointOfOrderOn = false;
  }

  /**
   * Reverses the order of gameplay.
   */
  public void reversePlayerOrder() {
    this.gameplayReversed = !this.gameplayReversed;
  }

  /**
   * Updates the turn index so that it moves on to the next player.
   */
  public void advanceTurn() {
    this.turnIndex = this.nextTurnIndex();

    if (getCurrentPlayer() instanceof AiPlayer) {
      AiPlayer aiPlayer = (AiPlayer) this.getCurrentPlayer();
      MaoPlayAction aiCardAction = aiPlayer.predictCard(getLastPlayed());
      try {
        this.playCardWithMessage(aiPlayer.getId(), aiCardAction.getCard(),
            aiCardAction.getMessage());
      } catch (CardNotFoundException e) {
        System.out.println("Card not found exception from ai player");
      }
    }
  }

  private int nextTurnIndex() {
    return Math.floorMod((this.turnIndex + (!gameplayReversed ? 1 : -1)),
        this.numPlayers());
  }

  /**
   * Determines whether or not the Board is in a point of order state.
   *
   * @return A boolean representing whether or not the Board is in a point of order state.
   */
  public boolean isPointOfOrderOn() {
    return pointOfOrderOn;
  }

  /**
   * Determines if the gameplay order is currently reversed.
   *
   * @return A boolean representing whether gameplay order is reversed.
   */
  public boolean isGameplayReversed() {
    return gameplayReversed;
  }

  /**
   * Skips the player's turn.
   */
  public void skipTurn() {
    this.turnIndex = this.nextTurnIndex();
  }

  /**
   * Checks if a player sends "mao" in the chat while playing their final card.
   *
   * @param player  The player potentially playing their final round.
   * @param message The message the player sends in the chat along with their
   *                potential final card.
   * @return True if the win condition is met.
   */
  private boolean checkWinCondition(Player player, String message) {
    // winning condition: player has no cards left and player says mao when playing final card
    Collection<Card> finalCards = player.getCards();
    if (finalCards.isEmpty()) {
      boolean maoFound = false;
      String[] inputs = message.split(";");
      for (String inp : inputs) {
        if (inp.equalsIgnoreCase("mao")) {
          maoFound = true;
          break;
        }
      }
      if (!maoFound) {
        for (int i = 0; i < 5; i++) {
          this.applyPenalty(player.getId(), "Not saying Mao, card " + (i + 1) + " added.");
        }
        return false;
      } else {
        String gameWonMessage = GSON.toJson(ImmutableMap.of(
            "type", ServerMessage.END_GAME.ordinal(),
            "payload", ImmutableMap.of(
                "username", player.getUsername()
            )
        ));
        sendToAllPlayers(gameWonMessage);
        return true;
      }
    }
    return false;
  }

  /**
   * Initializes AI Player on the AI web server.
   *
   * @param aiPlayer ai player to be initialized
   */
  private void sendAIInit(AiPlayer aiPlayer) {
    try {
      URL serverURL = new URL(AiPlayer.SERVER_URL + "/start");
      HttpURLConnection connPy = (HttpURLConnection) serverURL.openConnection();
      connPy.setRequestMethod("POST");
      connPy.setRequestProperty("content-type", "application/json");
      connPy.setRequestProperty("Accept", "application/json");
      connPy.setDoOutput(true);

      String jsonInputString = "{\"numAI\": \"" + aiPlayer.getId()
          + "\", \"aiDifficulty\": \"" + aiPlayer.getDifficulty()
          + "\", \"lobby\": \"" + this.lobbyCode + "\"}";

      try (OutputStream os = connPy.getOutputStream()) {
        byte[] input = jsonInputString.getBytes(StandardCharsets.UTF_8);
        os.write(input, 0, input.length);
      }

      try (BufferedReader br = new BufferedReader(
          new InputStreamReader(connPy.getInputStream(), StandardCharsets.UTF_8))) {
        StringBuilder response = new StringBuilder();
        String responseLine;
        while ((responseLine = br.readLine()) != null) {
          response.append(responseLine.trim());
        }
        System.out.println(response);
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * Sends a correct/valid card played + message combination to the AI for it to use when predicting
   * cards to play/messages to send.
   *
   * @param cardPlayed     Card that the human played
   * @param message        Message that the human sent with the card played
   * @param numCardsAtTurn number of cards that the human had when they played this card/message
   */
  private void sendAIHumanAction(Card cardPlayed, String message, int numCardsAtTurn) {
    try {
      URL serverURL = new URL(AiPlayer.SERVER_URL + "/human");
      HttpURLConnection connPy = (HttpURLConnection) serverURL.openConnection();
      connPy.setRequestMethod("POST");
      connPy.setRequestProperty("content-type", "application/json");
      connPy.setRequestProperty("Accept", "application/json");
      connPy.setDoOutput(true);

      String jsonInputString = "{\"suit\": " + "\"" + cardPlayed.getSuit().name() + "\", \"rank\": "
          + "\"" + cardPlayed.getRank().name() + "\", \"phrase\": \"" + message
          + "\", \"num_cards\": \"" + numCardsAtTurn
          + "\", \"lobby\": \"" + this.lobbyCode + "\"}";
      try (OutputStream os = connPy.getOutputStream()) {
        byte[] input = jsonInputString.getBytes("utf-8");
        os.write(input, 0, input.length);
      }

      try (BufferedReader br = new BufferedReader(
          new InputStreamReader(connPy.getInputStream(), "utf-8"))) {
        StringBuilder response = new StringBuilder();
        String responseLine = null;
        while ((responseLine = br.readLine()) != null) {
          response.append(responseLine.trim());
        }
        System.out.println(response.toString());
      }

    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * Sends a message to all human players involved in the game of Mao.
   *
   * @param message The message to be sent to the human players.
   */
  private void sendToAllPlayers(String message) {
    for (Player p : this.players) {
      if (p instanceof MaoPlayer) {
        MaoPlayer maoPlayer = (MaoPlayer) p;
        try {
          maoPlayer.getSocket().getRemote().sendString(message);
        } catch (IOException e) {
          System.out.println("Couldn't send a message to player "
              + maoPlayer.getUsername() + ".");
        }
      }
    }
  }

  /**
   * Applies a penalty to a given player.
   *
   * @param player The player that incurs the penalty.
   */
  private void penalty(Player player) throws NoSuchElementException {
    player.addCard(this.popTopCard());
  }
}
