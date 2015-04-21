package edu.warbot.game;

import edu.warbot.FSMEditor.models.Model;
import edu.warbot.agents.*;
import edu.warbot.agents.agents.WarBase;
import edu.warbot.agents.buildings.Wall;
import edu.warbot.agents.enums.WarAgentType;
import edu.warbot.brains.WarBrain;
import edu.warbot.brains.capacities.Builder;
import edu.warbot.brains.capacities.Creator;
import edu.warbot.communications.WarKernelMessage;
import edu.warbot.gui.launcher.WarLauncherInterface;
import edu.warbot.tools.WarMathTools;
import edu.warbot.tools.geometry.CoordCartesian;
import edu.warbot.tools.geometry.CoordPolar;

import javax.swing.*;
import java.awt.*;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;

public class Team {

    public static final String DEFAULT_GROUP_NAME = "defaultGroup-Warbot";
    public static int MAX_DYING_STEP = 5;
    private List<TeamListener> listeners;

    private String _name;
    private ImageIcon _teamLogo;
    private Color _color;
    private String _description;
    private HashMap<String, Class<? extends WarBrain>> _brainControllers;
    private ArrayList<ControllableWarAgent> _controllableAgents;
    private ArrayList<WarProjectile> _projectiles;
    private ArrayList<WarBuilding> _buildings;
    private HashMap<WarAgentType, Integer> _nbUnitsLeft;
    private ArrayList<WarAgent> _dyingAgents;
    private WarGame game;
    private Model fsmModel;
    private boolean hasLost;

    public Team(String nom) {
        this(nom, Color.WHITE, null, "", new ArrayList<ControllableWarAgent>(), new ArrayList<WarProjectile>(), new ArrayList<WarBuilding>(),
                new HashMap<String, Class<? extends WarBrain>>(), new HashMap<WarAgentType, Integer>(), new ArrayList<WarAgent>(), null);
        for (WarAgentType type : WarAgentType.values())
            _nbUnitsLeft.put(type, 0);
    }

    public Team(String nom, Color color, ImageIcon logo, String description, ArrayList<ControllableWarAgent> controllableAgents, ArrayList<WarProjectile> projectiles, ArrayList<WarBuilding> buildings,
                HashMap<String, Class<? extends WarBrain>> brainControllers, HashMap<WarAgentType, Integer> nbUnitsLeft, ArrayList<WarAgent> dyingAgents, Model fsmModel) {
        _name = nom;
        _color = color;
        _teamLogo = logo;
        _description = description;
        _controllableAgents = controllableAgents;
        _projectiles = projectiles;
        _buildings = buildings;
        _brainControllers = brainControllers;
        _nbUnitsLeft = nbUnitsLeft;
        _dyingAgents = dyingAgents;

        listeners = new ArrayList<>();
        hasLost = false;

        this.fsmModel = fsmModel;
    }

    public Team(String nom, Color color, ImageIcon logo, String description, ArrayList<ControllableWarAgent> controllableAgents, ArrayList<WarProjectile> projectiles, ArrayList<WarBuilding> buildings,
                HashMap<String, Class<? extends WarBrain>> brainControllers, HashMap<WarAgentType, Integer> nbUnitsLeft, ArrayList<WarAgent> dyingAgents) {
        _name = nom;
        _color = color;
        _teamLogo = logo;
        _description = description;
        _controllableAgents = controllableAgents;
        _projectiles = projectiles;
        _buildings = buildings;
        _brainControllers = brainControllers;
        _nbUnitsLeft = nbUnitsLeft;
        _dyingAgents = dyingAgents;

        listeners = new ArrayList<>();
        hasLost = false;
    }

    public static String getRealNameFromTeamName(String teamName) {
        if (teamName.endsWith(WarLauncherInterface.TEXT_ADDED_TO_DUPLICATE_TEAM_NAME))
            return teamName.substring(0, teamName.lastIndexOf(WarLauncherInterface.TEXT_ADDED_TO_DUPLICATE_TEAM_NAME));
        else
            return teamName;
    }

    public void setLogo(ImageIcon logo) {
        _teamLogo = logo;
    }

    public void addBrainControllerClassForAgent(String agent, Class<? extends WarBrain> brainController) {
        _brainControllers.put(agent, brainController);
    }

