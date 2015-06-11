package edu.warbot.scriptcore.team;

import edu.warbot.brains.brains.WarBaseBrain;
import edu.warbot.scriptcore.scriptagent.ScriptAgent;

public abstract class ScriptableWarBase extends WarBaseBrain implements Scriptable {

    private ScriptAgent script;

    public ScriptableWarBase() {
        super();
    }

    @Override
    public void activate() {
    }

    @Override
    public String action() {


        return getScriptAgent().action();
    }

    public ScriptAgent getScriptAgent() {
        return script;
    }

    public void setScriptAgent(ScriptAgent script) {
        this.script = script;
    }
}
