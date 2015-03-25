package edu.warbot.gui.launcher;

import edu.warbot.FSMEditor.FSMEditor;
import edu.warbot.gui.GuiIconsLoader;
import edu.warbot.gui.viewer.debug.eventlisteners.LoadSituationActionListener;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

@SuppressWarnings("serial")
public class InterfaceLauncherMenuBar extends JMenuBar {

	public InterfaceLauncherMenuBar(final WarLauncherInterface launcherInterface) {
		super();
		
		JMenu menuFile = new JMenu("Situation");
		menuFile.setMnemonic(KeyEvent.VK_S);
		
		JMenuItem itemOpen = new JMenuItem("Ouvrir", GuiIconsLoader.getIcon("open.png"));
        itemOpen.setMnemonic(KeyEvent.VK_O);
        itemOpen.setToolTipText("Charge une situation sauvegardée");
        itemOpen.addActionListener(new LoadSituationActionListener(launcherInterface));
        menuFile.add(itemOpen);
        
        add(menuFile);
        
        
		JMenu menuFSM = new JMenu("FSM");
		menuFSM.setMnemonic(KeyEvent.VK_F);
		
		JMenuItem itemCreateFSM = new JMenuItem("Créer une FSM");
		itemCreateFSM.setMnemonic(KeyEvent.VK_C);
		itemCreateFSM.setToolTipText("Lance l'outil de cr�ation d'architecture FSM pour Warbot");
		itemCreateFSM.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				new FSMEditor();
			}
		});
        menuFSM.add(itemCreateFSM);
        
        add(menuFSM);
	}
	
}
