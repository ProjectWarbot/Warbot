package edu.warbot.agents;

import edu.warbot.agents.actions.ControllableActionsMethods;
import edu.warbot.agents.enums.WarAgentType;
import edu.warbot.agents.percepts.PerceptsGetter;
import edu.warbot.agents.percepts.WallPercept;
import edu.warbot.agents.percepts.WarAgentPercept;
import edu.warbot.agents.resources.WarFood;
import edu.warbot.brains.WarBrain;
import edu.warbot.brains.capacities.Controllable;
import edu.warbot.brains.implementations.AgentBrainImplementer;
import edu.warbot.brains.implementations.WarBrainImplementation;
import edu.warbot.communications.WarKernelMessage;
import edu.warbot.communications.WarMessage;
import edu.warbot.game.Team;
import edu.warbot.tools.WarMathTools;
import edu.warbot.tools.geometry.CoordCartesian;
import edu.warbot.tools.geometry.CoordPolar;
import madkit.kernel.Message;

import java.awt.*;
import java.util.ArrayList;
import java.util.Random;
import java.util.logging.Level;

public abstract class ControllableWarAgent extends AliveWarAgent implements ControllableActionsMethods, Controllable {

	private double _angleOfView;
	private double _distanceOfView;
	private int _nbElementsInBag;
	private int _bagSize;
	private int _idNextAgentToGive;
	private double _viewDirection;
	private WarBrain _brain;
	private PerceptsGetter _perceptsGetter;
	private String _debugString;
	private Color _debugStringColor;
    private Shape debugShape;
    private ArrayList<WarMessage> thisTickMessages;

	public ControllableWarAgent(String firstActionToDo, Team team, Hitbox hitbox, WarBrain brain, double distanceOfView, double angleOfView, int cost, int maxHealth, int bagSize) {
		super(firstActionToDo, team, hitbox, cost, maxHealth);

		_distanceOfView = distanceOfView;
		_angleOfView = angleOfView;
		_bagSize = bagSize;
		_nbElementsInBag = 0;
		_viewDirection = 180 * new Random().nextDouble();
		_debugString = "";
		_debugStringColor = Color.BLACK;

        _brain = brain;
        if(_brain instanceof AgentBrainImplementer)
            ((AgentBrainImplementer) _brain).setAgent(this);
    }

	@Override
	protected void activate() {
        super.activate();
        _perceptsGetter = getTeam().getGame().getSettings().getPerceptsGetterNewInstance(this, getTeam().getGame());
		randomHeading();
        _brain.activate();
    }
	
	@Override
	protected void doBeforeEachTick() {
		super.doBeforeEachTick();
		_perceptsGetter.setPerceptsOutdated(); // On indique au PerceptGetter qu'un nouveau tick est passé
        thisTickMessages = null;
	}

	@Override
	public String give() {
		logger.log(Level.FINEST, this.toString() + " giving...");
		if(getNbElementsInBag() > 0) { // Si l'agent courant a quelque chose à donner
			WarAgent agentToGive = getTeam().getAgentWithID(_idNextAgentToGive);
			if (agentToGive != null) { // Si agent existe
				if (getAverageDistanceFrom(agentToGive) <= MAX_DISTANCE_GIVE) { // Si il n'est pas trop loin
					if (agentToGive instanceof ControllableWarAgent) { // Si c'est un ControllableWarAgent
						if (((ControllableWarAgent) agentToGive).getBagSize() > ((ControllableWarAgent) agentToGive).getNbElementsInBag()) { // Si son sac a un emplacement vide
							logger.log(Level.FINER, this.toString() + " give WarFood to " + agentToGive.toString());
							((ControllableWarAgent) agentToGive).addElementInBag();
							_nbElementsInBag--;
						}
					}
				}
			}
		}
		return getBrain().action();
	}

	@Override
	public String eat() {
		logger.finest(this.toString() + " eating...");
		if (getNbElementsInBag() > 0) {
			_nbElementsInBag--;
			heal(WarFood.HEALTH_GIVEN);
			logger.finer(this.toString() + " eat.");
		}
		return getBrain().action();
	}

	@Override
	public String idle() {
		logger.finer(this.toString() + " idle.");
		return getBrain().action();
	}

    public void forcePerceptsUpdate() {
        _perceptsGetter.forcePerceptsUpdate();
    }

	public WarBrain getBrain() {
		return _brain;
	}
	
	public void setBrain(WarBrain brain) {
		_brain = brain;
	}

