package com.packt.spacehops;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;

public class SpaceHops extends Game {

	private final AssetManager assetManager = new AssetManager();

	AssetManager getAssetManager() { return assetManager; }

	@Override
	public void create () {
		//Calls game screen
		setScreen(new LoadingScreen(this,0));
	}
}
