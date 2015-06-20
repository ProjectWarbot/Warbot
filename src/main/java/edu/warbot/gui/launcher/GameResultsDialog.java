package edu.warbot.gui.launcher;

import edu.warbot.game.InGameTeam;
import edu.warbot.game.WarGame;
import edu.warbot.game.WarGameListener;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

public class GameResultsDialog extends JFrame implements ActionListener, WindowListener, WarGameListener {

    private WarGame game;

    public GameResultsDialog(WarGame game) {
        super("Fin du jeu !");
        setLayout(new BorderLayout());
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(false);
        addWindowListener(this);

        this.game = game;
        game.addWarGameListener(this);

        JPanel pnlResult = new JPanel(new BorderLayout());
        JPanel pnlWinners = new JPanel(new FlowLayout());
        for (InGameTeam t : game.getPlayerTeams())
            pnlWinners.add(new JLabel(t.getName(), t.getImage(), JLabel.CENTER));
        pnlResult.add(pnlWinners, BorderLayout.CENTER);
        // Cas où il y n'y a plus qu'une équipe en jeu
        if (game.getPlayerTeams().size() == 1) {
            // On la déclare vainqueur
            pnlResult.add(new JLabel("Victoire de :"), BorderLayout.NORTH);
        } else {
            // Sinon il y a ex-aequo !
            pnlResult.add(new JLabel("Ex-Aequo entre les équipes :"), BorderLayout.NORTH);
        }
        add(pnlResult, BorderLayout.CENTER);

        JButton btnOk = new JButton("Ok");
        add(btnOk, BorderLayout.SOUTH);
        btnOk.addActionListener(this);

        pack();
        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        dispose();
    }

    @Override
    public void windowOpened(WindowEvent e) {
    }

    @Override
    public void windowClosing(WindowEvent e) {
    }

    @Override
    public void windowClosed(WindowEvent e) {
        game.stopGame();
    }

    @Override
    public void windowIconified(WindowEvent e) {
    }

    @Override
    public void windowDeiconified(WindowEvent e) {
    }

    @Override
    public void windowActivated(WindowEvent e) {
    }

    @Override
    public void windowDeactivated(WindowEvent e) {
    }

    @Override
    public void onNewTeamAdded(InGameTeam newInGameTeam) {
    }

    @Override
    public void onTeamLost(InGameTeam removedInGameTeam) {
    }

    @Override
    public void onGameOver() {
    }

    @Override
    public void onGameStopped() {
        dispose();
    }

    @Override
    public void onGameStarted() {
    }
}
