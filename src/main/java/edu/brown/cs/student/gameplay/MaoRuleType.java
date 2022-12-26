package edu.brown.cs.student.gameplay;

import edu.brown.cs.student.rules.RuleType;

/**
 * Currently supported types of Mao rules:
 *   - PENALTY: standard rule, draw card if phrase not said
 *   - POINT_OF_ORDER: suspended gameplay, turn chat into discussion space
 *   - SILENCE: stay silent if a condition is satisfied, e.g. a particular card is drawn
 *   - START_SUSPENDED_STATE: add chains of things to say
 *      (eg. "have a very nice day" -> "have a very very nice day")
 *   - END_SUSPENDED_STATE: end the chain of things to say
 *   - REVERSE_GAMEPLAY: reverse the order of gameplay
 *   - SKIP_NEXT_PLAYER: skip over the next player while playing
 */
public enum MaoRuleType implements RuleType {
  VERBAL_PENALTY {
    @Override
    public String typeName() {
      return "verbal penalty";
    }
  },
  SILENCE {
    @Override
    public String typeName() {
      return "silence";
    }
  },
  REVERSE_GAMEPLAY {
    @Override
    public String typeName() {
      return "reverse gameplay";
    }
  },
  SKIP_NEXT_PLAYER {
    @Override
    public String typeName() {
      return "skip next player";
    }
  }
}
