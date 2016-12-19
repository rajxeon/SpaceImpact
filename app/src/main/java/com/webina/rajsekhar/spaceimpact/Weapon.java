package com.webina.rajsekhar.spaceimpact;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


/**
 * Created by RajSekhar on 11/22/2016.
 */
public class Weapon {
    Context mycontext;
    int screenX,screenY;
    int offset=120;
    float button_radius;
    int X,Y;
    Paint paint,arcPaint;
    private boolean isPressing;
    int max_ammo,ammo_count,max_missile,missile_count,max_weapon3;
    long last_ammo_fired,last_ammo_reloaded;
    long last_missile_fired,last_missile_reloaded;
    long last_weapon3_fired,last_weapon3_reloaded;
    int fire_rate_per_min=20,reload_rate_per_min=5;
    int missile_fire_rate_per_min=5;
    float missile_reload_rate_per_min=.5f;

    int weapon_3_fire_rate_per_min=1,weapon_3_ammo_count;
    float weapon_3_reload_rate_per_min=.1f;

    RectF rect = new RectF();
    private Bitmap bullet,missile_icon,weapon3_icon;
    List<Integer> bulletExit = new ArrayList<Integer>();
    public ArrayList<Bullet> bulletList = new  ArrayList<Bullet>();
    public ArrayList<Missile> missileList = new  ArrayList<Missile>();
    public ArrayList<Laser> laserList = new  ArrayList<Laser>();
    PlayerShip playership;
    public int position;
    Bitmap ammo_img;




    public  Weapon(Context context, int screenX, int screenY,PlayerShip playership,int position,Bitmap ammo_img){
        this.mycontext=context;
        this.screenX=screenX;
        this.screenY=screenY;
        this.position=position;
        max_ammo=500;
        ammo_count=500;

        max_missile=5;
        missile_count=5;

        max_weapon3=5;
        weapon_3_ammo_count=5;

        this.playership=playership;
        this.ammo_img=ammo_img;



        Y= (int) (screenY-button_radius-screenY*.1);

        button_radius= (float) (screenX*.04);
        X= (int) (offset+((position*(button_radius*2.7))));

        last_ammo_fired     = last_missile_fired    =last_weapon3_fired        =System.currentTimeMillis() ;
        last_ammo_reloaded  = last_missile_reloaded =last_weapon3_reloaded     =System.currentTimeMillis() ;

        paint=new Paint();
        paint.setStrokeWidth(4);
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.argb(80,255,255,255));
        paint.setAntiAlias(true);


        arcPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        arcPaint.setStrokeWidth(1);
        arcPaint.setStyle(Paint.Style.FILL);
        arcPaint.setColor(Color.argb(255,49,155,239));

        bullet= BitmapFactory.decodeResource(context.getResources(),R.drawable.bullet);
        bullet = Bitmap.createScaledBitmap(bullet, (int)(button_radius*1.2), (int)(button_radius*1.2), true);

        missile_icon= BitmapFactory.decodeResource(context.getResources(),R.drawable.missile_icon);
        missile_icon = Bitmap.createScaledBitmap(missile_icon, (int)(button_radius*1.2), (int)(button_radius*1.2), true);

