package edu.warbot.launcher;

import edu.warbot.agents.enums.WarAgentType;
import edu.warbot.agents.teams.Team;
import edu.warbot.exceptions.WarCommandException;
import edu.warbot.game.*;
import edu.warbot.gui.launcher.LoadingDialog;
import edu.warbot.gui.launcher.WarLauncherInterface;
import edu.warbot.loader.TeamLoader;
import edu.warbot.loader.situation.XMLSituationLoader;

import javax.swing.*;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

public class WarMain implements WarGameListener {

    public static final String TEAMS_DIRECTORY_NAME = "teams";
    public static final String CMD_NAME = "java WarMain";
    public static final String CMD_HELP = "--help";
    private static final String CMD_LOG_LEVEL = "--loglevel";
    private static final String CMD_NB_AGENT_OF_TYPE = "--nb";
    private static final String CMD_FOOD_APPEARANCE_RATE = "--foodrate";
    private static final String CMD_GAME_MODE = "--gamemode";
    private LoadingDialog loadingDialog;
    private WarGame game;
    private WarGameSettings settings;
    private WarLauncherInterface launcherInterface;
    private Map<String, Team> availableTeams;

    public WarMain() {
        availableTeams = new HashMap<>();
        settings = new WarGameSettings();

        // On récupère les équipes
        LoadingDialog loadDial = new LoadingDialog("Chargement des équipes...");
        loadDial.setVisible(true);

        TeamLoader tl = new TeamLoader();

        // On initialise la liste des équipes existantes dans le dossier "teams"
        availableTeams = tl.loadAvailableTeams();
        Shared.availableTeams = new HashMap<>(availableTeams);

        // On vérifie qu'au moins une équipe a été chargée
        if (availableTeams.size() > 0) {
            // On lance la launcher interface
            final WarMain warMain = this;
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    launcherInterface = new WarLauncherInterface(warMain, settings);
                    launcherInterface.setVisible(true);
                }
            });
        } else {
            JOptionPane.showMessageDialog(null, "Aucune équipe n'a été trouvé dans le dossier \"" + TEAMS_DIRECTORY_NAME + "\"",
                    "Aucune équipe", JOptionPane.ERROR_MESSAGE);
        }

        loadDial.dispose();
    }

    public WarMain(WarGameSettings settings, String... selectedTeamsName) throws WarCommandException {
        availableTeams = new HashMap<>();

        TeamLoader tl = new TeamLoader();
        // On initialise la liste des équipes existantes dans le dossier "teams"
        availableTeams = tl.loadAvailableTeams();
        Shared.availableTeams = new HashMap<>(availableTeams);

        // On vérifie qu'au moins une équipe a été chargée
        if (availableTeams.size() > 0) {
            // On lance le jeu
            this.settings = settings;
            if (selectedTeamsName.length > 1) {
                for (String teamName : selectedTeamsName) {
                    if (availableTeams.containsKey(teamName))
                        settings.addSelectedTeam(new InGameTeam(availableTeams.get(teamName)));
                    else
                        throw new WarCommandException("InGameTeam \"" + teamName + "\" does not exists. Available teams are : " + availableTeams.keySet());
                }
            } else {
                throw new WarCommandException("Please select at least two teams. Available teams are : " + availableTeams.keySet());
            }
            start();
        } else {
            throw new WarCommandException("Not team found in folder \"" + TEAMS_DIRECTORY_NAME + "\"");
        }
    }

    public static void main(String[] args) {

        if (args.length == 0) {
            //Lancement classique
            new WarMain();
        } else {
            try {
                System.out.println("Command arguments = " + Arrays.asList(args));
                commandLine(args);
            } catch (WarCommandException e) {
                System.err.println(e.getMessage());
            }
        }
    }

    private static void commandLine(String[] args) throws WarCommandException {

        WarGameSettings settings = new WarGameSettings();
        ArrayList<String> selectedTeams = new ArrayList<>();
        boolean askedForHelp = false;

        // TODO Remplacer cette vérification des arguments par une librairie JArgs / Apache CLI

        for (int currentArgIndex = 0; currentArgIndex < args.length; currentArgIndex++) {
            String currentArg = args[currentArgIndex];
            if (currentArg.startsWith("-")) {
                String[] splitDoubleDot = currentArg.split(":");
                String[] splitEquals = splitDoubleDot[splitDoubleDot.length - 1].split("=");
                String cmdName = splitDoubleDot[0];
                if (splitDoubleDot.length == 1)
                    cmdName = splitEquals[0];
                switch (cmdName) {
                    case CMD_HELP:
                        System.out.println(getCommandHelp());
                        askedForHelp = true;
                        break;
                    case CMD_LOG_LEVEL:
                        if (splitEquals.length == 2) {
                            try {
                                settings.setDefaultLogLevel(Level.parse(splitEquals[1]));
                            } catch (IllegalArgumentException e) {
                                throw new WarCommandException("Invalid log level : " + splitEquals[1]);
                            }
                        } else {
                            throw new WarCommandException("Invalid argument syntax : " + currentArg);
                        }
                        break;
                    case CMD_NB_AGENT_OF_TYPE:
                        if (splitEquals.length == 2) {
                            try {
                                WarAgentType agentType = WarAgentType.valueOf(splitEquals[0]);
                                try {
                                    int nbAgent = Integer.parseInt(splitEquals[1]);
                                    settings.setNbAgentOfType(agentType, nbAgent);
                                } catch (NumberFormatException e) {
                                    throw new WarCommandException("Invalid integer : " + splitEquals[1]);
                                }
                            } catch (IllegalArgumentException e) {
                                throw new WarCommandException("Unknown agent type : " + splitEquals[0]);
                            }
                        } else {
                            throw new WarCommandException("Invalid argument syntax : " + currentArg);
                        }
                        break;
                    case CMD_FOOD_APPEARANCE_RATE:
                        if (splitEquals.length == 2) {
                            try {
                                settings.setFoodAppearanceRate(Integer.parseInt(splitEquals[1]));
                            } catch (NumberFormatException e) {
                                throw new WarCommandException("Invalid integer : " + splitEquals[1]);
                            }
                        } else {
                            throw new WarCommandException("Invalid argument syntax : " + currentArg);
                        }
                        break;
                    case CMD_GAME_MODE:
                        if (splitEquals.length == 2) {
                            try {
                                WarGameMode gameMode = WarGameMode.valueOf(splitEquals[1]);
                                settings.setGameMode(gameMode);
                            } catch (IllegalArgumentException e) {
                                throw new WarCommandException("Unknown game mode : " + splitEquals[1]);
                            }
                        } else {
                            throw new WarCommandException("Invalid argument syntax : " + currentArg);
                        }
                        break;
                    default:
                        throw new WarCommandException("Invalid argument : " + currentArg);
                }
            } else {
                if (currentArg.endsWith(XMLSituationLoader.SITUATION_FILES_EXTENSION)) {
                    settings.setSituationLoader(new XMLSituationLoader(new File(currentArg)));
                } else {
                    selectedTeams.add(currentArg);
                }
            }
        }

        if (!askedForHelp)
            new WarMain(settings, selectedTeams.toArray(new String[]{}));
    }

    private static String getCommandHelp() {
        //TODO USE A HELPFORMATER LIKE HelpFormatter in CLI Apache
        return "Use : " + CMD_NAME + " [OPTION]... TEAM_NAME...\n" +
                " Or : " + CMD_NAME + " WAR_SITUATION_FILE\n" +
                "Launch a Warbot simulation or load and start a Warbot simulation from a situation file (*" + XMLSituationLoader.SITUATION_FILES_EXTENSION + ")\n\n" +
                "Available options :\n" +
                "\t" + CMD_LOG_LEVEL + "=LEVEL\t\tuse LEVEL as log level. LEVEL in [SEVERE, WARNING, INFO, CONFIG, FINE, FINER, FINEST]\n" +
                "\t" + CMD_NB_AGENT_OF_TYPE + ":AGENT_TYPE=NB\t\tset NB as number of agents of type AGENT_TYPE created at game start. AGENT_TYPE in " + Arrays.asList(WarAgentType.values()) + "\n" +
                "\t" + CMD_FOOD_APPEARANCE_RATE + "=RATE\t\t\tnew food will appear every RATE ticks\n" +
                "\t" + CMD_GAME_MODE + "=MODE\t\t\tset MODE as game mode. MODE in " + Arrays.asList(WarGameMode.values()) + "\n" +
                "\t" + CMD_HELP + "\t\t\t\t\tdisplay this help\n";
    }

    public void startGame() {
        loadingDialog = new LoadingDialog("Lancement de la simulation...");
        loadingDialog.setVisible(true);
        start();
    }

    public void start() {
        game = new WarGame(settings);
        new WarLauncher(game).executeLauncher();
        game.addWarGameListener(this);
    }

    public Map<String, Team> getAvailableTeams() {
        return (availableTeams);
    }

    @Override
    public void onNewTeamAdded(InGameTeam newTeam) {

    }

    @Override
    public void onTeamLost(InGameTeam removedTeam) {

    }


    @Override
    public void onGameOver() {
        if (launcherInterface != null)
            launcherInterface.displayGameResults(game);
        else { // Si la simulation a été lancée depuis la ligne de commande
            String finalTeams = "";
            for (InGameTeam team : game.getPlayerTeams()) {
                finalTeams += team.getName() + ", ";
            }
            finalTeams = finalTeams.substring(0, finalTeams.length() - 2);
            if (game.getPlayerTeams().size() == 1) {
                System.out.println("Victoire de : " + finalTeams);
            } else {
                System.out.println("Ex-Aequo entre les équipes : " + finalTeams);
            }
            game.stopGame();
        }
    }

    @Override
    public void onGameStopped() {
        game.removeWarGameListener(this);
        settings.prepareForNewGame();
        launcherInterface.setVisible(true);
    }

    @Override
    public void onGameStarted() {
        loadingDialog.dispose();
    }

    static class Shared {

        private static Map<String, Team> availableTeams;

        public static Map<String, Team> getAvailableTeams() {
            return availableTeams;
        }
    }



}
