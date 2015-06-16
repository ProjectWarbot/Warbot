package edu.warbot.game;

public interface WarGameListener {

    public void onNewTeamAdded(InGameTeam newInGameTeam);

    public void onTeamLost(InGameTeam removedInGameTeam);

    public void onGameOver();

    public void onGameStopped();

    public void onGameStarted();
}
