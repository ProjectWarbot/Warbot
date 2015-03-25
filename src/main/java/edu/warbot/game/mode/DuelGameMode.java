package edu.warbot.game.mode;

import edu.warbot.game.WarGame;
import edu.warbot.game.mode.endCondition.DuelEndCondition;

public class DuelGameMode extends AbstractGameMode {

    public DuelGameMode(WarGame game, Object[] args) {
        super(new DuelEndCondition(game));
    }

}
