package edu.warbot.brains.implementations;

import edu.warbot.agents.ControllableWarAgent;
import edu.warbot.agents.actions.constants.CreatorActions;
import edu.warbot.agents.enums.WarAgentType;
import edu.warbot.agents.percepts.WallPercept;
import edu.warbot.agents.percepts.WarAgentPercept;
import edu.warbot.brains.WarBrain;
import edu.warbot.brains.capacities.Creator;
import edu.warbot.communications.WarMessage;
import edu.warbot.tools.geometry.CoordPolar;
import madkit.kernel.AbstractAgent;

import java.awt.*;
import java.util.ArrayList;

public class WarCreatorBrainImplementation extends WarBrainImplementation implements CreatorActions, Creator {

    @Override
    public void setNextAgentToCreate(WarAgentType nextAgentToCreate) {
        ((Creator) this.agent).setNextAgentToCreate(nextAgentToCreate);
    }

    @Override
    public WarAgentType getNextAgentToCreate() {
        return ((Creator) this.agent).getNextAgentToCreate();
    }

    @Override
    public boolean isAbleToCreate(WarAgentType agent) {
        return ((Creator) this.agent).isAbleToCreate(agent);
    }
}
