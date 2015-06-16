package edu.warbot.agents.actions.constants;

import edu.warbot.launcher.WarGameConfig;

public interface ControllableActions extends IdlerActions {

    double MAX_DISTANCE_GIVE = WarGameConfig.getMaxDistanceGive();

    String ACTION_GIVE = "give";

    String ACTION_EAT = "eat";

}
