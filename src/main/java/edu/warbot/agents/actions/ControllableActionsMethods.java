package edu.warbot.agents.actions;

import edu.warbot.agents.actions.constants.ControllableActions;

public interface ControllableActionsMethods extends IdlerActionsMethods, ControllableActions {

	public String give();
	public String eat();
}
