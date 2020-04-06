/*
The GameScreen class process all of the information that is being displayed to the user
while in an active game environment.
    It creates the wireframe that is used to set up the game.
    Adds textures over the wireframe
    Render all the updates to the objects on screen
 */

package com.packt.spacehops;

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
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.utils.ActorGestureListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

class GameScreen extends ScreenAdapter {

    /*
    Dimensions -- Units the screen has
     */
    private static final float WORLD_WIDTH = 320;
    private static final float WORLD_HEIGHT = 480;

    private static final int ASTEROIDS_PASSED = 1;
    private static final int GOAL = 10;

    /*
    Games States dictate how the game should be behaving, we always start in Playing state here
    */
    private enum LEVEL {LevelOne, LevelTwo, LevelThree, StageFour}
    private LEVEL level = LEVEL.LevelOne;

    private enum PART {PartOne, PartTwo, PartThree, PartFour}
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
    private Texture background;
    private Texture topAsteroidTexture;
    private Texture bottomAsteroidTexture;
    private Texture earthTexture;
    private Texture moonTexture;
    private Texture collectibleTexture;
    private Texture communicationFrameTexture;
    private Texture progressBarTexture;
    private Texture profileTexture;
    private Texture progressBarFrameTexture;
    private Texture playUpTexture;
    private Texture playDownTexture;
    Texture spaceCraftTexture;

    private Stage stageStage;

    /*
    Flappee Bee Object -- Object that deals with Flappee Bee's operations
     */
    private SpaceCraft spaceCraft;

    /*
    Array of Flowers -- Array of flowers that act as our obstacles and the distance between each pair
     */
    private Array<Asteroids> asteroids = new Array<>();
    private Array<Collectible> collectibles = new Array<>();
    private static final float GAP_BETWEEN_ASTEROID = 200;

    private Planet earth;

    private ProgressBar progressBar;

    private ConversationBox conversationBox;
    private boolean screenOn = true;
    private static final float MOVE_TIME = 10F;
    private float moveTimer = MOVE_TIME;
    private static final float CLICK_TIME = 0.1F;
    private float clickTimer = CLICK_TIME;

    /*
    User Info
     */
    private int asteroidsPassed = 0;

    /*
    Bitmap and GlyphLayout
     */
    private BitmapFont bitmapFont;
    private GlyphLayout glyphLayout;

    /*
    Flags
     */
    private boolean debugFlag = false;
    private boolean textureFlag = true;
    private boolean clickFlag = true;
    private boolean pauseFlag = false;

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
        showMenuButton();       //Sets up menu button that brings up menu and pauses game
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
    Purpose: Sets up the button that will pause the game and bring up the menu
    */
    private void showMenuButton(){
        stageStage = new Stage(new FitViewport(WORLD_WIDTH,WORLD_HEIGHT));
        Gdx.input.setInputProcessor(stageStage);

        playDownTexture = new Texture(Gdx.files.internal("AdventurePressed.png"));
        playUpTexture = new Texture(Gdx.files.internal("AdventureUnpressed.png"));
        ImageButton play = new ImageButton(new TextureRegionDrawable(new TextureRegion(playUpTexture)),new TextureRegionDrawable(playDownTexture));
        play.setPosition(WORLD_WIDTH-30, WORLD_HEIGHT-20, Align.center);
        stageStage.addActor(play);

        play.addListener(new ActorGestureListener() {
            @Override
            public void tap(InputEvent event, float x, float y, int count,
                            int button) {
                super.tap(event, x, y, count, button);
                updatePause();
            }
        });
    }

    /*
    Input: Void
    Output: Void
    Purpose: Connects the images to the Texture objects
    */
    private void showTexture(){
        //AsteroidTextures
        topAsteroidTexture = new Texture(Gdx.files.internal("TowerUp.png"));
        bottomAsteroidTexture = new Texture(Gdx.files.internal("TowerDown.png"));

        //Spaceship
        spaceCraftTexture = new Texture(Gdx.files.internal("SpaceshipPack.png"));

        //Background
        earthTexture = new Texture(Gdx.files.internal("Earth.png"));
        moonTexture = new Texture(Gdx.files.internal("Moon.png"));

        //Collectible
        collectibleTexture = new Texture(Gdx.files.internal("CollectiblePack.png"));

        //Progress Bar
        progressBarTexture = new Texture(Gdx.files.internal("Progress.png"));
        progressBarFrameTexture = new Texture(Gdx.files.internal("ProgressBar.png"));

        //Communication Frame
        communicationFrameTexture = new Texture(Gdx.files.internal("CommunicationFrame.png"));
        profileTexture = new Texture(Gdx.files.internal("Profile_Pack.png"));
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
        clearScreen();	                 //Wipes screen
        setTextureMode();               //Checks if the user changed the status of texturesOnFlag
        if(textureFlag) {draw();}			//Draws the textures

        setDebugMode();                 //Checks if user changed the status of the debugModeFlag
        if(debugFlag) {                 //If debugMode is on ShapeRender will drawing lines
            renderEnemy();              //Draws Enemy Wire Frame
            renderUser();               //Draws User Wire Frame
            renderBackground();         //Draws Background Wire Frame
            renderCollectible();        //Draws Collectible Wire Frame
        }
        stageStage.draw();              //Draws the menu Button - Change name
        if(!pauseFlag){update(delta);}  //Updates the variables of all object if the game is not paused
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
        Asteroids newAsteroid = new Asteroids(topAsteroidTexture, bottomAsteroidTexture, collectibleTexture);
        newAsteroid.setPosition(WORLD_WIDTH + newAsteroid.getRadius());
        asteroids.add(newAsteroid);
    }


