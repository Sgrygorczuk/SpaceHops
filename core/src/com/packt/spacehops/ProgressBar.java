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
    //User display variables
    private float score = 0;
    private float goal;

    //Textures
    private final TextureRegion textureRegion;

    ProgressBar(TextureRegion textureRegion){
        this.textureRegion = textureRegion;
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
    void update(){ if(!goalReachedFlag()) {score++;} }

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
        batch.draw(textureRegion, 0, 0, textureRegion.getRegionWidth(), textureRegion.getRegionHeight());
    }

    /*
    Input: Batch for textures, Glyph and Bitmap for text
    Output: Void
    Purpose: Draws the numeric representation
    */
    private void drawScore(SpriteBatch batch, GlyphLayout glyphLayout, BitmapFont bitmapFont){
        String scoreAsString = (int) score + "/" + (int) goal;
        glyphLayout.setText(bitmapFont, scoreAsString);
        bitmapFont.draw(batch, scoreAsString, (float) textureRegion.getRegionWidth()/2 - glyphLayout.width/2,
                 (float) textureRegion.getRegionHeight()/2 + 4);
    }

    /*
    Input:  ShapeRenderer, object that draws wireframes
    Output:Void
    Purpose: Draws the wireframe
    */
    void drawDebug(ShapeRenderer shapeRenderer) {
        shapeRenderer.rect(textureRegion.getRegionX(), textureRegion.getRegionY(), textureRegion.getRegionWidth(), textureRegion.getRegionHeight());
    }
}
