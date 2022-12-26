package edu.brown.cs.student.gameplayTests;

import edu.brown.cs.student.gameplay.MaoRuleType;
import org.junit.Test;

import static org.junit.Assert.*;

public class MaoRuleTypeTest {

  @Test
  public void testTypeName() {
    assertEquals("verbal penalty", MaoRuleType.VERBAL_PENALTY.typeName());
    assertEquals("silence", MaoRuleType.SILENCE.typeName());
    assertEquals("reverse gameplay", MaoRuleType.REVERSE_GAMEPLAY.typeName());
    assertEquals("skip next player", MaoRuleType.SKIP_NEXT_PLAYER.typeName());
  }

}
