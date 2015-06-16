package edu.warbot.brains.capacities;

import edu.warbot.agents.enums.WarAgentType;

public interface Builder {

    WarAgentType getNextBuildingToBuild();

    void setNextBuildingToBuild(WarAgentType nextBuildingToBuild);

    boolean isAbleToBuild(WarAgentType building);

    int getIdNextBuildingToRepair();

    void setIdNextBuildingToRepair(int idNextBuildingToRepair);

}
