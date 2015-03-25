package edu.warbot.scriptcore.script;

import edu.warbot.scriptcore.interpreter.ScriptInterpreterLangage;

public class JsScript extends Script{

	
	public JsScript(StringBuilder file) {
		super(ScriptInterpreterLangage.JAVASCRIPT);
		setCodeAgent(file);
	}
}
