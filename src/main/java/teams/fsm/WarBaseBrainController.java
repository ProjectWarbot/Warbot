package teams.fsm;

import edu.warbot.brains.brains.WarBaseBrain;
import edu.warbot.fsm.WarFSM;

public abstract class WarBaseBrainController extends WarBaseBrain {

    WarFSM<WarBaseBrain> fsm;

    public WarBaseBrainController() {
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
        fsm = new WarFSM<WarBaseBrain>();
//
//		/*** Refelexes ***/
//		fsm.addReflexe(new WarReflexeAnswerMessage<WarBaseAdapter>(getAgent(), WarFSMMessage.whereAreYou, WarFSMMessage.here));
//
//		WarCondition<WarBaseAdapter> condReflex = new WarConditionPerceptCounter<WarBaseAdapter>(getAgent(), WarAgentType.WarRocketLauncher, true, ">", 0);
//		fsm.addReflexe(new WarReflexeWarnWithCondition<WarBaseAdapter>(getAgent(), condReflex, WarAgentType.WarRocketLauncher, WarFSMMessage.baseIsAttack));
//
//		/*** Etats ***/
//		//Création et remplissage du plan settings
//		//Pas super beau après un montra un system d'accesseur jolie
//		WarPlanSettings set1 = new WarPlanSettings();
//		set1.Agent_type_destination = new WarAgentType[2];
//		set1.Agent_type_destination[0] = WarAgentType.WarExplorer;
//		set1.Agent_type_destination[1] = WarAgentType.WarRocketLauncher;
//		set1.Number_agent_destination = new Integer[2];
//		set1.Number_agent_destination[0] = 1;
//		set1.Number_agent_destination[1] = 1;
//		set1.Value_reference = WarBase.MAX_HEALTH;
//		set1.Value_pourcentage = 50;
//		
//		WarEtat<WarBaseAdapter> etatCreerUnite = new WarEtat<WarBaseAdapter>("Etat creer unité",
//				new WarPlanCreateUnit(getAgent(), set1));
//		fsm.addEtat(etatCreerUnite);
//
//		fsm.setFirstEtat(etatCreerUnite);
//
//		fsm.initFSM();

    }

}