    public Class<? extends WarBrain> getBrainOfAgent(String agentName) {
        return this._brainControllers.get(agentName);
    }

    public HashMap<String, Class<? extends WarBrain>> getAllBrainControllers() {
        return _brainControllers;
    }

    public ImageIcon getImage() {
        return _teamLogo;
    }

    public String getName() {
        return _name;
    }

    public void setName(String newName) {
        _name = newName;
    }

    public String getDescription() {
        return _description;
    }

    public void setDescription(String description) {
        _description = description;
    }

    public void addWarAgent(WarAgent agent) {
        WarAgentType type = WarAgentType.valueOf(agent.getClass().getSimpleName());
        _nbUnitsLeft.put(type, _nbUnitsLeft.get(type) + 1);
        if (agent instanceof WarProjectile)
            _projectiles.add((WarProjectile) agent);
        else if (agent instanceof WarBuilding)
            _buildings.add((WarBuilding) agent);
        else if (agent instanceof ControllableWarAgent)
            _controllableAgents.add((ControllableWarAgent) agent);
        agent.getLogger().log(Level.FINEST, agent.toString() + " added to team " + this.getName());

        for (TeamListener listener : getListeners())
            listener.onAgentAdded(agent);
    }

    public ArrayList<ControllableWarAgent> getControllableAgents() {
        return _controllableAgents;
    }

    public ArrayList<WarProjectile> getProjectiles() {
        return _projectiles;
    }

    public ArrayList<WarBuilding> getBuildings() {
        return _buildings;
    }

    public void removeWarAgent(WarAgent agent) {
        WarAgentType type = WarAgentType.valueOf(agent.getClass().getSimpleName());
        _nbUnitsLeft.put(type, _nbUnitsLeft.get(type) - 1);
        if (agent instanceof WarProjectile)
            _projectiles.remove(agent);
        else if (agent instanceof WarBuilding)
            _buildings.remove(agent);
        else
            _controllableAgents.remove(agent);

        for (TeamListener listener : getListeners())
            listener.onAgentRemoved(agent);
    }

    public void setWarAgentAsDying(WarAgent agent) {
        removeWarAgent(agent);
        _dyingAgents.add(agent);
    }

    public ArrayList<WarAgent> getAllAgents() {
        ArrayList<WarAgent> toReturn = new ArrayList<>();
        toReturn.addAll(_controllableAgents);
        toReturn.addAll(_projectiles);
        toReturn.addAll(_buildings);
        return toReturn;
    }

    public void removeAllAgents() {
        _controllableAgents.clear();
        _projectiles.clear();
        _buildings.clear();
        _dyingAgents.clear();
        for (WarAgentType type : WarAgentType.values())
            _nbUnitsLeft.put(type, 0);
    }

    /**
     * Retourne l'agent dont l'id est celui passé en paramètre
     *
     * @param id - id de l'agent à récupérer
     * @return null si aucun agent n'a été trouvé
     */
    public WarAgent getAgentWithID(int id) {
        for (WarAgent a : getAllAgents()) {
            if (a.getID() == id)
                return a;
        }
        return null;
    }

    public void sendMessageToAllMembers(ControllableWarAgent sender, String message, String[] content) {
        // A savoir que Madkit exclut la possibilité qu'un agent s'envoie un message à lui-même, nous ne faisons donc pas le test ici
        for (WarAgent a : _controllableAgents) {
            sender.sendMessage(a.getAgentAddressIn(getName(), DEFAULT_GROUP_NAME, a.getClass().getSimpleName()),
                    new WarKernelMessage(sender, message, content));
        }
    }

    @Override
    public boolean equals(Object team) {
        if (team instanceof Team)
            return this.getName().equals(((Team) team).getName());
        else if (team instanceof String)
            return this.getName().equals((String) team);
        else
            return false;
    }

    public ArrayList<WarAgent> getAllAgentsInRadiusOf(WarAgent referenceAgent, double radius) {
        ArrayList<WarAgent> toReturn = new ArrayList<>();
        for (WarAgent a : getAllAgents()) {
            if (referenceAgent.getMinDistanceFrom(a) <= radius) {
                toReturn.add(a);
            }
        }
        return toReturn;
    }

