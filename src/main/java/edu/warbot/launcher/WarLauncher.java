package edu.warbot.launcher;

import edu.warbot.agents.WarAgent;
import edu.warbot.agents.enums.WarAgentType;
import edu.warbot.game.MotherNatureTeam;
import edu.warbot.game.Team;
import edu.warbot.game.WarGame;
import edu.warbot.game.WarGameSettings;
import edu.warbot.gui.viewer.WarDefaultViewer;
import edu.warbot.launcher.WarMain.Shared;
import edu.warbot.maps.AbstractWarMap;
import edu.warbot.tools.geometry.WarCircle;
import madkit.action.SchedulingAction;
import madkit.kernel.Madkit;
import madkit.message.SchedulingMessage;
import turtlekit.agr.TKOrganization;
import turtlekit.kernel.TKLauncher;
import turtlekit.kernel.TurtleKit;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import static turtlekit.kernel.TurtleKit.Option.launcher;

public class WarLauncher extends TKLauncher {

    public WarLauncher() {
        super();
    }

    @Override
    protected void activate() {
        super.activate();
        setMadkitProperty("GPU_gradients", "true");
    }

    @Override
    protected void createSimulationInstance() {
        WarGameSettings settings = Shared.getGame().getSettings();
        setLogLevel(settings.getLogLevel());
        setMadkitProperty(Madkit.LevelOption.agentLogLevel, settings.getLogLevel().toString());
        setMadkitProperty(Madkit.LevelOption.guiLogLevel, settings.getLogLevel().toString());
        setMadkitProperty(Madkit.LevelOption.kernelLogLevel, settings.getLogLevel().toString());
        setMadkitProperty(Madkit.LevelOption.madkitLogLevel, settings.getLogLevel().toString());
        setMadkitProperty(Madkit.LevelOption.networkLogLevel, settings.getLogLevel().toString());

        initProperties();
        setMadkitProperty(TurtleKit.Option.envWidth, String.valueOf(((Double) Shared.getGame().getMap().getWidth()).intValue()));
        setMadkitProperty(TurtleKit.Option.envHeight, String.valueOf(((Double) Shared.getGame().getMap().getHeight()).intValue()));

        setMadkitProperty(TurtleKit.Option.viewers, WarDefaultViewer.class.getName());
        setMadkitProperty(TurtleKit.Option.scheduler, WarScheduler.class.getName());
        setMadkitProperty(TurtleKit.Option.environment, WarEnvironment.class.getName());

        super.createSimulationInstance();

        if (settings.getSituationLoader() == null)
            launchAllAgents();
        else
            settings.getSituationLoader().launchAllAgentsFromXmlSituationFile(this);

        // Puis on lance la simulation
        sendMessage(getMadkitProperty(turtlekit.kernel.TurtleKit.Option.community),
                TKOrganization.ENGINE_GROUP, TKOrganization.SCHEDULER_ROLE, new SchedulingMessage(SchedulingAction.RUN));


        Shared.getGame().setGameStarted();
    }

    public void executeLauncher(String... args) {
        final ArrayList<String> arguments = new ArrayList<>(Arrays.asList(
                Madkit.BooleanOption.desktop.toString(), "false",
                Madkit.Option.configFile.toString(), "turtlekit/kernel/turtlekit.properties",
                launcher.toString(), WarLauncher.class.getName()));
        if (args != null) {
            arguments.addAll(Arrays.asList(args));
        }
        new Madkit(arguments.toArray(new String[0]));
    }

    private void launchAllAgents() {
        WarGame game = Shared.getGame();
        ArrayList<Team> playerTeams = game.getPlayerTeams();
        AbstractWarMap map = game.getMap();
        ArrayList<ArrayList<WarCircle>> teamsPositions = map.getTeamsPositions();
        int teamCount = 0;
        MotherNatureTeam motherNatureTeam = game.getMotherNatureTeam();

        try {
            int compteur;
            for (Team t : playerTeams) {
                // On sélectionne aléatoirement la position de l'équipe depuis les différentes possibilités
                WarCircle selectedPosition = teamsPositions.get(teamCount).get(new Random().nextInt(teamsPositions.get(teamCount).size()));
                for (WarAgentType agentType : WarAgentType.values()) {
                    for (compteur = 0; compteur < game.getSettings().getNbAgentOfType(agentType); compteur++) {
                        try {
                            WarAgent agent = t.instantiateNewControllableWarAgent(agentType.toString());
                            launchAgent(agent);
                            agent.setRandomPositionInCircle(selectedPosition);
                        } catch (InvocationTargetException | NoSuchMethodException | ClassNotFoundException e) {
                            System.err.println("Erreur lors de l'instanciation de l'agent. Type non reconnu : " + agentType);
                            e.printStackTrace();
                        }
                        // On créé autant de WarFood que d'agent au départ
                        motherNatureTeam.createAndLaunchNewResource(game.getMap(), this, WarAgentType.WarFood);
                    }
                }
                teamCount++;
            }
        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | SecurityException e) {
            System.err.println("Erreur lors de l'instanciation des classes à partir des données XML");
            e.printStackTrace();
        }
    }
}
