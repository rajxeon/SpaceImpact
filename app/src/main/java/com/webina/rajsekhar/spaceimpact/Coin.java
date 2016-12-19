package com.webina.rajsekhar.spaceimpact;

import android.content.Context;
import android.graphics.Rect;

/**
 * Created by RajSekhar on 12/3/2016.
 */
public class Coin {
    int speed=5;
    Context mycontext;
    int screenX,screenY, player_speed, posX, posY,coin_value;
    Rect srcRect,destRect;
    long last_frame_milli,coin_birth_milli;
    int frame,max_frame,img_offset=0;
    int bitmap_width,bitmap_height;
    boolean destroyable=false;
    boolean up_mode=true;
    boolean down_mode=false;
    int coin_speed;
    long coin_down_millis;


    float gravity=2,speedY=10;

    public Coin(Context context, int screenX, int screenY, int player_speed, int posX,int posY,int bitmap_width,int bitmap_height,int enemy_speed){
        this.mycontext=context;
        this.screenX=screenX;
        this.screenY=screenY;
        this.player_speed=player_speed;
        this.posX=posX;
        this.posY=posY;
        this.bitmap_width=bitmap_width;
        this.bitmap_height=bitmap_height;
        this.max_frame=7;
        this.coin_speed=enemy_speed;
        coin_birth_milli=System.currentTimeMillis();
        coin_down_millis=-1;

    }

    public void update(){
        long milli_now = System.currentTimeMillis();

        if ((milli_now - coin_birth_milli) > (250)) {
            down_mode=true;
            up_mode=false;

            coin_down_millis=System.currentTimeMillis();


        }

        if (coin_down_millis!=-1 && (milli_now - coin_down_millis) > (250)) {
            down_mode=false;
            up_mode=false;
        }


        if ((milli_now - last_frame_milli) > (40)) {
            //Change offset of the image
            img_offset=(frame%max_frame);
            srcRect=new Rect(img_offset*bitmap_width/max_frame,0,(img_offset+1)*bitmap_width/max_frame,bitmap_height);
            frame++;
            last_frame_milli = System.currentTimeMillis();

            posX -= (player_speed/6);
            posX -= coin_speed;
            if(up_mode){
                posY -=(speedY);
                speedY=speedY-gravity;
            }
            if(down_mode){
                posY -=(speedY);
                speedY=speedY-gravity;
            }

        }

        destRect=new Rect(posX,posY, (int) (posX+screenX*.03), (int) (posY+screenX*.03));
        if(posX<-200) destroyable=true;
        if(posY>screenY-100) destroyable=true;
    }

    public Rect getSrcRect(){
        return srcRect;
    }

    public Rect getDestRect(){
        return destRect;
    }
}