    public ArrayList<WarAgent> getAllAgentsInRadius(double posX, double posY, double radius) {
        ArrayList<WarAgent> toReturn = new ArrayList<>();
        for (WarAgent a : getAllAgents()) {
            if ((WarMathTools.getDistanceBetweenTwoPoints(posX, posY, a.getX(), a.getY()) - a.getHitboxMaxRadius()) <= radius) {
                toReturn.add(a);
            }
        }
        return toReturn;
    }

    public ArrayList<WarBuilding> getBuildingsInRadiusOf(WarAgent referenceAgent, double radius) {
        ArrayList<WarBuilding> toReturn = new ArrayList<>();
        for (WarBuilding building : _buildings) {
            if (referenceAgent.getMinDistanceFrom(building) <= radius) {
                toReturn.add(building);
            }
        }
        return toReturn;
    }

    public Color getColor() {
        return _color;
    }

    public void setColor(Color color) {
        this._color = color;
    }

    public int getNbUnitsLeftOfType(WarAgentType type) {
        return _nbUnitsLeft.get(type);
    }

    public HashMap<WarAgentType, Integer> getAllNbUnitsLeft() {
        return _nbUnitsLeft;
    }

    public void killAllAgents() {
        for (WarAgent a : getAllAgents())
            a.killAgent(a);
    }

    public Team duplicate(String newName) {
        return new Team(newName,
                ((this.getColor() == null) ? null : (new Color(this.getColor().getRGB()))),
                this.getImage(),
                this.getDescription(),
                new ArrayList<>(this.getControllableAgents()),
                new ArrayList<>(this.getProjectiles()),
                new ArrayList<>(this.getBuildings()),
                new HashMap<>(this.getAllBrainControllers()),
                new HashMap<>(this.getAllNbUnitsLeft()),
                new ArrayList<>(this.getDyingAgents()),
                this.getFSMModel()
        );
    }

    public void doAfterEachTick() {
        for (int i = 0; i < _dyingAgents.size(); i++) {
            WarAgent a = _dyingAgents.get(i);
            if (a.getDyingStep() == 0)
                a.killAgent(a);
            a.incrementDyingStep();
            if (a.getDyingStep() > MAX_DYING_STEP) {
                _dyingAgents.remove(i);
            }
        }
    }

    public ArrayList<WarAgent> getDyingAgents() {
        return new ArrayList<>(_dyingAgents);
    }

    public WarGame getGame() {
        return game;
    }

    public void setGame(WarGame game) {
        this.game = game;
    }

    public ControllableWarAgent instantiateNewControllableWarAgent(String agentName) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, ClassNotFoundException {
        String agentToCreateClassName = WarBase.class.getPackage().getName() + "." + agentName;
        Class<? extends WarBrain> brainClass = getBrainOfAgent(agentName);
        ControllableWarAgent a = (ControllableWarAgent) Class.forName(agentToCreateClassName)
                .getConstructor(Team.class, brainClass.getSuperclass().getSuperclass())
                .newInstance(this, brainClass.newInstance());

        // TODO
//		if(a.getBrain() instanceof WarFSMBrainController){
//			WarAgentType agentType = WarAgentType.valueOf(agentName);
//			//Intancie la fsm et la donne comme brain à l'agent
//			ControllableWarAgentAdapter adapter = a.getBrain().getAgent();
//
//			if(getFSMModel() == null)
//				System.out.println();
//
//			FSMInstancier<ControllableWarAgentAdapter> fsmInstancier = new FSMInstancier(getFSMModel());
//			WarFSM warFsm = fsmInstancier.getBrainControleurForAgent(agentType, adapter);
//			WarFSMBrainController fsmBrainController = (WarFSMBrainController)a.getBrain();
//			fsmBrainController.setFSM(warFsm);
//		}

        return a;
    }

    public WarBuilding instantiateNewBuilding(String buildingName) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, ClassNotFoundException {
        String buildingToCreateClassName = Wall.class.getPackage().getName() + "." + buildingName;
        WarBuilding building = (WarBuilding) Class.forName(buildingToCreateClassName)
                .getConstructor(Team.class)
                .newInstance(this);

        return building;
    }

