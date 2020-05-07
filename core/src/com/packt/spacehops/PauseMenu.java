package com.packt.spacehops;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.utils.ActorGestureListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.FitViewport;

class PauseMenu {

    /*
    Dimensions -- Units the screen has
    */
    private static final float WORLD_WIDTH = 320;
    private static final float WORLD_HEIGHT = 480;

    //Buttons and stages
    private Stage menuButtonScreen;     //Button that leads to the pause menu
    private Stage pauseMenuScreen;      //Buttons in the pause menu
    private Stage nextLevelStage;
    private float buttonHeight;         //Height of the buttons so that we can
    private float buttonWidth;

    private TextureAtlas uiAtlas;

    private Rectangle menuBackground;              //Rectangle that keeps the info of the menu background
    private TextureRegion pauseMenuTexture;       //Texture of the menu's background

    //TO BE CHANGED TO NEXT LEVEL
    private TextureRegion nextLevelUpTexture;
    private TextureRegion nextLevelDownTexture;

    private boolean pauseFlag = false;      //Tells us if the game is paused
    private boolean disposeFlag = false;    //Tells us if we need to get rid off the assets

    private SpaceHops spaceHops;                      //Sends up to different screen

    /*
    Input: Game, object used to set up screens
    Output: Void
    Purpose: Constructors, creates all the necessary objects
    */
    PauseMenu(SpaceHops spaceHops){
        this.spaceHops = spaceHops;
        setUp();
        showMenuButton();                   //Creates the menu button
        showPauseMenu();                    //Creates the buttons inside the pause menu
        showNextLevelMenu();
        showMenuBackground();               //Sets up vars to display the background of the pause menu
    }

    private void setUp(){
        uiAtlas = spaceHops.getAssetManager().get("ui_assets.atlas");
        nextLevelUpTexture = uiAtlas.findRegion("SmallButtonUnpressed");
        nextLevelDownTexture = uiAtlas.findRegion("SmallButtonPressed");
    }

    /*
    Input: Void
    Output: Void
    Purpose: Tells game if it's paused or not
    */
    boolean getPauseFlag(){return pauseFlag;}

    /*
    Input: Void
    Output: Void
    Purpose: Tells game to release the assets
    */
    boolean getDisposeFlag(){return disposeFlag;}

    /*
    Input: Void
    Output: Void
    Purpose: Provide the game with the menu button
    */
    Stage getMenuButtonStage(){return menuButtonScreen;}

    /*
    Input: Void
    Output: Void
    Purpose: Provide the game with the pause menu buttons
    */
    Stage getPauseMenuScreen(){return pauseMenuScreen;}

    /*
    Input: Void
    Output: Void
    Purpose: Provide the game with the pause menu buttons
    */
    Stage getNextLevelStage(){return nextLevelStage;}

    /*
    Input: Delta
    Output: Void
    Purpose: Pauses the game
    */
    private void updatePause(){ pauseFlag = !pauseFlag; }

    /*
    Input: Delta
    Output: Void
    Purpose: Sets up the background of the pause and next level menu
    */
    private void showMenuBackground(){
        menuBackground = new Rectangle(WORLD_WIDTH/2 - buttonWidth/2 - 10,
                (float) (WORLD_HEIGHT/2 - 1.5 * buttonHeight - 10),
                buttonWidth + 20, 2 * buttonHeight + 40);

        pauseMenuTexture = uiAtlas.findRegion("CommunicationFrame");
    }

