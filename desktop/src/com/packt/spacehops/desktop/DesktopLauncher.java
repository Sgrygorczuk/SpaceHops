package com.packt.spacehops.desktop;

import com.badlogic.gdx.Files;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.packt.spacehops.SpaceHops;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.height = 480;
		config.width = 320;
		config.resizable = true;
		config.addIcon("Title.png", Files.FileType.Internal);
		new LwjglApplication(new SpaceHops(), config);

	}
}
