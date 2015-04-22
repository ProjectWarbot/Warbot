package edu.warbot.FSM.plan;

import edu.warbot.FSM.action.WarAction;
import edu.warbot.FSM.action.WarActionChercherNouriture;
import edu.warbot.FSM.action.WarActionHealAlly;
import edu.warbot.FSM.condition.WarCondition;
import edu.warbot.FSM.condition.WarConditionTimeOut;
import edu.warbot.FSMEditor.settings.EnumMethod;
import edu.warbot.FSMEditor.settings.GenericConditionSettings;
import edu.warbot.FSMEditor.settings.GenericPlanSettings;
import edu.warbot.agents.enums.WarAgentType;
import edu.warbot.brains.WarBrain;
import edu.warbot.brains.capacities.Movable;

import javax.swing.*;

/**
 * Heal les alliés
 */
public class WarPlanHealAlly<BrainType extends WarBrain & Movable> extends WarPlan<BrainType> {

    private WarAgentType agentType;

    public WarPlanHealAlly(BrainType brain, GenericPlanSettings planSettings) {
        super("Plan heal ally", brain, planSettings);

        if (getPlanSettings().Agent_type != null)
            this.agentType = getPlanSettings().Agent_type;
        else
            JOptionPane.showMessageDialog(null, "You must chose <Agent_type> for plan <WarPlanHealAlly>", "Missing attribut", JOptionPane.ERROR_MESSAGE);

    }

    public void buildActionList() {

        setPrintTrace(true);

        WarAction<BrainType> actionHeal =
                new WarActionHealAlly<>(getBrain(), agentType);
        addAction(actionHeal);

        WarAction<BrainType> actionFindFood = new WarActionChercherNouriture<>(getBrain());
        addAction(actionFindFood);

        GenericConditionSettings condSet1 = new GenericConditionSettings();
        condSet1.Methode = EnumMethod.isBagEmpty;
        WarCondition<BrainType> condhealToFind =
                new WarConditionTimeOut<>("cond_heal", getBrain(), condSet1);
        actionHeal.addCondition(condhealToFind);
        condhealToFind.setDestination(actionFindFood);

        GenericConditionSettings condSet2 = new GenericConditionSettings();
        condSet2.Methode = EnumMethod.isBagFull;
        WarCondition<BrainType> condFindToHeal =
                new WarConditionTimeOut<>("cond_food", getBrain(), condSet2);
        condFindToHeal.setDestination(actionHeal);
        actionFindFood.addCondition(condFindToHeal);

        setFirstAction(actionHeal);
    }

}
