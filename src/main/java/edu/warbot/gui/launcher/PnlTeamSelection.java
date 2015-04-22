package edu.warbot.gui.launcher;

import edu.warbot.game.Team;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Map;

@SuppressWarnings("serial")
public class PnlTeamSelection extends JPanel {

    private ArrayList<JRadioButton> _radioButtons;
    private Map<String, Team> availableTeams;

    public PnlTeamSelection(String title, Map<String, Team> availableTeams) {
        super();
        this.availableTeams = availableTeams;
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(new TitledBorder(title));

        _radioButtons = new ArrayList<>();
        ButtonGroup buttonGroup = new ButtonGroup();
        for (Team t : availableTeams.values()) {
            final JPanel pnlTeam = new JPanel();
            pnlTeam.setLayout(new FlowLayout(FlowLayout.LEFT));
            pnlTeam.setMaximumSize(new Dimension(500, 300));
            pnlTeam.setMinimumSize(new Dimension(300, 200));
            pnlTeam.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(184, 207, 229)));

            JRadioButton currentRadioButton = new JRadioButton();
            currentRadioButton.setName(t.getName());
            buttonGroup.add(currentRadioButton);
            _radioButtons.add(currentRadioButton);
            pnlTeam.add(currentRadioButton);
            pnlTeam.add(new JLabel(t.getImage()));
            JPanel pnlRight = new JPanel();
            pnlRight.setLayout(new GridLayout(2, 1));
            pnlRight.add(new JLabel(t.getName()));
            HiddenLongTextJLabel lblTeamDescription = new HiddenLongTextJLabel(t.getDescription(), 35, 2);
            lblTeamDescription.setFont(getFont().deriveFont(Font.PLAIN));
            lblTeamDescription.addMouseListener(new MouseAdapter() {
                public void mouseClicked(MouseEvent e) {
                    pnlTeam.dispatchEvent(SwingUtilities.convertMouseEvent(e.getComponent(), e, pnlTeam));
                }
            });
            pnlRight.add(lblTeamDescription);
            pnlTeam.add(pnlRight);
            pnlTeam.addMouseListener(new SetSelectedOnClickMouseListener(currentRadioButton));

            add(pnlTeam);
        }
        add(Box.createVerticalGlue());

        _radioButtons.get(0).setSelected(true);
    }

    public Team getSelectedTeam() {
        for (JRadioButton rb : _radioButtons) {
            if (rb.isSelected()) {
                return availableTeams.get(rb.getName());
            }
        }
        return null;
    }
}
