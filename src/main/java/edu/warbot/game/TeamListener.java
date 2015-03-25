package edu.warbot.game;

import edu.warbot.agents.WarAgent;

public interface TeamListener {

    public void onAgentAdded(WarAgent newAgent);
    public void onAgentRemoved(WarAgent removedAgent);
}
