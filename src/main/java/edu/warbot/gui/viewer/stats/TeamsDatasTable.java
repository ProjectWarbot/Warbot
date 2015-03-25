package edu.warbot.gui.viewer.stats;

import edu.warbot.game.Team;
import edu.warbot.game.WarGame;
import edu.warbot.game.WarGameListener;

import javax.swing.*;

@SuppressWarnings("serial")
public class TeamsDatasTable extends JTable implements WarGameListener {

	private TeamsDatasTableModel _model;
	private WarGame game;
	
	public TeamsDatasTable(WarGame game) {
		super();
		this.game = game;
		
		setDefaultRenderer(Object.class, new DefaultCellRenderer());
		getTableHeader().setVisible(false);
		setRowHeight(20);
		
		_model = new TeamsDatasTableModel(game);
		setModel(_model);
		getColumnModel().getColumn(0).setCellRenderer(new HeaderCellRenderer());
		
		game.addWarGameListener(this);
	}
	
    @Override
    public void onNewTeamAdded(Team newTeam) {
        updateTeamsDataTable();
    }

    @Override
    public void onTeamLost(Team removedTeam) {
//        updateTeamsDataTable();
    }

    @Override
    public void onGameOver() {}

    @Override
    public void onGameStopped() {}

    @Override
    public void onGameStarted() {}

    private void updateTeamsDataTable() {
        _model = new TeamsDatasTableModel(game);
        setModel(_model);
        getColumnModel().getColumn(0).setCellRenderer(new HeaderCellRenderer());
    }
}
