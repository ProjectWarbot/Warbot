package edu.warbot.game.mode;

import edu.warbot.game.WarGame;
import edu.warbot.game.mode.endCondition.NumberLostAgentEndCondition;

public class NumberLostAgentGameMode extends AbstractGameMode {

    public NumberLostAgentGameMode(WarGame game, Object[] args) {
        super(new NumberLostAgentEndCondition(game, (Long) args[0]));
    }

}