package edu.warbot.agents;

import edu.warbot.game.Team;
import edu.warbot.launcher.WarGameConfig;

public abstract class WarResource extends WarAgent {

	private static final String ACTION_DEFAULT = "action";

	public static final double MAX_DISTANCE_TAKE = WarGameConfig.getMaxDistanceTake();
	
	public WarResource(Hitbox hitbox, Team team) {
		super(ACTION_DEFAULT, team, hitbox);
	}

	@Override
	protected void activate() {
		super.activate();
		do {
			randomLocation();
		} while (isGoingToBeOutOfMap() || isGoingToBeOverAnOtherAgent());
	}

    @Override
    public void kill() {
        killAgent(this);
        getTeam().removeWarAgent(this);
    }

    public String action() {
		return ACTION_DEFAULT;
	}
	
}
