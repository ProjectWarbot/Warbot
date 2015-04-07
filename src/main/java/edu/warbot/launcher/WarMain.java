package edu.warbot.launcher;

import edu.warbot.FSM.WarFSMBrain;
import edu.warbot.FSMEditor.FSMModelRebuilder;
import edu.warbot.FSMEditor.xmlParser.FsmXmlReader;
import edu.warbot.agents.enums.WarAgentType;
import edu.warbot.brains.WarBrain;
import edu.warbot.brains.capacities.Agressive;
import edu.warbot.brains.implementations.WarBrainImplementation;
import edu.warbot.game.*;
import edu.warbot.gui.launcher.LoadingDialog;
import edu.warbot.gui.launcher.WarLauncherInterface;
import edu.warbot.scriptcore.ScriptedTeam;
import edu.warbot.scriptcore.exceptions.DangerousFunctionPythonException;
import edu.warbot.scriptcore.exceptions.NotFoundScriptLanguageException;
import edu.warbot.scriptcore.exceptions.UnrecognizedScriptLanguageException;
import edu.warbot.scriptcore.interpreter.ScriptInterpreterFactory;
import edu.warbot.scriptcore.interpreter.ScriptInterpreterLanguage;
import edu.warbot.scriptcore.script.Script;
import edu.warbot.tools.WarIOTools;
import javassist.*;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.lang.reflect.Modifier;
import java.net.MalformedURLException;
import java.util.*;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.logging.Level;
import java.util.zip.ZipFile;

public class WarMain implements WarGameListener {
    public static final String TEAMS_DIRECTORY_NAME = "teams";
    private static final String CMD_NAME = "java WarMain";
    private static final String CMD_LOG_LEVEL = "--loglevel";
    private static final String CMD_NB_AGENT_OF_TYPE = "--nb";
    private static final String CMD_FOOD_APPEARANCE_RATE = "--foodrate";
    private static final String CMD_GAME_MODE = "--gamemode";
    private static final String CMD_HELP = "--help";
    private static final String TMP_BRAINS_OUTPUT_DIRECTORY = "bin";

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

