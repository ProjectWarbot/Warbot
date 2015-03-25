package edu.warbot.game;

public interface WarGameListener {

    public void onNewTeamAdded(Team newTeam);
    public void onTeamLost(Team removedTeam);
    public void onGameOver();
    public void onGameStopped();
    public void onGameStarted();
}
