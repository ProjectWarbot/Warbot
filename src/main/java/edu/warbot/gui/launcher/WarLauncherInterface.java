package edu.warbot.gui.launcher;

import edu.warbot.agents.enums.WarAgentCategory;
import edu.warbot.agents.enums.WarAgentType;
import edu.warbot.game.Team;
import edu.warbot.game.WarGame;
import edu.warbot.game.WarGameMode;
import edu.warbot.game.WarGameSettings;
import edu.warbot.gui.GuiIconsLoader;
import edu.warbot.gui.launcher.mapSelection.MapMiniature;
import edu.warbot.gui.launcher.mapSelection.MapSelectionListener;
import edu.warbot.launcher.UserPreferences;
import edu.warbot.launcher.WarMain;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.logging.Level;

@SuppressWarnings("serial")
public class WarLauncherInterface extends JFrame {

    public static final String TEXT_ADDED_TO_DUPLICATE_TEAM_NAME = "_bis";

    // Eléments de l'interface
    private HashMap<WarAgentType, NbWarAgentSlider> sliderNbAgents;
    private HashMap<WarAgentType, NbWarAgentSlider> foodCreationRates;

    private PnlTeamSelection pnlSelectionTeam1;
    private PnlTeamSelection pnlSelectionTeam2;

    private MapMiniature currentDisplayedMapMiniature;

    private JButton boutonQuitter;
    private JButton boutonValider;

    private JComboBox<String> cbLogLevel;

    private JCheckBox cbEnhancedGraphismEnabled;

    private WarMain warMain;
    private WarGameSettings settings;

