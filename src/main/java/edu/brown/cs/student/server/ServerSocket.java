package edu.brown.cs.student.server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import edu.brown.cs.student.cards.Card;
import edu.brown.cs.student.gameplay.Board;
import edu.brown.cs.student.gameplay.Lobby;
import edu.brown.cs.student.gameplay.MaoPlayer;
import edu.brown.cs.student.gameplay.Settings;
import edu.brown.cs.student.maoExceptions.CardNotFoundException;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketError;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;

/**
 * The ServerSocket class handles messages sent from the frontend by responding
 * appropriately with backend functionality.
 */
@WebSocket(maxIdleTime = Integer.MAX_VALUE)
public class ServerSocket {

  private static final Gson GSON = new Gson();
  private final Collection<Lobby> lobbies = new ArrayList<>();
  private final Map<Session, Lobby> sessionLobbyMap = new HashMap<>();

  @OnWebSocketClose
  public void closed(Session session, int statusCode, String reason) {
    handleLeaveLobby(session);
  }

  @OnWebSocketError
  public void error(Throwable t) {
    t.printStackTrace();
  }

  @OnWebSocketMessage
  public void message(Session session, String message) throws IOException {
    JsonObject msgJson = GSON.fromJson(message, JsonObject.class);
    int type = msgJson.get("type").getAsInt();
    JsonObject payload = msgJson.getAsJsonObject("payload");

    if (type == ClientMessage.CREATE_LOBBY.ordinal()) {
      // create a lobby, making the requesting socket the host
      String username = payload.get("username").getAsString();
      handleCreateLobby(username, session);

    } else if (type == ClientMessage.JOIN_LOBBY.ordinal()) {
      // join a lobby, making the requesting socket a normal player
      String username = payload.get("username").getAsString();
      String lobbyCode = payload.get("lobbyCode").getAsString();
      handleJoinLobby(username, lobbyCode, session);

    } else if (type == ClientMessage.LEAVE_LOBBY.ordinal()) {
      handleLeaveLobby(session);

    } else if (type == ClientMessage.KICK_PLAYER.ordinal()) {
      int playerId = payload.get("playerId").getAsInt();
      handleKickPlayer(playerId, session);

    } else if (type == ClientMessage.UPDATE_SETTINGS.ordinal()) {
      Settings updatedSettings = GSON.fromJson(payload.get("settings"), Settings.class);
      handleUpdateSettings(updatedSettings, session);

    } else if (type == ClientMessage.PLAY_CARD_WITH_MESSAGE.ordinal()) {
      int playerId = payload.get("playerId").getAsInt();
      Card card = GSON.fromJson(payload.get("card"), Card.class);
      String playerMessage = payload.get("message").getAsString();
      handlePlayCardWithMessage(playerId, card, playerMessage, session);

    } else if (type == ClientMessage.MANUAL_PENALTY.ordinal()) {
      int playerId = payload.get("playerId").getAsInt();
      String hint = payload.get("hint").getAsString();
      handleManualPenalty(playerId, hint, session);

    } else if (type == ClientMessage.CHAT_MESSAGE.ordinal()) {
      int playerId = payload.get("playerId").getAsInt();
      String chatMessage = payload.get("chatMessage").getAsString();
      handleChatMessage(playerId, chatMessage, session);

    } else if (type == ClientMessage.START_GAME.ordinal()) {
      handleStartGame(session);

    } else if (type == ClientMessage.DRAW_CARD.ordinal()) {
      int playerId = payload.get("playerId").getAsInt();
      handleDrawCard(playerId, session);
    }
  }

  /**
   * Handler for when a host in the frontend attempts to create a lobby.
   * @param username A String representing the username of the host player.
   * @param session The relevant session for the game.
   */
  private void handleCreateLobby(String username, Session session) {
    Lobby createdLobby = new Lobby();
    MaoPlayer host = new MaoPlayer(username, true, session);
    createdLobby.addPlayer(host);
    sessionLobbyMap.put(session, createdLobby);
    lobbies.add(createdLobby);
  }

  /**
   * Handler for when a player attempts to join a lobby in the frontend.
   * @param username A String representing the username of the player.
   * @param lobbyCode A String representing the code needed to enter the lobby.
   * @param session The relevant session for the game.
   * @throws IOException When reading or sending fails.
   */
  private void handleJoinLobby(String username, String lobbyCode, Session session)
          throws IOException {
    MaoPlayer maoPlayer = new MaoPlayer(username, false, session);
    for (Lobby lobbyToJoin : lobbies) {
      if (lobbyCode.equalsIgnoreCase(lobbyToJoin.getLobbyCode())) {
        if (lobbyToJoin.isFull() || lobbyToJoin.isGameStarted()) {
          String lobbyLockedJson = GSON.toJson(ImmutableMap.of(
              "type", ServerMessage.LOBBY_LOCKED.ordinal(),
              "payload", ImmutableMap.of()
          ));
          session.getRemote().sendString(lobbyLockedJson);
        } else {
          lobbyToJoin.addPlayer(maoPlayer);
          sessionLobbyMap.put(session, lobbyToJoin);
        }
        return;
      }
    }
    // only get here if lobby code is invalid
    String invalidCodeJson = GSON.toJson(ImmutableMap.of(
        "type", ServerMessage.INVALID_LOBBY_CODE.ordinal(),
        "payload", ImmutableMap.of()
    ));
    session.getRemote().sendString(invalidCodeJson);
  }

