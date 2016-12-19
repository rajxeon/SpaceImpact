package com.webina.rajsekhar.spaceimpact;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.util.Log;

/**
 * Created by RajSekhar on 12/9/2016.
 */
public class Health {
    Context mycontext;
    int screenX,screenY, posX, posY,full_health,_currentHealth;
    Rect backSrcRect,backDestRect,healthSrcRect,healthDestRect;

    Bitmap bitmap_back,bitmap_front_full,bitmap_front_low;
    public Health(Context context, int screenX, int screenY,int full_health){
        this.mycontext=context;
        this.screenX=screenX;
        this.screenY=screenY;
        this.full_health=full_health;
        bitmap_back= BitmapFactory.decodeResource(context.getResources(),R.drawable.empty_lifebar);

        bitmap_front_full= BitmapFactory.decodeResource(context.getResources(),R.drawable.full_health);
        bitmap_front_low= BitmapFactory.decodeResource(context.getResources(),R.drawable.low_health);


        //Bar will be 15% of screen width
        int left= (int) (screenX-(screenX*.15)-20);
        posX=left;
        posY=5;
        int right= (int) (left+screenX*.15);
        backDestRect=new Rect(left,posY,right, (int) (screenX*.025));
        backSrcRect=new Rect(0,0,bitmap_back.getWidth(),bitmap_back.getHeight());
        healthSrcRect=new Rect(0,0, (int) (bitmap_front_full.getWidth()),bitmap_front_full.getHeight());
       // healthSrcRect=backSrcRect;

    }

    public int get_currentHealth(){
        return _currentHealth;
    }

    public Bitmap getHealthBar(){
        if(_currentHealth>=20)
            return bitmap_front_full;
        else
            return bitmap_front_low;
    }

    public Rect getHealthSrcRect(){
        return healthSrcRect;
    }

    public void update(int currentHealth){
        this._currentHealth=currentHealth;
        float percent=(currentHealth*100)/full_health;
       // Log.i("test", String.valueOf(bitmap_front_full.getWidth()*percent/100));

        healthDestRect=new Rect(posX,posY,(int) (posX+screenX*.15*percent/100), (int) (screenX*.025));

    }

}
