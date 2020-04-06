package com.packt.spacehops;

import com.badlogic.gdx.Game;

public class SpaceHops extends Game {
	@Override
	public void create () {
		//Calls game screen
		setScreen(new StartScreen(this));
	}
}
