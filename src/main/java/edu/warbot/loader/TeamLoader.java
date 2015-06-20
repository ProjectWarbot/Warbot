package edu.warbot.loader;

import edu.warbot.agents.enums.WarAgentType;
import edu.warbot.agents.teams.FSMTeam;
import edu.warbot.agents.teams.JavaTeam;
import edu.warbot.agents.teams.ScriptedTeam;
import edu.warbot.agents.teams.Team;
import edu.warbot.brains.WarBrain;
import edu.warbot.exceptions.TeamAlreadyExistsException;
import edu.warbot.fsm.editor.FSMModelRebuilder;
import edu.warbot.fsm.editor.parsing.xml.FsmXmlParser;
import edu.warbot.fsm.editor.parsing.xml.FsmXmlReader;
import edu.warbot.launcher.TeamConfigReader;
import edu.warbot.launcher.UserPreferences;
import edu.warbot.scriptcore.exceptions.DangerousFunctionPythonException;
import edu.warbot.scriptcore.exceptions.NotFoundScriptLanguageException;
import edu.warbot.scriptcore.exceptions.UnrecognizedScriptLanguageException;
import edu.warbot.scriptcore.interpreter.ScriptInterpreterFactory;
import edu.warbot.scriptcore.interpreter.ScriptInterpreterLanguage;
import edu.warbot.tools.WarIOTools;
import javassist.CannotCompileException;
import javassist.ClassPath;
import javassist.ClassPool;
import javassist.NotFoundException;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.zip.ZipFile;

/**
 * Created by beugnon on 18/06/15.
 *
 * TeamLoader, s'occupe du chargement des équipes (par instance Team) depuis plusieurs endroits (sources, jar, dossier)
 *
 * @author BEUGNON Sébastien
 *
 * @since 3.2.3
 */
public class TeamLoader {

    public static final String TEAMS_DIRECTORY_NAME = "teams";

    private static final String TMP_BRAINS_OUTPUT_DIRECTORY = "bin";

    private ImplementationProducer implementationProducer;

    public TeamLoader() {
        implementationProducer = new ImplementationProducer();
    }

    public ImplementationProducer getImplementationProducer() {
        return implementationProducer;
    }


    @SuppressWarnings("unchecked")
    public Map<String, Team> loadAvailableTeams() {
        Map<String, Team> loadedTeams = new HashMap<>();

        //RAJOUTER A UNE FACTORY DE CHARGEMENT
        loadedTeams.putAll(getTeamsFromSourceDirectory());

        for (Map.Entry<String, Team> currentLoadedTeam : getTeamsFromJarDirectory(loadedTeams.keySet()).entrySet()) {
            loadedTeams.put(currentLoadedTeam.getKey(), currentLoadedTeam.getValue());
        }
        return loadedTeams;
    }

