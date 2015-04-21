package teams.fsm;

import edu.warbot.FSM.WarFSM;
import edu.warbot.brains.brains.WarRocketLauncherBrain;

public abstract class WarRocketLauncherBrainController extends WarRocketLauncherBrain {

    WarFSM<WarRocketLauncherBrain> fsm;

    public WarRocketLauncherBrainController() {
        super();
    }

    @Override
    public void activate() {
        initialisation();
    }

    @Override
    public String action() {
        return fsm.executeFSM();
    }

    private void initialisation() {
        fsm = new WarFSM<WarRocketLauncherBrain>();

//		/*** Refelxes ***/
//		WarCondition<WarRocketLauncherAdapter> condReflex = new WarConditionPerceptCounter<WarRocketLauncherAdapter>(getAgent(), WarAgentType.WarBase, true, ">", 0);
//		fsm.addReflexe(new WarReflexeWarnWithCondition<WarRocketLauncherAdapter>(getAgent(), condReflex, WarAgentType.WarRocketLauncher, WarFSMMessage.enemyBaseHere));
//
//		/*** Etats ***/
//		WarPlanSettings set1 = new WarPlanSettings();
//		set1.Offensif = true;
//		WarEtat<WarRocketLauncherAdapter> etatPatrouille = new WarEtat<WarRocketLauncherAdapter>("Etat patrouiler", 
//				new WarPlanPatrouiller(getAgent(), set1));
//		fsm.addEtat(etatPatrouille);
//
//		WarPlanSettings set2 = new WarPlanSettings();
//		set2.Agent_type_destination = new WarAgentType[1];
//		set2.Agent_type_destination[0] = WarAgentType.WarBase;
//		WarEtat<WarRocketLauncherAdapter> etatAtt = new WarEtat<WarRocketLauncherAdapter>("Etat Attaquer", 
//				new WarPlanAttaquer(getAgent(), set2));
//		fsm.addEtat(etatAtt);
//
//		WarEtat<WarRocketLauncherAdapter> etatDef = new WarEtat<WarRocketLauncherAdapter>("Etat Defendre", 
//				new WarPlanDefendre(getAgent(), null));
//		fsm.addEtat(etatDef);
//
//		/*** Conditions ***/
//		WarCondition<WarRocketLauncherAdapter> cond1 = new WarConditionPerceptCounter<WarRocketLauncherAdapter>(getAgent(), WarAgentType.WarBase, true, ">", 0);
//		cond1.setDestination(etatAtt);
//		etatPatrouille.addCondition(cond1);
//
//		WarCondition<WarRocketLauncherAdapter> cond2 = new WarConditionPerceptCounter<WarRocketLauncherAdapter>(getAgent(), WarAgentType.WarBase, true, "==", 0);
//		cond2.setDestination(etatAtt);
//		etatAtt.addCondition(cond2);
//
//		WarCondition<WarRocketLauncherAdapter> cond3 = new WarConditionMessageChecker<WarRocketLauncherAdapter>(getAgent(), WarAgentType.WarBase, WarFSMMessage.baseIsAttack);
//		cond3.setDestination(etatDef);
//		etatPatrouille.addCondition(cond3);
//		/*
//		WarCondition cond4 = new WarConditionPerceptCounter(getAgent(), WarAgentType.WarRocketLauncher, true, "==", 0);
//		cond4.setDestination(etatAtt);
//		etatDef.addCondition(cond4);
//		*/
//		fsm.setFirstEtat(etatPatrouille);
//
//		fsm.initFSM();
    }

}