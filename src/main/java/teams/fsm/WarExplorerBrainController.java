package teams.fsm;

import edu.warbot.FSM.WarFSM;
import edu.warbot.brains.brains.WarExplorerBrain;

public abstract class WarExplorerBrainController extends WarExplorerBrain {

    private WarFSM<WarExplorerBrain> fsm;

    public WarExplorerBrainController() {
        super();
    }

    @Override
    public String action() {
        return fsm.executeFSM();
    }

    @Override
    public void activate() {
        initialisation();
    }

    private void initialisation() {
        fsm = new WarFSM<WarExplorerBrain>();

//		/*** Reflexes ***/
//		WarCondition<WarExplorerAdapter> condReflex = new WarConditionPerceptCounter<WarExplorerAdapter>(getAgent(), WarAgentType.WarBase, true, ">", 0);
//		fsm.addReflexe(new WarReflexeWarnWithCondition<WarExplorerAdapter>(getAgent(), condReflex, WarAgentType.WarRocketLauncher, WarFSMMessage.enemyBaseHere));
//
//		/*** Etats ***/
//		WarEtat<WarExplorerAdapter> etatGetFood = new WarEtat<WarExplorerAdapter>("Etat get Food", new WarPlanRamasserNouriture<WarExplorerAdapter>(getAgent(), null));
//		fsm.addEtat(etatGetFood);
//
//		WarPlanSettings set1 = new WarPlanSettings();
//		set1.Value_pourcentage = 50;
//		set1.Value_pourcentage_destination = 50;
//		WarEtat<WarExplorerAdapter> etatHeal = new WarEtat<WarExplorerAdapter>("Etat heal", 
//				new WarPlanHealer<WarExplorerAdapter>(getAgent(), set1));
//		fsm.addEtat(etatHeal);
//
//		WarEtat<WarExplorerAdapter> etatSecure = new WarEtat<WarExplorerAdapter>("Etat be secure", new WarPlanBeSecure<WarExplorerAdapter>(getAgent(), null));
//		fsm.addEtat(etatSecure);
//
//		/*** Conditions ***/
//		WarCondition<WarExplorerAdapter> cond1 = new WarConditionAttributCheck<WarExplorerAdapter>(getAgent(), WarCondition.HEALTH, "<", WarExplorer.MAX_HEALTH, 20);
//		cond1.setDestination(etatHeal);
//
//		etatGetFood.addCondition(cond1);
//
//		WarCondition<WarExplorerAdapter> cond2 = new WarConditionPerceptAttributCheck<WarExplorerAdapter>(getAgent(), WarCondition.HEALTH, "<", WarExplorer.MAX_HEALTH, 20, false, null, true);
//		cond2.setDestination(etatHeal);
//		etatGetFood.addCondition(cond2);
//
//		WarCondition<WarExplorerAdapter> cond3 = new WarConditionAttributCheck<WarExplorerAdapter>(getAgent(), WarCondition.HEALTH, ">", WarExplorer.MAX_HEALTH, 80);
//		cond3.setDestination(etatGetFood);
//		etatHeal.addCondition(cond3);
//
//		WarCondition<WarExplorerAdapter> cond4 = new WarConditionPerceptAttributCheck<WarExplorerAdapter>(getAgent(), WarCondition.HEALTH, ">", WarExplorer.MAX_HEALTH, 80, false, null, true);
//		cond4.setDestination(etatGetFood);
//		etatHeal.addCondition(cond4);
//
//		WarCondition<WarExplorerAdapter> cond5 = new WarConditionPerceptCounter<WarExplorerAdapter>(getAgent(), WarAgentType.WarRocketLauncher, true, ">", 3);
//		cond5.setDestination(etatSecure);
//		etatHeal.addCondition(cond5);
//
//		WarCondition<WarExplorerAdapter> cond6 = new WarConditionPerceptCounter<WarExplorerAdapter>(getAgent(), WarAgentType.WarRocketLauncher, true, "==", 0);
//		cond6.setDestination(etatHeal);
//		etatSecure.addCondition(cond6);
//
//		WarCondition<WarExplorerAdapter> cond7 = new WarConditionPerceptCounter<WarExplorerAdapter>(getAgent(), WarAgentType.WarRocketLauncher, true, ">", 3);
//		cond7.setDestination(etatSecure);
//		etatGetFood.addCondition(cond7);
//
//		WarCondition<WarExplorerAdapter> cond8 = new WarConditionAttributCheck<WarExplorerAdapter>(getAgent(), WarCondition.HEALTH, "==", WarExplorer.MAX_HEALTH, 80);
//		cond8.setDestination(etatGetFood);
//		etatSecure.addCondition(cond8);
//
//		fsm.setFirstEtat(etatGetFood);
//
//		fsm.initFSM();
    }
}
