/*
The Dragon class creates an enemy that hovers on the left side of the screen it moves up and down
attacking with fire attacks and biting the user
 */

package com.packt.spacehops;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
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

    //Rectangles that hold the head of snake positions
    private final Rectangle horn;
    private final Rectangle head;

    //Array of scales that flow off the dragon
    private Array<Collectible> scales = new Array<>();
    //Bullets that the dragon shoots
    private Array<Collectible> bullets = new Array<>();

    //-1 Enter
    //0 Moving up and down, 1 prepare for attacking, 12 attacking, 3 coming back
    private int modeFlag = -1;

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
    private static final float MOVE_TIME = 5F;
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

    //Textures
    private Texture scaleTexture;
    private Texture headTexture;
    private Texture bulletTexture;

    /*
    Input: Textures for head, bullet and scales
    Output: Void
    Purpose: Constructor makes necessary shapes and sets up textures
    */
    Dragon(Texture head, Texture scale, Texture bullet){
        //Sets up the
        this.head = new Rectangle(-HEAD_WIDTH, 240, HEAD_WIDTH, HEAD_HEIGHT);
        this.horn = new Rectangle(-HEAD_WIDTH, 240, HORN_WIDTH, HEAD_HEIGHT + HORN_HEIGHT);

        //Sets up textures
        bulletTexture = bullet;
        headTexture = head;
        scaleTexture = scale;
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

    /*
    Input: Delta, timing
    Output: Void
    Purpose: Counts down when to switch between modes
    */
    private void updateMode(float delta) {
        //MODE -1 is dragon entering the screen
        if(modeFlag == -1 && head.x > HEAD_WIDTH - 10){modeFlag = 0;}
        //MODE 0 is the dragon idling on the left side of the screen
        else if (modeFlag == 0){
                moveTimer -= delta;
                if (moveTimer <= 0) {
                    moveTimer = MOVE_TIME;
                    modeFlag = 1;
                }
            }
        //MODE 1 is the mode where dragon stops indicating to player that the
        //Dragons is about to attack, has random chance of shooting fire or biting
        else if(modeFlag == 1){
                prepTimer -= delta;
                if (prepTimer <= 0) {
                    prepTimer = PREP_TIME;
                    int mode = MathUtils.random(2,3); //Choose the attack type
                    setUpMinAndMax();                  //Sets up bounds
                    modeFlag = mode;
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
        //MODE 3 moves the dragon in for a bite
        else if(modeFlag == 3 && head.x >= ATTACK_X-head.x){
            modeFlag = 4;
        }
        //Returns the dragon back to idle position once back on left side of the screen
        else if(modeFlag == 4 && head.x <= 30){modeFlag = 0;}
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
        //Biting
        else if(modeFlag == 3){
            updateMovement();                           //Updates the which direction its going in
            updateHorizontalMomentOscillatingY();       //Updates y position
            moveXForward();                           //Updates x position
        }
        //Returning
        else if(modeFlag == 4) {
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
        Collectible scale = new Collectible(scaleTexture);
        scale.setPosition(head.x, horn.y + horn.height); //Random point behind the head
        scale.setRadius();  //Random size
        scales.add(scale);  //Adds it to the array
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
                if (scale.getX() < 0) { scales.removeValue(scale, true); }}
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
                Collectible bullet = new Collectible(scaleTexture);
                bullet.setRadius(8f);
                bullet.setPortionNoOffset(head.x + head.width, y + head.height / 2);
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

    void restart(){
        scales.clear();
        bullets.clear();
        modeFlag = -1;
        head.x = -HEAD_WIDTH;
        head.y = HEAD_HEIGHT;
        horn.x = -HEAD_WIDTH;
        horn.y = HEAD_HEIGHT/2 + HORN_HEIGHT;
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
        return Intersector.overlaps(spaceCraftCollisionCircle, head);
    }

    void draw(SpriteBatch batch) {
        batch.draw(headTexture, head.x, head.y);
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
        for (Collectible bullet : bullets){bullet.drawDebug(shapeRenderer);}
        for (Collectible scale : scales){scale.drawDebug(shapeRenderer);}
    }
}
