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

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

class AdventureLevelThree extends ScreenAdapter {

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
    private ShapeRenderer shapeRendererEnemy;           //Creates wire frame for enemy objects
    private ShapeRenderer shapeRendererUser;            //Creates wire frame for player object
    private ShapeRenderer shapeRendererBackground;      //Creates wire frame for background objects
    private ShapeRenderer shapeRendererCollectible;     //Creates wire frame for collectible objects

    private Viewport viewport;			 //The screen where we display things
    private Camera camera;				 //The camera viewing the viewport
    private SpriteBatch batch;			 //Batch that holds all of the textures

    /*
    Textures
     */
    private TextureRegion spaceCraftTexture;          //Player Texture
    private TextureRegion collectibleTexture;         //Collectible Texture
    private TextureRegion progressBarTexture;         //Texture of the progress bar
    private TextureRegion backgroundTexture;          //Background
    private TextureRegion profileTexture;             //Profile of the talking character
    private TextureRegion communicationFrameTexture;  //Frame
    private TextureRegion speedOMeterFrameTexture;
    private TextureRegion speedOMeterLightsTexture;
    private TextureRegion boxCollectibleTexture;
    private TextureRegion boxShieldTexture;
    private TextureRegion bombTexture;
    private TextureRegion botTexture;
    private TextureRegion robotArm;
    private TextureRegion shieldTexture;
    private TextureRegion borderTexture;
    private TextureRegion floatJunkTexture;

    /*
    User spaceship object
     */
    private SpaceCraft spaceCraft;          //Player object
    private WarehouseBot warehouseBot;
    private ConversationBox conversationBox;//Conversation box that is used to talk to the user

    /*
    Array of the asteroids and collectibles that the user will encounter
    */
    private float GAP_BETWEEN_FLOATING_OBJECTS = 200;
    private Array<Collectible> floatingObjects = new Array<>();     //Array of asteroids
    private Array<SpaceBorder> spaceBorders = new Array<>();     //Array of asteroids
    private Array<FloatingJunk> floatingJunksArray = new Array<>();     //Array of asteroids


    //Background objects we use
    private PauseMenu pauseMenu;
    private ProgressBar progressBar;        //Progress Bar that show user's progress
    private SpeedOMeter speedOMeter;

    //Timing variables
    private static final float MOVE_TIME = 10F;                 //Time that the conversation box stays on screen
    private float moveTimer = MOVE_TIME;                        //Counter that checks if it reached the end of time

    /*
    Bitmap and GlyphLayout
     */
    private BitmapFont bitmapFont;
    private GlyphLayout glyphLayout;
    private BitmapFont menuBitmapFont;
    private GlyphLayout menuGlyphLayout;

    /*
    Flags
     */
    private static final int GOAL = 10;          //Goal of the level to end
    private boolean debugFlag = false;          //Tells screen to draw debug wireframe
    private boolean textureFlag = true;         //Tells screen to draw textures
    private boolean endLevelFlag = false;       //Tells screen that the level is complete and to give the next stage menu
    private boolean screenOnFlag = true;        //Tells screen that the conversation box is on

    //
    private final SpaceHops spaceHops;
    AdventureLevelThree(SpaceHops spaceHops) { this.spaceHops = spaceHops; }

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
        menuBitmapFont = new BitmapFont();
        menuGlyphLayout = new GlyphLayout();
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
        spaceCraft.updatePosition(WORLD_WIDTH/2, WORLD_HEIGHT/2);
        spaceCraft.setShieldTexture(shieldTexture);

        //Player UI
        //Progress of stage
        progressBar = new ProgressBar(progressBarTexture);
        progressBar.setGoal(GOAL);

        setUpFloatingJunk(50,320,.5);
        setUpFloatingJunk(100,300, .3);
        setUpFloatingJunk(200,350,.4);
        setUpFloatingJunk(300,280,.2);

        warehouseBot = new WarehouseBot(botTexture, robotArm);

        speedOMeter = new SpeedOMeter(speedOMeterFrameTexture, speedOMeterLightsTexture);

