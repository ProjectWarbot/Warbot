package edu.warbot.game.modes;

import edu.warbot.game.WarGame;
import edu.warbot.game.modes.endCondition.NumberLostAgentEndCondition;

public class NumberLostAgentGameMode extends AbstractGameMode {

    public NumberLostAgentGameMode(WarGame game, Object[] args) {
        super(new NumberLostAgentEndCondition(game, (Long) args[0]));
    }

}