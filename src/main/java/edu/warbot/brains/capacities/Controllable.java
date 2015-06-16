package edu.warbot.brains.capacities;

import edu.warbot.agents.enums.WarAgentType;
import edu.warbot.agents.percepts.WallPercept;
import edu.warbot.agents.percepts.WarAgentPercept;
import edu.warbot.communications.WarMessage;
import edu.warbot.tools.geometry.PolarCoordinates;
import madkit.kernel.AbstractAgent.ReturnCode;

import java.awt.*;
import java.util.ArrayList;

public interface Controllable extends Alive {

    ReturnCode sendMessage(int idAgent, String message, String... content);

    void broadcastMessageToAll(String message, String... content);

    ReturnCode broadcastMessageToAgentType(WarAgentType agentType, String message, String... content);

    ReturnCode broadcastMessage(String groupName, String roleName, String message, String... content);

    ReturnCode reply(WarMessage warMessage, String message, String... content);

    ArrayList<WarMessage> getMessages();

    void setIdNextAgentToGive(int idNextAgentToGive);

    int getBagSize();

    int getNbElementsInBag();

    boolean isBagEmpty();

    boolean isBagFull();

    double getViewDirection();

    void setViewDirection(double viewDirection);

    ArrayList<WarAgentPercept> getPerceptsAllies();

    ArrayList<WarAgentPercept> getPerceptsEnemies();

    ArrayList<WarAgentPercept> getPerceptsResources();

    ArrayList<WarAgentPercept> getPerceptsAlliesByType(WarAgentType agentType);

    ArrayList<WarAgentPercept> getPerceptsEnemiesByType(WarAgentType agentType);

    ArrayList<WarAgentPercept> getPercepts();

    ArrayList<WallPercept> getWallPercepts();

    String getDebugString();

    void setDebugString(String debugString);

    Color getDebugStringColor();

    void setDebugStringColor(Color color);

    Shape getDebugShape();

    void setDebugShape(Shape debugShape);

    PolarCoordinates getAveragePositionOfUnitInPercept(WarAgentType agentType, boolean ally);

    PolarCoordinates getIndirectPositionOfAgentWithMessage(WarMessage message);

    PolarCoordinates getTargetedAgentPosition(double angleToAlly, double distanceFromAlly, double angleFromAllyToTarget, double distanceBetweenAllyAndTarget);

}
