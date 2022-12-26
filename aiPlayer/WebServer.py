import signal
import sys
from http.server import BaseHTTPRequestHandler,HTTPServer
from sys import exit
import cgi
import json
import time
import os
sys.path.append(".")
from AiPlayer import AIPlayer
from Card import Card

acceptedCards = []
acceptedPhrases = []
aiplayers = {}

# handle ctrl-c
def signal_handler(signal, frame):
	print('You pressed Ctrl+C! Exiting...')
	sys.exit(0)

def aiPredict(prev_feat, prev_lab, num, top_card):
	"""Calls on specific AI Player to make a prediction"""
	cardPlayed, words = aiplayers[num].play(top_card, prev_feat, prev_lab)
	load = {"card": cardPlayed, "words": words}
	return load

def turn(card, phrase):
	"""Handles adding cards and phrases to the list of accepted cards/phrases"""
	acceptedCards.append(card)
	acceptedPhrases.append(phrase)

def createPlayer(ai_id, diff):
	"""Creates an AI Player and adds it to the list of AI Players"""
	aiplayers[ai_id] = AIPlayer(diff)

# The Webserver
class myHandler(BaseHTTPRequestHandler):
	def _set_headers(self):
		"""Sets headers for post requests"""
		self.send_response(200)
		self.send_header('Content-type', 'application/json')
		self.end_headers()

	# Handler for post requests
	def do_POST(self):
		if self.path.endswith("/human"):
			# Sent a correct turn from a human
			print(self.path)
			ctype, pdict = cgi.parse_header(self.headers.get('content-type'))

			if ctype != 'application/json':
				self.send_response(400)
				self.end_headers()
				print("not app json")
				return
			length = int(self.headers.get('content-length'))
			print(self.rfile)
			message = json.loads(self.rfile.read(length))
			print(message)

			acceptedCards.append([Card(message["suit"], message["rank"]), int(message["num_cards"])])
			acceptedPhrases.append(message["phrase"].split(";"))
			print(message["phrase"].split(";"))
			message['received'] = 'ok'
			
			# send the message back
			self._set_headers()
			self.wfile.write(json.dumps(message).encode())

		if self.path.endswith("/add"):
			#Adds a given card to a specific AI Player's hand
			print(self.path)
			ctype, pdict = cgi.parse_header(self.headers.get('content-type'))

			if ctype != 'application/json':
				self.send_response(400)
				self.end_headers()
				print("not app json")
				return
			length = int(self.headers.get('content-length'))
			message = json.loads(self.rfile.read(length))
			aiplayers[message["ai_id"]].addCard(Card(message["suit"], message["rank"]))
			message['received'] = 'ok'
			
			# send the message back
			self._set_headers()
			self.wfile.write(json.dumps(message).encode())

		if self.path.endswith("/remove"):
			#Removes a given card to a specific AI Player's hand
			print(self.path)
			ctype, pdict = cgi.parse_header(self.headers.get('content-type'))

			if ctype != 'application/json':
				self.send_response(400)
				self.end_headers()
				print("not app json")
				return
			length = int(self.headers.get('content-length'))
			message = json.loads(self.rfile.read(length))
			print(message)

			aiplayers[message["ai_id"]].removeCard(Card(message["suit"], message["rank"]))
			message['received'] = 'ok'
			
			# send the message back
			self._set_headers()
			self.wfile.write(json.dumps(message).encode())
		if self.path.endswith("/start"):
			# Initializes the creation of an AI Player
			print(self.path)
			ctype, pdict = cgi.parse_header(self.headers.get('content-type'))

			if ctype != 'application/json':
				self.send_response(400)
				self.end_headers()
				print("not app json")
				return
			length = int(self.headers.get('content-length'))
			print(self.rfile)
			message = json.loads(self.rfile.read(length))
			print(message)
			createPlayer(message['numAI'], int(message['aiDifficulty']))
			message['received'] = 'ok'
			
			# send the message back
			self._set_headers()
			self.wfile.write(json.dumps(message).encode())
		if self.path.endswith("/predict"):
			# Tells one of the AI Players to send its predicted card/hand
			time.sleep(2)
			print(self.path)
			ctype, pdict = cgi.parse_header(self.headers.get('content-type'))

			if ctype != 'application/json':
				self.send_response(400)
				self.end_headers()
				print("not app json")
				return
			length = int(self.headers.get('content-length'))
			message = json.loads(self.rfile.read(length))
			
			resp = aiPredict(acceptedCards, acceptedPhrases, message["ai_id"], Card(message["top_suit"], message["top_rank"]))
			card = resp["card"]
			phrase = resp["words"]
			message['received'] = 'ok'
			message['suit'] = card.suit
			print(message)
			message['rank'] = card.rank

			# Combine multiple words predicted into one phrase delimited by semicolons if multiple words are predicted.
			final_msg = ""
			for p in phrase:
				final_msg += p
				final_msg += ";"
			print(final_msg)
			if len(phrase) > 0:
				final_msg = final_msg[:(len(final_msg) - 1)]
			message['phrase'] = final_msg

			# send the message back
			self._set_headers()
			self.wfile.write(json.dumps(message).encode())


def main():
	#print(os.environ)
	#PORT = 8000
	PORT = os.environ['PORT']
	print(PORT)
	signal.signal(signal.SIGINT, signal_handler)
	server = HTTPServer(('https://tranquil-escarpment-52157.herokuapp.com', PORT), myHandler)
	print('Server running on port %s' % PORT)
	server.serve_forever()

if __name__ == '__main__':
	main()