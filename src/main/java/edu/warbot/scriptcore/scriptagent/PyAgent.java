package edu.warbot.scriptcore.scriptagent;

import org.python.core.PyObject;

import edu.warbot.brains.WarBrain;
import edu.warbot.scriptcore.wrapper.PyWrapper;

public class PyAgent extends PyObject implements ScriptAgent, Action{

	private Action action;

	public PyAgent() {
		
	}

	@Override
	public String action() {
		return action.action();
	}

	@Override
	public void link(WarBrain brain) {
		invoke("set",new PyWrapper<WarBrain>(brain));
	}

	public Action getAction() {
		return action;
	}

	public void setAction(Action action) {
		this.action = action;
	}
}
