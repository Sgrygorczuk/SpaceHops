package com.packt.spacehops;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;

/*
Input: Void
Output: Void
Purpose: Class that deals with all variables connected to the Asteroids
*/
class Asteroids{

    /*
    Dimensions
     */
    private static final float DISTANCE_BETWEEN_FLOOR_AND_CEILING = 225F;
    private static final float COLLISION_RECTANGLE_WIDTH = 15f;
    private static final float COLLISION_RECTANGLE_HEIGHT = 225f;
    private static final float HEIGHT_OFFSET = -200f;
    private static final float ASTEROID_CIRCLE_RADIUS = 32f;
    private static final float COLLECTIBLE_CIRCLE_RADIUS = 20f;

    /*
    Objects
     */
    private final Circle floorCollisionCircle;
    private final Rectangle floorCollisionRectangle;
    private final Circle ceilingCollisionCircle;
    private final Rectangle ceilingCollisionRectangle;
    
    private final Circle collectibleCircle;

    //Position
    private float x = 0;

    /*
    Movement
     */
    private static final float MAX_SPEED_PER_SECOND = 100f;

    /*
    Flags
     */
    private boolean pointClaimed = false;

    /*
    Textures
     */
    private final Texture floorTexture;
    private final Texture ceilingTexture;

    //Texture and Animation
    private static final int TILE_WIDTH = 40;			//The width of each tile in the texture
    private static final int TILE_HEIGHT = 40;			//The height of each tile in the texture
    private static final float FRAME_DURATION = 0.25f;	//How long each tile lasts on screen
    private float animationTime = 0;
    private final Animation animation;

    /*
    Input: Delta
    Output: Void
    Purpose: Flower constructor, creates the rectangle and circle on top, and places it at -400 to 0 y
    */
    Asteroids(Texture floorTexture, Texture ceilingTexture, Texture collectibleTexture){
        this.ceilingTexture = ceilingTexture;
        this.floorTexture = floorTexture;

        TextureRegion[][] collectibleTextures = new TextureRegion(collectibleTexture).split(TILE_WIDTH, TILE_HEIGHT); //Breaks down the texture into tiles

        //Sets the animation to be the texture 0-3 and sets it to loop
        animation = new Animation<>(FRAME_DURATION, collectibleTextures[0][0], collectibleTextures[0][1], collectibleTextures[0][2], collectibleTextures[0][1]);
        animation.setPlayMode(Animation.PlayMode.LOOP);

        //Randomly decides how tall the bottom flower is then it initializes that bottom flower
        float y = MathUtils.random(HEIGHT_OFFSET);
        this.floorCollisionRectangle = new Rectangle(x, y,COLLISION_RECTANGLE_WIDTH, COLLISION_RECTANGLE_HEIGHT);
        this.floorCollisionCircle = new Circle((x + COLLISION_RECTANGLE_WIDTH)/2, y + COLLISION_RECTANGLE_HEIGHT ,ASTEROID_CIRCLE_RADIUS);

        //Uses the distance between flower to determine where how tall the other flower is going to be
        this.ceilingCollisionRectangle = new Rectangle(x, y + COLLISION_RECTANGLE_HEIGHT + DISTANCE_BETWEEN_FLOOR_AND_CEILING,COLLISION_RECTANGLE_WIDTH, COLLISION_RECTANGLE_HEIGHT);
        this.ceilingCollisionCircle = new Circle((x + COLLISION_RECTANGLE_WIDTH)/2, y + COLLISION_RECTANGLE_HEIGHT + DISTANCE_BETWEEN_FLOOR_AND_CEILING,ASTEROID_CIRCLE_RADIUS);

        this.collectibleCircle = new Circle((x + COLLISION_RECTANGLE_WIDTH)/2, y + COLLISION_RECTANGLE_HEIGHT + DISTANCE_BETWEEN_FLOOR_AND_CEILING/2, COLLECTIBLE_CIRCLE_RADIUS);
    }

    /*
    Input: Void
    Output: Returns X
    Purpose: Gives the Screen X coordinate
    */
    float getX(){ return x; }

    /*
    Input: Void
    Output: Returns Circe Radius
    Purpose: Gives the width of circle
    */
    float getWidth(){ return ASTEROID_CIRCLE_RADIUS;}

