package edu.warbot.scriptcore.script;

import edu.warbot.scriptcore.interpreter.ScriptInterpreterLangage;

public class PyScript extends Script{

	
	public PyScript(StringBuilder file) {
		super(ScriptInterpreterLangage.PYTHON);
		setCodeAgent(file);
	}

}
