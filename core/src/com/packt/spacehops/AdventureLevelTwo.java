/*
The GameScreen class process all of the information that is being displayed to the user
while in an active game environment.
    It creates the wireframe that is used to set up the game.
    Adds textures over the wireframe
    Render all the updates to the objects on screen


    Collectible mechanics, to match the distancing between asteroids and collectibles
        the collectibles are turned invisible at collision and gotten rid off once they leave
        the screen otherwise they would slowly encroach onto of the asteroids where players
        can't get to
 */

package com.packt.spacehops;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

class AdventureLevelTwo extends ScreenAdapter {

    /*
    Dimensions -- Units the screen has
     */
    private static final float WORLD_WIDTH = 320;
    private static final float WORLD_HEIGHT = 480;

    /*
    Games States dictate how the game should be behaving, we always start in Playing state here
    */
    private enum PART {PartOne, PartTwo, PartThree, PartFour, PartFive}
    private PART part = PART.PartOne;

    /*
    Image processing -- Objects that modify the view and textures
     */
    private ShapeRenderer shapeRendererEnemy; //Creates the wire frames
    private ShapeRenderer shapeRendererUser;
    private ShapeRenderer shapeRendererBackground;
    private ShapeRenderer shapeRendererCollectible;

    private Viewport viewport;			 //The screen where we display things
    private Camera camera;				 //The camera viewing the viewport
    private SpriteBatch batch;			 //Batch that holds all of the textures

    /*
    Textures
     */
    private Texture spaceCraftTexture;
    private Texture menuBackgroundTexture;
    private Texture scaleTexture;
    private Texture floorBorderTexture;
    private Texture ceilingBorderTexture;
    private Texture dragonHeadTexture;

    /*
    User spaceship object
     */
    private SpaceCraft spaceCraft;
    private Dragon dragon;

    /*
    Array of the asteroids and collectibles that the user will encounter
     */
    private Array<SpaceBorder> spaceBorders = new Array<>();         //Array of asteroids
    private Array<Collectible> collectibles = new Array<>();    //Array of collectibles

    //Background objects we use
    private PauseMenu pauseMenu;

    //Timing variables
    private static final float MOVE_TIME = 10F;                 //Time that the conversation box stays on screen
    private float moveTimer = MOVE_TIME;                        //Counter that checks if it reached the end of time

    /*
    Bitmap and GlyphLayout
     */
    private BitmapFont bitmapFont;
    private GlyphLayout glyphLayout;

    /*
    Flags
     */
    private boolean debugFlag = true;          //Tells screen to draw debug wireframe
    private boolean textureFlag = false;         //Tells screen to draw textures
    private boolean stopSpawningFlag = false;   //Tells screen to stop creating more asteroids and collectibles
    private boolean endLevelFlag = false;       //Tells screen that the level is complete and to give the next stage menu
    private boolean screenOnFlag = true;        //Tells screen that the conversation box is on

    //
    private final Game game;
    AdventureLevelTwo(Game game) { this.game = game; }

