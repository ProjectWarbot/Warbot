package edu.warbot.game.mode.endCondition;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import edu.warbot.agents.WarAgent;
import edu.warbot.game.Team;

public class WarCollectionNumberAgentByTeam {

    private Team team;

    //Le nom de la class de l'agent et de son nombre d'unité
    private Map<String, Long> agentsTeam;

    public WarCollectionNumberAgentByTeam(Team team) {
        super();
        this.team = team;
        this.agentsTeam = new HashMap<String, Long>();
        // TODO mettre les agents déjà crée à la création de la simulati
    }


    public Team getTeam() {
        return team;
    }

    public Long getNumberOfAgent(WarAgent agent) {

        Iterator<Entry<String, Long>> entries = agentsTeam.entrySet().iterator();

        while (entries.hasNext()) {
            Entry<String, Long> thisEntry = entries.next();

            if(equalAgent(thisEntry.getKey(), agent))
                return thisEntry.getValue();
        }

        return new Long(0);
    }

    public void incrementNumberOfAgent(WarAgent agent) {
        Iterator<Entry<String, Long>> entries = agentsTeam.entrySet().iterator();

        while (entries.hasNext()) {
            Entry<String, Long> thisEntry = entries.next();

            if(equalAgent(thisEntry.getKey(), agent)) {
                Long l = thisEntry.getValue();
                if(l < Long.MAX_VALUE)
                    thisEntry.setValue(l++);
                break;
            }
        }
    }

    public void decrementNumberOfAgent(WarAgent agent) {
        Iterator<Entry<String, Long>> entries = agentsTeam.entrySet().iterator();

        while (entries.hasNext()) {
            Entry<String, Long> thisEntry = entries.next();

            if(equalAgent(thisEntry.getKey(), agent)) {
                Long l = thisEntry.getValue();
                if(l > 0)
                    thisEntry.setValue(l--);
                break;
            }
        }
    }

    public void addNewAgent(WarAgent agent) {
        if(!isInCollectionOfAgent(agent))
            agentsTeam.put(agent.getClass().getSimpleName(), new Long(1));
    }

    public boolean isInCollectionOfAgent(WarAgent agent) {

        Iterator<Entry<String, Long>> entries = agentsTeam.entrySet().iterator();

        while (entries.hasNext()) {
            Entry<String, Long> thisEntry = entries.next();

            if(equalAgent(thisEntry.getKey(), agent))
                return true;
        }

        return false;
    }

    public boolean isCollectionOfThisTeam(Team team) {
        return this.team.equals(team);
    }

    public boolean equalAgent(String name, WarAgent agent) {
        return agent.getClass().getSimpleName().equals(name);
    }

}