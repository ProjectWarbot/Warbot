package edu.warbot.FSM.plan;

import edu.warbot.FSM.action.WarAction;
import edu.warbot.FSM.action.WarActionSendMessage;
import edu.warbot.FSMEditor.settings.EnumMessage;
import edu.warbot.FSMEditor.settings.GenericPlanSettings;
import edu.warbot.agents.enums.WarAgentType;
import edu.warbot.brains.WarBrain;

import javax.swing.*;

public class WarPlanSendMessage<BrainType extends WarBrain> extends WarPlan<BrainType> {
	
	WarAgentType agentType;
	EnumMessage msg;
	
	public WarPlanSendMessage(BrainType brain, GenericPlanSettings planSettings) {
		super("Plan Attaquer", brain, planSettings);
		
		if(getPlanSettings().Agent_type != null)
			this.agentType = getPlanSettings().Agent_type;
		else
			JOptionPane.showMessageDialog(null, "You must chose <Agent_type> for plan <WarPlanSendMessage>", "Missing attribut", JOptionPane.ERROR_MESSAGE);
	
		if(getPlanSettings().Message != null)
			this.msg = getPlanSettings().Message;
		else
			JOptionPane.showMessageDialog(null, "You must chose <Message> for plan <WarPlanSendMessage>", "Missing attribut", JOptionPane.ERROR_MESSAGE);
	
		
	}

	public void buildActionList() {
		
		setPrintTrace(true);
			
		WarAction<BrainType> actionMsg =
				new WarActionSendMessage<>(getBrain(), agentType, msg);
		
		addAction(actionMsg);
		
		setFirstAction(actionMsg);
	}
	
}
