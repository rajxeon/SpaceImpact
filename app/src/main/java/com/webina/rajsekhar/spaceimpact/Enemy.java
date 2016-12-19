package com.webina.rajsekhar.spaceimpact;

import android.content.Context;
import android.graphics.Rect;

import java.util.Random;

/**
 * Created by RajSekhar on 11/28/2016.
 */
public class Enemy {
    Context myContext;
    int screenX,screenY,posX,posY;
    public int enemySpeed;
    int enemyLevel=1;
    int ySign=1;
    float yComponent=0;
    PlayerShip playership;
    boolean destroyable=false;
    Rect hitBox;
    public Enemy(Context context,int screenX,int screenY,int enemySpeed,PlayerShip playership){
        this.myContext=context;
        this.screenX=screenX;
        this.screenY=screenY;
        this.playership=playership;
        this.enemySpeed=enemySpeed;
    }

    public Rect getHitBox(){
        return hitBox;
    }


    public int getposX(){
        return  posX;
    }

    public int getposY(){
        return  posY;
    }

    public void update(){
        posX+=(enemyLevel*enemySpeed);
    }
}
