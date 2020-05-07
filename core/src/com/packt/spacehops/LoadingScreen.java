package com.packt.spacehops;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.assets.loaders.BitmapFontLoader;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class LoadingScreen extends ScreenAdapter{
    //Screen Dimensions
    private static final float WORLD_WIDTH = 480;
    private static final float WORLD_HEIGHT = 640;

    //Size of the bar
    private static final float PROGRESS_BAR_WIDTH = 100;
    private static final float PROGRESS_BAR_HEIGHT = 25;

    //Visual objects
    private ShapeRenderer shapeRenderer;
    private Viewport viewport;
    private Camera camera;

    //State of the progress bar
    private float progress = 0;

    //The
    private final SpaceHops spaceHops;

    //Flag
    private int levelSelectFlag;

    /*
    Input: SpaceHops
    Output: Void
    Purpose: Grabs the info from main screen that holds asset manager
    */
    LoadingScreen(SpaceHops spaceHops, int levelSelectFlag) {
        this.spaceHops = spaceHops;
        this.levelSelectFlag = levelSelectFlag;
    }

    /*
    Input: Dimensions
    Output: Void
    Purpose: Resize the screen when window size changes
    */
    @Override
    public void resize(int width, int height) { viewport.update(width, height); }

    /*
    Input: Void
    Output: Void
    Purpose: Set up the the textures and objects
    */
    @Override
    public void show() {
        //Sets up the camera
        camera = new OrthographicCamera();
        camera.position.set(WORLD_WIDTH / 2, WORLD_HEIGHT / 2, 0);
        camera.update();
        viewport = new FitViewport(WORLD_WIDTH, WORLD_HEIGHT, camera);

        //Set up wire frame
        shapeRenderer = new ShapeRenderer();

        loadAssets();

    }

    private void loadAssets(){
        //Loads all the images from the big image
        spaceHops.getAssetManager().load("ui_assets.atlas", TextureAtlas.class);
        if(levelSelectFlag == 0) {
            spaceHops.getAssetManager().load("main_screen_assets.atlas", TextureAtlas.class);
            BitmapFontLoader.BitmapFontParameter bitmapFontParameter = new BitmapFontLoader.BitmapFontParameter();
            bitmapFontParameter.atlasName = "font_assets.atlas";
            //Adds font to manager
            spaceHops.getAssetManager().load("Fonts/ButtonFont.fnt", BitmapFont.class, bitmapFontParameter);
            spaceHops.getAssetManager().load("ui_assets.atlas", TextureAtlas.class);
        }
        else if(levelSelectFlag > 0) {
            spaceHops.getAssetManager().load("ship_assets.atlas", TextureAtlas.class);
            spaceHops.getAssetManager().load("profile_assets.atlas", TextureAtlas.class);
            if(levelSelectFlag == 1){spaceHops.getAssetManager().load("level_one_assets.atlas", TextureAtlas.class);}
            else if(levelSelectFlag == 2){spaceHops.getAssetManager().load("level_two_assets.atlas", TextureAtlas.class);}
            else if(levelSelectFlag == 3){spaceHops.getAssetManager().load("level_three_assets.atlas", TextureAtlas.class);}
        }
    }

    /*
    Input: Delta, timing
    Output: Void
    Purpose: What gets drawn
    */
    @Override
    public void render(float delta) {
        update();       //Update the variables
        clearScreen();  //Make screen black
        draw();         //Draws progress bar
    }

    /*
    Input: Void
    Output: Void
    Purpose: Gets rid of all visuals
    */
    @Override
    public void dispose() { shapeRenderer.dispose(); }


    /*
    Input: Void
    Output: Void
    Purpose: Updates the variable of the progress bar, when the whole thing is load it turn on game screen
    */
    private void update() {
        if (spaceHops.getAssetManager().update()) {
            if (levelSelectFlag == 0) { spaceHops.setScreen(new StartScreen(spaceHops)); }
            else if (levelSelectFlag == 1) { spaceHops.setScreen(new AdventureLevelOne(spaceHops)); }
            else if (levelSelectFlag == 2) { spaceHops.setScreen(new AdventureLevelTwo(spaceHops)); }
            else if (levelSelectFlag == 3) { spaceHops.setScreen(new AdventureLevelThree(spaceHops)); }
        }
        else { progress = spaceHops.getAssetManager().getProgress(); }
    }

    /*
    Input: Void
    Output: Void
    Purpose: Sets screen color
    */
    private void clearScreen() {
        Gdx.gl.glClearColor(Color.BLACK.r, Color.BLACK.g, Color.BLACK.b, Color.BLACK.a);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
    }

    /*
    Input: Void
    Output: Void
    Purpose: Draws the progress
    */
    private void draw() {
        //Sets up the camera
        shapeRenderer.setProjectionMatrix(camera.projection);
        shapeRenderer.setTransformMatrix(camera.view);
        //Sets the shape to be filled in
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        //Sets color
        shapeRenderer.setColor(Color.WHITE);
        //Draws the rectangle
        shapeRenderer.rect(
                (WORLD_WIDTH - PROGRESS_BAR_WIDTH) / 2, (WORLD_HEIGHT/2 - PROGRESS_BAR_HEIGHT / 2),
                progress * PROGRESS_BAR_WIDTH, PROGRESS_BAR_HEIGHT);
        shapeRenderer.end();
    }
}