	@Override
	public ReturnCode sendMessage(int idAgent, String message, String... content) {
		WarAgent agent = getTeam().getAgentWithID(idAgent);
		if (agent != null) {
			logger.finer(this.toString() + " send message to " + agent.toString());
			logger.finest("This message is : [" + message + "] " + content);
			return sendMessage(agent.getAgentAddressIn(getTeam().getName(), Team.DEFAULT_GROUP_NAME, agent.getClass().getSimpleName()), new WarKernelMessage(this, message, content));
		}
		return ReturnCode.INVALID_AGENT_ADDRESS;
	}

	@Override
	public ReturnCode broadcastMessageToAgentType(WarAgentType agentType, String message, String... content) {
		logger.finer(this.toString() + " send message to default role agents : " + agentType);
		logger.finest("This message is : [" + message + "] " + content);
		return sendMessage(getTeam().getName(), Team.DEFAULT_GROUP_NAME, agentType.toString(), new WarKernelMessage(this, message, content));
	}

	@Override
	public ReturnCode broadcastMessage(String groupName, String roleName, String message, String... content) {
		logger.finer(this.toString() + " send message to agents from group " + groupName + " with role " + roleName);
		logger.finest("This message is : [" + message + "] " + content);
		return sendMessage(getTeam().getName(), groupName, roleName, new WarKernelMessage(this, message, content));
	}

	@Override
	public void broadcastMessageToAll(String message, String... content) {
		logger.finer(this.toString() + " send message to all his team.");
		logger.finest("This message is : [" + message + "] " + content);
		getTeam().sendMessageToAllMembers(this, message, content);
	}

	@Override
	public ReturnCode reply(WarMessage warMessage, String message, String... content) {
		logger.log(Level.FINER, this.toString() + " send reply to " + warMessage.getSenderID());
		logger.log(Level.FINEST, "This message is : [" + message + "] " + content);
		return sendMessage(warMessage.getSenderID(), message, content);
	}

    @Override
    public ArrayList<WarMessage> getMessages() {
        if(thisTickMessages == null) {
            thisTickMessages = new ArrayList<>();
            Message msg;
            while ((msg = nextMessage()) != null) {
                if (msg instanceof WarKernelMessage) {
                    WarMessage warMsg = new WarMessage((WarKernelMessage) msg, this);
                    logger.log(Level.FINER, this.toString() + " received message from " + warMsg.getSenderID());
                    logger.log(Level.FINEST, "This message is : " + warMsg.toString());
                    thisTickMessages.add(warMsg);
                }
            }
        }
        return thisTickMessages;
    }

    public double getAngleOfView() {
		return _angleOfView;
	}
	
	public double getDistanceOfView() {
		return _distanceOfView;
	}

	@Override
	public int getNbElementsInBag() {
		return _nbElementsInBag;
	}

	@Override
	public int getBagSize() {
		return _bagSize;
	}
	
	@Override
	public boolean isBagEmpty() {
		return getNbElementsInBag() == 0;
	}
	
	@Override
	public boolean isBagFull() {
		return getNbElementsInBag() == getBagSize();
	}

	public void addElementInBag() {
		_nbElementsInBag++;
	}

	@Override
	public void setIdNextAgentToGive(int idNextAgentToGive) {
		_idNextAgentToGive = idNextAgentToGive;
	}

	@Override
	public void setViewDirection(double newViewDirection) {
		this._viewDirection = newViewDirection;
	}

	@Override
	public double getViewDirection() {
		return _viewDirection;
	}

	@Override
	public ArrayList<WarAgentPercept> getPercepts() {
		return _perceptsGetter.getPercepts();
	}

	public ArrayList<WarAgentPercept> getPercepts(boolean ally) {
		return _perceptsGetter.getWarAgentsPercepts(ally);
	}
	
	@Override
	public ArrayList<WarAgentPercept> getPerceptsAllies() {
		return getPercepts(true);
	}
	
	@Override
	public ArrayList<WarAgentPercept> getPerceptsEnemies() {
		return getPercepts(false);
	}
	
	@Override
	public ArrayList<WarAgentPercept> getPerceptsResources() {
		return _perceptsGetter.getResourcesPercepts();
	}
	
	public ArrayList<WarAgentPercept> getPerceptsByAgentType(WarAgentType agentType, boolean ally) {
		return _perceptsGetter.getPerceptsByType(agentType, ally);
	}
	
	@Override
	public ArrayList<WarAgentPercept> getPerceptsAlliesByType(WarAgentType agentType) {
		return getPerceptsByAgentType(agentType, true);
	}
	
