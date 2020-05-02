/*
The Planet.java is composed of two classes the planet class and the moon class.
This class is used as a dynamic background element that has a planet and x moons orbiting it.
The developer has full control over where the planet is, how many moons it has and their sizes.
The orbit is randomly generated.
 */

package com.packt.spacehops;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;

class Planet{

    //Texture of the planet
    private TextureRegion planetTexture;

    //Objects the class holds
    private final Circle planet;
    private Array<Moon> moons = new Array<>();

    //Dimensions of the planet
    private float RADIUS;
    private float x;
    private float y;

    /*
    Input: x, y, radius as dimensions of the circle object, texture image
    Output: Void
    Purpose: Central object that the moons will orbit
    */
    Planet(float x, float y, float RADIUS, TextureRegion planetTexture){
        //Sets dimensions
        this.x = x;
        this.y = y;
        this.RADIUS = RADIUS;
        //Connects texture
        this.planetTexture = planetTexture;
        //Creates the circle object
        planet = new Circle(x, y, RADIUS);
    }

    /*
    Input: Radius for size of circle and texture image of the moon
    Output: Void
    Purpose: Initializes all the variables that are going to be displayed
    */
    void createMoon(float radius, TextureRegion moonTexture){
        //Create a random angle between 45 and 90 so each moon has a different orbit.
        float angle = MathUtils.random(45,90);
        //Calculates the x and y coordinate extensions to match that angle
        float radiusX = RADIUS * MathUtils.cos((angle*MathUtils.PI)/180);
        float radiusY = RADIUS * MathUtils.sin((angle*MathUtils.PI)/180);
        //Sets the start coordinates
        float moonX = radiusX + 2 * radius;
        float moonY = radiusY + 2 * radius;
        //Random chance to move around the left slope or right slope
        if(MathUtils.randomBoolean()){moonX = -moonX;}
        //Creates moon
        Moon moon = new Moon(x + moonX, x - moonX, y + moonY, y - moonY, radius, moonTexture);
        //Adds moon to the array
        moons.add(moon);
    }

    /*
    Input: Void
    Output: Void
    Purpose: Updates the position of all of the moons
    */
    void update(){
        for(Moon moon : moons){ moon.update(); }
    }

    /*
    Input: Void
    Output: Void
    Purpose: Draws the images of moons and planet based on their distance to the camera.
    */
    void draw(SpriteBatch batch){
        //Sets up two arrays ands split the moons between in front of planet or behind planet
        Array<Moon> behindMoons = new Array<>();
        Array<Moon> frontMoons = new Array<>();
        for(Moon moon : moons){
            if(moon.getBehind()){ behindMoons.add(moon);}
            else { frontMoons.add(moon); }
        }
        //Draws the behind moons first, then planet then in front moons
        for(Moon moon : behindMoons){moon.draw(batch);}
        batch.draw(planetTexture, planet.x-RADIUS, planet.y-RADIUS, 2*planet.radius,2*planet.radius);
        for(Moon moon: frontMoons){moon.draw(batch);}
    }

    /*
    Input: Void
    Output:Void
    Purpose: Draws the wireframe of planet and moons
    */
    void drawDebug(ShapeRenderer shapeRenderer) {
        shapeRenderer.circle(planet.x, planet.y, planet.radius);
        for(Moon moon : moons){ moon.drawDebug(shapeRenderer); }
    }
}

class Moon{
    //Dimensions
    private float RADIUS;
    private float x;
    private float y;

    //Bound and movement speeds
    private float yMax;
    private float yMin;
    private float xSlope;
    private float ySlope;

    //Circle object
    private final Circle moon;

    //Image texture
    private TextureRegion moonTexture;

    //Flags
    private boolean goingDownFlag = true;
    private boolean behindFlag;

    /*
    Input: Passes in bounds, radius for size and texture image
    Output: Void
    Purpose: Create a moon object and it's path
    */
    Moon(float xMax, float xMin, float yMax, float yMin, float RADIUS, TextureRegion moonTexture){
        //Dimensions
        this.x = xMax;
        this.y = yMax;
        this.RADIUS = RADIUS;

        //Sets bounds and calculates the speed of movement between them
        this.yMax = yMax;
        this.yMin = yMin;
        if(xMax > xMin){xSlope = (xMax - xMin)/(xMax);}
        else{xSlope = (xMax - xMin)/(xMin);}
        ySlope = (yMax - yMin)/(yMax);

        //Sets planet in front or behind randomly
        behindFlag = MathUtils.randomBoolean();

        //Sets texture
        this.moonTexture = moonTexture;

        //Creates circle object
        moon = new Circle(x, y, RADIUS);
    }

    /*
    Input: Void
    Output: Void
    Purpose: Function that updates the variables
    */
    void update(){
        updateDirection();
        setPosition();
        moon.setX(x);
        moon.setY(y);
    }

    /*
    Input: Void
    Output: Void
    Purpose: Keeps track of where on path the planet is on and if it reaches a bound it changes
    directions
    */
    private void updateDirection(){
        //
        if(y < yMin){
            goingDownFlag = false;
            updateBehind();
        }
        else if(y > yMax){
            goingDownFlag = true;
            updateBehind();
        }
    }

    /*
    Input: Void
    Output: Void
    Purpose: Changes the behind to opposite of itself
    */
    private void updateBehind(){behindFlag = !behindFlag; }

    /*
    Input: Void
    Output: Void
    Purpose: changes the x and y position based on which direction the moon is going
    */
    private void setPosition(){
        //Going down
        if(goingDownFlag){
            x -= xSlope;
            y -= ySlope;
        }
        //Going up
        else {
            x += xSlope;
            y += ySlope;
        }
    }

    /*
    Input: Void
    Output: Void
    Purpose: Returns the behindFlag
    */
    boolean getBehind(){return behindFlag;}

    /*
    Input: Void
    Output: Void
    Purpose: Draws the moon
    */
    void draw(SpriteBatch batch) {
        batch.draw(moonTexture, moon.x - RADIUS, moon.y - RADIUS, 2*moon.radius,2*moon.radius);
    }

    /*
    Input: Void
    Output: Void
    Purpose: Draws the wireframe of the moon
    */
    void drawDebug(ShapeRenderer shapeRenderer) {
        shapeRenderer.circle(moon.x, moon.y, moon.radius);
    }
}