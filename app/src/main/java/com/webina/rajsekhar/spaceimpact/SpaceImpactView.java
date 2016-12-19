package com.webina.rajsekhar.spaceimpact;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.MotionEventCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Random;

/**
 * Created by RajSekhar on 11/19/2016.
 */

public class SpaceImpactView extends SurfaceView implements SurfaceHolder.Callback {
    private int backgroundOrigW;
    private int backgroundOrigH;
    private SpaceImpactThread thread;
    private Context myContext;
    private SurfaceHolder mySurfaceHolder;
    private float scaleW;
    private float scaleH;
    private int screenW = 1;
    private int screenH = 1;
    private Bitmap backgroundImg;
    private Bitmap joystick_back;
    private Bitmap joystick_over;
    private Bitmap joystick_btn;
    private int frame_number=0;

    private boolean running = false;
    private float drawScaleW;
    private float drawScaleH;
    float joystick_angle=0f;
    boolean debug=false;

    private boolean joystick_touched=false,joystick_btn_touched=false;
    private float joystick_btn_distance;
    private int joystick_x,joystick_center_x,joystick_btn_x,joystick_touched_btn_x=-1;
    private int joystick_y,joystick_center_y,joystick_btn_y,joystick_touched_btn_y=-1;
    int numSpecs = 40;
    private Paint paint;
    private boolean weapon_1_touched=false;
    private boolean weapon_2_touched=false;
    private boolean weapon_3_touched=false;
    long last_enemy_birth,last_gem_generated;
    //Game object
    // Make some random space dust

    public ArrayList<SpaceDust> dustList = new  ArrayList<SpaceDust>();
    public ArrayList<Object> enemies=new ArrayList<>();
    public ArrayList<Blast> blastArrayList=new ArrayList<Blast>();
    public ArrayList<Coin> coinArrayList=new ArrayList<Coin>();
    public ArrayList<Toast> toastArrayList=new ArrayList<Toast>();
    public ArrayList<Gem>   gemArrayList=new ArrayList<Gem>();



    private boolean joy_stick_released=false;
    PlayerShip playerShip;
    Weapon wepon1,wepon2,wepon3;
    private Bitmap bullet_im,missile;



    //Enemy bitmap
    Bitmap enemy1,enemy2,enemy3;

    //Gem image
    Bitmap gem_im;

    //Blast bitmap
    Bitmap blast1_im,blast2_im;
    Blast  blast1,blast2;

    int enemyLevel=1;


    //Coins
    Bitmap gold_coin_im,single_coin_im,single_jem_im,single_life_im,single_enemy,single_warning,single_score_im,single_health_im,
            play_pause_im;

    //Toasts


    //Fonts
    Typeface halo;

    //Health
    Health health;

    //game addons
    int gem_count=0,coins_no=0;

    long startFrameTime;
    long timeThisFrame;
    long fps,gameStartTime=0,game_sec_elapsed=0,last_enemy_updated=0;

    int enemyWaveRepeatTime=45,enemyUpdateRepeatTime=60,gemBirthRepeatTime=1000;

    long lastMsgDisplayed=0;
    double score=0;
    long last_score_updated=0;

    public SpaceImpactView(Context context, AttributeSet attrs) {
        super(context, attrs);
        SurfaceHolder holder=getHolder();
        holder.addCallback(this);

        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int screenW = size.x;
        int screenH = size.y;
        last_enemy_birth=0;
        last_gem_generated= System.currentTimeMillis();

        paint=new Paint();


        for (int i = 0; i < numSpecs; i++) {
            // Where will the dust spawn?
            SpaceDust spec = new SpaceDust(screenW, screenH);
            dustList.add(spec);
        }

        bullet_im=BitmapFactory.decodeResource(context.getResources(),R.drawable.bullet_im);
        bullet_im=Bitmap.createScaledBitmap(bullet_im,(int)(screenW*.04),(int)(screenH*.003),true);
        missile  =BitmapFactory.decodeResource(context.getResources(),R.drawable.missile);




        playerShip=new PlayerShip(context,screenW,screenH);
        //Create the Weapon buttons
        wepon1=new Weapon(context,screenW,screenH,playerShip,1,bullet_im);
        wepon2=new Weapon(context,screenW,screenH,playerShip,2,missile);
        wepon3=new Weapon(context,screenW,screenH,playerShip,3,missile);

        //Load enemies bitmaps
        enemy1=BitmapFactory.decodeResource(context.getResources(),R.drawable.enemy1,null);
        enemy1=Bitmap.createScaledBitmap(enemy1,(int)(screenW*.05),(int)(screenH*.06),true);

        enemy2=BitmapFactory.decodeResource(context.getResources(),R.drawable.enemy2,null);
        enemy2=Bitmap.createScaledBitmap(enemy2,(int)(screenW*.05),(int)(screenH*.06),true);

        enemy3=BitmapFactory.decodeResource(context.getResources(),R.drawable.enemy3,null);
        enemy3=Bitmap.createScaledBitmap(enemy3,(int)(screenW*.05),(int)(screenH*.06),true);

        play_pause_im=BitmapFactory.decodeResource(context.getResources(),R.drawable.paly_pause,null);
        play_pause_im=Bitmap.createScaledBitmap(play_pause_im,(int)(screenW*.04),(int)(screenW*.04),true);

        blast1_im=BitmapFactory.decodeResource(context.getResources(),R.drawable.blast1,null);
        blast2_im=BitmapFactory.decodeResource(context.getResources(),R.drawable.blast2,null);

        gold_coin_im=BitmapFactory.decodeResource(context.getResources(),R.drawable.gold_coin,null);
        single_coin_im=BitmapFactory.decodeResource(context.getResources(),R.drawable.single_coin,null);
        single_score_im=BitmapFactory.decodeResource(context.getResources(),R.drawable.single_score,null);
        single_jem_im=BitmapFactory.decodeResource(context.getResources(),R.drawable.single_jem,null);
        single_health_im=BitmapFactory.decodeResource(context.getResources(),R.drawable.single_health,null);
        single_life_im=BitmapFactory.decodeResource(context.getResources(),R.drawable.single_life,null);
        single_enemy=BitmapFactory.decodeResource(context.getResources(),R.drawable.single_enemy,null);
        single_warning=BitmapFactory.decodeResource(context.getResources(),R.drawable.single_warning,null);

        gem_im=BitmapFactory.decodeResource(context.getResources(),R.drawable.gem,null);
        health=new Health(context,screenW,screenH,100);

        //Fonts load
        halo=Typeface.createFromAsset(context.getAssets(),"fonts/nuron.ttf");
        gameStartTime = System.currentTimeMillis();
        last_enemy_updated= System.currentTimeMillis();





        thread=new SpaceImpactThread(holder,context,new Handler(){
            @Override
            public void handleMessage(Message m) {

            }
        });



        setFocusable(true);



    }

