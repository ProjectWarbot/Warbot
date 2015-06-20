package edu.warbot.game;

public interface WarGameListener {

    void onNewTeamAdded(InGameTeam newInGameTeam);

    void onTeamLost(InGameTeam removedInGameTeam);

    void onGameOver();

    void onGameStopped();

    void onGameStarted();
}
