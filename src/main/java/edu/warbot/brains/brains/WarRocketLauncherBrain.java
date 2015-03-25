package edu.warbot.brains.brains;

import edu.warbot.agents.actions.constants.AgressiveActions;
import edu.warbot.agents.actions.constants.MovableActions;
import edu.warbot.brains.WarBrain;
import edu.warbot.brains.capacities.Agressive;
import edu.warbot.brains.capacities.Movable;

public abstract class WarRocketLauncherBrain extends WarBrain implements AgressiveActions, Agressive, MovableActions, Movable {

    @Override
    public String action() {
        return ACTION_IDLE;
    }

}