    public Model getFSMModel() {
        return this.fsmModel;
    }

    public void setFsmModel(Model fsmModel) {
        this.fsmModel = fsmModel;
    }

    public void createUnit(Creator creatorAgent, WarAgentType agentTypeToCreate) {
        if (creatorAgent instanceof AliveWarAgent) {
            try {
                ((AliveWarAgent) creatorAgent).getLogger().log(Level.FINEST, creatorAgent.toString() + " creating " + agentTypeToCreate);
                if (creatorAgent.isAbleToCreate(agentTypeToCreate)) {
                    ControllableWarAgent a = instantiateNewControllableWarAgent(agentTypeToCreate.toString());
                    if (a.getCost() < ((AliveWarAgent) creatorAgent).getHealth()) {
                        ((AliveWarAgent) creatorAgent).launchAgent(a);
                        a.setPositionAroundOtherAgent(((AliveWarAgent) creatorAgent));
                        ((AliveWarAgent) creatorAgent).damage(a.getCost());
                        ((AliveWarAgent) creatorAgent).getLogger().log(Level.FINER, creatorAgent.toString() + " create " + agentTypeToCreate);
                    } else {
                        ((AliveWarAgent) creatorAgent).getLogger().log(Level.FINER, creatorAgent.toString() + " can't create " + agentTypeToCreate + " : not enough health !");
                    }
                } else {
                    ((AliveWarAgent) creatorAgent).getLogger().log(Level.FINER, creatorAgent.toString() + " isn't able to create " + agentTypeToCreate);
                }
            } catch (Exception e) {
                System.err.println("Erreur lors de l'instanciation du brainController de l'agent " + agentTypeToCreate.toString());
                e.printStackTrace();
            }
        }
    }

    public void build(Builder builderAgent, WarAgentType buildingTypeToBuild) {
        if (builderAgent instanceof AliveWarAgent) {
            try {
                ((AliveWarAgent) builderAgent).getLogger().log(Level.FINEST, builderAgent.toString() + " building " + buildingTypeToBuild);
                if (builderAgent.isAbleToBuild(buildingTypeToBuild)) {
                    WarBuilding building = instantiateNewBuilding(buildingTypeToBuild.toString());
                    if (building.getCost() < ((AliveWarAgent) builderAgent).getHealth()) {
                        ((AliveWarAgent) builderAgent).launchAgent(building);

                        // Position
                        CoordCartesian newBuildingPosition = WarMathTools.addTwoPoints(((AliveWarAgent) builderAgent).getPosition(), new CoordPolar(WarBuilding.MAX_DISTANCE_BUILD, ((AliveWarAgent) builderAgent).getHeading()));
                        for (WarAgent agent : getAllAgentsInRadiusOf(building, building.getHitboxMaxRadius())) {
                            agent.moveOutOfCollision();
                        }
                        building.setPosition(newBuildingPosition);
                        building.setHeading(((AliveWarAgent) builderAgent).getHeading());

                        // Cost
                        ((AliveWarAgent) builderAgent).damage(building.getCost());
                        ((AliveWarAgent) builderAgent).getLogger().log(Level.FINER, builderAgent.toString() + " built " + buildingTypeToBuild);
                    } else {
                        ((AliveWarAgent) builderAgent).getLogger().log(Level.FINER, builderAgent.toString() + " can't build " + buildingTypeToBuild + " : not enough health !");
                    }
                } else {
                    ((AliveWarAgent) builderAgent).getLogger().log(Level.FINER, builderAgent.toString() + " can't build " + buildingTypeToBuild);
                }
            } catch (Exception e) {
                System.err.println("Erreur lors de l'instanciation du brainController de l'agent " + buildingTypeToBuild.toString());
                e.printStackTrace();
            }
        }
    }

    public void addTeamListener(TeamListener teamListener) {
        listeners.add(teamListener);
    }

    public void removeTeamListener(TeamListener teamListener) {
        listeners.remove(teamListener);
    }

    private List<TeamListener> getListeners() {
        return listeners;
    }

    public void setHasLost(boolean hasLost) {
        this.hasLost = hasLost;
    }

    public boolean hasLost() {
        return hasLost;
    }
}
