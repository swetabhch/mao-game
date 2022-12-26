package edu.brown.cs.student.gameplay;

import edu.brown.cs.student.cards.Card;
import edu.brown.cs.student.cards.Rank;
import edu.brown.cs.student.cards.Suit;
import edu.brown.cs.student.maoExceptions.CardNotFoundException;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;
import java.util.Random;
import java.util.HashMap;
import java.util.Iterator;

import org.json.JSONObject;

public class AiPlayer implements Player {

  public static final String SERVER_URL = "https://mao-ai.herokuapp.com";
  private static final int RANDOM_USERNAME_LENGTH = 8;

  private final int id;
  private final String username;
  private final Collection<Card> cards;
  private final int difficulty;
  private final String lobbyCode;
  public AiPlayer(String username, Collection<Card> cards, int difficulty, String lobbyCode) {
    // need to keep ids unique between MaoPlayer and AiPlayer
    this.id = MaoPlayer.getAndIncrementId();
    this.difficulty = difficulty;
    this.username = username;
    this.cards = cards;
    this.lobbyCode = lobbyCode;
  }

  public AiPlayer(String username, int difficulty, String lobbyCode) {
    this(username, new ArrayList<>(), difficulty, lobbyCode);
  }

  public AiPlayer(int difficulty, String lobbyCode) {
    this.id = MaoPlayer.getAndIncrementId();
    this.cards = new ArrayList<>();
    this.difficulty = difficulty;
    // generating random username
    byte[] array = new byte[RANDOM_USERNAME_LENGTH];
    new Random().nextBytes(array);
    this.username = new String(array, Charset.forName("UTF-8"));
    this.lobbyCode = lobbyCode;
  }

  @Override
  public int getId() {
    return id;
  }

  @Override
  public String getUsername() {
    return username;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    AiPlayer player = (AiPlayer) o;
    return id == player.id && Objects.equals(username, player.username);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, username);
  }

  @Override
  public Collection<Card> getCards() {
    return new ArrayList<>(this.cards);
  }

  public MaoPlayAction predictCard(Card topCard) {

    try {
      URL serverURL = new URL(SERVER_URL + "/predict");
      HttpURLConnection connPy = (HttpURLConnection) serverURL.openConnection();
      connPy.setRequestMethod("POST");
      connPy.setRequestProperty("content-type", "application/json");
      connPy.setRequestProperty("Accept", "application/json");
      connPy.setDoOutput(true);

      String jsonInputString = "{\"ai_id\": " + "\"" + this.id
              + "\", \"top_suit\": \"" + topCard.getSuit().name()
              + "\", \"top_rank\": \"" + topCard.getRank().name()
              + "\", \"lobby\": \"" + this.lobbyCode + "\"}";

      try (OutputStream os = connPy.getOutputStream()) {
        byte[] input = jsonInputString.getBytes("utf-8");
        os.write(input, 0, input.length);
      }
      StringBuilder response = new StringBuilder();
      try (BufferedReader br = new BufferedReader(
              new InputStreamReader(connPy.getInputStream(), "utf-8"))) {
        String responseLine = null;
        while ((responseLine = br.readLine()) != null) {
          response.append(responseLine.trim());
        }

        System.out.println(response.toString());
      }
      HashMap<String, String> map = new HashMap<String, String>();
      JSONObject jObject = new JSONObject(response.toString());
      Iterator<?> keys = jObject.keys();

      while (keys.hasNext()) {
        String key = (String) keys.next();
        String value = jObject.getString(key);
        map.put(key, value);

      }
      System.out.println(map.get("rank") + " " + map.get("suit") + " " + map.get("phrase"));
      return new MaoPlayAction(new Card(Enum.valueOf(Rank.class, map.get("rank")),
              Enum.valueOf(Suit.class, map.get("suit"))), map.get("phrase"));

    } catch (Exception e) {
      e.printStackTrace();
      return null;
      // TODO: do better error handling here...
    }

  }

  @Override
  public void addCard(Card card) {
    this.cards.add(card);
    try {
      URL serverURL = new URL(SERVER_URL + "/add");
      HttpURLConnection connPy = (HttpURLConnection) serverURL.openConnection();
      connPy.setRequestMethod("POST");
      connPy.setRequestProperty("content-type", "application/json");
      connPy.setRequestProperty("Accept", "application/json");
      connPy.setDoOutput(true);

      String jsonInputString = "{\"suit\": " + "\"" + card.getSuit().name() + "\", \"rank\": "
              + "\"" + card.getRank().name() + "\", \"ai_id\": \"" + this.id + "\"}";

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
      // TODO: do better error handling here...
    }
  }

  @Override
  public void removeCard(Card card) throws CardNotFoundException {
    this.cards.remove(card);
    try {
      URL serverURL = new URL(SERVER_URL + "/remove");
      HttpURLConnection connPy = (HttpURLConnection) serverURL.openConnection();
      connPy.setRequestMethod("POST");
      connPy.setRequestProperty("content-type", "application/json");
      connPy.setRequestProperty("Accept", "application/json");
      connPy.setDoOutput(true);

      String jsonInputString = "{\"suit\": " + "\"" + card.getSuit().name() + "\", \"rank\": "
              + "\"" + card.getRank().name() + "\", \"ai_id\": \"" + this.id + "\"}";

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
      // TODO: do better error handling here...
    }
  }

  public int getDifficulty() {
    return difficulty;
  }
}
