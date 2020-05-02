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
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

class AdventureLevelOne extends ScreenAdapter {

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
    private TextureRegion topAsteroidTexture;
    private TextureRegion bottomAsteroidTexture;
    private TextureRegion earthTexture;
    private TextureRegion moonTexture;
    private TextureRegion collectibleTexture;
    private TextureRegion progressBarTexture;
    private TextureRegion profileTexture;
    private TextureRegion communicationFrameTexture;
    private TextureRegion progressBarFrameTexture;
    private TextureRegion spaceCraftTexture;

    /*
    User spaceship object
     */
    private SpaceCraft spaceCraft;

    /*
    Array of the asteroids and collectibles that the user will encounter
     */
    private Array<Asteroids> asteroids = new Array<>();         //Array of asteroids
    private Array<Collectible> collectibles = new Array<>();    //Array of collectibles

    //Background objects we use
    private Planet earth;                   //Shows earth and moon
    private ProgressBar progressBar;        //Progress Bar that show user's progress
    private ConversationBox conversationBox;//Conversation box that is used to talk to the user
    private PauseMenu pauseMenu;            //Pause Menu deals with buttons

    //Static variables
    private static final int ASTEROIDS_PASSED = 1;              //Amount of asteroids that need to be passed to move to next part
    private static final int GOAL = 10;                         //Goal of the level to end
    private static final float GAP_BETWEEN_ASTEROID = 200;      //Distance between objects

    //Timing variables
    private static final float MOVE_TIME = 10F;                 //Time that the conversation box stays on screen
    private float moveTimer = MOVE_TIME;                        //Counter that checks if it reached the end of time

    //
    private int asteroidsPassed = 0;      //In game counter to see how many asteroids have been passed

    /*
    Bitmap and GlyphLayout
     */
    private BitmapFont bitmapFont;
    private GlyphLayout glyphLayout;

    /*
    Flags
     */
    private boolean debugFlag = false;          //Tells screen to draw debug wireframe
    private boolean textureFlag = true;         //Tells screen to draw textures
    private boolean stopSpawningFlag = false;   //Tells screen to stop creating more asteroids and collectibles
    private boolean endLevelFlag = false;       //Tells screen that the level is complete and to give the next stage menu
    private boolean screenOnFlag = true;        //Tells screen that the conversation box is on

    //
    private final SpaceHops spaceHops;
    AdventureLevelOne(SpaceHops spaceHops) { this.spaceHops = spaceHops; }

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
        spaceCraft.updatePosition(WORLD_WIDTH/4, WORLD_HEIGHT/2);

        //Earth and Moon background object
        earth = new Planet(150,150,100, earthTexture);
        earth.createMoon(20,moonTexture);

        pauseMenu = new PauseMenu(spaceHops);
        pauseMenu.createNextLevelButton(1);

        //Player UI
        progressBar = new ProgressBar(WORLD_WIDTH,WORLD_HEIGHT,progressBarFrameTexture,progressBarTexture);
        progressBar.setGoal(GOAL);
        conversationBox = new ConversationBox(WORLD_WIDTH, WORLD_HEIGHT, communicationFrameTexture, profileTexture);
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
        TextureAtlas levelAtlas = spaceHops.getAssetManager().get("level_one_assets.atlas");
        TextureAtlas shipAtlas = spaceHops.getAssetManager().get("ship_assets.atlas");
        TextureAtlas profileAtlas = spaceHops.getAssetManager().get("profile_assets.atlas");
        TextureAtlas uiAtlas = spaceHops.getAssetManager().get("ui_assets.atlas");

        //AsteroidTextures
        topAsteroidTexture = levelAtlas.findRegion("TowerUp");
        bottomAsteroidTexture = levelAtlas.findRegion("TowerDown");

        //Spaceship
        spaceCraftTexture = shipAtlas.findRegion("SpaceshipPack");

        //Background
        earthTexture = levelAtlas.findRegion("Earth");
        moonTexture = levelAtlas.findRegion("Moon");

        //Collectible
        collectibleTexture = uiAtlas.findRegion("CollectiblePack");

        //Progress Bar
        progressBarTexture = uiAtlas.findRegion("Progress");
        progressBarFrameTexture = uiAtlas.findRegion("ProgressBar");

        //Communication Frame
        communicationFrameTexture = uiAtlas.findRegion("CommunicationFrame");
        profileTexture = profileAtlas.findRegion("Profile_Pack");
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
        for (Asteroids asteroid : asteroids) { asteroid.drawDebug(shapeRendererEnemy); } //Draws all the asteroids
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
        earth.drawDebug(shapeRendererBackground);                                       //Draws the earth and all of it's moons
        if(part != PART.PartOne) {progressBar.drawDebug(shapeRendererBackground);}   //Draws the progressbar if not in stage one of the level
        conversationBox.drawDebug(shapeRendererBackground);                             //Draws the communication frame
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
    Input: Void
    Output: Void
    Purpose: Creates a new asteroid row and adds it to the array
    */
    private void createNewAsteroid(){
        Asteroids newAsteroid = new Asteroids(topAsteroidTexture, bottomAsteroidTexture);
        newAsteroid.setPosition(WORLD_WIDTH + newAsteroid.getRadius());
        asteroids.add(newAsteroid);
    }

