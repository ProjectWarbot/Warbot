package edu.warbot.game.modes;

import edu.warbot.game.modes.endCondition.AbstractEndCondition;

public abstract class AbstractGameMode {

    private AbstractEndCondition endCondition;

    public AbstractGameMode(AbstractEndCondition endCondition) {
        this.endCondition = endCondition;
    }

    public AbstractEndCondition getEndCondition() {
        return endCondition;
    }
}
