/*
Initially the Collectible class was used for only collectible but because of its generic
functionality it became generic round object class. For now it's too much work to remake it and update
all classes. Will need to overhaul later
 */

package com.packt.spacehops;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.MathUtils;

public class Collectible {

    //Generic Collectible size, and  off set
    private static final float COLLECTIBLE_CIRCLE_RADIUS = 20f;
    private static final float ASTEROID_RADIUS = 32f;
    private float yMax;
    private float yMin;
    private float y = 350;
    private static final float Y_OFFSET = 200;

    //Flags
    private boolean collectedFlag = false;  //Sets to be collected so that it no longer collied after first touch
    private boolean oscillating_horizontal = true;
    private int state = 0;

    //Circle object
    private final Circle collectibleCircle;

    //Speed
    private static final float MAX_SPEED_PER_SECOND = 100f;

    //Texture and Animation
    private static final int TILE_WIDTH = 40;			//The width of each tile in the texture
    private static final int TILE_HEIGHT = 40;			//The height of each tile in the texture
    private static final float FRAME_DURATION = 0.25f;	//How long each tile lasts on screen
    private float animationTime = 0;
    private final Animation animation;

    /*
    Input: Texture of the object
    Output: Void
    Purpose: Constructor that breaks down the texture nto frame and create the circle object
    */
    Collectible(TextureRegion collectibleTexture){
        TextureRegion[][] collectibleTextures = new TextureRegion(collectibleTexture).split(TILE_WIDTH, TILE_HEIGHT); //Breaks down the texture into tiles

        //Sets the animation to be the texture 0-3 and sets it to loop
        if(collectibleTextures.length > 1) {
            this.animation = new Animation<>(FRAME_DURATION, collectibleTextures[0][0], collectibleTextures[0][1], collectibleTextures[0][2], collectibleTextures[0][1]);
            this.animation.setPlayMode(Animation.PlayMode.LOOP);
        }
        else {
            this.animation = new Animation<>(FRAME_DURATION, collectibleTextures[0][0]);
        }

        //Position
        this.collectibleCircle = new Circle(0,y,COLLECTIBLE_CIRCLE_RADIUS);
    }

    /*
    Input: X, sets the x position but allows the object to get random y. For LEVEL 1
    Output: Void
    Purpose: Creates a object at a specific x with random y
    */
    void setPosition(float x){
        collectibleCircle.setX(x);
        float y = MathUtils.random(Y_OFFSET);
        collectibleCircle.setY(this.y - y);
    }

    void setBound(){
        yMax = collectibleCircle.y + collectibleCircle.radius + 15;
        yMin = collectibleCircle.y - 15;
    }

    /*
    Input: X, sets the x, y and height of the offset. Used for the Scales in LEVEL 2 for Dragon
    Output: Void
    Purpose: Creates a object at a specific x with bound of y and height for y
    */
    void setPosition(float x ,float y, float height){
        collectibleCircle.setX(x);
        float offset = MathUtils.random(height);
        collectibleCircle.setY(y - offset);
    }

    /*
    Input: X, sets the x, y and height of the offset. Used for the Fire for Dragon LEVEL 2
    Output: Void
    Purpose: Creates an object at specify x and y no offset
    */
    void setPosition(float x ,float y){
        collectibleCircle.setX(x);
        collectibleCircle.setY(y);
    }


    /*
    Input: Void
    Output: X
    Purpose: Returns x
    */
    float getX(){return collectibleCircle.x;}

    /*
    Input: Void
    Output: Y
    Purpose: Returns Y
    */
    float getY(){return collectibleCircle.y;}

    /*
    Input: Void
    Output: Radius
    Purpose: Returns Radius
    */
    float getRadius(){return COLLECTIBLE_CIRCLE_RADIUS;}

    /*
    Input: Void
    Output: Asteroid Radius
    Purpose: Returns Asteroid Radius, used for spacing in LEVEL 1
    */
    float getAsteroidRadius(){return ASTEROID_RADIUS;}

    /*
    Input: Void
    Output: Boolean
    Purpose: Returns the colliding Flag
    */
    boolean getCollidingFlag(){return !collectedFlag;}

    int getState(){return state;}

    /*
    Input: Void
    Output: Circle
    Purpose: Returns circle for collision checks
    */
    Circle getCollisionCircle(){return collectibleCircle;}

    /*
    Input: Void
    Output: Void
    Purpose: Checks if the object has collided
    */
    boolean isColliding(SpaceCraft spaceCraft) {
        Circle spaceCraftCollisionCircle = spaceCraft.getCollisionCircle();
        return Intersector.overlaps(spaceCraftCollisionCircle, collectibleCircle);
    }

    /*
    Input: Void
    Output: Void
    Purpose: Says the object has been collided with and can't do it anymore
    */
    void setCollidingFlag(){ collectedFlag = true;}

    /*
    Input: Void
    Output: Void
    Purpose: Sets a random radius to be within a range of ASTEROID_RADIUS/4, ASTEROID_RADIUS/2
    */
    void setRadius(){ collectibleCircle.radius = MathUtils.random(ASTEROID_RADIUS/4, ASTEROID_RADIUS/2);}

    /*
    Input: Radius
    Output: Void
    Purpose: Sets a passed in radius as radius
    */
    void setRadius(float radius){
        collectibleCircle.radius = radius;
    }

    void setState(int state){this.state = state;}

    /*
    Input: Void
    Output: Void
    Purpose: General update function for collectible
    */
    void update(float delta, SpaceCraft spaceCraft){
        updateAnimation(delta);                     //Update animation frame
        isColliding(spaceCraft);                    //Check if it hit anything
        updatePosition(collectibleCircle.x-(MAX_SPEED_PER_SECOND * delta)); //Move the object
    }

    /*
    Input: Delta
    Output: Void
    Purpose: Updates to new animation frame
    */
    void updateAnimation(float delta){animationTime += delta;}

    /*
    Input: X
    Output: Void
    Purpose: Updates position to that's passed in
    */
    private void updatePosition(float x){ collectibleCircle.x = x;}

    /*
    Input: Void
    Output: Void
    Purpose: Move the x by -3
    */
    void updatePosition(int speed){collectibleCircle.x -= speed;}

    void updateY(){
        checkYBounds();
        if(oscillating_horizontal){ collectibleCircle.y += 1; }
        else  { collectibleCircle.y -= 1; }
    }

    private void checkYBounds(){
        if(collectibleCircle.y + collectibleCircle.radius > yMax){oscillating_horizontal = false;}
        else if(collectibleCircle.y < yMin){oscillating_horizontal = true;}
    }

    /*
    Input: SpriteBatch
    Output: Void
    Purpose: Draws the object if hasn't collided
    */
    void draw(SpriteBatch batch){
        if(!collectedFlag) {
            TextureRegion collectibleTexture = (TextureRegion) animation.getKeyFrame(animationTime);
            batch.draw(collectibleTexture, collectibleCircle.x  - collectibleCircle.radius, collectibleCircle.y - collectibleCircle.radius,
                    2*collectibleCircle.radius, 2*collectibleCircle.radius);}
        }

    /*
    Input: ShapeRenderer
    Output: Void
    Purpose: Draws the wire frame
    */
    void drawDebug(ShapeRenderer shapeRenderer){ if(!collectedFlag) { shapeRenderer.circle(collectibleCircle.x, collectibleCircle.y, collectibleCircle.radius); }}}