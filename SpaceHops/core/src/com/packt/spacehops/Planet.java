package com.packt.spacehops;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;

class Moon{
    private final Circle moon;
    private float RADIUS;
    private float x;
    private float y;
    private float yMax;
    private float yMin;
    private float xSlope;
    private float ySlope;
    private boolean behind;

    private Texture moonTexture;

    private boolean goingDown = true;

    Moon(float xMax, float xMin, float yMax, float yMin, float RADIUS, Texture moonTexture){
        this.x = xMax;
        this.y = yMax;
        this.yMax = yMax;
        this.yMin = yMin;

        behind = MathUtils.randomBoolean();

        if(xMax > xMin){xSlope = (xMax - xMin)/(xMax);}
        else{xSlope = (xMax - xMin)/(xMin);}

        ySlope = (yMax - yMin)/(yMax);

        this.RADIUS = RADIUS;

        this.moonTexture = moonTexture;

        moon = new Circle(x, y, RADIUS);
    }

    private void changeDirection(){
        if(y < yMin){
            goingDown = false;
            changeBehind();
        }
        else if(y > yMax){
            goingDown = true;
            changeBehind();
        }
    }

    private void changeBehind(){
        behind = !behind;
    }

    boolean isBehind(){return behind;}

    private void setPosition(){
        if(goingDown){
            x -= xSlope;
            y -= ySlope;
        }
        else {
            x += xSlope;
            y += ySlope;
        }
    }

    void update(){
        changeDirection();
        setPosition();
        moon.setX(x);
        moon.setY(y);
    }

    void draw(SpriteBatch batch) {
        batch.draw(moonTexture, moon.x - RADIUS, moon.y - RADIUS, 2*moon.radius,2*moon.radius);
    }

    void drawDebug(ShapeRenderer shapeRenderer) {
        shapeRenderer.circle(moon.x, moon.y, moon.radius);
    }
}

class Planet{

    private Texture planetTexture;

    private final Circle planet;
    private Array<Moon> moons = new Array<>();

    private float RADIUS;
    private float x;
    private float y;

    Planet(float x, float y, float RADIUS, Texture planetTexture){
        this.x = x;
        this.y = y;
        this.RADIUS = RADIUS;

        this.planetTexture = planetTexture;

        planet = new Circle(x, y, RADIUS);

    }

    void createMoon(float radius, Texture moonTexture){
        float angle = MathUtils.random(45,90);
        float radiusX = RADIUS * MathUtils.cos((angle*MathUtils.PI)/180);
        float radiusY = RADIUS * MathUtils.sin((angle*MathUtils.PI)/180);
        float moonX = radiusX + 2 * radius;
        float moonY = radiusY + 2 * radius;
        if(MathUtils.randomBoolean()){moonX = -moonX;}
        Moon moon = new Moon(x + moonX, x - moonX, y + moonY, y - moonY, radius, moonTexture);
        moons.add(moon);
    }

    void update(){
        for(Moon moon : moons){ moon.update(); }
    }

    void draw(SpriteBatch batch){
        Array<Moon> behindMoons = new Array<>();
        Array<Moon> frontMoons = new Array<>();
        for(Moon moon : moons){
            if(moon.isBehind()){
                behindMoons.add(moon);
            }
            else {
                frontMoons.add(moon);
            }
        }
        for(Moon moon : behindMoons){moon.draw(batch);}
        batch.draw(planetTexture, planet.x-RADIUS, planet.y-RADIUS, 2*planet.radius,2*planet.radius);
        for(Moon moon: frontMoons){moon.draw(batch);}
    }

    /*
    Input: Void
    Output:Void
    Purpose: Draws the wireframe
    */
    void drawDebug(ShapeRenderer shapeRenderer) {
        shapeRenderer.circle(planet.x, planet.y, planet.radius);
        for(Moon moon : moons){ moon.drawDebug(shapeRenderer); }
    }
}