    private Map<String, Team> getTeamsFromJarDirectory(Set<String> excludedTeams) {
        Map<String, Team> teamsLoaded = new HashMap<>();

        String jarDirectoryPath = TEAMS_DIRECTORY_NAME + File.separator;
        File jarDirectory = new File(jarDirectoryPath);
        // On regarde si un dossier jar existe
        if (!jarDirectory.exists() || jarDirectory.isDirectory()) {
            jarDirectory.mkdir();
        }
        File[] filesInJarDirectory = jarDirectory.listFiles();

        //Exploration des fichiers externes
        for (File currentFile : filesInJarDirectory) {
            try {

                //TODO METTRE UNE FACTORY DE CHARGEMENT EN FONCTION DU TYPE DE FICHIER (JAR, TAR, ZIP, DIRECTORY)
                //Lecture depuis un jar
                if (currentFile.getCanonicalPath().endsWith(".jar")) {
                    Team currentTeam;
                    JarFile jarCurrentFile = new JarFile(currentFile);

                    // On parcours les entrées du fichier JAR à la recherche des fichiers souhaités
                    HashMap<String, JarEntry> allJarEntries = getAllJarEntry(jarCurrentFile);

                    if (allJarEntries.containsKey(TeamConfigReader.FILE_NAME)) {
                        currentTeam = loadTeamFromJar(currentFile, jarCurrentFile, allJarEntries, excludedTeams);

                        // Puis on ferme le fichier JAR
                        jarCurrentFile.close();

                        // Si il y a déjà une équipe du même nom ou qu'elle est exclue, on ne l'ajoute pas
                        if (teamsLoaded.containsKey(currentTeam.getTeamName()))
                            System.err.println("Erreur lors de la lecture d'une équipe : le nom d'équipe '" +
                                    currentTeam.getTeamName() + "' est déjà utilisé.");
                        else
                            teamsLoaded.put(currentTeam.getTeamName(), currentTeam);
                    } else { // Si le fichier de configuration n'a pas été trouvé
                        System.err.println("Le fichier de configuration est introuvable dans le fichier JAR " +
                                currentFile.getCanonicalPath());
                    }
                }
                //lecture depuis une archive zip
                else if (currentFile.getCanonicalPath().endsWith(".zip")) {
                    ZipFile zipCurrentFile = new ZipFile(currentFile);
                    //TODO PUT ZIP TEAM HANDLER
                }
                //Lecture depuis un dossier
                else if (currentFile.isDirectory()) {
                    //LOOK FOR A CONFIG.YML
                    List<String> files = Arrays.asList(currentFile.list());
                    if (files.contains(TeamConfigReader.FILE_NAME)) {
                        Team currentTeam;
                        currentTeam = loadTeamFromDirectory(currentFile, excludedTeams);
                        // Si il y a déjà une équipe du même nom ou qu'elle est exclue, on ne l'ajoute pas
                        if (teamsLoaded.containsKey(currentTeam.getTeamName()))
                            System.err.println("Erreur lors de la lecture d'une équipe : le nom d'équipe '" +
                                    currentTeam.getTeamName() + "' est déjà utilisé.");
                        else
                            teamsLoaded.put(currentTeam.getTeamName(), currentTeam);
                    } else { // Si le fichier de configuration n'a pas été trouvé
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
                System.err.println("Lecture des fichiers JAR : Pointeur nul détecté");
                e.printStackTrace();
            } catch (CannotCompileException e) {
                System.err.println("Lecture des fichiers JAR : Problème de compilation de classe");
                e.printStackTrace();
            } catch (NotFoundException e) {
                System.err.println("Lecture des fichiers JAR : Classe non trouvée lors d'une implémentation");
                e.printStackTrace();
            } catch (UnrecognizedScriptLanguageException e) {
                System.err.println("Lecture des fichiers JAR : Langage de script non reconnu");
                e.printStackTrace();
            } catch (NotFoundScriptLanguageException e) {
                System.err.println("Lecture des fichiers JAR : Langage de script absent");
                e.printStackTrace();
            } catch (DangerousFunctionPythonException e) {
                System.err.println("Lecture des fichiers JAR : Problème de modificatin des fonctions python");
                e.printStackTrace();
            }
        }

        return teamsLoaded;
    }


    private ImageIcon loadLogo(File file, final TeamConfigReader teamConfigReader) {
        File[] logo = file.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.equals(teamConfigReader.getIconPath());
            }
        });

