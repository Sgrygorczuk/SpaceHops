package com.packt.spacehops;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.utils.ActorGestureListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.FitViewport;

class StartScreen extends ScreenAdapter {
    private static final float WORLD_WIDTH = 320;
    private static final float WORLD_HEIGHT = 480;

    private Stage stage;

    private Texture backgroundTexture;
    private Texture adventureUpTexture;
    private Texture adventureDownTexture;
    private Texture endlessUpTexture;
    private Texture endlessDownTexture;
    private Texture titleTexture;

    private final Game game;
    StartScreen(Game game) { this.game = game; }

    public void show(){
        stage = new Stage(new FitViewport(WORLD_WIDTH,WORLD_HEIGHT));
        Gdx.input.setInputProcessor(stage);

        backgroundTexture = new Texture(Gdx.files.internal("Space.png"));
        Image background = new Image(backgroundTexture);
        stage.addActor(background);

        titleTexture = new Texture(Gdx.files.internal("Title.png"));
        Image title = new Image(titleTexture);
        title.setPosition(WORLD_WIDTH/2, 3*WORLD_HEIGHT/4,Align.center);
        stage.addActor(title);

        adventureDownTexture = new Texture(Gdx.files.internal("AdventurePressed.png"));
        adventureUpTexture = new Texture(Gdx.files.internal("AdventureUnpressed.png"));
        ImageButton adventure = new ImageButton(new TextureRegionDrawable(new TextureRegion(adventureUpTexture)),
                new TextureRegionDrawable(adventureDownTexture));
        adventure.setPosition(WORLD_WIDTH/2, WORLD_HEIGHT/6+adventure.getHeight() + 2, Align.center);
        stage.addActor(adventure);

        endlessDownTexture = new Texture(Gdx.files.internal("EndlessPressed.png"));
        endlessUpTexture = new Texture(Gdx.files.internal("EndlessUnpressed.png"));
        ImageButton endless = new ImageButton(new TextureRegionDrawable(new TextureRegion(endlessUpTexture)),
                new TextureRegionDrawable(endlessDownTexture));
        endless.setPosition(WORLD_WIDTH/2, WORLD_HEIGHT/6, Align.center);
        stage.addActor(endless);

        adventure.addListener(new ActorGestureListener() {
            @Override
            public void tap(InputEvent event, float x, float y, int count,
                            int button) {
                super.tap(event, x, y, count, button);
                game.setScreen(new GameScreen(game));
                dispose();
            }
        });
    }

    public void resize(int width, int height){ stage.getViewport().update(width,height,true);}

    public void render(float delta){
        stage.act(delta);
        stage.draw();
    }

    @Override
    public void dispose() {
        stage.dispose();
        backgroundTexture.dispose();
        adventureUpTexture.dispose();
        adventureDownTexture.dispose();
        endlessDownTexture.dispose();
        endlessUpTexture.dispose();
        titleTexture.dispose();
    }

}
