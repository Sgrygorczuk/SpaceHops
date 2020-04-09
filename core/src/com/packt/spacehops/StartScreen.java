package com.packt.spacehops;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.utils.ActorGestureListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

class StartScreen extends ScreenAdapter {
    private static final float WORLD_WIDTH = 320;
    private static final float WORLD_HEIGHT = 480;

    private Viewport viewport;			 //The screen where we display things
    private Camera camera;				 //The camera viewing the viewport
    private SpriteBatch batch;			 //Batch that holds all of the textures

    private Stage stage;

    private Texture adventureUpTexture;
    private Texture adventureDownTexture;
    private Texture endlessUpTexture;
    private Texture endlessDownTexture;
    private Texture currentBackgroundTexture;
    private Texture newBackgroundTexture;
    private Texture backButtonUpTexture;
    private Texture backButtonDownTexture;
    private Texture mainScreenUpTexture;
    private Texture mainScreenDownTexture;
    private Texture sideScreenUpTexture;
    private Texture sideScreenDownTexture;
    private Texture sidScreenUnavailableTenure;
    private Texture backButtonTexture;

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

    private ImageButton buttonArray[];

    private String paths[];

    private float newX = -WORLD_WIDTH;
    private float currentX = 0;

    private float RATE_OF_CHANGE = 20;
    private float BUTTON_SPACING_GAP = 4;

    private final Game game;
    StartScreen(Game game) { this.game = game; }

    //Flags
    private int currentScreenPositionFlag = 1; //1 Center, 0 Left, 2 Right
    private int destinationFlag = 0;
    private boolean directionFlag = false;
    private boolean movingFlag = false;

    public void show(){
        stage = new Stage(new FitViewport(WORLD_WIDTH,WORLD_HEIGHT));
        Gdx.input.setInputProcessor(stage);

        showCamera();
        showTextures();
        showMainButtons();
        showLeftButtons();
        showRightButton();

        paths = new String[] {"LeftScreen.png", "MainScreen.png", "RightScreen.png"};
        buttonArray = new ImageButton[] {adventureButton, endlessButton, settingsButton, shipyardButton,
                leftBackButton, rightBackButton, adventureLevelOneButton, adventureLevelTwoButton, adventureLevelThreeButton,
                adventureLevelFourButton, adventureLevelFiveButton, endlessLevelOneButton, endlessLevelTwoButton,
                endlessLevelThreeButton, endlessLevelFourButton, endlessLevelFourButton, endlessLevelFiveButton,
                endlessLevelSixButton};
        batch = new SpriteBatch();
    }