        if(position>=3){
            switch (position){
                case 3:
                    //Load laser weapon
                    weapon3_icon= BitmapFactory.decodeResource(context.getResources(),R.drawable.laser_icon);
                    weapon3_icon = Bitmap.createScaledBitmap(weapon3_icon, (int)(button_radius*1.2), (int)(button_radius*1.2), true);
                    break;
                case 4:
                    //Load some other weapon

                    break;
            }
        }
    }


    public  ArrayList<Bullet> getBulletList(){
        return bulletList;
    }

    public  ArrayList<Missile> getMissileList(){
        return missileList;
    }

    public  ArrayList<Laser> getLaserList(){
        return laserList;
    }


    public Bitmap getBulletIconBitmap(){
        return bullet;
    }

    public Bitmap getMissileIconBitmap(){
        return missile_icon;
    }

    public Bitmap getWeapon3IconBitmap(){
        return weapon3_icon;
    }

    public Paint getArcPaint(){
        return arcPaint;
    }

    public float getArcRadius(){
        //Log.i("test", String.valueOf(((ammo_count*360.0)/max_ammo)));
        if(position==1)  return (float) ((ammo_count*360.0)/max_ammo);
        if(position==2)  return (float) ((missile_count*360.0)/max_missile);
        if(position==3)  return (float) ((weapon_3_ammo_count*360.0)/max_weapon3);
        return 0;

    }
    public RectF getRect(){
        rect.set(X-button_radius+4,Y-button_radius+4,X+button_radius-4,Y+button_radius-4);
        //canvas.drawArc(rect, -90, 60, true, arcPaint);
        return rect;
    }

    public void update(){


        for(int i=0; i<bulletList.size();i++){
            Bullet b= bulletList.get(i);

            if(b.getBulletX()<screenX+200){
                b.update();
            }
            else{
                bulletList.remove(i);
            }
            if(b.destroyable){
                //Log.i("test", "destroyable");
                try{
                    bulletList.remove(i);
                }
                catch (Exception e){

                }

            }
        }

        for(int i=0; i<missileList.size();i++){
            Missile m= missileList.get(i);

            if(m.getMissileX()<screenX+300 ){
                m.update();
            }

            else{
                missileList.remove(i);
            }

            if(m.destroyable){
                //Log.i("test", "destroyable");
                try{
                    missileList.remove(i);
                }
                catch (Exception e){

                }

            }
        }

        for(int i=0; i<laserList.size();i++){
            Laser l= laserList.get(i);

            if(!l.destroyable){
                l.update();
            }
            else{
                laserList.remove(i);
            }
        }

        switch (position){
            case 1:
                //Bullet
                if(this.isPressing() && ammo_count>0){
                    long milli_now=System.currentTimeMillis();
                    if((milli_now-last_ammo_fired)>(1000.00/fire_rate_per_min)){
                        //Change the refill color if 20% ammo left
                        if((ammo_count*100)/max_ammo<20){
                            arcPaint.setColor(Color.argb(255,255,85,85));
                        }
                        else{
                            arcPaint.setColor(Color.argb(255,49,155,239));
                        }

                        //Fire a ammo
                        Bullet bullet=new Bullet(mycontext,screenX,screenY,
                                playership.getX()+(playership.getBitmap().getWidth()/2),
                                playership.getY()+(playership.getBitmap().getHeight()/2));
                        bulletList.add(bullet);

                        ammo_count--;
                        //Log.i("test", String.valueOf(ammo_count));
                        last_ammo_fired = System.currentTimeMillis() ;
                    }
                }

                if(!this.isPressing() && ammo_count<max_ammo) {
                    //Reload
                    long milli_now_reload = System.currentTimeMillis();

                    if ((milli_now_reload - last_ammo_reloaded) > (1000.00/reload_rate_per_min)) {
                        //Reload an ammo
                        ammo_count++;
                        //Change the refill color if 20% ammo left
                        if((ammo_count*100)/max_ammo<20){
                            arcPaint.setColor(Color.argb(255,255,85,85));
                        }
                        else{
                            arcPaint.setColor(Color.argb(255,49,155,239));
                        }

                        last_ammo_reloaded = System.currentTimeMillis();
                    }
                }
                break;

            case 2:
                //Missile
                if(this.isPressing() && missile_count>0){
                    long milli_now_missile=System.currentTimeMillis();
                    if((milli_now_missile-last_missile_fired)>(1000.00/missile_fire_rate_per_min)){
                        //Change the refill color if 25% ammo left
                        if((missile_count*100)/max_missile<25){
                            arcPaint.setColor(Color.argb(255,255,85,85));
                        }
                        else{
                            arcPaint.setColor(Color.argb(255,49,155,239));
                        }

                        //Fire a ammo
                        Missile missile=new Missile(mycontext,screenX,screenY,
                                playership.getX()+(playership.getBitmap().getWidth()/2),
                                playership.getY()+(playership.getBitmap().getHeight()/2));
                        missileList.add(missile);

                        missile_count--;
                        //Log.i("test", String.valueOf(ammo_count));
                        last_missile_fired = System.currentTimeMillis() ;
                    }
                }

                if(!this.isPressing() && missile_count<max_missile) {
                    //Reload
                    long milli_now_reload_missile = System.currentTimeMillis();

                    if ((milli_now_reload_missile - last_missile_reloaded) > (1000.00/missile_reload_rate_per_min)) {
                        //Reload an ammo
                        missile_count++;
                        //Change the refill color if 25% ammo left
                        if((missile_count*100)/max_missile<25){
                            arcPaint.setColor(Color.argb(255,255,85,85));
                        }
                        else{
                            arcPaint.setColor(Color.argb(255,49,155,239));
                        }

                        last_missile_reloaded = System.currentTimeMillis();
                    }
                }

                break;

            case 3:
                //Laser
                if(this.isPressing() && weapon_3_ammo_count>0){
                    long milli_now_weapon3=System.currentTimeMillis();
                    if((milli_now_weapon3-last_weapon3_fired)>(1000.00/weapon_3_fire_rate_per_min)){
                        //Change the refill color if 25% ammo left
                        if((weapon_3_ammo_count*100)/max_weapon3<25){
                            arcPaint.setColor(Color.argb(255,255,85,85));
                        }
                        else{
                            arcPaint.setColor(Color.argb(255,49,155,239));
                        }

                        //Fire a ammo
                        Laser laser=new Laser(mycontext,screenX,screenY,
                                playership.getX()+(playership.getBitmap().getWidth()/2),
                                playership.getY()+(playership.getBitmap().getHeight()/2));
                        laserList.add(laser);

                        weapon_3_ammo_count--;
                        //Log.i("test", String.valueOf(ammo_count));
                        last_weapon3_fired = System.currentTimeMillis() ;
                    }
                }

                if(!this.isPressing() && weapon_3_ammo_count<max_weapon3) {
                    //Reload
                    long milli_now_reload_weapon3 = System.currentTimeMillis();

                    if ((milli_now_reload_weapon3 - last_weapon3_reloaded) > (1000.00/weapon_3_reload_rate_per_min)) {
                        //Reload an ammo
                        weapon_3_ammo_count++;
                        //Change the refill color if 25% ammo left
                        if((weapon_3_ammo_count*100)/max_weapon3<25){
                            arcPaint.setColor(Color.argb(255,255,85,85));
                        }
                        else{
                            arcPaint.setColor(Color.argb(255,49,155,239));
                        }

                        last_weapon3_reloaded = System.currentTimeMillis();
                    }
                }

                break;

        }


        if(isPressing) paint.setColor(Color.argb(255,49,155,239));
        else paint.setColor(Color.argb(80,255,255,255));
    }

    public void setIsPressing(boolean a){
        isPressing=a;
    }

    public boolean isPressing(){
        return isPressing;
    }

    public Paint getPaint(){
        return paint;
    }

    public float get_button_radius(){
        return button_radius;
    }

    public int getX(){
        return X;
    }
    public int getY(){
        return Y;
    }




    public class Missile{
        private  int screenX,screenY,shipX,shipY;
        private int missileSpeed=15;
        private int missileX,missileY;
        private int maxY;
        Rect src,dest;
        Bitmap missile_im;
        int frame,max_frame,img_offset=0;
        long last_frame_milli=0;
        boolean destroyable=false;
        Rect hitBox;
        int projectionX,projectionY;

        int level_factor=1;
        int damage=10;




        public Missile(Context context,int screenX, int screenY,int shipX,int shipY){
            mycontext=context;
            this.screenX=screenX;
            this.screenY=screenY;
            this.shipX=shipX;
            this.shipY=shipY;
            frame=0;
            max_frame=5;

            Random ran = new Random();
            int random_num=-3+ran.nextInt(3);
            maxY=random_num;


            missileX=shipX;
            missileY=shipY;

            missile_im=BitmapFactory.decodeResource(mycontext.getResources(),R.drawable.missile);

            //Log.i("test", String.valueOf(ammo_img.getWidth()));
            projectionX= (int) (screenX*.04*1.7);
            projectionY=  (int) (screenY*.04*.7);
            this.hitBox=new Rect(missileX,missileY,missileX+projectionX/max_frame,missileY+projectionY);


        }

        public int getDamage(){
            return damage*level_factor;
        }

        public Rect getHitBox(){
            return  hitBox;
        }

        public Bitmap getMissileImage(){
            return missile_im;
        }

        public Rect getSrc(){
            return src;
        }

        public Rect getDest(){
            return dest;
        }



        public int getMissileX(){
            return missileX;
        }

        public int getMissileY(){
            return missileY;
        }



        public void update(){

            missileX+=missileSpeed;
            //bulletY+=maxY;

            //Update the sprite

            long milli_now = System.currentTimeMillis();

            if ((milli_now - last_frame_milli) > (50)) {
                //Change offset of the image
                img_offset=(frame%max_frame);
                src=new Rect(img_offset*missile_im.getWidth()/5,0,(img_offset+1)*missile_im.getWidth()/5,missile_im.getHeight());
                frame++;
                last_frame_milli = System.currentTimeMillis();
                }

            dest=new Rect(missileX,shipY,missileX+projectionX,shipY+projectionY);

            hitBox.left=missileX;
            hitBox.top=missileY;
            hitBox.right=missileX+projectionX;
            hitBox.bottom=missileY+projectionY;
            //Log.i("test", String.valueOf(destroyable));


        }
    }



    public class Bullet{
        private  int screenX,screenY,shipX,shipY;
        private int bulletSpeed=70;
        private int bulletX,bulletY;
        private int maxY;
        private Rect hitBox;
        boolean destroyable=false;
        int level_factor=1;
        int damage=5;

        

        public Bullet(Context context,int screenX, int screenY,int shipX,int shipY){
            mycontext=context;
            this.screenX=screenX;
            this.screenY=screenY;
            this.shipX=shipX;
            this.shipY=shipY+50;

            Random ran = new Random();
            int random_num=-3+ran.nextInt(3);
            maxY=random_num;


            bulletX=shipX;
            bulletY=shipY;

            this.hitBox=new Rect(bulletX,bulletY,bulletX+ammo_img.getWidth(),bulletY+ammo_img.getHeight());


        }

        public int getDamage(){
            return damage*level_factor;
        }

        public Rect getHitBox(){
            return  hitBox;
        }

        public int getBulletX(){
            return bulletX;
        }

        public int getBulletY(){
            return bulletY;
        }

        public void update(){

            bulletX+=bulletSpeed;
            bulletY+=maxY;

            hitBox.left=bulletX;
            hitBox.top=bulletY;
            hitBox.right=bulletX+ammo_img.getWidth();
            hitBox.bottom=bulletY+ammo_img.getHeight();
        }
    }

    public class Laser{
        private  int screenX,screenY,shipX,shipY;
        private int laserX,laserY;
        private int maxY;
        long last_laser_updated_milli=0;
        int alpha=255;
        boolean destroyable=false;
        Paint laserpaint;
        Bitmap laserBitmap;
        Rect hitBox;
        int level_factor=1;
        int damage=15;



        public Laser(Context context,int screenX, int screenY,int shipX,int shipY){
            mycontext=context;
            this.screenX=screenX;
            this.screenY=screenY;
            this.shipX=shipX;
            this.shipY=shipY+50;
            laserpaint=new Paint();
            laserpaint.setAlpha(alpha);

            laserBitmap=BitmapFactory.decodeResource(context.getResources(),R.drawable.laser);
            laserBitmap=Bitmap.createScaledBitmap(laserBitmap,screenX,(int)(screenY*.03),false);



            laserX=shipX;
            laserY=shipY;

            this.hitBox=new Rect(laserX,laserY,laserX+screenX,laserY+(int)(screenY*.03));


        }

        public int getDamage(){
            return damage*level_factor;
        }

        public Rect getHitBox(){
            return hitBox;
        }

        public Bitmap getLaserBitmap(){
            return laserBitmap;
        }

        public Paint getLaserPaint(){
            return  laserpaint;
        }

        public int getLaserX(){
            return laserX;
        }

        public int getLaserY(){
            return laserY;
        }

        public void update(){

            long milli_now = System.currentTimeMillis();

            if ((milli_now - last_laser_updated_milli) > (40)) {
                //Change offset of the image

                alpha-=25;
                if(alpha<=0) alpha=0;
                laserpaint.setAlpha(alpha);
                last_laser_updated_milli = System.currentTimeMillis();
            }
            if(alpha<=0)destroyable=true;
        }
    }

}


