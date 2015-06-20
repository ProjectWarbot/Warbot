package edu.warbot.game;

import edu.warbot.agents.WarAgent;
import edu.warbot.agents.WarBuilding;
import edu.warbot.game.mode.AbstractGameMode;
import edu.warbot.maps.AbstractWarMap;

import java.awt.*;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

public class WarGame {

    public static final Color[] TEAM_COLORS = {
            new Color(149, 149, 255), // Blue
            new Color(255, 98, 98), // Red
            Color.YELLOW,
            Color.PINK,
            Color.CYAN,
            Color.ORANGE,
            Color.MAGENTA
    };

    public static Integer FPS = 0;
    private double timeLastSecond = -1;
    private Integer currentFPS = 0;

    private List<WarGameListener> listeners;

    private MotherNatureTeam _motherNature;
    private List<InGameTeam> playerInGameTeams;
    private List<InGameTeam> loserInGameTeams;
    private AbstractWarMap _map;
    private WarGameSettings settings;
    private AbstractGameMode gameMode;

    public WarGame(WarGameSettings settings) {
        this.settings = settings;
        listeners = new ArrayList<>();
        this._motherNature = new MotherNatureTeam(this);
        this.playerInGameTeams = settings.getSelectedInGameTeams();
        loserInGameTeams = new ArrayList<>();
        int colorCounter = 0;
        for (InGameTeam t : playerInGameTeams) {
            t.setColor(TEAM_COLORS[colorCounter]);
            t.setGame(this);
            colorCounter++;
        }
        _map = settings.getSelectedMap();
        try {
            gameMode = settings.getGameMode().getGameModeClass().getConstructor(WarGame.class, Object[].class).newInstance(this, settings.getGameModeArguments());
        } catch (InstantiationException | InvocationTargetException | IllegalAccessException | NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    public void setLogLevel(Level l) {
        for (InGameTeam t : playerInGameTeams) {
            for (WarAgent a : t.getAllAgents()) {
                a.setLogLevel(l);
            }
        }
        for (WarAgent a : _motherNature.getAllAgents()) {
            a.setLogLevel(l);
        }
        settings.setDefaultLogLevel(l);
    }

    public MotherNatureTeam getMotherNatureTeam() {
        return _motherNature;
    }

    public void addPlayerTeam(InGameTeam inGameTeam) {
        InGameTeam newInGameTeam = inGameTeam.duplicate(inGameTeam.getName());
        playerInGameTeams.add(newInGameTeam);
        newInGameTeam.setGame(this);
        for (WarGameListener listener : getListeners())
            listener.onNewTeamAdded(newInGameTeam);
    }

    public void setTeamAsLost(InGameTeam inGameTeam) {
        playerInGameTeams.remove(inGameTeam);
        loserInGameTeams.add(inGameTeam);
//        inGameTeam.killAllAgents();

        for (WarGameListener listener : getListeners())
            listener.onTeamLost(inGameTeam);
    }

    public InGameTeam getPlayerTeam(String teamName) {
        for (InGameTeam t : playerInGameTeams) {
            if (t.getName().equals(teamName))
                return t;
        }
        return null;
    }

    public ArrayList<InGameTeam> getPlayerTeams() {
        return new ArrayList<>(playerInGameTeams);
    }

    public ArrayList<InGameTeam> getAllTeams() {
        ArrayList<InGameTeam> inGameTeams = getPlayerTeams();
        inGameTeams.add(getMotherNatureTeam());
        inGameTeams.addAll(loserInGameTeams);
        return inGameTeams;
    }

    public ArrayList<WarAgent> getAllAgentsInRadiusOf(WarAgent a, double radius) {
        ArrayList<WarAgent> toReturn = new ArrayList<>();
        for (InGameTeam t : getAllTeams()) {
            toReturn.addAll(t.getAllAgentsInRadiusOf(a, radius));
        }
        return toReturn;
    }

    public ArrayList<WarAgent> getAllAgentsInRadius(double posX, double posY, double radius) {
        ArrayList<WarAgent> toReturn = new ArrayList<>();
        for (InGameTeam t : getAllTeams()) {
            toReturn.addAll(t.getAllAgentsInRadius(posX, posY, radius));
        }
        return toReturn;
    }

    public ArrayList<WarBuilding> getBuildingsInRadiusOf(WarAgent referenceAgent, double radius) {
        ArrayList<WarBuilding> toReturn = new ArrayList<>();
        for (InGameTeam t : getAllTeams()) {
            toReturn.addAll(t.getBuildingsInRadiusOf(referenceAgent, radius));
        }
        return toReturn;
    }


    public String[] getPlayerTeamNames() {
        String[] toReturn = new String[playerInGameTeams.size()];
        int compteur = 0;
        for (InGameTeam t : playerInGameTeams) {
            toReturn[compteur] = t.getName();
            compteur++;
        }
        return toReturn;
    }

    public AbstractWarMap getMap() {
        return _map;
    }

    public WarGameSettings getSettings() {
        return settings;
    }

    public void doAfterEachTick() {
        calculeFPS();
        for (InGameTeam t : playerInGameTeams)
            t.doAfterEachTick();
        _motherNature.doAfterEachTick();
        gameMode.getEndCondition().doAfterEachTick();

        for (InGameTeam t : loserInGameTeams) {
            if (!t.hasLost()) {
                t.setHasLost(true);
                t.killAllAgents();
            }
        }

        if (gameMode.getEndCondition().isGameEnded())
            setGameOver();
    }

    public void setGameOver() {
        for (WarGameListener listener : getListeners())
            listener.onGameOver();
    }

    public void stopGame() {
        for (WarGameListener listener : getListeners())
            listener.onGameStopped();
    }

    public void setGameStarted() {
        for (WarGameListener listener : getListeners())
            listener.onGameStarted();
    }

    protected void calculeFPS() {
        currentFPS++;
        if (timeLastSecond + 1000 < System.currentTimeMillis() || timeLastSecond == -1) {
            timeLastSecond = System.currentTimeMillis();
            FPS = currentFPS;
            currentFPS = 0;
        }
    }

    public Integer getFPS() {
        return FPS;
    }

    public AbstractGameMode getGameMode() {
        return gameMode;
    }

    public void addWarGameListener(WarGameListener warGameListener) {
        listeners.add(warGameListener);
    }

    public void removeWarGameListener(WarGameListener warGameListener) {
        listeners.remove(warGameListener);
    }

    private List<WarGameListener> getListeners() {
        return new ArrayList<>(listeners);
    }

}
