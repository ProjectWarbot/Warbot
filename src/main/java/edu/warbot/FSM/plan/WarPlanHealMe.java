package edu.warbot.FSM.plan;

/**
 * Me heal
 */

import edu.warbot.FSM.action.WarAction;
import edu.warbot.FSM.action.WarActionHealMe;
import edu.warbot.FSMEditor.settings.GenericPlanSettings;
import edu.warbot.brains.WarBrain;

public class WarPlanHealMe<BrainType extends WarBrain> extends WarPlan<BrainType> {

    public WarPlanHealMe(BrainType brain, GenericPlanSettings planSettings) {
        super("Plan heal myself", brain, planSettings);
    }

    public void buildActionList() {

        setPrintTrace(true);

        WarAction<BrainType> actionHeal = new WarActionHealMe<>(getBrain());
        addAction(actionHeal);

        setFirstAction(actionHeal);
    }

}
