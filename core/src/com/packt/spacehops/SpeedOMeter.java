package com.packt.spacehops;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

class SpeedOMeter {

    //Textures
    private final TextureRegion backgroundTexture;
    private static final int TILE_WIDTH = 21;			//The width of each tile in the texture
    private static final int TILE_HEIGHT = 21;			//The height of each tile in the texture
    private final TextureRegion[][] lightsTexture;

    private int state = 0;

    SpeedOMeter(TextureRegion backgroundTextureRegion, TextureRegion lightsTextures){
        this.backgroundTexture = backgroundTextureRegion;
        this.lightsTexture = new TextureRegion(lightsTextures).split(TILE_WIDTH, TILE_HEIGHT);
    }

    void updateState(float speed){
        if(speed > 0){state = 0;}
        if(speed < 0 && speed > -10){state = 1;}
        if(speed < -10){state = 2;}
    }

    int getState(){return state;}

    void draw(SpriteBatch batch){
        batch.draw(backgroundTexture, 320-backgroundTexture.getRegionWidth(), 0);
        if(state == 0) {
            batch.draw(lightsTexture[0][2], 296, 2);
        }
        else if(state == 1){
            batch.draw(lightsTexture[0][1], 296, 2);
        }
        else{
            batch.draw(lightsTexture[0][0], 296, 2);
        }
    }
}
