/*
The ConversationBox class creates a box with another box it in that show a profile, it displays
text and is used to communicate to the player.

The box opens from the center of the screen and expands in the horizontal direction then in vertical.
Reverse for closing
 */

package com.packt.spacehops;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;

class ConversationBox {

    //Dimensions
    private float outerRectangleWidth;      //Finals width of the box
    private float outerRectangleHeight;     //Final height of the box
    private float outerX;                   //Final x of the box
    private float outerY;                   //Final y of the box
    private float outerMidpointX;           //Center of the screen
    private float outerMidpointY;           //Center of box
    private final static float CHANGE_OF_RATE = 20; //Speed at which the box changes height and width
    private float changeRateX;              //Calculated value of x speed in reference to the two x coordinates and CHANGE_OF_RATE
    private float changeRateY;              //Calculated value of y speed in reference to the two y coordinates and CHANGE_OF_RATE
    private float changeRateWidth;
    private float changeRateHeight;

    //Rectangle objects
    private final Rectangle outerRectangle;
    private final Rectangle profileRectangle;

    //Texture and Animation
    private final TextureRegion outerTexture;
    private static final int TILE_WIDTH = 32;			//The width of each tile in the texture
    private static final int TILE_HEIGHT = 32;			//The height of each tile in the texture
    private static final float FRAME_DURATION = 0.25f;	//How long each tile lasts on screen
    private float animationTime = 0;
    private final Animation animation;

    //Flags
    private boolean profileFlag = false;

    /*
    Input: Takes in screen dimensions and textures
    Output: Void
    Purpose: Calculate the dimensions of the frame, profile image and the change at which it opens
    the frame
    */
    ConversationBox(float screenWidth, float screenHeight, TextureRegion outerTexture, TextureRegion profileTexture){
        //Calculate the dimensions
        outerRectangleWidth = 3*screenWidth/4;
        outerRectangleHeight = screenHeight/8;
        float profileRectangleWidth = screenWidth / 8;
        float profileRectangleHeight = outerRectangleHeight - 20;

        //Calculation of positions
        outerX = (screenWidth - outerRectangleWidth)/2;
        outerY = screenHeight - screenHeight/6;
        outerMidpointX = screenWidth/2;
        outerMidpointY = screenHeight-screenHeight/8;
        float profileX = outerX + 10;
        float profileY = outerY + 10;

        //Calculate change of rate at which the box opens and closes
        changeRateX = (outerMidpointX - outerX)/CHANGE_OF_RATE;
        changeRateY = (outerMidpointY - outerY)/CHANGE_OF_RATE;
        changeRateWidth = outerRectangleWidth/CHANGE_OF_RATE;
        changeRateHeight = outerRectangleHeight/CHANGE_OF_RATE;

        //Divides the profile texture into frames
        TextureRegion[][] profileTextures = new TextureRegion(profileTexture).split(TILE_WIDTH, TILE_HEIGHT); //Breaks down the texture into tiles

        //Sets the animation to be the texture 0-3 and sets it to loop
        animation = new Animation<>(FRAME_DURATION, profileTextures[0][0], profileTextures[0][1]);
        animation.setPlayMode(Animation.PlayMode.LOOP);

        //Connect the frame texture
        this.outerTexture = outerTexture;

        //Create the rectangle objects
        this.outerRectangle = new Rectangle(outerMidpointX, outerMidpointY, 0, 0);
        this.profileRectangle = new Rectangle(profileX, profileY, profileRectangleWidth, profileRectangleHeight);
    }

    /*
    Input: Delta to keep up with time of animation, and screenUp to tell if the frame should be open
    Output: Void
    Purpose: Updates the variables that keep the frame open or closed
    */
    void update(float delta, int screenUp){
        animationTime += delta;
        if(screenUp == 0) {updateStartScreen();}
        if(screenUp == 1) {updateEndScreen();}
    }

    void restartTimer(){ animationTime = 0; }

    /*
    Input: Void
    Output: Void
    Purpose: Checks three states, opening the width, opening height, open setting profile on
    */
    private void updateStartScreen(){
        //Opening width
        if(!widthFull()){
            increaseWidth();                                //Expands the width
            if(heightEmpty()){increaseHeight();}            //Make height open a bit so we can see the image
            if(outerRectangle.x != outerX) {decreaseX();}   //Moves the x so it looks like its opening in both directions
        }
        //Opening height
        if(widthFull() && !heightFull()){
            increaseHeight();                               //Expands y
            if(outerRectangle.y != outerY) { decreaseY();}  //Moves y so it looks like its opening in both directions
        }
        //Open
        if(widthFull() && heightFull()) { profileFlag = true; } //Turns profile on
    }

