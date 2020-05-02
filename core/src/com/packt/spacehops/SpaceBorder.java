/*
The ScreenBoarder class draws boarders on the top and bottom of the screen that can be collided with
 */

package com.packt.spacehops;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;

class SpaceBorder {

    //Rectangle objects that keep track of where the textures are
    private final Rectangle floorRectangle;
    private final Rectangle ceilingRectangle;

    //Dimensions of the rectangle textures and world they move in
    private static final float WORLD_HEIGHT = 480;
    private static final float RECTANGLE_WIDTH = 320f;
    private static final float RECTANGLE_HEIGHT = 10;

    //Speed at which the textures move to the left
    private static final float SPEED = 1;

    //Textures
    private TextureRegion floorTexture;
    private TextureRegion ceilingTexture;

    /*
    Input: Void
    Output: Void
    Constructor that initializes a boarder
    */
    SpaceBorder(float initialX, TextureRegion top, TextureRegion bottom){
        //Sets up textures
        floorTexture = bottom;
        ceilingTexture = top;

        //Sets up rectangles
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

    /*
    Input: Void
    Output: Void
    Purpose: Updates the position of the boarder on screen
    */
    void updatePosition(){
        floorRectangle.x -= SPEED;
        ceilingRectangle.x -= SPEED;
    }

    /*
    Input: Void
    Output: Float X
    Purpose: Returns the x value
    */
    float getX(){return  floorRectangle.x;}

    /*
    Input: Batch
    Output: Void
    Purpose: Draws the textures
    */
    void draw(SpriteBatch batch){
        batch.draw(floorTexture, floorRectangle.x, floorRectangle.y, floorRectangle.width, floorRectangle.height);
        batch.draw(ceilingTexture, ceilingRectangle.x, ceilingRectangle.y,  floorRectangle.width, floorRectangle.height);
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
