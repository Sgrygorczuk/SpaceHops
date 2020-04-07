package com.packt.spacehops;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.MathUtils;

public class Collectible {

        private static final float COLLECTIBLE_CIRCLE_RADIUS = 20f;
        private static final float ASTEROID_RADIUS = 32f;
        private static final float Y_OFFSET = 200;

        private boolean collectedFlag = false;

        private final Circle collectibleCircle;

        //Position
        private float x = 0;
        private float y = 350;
        private static final float MAX_SPEED_PER_SECOND = 100f;

        //Texture and Animation
        private static final int TILE_WIDTH = 40;			//The width of each tile in the texture
        private static final int TILE_HEIGHT = 40;			//The height of each tile in the texture
        private static final float FRAME_DURATION = 0.25f;	//How long each tile lasts on screen
        private float animationTime = 0;
        private final Animation animation;

        Collectible(Texture collectibleTexture){

            TextureRegion[][] collectibleTextures = new TextureRegion(collectibleTexture).split(TILE_WIDTH, TILE_HEIGHT); //Breaks down the texture into tiles

            //Sets the animation to be the texture 0-3 and sets it to loop
            this.animation = new Animation<>(FRAME_DURATION, collectibleTextures[0][0], collectibleTextures[0][1], collectibleTextures[0][2], collectibleTextures[0][1]);
            this.animation.setPlayMode(Animation.PlayMode.LOOP);

            this.collectibleCircle = new Circle(x,y,COLLECTIBLE_CIRCLE_RADIUS);
        }

        void setPosition(float x){
            collectibleCircle.setX(x);
            float y = MathUtils.random(Y_OFFSET);
            collectibleCircle.setY(this.y - y);
        }

        float getX(){return collectibleCircle.x;}

        float getRadius(){return COLLECTIBLE_CIRCLE_RADIUS;}

        float getAsteroidRadius(){return ASTEROID_RADIUS;}

    /*
    Input: Void
    Output: Void
    Purpose: Checks if flappy has intersected with any of the rectangles or circles
    */
    boolean isColliding(SpaceCraft spaceCraft) {
        Circle spaceCraftCollisionCircle = spaceCraft.getCollisionCircle();
        return Intersector.overlaps(spaceCraftCollisionCircle, collectibleCircle);
    }

    void setCollidingFlag(){ collectedFlag = true;}

    boolean getCollidingFlag(){return collectedFlag;}


        /*
        Input: Void
        Output: Void
        Purpose: Method that the object calls when being updated by the render
        */
        void update(float delta, SpaceCraft spaceCraft){
            animationTime += delta;
            isColliding(spaceCraft);
            updatePosition(collectibleCircle.x-(MAX_SPEED_PER_SECOND * delta));
        }

        /*
        Input: Void
        Output: Void
        Purpose: Gives the Screen X coordinate
        */
        void updatePosition(float x){ collectibleCircle.x = x;}


    /*
    Input: Void
    Output: Void
    Purpose: Checks if the object is behind flappee
    */
        // boolean isPointClaimed(){return pointClaimed;}

    /*
    Input: Void
    Output: Void
    Purpose: Sets flag that this object went past flappee
    */
        //void markPointClaimed(){ pointClaimed = true;}

        void draw(SpriteBatch batch){
            if(!collectedFlag) {
                TextureRegion collectibleTexture = (TextureRegion) animation.getKeyFrame(animationTime);
                batch.draw(collectibleTexture, collectibleCircle.x  - COLLECTIBLE_CIRCLE_RADIUS, collectibleCircle.y - COLLECTIBLE_CIRCLE_RADIUS);}
        }

        void drawDebug(ShapeRenderer shapeRenderer){ if(!collectedFlag) { shapeRenderer.circle(collectibleCircle.x, collectibleCircle.y, collectibleCircle.radius); }}}