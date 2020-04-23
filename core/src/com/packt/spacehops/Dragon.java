/*
The Dragon class creates an enemy that hovers on the left side of the screen it moves up and down
attacking with fire attacks and biting the user
 */

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
import com.badlogic.gdx.utils.Array;

class Dragon {

    //Dimension's of the head of the snake
    private static final float HEAD_WIDTH = 90f;
    private static final float HEAD_HEIGHT = 2*90/3f;
    private static final float HORN_WIDTH = 90/4f;
    private static final float HORN_HEIGHT = 90/3f;
    private static final float LASER_HEIGHT = 14;

    //Rectangles that hold the head of snake positions
    private final Rectangle horn;
    private final Rectangle head;
    private final Rectangle eyeLaser;

    //Array of scales that flow off the dragon
    private Array<Collectible> scales = new Array<>();
    //Bullets that the dragon shoots
    private Array<Collectible> bullets = new Array<>();

    //Bounds why which the dragon moves around during phase 0
    private static final float OSCILLATING_Y_MAX = 440 - HEAD_HEIGHT;
    private static final float OSCILLATING_Y_MIN = 35;
    private static final float OSCILLATING_X_MAX = 90;
    private static final float OSCILLATING_X_MIN = 10;
    //Speed at which the dragon is moving between boarders
    private static final float Y_SPEED = (OSCILLATING_Y_MIN+OSCILLATING_Y_MAX)/200;
    private static final float X_SPEED = (OSCILLATING_X_MIN+OSCILLATING_X_MAX)/50;
    //Tells which direction its moving while in 0 state
    private boolean oscillating_width = true; //Moving from min to max x
    private boolean oscillating_height = true; //Moving from min to max y

    //When moving in for bit these are the variables during phase 2
    //Bounds
    private float yMax = 0;
    private float yMin = 0;
    private static final float ATTACK_X = 400;
    //Moving from min to max y
    private boolean oscillating_horizontal = true;
    //The position it started at
    private float y = 0;
    //Used to calculate the yMax and yMin
    private static final float Y_OFFSET = 10;

    //Timing variables
    //Timing variable for how long it's idling
    private static float MOVE_TIME = 5F;
    private float moveTimer = MOVE_TIME;
    //A pause time for user to get that dragon is going to attack
    private static final float PREP_TIME = 1F;
    private float prepTimer = PREP_TIME;
    //How long the shooting of the bullets last
    private static final float SHOOT_TIME_END = 1;
    private float shootTimerEnd = SHOOT_TIME_END;
    //Interval at which bullets are shot out
    private static final float SHOOT_TIME = 0.2F;
    private float shootTimer = SHOOT_TIME;
    //Interval at which bullets are shot out
    private static final float LASER_TIME = 1F;
    private float laserTimer = LASER_TIME;

    //Textures
    private Texture scaleTexture;
    private TextureRegion[][] headTexture;
    private Texture bulletTexture;
    private Texture laserTexture;

    //Texture and Animation
    private static final int HEAD_TILE_WIDTH = 120;			//The width of each tile in the texture
    private static final int HEAD_TILE_HEIGHT = 98;			//The height of each tile in the texture
    private static final float FRAME_DURATION = 0.1f;	//How long each tile lasts on screen
    private static final float EYE_FRAME_DURATION = .25f;
    private float animationTime = 0;
    private float laserAnimationTime = 0;
    private final Animation eyeAnimation;
    private final Animation laserAnimation;
    private final Animation mouthAnimation;

    private boolean eyeLaserFlag = true; //True = Star Shooting, False = Stop Shooting
    private boolean animationFlag = false; //False = Eye, True = Mouth
    private boolean modeLockedInFlag = false;
    //-2 Nothing, -1 Enter
    //0 Moving up and down, 1 prepare for attacking, 12 attacking, 3 coming back
    private int modeFlag = -2;
    private int futureModeFlag = 2;
    private int phaseFlag = 0;
    private int attackCounterFlag = 0;

