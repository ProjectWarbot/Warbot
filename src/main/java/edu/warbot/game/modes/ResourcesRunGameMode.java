package edu.warbot.game.modes;

import edu.warbot.game.WarGame;
import edu.warbot.game.modes.endCondition.ResourcesRunEndCondition;

public class ResourcesRunGameMode extends AbstractGameMode {

    public ResourcesRunGameMode(WarGame game, Object[] args) {
        super(new ResourcesRunEndCondition(game, (Integer) args[0]));
    }

}