    public SpaceImpactThread getThread() {
        return  thread;
    }



    class SpaceImpactThread extends Thread{

        public SpaceImpactThread(SurfaceHolder surfaceHolder, Context context,Handler handler){
            mySurfaceHolder=surfaceHolder;
            //mySurfaceHolder.setFormat(PixelFormat.RGB_565);
            myContext=context;
            backgroundImg = BitmapFactory.decodeResource(context.getResources(), R.drawable.bg);
            joystick_back = BitmapFactory.decodeResource(context.getResources(),R.drawable.joystick_back);
            joystick_over = BitmapFactory.decodeResource(context.getResources(),R.drawable.joystick_over);
            joystick_btn  = BitmapFactory.decodeResource(context.getResources() ,R.drawable.joystick_btn);

            backgroundOrigW = backgroundImg.getWidth();
            backgroundOrigH = backgroundImg.getHeight();

        }

        @Override
        public void run() {

            while (running) {
                startFrameTime=System.currentTimeMillis();
                update();
                control();

                Canvas c = null;
                try {
                    c = mySurfaceHolder.lockCanvas(null);
                    synchronized (mySurfaceHolder) {
                        draw(c);
                    }
                } finally {
                    if (c != null) {
                        mySurfaceHolder.unlockCanvasAndPost(c);
                    }
                }

                timeThisFrame=System.currentTimeMillis()-startFrameTime;
                if(timeThisFrame>1){
                    //Calculate the fps
                    fps=1000/timeThisFrame;
                }
            }
        }

        private float getPlayerSpeed(){
            float power=joystick_btn_distance;
            float angle=joystick_angle;
            float speed;
            angle= (float) Math.toRadians(angle);

            speed= (float) (Math.cos(angle)*power);
            //Log.i("test","speed "+String.valueOf((speed)));
            if(speed<10) speed=10;
            return speed;
        }

        private void update(){

            //Update the player score
            long score_milli_now = System.currentTimeMillis();
            if((score_milli_now-last_score_updated)>100){
                score+=getPlayerSpeed();
                last_score_updated= System.currentTimeMillis();
            }

            //Update the game time
            //gameStartTime
            //generateWave
            long game_milli_now = System.currentTimeMillis();

            if((game_milli_now-gameStartTime)>1000){
                game_sec_elapsed=(game_milli_now-gameStartTime)/1000;



            }

            //Update the enemy after every 1 minute
            if(game_sec_elapsed%enemyUpdateRepeatTime==0){
                //Update enemy only one time per 60 sec
                long milli_now = System.currentTimeMillis();

                if ((milli_now - last_enemy_updated) > (2000) ) {
                    last_enemy_updated= System.currentTimeMillis();
                    enemyLevel+=1;

                    Toast temp=new Toast(myContext,screenW,screenH,screenW/2,screenH/2,single_enemy,"Enemy level x "+enemyLevel,.03f);
                    //Log.i("test", String.valueOf(temp.getWidth()));
                    Toast emy_up=new Toast(myContext,screenW,screenH,((screenW-temp.getWidth())-temp.getWidth()/4),screenH/2,single_enemy,"Enemy level x "+enemyLevel,.03f,"RED");
                    emy_up.setDuration(5);

                    Paint em_paint=new Paint();
                    em_paint.setTextAlign(Paint.Align.LEFT);
                    em_paint.setColor(Color.argb(255, 255, 255, 255));
                    em_paint.setTextSize(25);
                    em_paint.setTypeface(halo);
                    em_paint.setTextSize(.025f*screenW);

                    emy_up.setPaint(em_paint);
                    toastArrayList.add(emy_up);
                }

                //


            }





            if(joystick_btn_touched==false){
                if(joystick_btn_distance>0) joystick_btn_distance-=2;
                if(joystick_angle>0) joystick_angle-=2;
            }

            for (SpaceDust sd : dustList) {
                sd.update((int) getPlayerSpeed());
            }

            //Update playership
            playerShip.update(joystick_btn_distance,joystick_angle, (int) getPlayerSpeed());

            //Update weapon 1
            wepon1.setIsPressing(weapon_1_touched);
            wepon1.update();

            //Update weapon 2
            wepon2.setIsPressing(weapon_2_touched);
            wepon2.update();

            //Update weapon 2
            wepon3.setIsPressing(weapon_3_touched);
            wepon3.update();

            //Update all the enemy in the list
            for(int i=0; i<enemies.size();i++){
                Object enemy= enemies.get(i);
                //Log.i("test",enemy.getClass().toString());

                if(enemy.getClass().toString().indexOf("Enemy1")>=0){

                    if(((Enemy1) enemy).getposX()<0){
                        ((Enemy1) enemy).destroyable=true;
                        int left = playerShip.getY();
                        int top = playerShip.getX();
                        toastArrayList.add(new Toast(myContext,screenW,screenH,top,left,single_coin_im," x -1",.018f,"RED"));
                        if(coins_no>0){
                            coins_no--;
                        }
                    }

                    if(!((Enemy1)enemy).destroyable){
                        ((Enemy1) enemy).update((int) getPlayerSpeed());

                    }
                    else{
                        enemies.remove(i);
                    }
                }

            }

            //Update the blast list
            for(int i=0; i<blastArrayList.size();i++){
                Blast blast= blastArrayList.get(i);
                //Log.i("test",enemy.getClass().toString());

                if(!blast.destroyable){
                    blast.update();
                }
                else{
                    blastArrayList.remove(i);
                }
            }

            //Update the coins list

            //Log.i("test", String.valueOf(coinArrayList.size()));
            for(int i=0; i<coinArrayList.size();i++){
                Coin coin= coinArrayList.get(i);
                //Log.i("test",enemy.getClass().toString());

                if(!coin.destroyable){
                    coin.update();
                }
                else{
                    coinArrayList.remove(i);
                }
            }

            //Update the toast list
            //Log.i("test", String.valueOf(toastArrayList.size()));
            for(int i=0; i<toastArrayList.size();i++){
                Toast toast= toastArrayList.get(i);
                //Log.i("test",enemy.getClass().toString());

                if(!toast.destroyable){
                    toast.update();
                }
                else{
                    toastArrayList.remove(i);
                }
            }

            //Update the jem
            for(int i=0; i<gemArrayList.size();i++){
                Gem gem= gemArrayList.get(i);
                //Log.i("test",enemy.getClass().toString());

                if(!gem.destroyable){
                    gem.update(getPlayerSpeed());
                }
                else{
                    gemArrayList.remove(i);
                }
            }

            //Update the health bar
            health.update(playerShip.health);



            enemyRespawn();
            collitiondetection();




        }

