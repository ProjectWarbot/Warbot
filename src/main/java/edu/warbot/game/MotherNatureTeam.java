package edu.warbot.game;

import edu.warbot.agents.WarAgent;
import edu.warbot.agents.WarResource;
import edu.warbot.agents.enums.WarAgentCategory;
import edu.warbot.agents.enums.WarAgentType;
import edu.warbot.agents.resources.WarFood;
import edu.warbot.maps.AbstractWarMap;
import edu.warbot.tools.WarMathTools;
import edu.warbot.tools.geometry.CartesianCoordinates;
import edu.warbot.tools.geometry.PolarCoordinates;
import edu.warbot.tools.geometry.WarCircle;
import madkit.kernel.Agent;

import java.awt.*;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Random;

public class MotherNatureTeam extends Team {

    public static final String NAME = "Mère nature";
    public static final Color COLOR = Color.GREEN;

    private ArrayList<WarResource> _resources;

    public MotherNatureTeam(WarGame game) {
        super(NAME);
        _resources = new ArrayList<>();
        setColor(COLOR);
        setGame(game);
    }

//	public void init() {
//		Dimension mapSize = Game.getInstance().getMap().getBounds();
//		ArrayList<CartesianCoordinates> teamsPositions = Game.getInstance().getMap().getTeamsPositions(Game.getInstance().getPlayerTeams().size());
//		for (CartesianCoordinates pos : teamsPositions) {
//			for (int i = 0; i < WarConfig.getNbResourcesAreasPerTeam(); i++) {
//				CartesianCoordinates areaPos = WarMathTools.addTwoPoints(
//						pos,
//						PolarCoordinates.getRandomInBounds(WarConfig.getMaxDistanceOfResourcesAreasFromOwnerTeam())
//						);
//				areaPos.normalize(0, mapSize.width, 0, mapSize.height);
//				_resourcesAreas.add(areaPos);
//			}
//		}
//		CartesianCoordinates center = WarMathTools.getCenterOfPoints(teamsPositions);
//		for (int i = 0; i < 2; i++) {
//			CartesianCoordinates areaPos = WarMathTools.addTwoPoints(
//					center,
//					new PolarCoordinates(90, 90 + i * 180)
//					);
//			areaPos.normalize(0, mapSize.width, 0, mapSize.height);
//			_resourcesAreas.add(areaPos);
//		}
//	}

    @Override
    public void addWarAgent(WarAgent agent) {
        if (agent instanceof WarResource)
            _resources.add((WarResource) agent);
        else
            super.addWarAgent(agent);
    }

    public ArrayList<WarResource> getResources() {
        return _resources;
    }

    @Override
    public void removeWarAgent(WarAgent agent) {
        if (agent instanceof WarResource)
            _resources.remove((WarResource) agent);
        else
            super.removeWarAgent(agent);
    }

    public ArrayList<WarResource> getAccessibleResourcesFor(WarAgent referenceAgent) {
        ArrayList<WarResource> toReturn = new ArrayList<>();
        for (WarResource a : _resources) {
            if (referenceAgent.getAverageDistanceFrom(a) <= WarResource.MAX_DISTANCE_TAKE) {
                toReturn.add(a);
            }
        }
        return toReturn;
    }

    @Override
    public ArrayList<WarAgent> getAllAgents() {
        ArrayList<WarAgent> toReturn = super.getAllAgents();
        toReturn.addAll(_resources);
        return toReturn;
    }

    public void createAndLaunchNewResource(AbstractWarMap map, Agent launcher, WarAgentType resourceType) {
        if (resourceType.getCategory() == WarAgentCategory.Resource) {
            try {
                WarResource resource = instantiateNewWarResource(resourceType.toString());
                launcher.launchAgent(resource);
                ArrayList<WarCircle> foodPositions = map.getFoodPositions();
                CartesianCoordinates newPos = WarMathTools.addTwoPoints(
                        foodPositions.get(new Random().nextInt(foodPositions.size())).getCenterPosition(),
                        PolarCoordinates.getRandomInBounds(AbstractWarMap.FOOD_POSITION_RADIUS).toCartesian());
                newPos.normalize(0, map.getWidth(), 0, map.getHeight());
                resource.setPosition(newPos);
                resource.moveOutOfCollision();
            } catch (InstantiationException | IllegalAccessException | ClassNotFoundException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
                System.err.println("Erreur lors de la création d'une nouvelle ressource : " + resourceType);
                e.printStackTrace();
            }
        } else {
            System.err.println(resourceType + " n'est pas une resource.");
        }
    }

    public WarResource instantiateNewWarResource(String agentName) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, ClassNotFoundException {
        String resourceToCreateClassName = WarFood.class.getPackage().getName() + "." + agentName;
        WarResource a = (WarResource) Class.forName(resourceToCreateClassName).getConstructor(Team.class).newInstance(this);

//		a.setLogLevel(getGame().getSettings().getLogLevel());

        return a;
    }


}
