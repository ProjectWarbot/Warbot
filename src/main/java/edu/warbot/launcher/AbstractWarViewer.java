package edu.warbot.launcher;

import edu.warbot.agents.ControllableWarAgent;
import edu.warbot.agents.WarAgent;
import edu.warbot.agents.percepts.WarAgentPercept;
import edu.warbot.game.WarGame;
import edu.warbot.gui.viewer.MapExplorationListener;
import edu.warbot.gui.viewer.WarToolBar;
import edu.warbot.gui.viewer.debug.DebugModePanel;
import edu.warbot.gui.viewer.stats.GameStatsPanel;
import edu.warbot.tools.geometry.CoordCartesian;
import edu.warbot.tools.geometry.CartesianCoordinates;
import madkit.simulation.probe.SingleAgentProbe;
import turtlekit.agr.TKOrganization;
import turtlekit.kernel.TKScheduler;
import turtlekit.viewer.AbstractGridViewer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseWheelListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;

@SuppressWarnings("serial")
public abstract class AbstractWarViewer extends AbstractGridViewer {

    protected static final int DEFAULT_CELL_SIZE = 1;

    private WarToolBar warToolBar;
    private DebugModePanel debugModePanel;
    private GameStatsPanel gameStatsPanel;

    private MapExplorationListener mapExplorationMouseListener;

    private ArrayList<Integer> agentsIDsSeenBySelectedAgent;

    private double mapOffsetX, mapOffsetY;

    private WarGame game;

    public AbstractWarViewer() {
        super();
        this.game = Shared.getGame();
        warToolBar = new WarToolBar(this);
        debugModePanel = new DebugModePanel(this);
        gameStatsPanel = new GameStatsPanel(game);
        agentsIDsSeenBySelectedAgent = new ArrayList<>();
    }

    @Override
    protected void activate() {
        super.activate();
        setSynchronousPainting(false);
    }

    @Override
    public void setupFrame(final JFrame frame) {
        super.setupFrame(frame);
        for (MouseWheelListener listener : getDisplayPane().getMouseWheelListeners())
            getDisplayPane().removeMouseWheelListener(listener);
        setDisplayPane(new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                render(g);
            }
        });
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                game.stopGame();
            }
        });
        frame.getContentPane().remove(((BorderLayout) frame.getContentPane().getLayout()).getLayoutComponent(BorderLayout.PAGE_START));
        frame.add(createJToolBar(), BorderLayout.PAGE_START);
        frame.setJMenuBar(createJMenuBar());

        frame.setTitle("Warbot");

        debugModePanel.init(frame);
        gameStatsPanel.init(frame);

        setCellSize(DEFAULT_CELL_SIZE);
        getDisplayPane().setSize(new Dimension(getWidth(), getHeight()));
        getDisplayPane().setBackground(new Color(230, 230, 230));

        mapExplorationMouseListener = new MapExplorationListener(this);
        getDisplayPane().addMouseMotionListener(mapExplorationMouseListener);
        getDisplayPane().addMouseListener(mapExplorationMouseListener);
        getDisplayPane().addMouseWheelListener(mapExplorationMouseListener);

        frame.setExtendedState(Frame.MAXIMIZED_BOTH);
        moveMapOffsetTo((Toolkit.getDefaultToolkit().getScreenSize().width - game.getMap().getWidth()) / 2.,
                (frame.getContentPane().getHeight() - game.getMap().getHeight()) / 2.);
    }

    protected JMenuBar createJMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        menuBar.add(debugModePanel.getDebugMenu());

        return menuBar;
    }

    protected JToolBar createJToolBar() {
        JToolBar toolBar = new JToolBar();
        toolBar.setFloatable(false);

        SingleAgentProbe<TKScheduler, Double> p = new SingleAgentProbe<>(getCommunity(), TKOrganization.ENGINE_GROUP, TKOrganization.SCHEDULER_ROLE, "GVT");
        addProbe(p);
        final TKScheduler tkScheduler = p.getCurrentAgentsList().get(0);

        toolBar.add(warToolBar);
        toolBar.addSeparator();
        toolBar.add(debugModePanel.getDebugModeToolBar());
        toolBar.addSeparator();
        toolBar.add(gameStatsPanel.getStatsToolBar());
        toolBar.addSeparator();
        toolBar.add(getToolBar());
        getToolBar().setFloatable(false);
        toolBar.addSeparator();
        JToolBar schedulerToolBar = tkScheduler.getSchedulerToolBar();
        schedulerToolBar.setFloatable(false);
        toolBar.add(schedulerToolBar);
        toolBar.add(tkScheduler.getGVTLabel());

        return toolBar;
    }


    @Override
    protected void render(Graphics g) {
        if (getDebugModePanel().getDebugTools().getSelectedAgent() != null) {
            // Update de l'affichage des infos sur l'unité sélectionnée
            getDebugModePanel().getDebugTools().getAgentInformationsPanel().update();
            // On récupère la liste des agents vus par l'agent sélectionné
            WarAgent selectedAgent = getDebugModePanel().getDebugTools().getSelectedAgent();
            if (selectedAgent instanceof ControllableWarAgent) {
                for (WarAgentPercept p : ((ControllableWarAgent) selectedAgent).getPercepts())
                    agentsIDsSeenBySelectedAgent.add(p.getID());
            }
        }
    }

    public void moveMapOffsetTo(double newOffsetX, double newOffsetY) {
        this.mapOffsetX = Math.max(Math.min(newOffsetX, getDisplayPane().getWidth() - 200), 200 - getDisplayedMapWidth());
        this.mapOffsetY = Math.max(Math.min(newOffsetY, getDisplayPane().getHeight() - 200), 200 - getDisplayedMapHeight());
        getFrame().repaint();
    }

    public CoordCartesian convertClickPositionToMapPosition(double clicX, double clicY) {
        return new CoordCartesian((clicX - getMapOffsetX()) / cellSize, (clicY - getMapOffsetY()) / cellSize);
    }

    public CoordCartesian convertMapPositionToDisplayPosition(double mapX, double mapY) {
        return new CoordCartesian((mapX * cellSize) + getMapOffsetX(), (mapY * cellSize) + getMapOffsetY());
    }

    public double getMapOffsetX() {
        return mapOffsetX;
    }

    public double getMapOffsetY() {
        return mapOffsetY;
    }

    public double getDisplayedMapWidth() {
        return getWidth() * cellSize;
    }

    public double getDisplayedMapHeight() {
        return getHeight() * cellSize;
    }

    public DebugModePanel getDebugModePanel() {
        return debugModePanel;
    }

    public WarGame getGame() {
        return game;
    }

    public WarToolBar getWarToolBar() {
        return warToolBar;
    }

    protected ArrayList<Integer> getAgentsIDsSeenBySelectedAgent() {
        return agentsIDsSeenBySelectedAgent;
    }

    public void setMapExplorationEventsEnabled(boolean bool) {
        mapExplorationMouseListener.setOnlyRightClick(!bool);
    }

}