        public void generate_random_blast(int top,int left){
            blastArrayList.add(new Blast(myContext,top,left,10,60,screenW,.9f,blast1_im));
        }

        public void generate_gem(){
            long milli_now = System.currentTimeMillis();

            if ((milli_now - last_gem_generated) > (gemBirthRepeatTime) ) {
                Random r = new Random();
                int yVal=r.nextInt((screenH-50)-10) + 10;

                gemArrayList.add(new Gem(myContext,screenW,screenH, (int) getPlayerSpeed(),screenW+100,yVal,gem_im.getWidth(),gem_im.getHeight()));
                last_gem_generated= System.currentTimeMillis();
            }



        }



        public void collitiondetection(){
            //Update the ammolist

            int top, left;
            for(Object enemy:enemies) {
                //Detect for bullet
                if (wepon1.getBulletList().size() > 0) {

                    ArrayList<Weapon.Bullet> bullets = new ArrayList<Weapon.Bullet>();
                    bullets = wepon1.getBulletList();
                    for (Weapon.Bullet b : bullets) {
                        if (Rect.intersects(b.getHitBox(), ((Enemy1) enemy).getHitBox())) {
                            b.destroyable = true;
                            ((Enemy1) enemy).deductLife(b.getDamage());
                            //Log.i("test", String.valueOf(((Enemy1) enemy).getLife()));
                            if(((Enemy1) enemy).getLife()<0){
                                ((Enemy1) enemy).destroyable=true;



                                left = ((Enemy1) enemy).posY - 20;
                                top = ((Enemy1) enemy).posX - 20;
                                generate_random_blast(top, left);

                                //generate a coin
                                coinArrayList.add(new Coin(myContext,screenW,screenH, (int) getPlayerSpeed(),
                                        top,left,gold_coin_im.getWidth(),gold_coin_im.getHeight(),((Enemy1) enemy).enemySpeed));

                                //Generate the toast message
                                int lf_factor=((Enemy1) enemy).getLife_factor();
                                int coin_count=(lf_factor/5)+1;
                                coins_no+=coin_count;
                               // Log.i("test", String.valueOf(lf_factor));

                                toastArrayList.add(new Toast(myContext,screenW,screenH,top+20,left-20,single_coin_im," x "+coin_count,.018f));


                            }




                        }
                    }
                }



                if (wepon2.getMissileList().size() > 0) {
                    ArrayList<Weapon.Missile> missiles = new ArrayList<Weapon.Missile>();
                    missiles = wepon2.getMissileList();
                    for (Weapon.Missile m : missiles) {
                        if (Rect.intersects(m.getHitBox(), ((Enemy1) enemy).getHitBox())) {
                            //Log.i("test","collation detected");

                            left = ((Enemy1) enemy).posY - 20;
                            top = ((Enemy1) enemy).posX - 20;
                            generate_random_blast(top, left);

                            m.destroyable = true;
                            ((Enemy1) enemy).deductLife(m.getDamage());
                            //Log.i("test", String.valueOf(((Enemy1) enemy).getLife()));
                            if(((Enemy1) enemy).getLife()<0){
                                ((Enemy1) enemy).destroyable=true;

                                left = ((Enemy1) enemy).posY - 20;
                                top = ((Enemy1) enemy).posX - 20;
                                generate_random_blast(top, left);

                                //generate a coin
                                coinArrayList.add(new Coin(myContext,screenW,screenH, (int) getPlayerSpeed(),
                                        top,left,gold_coin_im.getWidth(),gold_coin_im.getHeight(),((Enemy1) enemy).enemySpeed));

                                //Generate the toast message
                                int lf_factor=((Enemy1) enemy).getLife_factor();
                                int coin_count=(lf_factor/5)+1;
                                coins_no+=coin_count;
                                // Log.i("test", String.valueOf(lf_factor));

                                toastArrayList.add(new Toast(myContext,screenW,screenH,top+20,left-20,single_coin_im," x "+coin_count,.018f));

                            }

                        }
                    }
                }
                //Detect for Lasers
                //Log.i("test", String.valueOf(wepon3.getLaserList().size()));

                if (wepon3.getLaserList().size() > 0) {
                    ArrayList<Weapon.Laser> lasers = new ArrayList<Weapon.Laser>();
                    lasers = wepon3.getLaserList();
                    for (Weapon.Laser l : lasers) {
                        if (Rect.intersects(l.getHitBox(), ((Enemy1) enemy).getHitBox())) {

                            ((Enemy1) enemy).deductLife(l.getDamage());
                            //Log.i("test", String.valueOf(((Enemy1) enemy).getLife()));
                            if(((Enemy1) enemy).getLife()<0){
                                ((Enemy1) enemy).destroyable=true;

                                left = ((Enemy1) enemy).posY - 20;
                                top = ((Enemy1) enemy).posX - 20;
                                generate_random_blast(top, left);

                                //generate a coin
                                coinArrayList.add(new Coin(myContext,screenW,screenH, (int) getPlayerSpeed(),
                                        top,left,gold_coin_im.getWidth(),gold_coin_im.getHeight(),((Enemy1) enemy).enemySpeed));

                                //Generate the toast message
                                int lf_factor=((Enemy1) enemy).getLife_factor();
                                int coin_count=(lf_factor/5)+1;
                                coins_no+=coin_count;
                                // Log.i("test", String.valueOf(lf_factor));
                                // Log.i("test", String.valueOf(lf_factor));

                                toastArrayList.add(new Toast(myContext,screenW,screenH,top+20,left-20,single_coin_im," x "+coin_count,.018f));

                            }

                        }
                    }
                }
            }

            //Detect collition of the ship and enemy

            for(Object enemy:enemies) {
                if(((Enemy1) enemy).getposX()<screenW/3){
                    if(Rect.intersects(playerShip.getHitBox(),((Enemy1) enemy).getHitBox())){
                        playerShip.setHealth(((Enemy1) enemy).getLife());
                        ((Enemy1) enemy).destroyable=true;
                        generate_random_blast(((Enemy1) enemy).getposX(),((Enemy1) enemy).getposY());

                        toastArrayList.add(new Toast(myContext,screenW,screenH,((Enemy1) enemy).getposX()+20,((Enemy1) enemy).getposY()-20,single_life_im," - "+((Enemy1) enemy).getLife(),.018f,"RED"));
                        //paint
                    }
                }

            }

            //Detect collition of the ship and gem
            if (gemArrayList.size() > 0) {
                for (Gem g :gemArrayList) {
                    if(g.getPosX()<screenW/3){
                        if(Rect.intersects(playerShip.getHitBox(),g.getHitBox())){

                            left = g.posY - 20;
                            top =  g.posX - 20;
                            g.destroyable=true;
                            gem_count++;
                            toastArrayList.add(new Toast(myContext,screenW,screenH,top+20,left-20,single_jem_im," x 1",.018f));
                        }
                    }
                }
            }



            /*ArrayList<Weapon.Bullet> bullets=new ArrayList<Weapon.Bullet>();
            bullets=wepon1.getBulletList();
            //Log.i("test", String.valueOf(bullets.size()));
            for(Weapon.Bullet b:bullets){
                //Log.i("test", String.valueOf(b.getBulletX()));
                canvas.drawBitmap(bullet_im,b.getBulletX(),b.getBulletY(),null);

            }*/

        }

