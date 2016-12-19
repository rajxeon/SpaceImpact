package com.webina.rajsekhar.spaceimpact;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.util.Log;

import java.util.Random;

/**
 * Created by RajSekhar on 11/28/2016.
 */
public class Enemy1 extends Enemy{
    private Bitmap bitmap;
    public int enemyLevel=1,life_factor=5,life;
    public Enemy1(Context context, int screenX, int screenY,int enemySpeed,float yComponent,PlayerShip playership,Bitmap bitmap,
                  int enemyLevel,int life_factor) {
        super(context, screenX, screenY, enemySpeed, playership);


        this.bitmap = bitmap;
        this.yComponent = yComponent;


        posX = screenX + 200;
        Random r = new Random();
        posY = r.nextInt((screenY - bitmap.getHeight() - 50) - bitmap.getHeight()) + bitmap.getHeight();
        this.hitBox=new Rect(posX,posY,posX+bitmap.getWidth(),posY+bitmap.getHeight());
        this.life_factor=life_factor;
        this.life=life_factor*enemyLevel;

    }

    public int getLife_factor(){
        return life_factor;
    }


    public void deductLife(int l){
        life-=l;
    }


    public int getLife(){
        return life;
    }



    public void updateY(){
        if(posY<=0){
            ySign=1;
        }
        if(posY>screenY-bitmap.getHeight()){
            ySign=-1;
        }

    }

    public void update(int playerSpeed){
//      //Log.i("test", String.vlayerSpeedyerSpeed));
        posX -= (playerSpeed/6);
        posX -= enemySpeed;
        posY +=(yComponent*enemySpeed*ySign);

        hitBox.left=posX;
        hitBox.top=posY;
        hitBox.right=posX+bitmap.getWidth();
        hitBox.bottom=posY+bitmap.getHeight();

        //if(posX<-500) destroyable=true;
        updateY();
        //if(life<=0) destroyable=true;
    }

    public Bitmap getBitmap(){
        return bitmap;
    }
}
