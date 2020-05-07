package com.packt.spacehops;

import com.badlogic.gdx.Gdx;
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
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.utils.ActorGestureListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

class StartScreen extends ScreenAdapter {

    //Screen dimensions
    private static final float WORLD_WIDTH = 320;
    private static final float WORLD_HEIGHT = 480;

    //Camera and textures
    private Viewport viewport;			 //The screen where we display things
    private Camera camera;				 //The camera viewing the viewport
    private SpriteBatch batch;			 //Batch that holds all of the textures

    private TextureAtlas screenAtlas;
    private TextureAtlas buttonAtlas;

    private TextureRegion currentBackgroundTexture;
    private TextureRegion newBackgroundTexture;
    private TextureRegion mainScreenUpTexture;
    private TextureRegion mainScreenDownTexture;
    private TextureRegion sideScreenUpTexture;
    private TextureRegion sideScreenDownTexture;
    private TextureRegion backLeftButtonUnpressedTexture;
    private TextureRegion backLeftButtonPressedTexture;
    private TextureRegion backRightButtonUnpressedTexture;
    private TextureRegion backRightButtonPressedTexture;
    private TextureRegion doorUpTexture;
    private TextureRegion doorDownTexture;
    private TextureRegion greenLightTexture;
    private TextureRegion adventureIconTexture;
    private TextureRegion endlessIconTexture;
    private TextureRegion shipyardIconTexture;
    private TextureRegion settingsIconTexture;

    //Stage and the buttons it holds
    private Stage mainStage;
    private Stage shipyardStage;

    private ImageButton adventureButton;
    private ImageButton endlessButton;
    private ImageButton shipyardButton;
    private ImageButton settingsButton;
    private ImageButton adventureLevelOneButton;
    private ImageButton adventureLevelTwoButton;
    private ImageButton adventureLevelThreeButton;
    private ImageButton adventureLevelFourButton;
    private ImageButton adventureLevelFiveButton;
    private ImageButton endlessLevelOneButton;
    private ImageButton endlessLevelTwoButton;
    private ImageButton endlessLevelThreeButton;
    private ImageButton endlessLevelFourButton;
    private ImageButton endlessLevelFiveButton;
    private ImageButton endlessLevelSixButton;
    private ImageButton leftBackButton;
    private ImageButton rightBackButton;

    //Holds all of the horizontal buttons spread across left, main and right screens so we can update
    //their positions
    private ImageButton[] buttonArray;

    //Keeps track of the names of the screens so we can choose which one we want to load in
    private String[] paths;

    //Keeps track of the x value of the current and new backgrounds and we shift between them
    private float newX = -WORLD_WIDTH;
    private float currentX = 0;
    private float doorUpY;
    private float doorDownY;
    private float textMove = 0;

    //Static Variables
    private final static float RATE_OF_CHANGE = 20;         //How fast items move left and right
    private final static float DOOR_RATE_OF_CHANGE = 10;     //How fast doors close and open
    private final static float BUTTON_SPACING_GAP = 6;      //How much space there is between buttons

    /*
    Bitmap and GlyphLayout
    */

    private BitmapFont bitmapFont;
    private GlyphLayout glyphLayout;

    //Game for changing screens
    private final SpaceHops spaceHops;
    //Constructor that keeps the screen info
    StartScreen(SpaceHops spaceHops) { this.spaceHops = spaceHops; }

    //Flags
    private int currentScreenPositionFlag = 1; //1 Center, 0 Left, 2 Right. Where we are now
    private int destinationFlag = 0;           //1 Center, 0 Left, 2 Right. Where we want to go to.
    private boolean directionFlag = false;     //Moving left = false, moving right = true
    private boolean movingFlag = false;        //Is the screen moving now
    private int doorFlag = 0;                  //0 - Nothing happening, 1 closing, 2 opening
    private int locationFlag = 0;             //0 - Main screen, 1 shipyard, 2 setting

