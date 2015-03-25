package edu.warbot.brains.brains;

import edu.warbot.agents.actions.constants.AgressiveActions;
import edu.warbot.brains.WarBrain;
import edu.warbot.brains.capacities.Agressive;

public abstract class WarTurretBrain extends WarBrain implements AgressiveActions, Agressive {

    @Override
    public String action() {
        return ACTION_IDLE;
    }

}
