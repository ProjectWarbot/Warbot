package edu.warbot.brains.capacities;

import edu.warbot.agents.enums.WarAgentType;
import edu.warbot.agents.percepts.WallPercept;
import edu.warbot.agents.percepts.WarAgentPercept;
import edu.warbot.communications.WarMessage;
import edu.warbot.tools.geometry.CoordPolar;
import madkit.kernel.AbstractAgent.ReturnCode;

import java.awt.*;
import java.util.ArrayList;

public interface Controllable extends Alive {

	public ReturnCode sendMessage(int idAgent, String message, String ... content);
	public void broadcastMessageToAll(String message, String ... content);
    public ReturnCode broadcastMessageToAgentType(WarAgentType agentType, String message, String ... content);
    public ReturnCode broadcastMessage(String groupName, String roleName, String message, String ... content);
    public ReturnCode reply(WarMessage warMessage, String message, String ... content);
    public ArrayList<WarMessage> getMessages();

	public void setIdNextAgentToGive(int idNextAgentToGive);
	public int getBagSize();
	public int getNbElementsInBag();
	public boolean isBagEmpty();
	public boolean isBagFull();
	
	public void setViewDirection(double viewDirection);
	public double getViewDirection();

	public ArrayList<WarAgentPercept> getPerceptsAllies();
	public ArrayList<WarAgentPercept> getPerceptsEnemies();
	public ArrayList<WarAgentPercept> getPerceptsResources();
	public ArrayList<WarAgentPercept> getPerceptsAlliesByType(WarAgentType agentType);
	public ArrayList<WarAgentPercept> getPerceptsEnemiesByType(WarAgentType agentType);
	public ArrayList<WarAgentPercept> getPercepts();
    public ArrayList<WallPercept> getWallPercepts();
	
	public String getDebugString();
	public void setDebugString(String debugString);
	public Color getDebugStringColor();
	public void setDebugStringColor(Color color);
    public Shape getDebugShape();
    public void setDebugShape(Shape debugShape);
	
	public CoordPolar getAveragePositionOfUnitInPercept(WarAgentType agentType, boolean ally);
	public CoordPolar getIndirectPositionOfAgentWithMessage(WarMessage message);
	public CoordPolar getTargetedAgentPosition(double angleToAlly, double distanceFromAlly, double angleFromAllyToTarget, double distanceBetweenAllyAndTarget);
}