    /*
    Input: Void
    Output: Void
    Purpose: Matches the dimensions of the game screen to that of the display screen
    */
    @Override
    public void resize(int width, int height) { viewport.update(width, height); }

    /*
    Input: Void
    Output: Void
    Purpose: Initializes all of the variables that the game will be using
    */
    public void show(){
        //Sets up stage and tells it to make buttons clickable
        mainStage = new Stage(new FitViewport(WORLD_WIDTH,WORLD_HEIGHT));
        shipyardStage = new Stage(new FitViewport(WORLD_WIDTH,WORLD_HEIGHT));
        Gdx.input.setInputProcessor(mainStage); //Gives button power to mainStage

        showCamera();       //Sets up the camera
        showTextures();     //Sets up texture to be used in future objects
        showMainButtons();  //Creates the main screen buttons and their functionality
        showLeftButtons();  //Creates the left screen buttons and their functionality
        showRightButton();  //Creates the right screen buttons and their functionality
        showShipyardButtons();
        showInitialize();   //Initializes extra variables
        showVisibility();

        //BitmapFont and GlyphLayout
        bitmapFont = new BitmapFont();
        bitmapFont = spaceHops.getAssetManager().get("Fonts/ButtonFont.fnt");
        glyphLayout = new GlyphLayout();
    }

