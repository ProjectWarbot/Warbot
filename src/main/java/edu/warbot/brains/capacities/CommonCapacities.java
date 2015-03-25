package edu.warbot.brains.capacities;

import edu.warbot.agents.percepts.WarAgentPercept;
import madkit.kernel.AbstractAgent;

public interface CommonCapacities {

	public AbstractAgent.ReturnCode requestRole(String group, String role);
	public AbstractAgent.ReturnCode leaveRole(String group, String role);
	public AbstractAgent.ReturnCode leaveGroup(String group);
	public int getNumberOfAgentsInRole(String group, String role);
	
	public double getHeading();
	public void setHeading(double angle);
	public void setRandomHeading();
	public void setRandomHeading(int range);

	public String getTeamName();
	public boolean isEnemy(WarAgentPercept percept);

	public int getID();
}
