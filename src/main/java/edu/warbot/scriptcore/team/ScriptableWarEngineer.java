package edu.warbot.scriptcore.team;

import edu.warbot.brains.brains.WarEngineerBrain;
import edu.warbot.scriptcore.scriptagent.ScriptAgent;

public abstract class ScriptableWarEngineer extends WarEngineerBrain implements Scriptable {
	
	private ScriptAgent script;
	
	public ScriptableWarEngineer() {
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
