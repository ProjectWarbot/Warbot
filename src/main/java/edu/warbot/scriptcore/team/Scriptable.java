package edu.warbot.scriptcore.team;

import edu.warbot.scriptcore.scriptagent.ScriptAgent;

public interface Scriptable {

	public void setScriptAgent(ScriptAgent script);

	public ScriptAgent getScriptAgent();
}