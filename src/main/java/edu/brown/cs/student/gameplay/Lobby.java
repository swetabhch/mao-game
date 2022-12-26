package edu.brown.cs.student.gameplay;

import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import edu.brown.cs.student.maoExceptions.PlayerNotFoundException;
import edu.brown.cs.student.server.ServerMessage;
import org.eclipse.jetty.websocket.api.Session;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Collection;

public class Lobby {
  private static final Gson GSON = new Gson();
  private static final int LOBBY_CODE_LENGTH = 6;

  private final String lobbyCode;
  private final List<MaoPlayer> maoPlayers;
  private final Map<Session, MaoPlayer> sessionPlayerMap;
  private Settings settings;
  private boolean gameStarted;
  private Board board;

  public Lobby() {
    this.lobbyCode = getAlphanumericString();
    this.maoPlayers = new ArrayList<>();
    this.sessionPlayerMap = new HashMap<>();
    this.settings = new Settings();
    this.gameStarted = false;
    this.board = null;
  }

  public void addPlayer(MaoPlayer maoPlayer) {
    if (gameStarted) {
      return;
    }

    String playerJoinJson = GSON.toJson(ImmutableMap.of(
        "type", ServerMessage.PLAYER_JOIN_LOBBY.ordinal(),
        "payload", ImmutableMap.of(
            "player", maoPlayer
        )
    ));
    sendToAllPlayers(playerJoinJson, false);

    maoPlayers.add(maoPlayer);
    sessionPlayerMap.put(maoPlayer.getSocket(), maoPlayer);

    try {
      maoPlayer.getSocket().getRemote().sendString(getStateAsJson(maoPlayer));
    } catch (IOException e) {
      System.out.println("Couldn't send a message to player " + maoPlayer.getUsername() + ".");
    }
  }

  public void removePlayer(MaoPlayer maoPlayer) {
    // remove player before sending out message, to avoid socket errors
    maoPlayers.remove(maoPlayer);
    sessionPlayerMap.remove(maoPlayer.getSocket());

    if (!this.gameStarted) {
      String playerLeaveJson = GSON.toJson(ImmutableMap.of(
          "type", ServerMessage.PLAYER_LEAVE_LOBBY.ordinal(),
          "payload", ImmutableMap.of(
              "player", maoPlayer
          )
      ));
      sendToAllPlayers(playerLeaveJson, false);
    } else {
      try {
        board.removePlayer(maoPlayer);
      } catch (PlayerNotFoundException e) {
        System.out.println(e.getMessage());
      }
    }
  }

  public void kickPlayer(MaoPlayer maoPlayer) {
    // method should never be invoked if the game has started
    if (this.gameStarted) {
      return;
    }

    String playerLeaveJson = GSON.toJson(ImmutableMap.of(
        "type", ServerMessage.PLAYER_LEAVE_LOBBY.ordinal(),
        "payload", ImmutableMap.of(
            "player", maoPlayer
        )
    ));
    sendToAllPlayers(playerLeaveJson, false);

    // only remove player after sending out message
    maoPlayers.remove(maoPlayer);
    sessionPlayerMap.remove(maoPlayer.getSocket());
  }

  public String getLobbyCode() {
    return lobbyCode;
  }

  public Collection<MaoPlayer> getPlayers() {
    return maoPlayers;
  }

  public MaoPlayer getPlayerWithSocket(Session session) {
    return sessionPlayerMap.get(session);
  }

  public Session getSocketWithPlayerId(int playerId) {
    for (Map.Entry<Session, MaoPlayer> entry: sessionPlayerMap.entrySet()) {
      if (entry.getValue().getId() == playerId) {
        return entry.getKey();
      }
    }
    return null;
  }

  public boolean isFull() {
    return maoPlayers.size() >= settings.getNumPlayers();
  }

  public boolean isGameStarted() {
    return gameStarted;
  }

  public Board getBoard() {
    return board;
  }

  public Settings getSettings() {
    return settings;
  }

  public void setSettings(Settings settings) {
    this.settings = settings;
    String updateSettingsJson = GSON.toJson(ImmutableMap.of(
        "type", ServerMessage.UPDATE_SETTINGS.ordinal(),
        "payload", ImmutableMap.of(
            "settings", settings
        )
    ));
    sendToAllPlayers(updateSettingsJson, true);
  }

  public void startGame() {
    this.board = new Board(this.maoPlayers, this.settings, this.lobbyCode);
    this.board.startGame();
    this.gameStarted = true;
  }

  public String getStateAsJson(MaoPlayer maoPlayer) {
    return GSON.toJson(ImmutableMap.of(
        "type", ServerMessage.LOBBY_STATE.ordinal(),
        "payload", ImmutableMap.of(
            "playerId", maoPlayer.getId(),
            "lobbyCode", lobbyCode,
            "players", maoPlayers,
            "settings", settings
        )
    ));
  }

  private String getAlphanumericString() {
    String charBank = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
    StringBuilder randString = new StringBuilder();
    Random r = new Random();
    for (int i = 0; i < LOBBY_CODE_LENGTH; i++) {
      int index = (int) (r.nextFloat() * charBank.length());
      randString.append(charBank.charAt(index));
    }
    return randString.toString();
  }

  private void sendToAllPlayers(String message, boolean excludeHost) {
    for (MaoPlayer p : maoPlayers) {
      if (excludeHost && p.isHost()) {
        continue;
      }
      try {
        p.getSocket().getRemote().sendString(message);
      } catch (IOException e) {
        System.out.println("Couldn't send a message to player " + p.getUsername() + ".");
      }
    }
  }
}