    /*
    Input: Void
    Output: Void
    Purpose: Sets up the button that will pause the game and bring up the menu
    */
    private void showMenuButton(){
        //Sets up stage to be screen size
        menuButtonScreen = new Stage(new FitViewport(WORLD_WIDTH,WORLD_HEIGHT));
        Gdx.input.setInputProcessor(menuButtonScreen);    //Give it the control

        //Sets up textures used by the button
        TextureRegion menuUpTexture = uiAtlas.findRegion("BigButtonUnpressed");
        TextureRegion menuDownTexture = uiAtlas.findRegion("BigButtonPressed");

        //Creates button and position
        ImageButton menuButton = new ImageButton(new TextureRegionDrawable(new TextureRegion(menuUpTexture)), new TextureRegionDrawable(menuDownTexture));
        menuButton.setPosition(WORLD_WIDTH, WORLD_HEIGHT - (float) menuDownTexture.getRegionHeight()/2, Align.center);
        menuButton.setWidth((float) menuDownTexture.getRegionWidth()/2);
        menuButtonScreen.addActor(menuButton);

        //When clicked opens ip the menu
        if(!pauseFlag){
            menuButton.addListener(new ActorGestureListener() {@Override
            public void tap(InputEvent event, float x, float y, int count, int button) {
                super.tap(event, x, y, count, button);
                updatePause();
                Gdx.input.setInputProcessor(pauseMenuScreen);
            }
            });
        }
    }

    /*
    Input: Void
    Output: Void
    Purpose: Sets up the buttons that are in the paused menu
    */
    private void showPauseMenu(){
        /*
        Set up
         */
        //Sets up the stage object
        pauseMenuScreen = new Stage(new FitViewport(WORLD_WIDTH,WORLD_HEIGHT));

        //Sets up the textures
        TextureRegion quitUpTexture = uiAtlas.findRegion("SmallButtonUnpressed");
        TextureRegion quitDownTexture = uiAtlas.findRegion("SmallButtonPressed");

        TextureRegion resumeUpTexture = uiAtlas.findRegion("SmallButtonUnpressed");
        TextureRegion resumeDownTexture = uiAtlas.findRegion("SmallButtonPressed");

        //Sets up the buttons
        ImageButton quitButton = new ImageButton(new TextureRegionDrawable(new TextureRegion(quitUpTexture)),new TextureRegionDrawable(quitDownTexture));
        quitButton.setPosition(WORLD_WIDTH/2, WORLD_HEIGHT/2-quitButton.getHeight(), Align.center);
        pauseMenuScreen.addActor(quitButton);

        ImageButton resumeButton = new ImageButton(new TextureRegionDrawable(new TextureRegion(resumeUpTexture)),new TextureRegionDrawable(resumeDownTexture));
        resumeButton.setPosition(WORLD_WIDTH/2, WORLD_HEIGHT/2+10, Align.center);
        pauseMenuScreen.addActor(resumeButton);

        /*
        Listeners
         */

        //Goes back to the game
        if(!pauseFlag){
            resumeButton.addListener(new ActorGestureListener() {@Override
            public void tap(InputEvent event, float x, float y, int count, int button) {
                super.tap(event, x, y, count, button);
                updatePause();
                Gdx.input.setInputProcessor(menuButtonScreen);
            }
            });
        }

        //Quits to main menu
        if(!pauseFlag){
            quitButton.addListener(new ActorGestureListener() {@Override
            public void tap(InputEvent event, float x, float y, int count, int button) {
                super.tap(event, x, y, count, button);
                spaceHops.setScreen(new LoadingScreen(spaceHops, 0));
                disposeFlag = !disposeFlag;
                dispose();
            }
            });
        }

        //Store the heights of the buttons to calculate the background texture size
        buttonHeight = resumeButton.getHeight();
        buttonWidth = resumeButton.getWidth();
    }

    /*
    Input: Void
    Output: Void
    Purpose: Sets up the the Stage and quit button for next level menu
    */
    private void showNextLevelMenu(){
        /*
        Set up
         */
        //Sets up the stage object
        nextLevelStage = new Stage(new FitViewport(WORLD_WIDTH,WORLD_HEIGHT));

        //Sets up the textures
        TextureRegion quitUpTexture = uiAtlas.findRegion("SmallButtonUnpressed");
        TextureRegion quitDownTexture = uiAtlas.findRegion("SmallButtonPressed");

        //Sets up the buttons
        ImageButton quitButton = new ImageButton(new TextureRegionDrawable(new TextureRegion(quitUpTexture)),new TextureRegionDrawable(quitDownTexture));
        quitButton.setPosition(WORLD_WIDTH/2, WORLD_HEIGHT/2-quitButton.getHeight(), Align.center);
        nextLevelStage.addActor(quitButton);

        /*
        Listeners
         */

        //Quits to main menu
        if(!pauseFlag){
            quitButton.addListener(new ActorGestureListener() {@Override
            public void tap(InputEvent event, float x, float y, int count, int button) {
                super.tap(event, x, y, count, button);
                spaceHops.setScreen(new LoadingScreen(spaceHops, 0));
                disposeFlag = !disposeFlag;
                dispose();
            }
            });
        }
    }