    /*
    Input: Void
    Output: Void
    Purpose: Sets up all the textures that will be used for display background and buttons
    */
    private void showTextures(){
        screenAtlas = spaceHops.getAssetManager().get("main_screen_assets.atlas");
        buttonAtlas = spaceHops.getAssetManager().get("ui_assets.atlas");

        //Background texture
        currentBackgroundTexture = screenAtlas.findRegion("Screen_Center");

        //Central Screen Textures
        mainScreenUpTexture = buttonAtlas.findRegion("BigButtonUnpressed");
        mainScreenDownTexture = buttonAtlas.findRegion("BigButtonPressed");

        //Side Screen Textures
        sideScreenUpTexture = buttonAtlas.findRegion("SmallButtonUnpressed");
        sideScreenDownTexture = buttonAtlas.findRegion("SmallButtonPressed");
        backLeftButtonUnpressedTexture = screenAtlas.findRegion("LeftArrowUnpressed");
        backLeftButtonPressedTexture = screenAtlas.findRegion("LeftArrowPressed");
        backRightButtonUnpressedTexture = screenAtlas.findRegion("RightArrowUnpressed");
        backRightButtonPressedTexture = screenAtlas.findRegion("RightArrowPressed");

        greenLightTexture = screenAtlas.findRegion("GreenLight");

        //
        doorUpTexture = screenAtlas.findRegion("DoorUp");
        doorDownTexture = screenAtlas.findRegion("DoorDown");

        TextureRegion[][] icons = new TextureRegion(screenAtlas.findRegion("ButtonSymbols")).split(34, 34);
        adventureIconTexture = icons[0][0];
        endlessIconTexture = icons[0][1];
        shipyardIconTexture = icons[0][2];
        settingsIconTexture = icons[0][3];
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
    Purpose: Sets the buttons, adding in their textures and functionality
    */
    private void showMainButtons(){
        /*
        Set Up
         */

        //Creates an array so that we can make all the buttons in the same format
        ImageButton[] buttons = {adventureButton, endlessButton, shipyardButton, settingsButton};

        //Creates the buttons at equal distance in a row with same background images
        ImageButton[] newButtons = setUpButtons(buttons, WORLD_WIDTH/2, 222, mainScreenUpTexture, mainScreenDownTexture);

        //Realizes the buttons because the copies inside the for loop are local and other wise the
        //global ones stay null
        //And adds them to stage so that we can dispose of them when leaving screen
        adventureButton = newButtons[0];
        mainStage.addActor(adventureButton);
        endlessButton = newButtons[1];
        mainStage.addActor(endlessButton);
        shipyardButton = newButtons[2];
        mainStage.addActor(shipyardButton);
        settingsButton = newButtons[3];
        mainStage.addActor(settingsButton);

        /*
        Listeners
         */

        //Adds listener action that moves the screen to the left
        adventureButton.addListener(new ActorGestureListener() {
            @Override
            public void tap(InputEvent event, float x, float y, int count, int button) {
                super.tap(event, x, y, count, button);
                setDestination(0);
                if(initializeSetUpOfNewBackground()) {setUpNewBackground();}
            }
        });

        //Adds listener action that moves the screen to the right
        endlessButton.addListener(new ActorGestureListener() {
            @Override
            public void tap(InputEvent event, float x, float y, int count, int button) {
                super.tap(event, x, y, count, button);
                setDestination(2);
                if(initializeSetUpOfNewBackground()) {setUpNewBackground();}
            }
        });

        //Adds listener action that moves the screen to the right
        shipyardButton.addListener(new ActorGestureListener() {
            @Override
            public void tap(InputEvent event, float x, float y, int count, int button) {
                super.tap(event, x, y, count, button);
                setLocationFlag(1);
                setClosingDoor();
            }
        });
    }

    /*
    Input: Void
    Output: Void
    Purpose: Sets the buttons, adding in their textures and functionality
    */
    private void showLeftButtons(){

        /*
        Set up
         */

        //Creates the back button that moves us to the central screen
        leftBackButton = new ImageButton(new TextureRegionDrawable(new TextureRegion(backRightButtonUnpressedTexture)),
                new TextureRegionDrawable(backRightButtonPressedTexture));
        leftBackButton.setPosition(-1*WORLD_WIDTH/8 + 3, WORLD_HEIGHT/12, Align.center);
        mainStage.addActor(leftBackButton);

        //Sets up array for button initialization in bulk
        ImageButton[] buttons = {adventureLevelOneButton, adventureLevelTwoButton, adventureLevelThreeButton,
                adventureLevelFourButton, adventureLevelFiveButton};

        //Set up the buttons
        ImageButton[] newButtons = setUpButtons(buttons, -WORLD_WIDTH/2 - 4, 213, sideScreenUpTexture, sideScreenDownTexture);

        //Reinitialize them because the ones in the array aren't global
        //And adds them to stage so that we can dispose of them when leaving screen
        adventureLevelOneButton = newButtons[0];
        mainStage.addActor(adventureLevelOneButton);
        adventureLevelTwoButton = newButtons[1];
        mainStage.addActor(adventureLevelTwoButton);
        adventureLevelThreeButton = newButtons[2];
        mainStage.addActor(adventureLevelThreeButton);
        adventureLevelFourButton = newButtons[3];
        mainStage.addActor(adventureLevelFourButton);
        adventureLevelFiveButton = newButtons[4];
        mainStage.addActor(adventureLevelFiveButton);

        /*
        Listeners
         */

        //Moves the user back to the central screen
        leftBackButton.addListener(new ActorGestureListener() {
            @Override
            public void tap(InputEvent event, float x, float y, int count, int button) {
                super.tap(event, x, y, count, button);
                setDestination(1);
                if(initializeSetUpOfNewBackground()) {setUpNewBackground();}
            }
        });

        //Turns on level one
        adventureLevelOneButton.addListener(new ActorGestureListener() {
            @Override
            public void tap(InputEvent event, float x, float y, int count, int button) {
                super.tap(event, x, y, count, button);
                spaceHops.setScreen(new LoadingScreen(spaceHops,1));
                dispose();
            }
        });

        //Turns on level one
        adventureLevelTwoButton.addListener(new ActorGestureListener() {
            @Override
            public void tap(InputEvent event, float x, float y, int count, int button) {
                super.tap(event, x, y, count, button);
                spaceHops.setScreen(new LoadingScreen(spaceHops ,2));
                dispose();
            }
        });

        //Turns on level one
        adventureLevelThreeButton.addListener(new ActorGestureListener() {
            @Override
            public void tap(InputEvent event, float x, float y, int count, int button) {
                super.tap(event, x, y, count, button);
                spaceHops.setScreen(new LoadingScreen(spaceHops, 3));
                dispose();
            }
        });

    }

    /*
    Input: Void
    Output: Void
    Purpose: Sets the buttons, adding in their textures and functionality
    */
    private void showRightButton(){
        /*
        Set Up
         */

        //Sets up the button that takes us back to the central screen
        rightBackButton = new ImageButton(new TextureRegionDrawable(new TextureRegion(backLeftButtonUnpressedTexture)),
                new TextureRegionDrawable(backLeftButtonPressedTexture));
        rightBackButton.setPosition(9*WORLD_WIDTH/8 - 3, WORLD_HEIGHT/12, Align.center);
        mainStage.addActor(rightBackButton);

        //Sets up array for bulk initialization of buttons
        ImageButton[] buttons = {endlessLevelOneButton, endlessLevelTwoButton, endlessLevelThreeButton,
        endlessLevelFourButton, endlessLevelFiveButton, endlessLevelSixButton};

        //Initializes the button with position and texture
        ImageButton[] newButtons = setUpButtons(buttons, 3*WORLD_WIDTH/2, 213, sideScreenUpTexture, sideScreenDownTexture);

        //Reinitialize the buttons because the ones in the array aren't global
        //And adds them to the stage so they can be disposed of after we leave this screen
        endlessLevelOneButton = newButtons[0];
        mainStage.addActor(endlessLevelOneButton);
        endlessLevelTwoButton = newButtons[1];
        mainStage.addActor(endlessLevelTwoButton);
        endlessLevelThreeButton = newButtons[2];
        mainStage.addActor(endlessLevelThreeButton);
        endlessLevelFourButton = newButtons[3];
        mainStage.addActor(endlessLevelFourButton);
        endlessLevelFiveButton = newButtons[4];
        mainStage.addActor(endlessLevelFiveButton);
        endlessLevelSixButton = newButtons[5];
        mainStage.addActor(endlessLevelSixButton);

        /*
        Listeners
         */

        //Send the screen back to the central screen
        rightBackButton.addListener(new ActorGestureListener() {
            @Override
            public void tap(InputEvent event, float x, float y, int count, int button) {
                super.tap(event, x, y, count, button);
                setDestination(1);
                if(initializeSetUpOfNewBackground()) {setUpNewBackground();}
            }
        });
    }

    /*
    Input: Void
    Output: Void
    Purpose: Sets the buttons, adding in their textures and functionality
    */
    private void showShipyardButtons(){
        /*
        Set Up
         */

        //Sets up the button that takes us back to the central screen
        ImageButton shipyardBackButton = new ImageButton(new TextureRegionDrawable(new TextureRegion(backLeftButtonUnpressedTexture)),
                new TextureRegionDrawable(backLeftButtonPressedTexture));
        shipyardBackButton.setPosition(WORLD_WIDTH/2, WORLD_HEIGHT/4, Align.center);
        shipyardStage.addActor(shipyardBackButton);

        //Send the screen back to the central screen
        shipyardBackButton.addListener(new ActorGestureListener() {
            @Override
            public void tap(InputEvent event, float x, float y, int count, int button) {
                super.tap(event, x, y, count, button);
                setLocationFlag(0);
                setClosingDoor();
            }
        });
    }

    /*
    Input: Void
    Output: Void
    Purpose: Sets the paths array, the button array so that their positions can be updated and the height
    of the door textures
    */
    private void showInitialize(){
        //Keeps track of the images that the new background is going to be attached to
        paths = new String[] {"Screen_Left", "Screen_Center", "Screen_Right"};
        //Keeps track of all of the buttons that exits with in the first three main screens
        buttonArray = new ImageButton[] {adventureButton, endlessButton, settingsButton, shipyardButton,
                leftBackButton, rightBackButton, adventureLevelOneButton, adventureLevelTwoButton, adventureLevelThreeButton,
                adventureLevelFourButton, adventureLevelFiveButton, endlessLevelOneButton, endlessLevelTwoButton,
                endlessLevelThreeButton, endlessLevelFourButton, endlessLevelFiveButton, endlessLevelSixButton};

        //Batch for drawing the textures
        batch = new SpriteBatch();

        doorUpY = WORLD_HEIGHT;                             //Places upperDoor above screen
        doorDownY = 0 - doorDownTexture.getRegionHeight();  //Places down door below screen
    }

    /*
    Input: Array of buttons that we will initializes, the height where the first start from,
        the x where they will be displayed and the up and down textures
    Output: Array of initialized buttons
    Purpose: Sets the buttons, adding in their textures and functionality
    */
    private ImageButton[] setUpButtons(ImageButton[] buttons, float x, float y, TextureRegion upTexture, TextureRegion downTexture){
        for(int i = 0; i < buttons.length; i++){
            //Create the button
            buttons[i] =  new ImageButton(new TextureRegionDrawable(upTexture), new TextureRegionDrawable(downTexture));
            //Calculate the height of the new button
            float newHeight = y - i * buttons[i].getHeight() - i * BUTTON_SPACING_GAP;
            //Position the button
            buttons[i].setPosition(x, newHeight, Align.center);
        }
        return buttons;
    }

    private void showVisibility(){
        ImageButton[] leftButtons = {adventureLevelOneButton, adventureLevelTwoButton, adventureLevelThreeButton,
                adventureLevelFourButton, adventureLevelFiveButton};

        ImageButton[] rightButtons = {endlessLevelOneButton, endlessLevelTwoButton, endlessLevelThreeButton,
                endlessLevelFourButton, endlessLevelFiveButton, endlessLevelSixButton};

        if(spaceHops.getSettings().getAllLevelsBeatFlag()){rightButtons[0].setVisible(true);}
        else {rightButtons[0].setVisible(false);}

        for(int i = 0; i < spaceHops.getSettings().getLevelCompletion().length ; i ++){
            if(spaceHops.getSettings().getLevelCompletion()[i] && i < 4){
                leftButtons[i+1].setVisible(true);
            }
            else if(i < 4){
                leftButtons[i+1].setVisible(false);
            }

            if(spaceHops.getSettings().getLevelCompletion()[i]){
                rightButtons[i+1].setVisible(true);
            }
            else{
                rightButtons[i+1].setVisible(false);
            }
        }
    }

    /*
    Input: Void
    Output: Void
    Purpose: Sets the location to be a new screen, Main, Shipyard or settings
    */
    private void setLocationFlag(int newLocation){ locationFlag = newLocation;}

    /*
    Input: Void
    Output: Void
    Purpose: Tells the screen to behave as the doors are closing
    */
    private void setClosingDoor() {doorFlag = 1;}

    /*
    Input: Void
    Output: Void
    Purpose: Tells the screen to behave as the doors are opening
    */
    private void setOpeningDoor() {doorFlag = 2;}

    /*
    Input: Void
    Output: Void
    Purpose: Tells the screen to behave as the doors are closed, nothing is happening
    */
    private void setStopDoor() {doorFlag = 0;}

    /*
    Input: Void
    Output: Void
    Purpose: Shows shipyard background and buttons
    */
    private void setUpShipyard(){
        currentBackgroundTexture = screenAtlas.findRegion("Shipyard");
        Gdx.input.setInputProcessor(shipyardStage); //Gives button power to mainStage
    }

    /*
    Input: Void
    Output: Void
    Purpose: Shows main screen background and buttons
    */
    private void setMain(){
        currentBackgroundTexture = screenAtlas.findRegion((paths[1]));
        Gdx.input.setInputProcessor(mainStage); //Gives button power to mainStage
    }

    /*
    Input: Void
    Output: Void
    Purpose: Changes the flag from moving to inverse of itself, to tell the screen if we're
        transitioning to a different screen
    */
    private void setMovingFlag() {movingFlag = !movingFlag;}

    /*
    Input: Where the user wants to go to
    Output: Void
    Purpose: Changes what screen the user wants to look at
    */
    private void setDestination(int destination){ destinationFlag = destination;}

    /*
    Input: Void
    Output: Void
    Purpose: Once we are moving we set up all of the variables needed
    */
    private void setUpNewBackground(){
        setNewBackgroundTexture();      //Creates the newBackgroundTexture
        setDirection();                 //Sees which direction we're moving
        setNewX();                      //Sets the potion of the newBackground
        setMovingFlag();                //Allows for the movement of objects
    }

    /*
    Input: Void
    Output: Void
    Purpose: When screen is moving it grabs a new texture based on what the destination is
    */
    private void setNewBackgroundTexture(){newBackgroundTexture = screenAtlas.findRegion(paths[destinationFlag]);}

    /*
    Input: Void
    Output: Void
    Purpose: Moving right 1 > 0 direction is true, moving left direction is false
    */
    private void setDirection(){
        if(currentScreenPositionFlag > destinationFlag){directionFlag = true;}
        else if(currentScreenPositionFlag < destinationFlag) {directionFlag = false;}
    }

    /*
    Input: Void
    Output: Void
    Purpose: If moving right we set newScreen at left side, moving left we set newScreen at right side
    */
    private void setNewX(){
        if(directionFlag){ newX = -WORLD_WIDTH;}
        else{newX = WORLD_WIDTH;}
    }

    /*
    Input: Void
    Output: Boolean, Tells us that we are going to move to new screen
    Purpose: Tell us that we're going to move
    */
    private boolean initializeSetUpOfNewBackground(){return !movingFlag && destinationFlag != currentScreenPositionFlag;}

    /*
    Input: Void
    Output: Void
    Purpose: Once the background reaches its final destination we start to clear the variables
        that were used for the move
    */
    private void reachedPosition() {
        setMovingFlag();                    //Stops things from moving
        //Sets background to that of the newBackGround
        currentBackgroundTexture = screenAtlas.findRegion(paths[destinationFlag]);
        currentScreenPositionFlag = destinationFlag; //Changes the currentPosition to that of the destination
        currentX = 0;   //Places the background at x = 0
    }


    /*
    Input: Delta, time
    Output: Void
    Purpose: Function that updates ever second and is responsible of the update of variables and
        drawing of the textures
    */
    public void render(float delta){
        update();           //Updates variables
        draw();             //Draws textures
        mainStage.act(delta);   //Checks buttons for clicks
    }

    /*
    Input: Void
    Output: Void
    Purpose: Updates the variables to their new positions or state
    */
    private void update(){
        //If reached end goal of movement start to clean up
        if(currentX == WORLD_WIDTH || (currentX == -WORLD_WIDTH)) { reachedPosition(); }
        //If we are moving update the position of buttons and background
        if(movingFlag){
            updateScreenPosition();
            updateButtonPosition();
            updateTextPosition();
        }
        if(doorFlag != 0){
            updateDoorPosition();
            updateDoorState();
        }
    }

    /*
    Input: Void
    Output: Void
    Purpose: Updates the x position of the newBackground and currentBackground based on which direction
        we're moving in
    */
    private void updateScreenPosition(){
        if(directionFlag) {
            currentX += RATE_OF_CHANGE;
            newX += RATE_OF_CHANGE;
        }
        else {
            currentX -= RATE_OF_CHANGE;
            newX -= RATE_OF_CHANGE;
        }
    }

    /*
    Input: Void
    Output: Void
    Purpose: Updates the x position of all of the buttons that are displayed horizontally
    */
    private void updateButtonPosition(){
        if(directionFlag) {
            for(ImageButton button : buttonArray){button.setPosition(button.getX() + RATE_OF_CHANGE, button.getY()); }
        }
        else {
            for(ImageButton button : buttonArray){button.setPosition(button.getX() - RATE_OF_CHANGE, button.getY()); }
        }
    }

    /*
    Input: Void
    Output: Void
    Purpose: Updates the x position of all of the buttons that are displayed horizontally
    */
    private void updateTextPosition(){
        if(directionFlag) { textMove += RATE_OF_CHANGE; }
        else { textMove -= RATE_OF_CHANGE; }
    }


    /*
    Input: Void
    Output: Void
    Purpose: Updates the y position of the doors based on which doorFlag we're in
    */
    private void updateDoorPosition(){
        if(doorFlag == 1) {
            doorDownY += DOOR_RATE_OF_CHANGE;
            doorUpY -= DOOR_RATE_OF_CHANGE;
        }
        else if(doorFlag == 2) {
            doorDownY -= DOOR_RATE_OF_CHANGE;
            doorUpY += DOOR_RATE_OF_CHANGE;
        }
    }

    /*
    Input: Void
    Output: Void
    Purpose: Switches between closing opening and updates the background when switching
    */
    private void updateDoorState(){
        if(doorFlag == 1 && doorUpY == WORLD_HEIGHT/2 || doorDownY == WORLD_HEIGHT/2){
            setOpeningDoor();
            if(locationFlag == 0){ setMain();}
            else if(locationFlag == 1) {setUpShipyard();}
            //else if (locationFlag == 2) {setUpSettings();}
        }
        else if(doorFlag == 2 && doorDownY == WORLD_HEIGHT || doorDownY == 0 - doorDownTexture.getRegionHeight()){ setStopDoor(); }
    }

    /*
    Input: Void
    Output: Void
    Purpose: Central methods that draws everything
     */
    private void draw(){
        //Paints screen black
        clearScreen();
        //Sets up camera
        batch.setProjectionMatrix(camera.projection);
        batch.setTransformMatrix(camera.view);
        batch.begin();
        drawBackground(); //Draws background
        //Draws newBackground if we're moving, otherwise newBackground doesn't exist
        if(movingFlag) {drawMovingBackground();}
        drawGreenLights();
        batch.end();

        //Draws horizontal buttons not part of batch
        if(doorFlag == 0 && locationFlag == 0 || doorFlag == 1 && locationFlag == 1 || doorFlag == 2 && locationFlag == 0) {
            mainStage.draw();
            batch.setProjectionMatrix(camera.projection);
            batch.setTransformMatrix(camera.view);
            batch.begin();
            drawText();
            batch.end();
        }
        else if(doorFlag == 0 && locationFlag == 1 || doorFlag == 1 && locationFlag == 0 || doorFlag == 2 && locationFlag == 1) {shipyardStage.draw();}

        batch.setProjectionMatrix(camera.projection);
        batch.setTransformMatrix(camera.view);
        batch.begin();
        if(doorFlag != 0){drawDoors();}
        batch.end();
    }


    /*
    Input: Void
    Output: Void
    Purpose: Makes screen black
    */
    private void clearScreen() {
        Gdx.gl.glClearColor(Color.BLACK.r, Color.BLACK.g, Color.BLACK.b, Color.BLACK.a); //Sets color to black
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);										 //Sends it to the buffer
    }