    /*
    Input: The width and height of the screen
    Output: Void
    Purpose: Updates the dimensions of the screen
    */
    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
    }

    /*
    Input: Void
    Output: Void
    Purpose: Initializes all the variables that are going to be displayed
    */
    @Override
    public void show() {
        showCamera();           //Sets up camera through which objects are draw through
        showTexture();          //Connects textures to the images
        showObjects();          //Creates object and passes them the dimensions and textures
        showRender();           //Sets up renders that will draw the debug of objects

        //Sets up the texture with the images
        batch = new SpriteBatch();

        //BitmapFont and GlyphLayout
        bitmapFont = new BitmapFont();
        glyphLayout = new GlyphLayout();
    }

    /*
    Input: Void
    Output: Void
    Purpose: Initializes the objects that are going to be displayed.
    Mostly giving objects dimension, position and connecting them to textures.
    */
    private void showObjects(){
        //Spaceship creation
        spaceCraft = new SpaceCraft(spaceCraftTexture);
        spaceCraft.updatePosition(2*WORLD_WIDTH/3, WORLD_HEIGHT/2);

        dragon = new Dragon(dragonHeadTexture, scaleTexture);
        pauseMenu = new PauseMenu(game);
    }

    /*
    Input: Void
    Output: Void
    Purpose: Sets up the camera through which all the objects are view through
    */
    private void showCamera(){
        camera = new OrthographicCamera();									//Sets a 2D view
        camera.position.set(WORLD_WIDTH / 2, WORLD_HEIGHT / 2, 0);	//Places the camera in the center of the view port
        camera.update();													//Updates the camera
        viewport = new FitViewport(WORLD_WIDTH, WORLD_HEIGHT, camera);		//
    }


    /*
    Input: Void
    Output: Void
    Purpose: Connects the images to the Texture objects
    */
    private void showTexture(){
        //Spaceship
        spaceCraftTexture = new Texture(Gdx.files.internal("SpaceshipPack.png"));

        menuBackgroundTexture = new Texture(Gdx.files.internal("CommunicationFrame.png"));

        floorBorderTexture = new Texture(Gdx.files.internal("Top.png"));
        ceilingBorderTexture = new Texture(Gdx.files.internal("Bottom.png"));

        dragonHeadTexture = new Texture(Gdx.files.internal("DragonHead.png"));
        scaleTexture = new Texture(Gdx.files.internal("CollectiblePack.png"));
    }

    /*
    Input: Void
    Output: Void
    Purpose: Sets up the different renders to draw objects in wireframe
    */
    private void showRender(){
        //Enemy
        shapeRendererEnemy = new ShapeRenderer();
        shapeRendererEnemy.setColor(Color.RED);

        //User
        shapeRendererUser = new ShapeRenderer();
        shapeRendererUser.setColor(Color.GREEN);

        //Background
        shapeRendererBackground = new ShapeRenderer();
        shapeRendererBackground.setColor(Color.WHITE);

        //Intractable
        shapeRendererCollectible = new ShapeRenderer();
        shapeRendererCollectible.setColor(Color.BLUE);
    }

    /*
    Input: Void
    Output: Void
    Purpose: Draws all of the variables on the screen
    */
    @Override
    public void render(float delta) {
        clearScreen();	                //Wipes screen
        setTextureMode();               //Checks if the user changed the status of texturesOnFlag
        if(textureFlag) {draw();}	    //Draws the textures

        setDebugMode();                 //Checks if user changed the status of the debugModeFlag
        if(debugFlag) {                 //If debugMode is on ShapeRender will drawing lines
            renderEnemy();              //Draws Enemy Wire Frame
            renderUser();               //Draws User Wire Frame
            renderBackground();         //Draws Background Wire Frame
            renderCollectible();        //Draws Collectible Wire Frame
        }
        if(!pauseMenu.getPauseFlag() && !endLevelFlag){ update(delta);} //Updates the variables of all object if the game is not paused
    }

    /*
    Input: Void
    Output: Void
    Purpose: Draws the enemy/obstacle wireframe
    */
    private void renderEnemy(){
        shapeRendererEnemy.setProjectionMatrix(camera.projection);      		                 //Screen set up camera
        shapeRendererEnemy.setTransformMatrix(camera.view);            			                 //Screen set up camera
        shapeRendererEnemy.begin(ShapeRenderer.ShapeType.Line);         		                 //Sets up to draw lines
        for(SpaceBorder spaceBorder : spaceBorders) {spaceBorder.drawDebug(shapeRendererEnemy);}
        dragon.drawDebug(shapeRendererEnemy);
        shapeRendererEnemy.end();
    }

    /*
    Input: Void
    Output: Void
    Purpose: Draws user wireframe
    */
    private void renderUser(){
        shapeRendererUser.setProjectionMatrix(camera.projection);    //Screen set up camera
        shapeRendererUser.setTransformMatrix(camera.view);           //Screen set up camera
        shapeRendererUser.begin(ShapeRenderer.ShapeType.Line);       //Sets up to draw lines
        spaceCraft.drawDebug(shapeRendererUser);                     //Draws draws the spaceship
        shapeRendererUser.end();
    }

    /*
    Input: Void
    Output: Void
    Purpose: Draws the background object and UI wireframes
    */
    private void renderBackground(){
        shapeRendererBackground.setProjectionMatrix(camera.projection);                 //Screen set up camera
        shapeRendererBackground.setTransformMatrix(camera.view);                        //Screen set up camera
        shapeRendererBackground.begin(ShapeRenderer.ShapeType.Line);                    //Starts to draw
        shapeRendererBackground.end();
    }

    /*
    Input: Void
    Output: Void
    Purpose: Draws wireframe of the collectibles -- needs to be redone along with collectible objects
    */
    private void renderCollectible(){
        shapeRendererCollectible.setProjectionMatrix(camera.projection);
        shapeRendererCollectible.setTransformMatrix(camera.view);
        shapeRendererCollectible.begin(ShapeRenderer.ShapeType.Line);
        for(Collectible collectible : collectibles) {collectible.drawDebug(shapeRendererCollectible);}
        shapeRendererCollectible.end();
    }


    /*
    Input: Void
    Output: Void
    Purpose: Checks for user input if the user clicks turns the debugMode flag on and off
    */
    private void setDebugMode() { if(Gdx.input.isKeyJustPressed(Input.Keys.NUM_1)) { debugFlag = !debugFlag;} }

    /*
    Input: Void
    Output: Void
    Purpose: Checks for user input if the user clicks turns the debugMode flag on and off
    */
    private void setTextureMode() { if(Gdx.input.isKeyJustPressed(Input.Keys.NUM_2)) {textureFlag = !textureFlag;} }

    /*
    Input: Delta, timing
    Output: Void
    Purpose: Central function that starts all the other update functions
    */
    private void update(float delta){
        //If we are leaving the screen we get rid of everything in memory
        if(pauseMenu.getDisposeFlag()){dispose();}
        updateForDeath();
        updateDragon(delta);
        updateSpaceBoarders();
        updateSpaceship();                       //Updates the position of the spaceship
    }

    /*
    Input: Delta
    Output: Void
    Purpose: Updates the position of the spaceship
    */
    private void updateSpaceship(){
        spaceCraft.update();
        if(Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            spaceCraft.flyUp();
        }
        blockSpaceshipLeavingTheWorld();
    }

    /*
    Input: Void
    Output: Void
    Purpose: Stops the Circle from going past the bottom of the screen
    */
    private void blockSpaceshipLeavingTheWorld(){
        //Gets the Y, sets the min: Radius away from bottom, max: World Height - Radius
        spaceCraft.updatePosition(spaceCraft.getX(), MathUtils.clamp(spaceCraft.getY(), spaceCraft.getRadius(), WORLD_HEIGHT - spaceCraft.getRadius()) );
    }

    private void updateForDeath(){
        for (SpaceBorder spaceBorder : spaceBorders) {
            if(spaceBorder.isColliding(spaceCraft)){restart();}
        }
        if(dragon.isColliding(spaceCraft)){restart();}
    }

    private void updateSpaceBoarders(){
        checkIfRemoveSpaceBoarder();
        checkIfNewBoarderIsNeeded();
        updateSpaceBoarderPosition();
    }

    private void updateDragon(float delta){ dragon.updatePosition(delta);}

    /*
    Input: Void
    Output: Void
    Purpose: Creates a new set of boarders
    */
    private void createNewSpaceBoarder(float initialX){
        SpaceBorder spaceBorder = new SpaceBorder(initialX, floorBorderTexture, ceilingBorderTexture);
        spaceBorders.add(spaceBorder);
    }

    /*
    Input: Void
    Output: Void
    Purpose: Checks if there is need to create a new asteroid if does calls createNewAsteroid()
    */
    private void checkIfNewBoarderIsNeeded(){
        //If no asteroids on screen exits
        if (spaceBorders.size == 0) { createNewSpaceBoarder(0); }
        //If the distance between the world and the new asteroid is enough
        else if (spaceBorders.size == 1){ createNewSpaceBoarder(WORLD_WIDTH);}
    }

    /*
    Input: Void
    Output: Void
    Purpose: Gets rid of the spaceboarder that's off the screen
    */
    private void checkIfRemoveSpaceBoarder(){
        if(spaceBorders.size > 0){
            SpaceBorder firstSpaceBorder = spaceBorders.first();
            if(firstSpaceBorder.getX() <= -WORLD_WIDTH){spaceBorders.removeValue(firstSpaceBorder,true); }
        }
    }

    private void updateSpaceBoarderPosition(){ for(SpaceBorder spaceBorder : spaceBorders){ spaceBorder.updatePosition(); }}


    /*
    Input: Void
    Output: Void
    Purpose: Restart the variables to initial position and removes all flowers from array.
    */
    private void restart(){
        spaceCraft.updatePosition(2*WORLD_WIDTH/3,WORLD_HEIGHT/2);
        spaceCraft.restart();
        collectibles.clear();
        part = PART.PartOne;
        screenOnFlag = true;
    }

    /*
    Input: Void
    Output: Void
    Purpose: Central function that draws the textures
    */
    private void draw() {
        //Viewport/Camera projection
        batch.setProjectionMatrix(camera.projection);
        batch.setTransformMatrix(camera.view);
        //Batch setting up texture
        batch.begin();
        spaceCraft.draw(batch);
        for(SpaceBorder spaceBorder : spaceBorders){spaceBorder.draw(batch);}
        if(pauseMenu.getPauseFlag() || endLevelFlag){pauseMenu.draw(batch);}
        dragon.draw(batch);
        batch.end();
        //Buttons are not part of the batch
        if(!pauseMenu.getPauseFlag() && !endLevelFlag){pauseMenu.getMenuButtonStage().draw();}
        else {pauseMenu.getPauseMenuScreen().draw();}
        if(endLevelFlag) {pauseMenu.getPauseMenuScreen().draw();}
    }

    /*
    Input: Void
    Output: Void
    Purpose: Updates all the variables on the screen
    */
    private void clearScreen() {
        Gdx.gl.glClearColor(Color.BLACK.r, Color.BLACK.g, Color.BLACK.b, Color.BLACK.a); //Sets color to black
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);										 //Sends it to the buffer
    }
}