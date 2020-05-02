package com.packt.spacehops.desktop;

import com.badlogic.gdx.Files;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.tools.texturepacker.TexturePacker;
import com.packt.spacehops.SpaceHops;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.height = 480;
		config.width = 320;
		config.resizable = true;
		config.addIcon("Title.png", Files.FileType.Internal);
		//Combines all images into asset packs

		/*
		TexturePacker.process("/home/sebastian/Projects/LibGDX_Personal/SpaceHops/android/assets/UI", "/home/sebastian/Projects/LibGDX_Personal/SpaceHops/android/assets", "ui_assets");
		TexturePacker.process("/home/sebastian/Projects/LibGDX_Personal/SpaceHops/android/assets/LevelOne", "/home/sebastian/Projects/LibGDX_Personal/SpaceHops/android/assets", "level_one_assets");
		TexturePacker.process("/home/sebastian/Projects/LibGDX_Personal/SpaceHops/android/assets/LevelTwo", "/home/sebastian/Projects/LibGDX_Personal/SpaceHops/android/assets", "level_two_assets");
		TexturePacker.process("/home/sebastian/Projects/LibGDX_Personal/SpaceHops/android/assets/MainScreen", "/home/sebastian/Projects/LibGDX_Personal/SpaceHops/android/assets", "main_screen_assets");
		TexturePacker.process("/home/sebastian/Projects/LibGDX_Personal/SpaceHops/android/assets/Profiles", "/home/sebastian/Projects/LibGDX_Personal/SpaceHops/android/assets", "profile_assets");
		TexturePacker.process("/home/sebastian/Projects/LibGDX_Personal/SpaceHops/android/assets/Ships", "/home/sebastian/Projects/LibGDX_Personal/SpaceHops/android/assets", "ship_assets");
		TexturePacker.process("/home/sebastian/Projects/LibGDX_Personal/SpaceHops/android/assets/Fonts", "/home/sebastian/Projects/LibGDX_Personal/SpaceHops/android/assets", "font_assets");
		*/

		new LwjglApplication(new SpaceHops(), config);
	}
}