    /*
    Input: Textures for head, bullet and scales
    Output: Void
    Purpose: Constructor makes necessary shapes and sets up textures
    */
    Dragon(Texture headTexture, Texture scaleTexture, Texture bulletTexture, Texture laserTexture){
        //Sets up the
        head = new Rectangle(-HEAD_WIDTH, 240, HEAD_WIDTH, HEAD_HEIGHT);
        horn = new Rectangle(-HEAD_WIDTH, 240, HORN_WIDTH, HEAD_HEIGHT + HORN_HEIGHT);
        eyeLaser = new Rectangle(-HEAD_WIDTH, 240+HORN_HEIGHT-LASER_HEIGHT/2, 0, LASER_HEIGHT);

        //Sets up textures
        this.bulletTexture = bulletTexture;
        this.scaleTexture = scaleTexture;
        this.laserTexture = laserTexture;
        this.headTexture = new TextureRegion(headTexture).split(HEAD_TILE_WIDTH, HEAD_TILE_HEIGHT); //Breaks down the texture into tiles

        eyeAnimation = new Animation<>(EYE_FRAME_DURATION, this.headTexture[0][0], this.headTexture[1][1],
                this.headTexture[1][2], this.headTexture[1][3]);
        eyeAnimation.setPlayMode(Animation.PlayMode.LOOP_PINGPONG);

        mouthAnimation = new Animation<>(FRAME_DURATION, this.headTexture[0][0], this.headTexture[0][1],
                this.headTexture[0][2], this.headTexture[0][3], this.headTexture[1][0]);
        mouthAnimation.setPlayMode(Animation.PlayMode.LOOP_PINGPONG);

        laserAnimation = new Animation<>(EYE_FRAME_DURATION, this.headTexture[0][0], this.headTexture[2][0],
                this.headTexture[2][1], this.headTexture[2][2]);
        mouthAnimation.setPlayMode(Animation.PlayMode.LOOP_PINGPONG);
    }

    /*
    Input: Void
    Output: Float X
    Purpose: Returns x to the user
    */
    float getX(){return head.x;}

    /*
    Input: Void
    Output: Float Y
    Purpose: Returns the y where the dragon stopped at to attack
    */
    float getCentralY(){return  y;}

    /*
    Input: Void
    Output: Float Height
    Purpose: Returns height of the dragon head
    */
    float getHeight(){return HEAD_HEIGHT;}

    /*
    Input: Void
    Output: Float Width
    Purpose: Returns width of the dragon head
    */
    float getWidth(){return HEAD_WIDTH;}

    void setStart(){modeFlag = -1;}

    void setPhase(int phase){
        phaseFlag = phase;
        if(phase == 0){ MOVE_TIME = 5F; }
        else if(phase == 1){ MOVE_TIME = 3F; }
        else{ MOVE_TIME = 1.5F; }
    }


    /*
    Input: Delta, timing
    Output: Void
    Purpose: Counts down when to switch between modes
    */
    private void updateMode(float delta) {
        //MODE -1 is dragon entering the screen
        if(modeFlag == -1 && head.x > HEAD_WIDTH - 10){
            modeFlag = 0;
            animationTime = 0;
        }
        else if(modeFlag == -1){
            animationTime += delta;
        }
        //MODE 0 is the dragon idling on the left side of the screen
        if (modeFlag == 0){
                //Resets the frame to have the whale have it's mouth closed
                if(mouthAnimation.getKeyFrame(animationTime) != headTexture[0][0]) {animationTime += delta;}
                //If it is closed start up the eye closing animation
                else { animationFlag = false; }
                //If eye closing is set to happen cycle through the frames
                if(!animationFlag){animationTime += delta;}
                moveTimer -= delta;
                if (moveTimer <= 0) {
                    moveTimer = MOVE_TIME;
                    animationFlag = true;
                    modeFlag = 1;
                }
            }
        //MODE 1 is the mode where dragon stops indicating to player that the
        //Dragons is about to attack, has random chance of shooting fire or biting
        else if(modeFlag == 1){
                prepTimer -= delta;
                //Open the mouth
                if(!modeLockedInFlag){
                    if(phaseFlag == 0){
                        futureModeFlag = 4;
                    }
                    else if (phaseFlag == 1){
                        if(attackCounterFlag == 3){
                            futureModeFlag = 4;
                            attackCounterFlag = 0;
                        }
                        else {
                            futureModeFlag = 2;
                            attackCounterFlag++;
                        }
                    }
                    else if(phaseFlag == 2){
                        if(attackCounterFlag == 3) {
                            futureModeFlag = 4;
                            attackCounterFlag = 0;
                        }
                        else if(attackCounterFlag == 0){
                            futureModeFlag = 3;
                            attackCounterFlag++;
                        }
                        else
                        {
                            futureModeFlag = MathUtils.random(2,3);
                            MOVE_TIME = MathUtils.random(1,4);
                            attackCounterFlag++;
                        }
                    }
                    modeLockedInFlag = true;
                }
                if(futureModeFlag == 3 && laserAnimation.getKeyFrame(laserAnimationTime) != headTexture[2][2]) {
                    laserAnimationTime += delta;
                }
                else if(mouthAnimation.getKeyFrame(animationTime) != headTexture[1][0]) {
                    animationTime += delta;}
                if (prepTimer <= 0) {
                    prepTimer = PREP_TIME;
                    if(futureModeFlag == 3){
                        eyeLaser.height = LASER_HEIGHT;
                        eyeLaser.x = horn.x + horn.width/2;
                        eyeLaser.y = horn.y + horn.height - LASER_HEIGHT;
                        eyeLaserFlag = true;
                        laserAnimationTime = 0;
                        animationTime = 0;
                    }
                    else{setUpMinAndMax();}          //Sets up bounds
                    modeFlag = futureModeFlag;
                    futureModeFlag = -1;
                    modeLockedInFlag = false;
                }
             }
        //MODE 2 is the dragon shooting has two timers one for end of MODE and one for
        //a pause between shots. After this it returns to idling.
        else if(modeFlag == 2){
            //Counts down between shots
            shootTimer -= delta;
            if (shootTimer <= 0) {
                shootTimer = SHOOT_TIME;
                createBullets();
            }
            //Counts down till it stops shooting
            shootTimerEnd -= delta;
            if (shootTimerEnd <= 0) {
                shootTimerEnd = SHOOT_TIME_END;
                modeFlag = 0;
            }
        }
        else if(modeFlag == 3) {
            if(!eyeLaserFlag && eyeLaser.width == 0){
                modeFlag = 0;
            }
        }
        //MODE 3 moves the dragon in for a bite
        else if(modeFlag == 4 && head.x >= ATTACK_X-head.x){
            modeFlag = 5;
        }
        //Returns the dragon back to idle position once back on left side of the screen
        else if(modeFlag == 5 && head.x <= 30){modeFlag = 0;}
    }

