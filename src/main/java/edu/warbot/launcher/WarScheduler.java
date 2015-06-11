package edu.warbot.launcher;

import edu.warbot.agents.WarAgent;
import edu.warbot.agents.enums.WarAgentType;
import edu.warbot.game.Team;
import edu.warbot.game.WarGame;
import edu.warbot.game.WarGameListener;
import edu.warbot.launcher.WarMain.Shared;
import madkit.action.KernelAction;
import madkit.agr.LocalCommunity;
import madkit.agr.LocalCommunity.Groups;
import madkit.agr.Organization;
import madkit.message.KernelMessage;
import madkit.simulation.activator.GenericBehaviorActivator;
import turtlekit.agr.TKOrganization;
import turtlekit.kernel.TKScheduler;

public class WarScheduler extends TKScheduler implements WarGameListener {

    // Délai initial entre chaque tick. Evite que le jeu aille trop vite.
    public static final int INITIAL_DELAY = 10;

    private GenericBehaviorActivator<WarAgent> _warAgentDoBeforeEachTickActivator;
    private WarGame game;
    private boolean isGameOver;

    public WarScheduler() {
        this.game = Shared.getGame();
    }

    @Override
    protected void activate() {
        super.activate();

        _warAgentDoBeforeEachTickActivator = new GenericBehaviorActivator<>(community, TKOrganization.TURTLES_GROUP, TKOrganization.TURTLE_ROLE, "doBeforeEachTick");
        addActivator(_warAgentDoBeforeEachTickActivator);

        setDelay(INITIAL_DELAY);

        game.addWarGameListener(this);
    }

    @Override
    public void doSimulationStep() {
        if (!isGameOver) {
            if (logger != null) {
                logger.finest("Doing simulation step " + getGVT());
            }

//		logger.finest("Activating --------> " + getPheroMaxReset());
//		getPheroMaxReset().execute();
            logger.finest("Activating --------> " + getWarAgentDoBeforeEachTickActivator());
            getWarAgentDoBeforeEachTickActivator().execute();
            logger.finest("Activating --------> " + getTurtleActivator());
            getTurtleActivator().execute();
//		logger.finest("Activating --------> " + getEnvironmentUpdateActivator());
//		getEnvironmentUpdateActivator().execute();
            logger.finest("Activating --------> " + getViewerActivator());
            getViewerActivator().execute();

            setGVT(getGVT() + 1.0D);

            // Apparition de WarResource
            if (getGVT() % game.getSettings().getFoodAppearanceRate() == 0) {
                game.getMotherNatureTeam().createAndLaunchNewResource(game.getMap(), this, WarAgentType.WarFood);
            }

            game.doAfterEachTick();
        }
    }

    protected GenericBehaviorActivator<WarAgent> getWarAgentDoBeforeEachTickActivator() {
        return _warAgentDoBeforeEachTickActivator;
    }

    @Override
    public void onNewTeamAdded(Team newTeam) {
    }

    @Override
    public void onTeamLost(Team removedTeam) {
    }

    @Override
    public void onGameOver() {
        setSimulationState(SimulationState.PAUSED);
        isGameOver = true;
    }

    @Override
    public void onGameStopped() {
        game.removeWarGameListener(this);
        sendMessage(
                LocalCommunity.NAME,
                Groups.SYSTEM,
                Organization.GROUP_MANAGER_ROLE,
                new KernelMessage(KernelAction.EXIT));
    }

    @Override
    public void onGameStarted() {
    }

    public WarGame getGame() {
        return game;
    }

    public void setGame(WarGame game) {
        this.game = game;
    }
}
