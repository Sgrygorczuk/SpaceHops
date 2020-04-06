package com.packt.spacehops;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;

public class ConversationBox {

    private float outerRectangleWidth;
    private float outerRectangleHeight;
    private float profileRectangleWidth;
    private float profileRectangleHeight;

    private float outerX;
    private float outerY;
    private float outerMidpointX;
    private float outerMidpointY;
    private float changeRateX;
    private float changeRateY;
    private float profileX;
    private float profileY;

    private final static float CHANGE_OF_RATE = 20;

    private final Rectangle outerRectangle;
    private final Rectangle profileRectangle;

    private boolean profileOn = false;

    private final Texture outerTexture;

    //Texture and Animation
    private static final int TILE_WIDTH = 32;			//The width of each tile in the texture
    private static final int TILE_HEIGHT = 32;			//The height of each tile in the texture
    private static final float FRAME_DURATION = 0.25f;	//How long each tile lasts on screen
    private float animationTime = 0;
    private final Animation animation;

    ConversationBox(float screenWidth, float screenHeight, Texture outerTexture, Texture profileTexture){
        outerRectangleWidth = 3*screenWidth/4;
        outerRectangleHeight = screenHeight/8;
        profileRectangleWidth = screenWidth/8;
        profileRectangleHeight = outerRectangleHeight - 20;

        outerX = (screenWidth - outerRectangleWidth)/2;
        outerY = screenHeight - screenHeight/6;
        outerMidpointX = screenWidth/2;
        outerMidpointY = screenHeight-screenHeight/8;
        changeRateX = (outerMidpointX - outerX)/CHANGE_OF_RATE;
        changeRateY = (outerMidpointY - outerY)/CHANGE_OF_RATE;
        profileX = outerX + 10;
        profileY = outerY + 10;

        TextureRegion[][] profileTextures = new TextureRegion(profileTexture).split(TILE_WIDTH, TILE_HEIGHT); //Breaks down the texture into tiles

        //Sets the animation to be the texture 0-3 and sets it to loop
        animation = new Animation<>(FRAME_DURATION, profileTextures[0][0], profileTextures[0][1]);
        animation.setPlayMode(Animation.PlayMode.LOOP);

        this.outerTexture = outerTexture;

        this.outerRectangle = new Rectangle(outerMidpointX, outerMidpointY, 0, 0);
        this.profileRectangle = new Rectangle(profileX, profileY, profileRectangleWidth, profileRectangleHeight);
    }

    boolean startScreen(){
        if(!widthFull()){
            increaseWidth();
            if(heightEmpty()){increaseHeight();}
            if(outerRectangle.x != outerX) {decreaseX();}
        }
        if(widthFull() && !heightFull()){
            increaseHeight();
            if(outerRectangle.y != outerY) { decreaseY();}
        }
        if(widthFull() && heightFull()) {
            profileOn = true;
            return true;
        }
        return false;
    }

    boolean endScreen(){
        profileOn = false;
        if(!heightAlmostEmpty()){
            decreaseHeight();
            if(outerRectangle.y != outerMidpointY) {increaseY();}
        }
        if(heightAlmostEmpty() && !widthEmpty()){
            decreaseWidth();
            if(outerRectangle.x != outerMidpointX) {increaseX();}
        }
        if(heightAlmostEmpty() && widthEmpty()){
            outerRectangle.height = 0;
            return false;
        }
        return true;
    }

    void update(float delta, int screenUp){
        if(screenUp == 0) {startScreen();}
        if(screenUp == 1) {endScreen();}
        animationTime += delta;
    }


    void increaseX(){outerRectangle.x += changeRateX;}
    void decreaseX(){outerRectangle.x -= changeRateX;}
    void increaseY(){outerRectangle.y += changeRateY;}
    void decreaseY(){outerRectangle.y -= changeRateY;}

    void increaseWidth(){ outerRectangle.width += outerRectangleWidth/CHANGE_OF_RATE; }
    void decreaseWidth(){ outerRectangle.width -= outerRectangleWidth/CHANGE_OF_RATE; }
    void increaseHeight(){ outerRectangle.height += outerRectangleHeight/CHANGE_OF_RATE; }
    void decreaseHeight(){ outerRectangle.height -= outerRectangleHeight/CHANGE_OF_RATE; }

    boolean widthFull(){return outerRectangle.width == outerRectangleWidth;}
    boolean widthEmpty(){return  outerRectangle.width <= 0;}
    boolean heightFull(){return outerRectangle.height == outerRectangleHeight;}
    boolean heightEmpty(){return  outerRectangle.height <= 0;}
    boolean heightAlmostEmpty(){return outerRectangle.height <= outerRectangleHeight/CHANGE_OF_RATE;}

    void draw(SpriteBatch batch){
        batch.draw(outerTexture, outerRectangle.x, outerRectangle.y, outerRectangle.width, outerRectangle.height);
        if(profileOn) {
            TextureRegion profileTexture = (TextureRegion) animation.getKeyFrame(animationTime);
            batch.draw(profileTexture, profileRectangle.x, profileRectangle.y, profileRectangle.width, profileRectangle.height);
        }
    }

    /*
    Input: Void
    Output:Void
    Purpose: Draws the wireframe
    */
    void drawDebug(ShapeRenderer shapeRenderer) {
        shapeRenderer.rect(outerRectangle.x, outerRectangle.y, outerRectangle.width, outerRectangle.height);
        if(profileOn){ shapeRenderer.rect(profileRectangle.x, profileRectangle.y, profileRectangle.width, profileRectangle.height); }
    }
}
