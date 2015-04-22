package teams.fsm;

import edu.warbot.agents.agents.WarTurret;
import edu.warbot.brains.brains.WarTurretBrain;

public abstract class WarTurretBrainController extends WarTurretBrain {

    public WarTurretBrainController() {
        super();
    }

    @Override
    public String action() {
        return WarTurret.ACTION_IDLE;
    }
}
