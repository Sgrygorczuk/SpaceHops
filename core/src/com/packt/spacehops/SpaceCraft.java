/*
The SpaceCraft class holds all of the information about the spaceships position and texture
 */

package com.packt.spacehops;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;

/*
Input: Void
Output: Void
Purpose: The class that keeps track of all of the information about our user object
*/
class SpaceCraft {
    //The circle class that will draw a circle on screen
    private final Circle collisionCircle;

    //Sets radius, and initial position
    private static final float COLLISION_RADIUS = 20f;		//Radius of the circle
    private float x = 0;									//Initial X
    private float y = 0;									//Initial Y

    //Movement - SpaceCraft only moves up and down along the y axis
    private static final float FLY_ACCELERATION = 5f;
    private static final float DIVE_ACCELERATION = 0.30f;
    private float ySpeed = 0;

    //Texture and Animation
    private static final int TILE_WIDTH = 32;			//The width of each tile in the texture
    private static final int TILE_HEIGHT = 32;			//The height of each tile in the texture
    private TextureRegion[][] spaceCraftTextures;

    /*
    Input: Void
    Output: Void
    Purpose: Constructed initializes a copy of the Flappee Bee
    */
    SpaceCraft(TextureRegion spaceCraftTexture) {
		/*
	Texture
	 */
        spaceCraftTextures = new TextureRegion(spaceCraftTexture).split(TILE_WIDTH, TILE_HEIGHT); //Breaks down the texture into tiles
        //Sets our object to be a circle
        collisionCircle = new Circle(x,y, COLLISION_RADIUS);
    }

    /*
    Input: Void
    Output: Returns X
    Purpose: Gives the Screen X coordinate
    */
    float getX(){ return x; }

    /*
    Input: Void
    Output: Returns collisionCircle
    Purpose: Gives user access to the collisionCircle
    */
    Circle getCollisionCircle(){ return collisionCircle;}

    /*
    Input: Void
    Output: Returns y
    Purpose: Gives the Screen y coordinate
    */
    float getY(){ return y; }

    /*
    Input: Void
    Output: Returns X
    Purpose: Gives the Screen X coordinate
    */
    float getRadius(){ return COLLISION_RADIUS; }

    /*
    Input: Void
    Output: Void
    Purpose: Updates the position and vertical speed of the spaceship
    */
    void update(){
        ySpeed -= DIVE_ACCELERATION;
        updatePosition(x, y + ySpeed);
    }

    /*
    Input: Void
    Output: Void
    Purpose: Updates the position variables of the spacescraft
    */
    void updatePosition(float x, float y){
        this.x = x;				 //Changes x variable in class
        this.y = y;				 //Changes y variable in class
        updateCollisionCircle();
    }

    /*
    Input: Void
    Output: Void
    Purpose: Updates the speed to be positive
    */
    void flyUp(){
        ySpeed = FLY_ACCELERATION;
        updatePosition(x, y + ySpeed);
    }

    /*
    Input: Void
    Output: Void
    Purpose: Resets the speed to 0
    */
    void restart(){ ySpeed = 0; }

    /*
    Input: Void
    Output: Void
    Purpose: Inputs the variables into the circle to be redrawn
    */
    private void updateCollisionCircle(){
        collisionCircle.setX(x);
        collisionCircle.setY(y);
    }

    /*
    Input: batch
    Output: Void
    Purpose: Inputs the variables into the circle to be redrawn
     */
    void draw(SpriteBatch batch){
        TextureRegion spaceCraftTexture = spaceCraftTextures[0][0];
        if(ySpeed > 0){spaceCraftTexture = spaceCraftTextures[0][1];}
        batch.draw(spaceCraftTexture, collisionCircle.x-COLLISION_RADIUS, collisionCircle.y-COLLISION_RADIUS, 2*collisionCircle.radius, 2*collisionCircle.radius);
    }

    /*
    Input: ShapeRenderer
    Output: Void
    Purpose: Draws the circle on the screen using render
    */
    void drawDebug(ShapeRenderer shapeRenderer) {
        shapeRenderer.circle(collisionCircle.x, collisionCircle.y, collisionCircle.radius);
    }
}