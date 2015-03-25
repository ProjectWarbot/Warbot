package edu.warbot.gui.launcher.mapSelection;

import edu.warbot.gui.launcher.WarLauncherInterface;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MapSelectionListener implements ActionListener {

    private WarLauncherInterface warLauncherInterface;
    private MapSelectionDialog selectionDialog;

    public MapSelectionListener(WarLauncherInterface warLauncherInterface) {
        this.warLauncherInterface = warLauncherInterface;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        selectionDialog = new MapSelectionDialog(warLauncherInterface);
        selectionDialog.setVisible(true);
    }
}