        // On initialise la liste des équipes existantes dans le dossier "teams"
        availableTeams = loadAvailableTeams();
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
            JOptionPane.showMessageDialog(null, "Aucune équipe n'a été trouvé dans le dossier \""+ TEAMS_DIRECTORY_NAME + "\"",
                    "Aucune équipe", JOptionPane.ERROR_MESSAGE);
        }

        loadDial.dispose();
    }

    public WarMain(WarGameSettings settings, String ... selectedTeamsName) throws WarCommandException {
        availableTeams = new HashMap<>();

        // On initialise la liste des équipes existantes dans le dossier "teams"
        availableTeams = loadAvailableTeams();
        Shared.availableTeams = new HashMap<>(availableTeams);

        // On vérifie qu'au moins une équipe a été chargée
        if (availableTeams.size() > 0) {
            // On lance le jeu
            this.settings = settings;
            if(selectedTeamsName.length > 1) {
                for (String teamName : selectedTeamsName) {
                    if (availableTeams.containsKey(teamName))
                        settings.addSelectedTeam(availableTeams.get(teamName));
                    else
                        throw new WarCommandException("Team \"" + teamName + "\" does not exists. Available teams are : " + availableTeams.keySet());
                }
            } else {
                throw new WarCommandException("Please select at least two teams. Available teams are : " + availableTeams.keySet());
            }
            start();
        } else {
            throw new WarCommandException("Not team found in folder \""+ TEAMS_DIRECTORY_NAME + "\"");
        }
    }

    public static void main(String[] args) {

        if (args.length == 0) {
            new WarMain();
        } else {
            try {
                System.out.println("Command arguments = " + Arrays.asList(args));

                WarGameSettings settings = new WarGameSettings();
                ArrayList<String> selectedTeams = new ArrayList<>();
                boolean askedForHelp = false;

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
                        if (currentArg.endsWith(SituationLoader.SITUATION_FILES_EXTENSION)) {
                            settings.setSituationLoader(new SituationLoader(new File(currentArg)));
                        } else {
                            selectedTeams.add(currentArg);
                        }
                    }
                }

                if (!askedForHelp)
                    new WarMain(settings, selectedTeams.toArray(new String[]{}));
            } catch (WarCommandException e) {
                System.err.println(e.getMessage());
            }
        }
    }

    private static String getCommandHelp() {
        return "Use : " + CMD_NAME + " [OPTION]... TEAM_NAME...\n" +
                " Or : " + CMD_NAME + " WAR_SITUATION_FILE\n" +
                "Launch a Warbot simulation or load and start a Warbot simulation from a situation file (*" + SituationLoader.SITUATION_FILES_EXTENSION + ")\n\n" +
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
        Shared.game = game;
        new WarLauncher().executeLauncher();
        game.addWarGameListener(this);
    }

    @SuppressWarnings("unchecked")
    public Map<String, Team> loadAvailableTeams() {
        Map<String, Team> loadedTeams = new HashMap<String, Team>();
        loadedTeams.putAll(getTeamsFromSourceDirectory());

        for(Map.Entry<String, Team> currentLoadedTeam : getTeamsFromJarDirectory(loadedTeams.keySet()).entrySet()) {
            loadedTeams.put(currentLoadedTeam.getKey(), currentLoadedTeam.getValue());
        }
        return loadedTeams;
    }

    private Map<String, Team> getTeamsFromJarDirectory(Set<String> excludedTeams) {
        Map<String, Team> teamsLoaded = new HashMap<>();

        String jarDirectoryPath = TEAMS_DIRECTORY_NAME + File.separator;
        File jarDirectory = new File(jarDirectoryPath);
        // On regarde si un dossier jar existe
        if (! jarDirectory.exists() || jarDirectory.isDirectory()) {
            jarDirectory.mkdir();
        }
        File[] filesInJarDirectory = jarDirectory.listFiles();

        //Exploration des fichiers externes
        for (File currentFile : filesInJarDirectory) {
            try {

                //Lecture depuis un jar
                if (currentFile.getCanonicalPath().endsWith(".jar"))
                {
                    Team currentTeam;
                    JarFile jarCurrentFile = new JarFile(currentFile);

                    // On parcours les entrées du fichier JAR à la recherche des fichiers souhaités
                    HashMap<String, JarEntry> allJarEntries = getAllJarEntry(jarCurrentFile);

                    if (allJarEntries.containsKey(TeamConfigReader.FILE_NAME)) {
                        currentTeam = loadTeamFromJar(currentFile, jarCurrentFile, allJarEntries, excludedTeams);

                        // Puis on ferme le fichier JAR
                        jarCurrentFile.close();

                        // Si il y a déjà une équipe du même nom ou qu'elle est exclue, on ne l'ajoute pas
                        if (teamsLoaded.containsKey(currentTeam.getName()))
                            System.err.println("Erreur lors de la lecture d'une équipe : le nom d'équipe '" + currentTeam.getName() + "' est déjà utilisé.");
                        else
                            teamsLoaded.put(currentTeam.getName(), currentTeam);
                    } else { // Si le fichier de configuration n'a pas été trouvé
                        System.err.println("Le fichier de configuration est introuvable dans le fichier JAR " + currentFile.getCanonicalPath());
                    }
                }
                //lecture depuis une archive zip
                else if(currentFile.getCanonicalPath().endsWith(".zip"))
                {
                    ZipFile zipCurrentFile = new ZipFile(currentFile);
                    //TODO PUT ZIP TEAM HANDLER
                }
                //Lecture depuis un dossier
                else if(currentFile.isDirectory())
                {
                    //LOOK FOR A CONFIG.YML
                    List<String> files = Arrays.asList(currentFile.list());
                    if(files.contains(TeamConfigReader.FILE_NAME))
                    {
                        Team currentTeam;
                        currentTeam = loadTeamFromDirectory(currentFile,excludedTeams);
                        // Si il y a déjà une équipe du même nom ou qu'elle est exclue, on ne l'ajoute pas
                        if (teamsLoaded.containsKey(currentTeam.getName()))
                            System.err.println("Erreur lors de la lecture d'une équipe : le nom d'équipe '" + currentTeam.getName() + "' est déjà utilisé.");
                        else
                            teamsLoaded.put(currentTeam.getName(), currentTeam);
                    }else { // Si le fichier de configuration n'a pas été trouvé
                        //TODO FAIRE UNE FOUILLE RECURSIVE ?
                        System.err.println("Le fichier de configuration est introuvable dans le dossier " + currentFile.getCanonicalPath());
                    }
                }
            } catch (TeamAlreadyExistsException e) {
                System.err.println("Lecture des fichiers JAR : " + e.getMessage());
            } catch (MalformedURLException e) {
                System.err.println("Lecture des fichiers JAR : URL mal formée");
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                System.err.println("Lecture des fichiers JAR : Classe non trouvée");
                e.printStackTrace();
            } catch (IOException e) {
                System.err.println("Lecture des fichiers JAR : Lecture de fichier");
                e.printStackTrace();
            } catch (NullPointerException e) {
                System.err.println("Lecture des fichiers JAR : Lecture de fichier");
                e.printStackTrace();
            } catch (CannotCompileException e) {
                e.printStackTrace();
            } catch (NotFoundException e) {
                e.printStackTrace();
            } catch (UnrecognizedScriptLanguageException e) {
                e.printStackTrace();
            } catch (NotFoundScriptLanguageException e) {
                e.printStackTrace();
            } catch (DangerousFunctionPythonException e) {
                e.printStackTrace();
            }
        }

        return teamsLoaded;
    }

    /**
     * CHARGEMENT D'UNE EQUIPE SCRIPTEE DEPUIS UN DOSSIER
     * @param file
     * @param excludedTeams
     * @return
     * @throws IOException
     * @throws ClassNotFoundException
     * @throws NotFoundException
     * @throws CannotCompileException
     * @throws TeamAlreadyExistsException
     */
    private Team loadTeamFromDirectory(File file, Set<String> excludedTeams) throws IOException, ClassNotFoundException, NotFoundException, CannotCompileException, TeamAlreadyExistsException, UnrecognizedScriptLanguageException, NotFoundScriptLanguageException, DangerousFunctionPythonException {
        Team currentTeam;

        File[] config = file.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.equals(TeamConfigReader.FILE_NAME);
            }
        });
        if(config.length==0)
        {
            throw new IOException("Not found configuration");
        }

        // On analyse le fichier XML
        BufferedInputStream input = new BufferedInputStream(new FileInputStream(config[0]));
        final TeamConfigReader teamConfigReader = new TeamConfigReader();
        teamConfigReader.load(input);
        input.close();

        if (excludedTeams.contains(teamConfigReader.getTeamName())) {
            throw new TeamAlreadyExistsException(teamConfigReader.getTeamName());
        }

        System.out.println(teamConfigReader.getTeamName());
        if(teamConfigReader.isScriptedTeam())
            currentTeam = instanciedScriptedTeam(teamConfigReader,file);
        else
            currentTeam = new Team(teamConfigReader.getTeamName());

        File[] logo = file.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.equals(teamConfigReader.getIconPath());
            }
        });
        if(logo.length==1)
        {
            ImageIcon teamLogo = null;
            try {
                FileInputStream fis = new FileInputStream(logo[0]);
                teamLogo = new ImageIcon(WarIOTools.toByteArray(fis));
                fis.close();
                currentTeam.setLogo(scaleTeamLogo(teamLogo));
            } catch (IOException e) {
                System.err.println("ERROR loading file " + logo[0].getName() + " inside directory " + file.getName());
                e.printStackTrace();
            }
        }

        currentTeam.setDescription(teamConfigReader.getTeamDescription());
        return currentTeam;
    }

    private Team loadTeamFromJar(File file, JarFile jarFile, HashMap<String, JarEntry> jarEntries, Set<String> excludedTeams) throws IOException, ClassNotFoundException, NotFoundException, CannotCompileException, TeamAlreadyExistsException {
        Team currentTeam;

        // On analyse le fichier XML
        BufferedInputStream input = new BufferedInputStream(jarFile.getInputStream(jarEntries.get("config.yml")));
        TeamConfigReader teamConfigReader = new TeamConfigReader();
        teamConfigReader.load(input);
        input.close();

        if (excludedTeams.contains(teamConfigReader.getTeamName())) {
            throw new TeamAlreadyExistsException(teamConfigReader.getTeamName());
        }
        // On créé l'équipe
        currentTeam = new Team(teamConfigReader.getTeamName());
        currentTeam.setLogo(getTeamLogoFromJar(jarEntries.get(teamConfigReader.getIconPath()), jarFile));
        currentTeam.setDescription(teamConfigReader.getTeamDescription().trim());
        // TODO get sound

        // On recherche les classes de type Brain
        String urlName = file.getCanonicalPath();
        ClassPool classPool = ClassPool.getDefault();
        classPool.insertClassPath(urlName);

        // Vérifie si l'équipe est une FSM (on regarde dans le fichier de configuration)
        Map<String, String> brainControllersClassesName = teamConfigReader.getBrainControllersClassesNameOfEachAgentType();
        if (teamConfigReader.isFSMTeam()) {
            // TODO partie FSM
//            JarEntry entryFSMConfiguration = jarEntries.get(FsmXmlParser.xmlConfigurationDefaultFilename);
//
//            InputStream fileFSMConfig = jarFile.getInputStream(entryFSMConfiguration);
//            FsmXmlReader fsmXmlReader = new FsmXmlReader(fileFSMConfig);
//            FSMModelRebuilder fsmModelRebuilder = new FSMModelRebuilder(fsmXmlReader.getGeneratedFSMModel());
//            currentTeam.setFsmModel(fsmModelRebuilder.getRebuildModel());
//
//            for (String agentName : brainControllersClassesName.keySet()) {
//                currentTeam.addBrainControllerClassForAgent(agentName, WarFSMBrain.class);
//            }
        } else {
            for (String agentName : brainControllersClassesName.keySet()) {
                currentTeam.addBrainControllerClassForAgent(agentName, createNewWarBrainImplementationClass(classPool, teamConfigReader.getBrainsPackageName() + "." + brainControllersClassesName.get(agentName)));
            }
        }

        return currentTeam;
    }

    private Map<String, Team> getTeamsFromSourceDirectory() {
        Map<String, Team> teamsLoaded = new HashMap<>();

        Map<String, String> teamsSourcesFolders = UserPreferences.getTeamsSourcesFolders();
        for (String currentFolder : teamsSourcesFolders.values()) {
            try {
                Team currentTeam;

                // On analyse le fichier XML
                FileInputStream input = new FileInputStream(getClass().getClassLoader().getResource(currentFolder).getFile()+"/"+TeamConfigReader.FILE_NAME);
                TeamConfigReader teamConfigReader = new TeamConfigReader();
                teamConfigReader.load(input);
                input.close();

                currentTeam = loadTeamFromSources(teamsSourcesFolders, teamConfigReader);

                // Si il y a déjà une équipe du même nom on ne l'ajoute pas
                if (teamsLoaded.containsKey(currentTeam.getName()))
                    System.err.println("Erreur lors de la lecture d'une équipe : le nom " + currentTeam.getName() + " est déjà utilisé.");
                else
                    teamsLoaded.put(currentTeam.getName(), currentTeam);
            } catch (FileNotFoundException e) {
                System.err.println("Le fichier de configuration est introuvable dans le dossier " + new File("").getAbsolutePath() + File.separatorChar + currentFolder);
                e.printStackTrace();
            } catch (MalformedURLException e) {
                System.err.println("Lecture des fichiers JAR : URL mal formée");
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                System.err.println("Lecture des fichiers JAR : Classe non trouvée");
                e.printStackTrace();
            } catch (IOException e) {
                System.err.println("Lecture des fichiers JAR : Lecture de fichier");
                e.printStackTrace();
            } catch (NullPointerException e) {
                System.err.println("Lecture des fichiers JAR : Un pointeur nul est apparu");
                e.printStackTrace();
            } catch (CannotCompileException e) {
                e.printStackTrace();
            } catch (NotFoundException e) {
                e.printStackTrace();
            }
        }

        return teamsLoaded;
    }


    public Team instanciedScriptedTeam(final TeamConfigReader teamConfigReader, File teamDirectory) throws
            NotFoundScriptLanguageException, UnrecognizedScriptLanguageException, IOException, ClassNotFoundException, DangerousFunctionPythonException {
        ScriptedTeam team = new ScriptedTeam(teamConfigReader.getTeamName());
        team.initListFunctions();
        ScriptInterpreterLanguage language = teamConfigReader.getScriptLanguage();
        team.setInterpreter(ScriptInterpreterFactory.getInstance(language).createScriptInterpreter());
        final Map<String, String> brainControllersClassesName = teamConfigReader.getBrainControllersClassesNameOfEachAgentType();

        if (teamConfigReader.getBrainControllersClassesNameOfEachAgentType().containsKey("WarTools")) {
            File warTool[] = teamDirectory.listFiles(new FilenameFilter() {
                @Override
                public boolean accept(File dir, String name) {
                    return name.equals(teamConfigReader.getBrainControllersClassesNameOfEachAgentType().get("WarTools"));
                }
            });
        }

        for (final String agentName : brainControllersClassesName.keySet()) {
            File[] tab = teamDirectory.listFiles(new FilenameFilter() {
                @Override
                public boolean accept(File dir, String name) {
                    return name.equals(brainControllersClassesName.get(agentName)) && !agentName.equals("WarTools");
                }
            });


            if (tab.length == 1) {
                InputStream input = new FileInputStream(tab[0]);
                Script sc = Script.checkDangerousFunctions(team, input, WarAgentType.valueOf(agentName));
                team.getInterpreter().addSCript(sc, WarAgentType.valueOf(agentName));
                input.close();
            }
        }
        return team;

    }

    private Team loadTeamFromSources(Map<String, String> teamsSourcesFolders, TeamConfigReader teamConfigReader) throws ClassNotFoundException, IOException, NotFoundException, CannotCompileException {
        Team currentTeam;
        File teamDirectory = new File(getClass().getClassLoader().getResource(teamsSourcesFolders.get(teamConfigReader.getTeamName())).getFile());
        currentTeam = new Team(teamConfigReader.getTeamName());
        currentTeam.setLogo(getTeamLogoFromFile(new File(teamDirectory.getAbsolutePath() + "/" + teamConfigReader.getIconPath())));
        currentTeam.setDescription(teamConfigReader.getTeamDescription().trim());
        // TODO get sound


        Map<String, String> brainControllersClassesName = teamConfigReader.getBrainControllersClassesNameOfEachAgentType();
        if(teamConfigReader.isFSMTeam()) {
            File fileFSMConfig = new File(teamDirectory.getAbsolutePath() + File.separatorChar + teamConfigReader.getFSMConfigurationFileName());

        	FsmXmlReader fsmXmlReader = new FsmXmlReader(fileFSMConfig);
        	FSMModelRebuilder fsmModelRebuilder = new FSMModelRebuilder(fsmXmlReader.getGeneratedFSMModel());
        	currentTeam.setFsmModel(fsmModelRebuilder.getRebuildModel());

            for (String agentName : brainControllersClassesName.keySet()) {
                currentTeam.addBrainControllerClassForAgent(agentName, WarFSMBrain.class);
            }
        }
        else {
            ClassPool defaultClassPool = ClassPool.getDefault();
            for (String agentName : brainControllersClassesName.keySet()) {
                currentTeam.addBrainControllerClassForAgent(agentName, createNewWarBrainImplementationClass(defaultClassPool, teamConfigReader.getBrainsPackageName() + "." + brainControllersClassesName.get(agentName)));
            }
        }

        return currentTeam;
    }

    private Class<? extends WarBrain> createNewWarBrainImplementationClass(ClassPool classPool, String brainClassName) throws NotFoundException, CannotCompileException, IOException {
        CtClass brainImplementationClass = classPool.get(WarBrainImplementation.class.getName());
        if(! brainImplementationClass.isFrozen()) {
            brainImplementationClass.setName(brainClassName + "BrainImplementation");
            brainImplementationClass.setModifiers(Modifier.PUBLIC);

            CtClass brainClass = classPool.get(brainClassName);
            String capacitiesPackageName = Agressive.class.getPackage().getName();
            for (CtClass brainInterface : brainClass.getSuperclass().getInterfaces()) {
                if (brainInterface.getPackageName().equals(capacitiesPackageName)) {
                    CtClass brainInterfaceImplementation = classPool.get(WarBrainImplementation.class.getPackage().getName() + ".War" + brainInterface.getSimpleName() + "BrainImplementation");
                    for (CtMethod interfaceImplementationMethod : brainInterface.getDeclaredMethods()) {
                        brainImplementationClass.addMethod(new CtMethod(
                                brainInterfaceImplementation.getDeclaredMethod(interfaceImplementationMethod.getName(), interfaceImplementationMethod.getParameterTypes()),
                                brainImplementationClass, null));
                    }
                }
            }

            brainImplementationClass.setSuperclass(brainClass);
            brainImplementationClass.writeFile(TMP_BRAINS_OUTPUT_DIRECTORY);
            brainClass.writeFile(TMP_BRAINS_OUTPUT_DIRECTORY);
            return brainImplementationClass.toClass().asSubclass(WarBrain.class);
        } else {
            return null;
        }
    }

    private ImageIcon getTeamLogoFromJar(JarEntry logoEntry, JarFile jarCurrentFile) {
        ImageIcon teamLogo = null;
        try {
            teamLogo = new ImageIcon(WarIOTools.toByteArray(jarCurrentFile.getInputStream(logoEntry)));
        } catch (IOException e) {
            System.err.println("ERROR loading file " + logoEntry.getName() + " inside jar file " + jarCurrentFile.getName());
            e.printStackTrace();
        }
        // TODO set general logo if no image found
        // On change sa taille
        return scaleTeamLogo(teamLogo);
    }

    private ImageIcon getTeamLogoFromFile(File file) {
        ImageIcon teamLogo = null;
        try {
            teamLogo = new ImageIcon(WarIOTools.toByteArray(new FileInputStream(file)));
        } catch (IOException e) {
            System.err.println("ERROR loading file " + file.getName() + " inside directory " + file.getParentFile().getName());
            e.printStackTrace();
        }
        // TODO set general logo if no image found
        return scaleTeamLogo(teamLogo);
    }

    private ImageIcon scaleTeamLogo(ImageIcon teamLogo) {
        return new ImageIcon(teamLogo.getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH));
    }

    private HashMap<String, JarEntry> getAllJarEntry(JarFile jarFile) {
        HashMap<String, JarEntry> allJarEntries = new HashMap<>();

        Enumeration<JarEntry> entries = jarFile.entries();

        JarEntry currentEntry;
        while (entries.hasMoreElements()) {
            currentEntry = entries.nextElement();

            String currentEntryName = currentEntry.getName();
            allJarEntries.put(currentEntryName.substring(currentEntryName.lastIndexOf("/") + 1, currentEntryName.length()), currentEntry);

            // Si c'est le fichier config.xml
            if (currentEntry.getName().endsWith(TeamConfigReader.FILE_NAME)) {
                // On le lit et on l'analyse grâce à la classe TeamXmlReader

            }
        }
        return allJarEntries;
    }

    public Map<String, Team> getAvailableTeams() {
        return new HashMap<>(availableTeams);
    }

    @Override
    public void onNewTeamAdded(Team newTeam) {

    }

    @Override
    public void onTeamLost(Team removedTeam) {

    }

    @Override
    public void onGameOver() {
        if(launcherInterface != null)
            launcherInterface.displayGameResults(game);
        else { // Si la simulation a été lancée depuis la ligne de commande
            String finalTeams = "";
            for(Team team : game.getPlayerTeams()) {
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

    public static class Shared {
        public static WarGame game;
        private static Map<String, Team> availableTeams;

        public static WarGame getGame() {
            return game;
        }

        public static Map<String, Team> getAvailableTeams(){
            return availableTeams;
        }
    }

    protected static class WarCommandException extends Exception {

        public WarCommandException(String message) {
            super(message + "\nEnter \""+CMD_NAME+" "+CMD_HELP+"\" for more informations.");
        }

    }

    protected static class TeamAlreadyExistsException extends Exception {
        public TeamAlreadyExistsException(String teamName) {
            super("Team name '" + teamName + "' already used.");
        }
    }

}