    /*
    Input: Delta, timing
    Output: Void
    Purpose: Updates all the systems that make the dragon work
    */
    void update(float delta){
        updateMode(delta);                          //Checks and changes what mode the dragon is in
        updateScale(delta);                         //Updates the creation, position and removal of scales
        updateBullets(delta);                       //Updates the creation, position and removal of bullets
        //Changes behavior based on mode
        if(modeFlag == -1){moveXForward();}
        //Idling
        else if(modeFlag == 0){
            updateYOscillatingDirection();              //Updates the Y movement direction
            updateXOscillatingDirection();              //Updates the X movement direction
            updateOscillating();                        //Updates x and y positions
        }
        else if(modeFlag == 3){
            updateEyeLaserPosition(delta);
        }
        //Biting
        else if(modeFlag == 4){
            animationTime += delta;
            updateMovement();                           //Updates the which direction its going in
            updateHorizontalMomentOscillatingY();       //Updates y position
            moveXForward();                           //Updates x position
        }
        //Returning
        else if(modeFlag == 5) {
            animationTime += delta;
            updateMovement();                           //Updates the which direction its going in
            updateHorizontalMomentOscillatingY();       //Updates y position
            moveXBackwards();                             //Updates x position
        }
    }

    /*
    Input: Void
    Output: Void
    Purpose: Moves the dragon head forward,
        used in MODE -1 and MODE 3 onto the screen from offscreen
    */
    private void moveXForward(){
        horn.x += X_SPEED;
        head.x += X_SPEED;
    }

    /*
    Input: Void
    Output: Void
    Purpose: Moves the dragon head backwards,
    used in MODE 4
    */
    private void moveXBackwards(){
        horn.x -= X_SPEED;
        head.x -= X_SPEED;}

    /*
    Input: Void
    Output: Void
    Purpose: Updates dragon head position while idling
    */
    private void updateOscillating(){
        //oscillating_width : true = right, false = left
        //Update X
        if(oscillating_width){
            horn.x += X_SPEED;
            head.x += X_SPEED;
        }
        else{
            horn.x -= X_SPEED;
            head.x -= X_SPEED;
        }
        //oscillating_height : true = up, false = down
        //Update Y
        if(oscillating_height){
            horn.y += Y_SPEED;
            head.y += Y_SPEED;
        }
        else {
            horn.y -= Y_SPEED;
            head.y -= Y_SPEED;
        }
    }

