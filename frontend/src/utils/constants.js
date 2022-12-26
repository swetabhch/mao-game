// all these constants are kept consistent with the backend

export const SERVER_MESSAGE = {
    INVALID_LOBBY_CODE: 0,
    LOBBY_LOCKED: 1,
    LOBBY_STATE: 2,
    PLAYER_JOIN_LOBBY: 3,
    PLAYER_LEAVE_LOBBY: 4,
    UPDATE_SETTINGS: 5,
    START_GAME: 6,
    PLAYER_LEAVE_BOARD: 7,
    CARD_PLAYED: 8,
    PENALTY_INCURRED: 9,
    START_POINT_OF_ORDER: 10,
    END_POINT_OF_ORDER: 11,
    CHAT_MESSAGE: 12,
    END_GAME: 13,
    CARD_DRAWN: 14
};

export const CLIENT_MESSAGE = {
    CREATE_LOBBY: 0,
    JOIN_LOBBY: 1,
    LEAVE_LOBBY: 2,
    KICK_PLAYER: 3,
    UPDATE_SETTINGS: 4,
    MANUAL_PENALTY: 5,
    CHAT_MESSAGE: 6,
    PLAY_CARD_WITH_MESSAGE: 7,
    START_GAME: 8,
    DRAW_CARD: 9,
    PING: 10,
};

export const RANKS = {
    'ACE': 'A',
    'TWO': '2',
    'THREE': '3',
    'FOUR': '4',
    'FIVE': '5',
    'SIX': '6',
    'SEVEN': '7',
    'EIGHT': '8',
    'NINE': '9',
    'TEN': '10',
    'JACK': 'J',
    'QUEEN': 'Q',
    'KING': 'K',
}

export const SUITS = {
    'CLUB': 'C',
    'DIAMOND': 'D',
    'HEART': 'H',
    'SPADE': 'S',
}