        ImageIcon teamLogo = null;
        if (logo.length == 1) {

            try {
                FileInputStream fis = new FileInputStream(logo[0]);
                teamLogo = new ImageIcon(WarIOTools.toByteArray(fis));
                fis.close();
            } catch (IOException e) {
                System.err.println("ERROR loading file " + logo[0].getName() + " inside directory " + file.getName());
                e.printStackTrace();
            }
        }
        return scaleTeamLogo(teamLogo);
    }

    /**
     * Chargement d'une équipe depuis un dossier (équipe scriptée)
     *
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
        if (config.length == 0) {
            throw new IOException("Not found configuration");
        }

        // On analyse le fichier XML
        BufferedInputStream input = new BufferedInputStream(new FileInputStream(config[0]));
        final TeamConfigReader teamConfigReader = new TeamConfigReader();
        teamConfigReader.load(input);
        input.close();
        ImageIcon teamLogo = loadLogo(file, teamConfigReader);


        if (excludedTeams.contains(teamConfigReader.getTeamName())) {
            throw new TeamAlreadyExistsException(teamConfigReader.getTeamName());
        }

        if (teamConfigReader.isScriptedTeam())
            currentTeam = generateScriptedTeam(teamConfigReader, file);
        else
            currentTeam = new JavaTeam(teamConfigReader.getTeamName(),
                    teamConfigReader.getTeamDescription(), teamLogo, null);


        return currentTeam;
    }


    private Team loadTeamFromJar(File file, JarFile jarFile, HashMap<String, JarEntry> jarEntries, Set<String> excludedTeams) throws IOException, ClassNotFoundException, NotFoundException, CannotCompileException, TeamAlreadyExistsException {
        Team currentTeam;

        // On analyse le fichier YML
        BufferedInputStream input = new BufferedInputStream(jarFile.getInputStream(jarEntries.get("config.yml")));
        TeamConfigReader teamConfigReader = new TeamConfigReader();
        teamConfigReader.load(input);
        input.close();

        if (excludedTeams.contains(teamConfigReader.getTeamName())) {
            throw new TeamAlreadyExistsException(teamConfigReader.getTeamName());
        }
        // On créé l'équipe
        ImageIcon logo = getTeamLogoFromJar(jarEntries.get(teamConfigReader.getIconPath()), jarFile);
        if (!teamConfigReader.isFSMTeam()) {

            // On recherche les classes de type Brain
            String urlName = file.getCanonicalPath();
            ClassPool classPool = ClassPool.getDefault();
            ClassPath cp = classPool.insertClassPath(urlName);

            URL url = cp.find("myteam.WarBaseBrainController");
            try {
                System.out.println(url.toURI().toString());
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
            // Vérifie si l'équipe est une fsm (on regarde dans le fichier de configuration)
            Map<String, String> brainControllersClassesName = teamConfigReader.getBrainControllersClassesNameOfEachAgentType();

            Map<WarAgentType, Class<? extends WarBrain>> brains = new HashMap<>();

            for (String agentName : brainControllersClassesName.keySet()) {
                brains.put(WarAgentType.valueOf(agentName),
                        getImplementationProducer().createWarBrainImplementationClass
                                (classPool,
                                        teamConfigReader.getBrainsPackageName() + "." +
                                                brainControllersClassesName.get(agentName)));
            }
            currentTeam = new JavaTeam(teamConfigReader.getTeamName(),
                    teamConfigReader.getTeamDescription().trim(), logo, brains);
        } else {

            //TODO DO THE LOADING SPECIFICITIES FOR FsmModel-based InGameTeam
            JarEntry entryFSMConfiguration = jarEntries.get(FsmXmlParser.xmlConfigurationDefaultFilename);

            InputStream fileFSMConfig = jarFile.getInputStream(entryFSMConfiguration);
            FsmXmlReader fsmXmlReader = new FsmXmlReader(fileFSMConfig);
            FSMModelRebuilder fsmModelRebuilder = new FSMModelRebuilder(fsmXmlReader.getGeneratedFSMModel());
            currentTeam = new FSMTeam(teamConfigReader.getTeamName(),
                    teamConfigReader.getTeamDescription().trim(), logo, fsmModelRebuilder.getRebuildModel());

        }


        return currentTeam;
    }

    private Map<String, Team> getTeamsFromSourceDirectory() {
        Map<String, Team> teamsLoaded = new HashMap<>();

        Map<String, String> teamsSourcesFolders = UserPreferences.getTeamsSourcesFolders();
        for (String currentFolder : teamsSourcesFolders.values()) {
            try {
                Team currentTeam;

                // On analyse le fichier YML
                InputStream input = getClass().getClassLoader().getResourceAsStream(currentFolder + "/" + TeamConfigReader.FILE_NAME);
                System.out.println(currentFolder + "/" + TeamConfigReader.FILE_NAME);
                TeamConfigReader teamConfigReader = new TeamConfigReader();
                teamConfigReader.load(input);
                input.close();

                currentTeam = loadTeamFromSources(teamsSourcesFolders, teamConfigReader);

                // Si il y a déjà une équipe du même nom on ne l'ajoute pas
                if (teamsLoaded.containsKey(currentTeam.getTeamName()))
                    System.err.println("Erreur lors de la lecture d'une équipe : le nom " + currentTeam.getTeamName() +
                            " est déjà utilisé.");
                else
                    teamsLoaded.put(currentTeam.getTeamName(), currentTeam);
            } catch (FileNotFoundException e) {
                System.err.println("Le fichier de configuration est introuvable dans le dossier " +
                        new File("").getAbsolutePath() + File.separatorChar + currentFolder);
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
            } catch (URISyntaxException e) {
                System.err.println("Lecture des fichiers JAR : Mauvaise URI");
                e.printStackTrace();
            }
        }

        return teamsLoaded;
    }

    public Team generateScriptedTeam(final TeamConfigReader teamConfigReader, File teamDirectory) throws
            NotFoundScriptLanguageException, UnrecognizedScriptLanguageException, IOException, ClassNotFoundException, DangerousFunctionPythonException {

        ScriptedTeam team = new ScriptedTeam(teamConfigReader.getTeamName(),
                teamConfigReader.getTeamDescription(),
                loadLogo(teamDirectory, teamConfigReader));

//        team.initFunctionList();
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
//                Script sc = Script.checkDangerousFunctions(team, input, WarAgentType.valueOf(agentName));
//                team.getInterpreter().addSCript(sc, WarAgentType.valueOf(agentName));
                team.getInterpreter().addScript(input, WarAgentType.valueOf(agentName));
                input.close();
            }
        }
        return team;

    }

    private Team loadTeamFromSources(Map<String, String> teamsSourcesFolders, final TeamConfigReader teamConfigReader) throws ClassNotFoundException, IOException, NotFoundException, CannotCompileException, URISyntaxException {
        Team currentTeam;
        URL url = getClass().getClassLoader().getResource(teamsSourcesFolders.get(teamConfigReader.getTeamName()));
        File teamDirectory = new File(url.getFile());
        ImageIcon logo = loadLogo(teamDirectory, teamConfigReader);

        if (teamConfigReader.isFSMTeam()) {
            File fileFSMConfig = new File(teamDirectory.getAbsolutePath() + File.separatorChar +
                    teamConfigReader.getFSMConfigurationFileName());

            FsmXmlReader fsmXmlReader = new FsmXmlReader(fileFSMConfig);
            FSMModelRebuilder fsmModelRebuilder = new FSMModelRebuilder(fsmXmlReader.getGeneratedFSMModel());
            currentTeam = new FSMTeam(teamConfigReader.getTeamName(),
                    teamConfigReader.getTeamDescription().trim(),
                    logo, fsmModelRebuilder.getRebuildModel());
        } else {

            Map<String, String> brainControllersClassesName = teamConfigReader.
                    getBrainControllersClassesNameOfEachAgentType();
            ClassPool defaultClassPool = ClassPool.getDefault();
            Map<WarAgentType, Class<? extends WarBrain>> brains = new HashMap<>();
            for (String agentName : brainControllersClassesName.keySet()) {

                brains.put(WarAgentType.valueOf(agentName),
                        implementationProducer.createWarBrainImplementationClass
                                (defaultClassPool,
                                        teamConfigReader.getBrainsPackageName() + "." +
                                                brainControllersClassesName.get(agentName)));
            }
            currentTeam = new JavaTeam(teamConfigReader.getTeamName(), teamConfigReader.getTeamDescription().trim(), logo,
                    brains);
        }

        return currentTeam;
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

}
