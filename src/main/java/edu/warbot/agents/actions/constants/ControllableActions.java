package edu.warbot.agents.actions.constants;

import edu.warbot.launcher.WarGameConfig;

public interface ControllableActions extends IdlerActions {

    public static final double MAX_DISTANCE_GIVE = WarGameConfig.getMaxDistanceGive();
    public static final String ACTION_GIVE = "give";
    public static final String ACTION_EAT = "eat";

}
