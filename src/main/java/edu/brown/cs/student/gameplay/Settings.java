package edu.brown.cs.student.gameplay;

import edu.brown.cs.student.maoExceptions.InconsistentSettingsException;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * The Settings class represents a collection of properties of a Mao game. Settings objects can
 * be used as bundles of information for classes like Board that require these properties.
 */
public class Settings {

  private int numPlayers;
  private int numDecks;
  private boolean usingAIPlayers;
  private int aiDifficulty;
  private boolean usingCustomRules;
  private List<MaoRule> customRules;
  private boolean usingTurnHighlights;

  public Settings(int numPlayers, int numDecks, boolean usingAIPlayers,
                  int aiDifficulty, boolean usingCustomRules, List<MaoRule> customRules,
                  boolean usingTurnHighlights) {
    this.numPlayers = numPlayers;
    this.numDecks = numDecks;
    this.usingAIPlayers = usingAIPlayers;
    this.aiDifficulty = aiDifficulty;
    this.usingCustomRules = usingCustomRules;
    this.customRules = customRules;
    this.usingTurnHighlights = usingTurnHighlights;
  }

  // default settings
  public Settings() {
    this(4, 1, false, 1, false, new ArrayList<>(), false);
  }

  public int getNumPlayers() {
    return numPlayers;
  }

  public void setNumPlayers(int numPlayers) {
    this.numPlayers = numPlayers;
  }

  public int getNumDecks() {
    return numDecks;
  }

  public void setNumDecks(int numDecks) {
    this.numDecks = numDecks;
  }

  public boolean isUsingAIPlayers() {
    return usingAIPlayers;
  }

  public void setUsingAIPlayers(boolean usingAIPlayers) {
    this.usingAIPlayers = usingAIPlayers;
  }

  public int getAIDifficulty() {
    return aiDifficulty;
  }

  public void setAIDifficulty(int aiLevel) throws InconsistentSettingsException {
    if (aiLevel > 3 || aiLevel < 1) {
      throw new InconsistentSettingsException(
          "ERROR: AI difficulty must be between 1 and 3 inclusive.");
    }
    this.aiDifficulty = aiLevel;
  }

  public boolean isUsingCustomRules() {
    return usingCustomRules;
  }

  public void setUsingCustomRules(boolean usingCustomRules) {
    this.usingCustomRules = usingCustomRules;
  }

  public List<MaoRule> getCustomRules() {
    return new ArrayList<>(customRules);
  }

  public void setCustomRules(List<MaoRule> customRules) {
    this.customRules = customRules;
  }

  public boolean isUsingTurnHighlights() {
    return usingTurnHighlights;
  }

  public void setUsingTurnHighlights(boolean usingTurnHighlights) {
    this.usingTurnHighlights = usingTurnHighlights;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Settings settings = (Settings) o;
    return numPlayers == settings.numPlayers
        && numDecks == settings.numDecks
        && usingAIPlayers == settings.usingAIPlayers
        && aiDifficulty == settings.aiDifficulty
        && usingCustomRules == settings.usingCustomRules
        && usingTurnHighlights == settings.usingTurnHighlights
        && customRules.equals(settings.customRules);
  }

  @Override
  public int hashCode() {
    return Objects.hash(numPlayers, numDecks, usingAIPlayers, aiDifficulty,
        usingCustomRules, customRules, usingTurnHighlights);
  }
}
