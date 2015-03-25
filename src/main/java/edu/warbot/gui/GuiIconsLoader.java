package edu.warbot.gui;

import javax.swing.*;
import java.io.File;

public class GuiIconsLoader {

	private static final String ASSETS_DIR = "assets";
	private static final String LOGO_DIR = ASSETS_DIR + File.separatorChar + "logos";
	private static final String ICONS_DIR = "assets" + File.separatorChar + "icons";
	
	public static ImageIcon getLogo(String fileName) {
		return new ImageIcon(LOGO_DIR + File.separatorChar + fileName);
	}
	
	public static ImageIcon getIcon(String fileName) {
		return new ImageIcon(ICONS_DIR + File.separatorChar + fileName);
	}
	
	public static ImageIcon getWarbotLogo() {
		return GuiIconsLoader.getLogo("logo.jpg");
	}
	
}
