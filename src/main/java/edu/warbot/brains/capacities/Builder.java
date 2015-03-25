package edu.warbot.brains.capacities;

import edu.warbot.agents.enums.WarAgentType;

public interface Builder {

    public void setNextBuildingToBuild(WarAgentType nextBuildingToBuild);
    public WarAgentType getNextBuildingToBuild();
    public boolean isAbleToBuild(WarAgentType building);

    public void setIdNextBuildingToRepair(int idNextBuildingToRepair);
    public int getIdNextBuildingToRepair();

}