    private void createNewCollectible(){
        Collectible newCollectible = new Collectible(collectibleTexture);
        if(collectibles.isEmpty()){newCollectible.setPosition(WORLD_WIDTH + newCollectible.getRadius());}
        else{newCollectible.setPosition(WORLD_WIDTH + newCollectible.getRadius());}
        collectibles.add(newCollectible);
    }

    private void checkForNewObjectsNeeded(){
        checkIfNewAsteroidIsNeeded();
        if(part != PART.PartOne) { checkIfNewCollectibleIsNeeded();}
    }

    private void checkIfNewCollectibleIsNeeded(){
        if (collectibles.size == 0) {
            createNewCollectible();
        }
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


    private void checkForRemovingObject(){
        removeAsteroidIfPassed();
        removeCollectible();
    }

    private void removeCollectible(){
        if(collectibles.size > 0){																					//Checks if we have more than 0 flowers
            Collectible firstCollectible = collectibles.first();																//Grabs the first flower
            if(firstCollectible.getX() < - firstCollectible.getRadius()){ collectibles.removeValue(firstCollectible,true); }	//If x is off screen remove from array
        }
    }

    /*
    Input: Void
    Output: Void
    Purpose: Removes flower from array if its off the screen
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
        //Creation and destruction of new flowers
        checkForNewObjectsNeeded();
        checkForRemovingObject();

        //Updates status of variables
        updatePart(delta);      //Checks which part of the level we are in and sets off events when we enter new
                                //Part of the level
        updateScore();                           //Updates score
        updateCommunicationScreenTime(delta);    //Updates the time that the screen time is on for
        updateClick(delta);                      //Updates the users ability to click jump button
        updateSpaceship(delta);                  //Updates the position of the spaceship
        updateAsteroids(delta);                  //Updates the asteroids
        updateCollectibles(delta);
        updatePlanet();                          //Updates the position of the moons
        if(checkForDeathCollision()){ restart();}   //Checks for restart
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
            asteroids.get(1).markPointClaimed();
            screenOn = true;
        }
        //Moves from Part 2 to Part 3, turns on commutation window
        if(part == PART.PartTwo && progressBar.getScore() == 1){
            part = PART.PartThree;
            screenOn = true;
        }
        //Moves from Part 3 to Part 4, turns on commutation window
        if(part == PART.PartThree && progressBar.getScore() == GOAL){
            part = PART.PartFour;
            screenOn = true;
        }

        //Tells the screen to turn on and which text output to give
        if(part.equals(PART.PartOne) && screenOn){ conversationBox.update(delta, 0);}
        if(part.equals(PART.PartOne) && !screenOn) {conversationBox.update(delta, 1);}
        if(part.equals(PART.PartTwo) && screenOn){ conversationBox.update(delta, 0);}
        if(part.equals(PART.PartTwo) && !screenOn) {conversationBox.update(delta, 1);}
        if(part.equals(PART.PartThree) && screenOn){ conversationBox.update(delta,0);}
        if(part.equals(PART.PartThree) && !screenOn) {conversationBox.update(delta, 1);}
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
            screenOn = false;
        }
    }

    /*
    Input: Delta, timing
    Output: Void
    Purpose: Counts down until the user can click jump - MAY GET RID OFF
    */
    private void updateClick(float delta){
        clickTimer -= delta;
        if (clickTimer <= 0) {
            clickTimer = CLICK_TIME;
            clickFlag = true;
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
    private void updateSpaceship(float delta){
        spaceCraft.update();
        if(Gdx.input.isKeyJustPressed(Input.Keys.SPACE) && clickFlag) {
            spaceCraft.flyUp();
            clickFlag = false;
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
    Purpose: Goes through each flower in the array and updates the position
    */
    private void updateAsteroids(float delta){ for(Asteroids asteroid : asteroids){ asteroid.update(delta);}}

    private void updateCollectibles(float delta){ for(Collectible collectible : collectibles){ collectible.update(delta, spaceCraft);}}

    /*
    Input: Delta
    Output: Void
    Purpose:
    */
    private void updateScore(){
        Asteroids asteroid = asteroids.first();
        if(asteroid.getX() <= spaceCraft.getX() && !asteroid.isPointClaimed()) {
            asteroidsPassed++;
            if (part != PART.PartOne){progressBar.update();}
        }
    }

    /*
    Input: Delta
    Output: Void
    Purpose: Pauses the game
    */
    private void updatePause(){ pauseFlag = !pauseFlag; }

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
        screenOn = false;
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
        earth.draw(batch);
        //batch.draw(background, 0 ,0);
        drawAsteroid();
        drawCollectible();
        spaceCraft.draw(batch);
        conversationBox.draw(batch);
        if(part != PART.PartOne) {progressBar.draw(batch, glyphLayout, bitmapFont);}
        batch.end();
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
}