package edu.warbot.agents.buildings;

import edu.warbot.agents.WarBuilding;
import edu.warbot.agents.enums.WarAgentType;
import edu.warbot.game.Team;
import edu.warbot.launcher.WarGameConfig;

import java.util.Map;

public class Wall extends WarBuilding {

    public static final int COST;
    public static final int MAX_HEALTH;

    static {
		Map<String, Object> data = WarGameConfig.getConfigOfWarAgent(WarAgentType.Wall);
        COST = (int) data.get(WarGameConfig.AGENT_CONFIG_COST);
        MAX_HEALTH = (int) data.get(WarGameConfig.AGENT_CONFIG_MAX_HEALTH);
	}

	public Wall(Team team) {
		super(team, WarGameConfig.getHitboxOfWarAgent(WarAgentType.Wall), COST, MAX_HEALTH);

        System.out.println("Wall w : " + getHitbox().getWidth());
        System.out.println("Wall h : " + getHitbox().getHeight());
	}

    public WarAgentType getType()
    {
        return WarAgentType.Wall;
    }
}
