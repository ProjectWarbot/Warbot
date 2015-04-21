package edu.warbot.FSM;

import edu.warbot.brains.WarBrain;

import javax.swing.*;

public abstract class WarFSMBrain extends WarBrain {

    private WarFSM<WarBrain> fsm;

    @Override
    public String action() {
        try {
            return fsm.executeFSM();
        } catch (NullPointerException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null,
                    "Error FSM use incorrect settings, please check <GenericSettings>, <settings> in the editor, and your settings in the Editor"
                    , "FSM intern error caused by wrong configuration", JOptionPane.INFORMATION_MESSAGE);
            return null;
        }
    }

    public WarFSM getFSM() {
        return fsm;
    }

    public void setFSM(WarFSM<WarBrain> fsm) {
        this.fsm = fsm;
    }

}
