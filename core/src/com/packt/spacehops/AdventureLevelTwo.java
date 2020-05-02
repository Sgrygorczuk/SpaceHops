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
    private TextureRegion scaleTexture;               //Trail behind the "Dragon"
    private TextureRegion fireTexture;                //Texture of the "Fire" attack
    private TextureRegion laserTexture;               //Texture of the laser
    private TextureRegion borderTexture;              //Texture of the bounds on top and bottom of screen
    private TextureRegion dragonHeadTexture;          //Texture of the enemy
    private TextureRegion progressBarTexture;         //Texture of the progress bar
    private TextureRegion progressBarFrameTexture;    //Texture of the Frame of the Progress bar
    private TextureRegion backgroundTexture;          //Background
    private TextureRegion portalLineTexture;          //Texture of the lines flying in the backgrounf
    private TextureRegion profileTexture;             //Profile of the talking character
    private TextureRegion communicationFrameTexture;  //Frame
    private TextureRegion sputnikTexture;             //Sputnik NPC Texture

    /*
    User spaceship object
     */
    private SpaceCraft spaceCraft;          //Player object
    private Dragon dragon;                  //Enemy object
    private ConversationBox conversationBox;//Conversation box that is used to talk to the user

    /*
    Array of the asteroids and collectibles that the user will encounter
     */
    private Array<SpaceBorder> spaceBorders = new Array<>();     //Array of asteroids
    private Array<Collectible> collectibles = new Array<>();    //Array of collectibles
    private Array<Rectangle> portalLines = new Array<>();       //Keeps track of the background lines

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
    private static final int GOAL = 10;          //Goal of the level to end
    private boolean debugFlag = false;          //Tells screen to draw debug wireframe
    private boolean textureFlag = true;         //Tells screen to draw textures
    private boolean stopSpawningFlag = false;   //Tells screen to stop creating more asteroids and collectibles
    private boolean endLevelFlag = false;       //Tells screen that the level is complete and to give the next stage menu
    private boolean screenOnFlag = true;        //Tells screen that the conversation box is on
    private boolean sputnikAliveFlag = true;    //Tells screen to show the sputnik texture
    private boolean sputnikMovementFlag = false; //Tells sputnik to move up and down, true = up, false = down
    private float sputnikY = 240;               //Base y position of the Sputnik

    //
    private final SpaceHops spaceHops;
    AdventureLevelTwo(SpaceHops spaceHops) { this.spaceHops = spaceHops; }

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

        //Enemy set up
        dragon = new Dragon(dragonHeadTexture, scaleTexture, fireTexture, laserTexture);

        //Player UI
            //Progress of stage
        progressBar = new ProgressBar(WORLD_WIDTH,WORLD_HEIGHT,progressBarFrameTexture,progressBarTexture);
        progressBar.setGoal(GOAL);
            //Talk from NPC
        conversationBox = new ConversationBox(WORLD_WIDTH, WORLD_HEIGHT, communicationFrameTexture, profileTexture);
            //Menus
        pauseMenu = new PauseMenu(spaceHops);

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
        TextureAtlas levelAtlas = spaceHops.getAssetManager().get("level_two_assets.atlas");
        TextureAtlas shipAtlas = spaceHops.getAssetManager().get("ship_assets.atlas");
        TextureAtlas profileAtlas = spaceHops.getAssetManager().get("profile_assets.atlas");
        TextureAtlas uiAtlas = spaceHops.getAssetManager().get("ui_assets.atlas");

        //Spaceship
        spaceCraftTexture = shipAtlas.findRegion("SpaceshipPack");


        //Collectible
        collectibleTexture = uiAtlas.findRegion("CollectiblePack");

        //Enemy
        dragonHeadTexture = levelAtlas.findRegion("DragonPack");
        laserTexture = levelAtlas.findRegion("Laser");
        fireTexture = levelAtlas.findRegion("CloudPack");
        scaleTexture = levelAtlas.findRegion("TearPack");
        borderTexture = levelAtlas.findRegion("CloudBoarder");

        //Background
        backgroundTexture = levelAtlas.findRegion("PortalBackground");
        portalLineTexture = levelAtlas.findRegion("PortalLines");
        sputnikTexture = levelAtlas.findRegion("Spudnik");

        //Communication Frame
        communicationFrameTexture = uiAtlas.findRegion("CommunicationFrame");
        profileTexture = profileAtlas.findRegion("RussianPack");

        //Progress Bar
        progressBarTexture = uiAtlas.findRegion("Progress");
        progressBarFrameTexture = uiAtlas.findRegion("ProgressBar");
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
        if(portalLines.size > 0) {drawDebugPortalLine(shapeRendererBackground);}
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
        if(pauseMenu.getDisposeFlag()){dispose();}      //If flag is true delete all the Stage and Texture objects
        updateSputnik();                                //Checks if sputnik should be displayed
        if(screenOnFlag) {updateCommunicationScreenTime(delta);}   //Counts down till screen goes down
        updatePart(delta);                              //Updates the phase of the level changing the enemy pattern
        updatePortalLines();                            //Updates the background line position
        updateCheckForDeath();                          //Check for death collision
        updateDragon(delta);                            //Updates the action and position of enemy
        updateCollectibles();                           //Update the position of collectibles
        updateSpaceBoarders();                          //Updates player with progress through the stage
        updateSpaceship();                       //Updates the position of the spaceship
    }

    /*
    Input: Void
    Output: Void
    Purpose: Updates the position and turns off texture if "killed"
    */
    private void updateSputnik(){
        updateSputnikPosition();
        if(dragon.getX() + dragon.getWidth() >= WORLD_WIDTH/4 + sputnikTexture.getRegionWidth()){sputnikAliveFlag = false;}
    }


    /*
    Input: Void
    Output: Void
    Purpose: Updates the State and position of the of the sputnik
    */
    private void updateSputnikPosition(){
        //oscillating_horizontal : true = up, false = down]
        if(sputnikY == 250){
            sputnikMovementFlag = false;}
        else if(sputnikY == 230){
            sputnikMovementFlag = true;}

        if(sputnikMovementFlag){ sputnikY += 1; }
        else{ sputnikY -= 1; }
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
            dragon.setStart();
        }
        //Moves from Part 2 to Part 3, turns on commutation window, sets dragon to bite enemy
        if(part == PART.PartTwo && progressBar.getScore() == 3){
            part = PART.PartThree;
            dragon.setPhase(1);
            screenOnFlag = true;
        }
        //Moves from Part 2 to Part 3, turns on commutation window, sets dragon to shoot fire and bite
        if(part == PART.PartThree && progressBar.getScore() == 6){
            part = PART.PartFour;
            dragon.setPhase(2);
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
    private void updateCheckForDeath(){
        //Checks if the player touched the boarders
        for (SpaceBorder spaceBorder : spaceBorders) {
            if(spaceBorder.isColliding(spaceCraft)){restart();}
        }
        //Checks if the player touched the enemy dragon or any of it's attacks
        if(dragon.isColliding(spaceCraft)){restart();}
    }

    /*
    Input: Void
    Output: Void
    Purpose: Central update dragon, all of the dragon updates are internal
    */
    private void updateDragon(float delta){ dragon.update(delta);}

    /*
    Input: Void
    Output: Void
    Purpose: Central collectible update method. Updates if we need a new collectible or if one needs to be removed
    */
    private void updateCollectibles(){
        checkIfNewCollectibleIsNeeded();
        removeCollectible();
    }

    /*
    Input: Void
    Output: Void
    Purpose: Checks if there is needs to create a new collectible, creates one once the dragon
        passes over the player y line
    */
    private void checkIfNewCollectibleIsNeeded(){
        if(dragon.getX() + dragon.getWidth()/2 >= 2*WORLD_WIDTH/3 && collectibles.size < 1){
            createNewCollectible();
        }
    }

    /*
    Input: Void
    Output: Void
    Purpose: Creates a new collectible
    */
    private void createNewCollectible(){
        Collectible collectible = new Collectible(collectibleTexture);
        collectible.setPosition(2*WORLD_WIDTH/3,dragon.getCentralY() + dragon.getHeight(), dragon.getHeight());
        collectible.setRadius(10f);
        collectibles.add(collectible);
    }

    /*
    Input: Void
    Output: Void
    Purpose: Removes a collectible if it was touched, updates the score and if score is equal to goal
    tells level to end
    */
    private void removeCollectible(){
        for(Collectible collectible : collectibles){
            //If space craft collides removes collectible and increases score
            if (collectible.isColliding(spaceCraft)) {
                collectibles.removeValue(collectible,true);
                progressBar.update();
                //If score is done end game
                if(progressBar.getScore() == GOAL){
                    Gdx.input.setInputProcessor(pauseMenu.getNextLevelStage());
                    endLevelFlag = true;}
            }
        }
    }


    /*
    Input: Void
    Output: Void
    Purpose: Central method for updating the portal lines
    */
    private void updatePortalLines(){
        checkIfNewPortalLineIsNeeded();     //Checks if we need more
        if(portalLines.size > 0) {
            updatePortalLinePosition();         //Updates their position
            removePortalLine();                 //Removes liens that are off screen
        }
    }

    /*
    Input: Void
    Output: Void
    Purpose: Checks if we need more lines, if we do makes more
    */
    private void checkIfNewPortalLineIsNeeded(){ if(portalLines.size < 5){createPortalLine();} }

    /*
    Input: Void
    Output: Void
    Purpose: Creates a new line
    */
    private void createPortalLine(){
        float height = MathUtils.random(1,3);       //Sets a random height of the line
        float width = MathUtils.random(16,64);      //Sets a random width of the height
        float y = MathUtils.random(WORLD_HEIGHT);   //Sets a random position on the screen
        Rectangle rectangle = new Rectangle(WORLD_WIDTH, y, width,height);  //Creates a rectangle
        portalLines.add(rectangle);                 //Adds it to the array
    }

    /*
    Input: Void
    Output: Void
    Purpose: Updates the position of the line with the speed being a ration between the width of line and world
    */
    private void updatePortalLinePosition(){for (Rectangle portalLine : portalLines){ portalLine.x -= WORLD_WIDTH/portalLine.width; }}

    /*
    Input: Void
    Output: Void
    Purpose: Removes the lines if they go off screen
    */
    private void removePortalLine(){
        for (Rectangle portalLine : portalLines) {
            if (portalLine.x + portalLine.width < 0) { portalLines.removeValue(portalLine, true);} }
        }

    /*
    Input: ShapeRender to add to the list of things to draw
    Output: Void
    Purpose: Draws the wire frame of the lines
    */
    private void drawDebugPortalLine(ShapeRenderer shapeRenderer){
            for (Rectangle portalLine : portalLines){ shapeRenderer.rect(portalLine.x, portalLine.y, portalLine.width, portalLine.height); }
    }

    /*
    Input: Batch to add to the list of textures
    Output: Void
    Purpose: Draws the texture
    */
    private void drawPortalLine(SpriteBatch batch){
        for (Rectangle portalLine : portalLines){ batch.draw(portalLineTexture, portalLine.x, portalLine.y, portalLine.width, portalLine.height); }
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
        sputnikAliveFlag = true;
        sputnikMovementFlag = false;
        sputnikY = 240;
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
        //Draws the background
        batch.draw(backgroundTexture, 0 ,0, WORLD_WIDTH, WORLD_HEIGHT);
        //If there are lines draw lines
        if(portalLines.size > 0){drawPortalLine(batch);}
        //Draws player
        spaceCraft.draw(batch);
        //Draws collectibles
        for (Collectible collectible : collectibles){collectible.draw(batch);}
        //Draws the boarder s
        for(SpaceBorder spaceBorder : spaceBorders){spaceBorder.draw(batch);}
        //Draws the dragon
        dragon.draw(batch);
        //Draws sputnik
        if(sputnikAliveFlag){batch.draw(sputnikTexture, WORLD_WIDTH/4, sputnikY);}
        //Draws conversation box
        conversationBox.draw(batch);
        //Draws progress bar
        progressBar.draw(batch, glyphLayout, bitmapFont);
        //Draws the pause menu frame
        if(pauseMenu.getPauseFlag() || endLevelFlag){pauseMenu.draw(batch);}
        batch.end();

        //Buttons are not part of the batch
        //Draw the start pause button
        if(!pauseMenu.getPauseFlag() && !endLevelFlag){pauseMenu.getMenuButtonStage().draw();}
        //Draws pause menu
        else {pauseMenu.getPauseMenuScreen().draw();}
        //Shows the exit level menu
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