package edu.brown.cs.student.server;

public enum ClientMessage {
  // payload: username
  CREATE_LOBBY,
  // payload: username, lobbyCode
  JOIN_LOBBY,
  // payload: empty
  LEAVE_LOBBY,
  // payload: playerId
  KICK_PLAYER,
  // payload: settings (complete settings object)
  UPDATE_SETTINGS,
  // payload: playerId, hint
  MANUAL_PENALTY,
  // payload: playerId, chatMessage
  CHAT_MESSAGE,
  // payload: playerId, card { rank, suit }, message
  PLAY_CARD_WITH_MESSAGE,
  // payload: empty
  START_GAME,
  // payload: playerId
  DRAW_CARD,
  // payload: empty
  PING
}
