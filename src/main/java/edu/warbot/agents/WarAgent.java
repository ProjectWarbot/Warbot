package edu.warbot.agents;

import edu.warbot.agents.actions.MovableActionsMethods;
import edu.warbot.agents.percepts.WarAgentPercept;
import edu.warbot.brains.capacities.CommonCapacities;
import edu.warbot.brains.capacities.Movable;
import edu.warbot.game.Team;
import edu.warbot.maps.AbstractWarMap;
import edu.warbot.tools.WarMathTools;
import edu.warbot.tools.geometry.CoordCartesian;
import edu.warbot.tools.geometry.CoordPolar;
import edu.warbot.tools.geometry.GeometryTools;
import edu.warbot.tools.geometry.WarCircle;
import madkit.kernel.AbstractAgent;
import turtlekit.kernel.Turtle;

import java.awt.geom.Area;

public abstract class WarAgent extends Turtle implements CommonCapacities {

    private static final int MAP_MARGINS = 2;

	private Hitbox hitbox;
	private Team _team;
	private int _dyingStep;

	public WarAgent(String firstActionToDo, Team team, Hitbox hitbox) {
		super(firstActionToDo);
		this._team = team;
        this.hitbox = hitbox;
		_dyingStep = 0;
	}

	@Override
	protected void activate() {
		super.activate();
		getTeam().addWarAgent(this);
		requestRole(Team.DEFAULT_GROUP_NAME, getClass().getSimpleName());
	}

	@Override
	protected void end() {
		super.end();
	}

    public abstract void kill();

    protected void doBeforeEachTick() { }

	@Override
	public AbstractAgent.ReturnCode requestRole(String group, String role) {
		createGroupIfAbsent(getTeam().getName(), group);
		return requestRole(getTeam().getName(), group, role);
	}
	
	@Override
	public AbstractAgent.ReturnCode leaveRole(String group, String role) {
		return super.leaveRole(getTeam().getName(), group, role);
	}

	@Override
	public AbstractAgent.ReturnCode leaveGroup(String group) {
		return super.leaveGroup(getTeam().getName(), group);
	}

	@Override
	public int getNumberOfAgentsInRole(String group, String role) {
		return (getAgentsWithRole(getTeam().getName(), group, role).size());
	}

	public Team getTeam() {
		return _team;
	}
	
	@Override
	public String getTeamName() {
		return getTeam().getName();
	}
	
	@Override
	public boolean isEnemy(WarAgentPercept percept) {
		return ! percept.getTeamName().equals(getTeamName());
	}

	public Hitbox getHitbox() {
		return hitbox;
	}

    public Area getActualForm() {
        return getActualFormAtPosition(getX(), getY());
    }

    public Area getActualFormAtPosition(double x, double y) {
        return new Area(GeometryTools.moveToAndRotateShape(getHitbox().getShape(), x - (getHitbox().getWidth() / 2.), y - (getHitbox().getHeight() / 2.), getHeading()));
    }

    public double getHitboxMinRadius() {
        return Math.min(getHitbox().getHeight(), getHitbox().getWidth()) / 2.;
    }

    public double getHitboxMaxRadius() {
        return Math.max(getHitbox().getHeight(), getHitbox().getWidth()) / 2.;
    }

    public String toString() {
		return "[" + getID() + "] " + getClass().getSimpleName() + " (" + getTeam().getName() + ")";
	}
	
	@Override
	public void setRandomHeading() {
		randomHeading();
	}
	
	@Override
	public void setRandomHeading(int range) {
		randomHeading(range);
	}

	protected boolean isGoingToBeOutOfMap() {

		CoordCartesian nextPos = new CoordCartesian(getX(), getY());
		if (this instanceof MovableActionsMethods)
			nextPos.add(new CoordPolar(((Movable) this).getSpeed(), getHeading()).toCartesian());

        return ! getTeam().getGame().getMap().getMapAccessibleArea().contains(nextPos.getX(), nextPos.getY());
	}

	protected boolean isGoingToBeOverAnOtherAgent() {
		CoordCartesian futurePosition = getPosition();
		double searchAreaRadius = getHitboxMaxRadius() * 4.;
        if (this instanceof MovableActionsMethods) {
			searchAreaRadius += ((Movable) this).getSpeed();
			futurePosition = WarMathTools.addTwoPoints(new CoordCartesian(getX(), getY()), new CoordPolar(((Movable) this).getSpeed(), getHeading()));
		}
		for(WarAgent a : getTeam().getGame().getAllAgentsInRadius(futurePosition.getX(), futurePosition.getY(), searchAreaRadius)) {
			if (a.getID() != getID() && a instanceof AliveWarAgent) {
				if (this instanceof Movable) {
					return isInCollisionWithAtPosition(futurePosition, a);
				} else
					return isInCollisionWith(a);
			}
		}
		return false;
	}