    /*
    Input: Void
    Output: Void
    Purpose: Draws the background
    */
    private void drawBackground(){
        batch.draw(currentBackgroundTexture, currentX, 0, WORLD_WIDTH, WORLD_HEIGHT);
    }

    /*
    Input: Void
    Output: Void
    Purpose: Draws the newBackground
    */
    private void drawMovingBackground(){
        batch.draw(newBackgroundTexture, newX, 0, WORLD_WIDTH, WORLD_HEIGHT);
    }

    /*
    Input: Void
    Output: Void
    Purpose: Draws the door textures
    */
    private void drawDoors(){
        batch.draw(doorUpTexture, 0, doorUpY);
        batch.draw(doorDownTexture, 0, doorDownY);
    }

    private void drawGreenLights() {
        for (int i = 0; i < spaceHops.getSettings().getLevelCompletion().length; i++) {
            if (spaceHops.getSettings().getLevelCompletion()[i]) {
                drawGreenLight(202 - i * 31);
            }
        }
    }

    private void drawGreenLight(float y){
        batch.draw(greenLightTexture, -269 + textMove, y);
    }


    /*
    Input: Void
    Output: Void
    Purpose: Draws the text that shows the score
    */
    private void drawText(){
        bitmapFont.getData().setScale(0.4f, 0.5f);
        drawMainText();
        drawAdventureText();
        drawEndlessText();
    }

