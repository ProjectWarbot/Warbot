package edu.warbot.launcher;

import edu.warbot.agents.AliveWarAgent;
import edu.warbot.agents.ControllableWarAgent;
import edu.warbot.agents.WarAgent;
import edu.warbot.agents.enums.WarAgentCategory;
import edu.warbot.agents.enums.WarAgentType;
import edu.warbot.game.MotherNatureTeam;
import edu.warbot.game.Team;
import edu.warbot.game.WarGame;
import edu.warbot.launcher.WarMain.Shared;
import edu.warbot.tools.WarXmlReader;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SituationLoader {

    public static final String SITUATION_FILES_EXTENSION = ".warsit";

    private HashMap<String, ArrayList<HashMap<String, String>>> xmlSituationFileContent;

    public SituationLoader(File xmlSituationFile) {
        xmlSituationFileContent = getXmlSituationFileContent(xmlSituationFile);
    }


    /**
     * Retourne une hashmap contenant les équipes (identifiées par leur nom). Chaque équipe est composée d'une liste d'agents.
     * Chaque donnée de chaque agent est enregistrée dans une hashmap
     * Exemple : je veux la position en X du premier agent de l'équipe "Test" :
     * Double.valueOf(getXmlSituationFileContent(file).get("Test").get(0).get("xPosition"));
     */
    private HashMap<String, ArrayList<HashMap<String, String>>> getXmlSituationFileContent(File file) {
        String rootPath = "/WarSituation";
        String teamsPath = rootPath + "/Teams";
        HashMap<String, ArrayList<HashMap<String, String>>> toReturn = new HashMap<>();
        try {
            // Ouverture du document
            Document doc = WarXmlReader.openXmlFile(file.getAbsolutePath());

            // Chargement
            XPath xPath = XPathFactory.newInstance().newXPath();
            NodeList teamsNode = (NodeList) xPath.compile(teamsPath).evaluate(doc, XPathConstants.NODESET);
            teamsNode = teamsNode.item(0).getChildNodes();
            Node node;
            for (int i = 0; i < teamsNode.getLength(); i++) { // Parcours des équipes
                if (teamsNode.item(i).getNodeType() == Node.ELEMENT_NODE) {
                    NodeList teamNode = teamsNode.item(i).getChildNodes();
                    // On récupère l'équipe courante (l'objet)
                    String currentTeamName = teamNode.item(0).getFirstChild().getNodeValue();
                    ArrayList<HashMap<String, String>> agentsOfCurrentTeam = new ArrayList<>();

                    for (int j = 0; j < teamNode.getLength(); j++) { // Parcours des agents d'une équipe
                        node = teamNode.item(j);
                        if (node.getNodeType() == Node.ELEMENT_NODE) {
                            if (node.getNodeName().equals("WarAgent")) {
                                // On récupère tous les paramètres de l'agent
                                agentsOfCurrentTeam.add(WarXmlReader.getNodesFromNodeList(doc, node.getChildNodes()));
                            }
                        }
                    } // Fin parcours des agents

                    toReturn.put(currentTeamName, agentsOfCurrentTeam);
                }
            }
        } catch (FileNotFoundException e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
        } catch (ParserConfigurationException | TransformerFactoryConfigurationError | IOException | SAXException | IllegalArgumentException | SecurityException e) {
            System.err.println("Erreur lors du chargement depuis le fichier XML.");
            e.printStackTrace();
        } catch (XPathExpressionException e) {
            System.err.println("Wrong XPath : " + teamsPath);
            e.printStackTrace();
        }

        return toReturn;
    }

    public List<Team> getTeamsToLoad() {
        List<Team> teamsToLoad = new ArrayList<Team>();

        for (String teamName : xmlSituationFileContent.keySet()) {
            if (!teamName.equals(MotherNatureTeam.NAME)) {
                Team currentTeam = Shared.getAvailableTeams().get(Team.getRealNameFromTeamName(teamName));
                // On vérifie si le jar de l'équipe existe
                if (currentTeam == null) {
                    System.err.println("Le fichier JAR de l'équipe " + Team.getRealNameFromTeamName(teamName) + " est manquant.");
                    System.exit(0);
                } else {
                    if (!teamsToLoad.contains(currentTeam))
                        teamsToLoad.add(currentTeam);
                    else
                        teamsToLoad.add(currentTeam.duplicate(teamName));
                }
            }
        }

        return teamsToLoad;
    }

    public void launchAllAgentsFromXmlSituationFile(WarLauncher launcher) {
        WarGame game = Shared.getGame();
        ArrayList<Team> teams = game.getPlayerTeams();
        teams.add(game.getMotherNatureTeam());

        try {
            for (Team t : teams) {
                ArrayList<HashMap<String, String>> agentsOfCurrentTeam = xmlSituationFileContent.get(t.getName());
                for (HashMap<String, String> agentDatas : agentsOfCurrentTeam) {
                    WarAgent agent;
                    String agentTypeName = agentDatas.get("Type");
                    try {
                        if (WarAgentType.valueOf(agentTypeName).getCategory() == WarAgentCategory.Resource) {
                            agent = game.getMotherNatureTeam().instantiateNewWarResource(agentTypeName);
                        } else if (WarAgentType.valueOf(agentTypeName).isControllable()) {
                            agent = t.instantiateNewControllableWarAgent(agentTypeName);
                        } else {
                            agent = t.instantiateNewBuilding(agentTypeName);
                        }
                        launcher.launchAgent(agent);
                        agent.setPosition(Double.valueOf(agentDatas.get("xPosition")), Double.valueOf(agentDatas.get("yPosition")));
                        if (agent instanceof ControllableWarAgent) {
                            agent.setHeading(Double.valueOf(agentDatas.get("Heading")));
                            ((ControllableWarAgent) agent).setViewDirection(Double.valueOf(agentDatas.get("ViewDirection")));
                            ((ControllableWarAgent) agent).init(Integer.valueOf(agentDatas.get("Health")),
                                    Integer.valueOf(agentDatas.get("NbElementsInBag")));
                        } else if (agent instanceof AliveWarAgent) {
                            ((AliveWarAgent) agent).init(Integer.valueOf(agentDatas.get("Health")));
                        }
                    } catch (InvocationTargetException | NoSuchMethodException | ClassNotFoundException e) {
                        System.err.println("Erreur lors de l'instanciation de l'agent. Type non reconnu : " + agentTypeName);
                        e.printStackTrace();
                    }
                }
            }
        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | SecurityException e) {
            System.err.println("Erreur lors de l'instanciation des classes à partir des données XML");
            e.printStackTrace();
        }
    }
}
