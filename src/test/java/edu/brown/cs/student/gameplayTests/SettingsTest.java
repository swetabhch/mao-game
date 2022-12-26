package edu.brown.cs.student.gameplayTests;

import edu.brown.cs.student.gameplay.Settings;
import edu.brown.cs.student.maoExceptions.InconsistentSettingsException;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.*;

public class SettingsTest {
  
  @Test
  public void testNotNull() {
    Settings settingsDefault = new Settings();
    Settings settingsSpecific = new Settings(4, 1, false, 1, false, new ArrayList<>(), false);
    assertNotNull(settingsDefault);
    assertNotNull(settingsSpecific);
  }
  
  @Test
  public void testEquals() {
    Settings settingsDefault = new Settings();
    Settings settingsDefault2 = new Settings();
    Settings settingsDefaultExp = new Settings(4, 1, false, 1, false, new ArrayList<>(), false);
    Settings settingsExp = new Settings(6, 2, true, 2, false, new ArrayList<>(), false);
    Settings settingsExp2 = new Settings(6, 2, true, 2, false, new ArrayList<>(), false);
    assertEquals(settingsDefault, settingsDefault);
    assertEquals(settingsDefault, settingsDefault2);
    assertEquals(settingsDefault, settingsDefaultExp);
    assertEquals(settingsExp, settingsExp2);
    assertNotEquals(settingsDefaultExp, settingsExp2);
  }

  @Test
  public void testGetters() {
    Settings settingsDefault = new Settings();
    Settings settingsExp = new Settings(6, 2, true, 2, false, new ArrayList<>(), false);
    assertEquals(4, settingsDefault.getNumPlayers());
    assertEquals(2, settingsExp.getNumDecks());
    assertFalse(settingsDefault.isUsingAIPlayers());
    assertEquals(2, settingsExp.getAIDifficulty());
    assertFalse(settingsDefault.isUsingCustomRules());
    assertEquals(new ArrayList<>(), settingsExp.getCustomRules());
    assertFalse(settingsDefault.isUsingTurnHighlights());
  }

  @Test
  public void testSetters() throws InconsistentSettingsException {
    // original: 4, 1, false, 1, false, new ArrayList<>(), false
    Settings settingsDefault = new Settings();
    settingsDefault.setNumPlayers(6);
    settingsDefault.setNumDecks(2);
    settingsDefault.setUsingAIPlayers(true);
    settingsDefault.setAIDifficulty(2);
    settingsDefault.setUsingCustomRules(true);
    settingsDefault.setCustomRules(new ArrayList<>());
    settingsDefault.setUsingTurnHighlights(true);
    assertEquals(6, settingsDefault.getNumPlayers());
    assertEquals(2, settingsDefault.getNumDecks());
    assertTrue(settingsDefault.isUsingAIPlayers());
    assertEquals(2, settingsDefault.getAIDifficulty());
    assertTrue(settingsDefault.isUsingCustomRules());
    assertEquals(new ArrayList<>(), settingsDefault.getCustomRules());
    assertTrue(settingsDefault.isUsingTurnHighlights());
  }
}