        public void enemyRespawn(){
            long milli_now = System.currentTimeMillis();
            int time_gap;
            if(game_sec_elapsed%enemyWaveRepeatTime==0 && game_sec_elapsed>0){

                long milli_now_msg= System.currentTimeMillis();

                if ((milli_now - lastMsgDisplayed) > (3000) ) {
                    lastMsgDisplayed = System.currentTimeMillis();

                    Toast temp = new Toast(myContext, screenW, screenH, screenW , screenH , single_enemy, "Enemy Wave", .03f);
                    //Log.i("test", String.valueOf(temp.getWidth()));
                    Toast emy_up = new Toast(myContext, screenW, screenH, (screenW-temp.getDest().width()*2)/2, screenH / 3, single_warning, "Enemy Wave", .03f);
                    emy_up.setDuration(5);

                    Paint em_paint = new Paint();
                    em_paint.setTextAlign(Paint.Align.LEFT);
                    em_paint.setColor(Color.argb(255, 255, 255, 255));
                    em_paint.setTextSize(25);
                    em_paint.setTypeface(halo);
                    em_paint.setTextSize(.025f * screenW);

                    emy_up.setPaint(em_paint);
                    toastArrayList.add(emy_up);


                }
                time_gap=100;
                generate_gem();
            }
            else{
                time_gap=1000;
            }

            if ((milli_now - last_enemy_birth) > (time_gap)) {
                //Time for new enemy to take birth

                Random r = new Random();
                float minX = 2.0f;
                float maxX = 0.0f;

                switch (r.nextInt(4-1) + 1){

                    case 1:
                        //Enemy type 1
                        enemies.add(new Enemy1(myContext,screenW,screenH,(r.nextInt(6-1) + 1),
                                (r.nextFloat() * (maxX - minX) + minX),playerShip,enemy1,enemyLevel,3));
                        break;
                    case 2:
                        //Enemy type 2

                        enemies.add(new Enemy1(myContext,screenW,screenH,(r.nextInt(6-1) + 1),
                                (r.nextFloat() * (maxX - minX) + minX),playerShip,enemy2,enemyLevel,7));
                        break;
                    case 3:
                        //Enemy type 3

                        enemies.add(new Enemy1(myContext,screenW,screenH,(r.nextInt(6-1) + 1),0,playerShip,enemy3,enemyLevel,12));
                        break;
                }




                last_enemy_birth = System.currentTimeMillis();
            }
        }



