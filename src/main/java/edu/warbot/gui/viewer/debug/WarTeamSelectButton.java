package edu.warbot.gui.viewer.debug;

import edu.warbot.game.Team;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

@SuppressWarnings("serial")
public class WarTeamSelectButton extends JToggleButton implements ActionListener {

    private Team team;
    private DebugModePanel toolBar;

    // TODO Ajout d'une image des agents
    public WarTeamSelectButton(Team team, DebugModePanel toolBar) {
        super();
        this.team = team;
        this.toolBar = toolBar;

        setBackground(team.getColor());
        setIcon(team.getImage());
        setText(team.getName());
        setFont(new Font("Arial", Font.PLAIN, 10));
//		setPreferredSize(new Dimension(125, 50));

        this.addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        toolBar.getDebugTools().setSelectedTeamForNextCreatedAgent(team);
    }

}
