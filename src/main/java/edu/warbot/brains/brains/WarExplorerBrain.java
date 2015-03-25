package edu.warbot.brains.brains;

import edu.warbot.agents.actions.constants.MovableActions;
import edu.warbot.brains.WarBrain;
import edu.warbot.brains.capacities.Movable;

public abstract class WarExplorerBrain extends WarBrain implements MovableActions, Movable {

    @Override
    public String action() {
        return ACTION_IDLE;
    }

}