    private void showTextures(){
        currentBackgroundTexture = new Texture(Gdx.files.internal("MainScreen.png"));

        adventureDownTexture = new Texture(Gdx.files.internal("AdventurePressed.png"));
        adventureUpTexture = new Texture(Gdx.files.internal("AdventureUnpressed.png"));

        endlessDownTexture = new Texture(Gdx.files.internal("EndlessPressed.png"));
        endlessUpTexture = new Texture(Gdx.files.internal("EndlessUnpressed.png"));

        backButtonUpTexture = new Texture(Gdx.files.internal("MenuUnpressed.png"));
        backButtonDownTexture = new Texture(Gdx.files.internal("MenuPressed.png"));

        mainScreenUpTexture = new Texture(Gdx.files.internal("ButtonUnpressed.png"));
        mainScreenDownTexture = new Texture(Gdx.files.internal("ButtonPressed.png"));

        sideScreenUpTexture = new Texture(Gdx.files.internal("LevelButtonUnpressed.png"));
        sideScreenDownTexture = new Texture(Gdx.files.internal("LevelButtonPressed.png"));
        sidScreenUnavailableTenure = new Texture(Gdx.files.internal("LevelButtonUnavailable.png"));
        backButtonTexture = new Texture(Gdx.files.internal("BackButton.png"));

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


    void showMainButtons(){

        ImageButton buttons[] = {adventureButton, endlessButton, shipyardButton, settingsButton};

        for(int i = 0; i < buttons.length; i++){
            buttons[i] =  new ImageButton(new TextureRegionDrawable(mainScreenUpTexture), new TextureRegionDrawable(mainScreenDownTexture));
            float height = WORLD_HEIGHT/3 - i * buttons[i].getHeight() - i * BUTTON_SPACING_GAP;
            buttons[i].setPosition(WORLD_WIDTH/2, height, Align.center);
            stage.addActor(buttons[i]);
        }

        adventureButton = buttons[0];
        endlessButton = buttons[1];
        shipyardButton = buttons[2];
        settingsButton = buttons[3];

        adventureButton.addListener(new ActorGestureListener() {
            @Override
            public void tap(InputEvent event, float x, float y, int count, int button) {
                super.tap(event, x, y, count, button);
                setDestination(0);
                if(initializeSetUpOfNewBackground()) {setUpNewBackground();}
            }
        });

        endlessButton.addListener(new ActorGestureListener() {
            @Override
            public void tap(InputEvent event, float x, float y, int count, int button) {
                super.tap(event, x, y, count, button);
                setDestination(2);
                if(initializeSetUpOfNewBackground()) {setUpNewBackground();}
            }
        });

    }


    void showLeftButtons(){
        leftBackButton = new ImageButton(new TextureRegionDrawable(new TextureRegion(backButtonTexture)),
                new TextureRegionDrawable(backButtonTexture));
        leftBackButton.setPosition(-1*WORLD_WIDTH/8, WORLD_HEIGHT/4, Align.center);
        stage.addActor(leftBackButton);

        leftBackButton.addListener(new ActorGestureListener() {
            @Override
            public void tap(InputEvent event, float x, float y, int count, int button) {
                super.tap(event, x, y, count, button);
                setDestination(1);
                if(initializeSetUpOfNewBackground()) {setUpNewBackground();}
            }
        });

        ImageButton buttons[] = {adventureLevelOneButton, adventureLevelTwoButton, adventureLevelThreeButton,
                adventureLevelFourButton, adventureLevelFiveButton};

        for(int i = 0; i < buttons.length; i++){
            buttons[i] =  new ImageButton(new TextureRegionDrawable(sideScreenUpTexture), new TextureRegionDrawable(sideScreenDownTexture));
            float height = WORLD_HEIGHT/3 - i * buttons[i].getHeight() - i * BUTTON_SPACING_GAP;
            buttons[i].setPosition(-WORLD_WIDTH/2, height, Align.center);
            stage.addActor(buttons[i]);
        }

        adventureLevelOneButton = buttons[0];
        adventureLevelTwoButton = buttons[1];
        adventureLevelThreeButton = buttons[2];
        adventureLevelFourButton = buttons[3];
        adventureLevelFiveButton = buttons[4];

        adventureLevelOneButton.addListener(new ActorGestureListener() {
            @Override
            public void tap(InputEvent event, float x, float y, int count, int button) {
                super.tap(event, x, y, count, button);
                game.setScreen(new GameScreen(game));
                dispose();
            }
        });
    }

    private void showRightButton(){
        rightBackButton = new ImageButton(new TextureRegionDrawable(new TextureRegion(backButtonTexture)),
                new TextureRegionDrawable(backButtonTexture));
        rightBackButton.setPosition(9*WORLD_WIDTH/8, WORLD_HEIGHT/4, Align.center);
        stage.addActor(rightBackButton);

        rightBackButton.addListener(new ActorGestureListener() {
            @Override
            public void tap(InputEvent event, float x, float y, int count, int button) {
                super.tap(event, x, y, count, button);
                setDestination(1);
                if(initializeSetUpOfNewBackground()) {setUpNewBackground();}
            }
        });

        ImageButton buttons[] = {endlessLevelOneButton, endlessLevelTwoButton, endlessLevelThreeButton,
        endlessLevelFourButton, endlessLevelFiveButton, endlessLevelSixButton};

        for(int i = 0; i < buttons.length; i++){
            buttons[i] =  new ImageButton(new TextureRegionDrawable(sideScreenUpTexture), new TextureRegionDrawable(sideScreenDownTexture));
            float height = WORLD_HEIGHT/3 - i * buttons[i].getHeight() - i * BUTTON_SPACING_GAP;
            buttons[i].setPosition(3*WORLD_WIDTH/2, height, Align.center);
            stage.addActor(buttons[i]);
        }

        endlessLevelOneButton = buttons[0];
        endlessLevelTwoButton = buttons[1];
        endlessLevelThreeButton = buttons[2];
        endlessLevelFourButton = buttons[3];
        endlessLevelFiveButton = buttons[4];
        endlessLevelSixButton = buttons[5];
    }

    void setMovingFlag() {movingFlag = !movingFlag;}

    void setDestination(int destination){ destinationFlag = destination;}

    void setTexture(){newBackgroundTexture = new Texture(Gdx.files.internal(paths[destinationFlag]));}

    void setDirection(){
        if(currentScreenPositionFlag > destinationFlag){directionFlag = true;}
        else if(currentScreenPositionFlag < destinationFlag) {directionFlag = false;}
    }

    void setNewX(){
        if(directionFlag){ newX = -WORLD_WIDTH;}
        else{newX = WORLD_WIDTH;}
    }

    boolean initializeSetUpOfNewBackground(){return !movingFlag && destinationFlag != currentScreenPositionFlag;}

    void setUpNewBackground(){
        setTexture();
        setDirection();
        setNewX();
        setMovingFlag();
    }

    void reachedPosition() {
        setMovingFlag();
        currentBackgroundTexture.dispose();
        newBackgroundTexture.dispose();
        currentBackgroundTexture = new Texture(Gdx.files.internal(paths[destinationFlag]));
        currentScreenPositionFlag = destinationFlag;
        currentX = 0;
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
    }

    public void render(float delta){
        update();

        draw();

        stage.act(delta);
    }

    void update(){
        if(currentX == WORLD_WIDTH || (currentX == -WORLD_WIDTH)) { reachedPosition(); }
        if(movingFlag){
            updateScreenPosition();
            updateButtonPosition();
        }
    }


    void updateScreenPosition(){
        if(directionFlag) {
            currentX += RATE_OF_CHANGE;
            newX += RATE_OF_CHANGE;
        }
        else {
            currentX -= RATE_OF_CHANGE;
            newX -= RATE_OF_CHANGE;
        }
    }

    void updateButtonPosition(){
        if(directionFlag) {
            for(ImageButton button : buttonArray){button.setPosition(button.getX() + RATE_OF_CHANGE, button.getY()); }
        }
        else {
            for(ImageButton button : buttonArray){button.setPosition(button.getX() - RATE_OF_CHANGE, button.getY()); }
        }
    }


    void draw(){
        clearScreen();

        batch.setProjectionMatrix(camera.projection);
        batch.setTransformMatrix(camera.view);
        batch.begin();
        drawBackground();
        if(movingFlag) {drawMovingBackground();}
        batch.end();

        stage.draw();

    }

    void drawBackground(){
        batch.draw(currentBackgroundTexture, currentX, 0, WORLD_WIDTH, WORLD_HEIGHT);
    }

    void drawMovingBackground(){
        batch.draw(newBackgroundTexture, newX, 0, WORLD_WIDTH, WORLD_HEIGHT);
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


    @Override
    public void dispose() {
        stage.dispose();
        adventureUpTexture.dispose();
        adventureDownTexture.dispose();
        endlessDownTexture.dispose();
        endlessUpTexture.dispose();
    }

}