    /*
    Input: Void
    Output: Void
    Purpose: Creates a new collectible, first one is off set to match the distancing along with asteroids
    */
    private void createNewCollectible(){
        Collectible newCollectible = new Collectible(collectibleTexture);
        if(asteroids.size == 1) {newCollectible.setPosition(asteroids.first().getX() + newCollectible.getAsteroidRadius() + GAP_BETWEEN_ASTEROID/2);}
        else{newCollectible.setPosition(WORLD_WIDTH + newCollectible.getAsteroidRadius());}
        if(part == PART.PartOne){newCollectible.setCollidingFlag();}
        collectibles.add(newCollectible);
    }

    /*
    Input: Void
    Output: Void
    Purpose: Checks if we need to make either a collectible or asteroid
    */
    private void checkForNewObjectsNeeded(){
        checkIfNewAsteroidIsNeeded();
        checkIfNewCollectibleIsNeeded();
    }

    /*
    Input: Void
    Output: Void
    Purpose: Checks if there is need to create a new collectible is need to be created
    */
    private void checkIfNewCollectibleIsNeeded(){
        //No collectible exits
        if (collectibles.size == 0) {
            createNewCollectible();
        }
        //collectible is distance away
        else {
            Collectible collectible = collectibles.peek();
            if (collectible.getX() < WORLD_WIDTH - GAP_BETWEEN_ASTEROID) {
                createNewCollectible();
            }
        }
    }

    /*
    Input: Void
    Output: Void
    Purpose: Checks if there is need to create a new asteroid if does calls createNewAsteroid()
    */
    private void checkIfNewAsteroidIsNeeded(){
        //If no asteroids on screen exits
        if (asteroids.size == 0) {
            createNewAsteroid();
        }
        //If the distance between the world and the new asteroid is enough
        else {
            Asteroids asteroid = asteroids.peek();
            if (asteroid.getX() < WORLD_WIDTH - GAP_BETWEEN_ASTEROID) {
                createNewAsteroid();
            }
        }
    }

    /*
    Input: Void
    Output: Void
    Purpose: Checks if asteroids or collectibles need ot be destroyed
    */
    private void checkForRemovingObject(){
        removeAsteroidIfPassed();
        removeCollectible();
    }

    /*
    Input: Void
    Output: Void
    Purpose: Gets rid of the collectible from array that went past the screen
    */
    private void removeCollectible(){
        if(collectibles.size > 0){																					//Checks if we have more than 0 flowers
            Collectible firstCollectible = collectibles.first();																//Grabs the first flower
            if(firstCollectible.getX() < - firstCollectible.getRadius()){ collectibles.removeValue(firstCollectible,true); }	//If x is off screen remove from array
        }
    }

    /*
    Input: Void
    Output: Void
    Purpose: Removes asteroids from array if its off the screen
    */
    private void removeAsteroidIfPassed(){
        if(asteroids.size > 0){																					//Checks if we have more than 0 flowers
            Asteroids firstAsteroid = asteroids.first();																//Grabs the first flower
            if(firstAsteroid.getX() < - firstAsteroid.getRadius()){ asteroids.removeValue(firstAsteroid,true); }	//If x is off screen remove from array
        }
    }

    /*
    Input: Delta, timing
    Output: Void
    Purpose: Central function that starts all the other update functions
    */
    private void update(float delta){
        //If we are leaving the screen we get rid of everything in memory
        if(pauseMenu.getDisposeFlag()){dispose();}
        //Creation and destruction of new flowers
        if(!stopSpawningFlag) {checkForNewObjectsNeeded();}
        checkForRemovingObject();

        //Updates status of variables
        updatePart(delta);      //Checks which part of the level we are in and sets off events when we enter new
        //Part of the level
        updateScore();                           //Updates score
        updateCommunicationScreenTime(delta);    //Updates the time that the screen time is on for
        updateSpaceship();                       //Updates the position of the spaceship
        updateAsteroids(delta);                  //Updates the asteroids
        updateCollectibles(delta);
        updatePlanet();                          //Updates the position of the moons
        if(checkForDeathCollision()){ restart();}//Checks for restart
    }

