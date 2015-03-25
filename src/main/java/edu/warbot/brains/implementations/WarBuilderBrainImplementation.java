package edu.warbot.brains.implementations;

import edu.warbot.agents.actions.constants.BuilderActions;
import edu.warbot.agents.enums.WarAgentType;
import edu.warbot.brains.capacities.Agressive;
import edu.warbot.brains.capacities.Builder;

public class WarBuilderBrainImplementation extends WarBrainImplementation implements BuilderActions, Builder {

    @Override
    public void setNextBuildingToBuild(WarAgentType nextBuildingToBuild) {
        ((Builder) this.agent).setNextBuildingToBuild(nextBuildingToBuild);
    }

    @Override
    public WarAgentType getNextBuildingToBuild() {
        return ((Builder) this.agent).getNextBuildingToBuild();
    }

    @Override
    public boolean isAbleToBuild(WarAgentType building) {
        return ((Builder) this.agent).isAbleToBuild(building);
    }

    @Override
    public void setIdNextBuildingToRepair(int idNextBuildingToRepair) {
        ((Builder) this.agent).setIdNextBuildingToRepair(idNextBuildingToRepair);
    }

    @Override
    public int getIdNextBuildingToRepair() {
        return ((Builder) this.agent).getIdNextBuildingToRepair();
    }
}
