package edu.warbot.gui.viewer.stats;

import edu.warbot.game.WarGame;
import edu.warbot.gui.GuiIconsLoader;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GameStatsPanel extends JPanel {

    private WarGame game;

    private JToggleButton btnStatsToolBar;
    private TeamsDatasTable teamsDatasTable;

    public GameStatsPanel(WarGame game) {
        super();
        this.game = game;

        setLayout(new FlowLayout());
        setPreferredSize(new Dimension(400, getPreferredSize().height));
        setBorder(BorderFactory.createMatteBorder(0, 3, 0, 0, Color.BLACK));

        teamsDatasTable = new TeamsDatasTable(game);
        teamsDatasTable.setPreferredSize(new Dimension(getPreferredSize().width - 10, teamsDatasTable.getPreferredSize().height));
        add(teamsDatasTable);
    }

    public void init(JFrame frame) {
        frame.add(this, BorderLayout.EAST);
        setVisible(false);
    }

    public JToolBar getStatsToolBar() {
        JToolBar toolBar = new JToolBar();
        toolBar.setFloatable(false);

        btnStatsToolBar = new JToggleButton();
        btnStatsToolBar.setIcon(GuiIconsLoader.getIcon("stats.png"));
        btnStatsToolBar.setToolTipText("Voir les statistiques du jeu");
        btnStatsToolBar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                GameStatsPanel.this.setVisible(btnStatsToolBar.isSelected());
            }
        });
        toolBar.add(btnStatsToolBar);

        return toolBar;
    }

}