    public WarLauncherInterface(WarMain warMain, WarGameSettings settings) {
        super("Warbot 3D");
        this.settings = settings;
        this.warMain = warMain;

		/* *** Fenêtre *** */
        setSize(1000, 700);
        setMinimumSize(new Dimension(600, 400));
        setLocationRelativeTo(null);
        setIconImage(GuiIconsLoader.getLogo("iconLauncher.png").getImage());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        setContentPane(mainPanel);

        // TODO
        setJMenuBar(new InterfaceLauncherMenuBar(this));

		/* *** Haut : Titre *** */
        JPanel panelTitre = new JPanel();
        JLabel imageTitre = new JLabel(GuiIconsLoader.getWarbotLogo());
        panelTitre.add(imageTitre);
        mainPanel.add(panelTitre, BorderLayout.NORTH);

		/* *** Droite : Options *** */
        JPanel panelOption = new JPanel();
        panelOption.setPreferredSize(new Dimension(300, panelOption.getPreferredSize().height));
        panelOption.setLayout(new BorderLayout());
        panelOption.setBorder(new TitledBorder("Options"));

        JPanel pnlNbUnits = new JPanel();
        pnlNbUnits.setLayout(new BoxLayout(pnlNbUnits, BoxLayout.Y_AXIS));
        sliderNbAgents = new HashMap<>();

        // Controllables
        for (WarAgentType a : WarAgentType.getControllableAgentTypes()) {
            NbWarAgentSlider slider = new NbWarAgentSlider("Nombre de " + a.toString(),
                    0, 30, UserPreferences.getNbAgentsAtStartOfType(a.toString()), 1, 10);
            sliderNbAgents.put(a, slider);
            pnlNbUnits.add(slider);
        }

        // Ressources
        foodCreationRates = new HashMap<>();
        for (WarAgentType a : WarAgentType.getAgentsOfCategories(WarAgentCategory.Resource)) {
            NbWarAgentSlider slider = new NbWarAgentSlider(a.toString() + " tous les x ticks",
                    0, 500, 150, 1, 100);
            foodCreationRates.put(a, slider);
            pnlNbUnits.add(slider);
        }

        panelOption.add(new JScrollPane(pnlNbUnits), BorderLayout.CENTER);

        JPanel panelAdvanced = new JPanel();
        panelAdvanced.setLayout(new BorderLayout());
        panelAdvanced.setBorder(new TitledBorder("Avancé"));
        String comboOption[] = {"ALL", "SEVERE", "WARNING", "INFO", "CONFIG", "FINE", "FINER", "FINEST"};
        this.cbLogLevel = new JComboBox<String>(comboOption);
        cbLogLevel.setSelectedItem("WARNING");
        panelAdvanced.add(new JLabel("Niveau de détail des logs"), BorderLayout.NORTH);
        panelAdvanced.add(cbLogLevel, BorderLayout.CENTER);
        // graphisme
        cbEnhancedGraphismEnabled = new JCheckBox("Charger la vue 2D isométrique", false);
        panelAdvanced.add(cbEnhancedGraphismEnabled, BorderLayout.SOUTH);

        panelOption.add(panelAdvanced, BorderLayout.SOUTH);

        JPanel panelMap = new JPanel();
        panelMap.setLayout(new BorderLayout());
        panelMap.setBorder(new TitledBorder("Carte"));
        currentDisplayedMapMiniature = new MapMiniature(this.settings.getSelectedMap(), MapMiniature.SIZE_SMALL);
        panelMap.add(currentDisplayedMapMiniature, BorderLayout.CENTER);
        JButton btnChooseMap = new JButton("Choisir une autre carte");
        btnChooseMap.addActionListener(new MapSelectionListener(this));
        panelMap.add(btnChooseMap, BorderLayout.EAST);
        panelOption.add(panelMap, BorderLayout.NORTH);

        mainPanel.add(panelOption, BorderLayout.EAST);

		/* *** Bas : Boutons *** */
        JPanel panelBas = new JPanel();

        boutonValider = new JButton("Valider");
        boutonValider.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                startGame();
            }
        });
        getRootPane().setDefaultButton(boutonValider);
        panelBas.add(boutonValider);

        boutonQuitter = new JButton("Quitter");
        boutonQuitter.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.exit(NORMAL);
            }
        });
        panelBas.add(boutonQuitter);

        mainPanel.add(panelBas, BorderLayout.SOUTH);

		/* *** Centre : Choix du mode et sélection des équipes *** */
        JTabbedPane tabbedPaneMillieu = new JTabbedPane();
        tabbedPaneMillieu.add(creerPanelModeClassic(), "Mode Duel");
        tabbedPaneMillieu.add(creerPanelModeMulti(), "Mode Tournoi");

        mainPanel.add(tabbedPaneMillieu, BorderLayout.CENTER);

        setVisible(true);
    }

    /**
     * Saves settings entered by the user in {@link WarGameSettings} instance
     */
    private void validEnteredSettings() {
        settings.setDefaultLogLevel((Level.parse((String) cbLogLevel.getSelectedItem())));

        for (WarAgentType agent : WarAgentType.values()) {
            NbWarAgentSlider slider = sliderNbAgents.get(agent);
            if (slider != null)
                settings.setNbAgentOfType(agent, slider.getSelectedValue());
        }
        settings.setFoodAppearanceRate(foodCreationRates.get(WarAgentType.WarFood).getSelectedValue());

        settings.setGameMode(WarGameMode.Duel); // TODO varier selon le choix dans l'interface

        settings.setEnabledEnhancedGraphism(cbEnhancedGraphismEnabled.isSelected());

        if (settings.getSituationLoader() == null) {
            // On récupère les équipes
            Team team1 = pnlSelectionTeam1.getSelectedTeam();
            Team team2 = pnlSelectionTeam2.getSelectedTeam();
            // Si c'est les mêmes équipes, on en duplique une en lui donnant un autre nom
            if (team1.equals(team2))
                team2 = team1.duplicate(team1.getName() + TEXT_ADDED_TO_DUPLICATE_TEAM_NAME);
            // On ajoute les équipes au jeu
            settings.addSelectedTeam(team1);
            settings.addSelectedTeam(team2);
        } else {
            for (Team t : settings.getSituationLoader().getTeamsToLoad())
                settings.addSelectedTeam(t);
        }
    }

    /**
     * Valid entered settings, hide this window and starts the game
     */
    public void startGame() {
        validEnteredSettings();
        warMain.startGame();
        setVisible(false);
    }

    private JPanel creerPanelModeMulti() {
        JPanel toReturn = new JPanel(new GridLayout());

        // TODO

        return toReturn;
    }

    private JPanel creerPanelModeClassic() {
        JPanel toReturn = new JPanel(new GridLayout(1, 2));

        pnlSelectionTeam1 = new PnlTeamSelection("Choix de l'équipe 1", warMain.getAvailableTeams());
        toReturn.add(new JScrollPane(pnlSelectionTeam1));
        pnlSelectionTeam2 = new PnlTeamSelection("Choix de l'équipe 2", warMain.getAvailableTeams());
        toReturn.add(new JScrollPane(pnlSelectionTeam2));

        return toReturn;
    }

    public JCheckBox getEnableEnhancedGraphism() {
        return cbEnhancedGraphismEnabled;
    }

    public void displayGameResults(WarGame game) {
        new GameResultsDialog(game);
    }

    public WarGameSettings getGameSettings() {
        return settings;
    }

    public void notifySelectedMapChanged() {
        currentDisplayedMapMiniature.setMap(settings.getSelectedMap());
    }
}
