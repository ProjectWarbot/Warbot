package edu.warbot.game.mode.endCondition;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import edu.warbot.agents.ControllableWarAgent;
import edu.warbot.agents.WarAgent;
import edu.warbot.game.MotherNatureTeam;
import edu.warbot.game.Team;
import edu.warbot.game.TeamListener;
import edu.warbot.game.WarGame;

public class NumberLostAgentEndCondition extends AbstractEndCondition implements TeamListener{

    private long maxAgentLost;

    private Team teamVictory;
    private List<Team> teamsOfGame;

    private Map<Team, Long> lostAgentByTeam;

    public NumberLostAgentEndCondition(WarGame game, long maxAgentLost) {
        super(game);
        this.maxAgentLost = maxAgentLost;
        this.teamVictory = null;
        this.teamsOfGame = game.getAllTeams();

        this.lostAgentByTeam = new HashMap<Team, Long>();

        for(Team team : teamsOfGame)
            if(!(team instanceof MotherNatureTeam))
                lostAgentByTeam.put(team, new Long(0));

        for (Team t : getGame().getPlayerTeams()) {
            t.addTeamListener(this);
        }

    }

    @Override
    public void doAfterEachTick() {
        // VOID
    }

    @Override
    public boolean isGameEnded() {

        if(getTeamVictory() != null) {
            System.out.println("Team victory : " + teamVictory.getName());
            return true;
        }

        return false;
    }

    @Override
    public void onAgentAdded(WarAgent newAgent) {
        // VOID
    }

    @Override
    public void onAgentRemoved(WarAgent removedAgent) {

        if(removedAgent instanceof ControllableWarAgent) {

            Iterator<Entry<Team, Long>> entries = lostAgentByTeam.entrySet().iterator();

            while (entries.hasNext()) {
                Entry<Team, Long> thisEntry = (Entry<Team, Long>) entries.next();

                Long newNumberLost = thisEntry.getValue() + 1;

                thisEntry.setValue(newNumberLost);

                if(thisEntry.getValue() > maxAgentLost)
                    whoTeamWin(removedAgent);
            }
        }

    }


    public void whoTeamWin(WarAgent removedAgent) {
        for(Team team : teamsOfGame)
            if(!(team instanceof MotherNatureTeam) && !(team.equals(removedAgent.getTeam())))
                teamVictory = team;
    }

    public Team getTeamVictory() {
        return teamVictory;
    }



}