	protected boolean isInCollisionWith(WarAgent agent) {
        Area currentAgentArea = new Area(getActualForm());
        Area agentArea = new Area(agent.getActualForm());
        agentArea.intersect(currentAgentArea);
        return ! agentArea.isEmpty();
	}

	protected boolean isInCollisionWithAtPosition(CoordCartesian pos, WarAgent agent) {
        Area currentAgentArea = new Area(getActualFormAtPosition(pos.getX(), pos.getY()));
        Area agentArea = new Area(agent.getActualForm());
        agentArea.intersect(currentAgentArea);
        return ! agentArea.isEmpty();
	}
	
	public void setPositionAroundOtherAgent(WarAgent agent) {
        setPosition(agent.getPosition());
        moveOutOfCollision();
	}

	public void setRandomPositionInCircle(CoordCartesian centerPoint, double radius) {
		setPosition(WarMathTools.addTwoPoints(centerPoint, CoordPolar.getRandomInBounds(radius).toCartesian()));
        moveOutOfCollision();
	}
	
	public void setRandomPositionInCircle(WarCircle circle) {
		setRandomPositionInCircle(new CoordCartesian(circle.getCenterX(), circle.getCenterY()), circle.getRadius());
	}
	
	public CoordCartesian getPosition() {
		return new CoordCartesian(getX(), getY());
	}
	
	public void setPosition(double a, double b) {
		super.setXY(a, b);
	}
	
	public void setPosition(CoordCartesian pos) {
		setPosition(pos.getX(), pos.getY());
	}

    public void moveOutOfCollision() {
        boolean isPositionChanged = false;

        // Test si l'agent est hors carte
        CoordCartesian agentPosition = getPosition();
        AbstractWarMap map = getTeam().getGame().getMap();
        if (! map.getMapAccessibleArea().contains(agentPosition.getX(), agentPosition.getY())) {
            CoordPolar moveToMapCenter = new CoordPolar(1, agentPosition.getAngleToPoint(new CoordCartesian(map.getCenterX(), map.getCenterY())));
            do {
                agentPosition.add(moveToMapCenter.toCartesian());
            } while(! map.getMapAccessibleArea().contains(agentPosition.getX(), agentPosition.getY()));
            setPosition(agentPosition);
            isPositionChanged = true;
        }

        // Test de collision avec un autre agent
        double searchAreaRadius = getHitboxMaxRadius();
        for(WarAgent a : getTeam().getGame().getAllAgentsInRadius(getX(), getY(), searchAreaRadius)) {
            if (a.getID() != getID() && a instanceof AliveWarAgent) {
                if (isInCollisionWith(a)) {
                    agentPosition = getPosition();
                    CoordPolar moveAwayFromAgent = new CoordPolar(1, a.getPosition().getAngleToPoint(getPosition()));
                    do {
                        agentPosition.add(moveAwayFromAgent.toCartesian());
                    } while (isInCollisionWithAtPosition(agentPosition, a)); // On boucle tant que l'agent est en collision avec l'autre agent
                    // Une fois que l'agent hors collision, on lui assigne sa nouvelle position
                    setPosition(agentPosition);
                    isPositionChanged = true;
                }
            }
        }

        // Pour finir, s'il y a eu une correction de la position, on rappelle la fonction pour être bien sûr qu'il n'y a plus de collision
        if (isPositionChanged)
            moveOutOfCollision();
    }

	/**
	 * @return Le nombre de tick passés depuis la mort de l'agent
	 */
	public int getDyingStep() {
		return _dyingStep;
	}
	
	public void incrementDyingStep() {
		_dyingStep++;
	}
	
	public double getAverageDistanceFrom(WarAgent a) {
		return WarMathTools.getDistanceBetweenTwoPoints(getX(), getY(), a.getX(), a.getY())
				- ((getHitboxMaxRadius() + getHitboxMinRadius()) / 2.) - ((a.getHitboxMaxRadius() + a.getHitboxMinRadius()) / 2.);
	}

    public double getMinDistanceFrom(WarAgent a) {
        return WarMathTools.getDistanceBetweenTwoPoints(getX(), getY(), a.getX(), a.getY())
                - getHitboxMaxRadius() - a.getHitboxMaxRadius();
    }

}
