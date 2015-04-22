package edu.warbot.FSM.plan;

import edu.warbot.FSM.action.WarAction;
import edu.warbot.FSM.action.WarActionIdle;
import edu.warbot.FSMEditor.settings.GenericPlanSettings;
import edu.warbot.brains.WarBrain;

/**
 * Reste sans bougé
 */
public class WarPlanIdle<BrainType extends WarBrain> extends WarPlan<BrainType> {

    private Integer nombreTik;

    public WarPlanIdle(BrainType brain, GenericPlanSettings planSettings) {
        super("Plan Idle", brain, planSettings);

        if (getPlanSettings().Tik_number != null)
            this.nombreTik = getPlanSettings().Tik_number;
        else
            this.nombreTik = 0;
    }

    public void buildActionList() {

        setPrintTrace(true);

        WarAction<BrainType> actionAttaquer = new WarActionIdle<>(getBrain(), nombreTik);

        addAction(actionAttaquer);

        setFirstAction(actionAttaquer);
    }

}