  /**
   * Handler for when a player attempts to leave a lobby.
   * @param session The relevant socket for the player attempting to leave.
   */
  private void handleLeaveLobby(Session session) {
    // don't do anything if the user simply refreshes the landing page
    if (!sessionLobbyMap.containsKey(session)) {
      return;
    }
    // search through lobbies and remove player with matching socket
    Lobby toLeave = sessionLobbyMap.get(session);
    MaoPlayer maoPlayerLeave = toLeave.getPlayerWithSocket(session);
    toLeave.removePlayer(maoPlayerLeave);
    sessionLobbyMap.remove(session);

    // kill the lobby if it's empty or the host leaves
    if (toLeave.getPlayers().isEmpty() || maoPlayerLeave.isHost()) {
      lobbies.remove(toLeave);
      sessionLobbyMap.values().removeIf(toLeave::equals);
    }
  }

  /**
   * Handler for when a player is kicked from the lobby.
   * @param playerId The id of the relevant player to kick.
   * @param session The relevant session for the game.
   */
  private void handleKickPlayer(int playerId, Session session) {
    // NOTE: sessions of kicked players remain in the sessionLobbyMap and sessionPlayerMap
    // host can't kick themself, so the lobby won't become empty
    Lobby toLeave = sessionLobbyMap.get(session);
    Session kicked = toLeave.getSocketWithPlayerId(playerId);
    MaoPlayer maoPlayerKicked = toLeave.getPlayerWithSocket(kicked);
    toLeave.kickPlayer(maoPlayerKicked);
    sessionLobbyMap.remove(kicked);
  }

  /**
   * Handler for when the lobby settings are updated.
   * @param updatedSettings The new settings for the lobby.
   * @param session The relevant session for the game.
   */
  private void handleUpdateSettings(Settings updatedSettings, Session session) {
    Lobby updatedLobby = sessionLobbyMap.get(session);
    updatedLobby.setSettings(updatedSettings);
  }

  /**
   * Handler for when a player plays a card and sends a message along with that card.
   * @param playerId The ID of the player making this move.
   * @param card The card played by the player.
   * @param message A String representing the verbal message sent by the player.
   * @param session The relevant session for the game.
   */
  private void handlePlayCardWithMessage(int playerId, Card card, String message, Session session) {
    Lobby updatedLobby = sessionLobbyMap.get(session);
    Board updatedBoard = updatedLobby.getBoard();
    try {
      boolean gameOver = updatedBoard.playCardWithMessage(playerId, card, message);
      if (gameOver) {
        lobbies.remove(updatedLobby);
        sessionLobbyMap.values().removeIf(updatedLobby::equals);
      }
    } catch (CardNotFoundException ce) {
      // should not get here if implemented correctly
      System.out.println(ce.getMessage());
    }
  }

  /**
   * Handler for when the host applies a manual penalty to a player.
   * @param playerId The player upon whom the manual penalty is applied.
   * @param hint A String representing the hint for the rule for which the penalty is
   *             being applied.
   * @param session The relevant session for the game.
   */
  private void handleManualPenalty(int playerId, String hint, Session session) {
    Board updatedBoard = sessionLobbyMap.get(session).getBoard();
    try {
      updatedBoard.applyPenalty(playerId, hint);
    } catch (NoSuchElementException ne) {
      // should not get here if implemented correctly
      System.out.println(ne.getMessage());
    }
  }

  /**
   * Handler for messages sent to the chat, responded to via penalties or points of order.
   * @param playerId The ID of the player sending the message.
   * @param chatMessage The message sent by the player in the chat.
   * @param session The relevant session for the game.
   */
  private void handleChatMessage(int playerId, String chatMessage, Session session) {
    Board updatedBoard = sessionLobbyMap.get(session).getBoard();
    updatedBoard.chatMessage(playerId, chatMessage);
  }

  /**
   * Handler for when the host starts the game.
   * @param session The relevant session for the game.
   */
  private void handleStartGame(Session session) {
    Lobby updatedLobby = sessionLobbyMap.get(session);
    updatedLobby.startGame();
  }

  /**
   * Handler for when a player simply draws a card from the deck.
   * @param playerId The ID of the player drawing the card.
   * @param session The relevant session for the game.
   */
  private void handleDrawCard(int playerId, Session session) {
    Board updatedBoard = sessionLobbyMap.get(session).getBoard();
    updatedBoard.drawAndAdvanceTurn(playerId);
  }
}
