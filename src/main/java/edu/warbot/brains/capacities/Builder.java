package edu.warbot.brains.capacities;

import edu.warbot.agents.enums.WarAgentType;

public interface Builder {

    public WarAgentType getNextBuildingToBuild();

    public void setNextBuildingToBuild(WarAgentType nextBuildingToBuild);

    public boolean isAbleToBuild(WarAgentType building);

    public int getIdNextBuildingToRepair();

    public void setIdNextBuildingToRepair(int idNextBuildingToRepair);

}
