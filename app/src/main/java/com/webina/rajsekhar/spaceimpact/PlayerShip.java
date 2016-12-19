package com.webina.rajsekhar.spaceimpact;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.os.Build;
import android.util.Log;


/**
 * Created by RajSekhar on 10/29/2016.
 */
public class PlayerShip {
    private Bitmap bitmap;
    private int x,y;
    private int speed=0;
    private boolean boosting;
    private final int GRAVITY=-12;
    private int shieldStrength;
    int health=100;

    // Stop ship leaving the screen
    private int maxY;
    private int minY;

    private int screenX;
    private int screenY;
    private int playerSpeed;

    //Limit the bounds of the ship's speed
    private final int MIN_SPEED = 1;
    private final int MAX_SPEED = 20;


    //Collition ditection
    Rect hitBox;

    public PlayerShip(Context context, int screenX, int screenY){

        this.screenX=screenX;
        this.screenY=screenY;
        x=50;
        y=50;
        speed=1;
        bitmap= BitmapFactory.decodeResource(context.getResources(),R.drawable.ship);
        bitmap = Bitmap.createScaledBitmap(bitmap,(int)(screenX*.08),(int)(screenX*.07), true);
        boosting = false;
        maxY = screenY - bitmap.getHeight();
        minY=0;
        shieldStrength = 2;
        hitBox=new Rect(x,y,bitmap.getWidth(),bitmap.getHeight());
    }

    public void setHealth(int health){
        this.health=this.health-health;
    }

    public void update(float joystick_distance, float joystick_angle,int playerSpeed){

        float alt= (float) (Math.sin(Math.toRadians(joystick_angle))*joystick_distance);

        int temp= (int) (alt*(-1)/5);
        y+= (temp);
        if(y<0) y=0;
        if(y>(screenY-bitmap.getWidth())) y=screenY-bitmap.getWidth();


        if(playerSpeed<15){
            if(x>5) x-=3;
        }
        else{
            if(x<100) x+=2;
        }


        // Refresh hit box location
        hitBox.left=x;
        hitBox.top=y;
        hitBox.right = x + bitmap.getWidth();
        hitBox.bottom = y + bitmap.getHeight();
    }

    //Getters
    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBoosting(){
        boosting=true;
    }

    public void stopBoosting(){
        boosting=false;
    }

    public int getSpeed() {
        return speed;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public Rect getHitBox(){
        return hitBox;
    }

    public int getShieldStrength(){
        return shieldStrength;
    }

    public void reduceShieldStrength(){
        shieldStrength --;
    }

}

