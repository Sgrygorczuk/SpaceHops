package com.packt.spacehops;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;

;
public class FloatingJunk {

    TextureRegion junkTexture;

    Rectangle junkRectangle;

    boolean direction = false;
    private float yMax;
    private float yMin;
    private float OFFSET = 80;

    FloatingJunk(TextureRegion textureRegion){
        junkTexture = textureRegion;

        junkRectangle = new Rectangle(0,0,0,0);
    }

    void setStats(float x, float y, double dimensionMod){
        junkRectangle.x = x;
        junkRectangle.y = y;
        OFFSET = MathUtils.random(40,OFFSET);
        yMax = y + OFFSET;
        yMin = y - OFFSET;
        junkRectangle.width = (float) (junkTexture.getRegionWidth() * dimensionMod);
        junkRectangle.height = (float) (junkTexture.getRegionHeight()*dimensionMod);
    }

    void updatePosition(){
        if(junkRectangle.y > yMax){direction = false;}
        else if(junkRectangle.y < yMin){direction = true;}

        if(direction){ junkRectangle.y += 1; }
        else { junkRectangle.y -= 1; }
    }

    void draw(SpriteBatch batch){
        batch.draw(junkTexture, junkRectangle.x, junkRectangle.y, junkRectangle.getWidth(), junkRectangle.getHeight());
    }
}