    /*
    Input: Void
    Output: Void
    Purpose: Updates the Y direction the dragon moves while oscillating idly
        oscillating_height : true = up, false = down
    */
    private void updateYOscillatingDirection() {
        if(head.y >= OSCILLATING_Y_MAX || head.y <= OSCILLATING_Y_MIN) {
        oscillating_height = !oscillating_height;}
    }

    /*
    Input: Void
    Output: Void
    Purpose: Updates the X direction the dragon moves while oscillating idly
        oscillating_width : true = up, false = down
    */
    private void updateXOscillatingDirection() {
        if(head.x >= OSCILLATING_X_MAX || head.x <= OSCILLATING_X_MIN) {
            oscillating_width = !oscillating_width;}
    }

    /*
    Input: Void
    Output: Void
    Purpose: Updates the Y direction while the dragon is moving in for bite
        oscillating_horizontal : true = up, false = down
    */
    private void updateMovement(){ if(head.y >= yMax || head.y <= yMin){oscillating_horizontal = !oscillating_horizontal;} }

    /*
    Input: Void
    Output: Void
    Purpose: Sets up y bounds at the point where the dragon stopped to attack
    */
    private void setUpMinAndMax(){
        y = head.y;
        //Sets up new offset
        yMax = y + Y_OFFSET;
        //If offset is larger than the normal max sets offset to be the normal max
        if(yMax > OSCILLATING_Y_MAX){yMax = OSCILLATING_Y_MAX;}
        //Sets up new offset
        yMin = y - Y_OFFSET;
        //If offset is smaller than normal min set offset to be the normal min
        if(yMin < OSCILLATING_Y_MIN){yMin = OSCILLATING_Y_MIN;}
    }

    /*
    Input: Void
    Output: Void
    Purpose: Updates Y position as the dragon moves in for an attack
    */
    private void updateHorizontalMomentOscillatingY(){
        //oscillating_horizontal : true = up, false = down
        if(oscillating_horizontal){
            horn.y += Y_SPEED;
            head.y += Y_SPEED;
        }
        else {
            horn.y -= Y_SPEED;
            head.y -= Y_SPEED;
        }
    }

    /*
    Input: Void
    Output: Void
    Purpose: Updates all the central functions of the scales
    */
    private void updateScale(float delta){
        createScale();                  //Creates new scales
        updateScaleAnimation(delta);    //Updates individual scale animation states
        updateScalePosition();          //Updates the scale positions
        removeScale();                  //Removes scales once off screen
    }

    /*
    Input: Void
    Output: Void
    Purpose: Creates new scale to be displayed
    */
    private void createScale(){
        if(scales.size < 10) {
            Collectible scale = new Collectible(scaleTexture);
            scale.setRadius();  //Random size
            scale.setPosition(head.x - scale.getRadius(), head.getY() + head.height, head.height); //Random point behind the head
            scales.add(scale);  //Adds it to the array
        }
    }

    /*
    Input: Void
    Output: Void
    Purpose: Updates the animation of each scale
    */
    private void updateScaleAnimation(float delta) {
        if (scales.size > 0) {
            for (Collectible scale : scales) {
                scale.updateAnimation(delta);
            }
        }
    }

    /*
    Input: Void
    Output: Void
    Purpose: Updates the position of the scales moving them left
    */
    private void updateScalePosition(){
        if(scales.size > 0){
            for(Collectible scale : scales){
                scale.updatePosition();
            }
        }
    }

    /*
    Input: Void
    Output: Void
    Purpose: Removes any scale that goes off screen
    */
    private void removeScale(){
        if(scales.size > 0) {
            for (Collectible scale : scales) {
                if (scale.getX() + scale.getRadius() < 0) { scales.removeValue(scale, true); }}
        }
    }

    /*
    Input: Void
    Output: Void
    Purpose: Central function that updates position animation and remove of bullets
    */
    private void updateBullets(float delta){
        updatePositionBullets();            //Updates position of bullets
        updateAnimationBullet(delta);       //Updates animation state
        removeBullets();                    //Removes bullets once off screen
    }

    /*
    Input: Void
    Output: Void
    Purpose: Updates the animation states for individual bullets
    */
    private void updateAnimationBullet(float delta){
        if(bullets.size > 0){
            for(Collectible bullet : bullets){bullet.updateAnimation(delta);}
        }
    }
    /*
    Input: Void
    Output: Void
    Purpose: Updates the animation states for individual bullets
    */
    private void createBullets(){
            for(int i = 0; i < 3; i++) {
                Collectible bullet = new Collectible(bulletTexture);
                bullet.setRadius(8f);
                bullet.setPortionNoOffset(head.x + head.width - 15, y + head.height / 2 - 5);
                bullets.add(bullet);
            }
    }
    private void removeBullets(){
        int counter = 0;
        if(bullets.size > 0) {
            for (Collectible bullet : bullets){
                if(bullet.getX()-bullet.getRadius() > 320){counter++;}
            }
        }
        if(counter == bullets.size){bullets.clear();}
    }

