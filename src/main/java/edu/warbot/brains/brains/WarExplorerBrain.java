package edu.warbot.brains.brains;

import edu.warbot.agents.actions.MovableActionsMethods;
import edu.warbot.agents.actions.constants.MovableActions;
import edu.warbot.brains.WarBrain;
import edu.warbot.brains.capacities.Movable;

public abstract class WarExplorerBrain extends WarBrain implements MovableActionsMethods, MovableActions, Movable {

    @Override
    public String action() {
        return ACTION_IDLE;
    }

    @Override
    public final String move() {
        return ACTION_MOVE;
    }

}
