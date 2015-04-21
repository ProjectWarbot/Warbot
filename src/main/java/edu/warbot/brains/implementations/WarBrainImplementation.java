package edu.warbot.brains.implementations;

import edu.warbot.agents.ControllableWarAgent;
import edu.warbot.agents.enums.WarAgentType;
import edu.warbot.agents.percepts.WallPercept;
import edu.warbot.agents.percepts.WarAgentPercept;
import edu.warbot.brains.WarBrain;
import edu.warbot.communications.WarMessage;
import edu.warbot.tools.geometry.CoordPolar;
import madkit.kernel.AbstractAgent;

import java.awt.*;
import java.util.ArrayList;

public class WarBrainImplementation extends WarBrain implements AgentBrainImplementer {

    protected ControllableWarAgent agent;

    @Override
    public void setAgent(ControllableWarAgent agent) {
        this.agent = agent;
    }

    @Override
    public AbstractAgent.ReturnCode sendMessage(int idAgent, String message, String... content) {
        return agent.sendMessage(idAgent, message, content);
    }

    @Override
    public void broadcastMessageToAll(String message, String... content) {
        agent.broadcastMessageToAll(message, content);
    }

    @Override
    public AbstractAgent.ReturnCode broadcastMessageToAgentType(WarAgentType agentType, String message, String... content) {
        return agent.broadcastMessageToAgentType(agentType, message, content);
    }

    @Override
    public AbstractAgent.ReturnCode broadcastMessage(String groupName, String roleName, String message, String... content) {
        return agent.broadcastMessage(groupName, roleName, message, content);
    }

    @Override
    public AbstractAgent.ReturnCode reply(WarMessage warMessage, String message, String... content) {
        return agent.reply(warMessage, message, content);
    }

    @Override
    public ArrayList<WarMessage> getMessages() {
        return agent.getMessages();
    }

    @Override
    public void setIdNextAgentToGive(int idNextAgentToGive) {
        agent.setIdNextAgentToGive(idNextAgentToGive);
    }

    @Override
    public int getBagSize() {
        return agent.getBagSize();
    }

    @Override
    public int getNbElementsInBag() {
        return agent.getNbElementsInBag();
    }

    @Override
    public boolean isBagEmpty() {
        return agent.isBagEmpty();
    }

    @Override
    public boolean isBagFull() {
        return agent.isBagFull();
    }

    @Override
    public double getViewDirection() {
        return agent.getViewDirection();
    }

    @Override
    public void setViewDirection(double viewDirection) {
        agent.setViewDirection(viewDirection);
    }

    @Override
    public ArrayList<WarAgentPercept> getPerceptsAllies() {
        return agent.getPerceptsAllies();
    }

    @Override
    public ArrayList<WarAgentPercept> getPerceptsEnemies() {
        return agent.getPerceptsEnemies();
    }

    @Override
    public ArrayList<WarAgentPercept> getPerceptsResources() {
        return agent.getPerceptsResources();
    }

    @Override
    public ArrayList<WarAgentPercept> getPerceptsAlliesByType(WarAgentType agentType) {
        return agent.getPerceptsAlliesByType(agentType);
    }

    @Override
    public ArrayList<WarAgentPercept> getPerceptsEnemiesByType(WarAgentType agentType) {
        return agent.getPerceptsEnemiesByType(agentType);
    }

    @Override
    public ArrayList<WarAgentPercept> getPercepts() {
        return agent.getPercepts();
    }

    @Override
    public ArrayList<WallPercept> getWallPercepts() {
        return agent.getWallPercepts();
    }

    @Override
    public String getDebugString() {
        return agent.getDebugString();
    }

    @Override
    public void setDebugString(String debugString) {
        agent.setDebugString(debugString);
    }

    @Override
    public Color getDebugStringColor() {
        return agent.getDebugStringColor();
    }

    @Override
    public void setDebugStringColor(Color color) {
        agent.setDebugStringColor(color);
    }

    @Override
    public Shape getDebugShape() {
        return agent.getDebugShape();
    }

    @Override
    public void setDebugShape(Shape debugShape) {
        agent.setDebugShape(debugShape);
    }

    @Override
    public CoordPolar getAveragePositionOfUnitInPercept(WarAgentType agentType, boolean ally) {
        return agent.getAveragePositionOfUnitInPercept(agentType, ally);
    }

    @Override
    public CoordPolar getIndirectPositionOfAgentWithMessage(WarMessage message) {
        return agent.getIndirectPositionOfAgentWithMessage(message);
    }

    @Override
    public CoordPolar getTargetedAgentPosition(double angleToAlly, double distanceFromAlly, double angleFromAllyToTarget, double distanceBetweenAllyAndTarget) {
        return agent.getTargetedAgentPosition(angleToAlly, distanceFromAlly, angleFromAllyToTarget, distanceBetweenAllyAndTarget);
    }

    @Override
    public int getHealth() {
        return agent.getHealth();
    }

    @Override
    public int getMaxHealth() {
        return agent.getMaxHealth();
    }

    @Override
    public AbstractAgent.ReturnCode requestRole(String group, String role) {
        return agent.requestRole(group, role);
    }

    @Override
    public AbstractAgent.ReturnCode leaveRole(String group, String role) {
        return agent.leaveRole(group, role);
    }

    @Override
    public AbstractAgent.ReturnCode leaveGroup(String group) {
        return agent.leaveGroup(group);
    }

    @Override
    public int getNumberOfAgentsInRole(String group, String role) {
        return agent.getNumberOfAgentsInRole(group, role);
    }

    @Override
    public double getHeading() {
        return agent.getHeading();
    }

    @Override
    public void setHeading(double angle) {
        agent.setHeading(angle);
    }

    @Override
    public void setRandomHeading() {
        agent.setRandomHeading();
    }

    @Override
    public void setRandomHeading(int range) {
        agent.setRandomHeading(range);
    }

    @Override
    public String getTeamName() {
        return agent.getTeamName();
    }

    @Override
    public boolean isEnemy(WarAgentPercept percept) {
        return agent.isEnemy(percept);
    }

    @Override
    public int getID() {
        return agent.getID();
    }
}