        private void control(){
            try{
                thread.sleep(0);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        private void draw(Canvas canvas){
            try{
                //canvas.drawBitmap(backgroundImg, 0, 0, null);
                canvas.drawColor(Color.argb(255,0,0,0));

                //Draw the space dust

                // White specs of dust
                paint.setColor(Color.argb(255, 255, 255, 255));

                //Draw the dust from our arrayList
                for (SpaceDust sd : dustList) {
                    //Log.i("test","spacedust:"+String.valueOf(sd.getY()));

                    paint.setColor(Color.argb(255, 255, 255, 255));
                    canvas.drawPoint(sd.getX(), sd.getY(), paint);

                }

                //Draw the weapons
                canvas.drawCircle(wepon1.getX(),wepon1.getY(),wepon1.get_button_radius(),wepon1.getPaint());
                canvas.drawCircle(wepon2.getX(),wepon2.getY(),wepon2.get_button_radius(),wepon2.getPaint());
                canvas.drawCircle(wepon3.getX(),wepon3.getY(),wepon3.get_button_radius(),wepon3.getPaint());


                //Draw the spaceship
                canvas.drawBitmap(playerShip.getBitmap(),playerShip.getX(),playerShip.getY(),null);

                joystick_x=screenW-joystick_over.getHeight()-screenW/40;
                joystick_y=screenH-joystick_over.getHeight()-40;


                joystick_btn_x=joystick_x+(joystick_back.getWidth()/2)-joystick_btn.getWidth()/2;
                joystick_btn_y=joystick_y+(joystick_back.getHeight()/2)-joystick_btn.getHeight()/2;
                joystick_center_x=joystick_btn_x+joystick_btn.getWidth()/2;
                joystick_center_y=joystick_btn_y+joystick_btn.getHeight()/2;




                if(joystick_btn_touched){
                    //Restrain the btn inside holder
                    //Get the distance of btn from center of holder

                    float temp_x=joystick_center_x-joystick_touched_btn_x;
                    temp_x=temp_x*temp_x;

                    float temp_y=joystick_center_y-joystick_touched_btn_y;
                    temp_y=temp_y*temp_y;

                    joystick_btn_distance= (float) Math.sqrt((temp_y+temp_x));
                    //Log.i("test","joystick_btn_distance "+String.valueOf(joystick_btn_distance));

                    if(joystick_btn_distance<joystick_over.getWidth()/2){
                        joystick_btn_x=joystick_touched_btn_x-joystick_btn.getWidth()/2;
                        joystick_btn_y=joystick_touched_btn_y-joystick_btn.getHeight()/2;
                    }
                    else{
                        joystick_btn_touched=false;

                        //Get the ratio of the line
                        //int k1=joystick_over.getWidth()/2;

                        //joystick_btn_x=joystick_touched_btn_x-joystick_btn.getWidth()/2;
                        //joystick_btn_y=joystick_touched_btn_y-joystick_btn.getHeight()/2;
                    }

                }

                canvas.drawBitmap(joystick_btn,joystick_btn_x,joystick_btn_y,null);

                if(joystick_touched){
                    canvas.drawBitmap(joystick_over,joystick_x,joystick_y,null);
                }
                else{
                    canvas.drawBitmap(joystick_back,joystick_x,joystick_y,null);
                }

                //Draw the bullets

                ArrayList<Weapon.Bullet> bullets=new ArrayList<Weapon.Bullet>();
                bullets=wepon1.getBulletList();
                //Log.i("test", String.valueOf(bullets.size()));
                for(Weapon.Bullet b:bullets){
                    //Log.i("test", String.valueOf(b.getBulletX()));
                    canvas.drawBitmap(bullet_im,b.getBulletX(),b.getBulletY(),null);

                }

                //Draw the missile
                ArrayList<Weapon.Missile> missiles=new ArrayList<Weapon.Missile>();


                missiles=wepon2.getMissileList();
                for(Weapon.Missile m:missiles){
                    //missile
                    canvas.drawBitmap(m.getMissileImage(),m.getSrc(),m.getDest(),null);

                }


                //Draw the Laser
                ArrayList<Weapon.Laser> lasers=new ArrayList<Weapon.Laser>();
                lasers=wepon3.getLaserList();
                //Log.i("test", String.valueOf(bullets.size()));
                for(Weapon.Laser l:lasers){
                    //Log.i("test", String.valueOf(b.getBulletX()));
                    //canvas.drawBitmap(bullet_im,m.getMissileX(),m.getMissileY(),null);

                    canvas.drawBitmap(l.getLaserBitmap(),l.getLaserX(),l.getLaserY(),l.getLaserPaint());

                }

                //Draw the enemies

                for(Object enemy:enemies){
                    //Log.i("test", String.valueOf(enemy.getClass()));
                    //canvas.drawBitmap(bullet_im,m.getMissileX(),m.getMissileY(),null);
                    canvas.drawBitmap(((Enemy1) enemy).getBitmap(),((Enemy1) enemy).getposX(),((Enemy1) enemy).getposY(),null);


                }

                //Draw the blasts

                for(Blast blast:blastArrayList){
                    canvas.drawBitmap(blast1_im,blast.getSrcRect(),blast.getDestRect(),null);

                }

                //Draw the Coins
                for(Coin coin:coinArrayList){
                    canvas.drawBitmap(gold_coin_im,coin.getSrcRect(),coin.getDestRect(),null);

                }

                //Draw the toasts
                for(Toast toast:toastArrayList){
                    canvas.drawText(toast.getText(),toast.getPosX() , toast.getPosY(), toast.getPaint());
                    if(toast.getBitmap()!=null){
                        //Draw the bitmap
                        canvas.drawBitmap(toast.getBitmap(),toast.getSrc(),toast.getDest(),toast.getPaint());
                    }
                }

                //Draw the jem
                for(Gem gem:gemArrayList){
                    canvas.drawBitmap(gem_im,gem.getSrcRect(),gem.getDestRect(),null);

                }




                //Draw the text info
                paint.setTextAlign(Paint.Align.LEFT);
                paint.setColor(Color.argb(255, 128, 128, 128));
                paint.setTextSize(25);
                paint.setTypeface(halo);
                int dimen= (int) paint.getTextSize();


                //Draw the health bar
                canvas.drawBitmap(health.getHealthBar(),health.healthSrcRect,health.healthDestRect,null);
                canvas.drawBitmap(health.bitmap_back,health.backSrcRect,health.backDestRect,null);

                canvas.drawBitmap(Bitmap.createScaledBitmap(single_health_im,dimen,dimen,true),screenW-220,40,null);
                canvas.drawText("x "+String.valueOf(health.get_currentHealth()),screenW-180,60,paint);



                canvas.drawBitmap(Bitmap.createScaledBitmap(single_jem_im,dimen,dimen,true),5+100,5,null);
                canvas.drawText("x "+String.valueOf(gem_count),dimen+10+100,25,paint);

                canvas.drawBitmap(Bitmap.createScaledBitmap(single_coin_im,dimen,dimen,true),150+100,5,null);
                canvas.drawText("x "+String.valueOf(coins_no),dimen+10+150+100,25,paint);

                canvas.drawBitmap(Bitmap.createScaledBitmap(single_score_im,dimen,dimen,true),300+100,5,null);
                canvas.drawText("x "+String.valueOf((int)Math.floor(score)),dimen+10+300+100,25,paint);


                //Log.i("test", String.valueOf(wepon1.getArcRadius()));
                //Weapon 1
                canvas.drawArc(wepon1.getRect(), -90, wepon1.getArcRadius(), true, wepon1.getArcPaint());
                canvas.drawBitmap(wepon1.getBulletIconBitmap(),
                        wepon1.getX()-wepon1.getBulletIconBitmap().getWidth()/2,
                        wepon1.getY()-wepon1.getBulletIconBitmap().getHeight()/2,null);

                //Weapon 2
                canvas.drawArc(wepon2.getRect(), -90, wepon2.getArcRadius(), true, wepon2.getArcPaint());
                canvas.drawBitmap(wepon2.getMissileIconBitmap(),
                        wepon2.getX()-wepon2.getMissileIconBitmap().getWidth()/2,
                        wepon2.getY()-wepon2.getMissileIconBitmap().getHeight()/2,null);

                //Weapon 3
                canvas.drawArc(wepon3.getRect(), -90, wepon3.getArcRadius(), true, wepon3.getArcPaint());
                canvas.drawBitmap(wepon3.getWeapon3IconBitmap(),
                        wepon3.getX()-wepon3.getWeapon3IconBitmap().getWidth()/2,
                        wepon3.getY()-wepon3.getWeapon3IconBitmap().getHeight()/2,null);


                //Draw play pause button
                canvas.drawBitmap(play_pause_im,0,0,null);





                debug=false;
                boolean sm_debug=true;
                if(sm_debug){
                    Paint newPaint;
                    newPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
                    newPaint.setStrokeWidth(1);
                    newPaint.setTextSize(20);
                    newPaint.setColor(Color.argb(255,255,0,255));

                    canvas.drawText("FPS:"+fps,(2),60,newPaint);

                }

                if(debug){
                    //Draw test
                    Paint arcPaint;
                    arcPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
                    arcPaint.setStrokeWidth(1);
                    arcPaint.setStyle(Paint.Style.STROKE);
                    arcPaint.setColor(Color.argb(255,255,0,255));


                    //canvas.drawRect(src,arcPaint);
                    //arcPaint.setColor(Color.argb(200,255,255,0));
                    //canvas.drawRect(dst,arcPaint);
                    for(Object enemy:enemies){
                        //Log.i("test", String.valueOf(enemy.getClass()));
                        //canvas.drawBitmap(bullet_im,m.getMissileX(),m.getMissileY(),null);
                        canvas.drawRect(((Enemy1) enemy).getHitBox(),arcPaint);
                        //canvas.drawBitmap(((Enemy1) enemy).getBitmap(),((Enemy1) enemy).getposX(),((Enemy1) enemy).getposY(),null);
                    }

                    for(Weapon.Bullet m:bullets){
                        //Log.i("test", String.valueOf(b.getBulletX()));
                        canvas.drawRect(m.getHitBox(),arcPaint);

                    }

                    for(Weapon.Missile m:missiles){
                        //Log.i("test", String.valueOf(b.getBulletX()));
                        canvas.drawRect(m.getHitBox(),arcPaint);

                    }

                    for(Weapon.Laser l:lasers){
                        //Log.i("test", String.valueOf(b.getBulletX()));
                        canvas.drawRect(l.getHitBox(),arcPaint);

                    }

                    //Draw the toasts
                    for(Toast toast:toastArrayList){
                        canvas.drawRect(toast.getDest(),arcPaint);
                        //canvas.drawText(toast.getText(),toast.getPosX() , toast.getPosY(), toast.getPaint());

                    }

                    //Draw the gem hit box
                    for(Gem gem:gemArrayList){
                        canvas.drawRect(gem.getHitBox(),arcPaint);
                        //canvas.drawText(toast.getText(),toast.getPosX() , toast.getPosY(), toast.getPaint());

                    }

                    //Draw the player hit box
                    canvas.drawRect(playerShip.getHitBox(),arcPaint);

                    //Draw the health bar
                    canvas.drawRect(health.healthSrcRect,arcPaint);






                }











            }
            catch (Exception e){

            }
        }

        boolean doTouchEvent(MotionEvent event) {


            //Log.i("test", "Pointer ID = " + pointerId);
            //Log.i("test","No of touch: "+String.valueOf(num));
            synchronized (mySurfaceHolder) {
                int action = MotionEventCompat.getActionMasked(event);
                // Get the index of the pointer associated with the action.
                int index = MotionEventCompat.getActionIndex(event);
                int xPos1 = -1;
                int yPos1 = -1;
                int xPos2 = -1;
                int yPos2 = -1;


                if (event.getPointerCount() > 1 && event.getPointerCount()<=2) {
                    //handle multi touch event

                    xPos1 = (int)MotionEventCompat.getX(event, 0);
                    yPos1 = (int)MotionEventCompat.getY(event, 0);

                    xPos2 = (int)MotionEventCompat.getX(event, 1);
                    yPos2 = (int)MotionEventCompat.getY(event, 1);

                    //Log.d("test", String.valueOf(index));
                    //Log.d("test", String.valueOf(xPos1));
                    //Log.d("test", String.valueOf(yPos1));


                    switch (event.getAction() & MotionEvent.ACTION_MASK) {
                        case MotionEvent.ACTION_POINTER_DOWN:






                            if(
                                    (xPos1>wepon1.getX()-wepon1.get_button_radius()
                                    && xPos1<wepon1.getX()+(wepon1.get_button_radius()*2)
                                    && yPos1>wepon1.getY()-wepon1.get_button_radius()
                                    && yPos1<wepon1.getY()+(wepon1.get_button_radius()*2))
                                    ||
                                    (xPos2>wepon1.getX()-wepon1.get_button_radius()
                                    && xPos2<wepon1.getX()+(wepon1.get_button_radius()*2)
                                    && yPos2>wepon1.getY()-wepon1.get_button_radius()
                                    && yPos2<wepon1.getY()+(wepon1.get_button_radius()*2))


                                    ){
                                weapon_1_touched=true;
                            }

                            if(
                                    (xPos1>wepon2.getX()-wepon2.get_button_radius()
                                            && xPos1<wepon2.getX()+(wepon2.get_button_radius()*2)
                                            && yPos1>wepon2.getY()-wepon2.get_button_radius()
                                            && yPos1<wepon2.getY()+(wepon2.get_button_radius()*2))
                                            ||
                                            (xPos2>wepon2.getX()-wepon2.get_button_radius()
                                                    && xPos2<wepon2.getX()+(wepon2.get_button_radius()*2)
                                                    && yPos2>wepon2.getY()-wepon2.get_button_radius()
                                                    && yPos2<wepon2.getY()+(wepon2.get_button_radius()*2))


                                    ){
                                weapon_2_touched=true;
                            }

                            if(
                                    (xPos1>wepon3.getX()-wepon3.get_button_radius()
                                            && xPos1<wepon3.getX()+(wepon3.get_button_radius()*2)
                                            && yPos1>wepon3.getY()-wepon3.get_button_radius()
                                            && yPos1<wepon3.getY()+(wepon3.get_button_radius()*2))
                                            ||
                                            (xPos2>wepon3.getX()-wepon3.get_button_radius()
                                                    && xPos2<wepon3.getX()+(wepon3.get_button_radius()*2)
                                                    && yPos2>wepon3.getY()-wepon3.get_button_radius()
                                                    && yPos2<wepon3.getY()+(wepon3.get_button_radius()*2))


                                    ){
                                weapon_3_touched=true;
                                //Log.i("test","weapon 3");
                            }


                            break;
                        case MotionEvent.ACTION_MOVE:
                            if(
                                    ((xPos1>joystick_btn_x-40
                                    && (xPos1<joystick_btn.getWidth()+joystick_btn_x+40))
                                    && (yPos1>joystick_btn_y-40
                                    && (yPos1<joystick_btn.getHeight()+joystick_btn_y+40)))



                            ){
                                joystick_btn_touched=true;
                                joystick_touched_btn_x=xPos1;
                                joystick_touched_btn_y=yPos1;
                                //Get the angle of movement
                                int p1=joystick_x+joystick_back.getWidth()/2;
                                int p2=joystick_y+joystick_back.getHeight()/2;

                                int q1=joystick_x+joystick_back.getWidth()/2+100;
                                int q2=joystick_y+joystick_back.getHeight()/2;

                                int r1=xPos1;
                                int r2=yPos1;

                                float pq= (float) Math.sqrt((float) Math.pow(p1-q1,2)+(float) Math.pow(p2-q2,2));
                                float pr= (float) Math.sqrt((float) Math.pow(p1-r1,2)+(float) Math.pow(p2-r2,2));
                                float qr= (float) Math.sqrt((float) Math.pow(q1-r1,2)+(float) Math.pow(q2-r2,2));

                                joystick_angle= (float) Math.toDegrees((float) Math.acos((pq*pq+pr*pr-qr*qr)/(2*pq*pr)));
                                //check if the touch is below the half of the joystick_back
                                if(yPos1>joystick_y+joystick_back.getHeight()/2){
                                    //Add 180 to the joystick_angle
                                    joystick_angle=180+(180-joystick_angle);
                                }
                            }

                            break;

                        case MotionEvent.ACTION_UP:
                            weapon_1_touched=(false);
                            weapon_2_touched=(false);
                            weapon_3_touched=(false);
                            joystick_touched=false;
                            joystick_btn_touched=false;
                            joystick_touched_btn_x=-1;
                            joystick_touched_btn_y=-1;
                            break;

                        case MotionEvent.ACTION_POINTER_UP:
                            weapon_1_touched=(false);
                            weapon_2_touched=(false);
                            weapon_3_touched=(false);

                            break;
                    }
                }

                else{
                    //Handle single touch
                    int eventaction = event.getAction();
                    int X = (int)event.getX();
                    int Y = (int)event.getY();

                    switch (eventaction ) {



                        case MotionEvent.ACTION_DOWN:

                            //Check if play pause button clicked
                            if(X>=0 && X<play_pause_im.getWidth()  ){
                                if(running==true){

                                    thread.setRunning(false);
                                    }
                                else{

                                    thread.setRunning(true);
                                    //thread.resume();
                                }
                                Log.i("test", String.valueOf(running));
                                Log.i("test", String.valueOf(thread.getState()));
                                //
                            }

                            if(X>wepon1.getX()-wepon1.get_button_radius() && X<wepon1.getX()+(wepon1.get_button_radius()*2)
                                    && Y>wepon1.getY()-wepon1.get_button_radius() && Y<wepon1.getY()+(wepon1.get_button_radius()*2)){
                                weapon_1_touched=(true);
                            }

                            if(X>wepon2.getX()-wepon2.get_button_radius() && X<wepon2.getX()+(wepon2.get_button_radius()*2)
                                    && Y>wepon2.getY()-wepon2.get_button_radius() && Y<wepon2.getY()+(wepon2.get_button_radius()*2)){
                                weapon_2_touched=(true);
                            }

                            if(X>wepon3.getX()-wepon3.get_button_radius() && X<wepon3.getX()+(wepon3.get_button_radius()*2)
                                    && Y>wepon3.getY()-wepon3.get_button_radius() && Y<wepon3.getY()+(wepon3.get_button_radius()*2)){
                                weapon_3_touched=(true);
                            }

                            if((X>joystick_x && (X<joystick_back.getWidth()+joystick_x))
                                    && (Y>joystick_y && (Y<joystick_back.getHeight()+joystick_y))){
                                joystick_touched=true;
                            }

                            break;
                        case MotionEvent.ACTION_UP:
                            weapon_1_touched=(false);
                            weapon_2_touched=(false);
                            weapon_3_touched=(false);
                            joystick_touched=false;
                            joystick_btn_touched=false;
                            joystick_touched_btn_x=-1;
                            joystick_touched_btn_y=-1;

                            break;
                        case MotionEvent.ACTION_MOVE:

                            if((X>joystick_btn_x-40 && (X<joystick_btn.getWidth()+joystick_btn_x+40))
                                    && (Y>joystick_btn_y-40 && (Y<joystick_btn.getHeight()+joystick_btn_y+40))){
                                joystick_btn_touched=true;
                                joystick_touched_btn_x=X;
                                joystick_touched_btn_y=Y;
                                //Get the angle of movement
                                int p1=joystick_x+joystick_back.getWidth()/2;
                                int p2=joystick_y+joystick_back.getHeight()/2;

                                int q1=joystick_x+joystick_back.getWidth()/2+100;
                                int q2=joystick_y+joystick_back.getHeight()/2;

                                int r1=X;
                                int r2=Y;

                                float pq= (float) Math.sqrt((float) Math.pow(p1-q1,2)+(float) Math.pow(p2-q2,2));
                                float pr= (float) Math.sqrt((float) Math.pow(p1-r1,2)+(float) Math.pow(p2-r2,2));
                                float qr= (float) Math.sqrt((float) Math.pow(q1-r1,2)+(float) Math.pow(q2-r2,2));

                                joystick_angle= (float) Math.toDegrees((float) Math.acos((pq*pq+pr*pr-qr*qr)/(2*pq*pr)));
                                //check if the touch is below the half of the joystick_back
                                if(Y>joystick_y+joystick_back.getHeight()/2){
                                    //Add 180 to the joystick_angle
                                    joystick_angle=180+(180-joystick_angle);
                                }
                            }

                            break;
                    }
                }






            }


            return true;
        }

        public void setSurfaceSize(int width, int height) {
            synchronized (mySurfaceHolder) {
                screenW = width;
                screenH = height;
                backgroundImg = Bitmap.createScaledBitmap(backgroundImg, width, height, true);

                drawScaleW = (float) screenW / 800;
                drawScaleH = (float) screenH / 600;

                joystick_back = Bitmap.createScaledBitmap(joystick_back,(int)(joystick_back.getWidth()/drawScaleW),
                        (int)(joystick_back.getHeight()/drawScaleW), true);
                joystick_over = Bitmap.createScaledBitmap(joystick_over,(int)(joystick_over.getWidth()/drawScaleW),
                        (int)(joystick_over.getHeight()/drawScaleW), true);
                joystick_btn = Bitmap.createScaledBitmap(joystick_btn,(int)(joystick_btn.getWidth()/drawScaleW),
                        (int)(joystick_btn.getHeight()/drawScaleW), true);


            }

        }

        public void setRunning(boolean b) {
            running = b;
        }

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return thread.doTouchEvent(event);
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        thread.setRunning(true);
        if (thread.getState() == Thread.State.NEW ){
            thread.start();
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        thread.setSurfaceSize(width, height);

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        thread.setRunning(false);
    }
}
