package edu.warbot.fsm.action;

import edu.warbot.agents.MovableWarAgent;
import edu.warbot.agents.agents.WarRocketLauncher;
import edu.warbot.agents.enums.WarAgentType;
import edu.warbot.agents.percepts.WarAgentPercept;
import edu.warbot.brains.brains.WarRocketLauncherBrain;
import edu.warbot.communications.WarMessage;
import edu.warbot.fsm.WarFSMMessage;
import edu.warbot.fsm.editor.settings.EnumMessage;
import edu.warbot.tools.geometry.CoordPolar;

import javax.swing.*;
import java.util.ArrayList;

/**
 * Cherche la base enemy
 */
public class WarActionChercherBase extends WarAction<WarRocketLauncherBrain> {

    CoordPolar coordPolarBase;

    public WarActionChercherBase(WarRocketLauncherBrain brain) {
        super(brain);
        JOptionPane.showMessageDialog(null, "Attention l'action ChercherBase n'a pas étée vérifiée et risque de ne pas fonctionner", "Waring not terminated action", JOptionPane.INFORMATION_MESSAGE);
    }

    @Override
    public String executeAction() {

        ArrayList<WarAgentPercept> basePercepts = getAgent().getPerceptsEnemiesByType(WarAgentType.WarBase);

        // Je vois la base
        if (basePercepts != null && basePercepts.size() > 0) {

            String baseCoord[] = {String.valueOf(basePercepts.get(0).getAngle()), String.valueOf(basePercepts.get(0).getDistance())};

            // Previent les autres unités de où est la base
            getAgent().broadcastMessageToAgentType(WarAgentType.WarRocketLauncher, WarFSMMessage.enemyBaseHere, baseCoord);

            getAgent().setHeading(basePercepts.get(0).getAngle());
            return MovableWarAgent.ACTION_MOVE;


        } else { //Si il ny a pas de base dans le percept

            // Je me souvient d'ou est la base
            if (coordPolarBase != null && coordPolarBase.getDistance() > WarRocketLauncher.DISTANCE_OF_VIEW) {
                getAgent().setHeading(coordPolarBase.getAngle());
                return MovableWarAgent.ACTION_MOVE;
            } else if (coordPolarBase != null && coordPolarBase.getDistance() < WarRocketLauncher.DISTANCE_OF_VIEW) {
                this.coordPolarBase = null;
            }

            WarMessage m = getMessageLocateBase();

            //Si j'ai un message qui me dit où est la base
            if (m != null && m.getContent().length == 2) {
                this.coordPolarBase = getAgent().getIndirectPositionOfAgentWithMessage(m);

                getAgent().setHeading(coordPolarBase.getAngle());
                return MovableWarAgent.ACTION_MOVE;
            } else if (m != null) {
                getAgent().setHeading(m.getAngle());
                return MovableWarAgent.ACTION_MOVE;
            }

            if (getAgent().isBlocked())
                getAgent().setRandomHeading();

            return MovableWarAgent.ACTION_MOVE;
        }

    }

    private WarMessage getMessageLocateBase() {
        for (WarMessage m : getAgent().getMessages()) {
            if (m.getMessage().equals(EnumMessage.enemy_base_here.name()))
                return m;
        }
        return null;
    }

    @Override
    public void actionWillBegin() {
        super.actionWillBegin();
    }

}
