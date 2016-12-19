package com.webina.rajsekhar.spaceimpact;

import android.content.Context;
import android.graphics.Rect;

/**
 * Created by RajSekhar on 12/7/2016.
 */
public class Gem {
    int speed=5;
    Context mycontext;
    int screenX,screenY, player_speed, posX, posY;
    Rect srcRect,destRect;
    long last_frame_milli,jem_birth_milli;
    int frame,max_frame,img_offset=0;
    int bitmap_width,bitmap_height;
    boolean destroyable=false;
    boolean up_mode=false;
    int jem_speed;
    float gravity=2,speedY=0;
    Rect hitBox;

    public Gem(Context context, int screenX, int screenY, int player_speed, int posX,int posY,int bitmap_width,int bitmap_height){
        this.mycontext=context;
        this.screenX=screenX;
        this.screenY=screenY;
        this.player_speed=player_speed;
        this.posX=posX;
        this.posY=posY;
        this.bitmap_width=bitmap_width;
        this.bitmap_height=bitmap_height;
        this.max_frame=8;
        this.jem_speed=speed;
        jem_birth_milli=System.currentTimeMillis();
        hitBox=new Rect();


    }

    public int getPosX(){
        return posX;
    }

    public void update(float playerSpeed){
        long milli_now = System.currentTimeMillis();

        if ((milli_now - last_frame_milli) > (80)) {
            //Change offset of the image
            img_offset=(frame%max_frame);
            srcRect=new Rect(img_offset*bitmap_width/max_frame,0,(img_offset+1)*bitmap_width/max_frame,bitmap_height);
            frame++;
            last_frame_milli = System.currentTimeMillis();


        }

        posX -= (playerSpeed/6);
        posX -= jem_speed;
        destRect=new Rect(posX,posY, (int) (posX+screenX*.04), (int) (posY+screenX*.04));

        hitBox.left=posX;
        hitBox.top=posY;
        hitBox.right=(int) (posX+screenX*.04);
        hitBox.bottom=(int) (posY+screenX*.04);

        if(posX<-200) destroyable=true;





    }

    public Rect getHitBox(){
        return hitBox;
    }

    public Rect getSrcRect(){
        return srcRect;
    }

    public Rect getDestRect(){
        return destRect;
    }

}
