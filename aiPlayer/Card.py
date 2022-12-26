class Card:
    """AI Player which learns rules to a game of Mao as the game progesses. """
    def __init__(self, suit, rank):
        self.suit = suit
        self.rank =  rank

    def get_suit(self):
        return self.suit

    def get_rank(self):
        return self.rank

    def convert_string_to_card(c):
        suit_dict = {'C': 'CLUB', 'H': 'HEART', 'S': 'SPADE', 'D': 'DIAMOND'}
        rank_dict = {"A": "ACE", "2": "2", "3": "3", "4": "4", "5": "5", "6": "6", "7": "7", "8": "8", "9": "9", "10": "10", "JACK": "11", "QUEEN": "12", "KING": "13"}
        return Card(suit_dict[c[0:1]], rank_dict(c[1:2]))