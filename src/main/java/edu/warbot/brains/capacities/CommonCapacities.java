package edu.warbot.brains.capacities;

import edu.warbot.agents.percepts.WarAgentPercept;
import madkit.kernel.AbstractAgent;

public interface CommonCapacities {

    AbstractAgent.ReturnCode requestRole(String group, String role);

    AbstractAgent.ReturnCode leaveRole(String group, String role);

    AbstractAgent.ReturnCode leaveGroup(String group);

    int getNumberOfAgentsInRole(String group, String role);

    double getHeading();

    void setHeading(double angle);

    void setRandomHeading();

    void setRandomHeading(int range);

    String getTeamName();

    boolean isEnemy(WarAgentPercept percept);

    int getID();
}