        //Talk from NPC
        conversationBox = new ConversationBox(WORLD_WIDTH, WORLD_HEIGHT, communicationFrameTexture, profileTexture);
        //Menus
        pauseMenu = new PauseMenu(spaceHops);

    }

    void setUpFloatingJunk(float x, float y, double mod){
        FloatingJunk floatingJunk = new FloatingJunk(floatJunkTexture);
        floatingJunk.setStats(x, y, mod);
        floatingJunksArray.add(floatingJunk);
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
        TextureAtlas levelAtlas = spaceHops.getAssetManager().get("level_three_assets.atlas");
        TextureAtlas shipAtlas = spaceHops.getAssetManager().get("ship_assets.atlas");
        TextureAtlas profileAtlas = spaceHops.getAssetManager().get("profile_assets.atlas");
        TextureAtlas uiAtlas = spaceHops.getAssetManager().get("ui_assets.atlas");

        //Spaceship
        spaceCraftTexture = shipAtlas.findRegion("SpaceshipPack");
        shieldTexture = shipAtlas.findRegion("Sheild");

        backgroundTexture = levelAtlas.findRegion("LevelThreeBackground");

        floatJunkTexture = levelAtlas.findRegion("Mess");

        //SpeedOMeter
        speedOMeterFrameTexture = uiAtlas.findRegion("SpeedOMeter");
        speedOMeterLightsTexture = uiAtlas.findRegion("SpeedOMeterLights");

        //Collectible
        collectibleTexture = uiAtlas.findRegion("CollectiblePack");

        botTexture = levelAtlas.findRegion("RobotPacket");
        robotArm = levelAtlas.findRegion("RobotArm");

        TextureRegion levelThreePack = levelAtlas.findRegion("AsstetPackLvl3");
        TextureRegion[][] breakDownTexture = new TextureRegion(levelThreePack).split(40, 40); //Breaks down the texture into tiles
        boxCollectibleTexture = breakDownTexture[0][0];
        boxShieldTexture = breakDownTexture[0][1];
        bombTexture = breakDownTexture[0][2];

        //Communication Frame
        communicationFrameTexture = uiAtlas.findRegion("CommunicationFrame");
        profileTexture = profileAtlas.findRegion("WearhouseProfile");

        //
        borderTexture = levelAtlas.findRegion("LightingBorder");

        //Progress Bar
        progressBarTexture = uiAtlas.findRegion("Score");
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
        if(pauseMenu.getDisposeFlag()){dispose();}      //If flag is true delete all the Stage and Texture objects
        if(screenOnFlag) {updateCommunicationScreenTime(delta);}   //Counts down till screen goes down
        updateFloatingJunk();
        updatePart(delta);                              //Updates the phase of the level changing the enemy pattern
        checkForArmCollision();                          //Check for death collision
        updateCollectibles();                           //Update the position of collectibles
        if(part != PART.PartOne){updateSpaceBoarders();}
        updateSpaceship();                       //Updates the position of the spaceship
        warehouseBot.update(delta);
        speedOMeter.updateState(spaceCraft.getSpeed());
    }

    private void updateFloatingJunk(){
        if(floatingJunksArray.size > 1){
            for(FloatingJunk floatingJunk : floatingJunksArray){floatingJunk.updatePosition(); }
        }
    }

    /*
    Input: Delta, timing
    Output: Void
    Purpose: Checks for what stage of the level we are and sets off appropriate events that correspond
    */
    private void updatePart(float delta){
        //Moves from Part 1 to Part 2, turns on commutation window, brings dragon in once communication end
        if(part == PART.PartOne && !screenOnFlag){ screenOnFlag = true;}
        else if(part == PART.PartOne && moveTimer-delta <= 0){
            part = PART.PartTwo;
        }
        //Moves from Part 2 to Part 3, turns on commutation window, sets dragon to bite enemy
        if(part == PART.PartTwo && progressBar.getScore() == 3){
            part = PART.PartThree;
            warehouseBot.setStartSpinning();
            screenOnFlag = true;
        }
        //Moves from Part 2 to Part 3, turns on commutation window, sets dragon to shoot fire and bite
        if(part == PART.PartThree && progressBar.getScore() == 6){
            part = PART.PartFour;
            screenOnFlag = true;
        }
        //Moves from Part 2 to Part 3, turns on commutation window, sets dragon to shoot fire and laser and bite
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

    /*
    Input: Void
    Output: Void
    Purpose: Checks if the player collided with any enemy objects if they did restarts
    */
    private void checkForArmCollision(){
        if(warehouseBot.isColliding(spaceCraft)){
            if(spaceCraft.getShieldFlag()){
                spaceCraft.setShieldFlag();
                spaceCraft.flyUp();
            }
            else {
                restart();
            }
        }
    }

    /*
    Input: Void
    Output: Void
    Purpose: Central collectible update method. Updates if we need a new collectible or if one needs to be removed
    */
    private void updateCollectibles(){
        checkIfNeedNewFloatingObject();
        updateCollectiblePosition();
        checkForFloatingObjectCollision();
        removeNewFloatingObject();
    }

    private void checkIfNeedNewFloatingObject(){
        //No collectible exits
        if (floatingObjects.size == 0) {
            createNewFloatingObject();
        }
        //collectible is distance away
        else {
            Collectible collectible = floatingObjects.peek();
            if (collectible.getX() < WORLD_WIDTH - GAP_BETWEEN_FLOATING_OBJECTS) {
                createNewFloatingObject();
            }
        }
    }

    private void createNewFloatingObject(){
        TextureRegion region = boxCollectibleTexture;
        int choice = MathUtils.random(0,2);
        if(choice == 1 && !spaceCraft.getShieldFlag()){ region = boxShieldTexture; }
        else if(choice == 2){ region = bombTexture; }
        else { choice = 0;}
        Collectible newFloatingObject = new Collectible(region);
        newFloatingObject.setPosition(320, 100, 5);
        newFloatingObject.setBound();
        newFloatingObject.setState(choice);
        floatingObjects.add(newFloatingObject);
    }

    /*
    Input: Void
    Output: Void
    Purpose: Gets rid of the collectible from array that went past the screen
    */
    private void removeNewFloatingObject(){
        if(floatingObjects.size > 0){
            Collectible firstCollectible = floatingObjects.first();
            if(firstCollectible.getX() < - firstCollectible.getRadius()){ floatingObjects.removeValue(firstCollectible,true); }
        }
    }

    private void updateCollectiblePosition() {
        if (floatingObjects.size > 0) {
            for (Collectible collectible : floatingObjects) {
                collectible.updatePosition(2);
                collectible.updateY();
            }
        }
    }

    private void checkForFloatingObjectCollision(){
        if (floatingObjects.size > 0) {
            for (Collectible collectible : floatingObjects) {
                if(collectible.isColliding(spaceCraft)){
                    //If collides with bomb, end level
                    if(collectible.getState() == 2){
                        if(spaceCraft.getShieldFlag()){
                           spaceCraft.setShieldFlag();
                           floatingObjects.removeValue(collectible,true);
                        }
                        else {
                            restart();
                        }
                    }
                    else{
                        //If collides while speed is enough to break; breaks box
                        if(speedOMeter.getState() == 2){
                            //Breaks shield box, gets shield
                            if(collectible.getState() == 1){
                                spaceCraft.setShieldFlag();
                                floatingObjects.removeValue(collectible,true);
                            }
                            //Breaks collectible box, gets point
                            else {
                                floatingObjects.removeValue(collectible,true);
                                progressBar.update(); }
                        }
                        //Bounce off the box
                        else {
                            //If collides on top of box
                            if(spaceCraft.getY() > collectible.getY()){ spaceCraft.flyUp(); }
                            //If Collides on bottom
                            else{ spaceCraft.flyDown(); }
                        }
                    }
                }
            }
        }
    }

    /*
Input: Void
Output: Void
Purpose:Central function for updating the space boarders
*/
    private void updateSpaceBoarders(){
        checkIfRemoveSpaceBoarder();
        checkIfNewBoarderIsNeeded();
        updateSpaceBoarderPosition();
        checkForSpaceBoarderCollision();
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
    Purpose: Checks if there is need to create a new boarder
    */
    private void checkIfNewBoarderIsNeeded(){
        //If no boarder on screen exits
        if (spaceBorders.size == 0) { createNewSpaceBoarder(0); }
        //If the there is only one make another one
        else if (spaceBorders.size == 1){ createNewSpaceBoarder(WORLD_WIDTH);}
    }

    /*
    Input: Void
    Output: Void
    Purpose: Gets rid of the space boarder that's off the screen
    */
    private void checkIfRemoveSpaceBoarder(){
        if(spaceBorders.size > 0){
            SpaceBorder firstSpaceBorder = spaceBorders.first();
            if(firstSpaceBorder.getX() <= -WORLD_WIDTH){spaceBorders.removeValue(firstSpaceBorder,true); }
        }
    }

    /*
    Input: Void
    Output: Void
    Purpose: Updates the position of the boarders
    */
    private void updateSpaceBoarderPosition(){ for(SpaceBorder spaceBorder : spaceBorders){ spaceBorder.updatePosition(); }}

    private void checkForSpaceBoarderCollision(){
        for (SpaceBorder spaceBorder : spaceBorders) {
            if (spaceBorder.isColliding(spaceCraft)) {
                if (spaceCraft.getShieldFlag()) {
                    spaceCraft.setShieldFlag();
                    if(spaceCraft.getY() < 100) {spaceCraft.flyUp();}
                    else{ spaceCraft.flyDown();}
                }
                else { restart(); }
            }
        }
    }

    /*
    Input: Void
    Output: Void
    Purpose: Restart the variables to initial position and removes all flowers from array.
    */
    private void restart(){
        spaceCraft.updatePosition(WORLD_WIDTH/2,WORLD_HEIGHT/2);
        spaceCraft.restart();
        floatingObjects.clear();
        spaceBorders.clear();
        warehouseBot.restart();
        progressBar.restart();
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
        batch.draw(backgroundTexture, 0 ,0);
        for(FloatingJunk floatingJunk : floatingJunksArray){floatingJunk.draw(batch);}
        //Draws player
        drawFloatingObjects(batch);
        warehouseBot.draw(batch);
        spaceCraft.draw(batch);
        if(part != PART.PartOne){for(SpaceBorder spaceBorder : spaceBorders){spaceBorder.draw(batch);}}
        speedOMeter.draw(batch);
        //Draws conversation box
        conversationBox.draw(batch);
        //Draws progress bar
        progressBar.draw(batch, glyphLayout, bitmapFont);
        //Draws the pause menu frame
        if(pauseMenu.getPauseFlag() || endLevelFlag){pauseMenu.draw(batch);}
        batch.end();

        drawMenus();
    }

    private void drawFloatingObjects(SpriteBatch batch){
        if (floatingObjects.size > 0) {
            for (Collectible collectible : floatingObjects) {
                collectible.draw(batch);
            }
        }
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

    private void drawMenus(){
        //Buttons are not part of the batch
        if(!pauseMenu.getPauseFlag() && !endLevelFlag){
            pauseMenu.getMenuButtonStage().draw();

            batch.setProjectionMatrix(camera.projection);
            batch.setTransformMatrix(camera.view);
            //Batch setting up texture
            batch.begin();
            pauseMenu.drawMenuText(menuGlyphLayout, menuBitmapFont, batch);
            batch.end();
        }
        else {
            pauseMenu.getPauseMenuScreen().draw();

            batch.setProjectionMatrix(camera.projection);
            batch.setTransformMatrix(camera.view);
            //Batch setting up texture
            batch.begin();
            pauseMenu.drawPauseText(glyphLayout, bitmapFont, batch);
            batch.end();
        }
        if(endLevelFlag) {
            pauseMenu.getNextLevelStage().draw();

            batch.setProjectionMatrix(camera.projection);
            batch.setTransformMatrix(camera.view);
            //Batch setting up texture
            batch.begin();
            pauseMenu.drawNextLevelText(glyphLayout, bitmapFont, batch);
            batch.end();
        }
    }

    /*
Input: Void
Output: Void
Purpose: Destroys everything once we move onto the new screen
*/
    @Override
    public void dispose() {
        pauseMenu.dispose();
    }
}