package com.example.shubham.pig;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.util.Random;
import java.util.Timer;

public class MainActivity extends AppCompatActivity implements SensorEventListener {
    //private final int rollAnimations = 50;
    private final int delayTime = 15;
    private  Resources res;
    private  ImageView die;
    private  Button Button_roll;
    private  Button Button_hold;
    private  TextView Score_p1;
    private  TextView Score_p2;
   // private final int[] diceImages = new int[] { R.drawable.one, R.drawable.two, R.drawable.three, R.drawable.four, R.drawable.five, R.drawable.six };
   // private Drawable dice[] = new Drawable[6];
    private final Random randomGen = new Random();
    private SensorManager sensorMgr;
    private Handler animationHandler;
    private long lastUpdate = -1;
    private float x, y, z;
   // private float last_x=0, last_y=0, last_z=0;
    private boolean paused=false;
    private static final int UPDATE_DELAY = 250;
    private static final int SHAKE_THRESHOLD = 3;
    private static int chance=1;
    private static int score_p1=0;
    private static int score_p2=0;
    private static int prevscore_p1=0;
    private static int prevscore_p2=0;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater=getMenuInflater();
        menuInflater.inflate(R.menu.menulist,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==R.id.new_game){
            initialize();
            Button_roll.setVisibility(View.VISIBLE);
            Button_hold.setVisibility(View.VISIBLE);

        }
        return true;
    }

    public void initialize(){
        paused=false;
        chance=1;
        score_p1=0;
        score_p2=0;
        prevscore_p1=0;
        prevscore_p2=0;
        Score_p1.setText("0");  Score_p2.setText("0");
        die.setImageResource(R.drawable.dice3droll);
        getSupportActionBar().setTitle(R.string.player_1);

        boolean accelSupported = sensorMgr.registerListener(this,
                sensorMgr.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),SensorManager.SENSOR_DELAY_GAME);
        if (!accelSupported) sensorMgr.unregisterListener(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        res = getResources();
        getSupportActionBar().setTitle(R.string.player_1);
        Button_roll=(Button)findViewById(R.id.button_roll);
        Button_hold=(Button)findViewById(R.id.button_hold);
        Score_p1=(TextView)findViewById(R.id.textViewScore1);
        Score_p2=(TextView)findViewById(R.id.textViewScore2);
        Score_p1.setText("0");  Score_p2.setText("0");
      //  initialize();
        Button_roll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    rollDice();
                } catch (Exception e) {};
            }
        });

        Button_hold.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    new AlertDialog.Builder(MainActivity.this).
                            setMessage(R.string.alert_2).setNeutralButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    }).show();
                    if(chance==1) {

                        prevscore_p1=score_p1; chance=2;
                        getSupportActionBar().setTitle(R.string.player_2);
                    }
                    else if(chance==2){
                        prevscore_p2=score_p2; chance=1;
                        //getActionBar().setTitle(R.string.player_1);
                        getSupportActionBar().setTitle(R.string.player_1);
                    }
                } catch (Exception e) {};
            }
        });
        die=(ImageView)findViewById(R.id.imagedice);
        animationHandler = new Handler() {
            public void handleMessage(Message msg) {
                int value;
                switch(value=randomGen.nextInt(6)+1) {
                    case 1:
                        die.setImageResource(R.drawable.one);
                        break;
                    case 2:
                        die.setImageResource(R.drawable.two);
                        break;
                    case 3:
                        die.setImageResource(R.drawable.three);
                        break;
                    case 4:
                        die.setImageResource(R.drawable.four);
                        break;
                    case 5:
                        die.setImageResource(R.drawable.five);
                        break;
                    case 6:
                        die.setImageResource(R.drawable.six);
                        break;
                    default:
                }
                if(chance==1 && value!=1){
                    score_p1+=value;
                    Score_p1.setText(Integer.toString(score_p1));
                    if(score_p1>=100){
                        getSupportActionBar().setTitle("Player 1 Wins");
                        new AlertDialog.Builder(MainActivity.this).setMessage("Player 1 wins").setTitle("Congratulations!").setNeutralButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                               //initialize();
                            }
                        }).show();
                        Button_roll.setVisibility(View.INVISIBLE);
                        Button_hold.setVisibility(View.INVISIBLE);
                        sensorMgr.unregisterListener(MainActivity.this);
                    }
                }
                else if(chance==2 && value!=1){
                    score_p2+=value;
                    Score_p2.setText(Integer.toString(score_p2));
                    if(score_p2>=100) {
                        getSupportActionBar().setTitle("Player 2 Wins");
                        //reset();
                        new AlertDialog.Builder(MainActivity.this).setMessage("Player 2 wins").setTitle("Congratulations!").setNeutralButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                              // initialize();
                            }
                        }).show();
                        Button_roll.setVisibility(View.INVISIBLE);
                        Button_hold.setVisibility(View.INVISIBLE);
                        sensorMgr.unregisterListener(MainActivity.this);

                    }
                }
                else if(value==1 && chance==1) {
                    new AlertDialog.Builder(MainActivity.this).
                            setMessage(R.string.alert_1).setNeutralButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    }).show();
                    score_p1=prevscore_p1;
                    Score_p1.setText(Integer.toString(score_p1));
                    chance=2;
                   getSupportActionBar().setTitle(R.string.player_2);
                }
                else if(value==1 && chance==2) {
                    new AlertDialog.Builder(MainActivity.this).
                            setMessage(R.string.alert_1).setNeutralButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    }).show();
                    score_p2=prevscore_p2;
                    Score_p2.setText(Integer.toString(score_p2));
                    chance=1;
                   getSupportActionBar().setTitle(R.string.player_1);
                }
                paused=false;	//user can press again

            }
            };
        sensorMgr = (SensorManager) getSystemService(SENSOR_SERVICE);
        boolean accelSupported = sensorMgr.registerListener(this,
                sensorMgr.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),SensorManager.SENSOR_DELAY_GAME);
        if (!accelSupported) sensorMgr.unregisterListener(this);
        //rollDice();
    }

    private void rollDice() {
        if (paused) return;
        new Thread(new Runnable() {
            @Override
            public void run() {
                doRoll();
            }
        }).start();
        MediaPlayer mp = MediaPlayer.create(this, R.raw.shake_dice);
        try {
            mp.prepare();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mp.start();
    }

    private void doRoll(){ // only does a single roll
        synchronized (getLayoutInflater()) {
            animationHandler.sendEmptyMessage(0);
        }
        try { // delay to allow for smooth animation
            Thread.sleep(delayTime);
        } catch (final InterruptedException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void onSensorChanged(SensorEvent event) {
        Sensor mySensor = event.sensor;
        if (mySensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            long curTime = System.currentTimeMillis();
            if ((curTime - lastUpdate) > UPDATE_DELAY) {
                long diffTime = (curTime - lastUpdate);
                lastUpdate = curTime;
                x = event.values[0];
                y = event.values[1];
                z = event.values[2];
                float acceleration = (float) Math.sqrt(x*x + y*y + z*z) - SensorManager.GRAVITY_EARTH;
                //  float speed = (Math.abs(x + y + z - last_x - last_y - last_z) / diffTime) * 1000;
                if (acceleration > SHAKE_THRESHOLD) { //the screen was shaked
                    rollDice();
                }
            }

        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        return; //this method isn't used
    }
    public void onResume() {
        super.onResume();
        paused = false;
    }

    public void onPause() {
        super.onPause();
        paused = true;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        CharSequence[] score=new CharSequence[]{Score_p1.getText(),Score_p2.getText()};
        outState.putCharSequenceArray("value",score);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        CharSequence[] score=savedInstanceState.getCharSequenceArray("value");
        Score_p1.setText(score[0]); Score_p2.setText(score[1]);
    }
}
