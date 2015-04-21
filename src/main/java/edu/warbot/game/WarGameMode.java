package edu.warbot.game;

import edu.warbot.game.mode.AbstractGameMode;
import edu.warbot.game.mode.DuelGameMode;
import edu.warbot.game.mode.ResourcesRunGameMode;
import edu.warbot.game.mode.TimerGameMode;

public enum WarGameMode {
    Duel(DuelGameMode.class),
    ResourcesRun(ResourcesRunGameMode.class),
    TimerRun(TimerGameMode.class),
    NumberAgentGameMode(edu.warbot.game.mode.NumberAgentGameMode.class);

    private Class<? extends AbstractGameMode> gameModeClass;

    private WarGameMode(Class<? extends AbstractGameMode> gameModeClass) {
        this.gameModeClass = gameModeClass;
    }

    public Class<? extends AbstractGameMode> getGameModeClass() {
        return gameModeClass;
    }
}
