package edu.warbot.scriptcore.team;

import edu.warbot.brains.brains.WarRocketLauncherBrain;
import edu.warbot.scriptcore.scriptagent.ScriptAgent;

public abstract class ScriptableWarRocketLauncher extends WarRocketLauncherBrain implements Scriptable {
	
	private ScriptAgent script;

	public ScriptableWarRocketLauncher() {
		super();
	}
	
	@Override
	public void activate() {
		
	}
	
	@Override
	public String action() {		
		return getScriptAgent().action();
	}	
	
	public void setScriptAgent(ScriptAgent script) {
		this.script = script;
	}

	public ScriptAgent getScriptAgent() {
		return script;
	}
	
}