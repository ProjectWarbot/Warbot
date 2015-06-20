package edu.warbot.scriptcore.team;

import edu.warbot.brains.brains.WarExplorerBrain;
import edu.warbot.scriptcore.scriptagent.ScriptAgent;

public abstract class ScriptableWarExplorer extends WarExplorerBrain implements Scriptable {


    private ScriptAgent script;

    public ScriptableWarExplorer() {
        super();
    }


    @Override
    public final String action() {
        return getScriptAgent().action();
    }

    public ScriptAgent getScriptAgent() {
        return script;
    }

    public void setScriptAgent(ScriptAgent script) {
        this.script = script;
    }
}