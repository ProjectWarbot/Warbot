package edu.warbot.game.mode;

import edu.warbot.game.mode.endCondition.AbstractEndCondition;

public abstract class AbstractGameMode {

    private AbstractEndCondition endCondition;

    public AbstractGameMode(AbstractEndCondition endCondition) {
        this.endCondition = endCondition;
    }

    public AbstractEndCondition getEndCondition() {
        return endCondition;
    }
}