    /*
    Input: Delta, timing
    Output: Void
    Purpose: Checks for what stage of the level we are and sets off appropriate events that correspond
    */
    private void updatePart(float delta){
        //Moves from Part 1 to Part 2, turns on commutation window
        if(part == PART.PartOne && asteroidsPassed == ASTEROIDS_PASSED){
            part = PART.PartTwo;
            screenOnFlag = true;
        }
        //Moves from Part 2 to Part 3, turns on commutation window
        if(part == PART.PartTwo && progressBar.getScore() == 1){
            part = PART.PartThree;
            screenOnFlag = true;
        }
        //Moves from Part 3 to Part 4, turns on commutation window
        if(part == PART.PartThree && progressBar.getScore() == GOAL-1){
            stopSpawningFlag = true;
            part = PART.PartFour;
            screenOnFlag = true;
        }
        if(part == PART.PartFour && progressBar.getScore() == GOAL){
            part = PART.PartFive;
        }
        if(part == PART.PartFive){
            endLevelFlag = true;
            Gdx.input.setInputProcessor(pauseMenu.getNextLevelStage());

        }

        //Tells the screen to turn on and which text output to give
        if(part.equals(PART.PartOne) && screenOnFlag){ conversationBox.update(delta, 0);}
        if(part.equals(PART.PartOne) && !screenOnFlag) {conversationBox.update(delta, 1);}
        if(part.equals(PART.PartTwo) && screenOnFlag && collectibles.first().getX() < WORLD_WIDTH && !collectibles.first().getCollidingFlag()){ conversationBox.update(delta, 0);}
        if(part.equals(PART.PartTwo) && !screenOnFlag) {conversationBox.update(delta, 1);}
        if(part.equals(PART.PartThree) && screenOnFlag){ conversationBox.update(delta,0);}
        if(part.equals(PART.PartThree) && !screenOnFlag) {conversationBox.update(delta, 1);}
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
    Input: Void
    Output: Void
    Purpose: Updates the position of the moons
    */
    private void updatePlanet(){ earth.update(); }

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
    Input: Delta
    Output: Void
    Purpose: Goes through each asteroid in the array and updates the position
    */
    private void updateAsteroids(float delta){ for(Asteroids asteroid : asteroids){ asteroid.update(delta);}}

    /*
    Input: Delta
    Output: Void
    Purpose: Goes through each collectible in the array and updates the position
    */
    private void updateCollectibles(float delta){ for(Collectible collectible : collectibles){ collectible.update(delta, spaceCraft); } }

    /*
    Input: Delta
    Output: Void
    Purpose: Checks what is the amount of asteroids passed and collectibles collected
    */
    private void updateScore(){
        if(!asteroids.isEmpty()) { updateAsteroidScore();}
        if(!collectibles.isEmpty()) { updateCollectibleScore();}
    }

    /*
    Input: Delta
    Output: Void
    Purpose: Checks what is the amount of asteroids passed
    */
    private void updateAsteroidScore(){
        Asteroids asteroid = asteroids.first();
        if (asteroid.getX() <= spaceCraft.getX() && !asteroid.isPointClaimed()) {
            asteroid.markPointClaimed();
            asteroidsPassed++;
        }
    }

    /*
    Input: Delta
    Output: Void
    Purpose: Checks if any collectibles have been collected with
    */
    private void updateCollectibleScore(){
        Collectible collectible = collectibles.first();
        if (collectible.isColliding(spaceCraft) && !collectible.getCollidingFlag()) {
            progressBar.update();
            collectible.setCollidingFlag();
        }
    }

    /*
    Input: Void
    Output: Void
    Purpose: Restart the variables to initial position and removes all flowers from array.
    */
    private void restart(){
        spaceCraft.updatePosition(WORLD_WIDTH/4,WORLD_HEIGHT/2);
        asteroids.clear();
        collectibles.clear();
        asteroidsPassed = 0;
        part = PART.PartOne;
        screenOnFlag = true;
        progressBar.restart();
    }

    /*
    Input: Void
    Output: Void
    Purpose: Checks for if spaceship has hit into any of the flowers
    */
    private boolean checkForDeathCollision(){
        for (Asteroids asteroid : asteroids){ if(asteroid.isColliding(spaceCraft)){return true;}}
        return false;
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
        earth.draw(batch);                  //Draws earth
        drawAsteroid();                     //Draws all asteroids
        drawCollectible();                  //Draws all collectibles
        spaceCraft.draw(batch);             //Draws user
        conversationBox.draw(batch);        //Draws conversation box
        //While not in part one draws the progress bar
        if(part != PART.PartOne) {progressBar.draw(batch, glyphLayout, bitmapFont);}
        //Draws menu if paused or level has ended
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
    Purpose: For every asteroid in the array it call the draw function from that object
    */
    private void drawAsteroid(){ for(Asteroids asteroid : asteroids){ asteroid.draw(batch); } }

    /*
    Input: Void
    Output: Void
    Purpose: For every asteroid in the array it call the draw function from that object
    */
    private void drawCollectible(){ for(Collectible collectible : collectibles){ collectible.draw(batch); } }

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