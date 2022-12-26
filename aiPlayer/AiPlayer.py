import sklearn.tree as sk
import sklearn.preprocessing as pre
import numpy as np
from Card import Card
import random

class AIPlayer:
    """AI Player which learns rules to a game of Mao as the game progesses. """
    def __init__(self, h, diff):
        """Initialize AI Player with a hand of cards"""
        self.hand = h
        self.difficulty = diff
        self.encoder = pre.OneHotEncoder(handle_unknown='ignore')
        self.dtclf = sk.DecisionTreeClassifier() 
        self.mlb = pre.MultiLabelBinarizer()

        self.suit_dict = {'CLUB': 1, 'HEART': 2, 'SPADE': 3, 'DIAMOND': 4}
        self.rank_dict = {"ACE": 1, "TWO": 2, "THREE": 3, "FOUR": 4, "FIVE": 5, "SIX": 6, "SEVEN": 7, "EIGHT": 8, "NINE": 9, "TEN": 10, "JACK": 11, "QUEEN": 12, "KING": 13}

    def __init__(self, diff):
        """Initialize AI Player with an empty hand of cards"""
        self.hand = []
        self.difficulty = diff
        self.encoder = pre.OneHotEncoder(handle_unknown='ignore')
        self.dtclf = sk.DecisionTreeClassifier() 
        self.mlb = pre.MultiLabelBinarizer()

        self.suit_dict = {'CLUB': 1, 'HEART': 2, 'SPADE': 3, 'DIAMOND': 4}
        self.rank_dict = {"ACE": 1, "TWO": 2, "THREE": 3, "FOUR": 4, "FIVE": 5, "SIX": 6, "SEVEN": 7, "EIGHT": 8, "NINE": 9, "TEN": 10, "JACK": 11, "QUEEN": 12, "KING": 13}

    def preprocess(self, pre_feat, pre_lab):
        """Preprocesses data and fits AI to previous non-penalized cards."""
        pref = pre_feat[:]
        prefeat = []
        for feat in range(len(pref)):
            prefeat.append([])
            prefeat[feat].append(pref[feat][0].suit)
            prefeat[feat].append(pref[feat][0].rank)
            prefeat[feat].append(pref[feat][1])
            prefeat[feat][0] = self.suit_dict[prefeat[feat][0]]
            prefeat[feat][0+1] = self.rank_dict[prefeat[feat][0+1]]
        prev_features = self.encoder.fit_transform(np.array(prefeat)).toarray()
        prev_labels = self.mlb.fit_transform(pre_lab)

        self.dtclf.fit(prev_features, prev_labels)

    def predictWords(self, card_played, prev_features, prev_labels):
        """Predict words to be typed out alongside card played."""
        self.preprocess(prev_features, prev_labels)
        self.suit_dict.setdefault(card_played[0], len(self.suit_dict))
        self.rank_dict.setdefault(card_played[1], len(self.rank_dict))

        card_played[0] = self.suit_dict[card_played[0]]
        card_played[1] = self.rank_dict[card_played[1]]
        card_played = np.array(card_played).reshape(1, -1)
        card_played = self.encoder.transform(card_played).toarray()
        Y = self.dtclf.predict(card_played)

        for y in Y:
            if np.count_nonzero(y) > 0:
                break
        else:
            return []
        if Y.ndim == 1:
            Y = np.array([Y])
        [words] = self.mlb.inverse_transform(np.array(Y))
        return list(words)

    def pick_card(self, top_card):
        """Selects card to play based on UNO-like rules"""
        possible_cards = []
        for card in self.hand:
            if card.suit == top_card.suit or card.rank == top_card.rank:
                possible_cards.append(card)
        if possible_cards == []:
            if len(self.hand) != 0:
                return random.choice(self.hand)
            else:
                raise Exception("Hand is already empty, game should be over.")
        else:
            return random.choice(possible_cards)

    def play(self, top_card, prev_features_all, prev_labels_all):
        """Pick a card to play from hand and determine words to say alongside it"""
        prev_features = []
        prev_labels = []
        # Creates model based on a set number of past rules
        if self.difficulty == 1:
            prev_features = prev_features_all[max(len(prev_features_all)-5, 0):]
            prev_labels = prev_labels_all[max(len(prev_labels_all)-5, 0):]
        if self.difficulty == 2:
            prev_features = prev_features_all[max(len(prev_features_all)-10, 0):]
            prev_labels = prev_labels_all[max(len(prev_labels_all)-10, 0):]
        if self.difficulty == 3:
            prev_features = prev_features_all
            prev_labels = prev_labels_all
        print(prev_features)
        print(prev_features_all)
        print(prev_labels)
        print(prev_labels_all)
        words = []
        selected_card = self.pick_card(top_card)
        for y in prev_labels:
            if y != []:
                break
        else:
            return selected_card, []
        if prev_features != []:
            words = self.predictWords([selected_card.suit, selected_card.rank, len(self.hand)], prev_features, prev_labels)
        #self.removeCard(card)
        return selected_card, words

    def addCard(self, card):
        """Adds card to hand"""
        self.hand.append(card)

    def removeCard(self, card):
        """Removes card from hand"""
        self.hand = [s for s in self.hand if (s.suit != card.suit or s.rank != card.rank)]

    def setHand(self, h):
        """Sets hand to be a pre-determined list of cards"""
        self.hand = h
