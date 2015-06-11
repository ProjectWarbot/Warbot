package edu.warbot.scriptcore.interpreter.python;

/**
 * Created by jimmy on 18/03/15.
 */
public enum ActionPython {
    actionBase("actionBase"),
    actionEngineer("actionEngineer"),
    actionExplorer("actionExplorer"),
    actionKamikaze("actionKamikaze"),
    actionRocketLauncher("actionRocketLauncher"),
    actionTurret("actionTurret");

    private String nameAction = "";

    ActionPython(String action) {
        this.nameAction = action;
    }

    public String toString() {
        return nameAction;
    }
}
