package edu.warbot.brains;


import edu.warbot.agents.actions.constants.ControllableActions;
import edu.warbot.brains.capacities.Controllable;

public abstract class WarBrain implements ControllableActions, Controllable {

	public WarBrain() {}
	
    public void activate() {}

    public String action() { return ACTION_IDLE; }

}
