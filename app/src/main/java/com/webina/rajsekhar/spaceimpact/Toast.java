package com.webina.rajsekhar.spaceimpact;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

/**
 * Created by RajSekhar on 12/6/2016.
 */
public class Toast {

    Context myContext;
    int screenX,  screenY,  posX,  posY;
    Paint paint;
    Bitmap bitmap;
    String text;

    float gravity=-10;
    int opacity=255,size;
    boolean destroyable=false;
    Rect dest,src;
    long animation_start_millis=0;
    int duration=10;

    public Toast(Context context, int screenX, int screenY,
                 int posX, int posY,Bitmap bitmap,String text,float size_in,String color) {

        myContext=context;
        this.screenX=screenX;
        this.screenY=screenY;
        this.posX=posX;
        this.posY=posY;
        this.bitmap=bitmap;
        this.text=text;
        paint=new Paint();
        paint.setTextAlign(Paint.Align.LEFT);
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        this.size= (int) (screenX*size_in);
        paint.setTextSize(size);

        switch (color){
            case "RED":
                paint.setColor(Color.argb(180,255,0,0));
                break;
            default:
                paint.setColor(Color.argb(130,20,220,0));
                break;
        }

        dest=new Rect(posX-size,posY-size+size/4,posX,posY+size/4);

        if(this.bitmap!=null){
            src=new Rect(0,0,this.bitmap.getWidth(),this.bitmap.getHeight());

        }
    }

    public Toast(Context context, int screenX, int screenY,
                 int posX, int posY,Bitmap bitmap,String text,float size_in) {

        //Without paint constructor

        myContext=context;
        this.screenX=screenX;
        this.screenY=screenY;
        this.posX=posX;
        this.posY=posY;
        this.bitmap=bitmap;
        this.text=text;
        paint=new Paint();
        paint.setTextAlign(Paint.Align.LEFT);
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        this.size= (int) (screenX*size_in);


        paint.setTextSize(size);
        paint.setColor(Color.argb(130,20,220,0));
        dest=new Rect(posX-size,posY-size+size/4,posX,posY+size/4);

        if(this.bitmap!=null){
            src=new Rect(0,0,this.bitmap.getWidth(),this.bitmap.getHeight());

        }
    }

    public void setPaint(Paint np){
        paint=np;
    }

    public void setDuration(int dur){
        duration=dur;
    }

    public Rect getSrc(){
        return src;
    }


    public Rect getDest(){
        return dest;
    }

    public int getWidth(){
        return getDest().width()+(text.length()*size);
    }



    public void update(){
        long milli_now = System.currentTimeMillis();

        if ((milli_now - animation_start_millis) > (30)) {

            posY+=gravity/2;
            dest.top=posY-size+size/4;
            dest.bottom=posY+size/4;
            paint.setAlpha(opacity);
            gravity+=.2;
            opacity-=duration;

            if(opacity<=0 || gravity>=0){
                destroyable=true;
            }
            animation_start_millis=System.currentTimeMillis();
        }

    }

    public Bitmap getBitmap(){
        return bitmap;
    }

    public String getText(){
        return text;
    }

    public Paint getPaint(){
        return paint;
    }

    public int getPosX(){
        return posX;
    }

    public int getPosY(){
        return posY;
    }


}
