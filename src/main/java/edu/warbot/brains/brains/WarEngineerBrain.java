package edu.warbot.brains.brains;

import edu.warbot.agents.actions.constants.BuilderActions;
import edu.warbot.agents.actions.constants.CreatorActions;
import edu.warbot.agents.actions.constants.MovableActions;
import edu.warbot.brains.WarBrain;
import edu.warbot.brains.capacities.Builder;
import edu.warbot.brains.capacities.Creator;
import edu.warbot.brains.capacities.Movable;

public abstract class WarEngineerBrain extends WarBrain implements CreatorActions, Creator, BuilderActions, Builder, MovableActions, Movable {

    @Override
    public String action() {
        return ACTION_IDLE;
    }

}