    private void drawMainText(){
        String[] textArray = new String[] {"Adventure", "Endless", "Shipyard", "Options"};
        TextureRegion[] iconArray = new TextureRegion[]{adventureIconTexture, endlessIconTexture, shipyardIconTexture, settingsIconTexture};
        float newHeight;
        for(int i = 0; i < textArray.length; i++){
            //Draws the names of the buttons
            newHeight = 223 - i * shipyardButton.getHeight() - i * BUTTON_SPACING_GAP;
            setUpText(textArray[i], viewport.getWorldWidth()/2 - 30 + textMove,
                    newHeight + glyphLayout.height/2);
            //Draws the icons on each button
            batch.draw(iconArray[i], viewport.getWorldWidth()/2 - 73 + textMove, newHeight - 18);
        }
    }


    private void drawAdventureText(){
        //Draws the title of the screen
        setUpText("Adventure", -190 + textMove, 315);
        String[] textArray = new String[] {"Moon Orbit", "Wormhole", "Space Warehouse", "Galactic War", "Crash Landing"};
        float newHeight;
        for(int i = 0; i < textArray.length; i++){
            newHeight = 213 - i * adventureLevelOneButton.getHeight() - i * BUTTON_SPACING_GAP;
            setUpText(textArray[i], viewport.getWorldWidth()/2 - 5 * adventureLevelOneButton.getWidth()/12 + textMove - 320,
                    newHeight + glyphLayout.height/2);
        }
    }

