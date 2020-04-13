package com.packt.spacehops;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;

class SpaceBorder {

    private final Rectangle floorRectangle;
    private final Rectangle ceilingRectangle;

    private static final float WORLD_HEIGHT = 480;
    private static final float RECTANGLE_WIDTH = 320f;
    private static final float RECTANGLE_HEIGHT = 25;
    private static final float SPEED = 1;

    private Texture floorTexture;
    private Texture ceilingTexture;

    SpaceBorder(float initialX, Texture top, Texture bottom){
        floorTexture = bottom;
        ceilingTexture = top;

        this.floorRectangle = new Rectangle(initialX, 0, RECTANGLE_WIDTH, RECTANGLE_HEIGHT);
        this.ceilingRectangle = new Rectangle(initialX, WORLD_HEIGHT - RECTANGLE_HEIGHT, RECTANGLE_WIDTH, RECTANGLE_HEIGHT);
    }

    /*
    Input: Void
    Output: Void
    Purpose: Checks if spacecraft is colliding with either of the boarders
    */
    boolean isColliding(SpaceCraft spaceCraft) {
        Circle spaceCraftCollisionCircle = spaceCraft.getCollisionCircle();
        return Intersector.overlaps(spaceCraftCollisionCircle, floorRectangle) ||
                Intersector.overlaps(spaceCraftCollisionCircle, ceilingRectangle);
    }

    void updatePosition(){
        floorRectangle.x -= SPEED;
        ceilingRectangle.x -= SPEED;
    }

    float getX(){return  floorRectangle.x;}

    void draw(SpriteBatch batch){
        batch.draw(floorTexture, floorRectangle.x, floorRectangle.y);
        batch.draw(ceilingTexture, ceilingRectangle.x, ceilingRectangle.y);
    }
    /*
    Input: Void
    Output:Void
    Purpose: Draws the wireframe
    */
    void drawDebug(ShapeRenderer shapeRenderer) {
        shapeRenderer.rect(floorRectangle.x, floorRectangle.y,  floorRectangle.width, floorRectangle.height);
        shapeRenderer.rect(ceilingRectangle.x, ceilingRectangle.y, ceilingRectangle.width, ceilingRectangle.height);
    }

}