	@Override
	public ArrayList<WarAgentPercept> getPerceptsEnemiesByType(WarAgentType agentType) {
		return getPerceptsByAgentType(agentType, false);
	}

    @Override
    public ArrayList<WallPercept> getWallPercepts() {
        return _perceptsGetter.getWallsPercepts();
    }

    public Shape getPerceptionArea() {
        return _perceptsGetter.getPerceptionArea();
    }

	/**
	 * Renvoi la position d'un agent qui n'est pas dans le percept mais qui est
	 * vu par un autre agent allié. L'agent allié doit envoyer un message aux
	 * unités qui vont pouvoir connaître la postion de l'agent ennemi.
	 *
	 * @param message
	 *            Le message reçu par l'agent qui voit l'enemi dans son
	 *            percept. Le message doit OBLIGATOIRETMENT contenir un tableau
	 *            de DEUX string : un pour la distance et l'autre pour l'angle
	 *            correspondant à la distance et l'angle que l'agent allié voit l'ennemi.
	 * @return Coordonnee polaire de l'agent ennemi perçu indirectement
	 */
	@Override
	public CoordPolar getIndirectPositionOfAgentWithMessage(WarMessage message) {
		if (message.getContent().length != 2) {
			System.out.println("ATTENTION vous devez envoyer un message avec des informations corrects dans getSecondPositionAgent");
			return null;
		}

		CoordPolar positionAllie = new CoordPolar(message.getDistance(), message.getAngle());
		CoordPolar positionEnnemi = new CoordPolar(Double.valueOf(message.getContent()[0]), Double.valueOf(message.getContent()[1]));

		CoordCartesian vecteurPositionAllie = positionAllie.toCartesian();
		CoordCartesian vecteurPositionEnemie = positionEnnemi.toCartesian();

		CoordCartesian positionfinal = new CoordCartesian(vecteurPositionAllie.getX() + vecteurPositionEnemie.getX(), 
				vecteurPositionAllie.getY() + vecteurPositionEnemie.getY());

		CoordPolar polaireFinal = positionfinal.toPolar();

		return polaireFinal;
	}

	/**
	 * Donne la position moyenne d'un groupe d'unite
	 * 
	 * @param agentType le type d'agent dont on veut connaitre la position moyenne
	 * @param ally = true, annemy = false
	 * 
	 * @return Coordonnee polaire de la position moyenne du groupe ou null si aucune unite
	 */
	@Override
	public CoordPolar getAveragePositionOfUnitInPercept(WarAgentType agentType, boolean ally) {
		ArrayList<WarAgentPercept> listPercept = this.getPerceptsByAgentType(agentType, ally);

		if (listPercept.size() == 0)
			return null;

		int nbEnemie = listPercept.size();
		CoordPolar listePolaireEnemie[] = new CoordPolar[nbEnemie];
		CoordCartesian listeVecteurEnemie[] = new CoordCartesian[nbEnemie];

		double sommeX = 0, sommeY = 0;

		for (int i = 0; i < nbEnemie; i++) {
			listeVecteurEnemie[i] = listePolaireEnemie[i].toCartesian();

			sommeX += listeVecteurEnemie[i].getX();
			sommeY += listeVecteurEnemie[i].getY();
		}

		CoordCartesian baricentre = new CoordCartesian(sommeX / nbEnemie, sommeY / nbEnemie);

		CoordPolar baricentrePolaire = baricentre.toPolar();

		return baricentrePolaire;
	}
	
	@Override
	public CoordPolar getTargetedAgentPosition(double angleToAlly, double distanceFromAlly, double angleFromAllyToTarget, double distanceBetweenAllyAndTarget) {
		return WarMathTools.addTwoPoints(new CoordPolar(distanceFromAlly, angleToAlly),
				new CoordPolar(distanceBetweenAllyAndTarget, angleFromAllyToTarget));
	}
	
	@Override
	public String getDebugString() {
		return _debugString;
	}
	
	@Override
	public void setDebugString(String debugString) {
		_debugString = debugString;
	}
	
	@Override
	public Color getDebugStringColor() {
		return _debugStringColor;
	}

	@Override
	public void setDebugStringColor(Color color) {
		_debugStringColor = color;
	}

    public Shape getDebugShape() {
        return debugShape;
    }

    public void setDebugShape(Shape debugShape) {
        this.debugShape = debugShape;
    }

    public void init(int health, int nbElementsInBag) {
		super.init(health);
		_nbElementsInBag = nbElementsInBag;
	}
}
