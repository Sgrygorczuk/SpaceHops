package com.packt.spacehops;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;

public class SpaceHops extends Game {

	private final AssetManager assetManager = new AssetManager();
	private Settings settings = new Settings();

	AssetManager getAssetManager() { return assetManager; }

	Settings getSettings(){return  settings;}

	@Override
	public void create () {
		//Calls game screen
		setScreen(new LoadingScreen(this,0));
	}
}
