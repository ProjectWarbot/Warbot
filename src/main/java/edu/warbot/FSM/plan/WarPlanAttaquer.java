package edu.warbot.FSM.plan;

import edu.warbot.FSM.action.WarAction;
import edu.warbot.FSM.action.WarActionAttaquer;
import edu.warbot.FSMEditor.settings.GenericPlanSettings;
import edu.warbot.agents.enums.WarAgentType;
import edu.warbot.brains.brains.WarRocketLauncherBrain;

public class WarPlanAttaquer extends WarPlan<WarRocketLauncherBrain> {

    private WarAgentType agentType;

    public WarPlanAttaquer(WarRocketLauncherBrain brain, GenericPlanSettings planSettings) {
        super("Plan Attaquer", brain, planSettings);

        if (getPlanSettings().Agent_type != null)
            this.agentType = getPlanSettings().Agent_type;
        else
            this.agentType = WarAgentType.WarRocketLauncher;
    }

    public void buildActionList() {

        setPrintTrace(true);

        WarAction<WarRocketLauncherBrain> actionAttaquer =
                new WarActionAttaquer(getBrain(), agentType);

        addAction(actionAttaquer);

        setFirstAction(actionAttaquer);
    }

}
