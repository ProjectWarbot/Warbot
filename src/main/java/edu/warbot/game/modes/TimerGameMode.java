package edu.warbot.game.modes;

import edu.warbot.game.WarGame;
import edu.warbot.game.modes.endCondition.TimerEndCondition;

public class TimerGameMode extends AbstractGameMode {

    public TimerGameMode(WarGame game, Object[] arg) {
        super(new TimerEndCondition(game, (Long) arg[0]));
    }

}