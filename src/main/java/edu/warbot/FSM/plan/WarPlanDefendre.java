package edu.warbot.FSM.plan;

import edu.warbot.FSM.action.WarAction;
import edu.warbot.FSM.action.WarActionDefendre;
import edu.warbot.FSMEditor.settings.GenericPlanSettings;
import edu.warbot.brains.brains.WarRocketLauncherBrain;

/**
 * Réalise l'action WarActionDefendre
 */
public class WarPlanDefendre extends WarPlan<WarRocketLauncherBrain> {

    public WarPlanDefendre(WarRocketLauncherBrain brain, GenericPlanSettings planSettings) {
        super("Plan Defendre", brain, planSettings);
    }

    public void buildActionList() {

        setPrintTrace(true);

        WarAction<WarRocketLauncherBrain> actionDef = new WarActionDefendre(getBrain());
        addAction(actionDef);

        setFirstAction(actionDef);
    }

}