    /*
    Input: Choice of which level this should send the player to
    Output: Void
    Purpose: Sets up the button that will send us to next level
    */
    void createNextLevelButton(final int levelChoice){
        ImageButton nextLevelButton = new ImageButton(new TextureRegionDrawable(new TextureRegion(nextLevelUpTexture)),
                new TextureRegionDrawable(nextLevelDownTexture));
        nextLevelButton.setPosition(WORLD_WIDTH/2, WORLD_HEIGHT/2+10, Align.center);
        nextLevelStage.addActor(nextLevelButton);

        //Goes back to the game
        if(!pauseFlag){
            nextLevelButton.addListener(new ActorGestureListener() {@Override
            public void tap(InputEvent event, float x, float y, int count, int button) {
                super.tap(event, x, y, count, button);
                chooseLevel(levelChoice);
                disposeFlag = !disposeFlag;
                dispose();
            }
            });
        }
    }

    /*
    Input: Choice of level
    Output: Void
    Purpose: Picks the level we're going to
    */
    private void chooseLevel(int levelChoice){
        if(levelChoice == 0) {spaceHops.setScreen(new LoadingScreen(spaceHops, 1));}
        if(levelChoice == 1) {spaceHops.setScreen(new LoadingScreen(spaceHops, 2));}
        if(levelChoice == 2) {spaceHops.setScreen(new LoadingScreen(spaceHops, 3));}
    }

    /*
    Input: Void
    Output: Void
    Purpose: Draws the background menu
    */
    void draw(SpriteBatch batch){ batch.draw(pauseMenuTexture, menuBackground.x, menuBackground.y, menuBackground.width, menuBackground.height);}

    void drawMenuText(GlyphLayout glyphLayout, BitmapFont bitmapFont, SpriteBatch batch){
        setUpText(glyphLayout, bitmapFont, batch, "Menu", WORLD_WIDTH - 3*glyphLayout.width/2, WORLD_HEIGHT - glyphLayout.height - 5);
    }

    void drawPauseText(GlyphLayout glyphLayout, BitmapFont bitmapFont, SpriteBatch batch){
        setUpText(glyphLayout, bitmapFont, batch, "Resume", WORLD_WIDTH/2 - glyphLayout.width, WORLD_HEIGHT/2 + glyphLayout.height + 5);
        setUpText(glyphLayout, bitmapFont, batch, "Quit", WORLD_WIDTH/2 - 15, WORLD_HEIGHT/2 - glyphLayout.height - 8);
    }

    void drawNextLevelText(GlyphLayout glyphLayout, BitmapFont bitmapFont, SpriteBatch batch){
        setUpText(glyphLayout, bitmapFont, batch, "Next Level", WORLD_WIDTH/2 - glyphLayout.width - 10, WORLD_HEIGHT/2 + glyphLayout.height + 5);
        setUpText(glyphLayout, bitmapFont, batch, "Quit", WORLD_WIDTH/2 - 15, WORLD_HEIGHT/2 - glyphLayout.height - 8);
    }

    private void setUpText(GlyphLayout glyphLayout, BitmapFont bitmapFont, SpriteBatch batch, String string, float x, float y){
        glyphLayout.setText(bitmapFont, string);
        bitmapFont.draw(batch, string, x, y);
    }

    /*
    Input: Void
    Output: Void
    Purpose: Releases the assets
    */
    void dispose() {
        menuButtonScreen.dispose();
        pauseMenuScreen.dispose();
    }

}
