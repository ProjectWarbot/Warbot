package edu.warbot.brains.capacities;

import edu.warbot.agents.enums.WarAgentType;

public interface Creator {

    public WarAgentType getNextAgentToCreate();

    public void setNextAgentToCreate(WarAgentType nextAgentToCreate);

    public boolean isAbleToCreate(WarAgentType agent);

}
