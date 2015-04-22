package teams.fsm;

import edu.warbot.agents.agents.WarExplorer;
import edu.warbot.brains.brains.WarEngineerBrain;

public abstract class WarEngineerBrainController extends WarEngineerBrain {

    public WarEngineerBrainController() {
        super();
    }

    @Override
    public String action() {

        if (isBlocked())
            setRandomHeading();
        return WarExplorer.ACTION_MOVE;
    }
}
