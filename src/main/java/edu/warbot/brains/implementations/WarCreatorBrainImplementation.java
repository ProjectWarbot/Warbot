package edu.warbot.brains.implementations;

import edu.warbot.agents.actions.constants.CreatorActions;
import edu.warbot.agents.enums.WarAgentType;
import edu.warbot.brains.capacities.Creator;

public class WarCreatorBrainImplementation extends WarBrainImplementation implements CreatorActions, Creator {

    @Override
    public WarAgentType getNextAgentToCreate() {
        return ((Creator) this.agent).getNextAgentToCreate();
    }

    @Override
    public void setNextAgentToCreate(WarAgentType nextAgentToCreate) {
        ((Creator) this.agent).setNextAgentToCreate(nextAgentToCreate);
    }

    @Override
    public boolean isAbleToCreate(WarAgentType agent) {
        return ((Creator) this.agent).isAbleToCreate(agent);
    }
}
