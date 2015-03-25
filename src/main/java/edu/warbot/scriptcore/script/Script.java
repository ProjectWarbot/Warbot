package edu.warbot.scriptcore.script;

import edu.warbot.scriptcore.interpreter.ScriptInterpreterLangage;

public abstract class Script {
	
	private ScriptInterpreterLangage langage;
	private StringBuilder codeAgent;
	
	public Script(ScriptInterpreterLangage langage) {
		super();
		this.langage = langage;
	}

	public ScriptInterpreterLangage getLangage() {
		return langage;
	}
	
	public StringBuilder getCodeAgent() {
		return codeAgent;
	}
	

	public void setCodeAgent(StringBuilder codeAgent) {
		this.codeAgent = codeAgent;
	}

	public void addCodeToAgent(StringBuilder code) {
		codeAgent.append(code);
	}

	public void addCodeToAgent(Script script) {
		codeAgent.append(script.getCodeAgent());
	}

	@Override
	public String toString() {
		return "Script [langage = " + langage + "]";
	}
}
