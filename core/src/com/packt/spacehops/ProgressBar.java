/*
The Progress Bar displays the progress of the user.
 */

package com.packt.spacehops;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;

class ProgressBar {
    //Dimensions and position
    private float outerRectangleWidth;
    private float outerRectangleHeight;
    private float innerRectangleWidth;
    private float outerX;
    private float outerY;
    private float innerX;
    private float innerY;

    //User display variables
    private float score = 0;
    private float goal;

    //Rectangle object
    private final Rectangle outerRectangle;
    private final Rectangle innerRectangle;

    //Textures
    private final TextureRegion outerTexture;
    private final TextureRegion innerTexture;

    /*
    Input: Screen dimensions, textures for the frame and progress bar
    Output: Void
    Purpose: Initializes the size and location of the progress bar based on the dimensions of the screen
    */
    ProgressBar(float screenWidth, float screenHeight, TextureRegion outerTexture, TextureRegion innerTexture){
        //Calculate width adn height of frame and progress bar
        outerRectangleWidth = 3*screenWidth/4;
        outerRectangleHeight = screenHeight/10;
        innerRectangleWidth = outerRectangleWidth - 20;
        float innerRectangleHeight = outerRectangleHeight - 20;
        //Calculate the position
        outerX = (screenWidth - outerRectangleWidth)/2;
        outerY = 20;
        innerX = outerX + 10;
        innerY = outerY + 10;
        //Attach the textures
        this.outerTexture = outerTexture;
        this.innerTexture = innerTexture;
        //Create the rectangle objects
        this.outerRectangle = new Rectangle(outerX, outerY, outerRectangleWidth, outerRectangleHeight);
        this.innerRectangle = new Rectangle(innerX, innerY, 0, innerRectangleHeight);
    }

    /*
    Input: The dev set goal
    Output: Void
    Purpose: Let's developer set the goal
    */
    void setGoal(int goal){this.goal = goal;}

    /*
    Input: Void
    Output: Void
    Purpose: Increase the score and updates progress bar size
    */
    void update(){
        if(!goalReachedFlag()) {score++;}
        updateInnerWidth();
    }

    /*
    Input: Void
    Output: Void
    Purpose: Calculates the dimensions of the image -- MAY CHANGE --
    */
    private void updateInnerWidth(){
        innerRectangle.width = (score/goal) * innerRectangleWidth;
    }

    /*
    Input: Void
    Output: Void
    Purpose: Increase the score and updates progress bar size
    */
    private boolean goalReachedFlag(){return score == goal;}

    /*
    Input: Void
    Output: Int score value
    Purpose: Returns the current score
    */
    int getScore(){ return (int) score;}

    /*
    Input: Void
    Output: Void
    Purpose: Sets score to 0 and update progress bar width
    */
    void restart(){
        score = 0;
        updateInnerWidth();
    }

    /*
    Input: Batch for textures, Glyph and Bitmap for text
    Output: Void
    Purpose: Draws the progress bar, frame and numeric representation of it
    */
    void draw(SpriteBatch batch, GlyphLayout glyphLayout, BitmapFont bitmapFont){
        drawFrame(batch);
        drawScore(batch, glyphLayout, bitmapFont);
    }

    /*
    Input: Batch to add the textures to
    Output: Void
    Purpose: Draws the progress bar, frame
    */
    private void drawFrame(SpriteBatch batch){
        batch.draw(outerTexture, outerX, outerY, outerRectangle.width, outerRectangle.height);
        batch.draw(innerTexture, innerX, innerY, innerRectangle.width, innerRectangle.height);
    }

    /*
    Input: Batch for textures, Glyph and Bitmap for text
    Output: Void
    Purpose: Draws the numeric representation
    */
    private void drawScore(SpriteBatch batch, GlyphLayout glyphLayout, BitmapFont bitmapFont){
        String scoreAsString = (int) score + "/" + (int) goal;
        glyphLayout.setText(bitmapFont, scoreAsString);
        bitmapFont.draw(batch, scoreAsString,outerRectangleWidth+glyphLayout.width,outerRectangleHeight+glyphLayout.height);
    }

    /*
    Input:  ShapeRenderer, object that draws wireframes
    Output:Void
    Purpose: Draws the wireframe
    */
    void drawDebug(ShapeRenderer shapeRenderer) {
        shapeRenderer.rect(outerRectangle.x, outerRectangle.y, outerRectangle.width, outerRectangle.height);
        shapeRenderer.rect(innerRectangle.x, innerRectangle.y, innerRectangle.width, innerRectangle.height);
    }
}
