package edu.brown.cs.student.server;

public enum ServerMessage {
  // sent from: ServerSocket | payload: empty
  INVALID_LOBBY_CODE,
  // sent from: ServerSocket | payload: empty
  LOBBY_LOCKED,
  // sent from: ServerSocket | payload: playerId, lobbyCode, players, settings
  LOBBY_STATE,
  // sent from: Lobby | payload: player
  PLAYER_JOIN_LOBBY,
  // sent from: Lobby | payload: player
  // same functionality for socket disconnect and clicking Leave Lobby
  PLAYER_LEAVE_LOBBY,
  // sent from: Lobby | payload: settings
  UPDATE_SETTINGS,
  // sent from: Board | payload: players, turnIndex, formedRules, startingCard
  START_GAME,
  // sent from: Board | payload: player, players, turnIndex
  PLAYER_LEAVE_BOARD,
  // sent from: Board | payload: username, card, message, players, turnIndex, hints
  CARD_PLAYED,
  // sent from: Board | payload: username, players, hint
  PENALTY_INCURRED,
  // sent from: Board | payload: empty
  START_POINT_OF_ORDER,
  // sent from: Board | payload: empty
  END_POINT_OF_ORDER,
  // sent from: Board | payload: username, message
  CHAT_MESSAGE,
  // sent from: Board | payload: username
  END_GAME,
  // sent from: Board | payload: empty
  CARD_DRAWN
}
