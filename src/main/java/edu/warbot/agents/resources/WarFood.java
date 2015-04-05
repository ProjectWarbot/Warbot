package edu.warbot.agents.resources;

import edu.warbot.agents.WarResource;
import edu.warbot.agents.enums.WarAgentType;
import edu.warbot.game.Team;
import edu.warbot.launcher.WarGameConfig;

import java.util.Map;

public class WarFood extends WarResource {

	public static final int HEALTH_GIVEN;

	static {
		Map<String, Object> data = WarGameConfig.getConfigOfWarAgent(WarAgentType.WarFood);
		HEALTH_GIVEN = (int) data.get(WarGameConfig.RESOURCE_WARFOOD_CONFIG_HEALTH_GIVEN);
	}

	public WarFood(Team team) {
		super(WarGameConfig.getHitboxOfWarAgent(WarAgentType.WarFood), team);

		System.out.println("Food w : " + getHitbox().getWidth());
		System.out.println("Food h : " + getHitbox().getHeight());

		System.out.println("Food x : " + getPosition());
		System.out.println("Food y : " + getPosition().getY());
	}

	public WarAgentType getType()
	{
		return WarAgentType.WarFood;
	}
}
