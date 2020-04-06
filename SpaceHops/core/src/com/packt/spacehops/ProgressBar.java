package com.packt.spacehops;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;

public class ProgressBar {

    private float outerRectangleWidth;
    private float outerRectangleHeight;
    private float innerRectangleWidth;
    private float innerRectangleHeight;

    private float score = 0;
    private float goal = 15;

    private float outerX;
    private float outerY;
    private float innerX;
    private float innerY;

    private final Rectangle outerRectangle;
    private final Rectangle innerRectangle;

    private final Texture outerTexture;
    private final Texture innerTexture;

    ProgressBar(float screenWidth, float screenHeight, Texture outerTexture, Texture innerTexture){

        outerRectangleWidth = 3*screenWidth/4;
        outerRectangleHeight = screenHeight/10;
        innerRectangleWidth = outerRectangleWidth - 20;
        innerRectangleHeight = outerRectangleHeight - 20;

        outerX = (screenWidth - outerRectangleWidth)/2;
        outerY = 20;
        innerX = outerX + 10;
        innerY = outerY + 10;

        this.outerTexture = outerTexture;
        this.innerTexture = innerTexture;

        this.outerRectangle = new Rectangle(outerX, outerY, outerRectangleWidth, outerRectangleHeight);
        this.innerRectangle = new Rectangle(innerX, innerY, 0, innerRectangleHeight);
    }

    void update(){
        if(!checkGoalReached()) {score++;}
        changeInnerWidth();
    }

    private void changeInnerWidth(){
        innerRectangle.width = (score/goal) * innerRectangleWidth;
    }

    private boolean checkGoalReached(){return score == goal;}

    void restart(){
        score = 0;
        changeInnerWidth();
    }

    void draw(SpriteBatch batch, GlyphLayout glyphLayout, BitmapFont bitmapFont){
        drawFrame(batch);
        drawScore(batch, glyphLayout, bitmapFont);
    }

    private void drawFrame(SpriteBatch batch){
        batch.draw(outerTexture, outerX, outerY, outerRectangle.width, outerRectangle.height);
        batch.draw(innerTexture, innerX, innerY, innerRectangle.width, innerRectangle.height);
    }

    private void drawScore(SpriteBatch batch, GlyphLayout glyphLayout, BitmapFont bitmapFont){
        String scoreAsString = (int) score + "/" + (int) goal;
        glyphLayout.setText(bitmapFont, scoreAsString);
        bitmapFont.draw(batch, scoreAsString,outerRectangleWidth+glyphLayout.width,outerRectangleHeight+glyphLayout.height);
    }


    int getScore(){ return (int) score;}

    /*
    Input: Void
    Output:Void
    Purpose: Draws the wireframe
    */
    void drawDebug(ShapeRenderer shapeRenderer) {
        shapeRenderer.rect(outerRectangle.x, outerRectangle.y, outerRectangle.width, outerRectangle.height);
        shapeRenderer.rect(innerRectangle.x, innerRectangle.y, innerRectangle.width, innerRectangle.height);
    }

}
