package edu.warbot.game.modes;

import edu.warbot.game.WarGame;
import edu.warbot.game.modes.endCondition.DuelEndCondition;

public class DuelGameMode extends AbstractGameMode {

    public DuelGameMode(WarGame game, Object[] args) {
        super(new DuelEndCondition(game));
    }

}
