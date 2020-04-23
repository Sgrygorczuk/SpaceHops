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
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
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
    private Texture collectibleTexture;
    private Texture scaleTexture;
    private Texture fireTexture;
    private Texture laserTexture;
    private Texture borderTexture;
    private Texture dragonHeadTexture;
    private Texture progressBarTexture;
    private Texture progressBarFrameTexture;
    private Texture backgroundTexture;
    private Texture portalLineTexture;
    private Texture profileTexture;
    private Texture communicationFrameTexture;
    private Texture sputnikTexture;

    /*
    User spaceship object
     */
    private SpaceCraft spaceCraft;
    private Dragon dragon;
    private ConversationBox conversationBox;//Conversation box that is used to talk to the user

    /*
    Array of the asteroids and collectibles that the user will encounter
     */
    private Array<SpaceBorder> spaceBorders = new Array<>();         //Array of asteroids
    private Array<Collectible> collectibles = new Array<>();    //Array of collectibles
    private Array<Rectangle> portalLines = new Array<>();

    //Background objects we use
    private PauseMenu pauseMenu;
    private ProgressBar progressBar;        //Progress Bar that show user's progress

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
    private static final int GOAL = 10;           //Goal of the level to end
    private boolean debugFlag = false;          //Tells screen to draw debug wireframe
    private boolean textureFlag = true;         //Tells screen to draw textures
    private boolean stopSpawningFlag = false;   //Tells screen to stop creating more asteroids and collectibles
    private boolean endLevelFlag = false;       //Tells screen that the level is complete and to give the next stage menu
    private boolean screenOnFlag = true;        //Tells screen that the conversation box is on
    private boolean sputnikAliveFlag = true;

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

        conversationBox = new ConversationBox(WORLD_WIDTH, WORLD_HEIGHT, communicationFrameTexture, profileTexture);

        dragon = new Dragon(dragonHeadTexture, scaleTexture, fireTexture, laserTexture);
        pauseMenu = new PauseMenu(game);

        //Player UI
        progressBar = new ProgressBar(WORLD_WIDTH,WORLD_HEIGHT,progressBarFrameTexture,progressBarTexture);
        progressBar.setGoal(GOAL);
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

        borderTexture = new Texture(Gdx.files.internal("CloudBoarder.png"));

        //Collectible
        collectibleTexture = new Texture(Gdx.files.internal("CollectiblePack.png"));

        dragonHeadTexture = new Texture(Gdx.files.internal("DragonPack.png"));
        laserTexture = new Texture(Gdx.files.internal("Laser.png"));
        fireTexture = new Texture(Gdx.files.internal("CloudPack.png"));
        scaleTexture = new Texture(Gdx.files.internal("TearPack.png"));

        //Background
        backgroundTexture = new Texture(Gdx.files.internal("PortalBackground.png"));
        portalLineTexture = new Texture(Gdx.files.internal("PortalLines.png"));

        //Communication Frame
        communicationFrameTexture = new Texture(Gdx.files.internal("CommunicationFrame.png"));
        profileTexture = new Texture(Gdx.files.internal("RussianPack.png"));

        //Progress Bar
        progressBarTexture = new Texture(Gdx.files.internal("Progress.png"));
        progressBarFrameTexture = new Texture(Gdx.files.internal("ProgressBar.png"));

        sputnikTexture = new Texture(Gdx.files.internal("Spudnik.png"));
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
        drawDebugPortalLine(shapeRendererBackground);
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
        updateSputnik();
        updateCommunicationScreenTime(delta);
        updatePart(delta);
        updatePortalLines();
        updateCheckForDeath();
        updateDragon(delta);
        updateCollectibles();
        updateSpaceBoarders();
        updateSpaceship();                       //Updates the position of the spaceship
    }

    private void updateSputnik(){ if(dragon.getX() + dragon.getWidth() >= WORLD_WIDTH/4 + sputnikTexture.getWidth()){sputnikAliveFlag = false;} }

    /*
Input: Delta, timing
Output: Void
Purpose: Checks for what stage of the level we are and sets off appropriate events that correspond
*/
    private void updatePart(float delta){
        //Moves from Part 1 to Part 2, turns on commutation window
        if(part == PART.PartOne && !screenOnFlag){ screenOnFlag = true;}
        else if(part == PART.PartOne && moveTimer-delta <= 0){
            part = PART.PartTwo;
            dragon.setStart();}
        //Moves from Part 2 to Part 3, turns on commutation window
        if(part == PART.PartTwo && progressBar.getScore() == 3){
            part = PART.PartThree;
            dragon.setPhase(1);
            screenOnFlag = true;
        }
        if(part == PART.PartThree && progressBar.getScore() == 6){
            part = PART.PartFour;
            dragon.setPhase(2);
            screenOnFlag = true;
        }
        if(part == PART.PartFour && progressBar.getScore() == 9){
            part = PART.PartFive;
            screenOnFlag = true;
        }

        //Tells the screen to turn on and which text output to give
        if(screenOnFlag){ conversationBox.update(delta, 0);}
        else{conversationBox.update(delta, 1);}

    }

    /*
    Input: Delta, timing
    Output: Void
    Purpose: Counts down until the communication screen turns off
    */
    private void updateCommunicationScreenTime(float delta) {
        moveTimer -= delta;
        if (moveTimer <= 0) {
            moveTimer = MOVE_TIME;
            screenOnFlag = false;
        }
    }

    /*
    Input: Delta
    Output: Void
    Purpose: Updates the position of the spaceship
    */
    private void updateSpaceship(){
        spaceCraft.update();
        if(Gdx.input.isKeyJustPressed(Input.Keys.UP)) {
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

    private void updateCheckForDeath(){
        for (SpaceBorder spaceBorder : spaceBorders) {
            if(spaceBorder.isColliding(spaceCraft)){restart();}
        }
        if(dragon.isColliding(spaceCraft)){restart();}
    }

    private void updateDragon(float delta){ dragon.update(delta);}

    private void updateCollectibles(){
        checkIfNewCollectibleIsNeeded();
        removeCollectible();
    }

    private void updatePortalLines(){
        System.out.println(portalLines.size);
        checkIfNewPortalLineIsNeeded();
        updatePortalLinePosition();
        removePortalLine();
    }

    private void checkIfNewPortalLineIsNeeded(){ if(portalLines.size < 5){createPortalLine();} }

    private void createPortalLine(){
        float height = MathUtils.random(1,3);
        float width = MathUtils.random(16,64);
        float y = MathUtils.random(WORLD_HEIGHT);
        Rectangle rectangle = new Rectangle(WORLD_WIDTH, y, width,height);
        portalLines.add(rectangle);
    }

    private void updatePortalLinePosition(){
        if(portalLines.size > 0){
            for (Rectangle portalLine : portalLines){
                System.out.println(portalLine.x);
                portalLine.x -= WORLD_WIDTH/portalLine.width;
            }
        }
    }

    private void removePortalLine(){
        if(portalLines.size > 0) {
            for (Rectangle portalLine : portalLines) {
                if (portalLine.x + portalLine.width < 0) { portalLines.removeValue(portalLine, true);} }
        }
    }

    private void drawDebugPortalLine(ShapeRenderer shapeRenderer){
        if(portalLines.size > 0){
            for (Rectangle portalLine : portalLines){
                shapeRenderer.rect(portalLine.x, portalLine.y, portalLine.width, portalLine.height);
            }
        }
    }

    private void drawPortalLine(SpriteBatch batch){
        if(portalLines.size > 0){
            for (Rectangle portalLine : portalLines){
                batch.draw(portalLineTexture, portalLine.x, portalLine.y, portalLine.width, portalLine.height);
            }
        }
    }

    /*
    Input: Void
    Output: Void
    Purpose: Checks if there is need to create a new asteroid if does calls createNewAsteroid()
    */
    private void checkIfNewCollectibleIsNeeded(){
        if(dragon.getX() + dragon.getWidth()/2 >= 2*WORLD_WIDTH/3 && collectibles.size < 1){
            createNewCollectible();
        }
    }

    /*
    Input: Void
    Output: Void
    Purpose: Creates a new set of boarders
    */
    private void createNewCollectible(){
        Collectible collectible = new Collectible(collectibleTexture);
        collectible.setPosition(2*WORLD_WIDTH/3,dragon.getCentralY() + dragon.getHeight(), dragon.getHeight());
        collectible.setRadius(10f);
        collectibles.add(collectible);
    }

    private void removeCollectible(){
        for(Collectible collectible : collectibles){
            if (collectible.isColliding(spaceCraft)) {
                collectibles.removeValue(collectible,true);
                progressBar.update();
                if(progressBar.getScore() == GOAL){
                    Gdx.input.setInputProcessor(pauseMenu.getNextLevelStage());
                    endLevelFlag = true;}
            }
        }
    }

    private void updateSpaceBoarders(){
        checkIfRemoveSpaceBoarder();
        checkIfNewBoarderIsNeeded();
        updateSpaceBoarderPosition();
    }

    /*
    Input: Void
    Output: Void
    Purpose: Creates a new set of boarders
    */
    private void createNewSpaceBoarder(float initialX){
        SpaceBorder spaceBorder = new SpaceBorder(initialX, borderTexture, borderTexture);
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
        dragon.restart();
        progressBar.restart();
        collectibles.clear();
        part = PART.PartOne;
        moveTimer = MOVE_TIME;
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
        batch.draw(backgroundTexture, 0 ,0, WORLD_WIDTH, WORLD_HEIGHT);
        drawPortalLine(batch);
        spaceCraft.draw(batch);
        for (Collectible collectible : collectibles){collectible.draw(batch);}
        for(SpaceBorder spaceBorder : spaceBorders){spaceBorder.draw(batch);}
        dragon.draw(batch);
        if(sputnikAliveFlag){batch.draw(sputnikTexture, WORLD_WIDTH/4, 240);}
        conversationBox.draw(batch);        //Draws conversation box
        progressBar.draw(batch, glyphLayout, bitmapFont);
        if(pauseMenu.getPauseFlag() || endLevelFlag){pauseMenu.draw(batch);}
        batch.end();
        //Buttons are not part of the batch
        if(!pauseMenu.getPauseFlag() && !endLevelFlag){pauseMenu.getMenuButtonStage().draw();}
        else {pauseMenu.getPauseMenuScreen().draw();}
        if(endLevelFlag) {pauseMenu.getNextLevelStage().draw();}
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