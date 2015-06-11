package edu.warbot.game.mode;

import edu.warbot.game.WarGame;
import edu.warbot.game.mode.endCondition.NumberAgentEndCondition;
import edu.warbot.game.mode.endCondition.WarNumberAgent;

import java.util.ArrayList;
import java.util.List;

public class NumberAgentGameMode extends AbstractGameMode {

    public NumberAgentGameMode(WarGame game, Object[] args) {
        super(new NumberAgentEndCondition(game));

        List<WarNumberAgent> agents = new ArrayList<WarNumberAgent>();

        for (Object agent : args)
            agents.add((WarNumberAgent) agent);

        ((NumberAgentEndCondition) this.getEndCondition()).setAgents(agents);
    }

}