    private void updatePositionBullets(){
        if(bullets.size > 0) {
            for (int i = 0; i < bullets.size; i++) {
                System.out.println(bullets.get(i).getY() + "," + yMax + head.height) ;
                if(i % 3 == 1 && bullets.get(i).getY() < yMax + head.height){
                    bullets.get(i).setPortionNoOffset(bullets.get(i).getX() + X_SPEED, bullets.get(i).getY() + Y_SPEED/10);
                }
                else if(i % 3 == 0 && bullets.get(i).getY() > yMin){
                    bullets.get(i).setPortionNoOffset(bullets.get(i).getX() + X_SPEED, bullets.get(i).getY() - Y_SPEED/10);
                }
                else{
                    bullets.get(i).setPortionNoOffset(bullets.get(i).getX() + X_SPEED, bullets.get(i).getY());
                }
            }
        }
    }

    private void updateEyeLaserPosition(float delta){
        if(eyeLaserFlag){ eyeLaser.width += 5;}
        else {
            if (eyeLaser.height > 0) {
                eyeLaser.height -= 2;
                eyeLaser.y += 1;
                eyeLaser.width -= 5;
            }
            else {
                eyeLaser.height = 0;
                eyeLaser.width = 0;
            }
        }
        if(eyeLaser.width + eyeLaser.x >= 320){
            laserTimer -= delta;
            if (laserTimer <= 0) {
                laserTimer = LASER_TIME;
                eyeLaserFlag = !eyeLaserFlag;
            }
        }
    }


    void restart(){
        scales.clear();
        bullets.clear();
        modeFlag = -2;
        eyeLaser.width = 0;
        head.x = -HEAD_WIDTH;
        head.y = 240;
        horn.x = -HEAD_WIDTH;
        horn.y = 240;
        animationTime = 0;
        laserAnimationTime = 0;
    }

    /*
    Input: Void
    Output: Void
    Purpose: Checks if spacecraft is colliding with either of the boarders
    */
    boolean isColliding(SpaceCraft spaceCraft) {
        Circle spaceCraftCollisionCircle = spaceCraft.getCollisionCircle();
        for(Collectible bullet : bullets){
            if(Intersector.overlaps(spaceCraftCollisionCircle, bullet.getCollisionCircle()))
            {
                return true;
            }
        }
        return Intersector.overlaps(spaceCraftCollisionCircle, head) ||
                Intersector.overlaps(spaceCraftCollisionCircle, horn) ||
                Intersector.overlaps(spaceCraftCollisionCircle, eyeLaser);
    }

    void draw(SpriteBatch batch) {
        TextureRegion headTexture;
        if(animationFlag && futureModeFlag == 3) {
            headTexture = (TextureRegion) laserAnimation.getKeyFrame(laserAnimationTime);
        }
        else if(animationFlag || modeFlag == -1){
            headTexture = (TextureRegion) mouthAnimation.getKeyFrame(animationTime);
        }
        else{
            headTexture = (TextureRegion) eyeAnimation.getKeyFrame(animationTime);
        }
        batch.draw(headTexture, head.x - 30, head.y - 8);
        batch.draw(laserTexture, eyeLaser.x, eyeLaser.y, eyeLaser.width, eyeLaser.height);
        for (Collectible scale : scales){scale.draw(batch);}
        for (Collectible bullet : bullets){bullet.draw(batch);}
    }


    /*
    Input: ShapeRenderer
    Output:Void
    Purpose: Draws the wireframe
    */
    void drawDebug(ShapeRenderer shapeRenderer) {
        shapeRenderer.rect(horn.x, horn.y, horn.width, horn.height);
        shapeRenderer.rect(head.x, head.y,  head.width, head.height);
        shapeRenderer.rect(eyeLaser.x, eyeLaser.y, eyeLaser.width, eyeLaser.height);
        for (Collectible bullet : bullets){bullet.drawDebug(shapeRenderer);}
        for (Collectible scale : scales){scale.drawDebug(shapeRenderer);}
    }
}