    /*
    Input: Void
    Output: Void
    Purpose: Lowering height to almost closing, closing width, closing height fully
    */
    private void updateEndScreen(){
        profileFlag = false;        //Turns off profile
        //Decrease height to almost closing
        if(!heightAlmostEmpty()){
            decreaseHeight();                                       //Decreases the height
            if(outerRectangle.y != outerMidpointY) {increaseY();}   //Moves y so it looks like its shrinking in both directions
        }
        //Decrease the width
        if(heightAlmostEmpty() && !widthEmpty()){
            decreaseWidth();                                        //Decrease the width
            if(outerRectangle.x != outerMidpointX) {increaseX();}   //Move x so it looks like its shrinking in both directions
        }
        //Full close the height
        if(heightAlmostEmpty() && widthEmpty()){ outerRectangle.height = 0; }   //Make height 0
    }

    /*
    Input: Void
    Output: Void
    Purpose: Increases x by change of rate
    */
    private void increaseX(){outerRectangle.x += changeRateX;}

    /*
    Input: Void
    Output: Void
    Purpose: Decreases x by change of rate
    */
    private void decreaseX(){outerRectangle.x -= changeRateX;}

    /*
    Input: Void
    Output: Void
    Purpose: Increases y by change of rate
    */
    private void increaseY(){outerRectangle.y += changeRateY;}

    /*
    Input: Void
    Output: Void
    Purpose: Decreases y by change of rate
    */
    private void decreaseY(){outerRectangle.y -= changeRateY;}

    /*
    Input: Void
    Output: Void
    Purpose: Increases width by change of rate
    */
    private void increaseWidth(){ outerRectangle.width += changeRateWidth; }

    /*
    Input: Void
    Output: Void
    Purpose: Decrease width by change of rate
    */
    private void decreaseWidth(){ outerRectangle.width -= changeRateWidth; }

    /*
    Input: Void
    Output: Void
    Purpose: Increase height by change of rate
    */
    private void increaseHeight(){ outerRectangle.height += changeRateHeight; }

    /*
    Input: Void
    Output: Void
    Purpose: Decreases height by change of rate
    */
    private void decreaseHeight(){ outerRectangle.height -= changeRateHeight; }

    /*
    Input: Void
    Output: Boolean, is current width equal to max width
    Purpose: Checks if the current width is equal to the end goal width
    */
    private boolean widthFull(){return outerRectangle.width == outerRectangleWidth;}

    /*
    Input: Void
    Output: Boolean, is current width 0
    Purpose: Checks if the current width is 0
    */
    private boolean widthEmpty(){return  outerRectangle.width <= 0;}

    /*
    Input: Void
    Output: Boolean, is current height equal to max height
    Purpose: Checks if the current height equal to max height
    */
    private boolean heightFull(){return outerRectangle.height == outerRectangleHeight;}

    /*
    Input: Void
    Output: Boolean, is current height is 0
    Purpose: Checks if the current height is 0
    */
    private boolean heightEmpty(){return  outerRectangle.height <= 0;}

    /*
    Input: Void
    Output: Boolean, is current height is almost 0, equal or less than change of rate
    Purpose: To keep the height open enough so we can see the width decreasing before fully closing frame
    */
    private boolean heightAlmostEmpty(){return outerRectangle.height <= changeRateHeight;}

    boolean getProfileFlag(){return  profileFlag;}

    /*
    Input: Batch, object that draws the textures
    Output: Void
    Purpose: Draws frame and if its fully open it draw the profile
    */
    void draw(SpriteBatch batch){
        batch.draw(outerTexture, outerRectangle.x, outerRectangle.y, outerRectangle.width, outerRectangle.height);
        if(profileFlag) {
            TextureRegion profileTexture = (TextureRegion) animation.getKeyFrame(animationTime);
            batch.draw(profileTexture, profileRectangle.x, profileRectangle.y, profileRectangle.width, profileRectangle.height);
        }
    }

    /*
    Input: ShapeRenderer, object that draws wireframes
    Output:Void
    Purpose: Draws the wireframe
    */
    void drawDebug(ShapeRenderer shapeRenderer) {
        shapeRenderer.rect(outerRectangle.x, outerRectangle.y, outerRectangle.width, outerRectangle.height);
        if(profileFlag){ shapeRenderer.rect(profileRectangle.x, profileRectangle.y, profileRectangle.width, profileRectangle.height); }
    }
}
