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

    ImageButton adventureButton;
    ImageButton levelOneButton;
    ImageButton endlessButton;
    ImageButton leftBackButton;
    ImageButton rightBackButton;

    ImageButton buttonArray[];

    String paths[];

    float newX = -WORLD_WIDTH;
    float currentX = 0;

    private float RATE_OF_CHANGE = 10;

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

        showMainButtons();
        showLeftButtons();

        currentBackgroundTexture = new Texture(Gdx.files.internal("MainScreen.png"));

        backButtonUpTexture = new Texture(Gdx.files.internal("MenuUnpressed.png"));
        backButtonDownTexture = new Texture(Gdx.files.internal("MenuPressed.png"));
        rightBackButton = new ImageButton(new TextureRegionDrawable(new TextureRegion(backButtonUpTexture)),
                new TextureRegionDrawable(backButtonDownTexture));
        rightBackButton.setPosition(5*WORLD_WIDTH/4, WORLD_HEIGHT/2, Align.center);
        stage.addActor(rightBackButton);

        rightBackButton.addListener(new ActorGestureListener() {
            @Override
            public void tap(InputEvent event, float x, float y, int count, int button) {
                super.tap(event, x, y, count, button);
                setDestination(1);
                if(!movingFlag && destinationFlag != currentScreenPositionFlag) {
                    setTexture();
                    setDirection();
                    setNewX();
                    setMovingFlag();
                }
            }
        });

        paths = new String[] {"LeftScreen.png", "MainScreen.png", "RightScreen.png"};
        buttonArray = new ImageButton[] {adventureButton, levelOneButton, endlessButton, leftBackButton, rightBackButton};
        showCamera();
        //Sets up the texture with the images
        batch = new SpriteBatch();
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
        adventureDownTexture = new Texture(Gdx.files.internal("AdventurePressed.png"));
        adventureUpTexture = new Texture(Gdx.files.internal("AdventureUnpressed.png"));
        adventureButton = new ImageButton(new TextureRegionDrawable(new TextureRegion(adventureUpTexture)),
                new TextureRegionDrawable(adventureDownTexture));
        adventureButton.setPosition(WORLD_WIDTH/2, WORLD_HEIGHT/6+adventureButton.getHeight() + 2, Align.center);
        stage.addActor(adventureButton);

        adventureButton.addListener(new ActorGestureListener() {
            @Override
            public void tap(InputEvent event, float x, float y, int count, int button) {
                super.tap(event, x, y, count, button);
                setDestination(0);
                if(!movingFlag && destinationFlag != currentScreenPositionFlag) {
                    setTexture();
                    setNewX();
                    setDirection();
                    setMovingFlag();
                }
            }
        });

        endlessDownTexture = new Texture(Gdx.files.internal("EndlessPressed.png"));
        endlessUpTexture = new Texture(Gdx.files.internal("EndlessUnpressed.png"));
        endlessButton = new ImageButton(new TextureRegionDrawable(new TextureRegion(endlessUpTexture)),
                new TextureRegionDrawable(endlessDownTexture));
        endlessButton.setPosition(WORLD_WIDTH/2, WORLD_HEIGHT/6, Align.center);
        stage.addActor(endlessButton);

        endlessButton.addListener(new ActorGestureListener() {
            @Override
            public void tap(InputEvent event, float x, float y, int count, int button) {
                super.tap(event, x, y, count, button);
                setDestination(2);
                if(!movingFlag && destinationFlag != currentScreenPositionFlag) {
                    setTexture();
                    setDirection();
                    setNewX();
                    setMovingFlag();
                }
            }
        });
    }


    void showLeftButtons(){
        backButtonUpTexture = new Texture(Gdx.files.internal("MenuUnpressed.png"));
        backButtonDownTexture = new Texture(Gdx.files.internal("MenuPressed.png"));
        leftBackButton = new ImageButton(new TextureRegionDrawable(new TextureRegion(backButtonUpTexture)),
                new TextureRegionDrawable(backButtonDownTexture));
        leftBackButton.setPosition(-1*WORLD_WIDTH/4, WORLD_HEIGHT/2, Align.center);
        stage.addActor(leftBackButton);

        leftBackButton.addListener(new ActorGestureListener() {
            @Override
            public void tap(InputEvent event, float x, float y, int count, int button) {
                super.tap(event, x, y, count, button);
                setDestination(1);
                if(!movingFlag && destinationFlag != currentScreenPositionFlag) {
                    setTexture();
                    setDirection();
                    setNewX();
                    setMovingFlag();
                }
            }
        });

        levelOneButton = new ImageButton(new TextureRegionDrawable(new TextureRegion(adventureUpTexture)),
                new TextureRegionDrawable(adventureDownTexture));
        levelOneButton.setPosition(-WORLD_WIDTH/2, WORLD_HEIGHT/6+levelOneButton.getHeight() + 2, Align.center);
        stage.addActor(levelOneButton);

        levelOneButton.addListener(new ActorGestureListener() {
            @Override
            public void tap(InputEvent event, float x, float y, int count, int button) {
                super.tap(event, x, y, count, button);
                game.setScreen(new GameScreen(game));
                dispose();
            }
        });
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
