package edu.warbot.brains.capacities;

import edu.warbot.agents.enums.WarAgentType;

public interface Creator {

	public void setNextAgentToCreate(WarAgentType nextAgentToCreate);
	public WarAgentType getNextAgentToCreate();
	public boolean isAbleToCreate(WarAgentType agent);
	
}