    /*
    Input: Void
    Output: Void
    Purpose: Gives the Screen X coordinate
    */
    void setPosition(float x){
        this.x = x;
        updateCollisionCirclePosition();
        updateCollisionRectanglePosition();
        collectibleCircle.setX(x);
    }

    /*
    Input: Void
    Output: Void
    Purpose: Updates the psotion of the circles
    */
    private void updateCollisionCirclePosition(){
        floorCollisionCircle.setX(x + floorCollisionRectangle.width/2);
        ceilingCollisionCircle.setX(x + floorCollisionRectangle.width/2);
    }

    /*
    Input: Void
    Output: Void
    Purpose: Updates the position of the rectangles
    */
    private void updateCollisionRectanglePosition(){
        floorCollisionRectangle.setX(x);
        ceilingCollisionRectangle.setX(x);
    }

    /*
    Input: Void
    Output: Void
    Purpose: Checks if flappy has intersected with any of the rectangles or circles
    */
    boolean isCollidingAsteroids(SpaceCraft spaceCraft) {
        Circle spaceCraftCollisionCircle = spaceCraft.getCollisionCircle();
        return Intersector.overlaps(spaceCraftCollisionCircle, floorCollisionCircle) ||
                Intersector.overlaps(spaceCraftCollisionCircle, ceilingCollisionCircle) ||
                Intersector.overlaps(spaceCraftCollisionCircle, ceilingCollisionRectangle) ||
                Intersector.overlaps(spaceCraftCollisionCircle, floorCollisionRectangle);
    }

    /*
    Input: Void
    Output: Void
    Purpose: Method that the object calls when being updated by the render
    */
    void update(float delta){
        animationTime += delta;
        setPosition(x-(MAX_SPEED_PER_SECOND * delta));
    }

    /*
    Input: Void
    Output: Void
    Purpose: Checks if the object is behind flappee
    */
    boolean isPointClaimed(){return pointClaimed;}

    /*
    Input: Void
    Output: Void
    Purpose: Sets flag that this object went past flappee
    */
    void markPointClaimed(){ pointClaimed = true;}

    /*
    Input: Void
    Output: Void
    Purpose: General function used to draw textures
    */
    void draw(SpriteBatch batch){
        drawFloor(batch);
        drawCeiling(batch);
    }

    /*
    Input: Void
    Output: Void
    Purpose: Adds the bottom flower texture
    */
    private void drawFloor(SpriteBatch batch){
        float textureX = floorCollisionCircle.x - floorTexture.getWidth()/2;
        float textureY = floorCollisionRectangle.y - 220 + floorCollisionCircle.radius;
        batch.draw(floorTexture, textureX, textureY);
    }

    /*
    Input: Void
    Output: Void
    Purpose: Adds the top flower texture
    */
    private void drawCeiling(SpriteBatch batch){
        float textureX = ceilingCollisionCircle.x - ceilingTexture.getWidth()/2;
        float textureY = ceilingCollisionRectangle.getY() - ASTEROID_CIRCLE_RADIUS;
        batch.draw(ceilingTexture, textureX, textureY);
    }

    void drawCollectable(SpriteBatch batch){
        TextureRegion collectibleTexture = (TextureRegion) animation.getKeyFrame(animationTime);
        batch.draw(collectibleTexture, collectibleCircle.x  - COLLECTIBLE_CIRCLE_RADIUS, collectibleCircle.y - COLLECTIBLE_CIRCLE_RADIUS);
    }

    /*
    Input: Void
    Output:Void
    Purpose: Draws the wireframe
*/
    void drawDebugAsteroid(ShapeRenderer shapeRenderer) {
        shapeRenderer.circle(floorCollisionCircle.x, floorCollisionCircle.y, floorCollisionCircle.radius);
        shapeRenderer.rect(floorCollisionRectangle.x, floorCollisionRectangle.y, floorCollisionRectangle.width, floorCollisionRectangle.height);
        shapeRenderer.circle(ceilingCollisionCircle.x, ceilingCollisionCircle.y, ceilingCollisionCircle.radius);
        shapeRenderer.rect(ceilingCollisionRectangle.x, ceilingCollisionRectangle.y, ceilingCollisionRectangle.width, ceilingCollisionRectangle.height);
    }

    void drawDebugCollectible(ShapeRenderer shapeRenderer){
        shapeRenderer.circle(collectibleCircle.x, collectibleCircle.y, collectibleCircle.radius);
    }
}