    private void drawEndlessText(){
        //Draws the title of the screen
        setUpText("Endless", 450 + textMove, 310);
        String[] textArray = new String[] {"Space Hops", "Moon Orbit", "Wormhole", "Space Warehouse", "Galactic War", "Crash Landing"};
        float newHeight;

        for(int i = 0; i < textArray.length; i++){
            //Draws the name of each level
            newHeight = 213 - i * adventureLevelOneButton.getHeight() - i * BUTTON_SPACING_GAP;
            setUpText(textArray[i], viewport.getWorldWidth()/2 - 5 * adventureLevelOneButton.getWidth()/12 + textMove + 320,
                    newHeight + glyphLayout.height/2);
            //Draw high scores for each level
            setUpText(String.valueOf(spaceHops.getSettings().getLevelHighScore()[i]), viewport.getWorldWidth()/2 + 100 + textMove + 320,
                    newHeight + glyphLayout.height/2);
        }

    }

    private void setUpText(String string, float x, float y){
        glyphLayout.setText(bitmapFont, string);
        bitmapFont.draw(batch, string, x, y);
    }

    /*
    Input: Void
    Output: Void
    Purpose: Destroys everything once we move onto the new screen
    */
    @Override
    public void dispose() {
        mainStage.dispose();
        shipyardStage.dispose();
        spaceHops.getAssetManager().unload("main_screen_assets.atlas");
    }

}
