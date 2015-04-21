package edu.warbot.game.mode.endCondition;

import edu.warbot.agents.ControllableWarAgent;
import edu.warbot.agents.agents.WarBase;
import edu.warbot.game.Team;
import edu.warbot.game.WarGame;

public class ResourcesRunEndCondition extends AbstractEndCondition {

    private int resourcesNeededToWin;
    private Team winner;

    public ResourcesRunEndCondition(WarGame game, int resourcesNeededToWin) {
        super(game);
        this.resourcesNeededToWin = resourcesNeededToWin;
    }

    @Override
    public void doAfterEachTick() {
        for (Team t : getGame().getPlayerTeams()) {
            int currentTeamResources = 0;
            for (ControllableWarAgent agent : t.getControllableAgents()) {
                if (agent instanceof WarBase) {
                    currentTeamResources += agent.getNbElementsInBag();
                }
            }
            if (currentTeamResources >= resourcesNeededToWin) {
                this.winner = t;
            }
        }
    }

    @Override
    public boolean isGameEnded() {
        if (winner != null) {
            for (Team t : getGame().getPlayerTeams()) {
                if (!t.equals(winner)) {
                    getGame().setTeamAsLost(t);
                }
            }
            return true;
        }
        return false;
    }
}
