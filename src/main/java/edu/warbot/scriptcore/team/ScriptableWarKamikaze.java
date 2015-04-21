package edu.warbot.scriptcore.team;

import edu.warbot.brains.brains.WarKamikazeBrain;
import edu.warbot.scriptcore.scriptagent.ScriptAgent;

public abstract class ScriptableWarKamikaze extends WarKamikazeBrain implements Scriptable {

    private ScriptAgent script;

    public ScriptableWarKamikaze() {
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
