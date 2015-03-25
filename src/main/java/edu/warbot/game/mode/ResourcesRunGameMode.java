package edu.warbot.game.mode;

import edu.warbot.game.WarGame;
import edu.warbot.game.mode.endCondition.ResourcesRunEndCondition;

public class ResourcesRunGameMode extends AbstractGameMode {

    public ResourcesRunGameMode(WarGame game, Object[] args) {
        super(new ResourcesRunEndCondition(game, (Integer) args[0]));
    }

}
