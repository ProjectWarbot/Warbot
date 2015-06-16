package edu.warbot.brains.brains;

import edu.warbot.agents.actions.BuilderActionsMethods;
import edu.warbot.agents.actions.CreatorActionsMethods;
import edu.warbot.agents.actions.MovableActionsMethods;
import edu.warbot.agents.actions.constants.BuilderActions;
import edu.warbot.agents.actions.constants.CreatorActions;
import edu.warbot.agents.actions.constants.MovableActions;
import edu.warbot.agents.enums.WarAgentType;
import edu.warbot.brains.WarBrain;
import edu.warbot.brains.capacities.Builder;
import edu.warbot.brains.capacities.Creator;
import edu.warbot.brains.capacities.Movable;

public abstract class WarEngineerBrain extends WarBrain implements CreatorActionsMethods, CreatorActions, Creator,
        BuilderActionsMethods, BuilderActions, Builder,
        MovableActionsMethods, MovableActions, Movable {

    @Override
    public String action() {
        return ACTION_IDLE;
    }

    @Override
    public String create() {
        return ACTION_CREATE;
    }

    public String create(WarAgentType warAgentType) {
        setNextAgentToCreate(warAgentType);
        return ACTION_CREATE;
    }

    @Override
    public String move() {
        return ACTION_MOVE;
    }

    @Override
    public String build() {
        return ACTION_BUILD;
    }

    @Override
    public String repair() {
        return ACTION_REPAIR;
    }

}
