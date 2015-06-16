package edu.warbot.brains;


import edu.warbot.agents.actions.ControllableActionsMethods;
import edu.warbot.agents.actions.IdlerActionsMethods;
import edu.warbot.agents.actions.constants.ControllableActions;
import edu.warbot.brains.capacities.Controllable;

public abstract class WarBrain implements ControllableActions, Controllable, ControllableActionsMethods, IdlerActionsMethods {

    public WarBrain() {
    }

    public void activate() {
    }

    public String action() {
        return idle();
    }

    @Override
    public final String idle() {
        return ACTION_IDLE;
    }

    @Override
    public String give() {
        return ACTION_GIVE;
    }

    @Override
    public String eat() {
        return ACTION_EAT;
    }

}
