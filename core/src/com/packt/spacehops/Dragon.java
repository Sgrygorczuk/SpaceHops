package com.packt.spacehops;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;

class Dragon {

    private static final float HEAD_WIDTH = 90f;
    private static final float HEAD_HEIGHT = 90f;
    private final Rectangle head;

    private Array<Collectible> scales = new Array<>();

    //0 Moving up and down, 1 prepare for attacking, 12 attacking, 3 coming back
    private int modeFlag = 0;

    private static final float OSCILLATING_Y_MAX = 440 - HEAD_HEIGHT;
    private static final float OSCILLATING_Y_MIN = 35;
    private static final float OSCILLATING_X_MAX = 90;
    private static final float OSCILLATING_X_MIN = 10;
    private static final float Y_SPEED = (OSCILLATING_Y_MIN+OSCILLATING_Y_MAX)/200;
    private static final float X_SPEED = (OSCILLATING_X_MIN+OSCILLATING_X_MAX)/50;

    private boolean oscillating_width = true; //Moving from min to max x
    private boolean oscillating_height = true; //Moving from min to max y

    private boolean oscillating_horizontal = true; //Moving from min to max y
    private float yMax = 0;
    private float yMin = 0;
    private static final float Y_OFFSET = 10;
    private static final float ATTACK_X = 400;

    //Timing variables
    private static final float MOVE_TIME = 10F;                 //Time that the conversation box stays on screen
    private float moveTimer = MOVE_TIME;                        //Counter that checks if it reached the end of time
    private static final float PREP_TIME = 0.5F;                  //Time that the conversation box stays on screen
    private float prepTimer = PREP_TIME;                        //Counter that checks if it reached the end of tim

    private Texture scaleTexture;
    private Texture headTexture;

    Dragon(Texture head, Texture scale){
        this.head = new Rectangle(0 + HEAD_WIDTH, 0 + HEAD_HEIGHT, HEAD_WIDTH, HEAD_HEIGHT);

        headTexture = head;
        scaleTexture = scale;
    }

    /*
    Input: Delta, timing
    Output: Void
    Purpose: Counts down until the communication screen turns off
    */
    private void updateMode(float delta) {
        if (modeFlag == 0){
                moveTimer -= delta;
                if (moveTimer <= 0) {
                    moveTimer = MOVE_TIME;
                    modeFlag = 1;
                }
            }
        if(modeFlag == 1){
                prepTimer -= delta;
                if (prepTimer <= 0) {
                    prepTimer = PREP_TIME;
                    setUpMinAndMax();
                    modeFlag = 2;
                }
             }
        if(modeFlag == 2 && head.x >= ATTACK_X-head.x){
            modeFlag = 3;
        }
        if(modeFlag == 3 && head.x <= 30){modeFlag = 0;}
    }

    void updatePosition(float delta){
        updateMode(delta);
        updateScale();
        updateYOscillatingDirection();
        updateXOscillatingDirection();
        if(modeFlag == 0){updateOscillating();}
        if(modeFlag == 2){
            updateMovement();
            updateHorizontalMomentOscillatingY();
            updateAttack();
        }
        if(modeFlag == 3) {
            updateMovement();
            updateHorizontalMomentOscillatingY();
            updateReturn();
        }
    }

    private void updateOscillating(){
        if(oscillating_width){head.x += X_SPEED;}
        else{head.x -= X_SPEED;}
        if(oscillating_height){head.y += Y_SPEED;}
        else {head.y -= Y_SPEED;}
    }

    private void updateYOscillatingDirection() {
        if(head.y >= OSCILLATING_Y_MAX || head.y <= OSCILLATING_Y_MIN) {
        oscillating_height = !oscillating_height;}
    }

    private void updateXOscillatingDirection() {
        if(head.x >= OSCILLATING_X_MAX || head.x <= OSCILLATING_X_MIN) {
            oscillating_width = !oscillating_width;}
    }

    private void updateMovement(){ if(head.y >= yMax || head.y <= yMin){oscillating_horizontal = !oscillating_horizontal;} }

    private void setUpMinAndMax(){
        yMax = head.y + Y_OFFSET;
        yMin = head.y - Y_OFFSET;
    }

    private void updateHorizontalMomentOscillatingY(){
        if(oscillating_horizontal){head.y += Y_SPEED;}
        else {head.y -= Y_SPEED;}
    }

    private void updateAttack(){head.x += 2;}

    private void updateReturn(){head.x -= 2;}

    private void updateScale(){
        createScale();
        updateScalePosition();
        removeScale();
    }

    private void updateScalePosition(){
        if(scales.size > 0){
            for(Collectible scale : scales){
                scale.updatePosition();
            }
        }
    }

    private void createScale(){
        Collectible scale = new Collectible(scaleTexture);
        scale.setPosition(head.x, head.y+head.height);
        scales.add(scale);
    }

    private void removeScale(){
        for (Collectible scale : scales){if(scale.getX() < 0){scales.removeValue(scale, true);}}
    }

    /*
    Input: Void
    Output: Void
    Purpose: Checks if spacecraft is colliding with either of the boarders
    */
    boolean isColliding(SpaceCraft spaceCraft) {
        Circle spaceCraftCollisionCircle = spaceCraft.getCollisionCircle();
        return Intersector.overlaps(spaceCraftCollisionCircle, head);
    }

    void draw(SpriteBatch batch) {
        batch.draw(headTexture, head.x, head.y);
        for (Collectible scale : scales){scale.draw(batch);}
    }


    /*
Input: Void
Output:Void
Purpose: Draws the wireframe
*/
    void drawDebug(ShapeRenderer shapeRenderer) {
        shapeRenderer.rect(head.x, head.y,  head.width, head.height);
        for (Collectible scale : scales){scale.drawDebug(shapeRenderer);}
    }
}
