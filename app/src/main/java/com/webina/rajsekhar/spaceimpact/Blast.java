package com.webina.rajsekhar.spaceimpact;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Rect;

/**
 * Created by RajSekhar on 11/30/2016.
 */
public class Blast {

    Context context;
    int posX, posY,  frame_interval,blast_size,blast_size_factor;
    Bitmap bitmap;
    private Rect srcRect;
    private Rect destRect;
    long last_frame_milli=0;
    int frame,max_frame,img_offset=0;
    public boolean destroyable=false;


    public Blast(Context context, int posX, int posy, int frame_count, int frame_interval,int screenX,float blast_size_factor,Bitmap bitmap){
        this.posX=posX;
        this.posY=posy;
        this.max_frame=frame_count;
        this.frame_interval=frame_interval;
        this.blast_size_factor=1;
        blast_size= (int) (screenX*.1*blast_size_factor);
        this.bitmap=bitmap;


    }

    public  void update(){
        long milli_now = System.currentTimeMillis();

        if ((milli_now - last_frame_milli) > (50)) {
            //Change offset of the image
            img_offset=(frame%max_frame);
            srcRect=new Rect(img_offset*bitmap.getWidth()/max_frame,0,(img_offset+1)*bitmap.getWidth()/max_frame,bitmap.getHeight());
            frame++;
            last_frame_milli = System.currentTimeMillis();
        }

        destRect=new Rect(posX,posY,posX+(int)(blast_size),posY+(int)(blast_size));
        if(frame==max_frame) destroyable=true;
    }

    public Rect getSrcRect(){
        return srcRect;
    }

    public Rect getDestRect(){
        return destRect;
    }

    public Bitmap getBitmap(){
        return bitmap;
    }
}
