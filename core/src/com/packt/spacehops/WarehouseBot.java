package com.packt.spacehops;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;

class WarehouseBot {

    //Texture and Animation
    private static final int TILE_WIDTH = 320;			//The width of each tile in the texture
    private static final int TILE_HEIGHT = 73;			//The height of each tile in the texture
    private TextureRegion[][] botTexture;
    private TextureRegion armTexture;

    private static final float FRAME_DURATION = .15f;
    private float animationTime = 0;
    private final Animation animation;

    private Rectangle leftArm;
    private Rectangle rightArm;

    private boolean swinging = false;
    boolean onFlag = false;
    boolean startSpinning = false;
    int spinGoal = 0;
    int spinCounter = 0;
    boolean clicked = false;

    //Timing variables
    private static final float MOVE_TIME = 10F;                 //Time that the conversation box stays on screen
    private float moveTimer = MOVE_TIME;                        //Counter that checks if it reached the end of time

    WarehouseBot(TextureRegion robotBody, TextureRegion armTexture){
        botTexture = new TextureRegion(robotBody).split(TILE_WIDTH, TILE_HEIGHT); //Breaks down the texture into tiles

        animation = new Animation<>(FRAME_DURATION,
                this.botTexture[5][0], this.botTexture[6][0], this.botTexture[7][0],
                this.botTexture[9][0], this.botTexture[8][0], this.botTexture[0][0],
                this.botTexture[1][0], this.botTexture[2][0], this.botTexture[3][0],
                this.botTexture[4][0]);
        animation.setPlayMode(Animation.PlayMode.LOOP);

        leftArm = new Rectangle(320, 200, 114, 66);
        rightArm = new Rectangle(320 + 114 + 60, 200, 114, 66);

        this.armTexture = armTexture;

    }

    void turnOn(){
        onFlag = true;
        spinGoal = MathUtils.random(3,6);
    }

    void turnOff(){  onFlag = false;}

    void update(float delta){
        System.out.println(spinGoal + " " + spinCounter + " " + onFlag);

        if(spinGoal == spinCounter){
            spinCounter = 0;
            turnOff();
        }

       if(startSpinning){
           if(onFlag){animationTime += delta;}
           else {
               moveTimer -= delta;
               if (moveTimer <= 0) {
                   moveTimer = MOVE_TIME;
                   turnOn();
               }
           }
       }

       if(swinging){
           updateArmPosition();
           if(checkIfLeftScreen()){
                swinging = false;
                restartArms();
           }
       }
    }

    boolean isColliding(SpaceCraft spaceCraft){
        Circle spaceCraftCollisionCircle = spaceCraft.getCollisionCircle();
        return Intersector.overlaps(spaceCraftCollisionCircle, leftArm) ||
                Intersector.overlaps(spaceCraftCollisionCircle, rightArm);
    }

    void setStartSpinning(){startSpinning = true;}

    private void updateArmPosition(){
        leftArm.x -= 12;
        rightArm.x -= 12;
    }

    private boolean checkIfLeftScreen(){
        return rightArm.x + rightArm.width < 0;
    }

    private void restartArms(){
        leftArm.x = 320;
        rightArm.x = 320 + 114 + 60;
    }

    void restart(){
        animationTime = 0;
        moveTimer = MOVE_TIME;
        onFlag = false;
        startSpinning = false;
        restartArms();
    }

    void draw(SpriteBatch batch) {
        TextureRegion textureRegion = (TextureRegion) animation.getKeyFrame(animationTime);
        batch.draw(textureRegion, 160 - (float) textureRegion.getRegionWidth()/2, 240);

        if(textureRegion == botTexture[9][0]){ swinging = true; }

        if(textureRegion == botTexture[5][0] && onFlag && !clicked){
            clicked = true;
            spinCounter++;
        }
        else if(textureRegion == botTexture[4][0] && clicked) { clicked = false; }

        if(swinging){
            batch.draw(armTexture, leftArm.x, leftArm.y);
            batch.draw(armTexture, rightArm.x, rightArm.y);
        }
    }


}
