package com.example.smt_bunny;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.res.ResourcesCompat;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.AnimationDrawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.view.LineChartView;

import static android.os.SystemClock.uptimeMillis;
import static com.example.smt_bunny.CanvasView.data;
import static com.example.smt_bunny.CanvasView.data_accel;
import static com.example.smt_bunny.CanvasView.line;
import static com.example.smt_bunny.CanvasView.line_accel1;
import static com.example.smt_bunny.CanvasView.line_accel2;
import static com.example.smt_bunny.CanvasView.line_accel3;
import static com.example.smt_bunny.CanvasView.lines;
import static com.example.smt_bunny.CanvasView.lines_accel;
import static com.example.smt_bunny.CanvasView.yAxisValues;
import static com.example.smt_bunny.CanvasView.yAxisValues_accel;
import static com.example.smt_bunny.CanvasView.yAxisValues_accel2;
import static com.example.smt_bunny.CanvasView.yAxisValues_accel3;
import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

//TODO
//TRIAL CONSTRUCTOR: in GIMP, make a bunch of preconstructed transparent paths. Some can have an obstacle; make sure obstacle is y-flippable.
//      Each path has an assigned height, and can be vertically flipped.
//Pick one of the trial paths.
//Pick a random Y for the bunny. If Y is less than path height, force it to curve down.
//      If Y is greater than screenHeight-PathHeight, force it to curve up. Otherwise, random curve up/down.
//Set path Y to bunny Y. vertical flip if necessary.
//Set Carrot Y to bunny Y + pathHeight for curve down, or -pathHeight for curve up.
//
//Show a "waiting' or something during intertrial interval
//BOOT_TIME in files actually refers to time that the start button was pressed on the home screen.
//Better bunny image
//Image selection
//Add instructions
//sound effects?

public class MainActivity extends AppCompatActivity implements SensorEventListener {//, View.OnTouchListener {

    //COLORS
    private int mColorWhite;

    //APP CONTROL
    public static Button closeAppBtn;
    public static int quit_click_count = 0;
    CanvasView mCanvasView;
    ConstraintLayout mConstraintLayout;


    //MAIN MENU
    private Button startBtn;
    private TextView inputIDlabel;
    private TextView inputID;
    private ToggleButton pathEnableToggle;
    public static boolean path_enable = FALSE;

    //DEBUG INTERFACE
    private boolean debug_enable = FALSE;
    private ToggleButton debugEnableToggle;
    public static boolean debug_mode = FALSE;
    private ToggleButton debugToggle;
    private ToggleButton chartToggle;
    private Button clearChartBtn;
    private TextView acceltext;
    private TextView acceldat;
    private TextView gyrotext;
    private TextView gyrodat;
    public static LineChartView touchChart;
    public static LineChartView accelChart1;

    //GAME ELEMENTS
    private ImageView bg_grass;
    public static ImageView bunny_brown;
    public static ImageView bunny_white;
    public static ImageView bunny;
    public static ImageView carrot_default;
    public static ImageView carrot;
    public ImageView bunnyCheer;
    public AnimationDrawable bunnyCheerAnim;
    public ImageView bunnyCheer1;
    public AnimationDrawable bunnyCheerAnim1;
    public ImageView bunnyCheer2;
    public AnimationDrawable bunnyCheerAnim2;
    public ImageView bunnyCheer3;
    public AnimationDrawable bunnyCheerAnim3;
    public ImageView bunnyCheer4;
    public AnimationDrawable bunnyCheerAnim4;
    public ImageView bunnyCheer5;
    public AnimationDrawable bunnyCheerAnim5;



    //    public static Path dirt;
    public static ImageView path_000;
    public static ImageView path_300;
    public static ImageView path_600;
    public static ImageView path_900;
    public static ImageView current_path;

    public static ImageView path_1600_300;
    public static ImageView curve_1600_300;

    //GAME VARS
    private int xDelta;
    private int yDelta;
    public static Random r = new Random();

    //SENSORS
    private SensorManager sensorManager;
    private Sensor accelerometer;
    private Sensor gyroscope;
    public static float accelValues1;
    public static float accelValues2;
    public static float accelValues3;

    //DATA STORAGE
    public static File File_Event;
    public static File File_Accel;
    public static File File_Touch;
    public String subID;
    public static boolean can_record;


    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mConstraintLayout = findViewById(R.id.constraintLayout);
        final GameState bunnyGame = new GameState();
        final CanvasView mCanvasView = new CanvasView(this, bunnyGame);



//      //  mConstraintLayout.setOnTouchListener(this);

        //COLORS
        mColorWhite = ResourcesCompat.getColor(getResources(), R.color.white, null);

        //APP CONTROL
        closeAppBtn = findViewById(R.id.closeAppBtn);
//        image1.setImageResource(R.drawable.NAMEOFIMAGE);

        //MAIN MENU
        startBtn = findViewById(R.id.startBtn);
        inputIDlabel = findViewById(R.id.inputIDlabel);
        inputID = findViewById(R.id.editTextID);
        pathEnableToggle = findViewById(R.id.pathEnableToggle);

        //DEBUG INTERFACE
        debugEnableToggle = findViewById(R.id.debugEnableToggle);
        debugToggle = findViewById(R.id.debugToggle);
        acceltext = findViewById(R.id.acceltext);
        acceldat = findViewById(R.id.acceldat);
        gyrotext = findViewById(R.id.gyrotext);
        gyrodat = findViewById(R.id.gyrodat);
        touchChart = findViewById(R.id.TouchChart);
        clearChartBtn = findViewById(R.id.clearChartBtn);
        accelChart1 = findViewById(R.id.AccelChart1);
        chartToggle = findViewById(R.id.chartToggle);
        touchChart.setVisibility(View.INVISIBLE);
        accelChart1.setVisibility(View.INVISIBLE);


        //GAME ELEMENTS
        bg_grass = findViewById(R.id.bg_grass);
        bunny_white = findViewById(R.id.bunny_white);
        bunny_brown = findViewById(R.id.bunny_brown);
        carrot_default = findViewById(R.id.carrot_default);
        path_000 = findViewById(R.id.path_000);
        path_300 = findViewById(R.id.path_300);
        path_600 = findViewById(R.id.path_600);
        path_900 = findViewById(R.id.path_900);
        path_1600_300 = findViewById(R.id.path_1600_300);
        curve_1600_300 = findViewById(R.id.curve_1600_300);

//        bunnyCheer  = findViewById(R.id.bunnyCheerSingle);
//        bunnyCheerAnim = (AnimationDrawable) bunnyCheer.getDrawable();
//        bunnyCheerAnim.start();
        bunnyCheer1  = findViewById(R.id.bunnyCheer1);
        bunnyCheerAnim1 = (AnimationDrawable) bunnyCheer1.getDrawable();
        bunnyCheer2  = findViewById(R.id.bunnyCheer2);
        bunnyCheerAnim2 = (AnimationDrawable) bunnyCheer2.getDrawable();
        bunnyCheer3  = findViewById(R.id.bunnyCheer3);
        bunnyCheerAnim3 = (AnimationDrawable) bunnyCheer3.getDrawable();
        bunnyCheer4  = findViewById(R.id.bunnyCheer4);
        bunnyCheerAnim4 = (AnimationDrawable) bunnyCheer4.getDrawable();
        bunnyCheer5  = findViewById(R.id.bunnyCheer5);
        bunnyCheerAnim5 = (AnimationDrawable) bunnyCheer5.getDrawable();


        bunny = bunny_brown;
        carrot = carrot_default;
        current_path = path_1600_300;
        int dirt_pattern_id = getResources().getIdentifier("dirt4.png","drawable","com.example.smt_bunny");
        Bitmap dirt_pattern_bitmap = BitmapFactory.decodeResource(getResources(), dirt_pattern_id);
//        bunny.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                switch(event.getAction() & MotionEvent.ACTION_MASK){
//                    case MotionEvent.ACTION_DOWN:
//                        bunnyOffsetX = event.getRawX() - v.getX();
//                        bunnyOffsetY = event.getRawY() - v.getY();
//                        return true;
//                    case MotionEvent.ACTION_MOVE:
//                        bunny.setX(event.getRawX()-bunnyOffsetX);
//                        bunny.setY(event.getRawY()-bunnyOffsetY);
//                        return true;
//                }
//                return false;
//            }
//        });

        //Set up sensors
        //see https://stackoverflow.com/questions/9358862/impossibility-to-change-the-rate-of-the-accelerometer for possibility making sensor mananger a foreground service (like music player) that gets priority CPU
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE_UNCALIBRATED);
        if (sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE) != null) {
            gyrotext.setText("GOT A GYRO");
        }
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_FASTEST);
        sensorManager.registerListener(this, gyroscope, SensorManager.SENSOR_DELAY_NORMAL);


        //Starting display
        viewHideAll();
        viewMenu("show");
        viewInterBlock("show");
        startBtn.setVisibility(View.INVISIBLE);
        pathEnableToggle.setVisibility(View.INVISIBLE);


        //Button Setup
        inputID.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    startBtn.setVisibility(View.VISIBLE);
                    pathEnableToggle.setVisibility(View.VISIBLE);
                    subID = inputID.getText().toString();
                }
                return false;
            }
        });


        startBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                can_record = initializeSaveFile();
                mConstraintLayout.addView(mCanvasView);

                viewHideAll();
                if(debug_enable){debugToggle.setVisibility(View.VISIBLE);}
                viewGame("show");
                //startTrial();
                bunnyGame.nextTrial();

            }
        });
        pathEnableToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                path_enable = isChecked;
            }
        });

        debugEnableToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                debug_enable = isChecked;
            }
        });
        debugToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    viewDebug("show");
//                    mCanvasView.drawTouch(100, 400, 200, 450);
                    WriteDatToFile(File_Event, "DEBUG_ON");
                    debug_mode = TRUE;

                } else {
                    viewDebug("hide");
                    WriteDatToFile(File_Event, "DEBUG_OFF");
                    debug_mode = FALSE;

                }

            }
        });
        closeAppBtn.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {
                quit_click_count = quit_click_count + 1;
                switch (quit_click_count) {
                    case 1:
                        closeAppBtn.setText("Really QUIT?");
                        closeAppBtn.setTextColor(mColorWhite);
                        break;
                    case 2:
                        finishAndRemoveTask();
                        break;
                }
            }
        });

        clearChartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                lines.clear();
                yAxisValues = null;
                line = null;
                yAxisValues = new ArrayList();
                yAxisValues.add(new PointValue(1, 1));
                line = new Line(yAxisValues);
                lines.add(line);
                data = new LineChartData();
                data.setLines(lines);
                touchChart.setLineChartData(data);

                lines_accel.clear();
                yAxisValues_accel = null;
                line_accel1 = null;
                yAxisValues_accel = new ArrayList();
                yAxisValues_accel.add(new PointValue(1, 1));
                line_accel1 = new Line(yAxisValues_accel);
                lines_accel.add(line_accel1);
                yAxisValues_accel2 = null;
                line_accel2 = null;
                yAxisValues_accel2 = new ArrayList();
                yAxisValues_accel2.add(new PointValue(1, 1));
                line_accel2 = new Line(yAxisValues_accel2);
                lines_accel.add(line_accel2);
                yAxisValues_accel3 = null;
                line_accel3 = null;
                yAxisValues_accel3 = new ArrayList();
                yAxisValues_accel3.add(new PointValue(1, 1));
                line_accel3 = new Line(yAxisValues_accel3);
                lines_accel.add(line_accel3);
                data_accel = new LineChartData();
                data_accel.setLines(lines_accel);
                accelChart1.setLineChartData(data_accel);
            }
        });

        chartToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    touchChart.setVisibility(View.VISIBLE);
                    accelChart1.setVisibility(View.VISIBLE);
                } else {
                    touchChart.setVisibility(View.INVISIBLE);
                    accelChart1.setVisibility(View.INVISIBLE);
                }
            }
        });
    }


    //SENSOR CONTROLLERS

    private void getAccelerometer(SensorEvent event) {
        //     final float alpha = (float) 0.8;
        // Isolate the force of gravity with the low-pass filter.
        //     gravity[0] = alpha * gravity[0] + (1 - alpha) * event.values[0];
        //     gravity[1] = alpha * gravity[1] + (1 - alpha) * event.values[1];
        //     gravity[2] = alpha * gravity[2] + (1 - alpha) * event.values[2];

        // Remove the gravity contribution with the high-pass filter.
        //     linear_acceleration[0] = event.values[0] - gravity[0];
        //     linear_acceleration[1] = event.values[1] - gravity[1];
        //     linear_acceleration[2] = event.values[2] - gravity[2];
        accelValues1 = event.values[0];
        accelValues2 = event.values[1];
        accelValues3 = event.values[2];

        acceldat.setText(String.valueOf(event.values[0]) + "\n" + String.valueOf(event.values[1]) + "\n" + String.valueOf(event.values[2]));
        String dat = String.valueOf(event.values[0]) + ", " + String.valueOf(event.values[1]) + ", " + String.valueOf(event.values[2]);
        WriteDatToFile(File_Accel, dat);
    }

    private void getGyroscope(SensorEvent event) {
        gyrodat.setText(String.valueOf(event.values[0]));
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            getAccelerometer(event);
        }
        if (event.sensor.getType() == Sensor.TYPE_GYROSCOPE_UNCALIBRATED) {
            getGyroscope(event);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    //TOUCH SENSOR
    //   @Override
    //   public boolean onTouch(View v, MotionEvent event) {
    //       if(event.getAction() == MotionEvent.ACTION_DOWN){

//        }
//        mCanvasView.drawTouch(200,400,220,500);
//        return true;
//    }

    //VIEW CONTROLLERS

    private void viewGame(String showhide) {
        int vis = View.VISIBLE;
        if (showhide.equals("hide")) {
            vis = View.INVISIBLE;
        }
        bg_grass.setVisibility(vis);
        bunny.setVisibility(vis);
        carrot.setVisibility(vis);
        current_path.setVisibility(vis);
    }

    private void viewDebug(String showhide) {
        int vis = View.VISIBLE;
        if (showhide.equals("hide")) {
            vis = View.INVISIBLE;
        }
        acceltext.setVisibility(vis);
        acceldat.setVisibility(vis);
        gyrotext.setVisibility(vis);
        gyrodat.setVisibility(vis);
        chartToggle.setVisibility(vis);
        clearChartBtn.setVisibility(vis);
        if (showhide.equals("hide")) {
            touchChart.setVisibility(View.INVISIBLE);
            accelChart1.setVisibility(View.INVISIBLE);
        }

    }

    private void viewMenu(String showhide) {
        int vis = View.VISIBLE;
        if (showhide.equals("hide")) {
            vis = View.INVISIBLE;
        }
        startBtn.setVisibility(vis);
        inputIDlabel.setVisibility(vis);
        inputID.setVisibility(vis);
        debugEnableToggle.setVisibility(vis);
        pathEnableToggle.setVisibility(vis);

    }

    private void viewInterBlock(String showhide) {
        int vis = View.VISIBLE;
        if (showhide.equals("hide")){
            vis = View.INVISIBLE;
        }
        bg_grass.setVisibility(vis);
        viewBunnyCheerFive(showhide);
    }

    private void viewBunnyCheerFive(String showhide){
        int vis = View.VISIBLE;
        if (showhide.equals("hide")){
            vis = View.INVISIBLE;
        }
        bunnyCheer1.setVisibility(vis);
        bunnyCheer2.setVisibility(vis);
        bunnyCheer3.setVisibility(vis);
        bunnyCheer4.setVisibility(vis);
        bunnyCheer5.setVisibility(vis);
        switch (vis) {
            case View.VISIBLE:
                bunnyCheerAnim1.start();
                bunnyCheerAnim2.start();
                bunnyCheerAnim3.start();
                bunnyCheerAnim4.start();
                bunnyCheerAnim5.start();
                break;
            case View.INVISIBLE:
                bunnyCheerAnim1.stop();
                bunnyCheerAnim2.stop();
                bunnyCheerAnim3.stop();
                bunnyCheerAnim4.stop();
                bunnyCheerAnim5.stop();
                break;
        }

    }



    private void viewHideAll() {
        viewDebug("hide");
        viewMenu("hide");
        viewGame("hide");
        viewInterBlock("hide");
    }


    private void viewTemplate(String showhide) {
        int vis = View.VISIBLE;
        if (showhide.equals("hide")) {
            vis = View.INVISIBLE;
        }
        //X.setVisibility(vis);
    }

    //GAMEPLAY FUNCTIONS

    public void startInterBlock(){
        viewHideAll();
        viewInterBlock("show");
    }

    public static void startTrial(int trialNumber, String pathType) {
        int screenHeight = Resources.getSystem().getDisplayMetrics().heightPixels;
        int screenWidth = Resources.getSystem().getDisplayMetrics().widthPixels;




        bunny.setX(screenWidth - bunny.getWidth());
        carrot.setX(0);

        //Pick a random number from (0-9)*100 and use that for carrot_Y.
        //bunny is constrained, locked to N offset from carrot, up or down. TEMP locked to 300.
        //If carrot is bottom half or middle, bunny higher. otherwise bunny lower.
        //dist_down is 900 - carrot_y. dist_up is carrot_y. Max dist is
        //int dist_max = max(carrot.getY(), 900-carrot.getY())
        //just use some conditionals.
        //if dist_max == 900 then do the 900 one automatically because otherwise it'll be too rare.

        int carrot_pick = r.nextInt(10)*100; //this will get random 0-9 and multiply 100
        carrot.setY(carrot_pick);
        int dist_down = 900 - carrot_pick;
        int dist_up = carrot_pick;
        int dist_max = Math.max(dist_down, dist_up);
        int path_type;
        String path_name = "";
        current_path.setVisibility(View.INVISIBLE);

        current_path = path_1600_300;
        if( carrot_pick <= 600) { //carrot in bottom half, so make it slope up (i.e., default pic)
            current_path.setScaleY(-1f);
            path_name = "300_up";
            bunny.setY(carrot.getY() + 300);
            current_path.setY(carrot.getY());
        } else {
            current_path.setScaleY(1f);
            path_name = "300_down";
            bunny.setY(carrot.getY() - 300);
            current_path.setY(carrot.getY()-300);


        }

        //Removed different path selections, offset will be constant
        /*
        switch (carrot_pick) {

            case 0:
                path_type = r.nextInt(4); //0,3,6,9
                switch(path_type){
                    case 0:
                        current_path = path_000;
                        current_path.setScaleY(1f);
                        path_name = "000";
                        carrot.setY(bunny.getY());
                        current_path.setY(bunny.getY());
                        break;
                    case 1:
                        current_path = path_300;
                        current_path.setScaleY(1f);
                        path_name = "300_down";
                        carrot.setY(bunny.getY()+300);
                        current_path.setY(bunny.getY());
                        break;
                    case 2:
                        current_path = path_600;
                        current_path.setScaleY(1f);
                        path_name = "600_down";
                        carrot.setY(bunny.getY()+600);
                        current_path.setY(bunny.getY());
                        break;
                    case 3:
                        current_path = path_900;
                        current_path.setScaleY(1f);
                        path_name = "900_down";
                        carrot.setY(bunny.getY()+900);
                        current_path.setY(bunny.getY());
                        break;
                }
                break;

            case 100:
            case 200:
                path_type = r.nextInt(3); //0,3,6
                switch(path_type){
                    case 0:
                        current_path = path_000;
                        current_path.setScaleY(1f);
                        path_name = "0";
                        carrot.setY(bunny.getY());
                        current_path.setY(bunny.getY());
                        break;
                    case 1:
                        current_path = path_300;
                        current_path.setScaleY(1f);
                        path_name = "300_down";
                        carrot.setY(bunny.getY()+300);
                        current_path.setY(bunny.getY());
                        break;
                    case 2:
                        current_path = path_600;
                        current_path.setScaleY(1f);
                        path_name = "600_down";
                        carrot.setY(bunny.getY()+600);
                        current_path.setY(bunny.getY());
                        break;
                }
                break;

            case 300:
                path_type = r.nextInt(4); //-3,0,3,6
                switch(path_type){
                    case 0:
                        current_path = path_300;
                        current_path.setScaleY(-1f);
                        path_name = "300_up";
                        carrot.setY(bunny.getY()-300);
                        current_path.setY(bunny.getY()-900);
                        break;
                    case 1:
                        current_path = path_000;
                        current_path.setScaleY(1f);
                        path_name = "000";
                        carrot.setY(bunny.getY());
                        current_path.setY(bunny.getY());
                        break;
                    case 2:
                        current_path = path_300;
                        current_path.setScaleY(1f);
                        path_name = "300_down";
                        carrot.setY(bunny.getY()+300);
                        current_path.setY(bunny.getY());
                        break;
                    case 3:
                        current_path = path_600;
                        current_path.setScaleY(1f);
                        path_name = "600_down";
                        carrot.setY(bunny.getY()+600);
                        current_path.setY(bunny.getY());
                        break;
                }
                break;

            case 400:
            case 500:
                path_type = r.nextInt(3); //-3,0,3
                switch(path_type){
                    case 0:
                        current_path = path_300;
                        current_path.setScaleY(-1f);
                        path_name = "300_up";
                        carrot.setY(bunny.getY()-300);
                        current_path.setY(bunny.getY()-900);
                        break;
                    case 1:
                        current_path = path_000;
                        current_path.setScaleY(1f);
                        path_name = "000";
                        carrot.setY(bunny.getY());
                        current_path.setY(bunny.getY());
                        break;
                    case 2:
                        current_path = path_300;
                        current_path.setScaleY(1f);
                        path_name = "300_down";
                        carrot.setY(bunny.getY()+300);
                        current_path.setY(bunny.getY());
                        break;
                }
                break;
            case 600:
                path_type = r.nextInt(4); //-6,-3,0,3
                switch(path_type){
                    case 0:
                        current_path = path_600;
                        current_path.setScaleY(-1f);
                        path_name = "600_up";
                        carrot.setY(bunny.getY()-600);
                        current_path.setY(bunny.getY()-900);
                        break;
                    case 1:
                        current_path = path_300;
                        current_path.setScaleY(-1f);
                        path_name = "300_up";
                        carrot.setY(bunny.getY()-300);
                        current_path.setY(bunny.getY()-900);
                        break;
                    case 2:
                        current_path = path_000;
                        current_path.setScaleY(1f);
                        path_name = "000";
                        carrot.setY(bunny.getY());
                        current_path.setY(bunny.getY());
                        break;
                    case 3:
                        current_path = path_300;
                        current_path.setScaleY(1f);
                        path_name = "300_down";
                        carrot.setY(bunny.getY()+300);
                        current_path.setY(bunny.getY());
                        break;

                }
                break;
            case 700:
            case 800:
                path_type = r.nextInt(3); //-6,-3,0
                switch(path_type){
                    case 0:
                        current_path = path_600;
                        current_path.setScaleY(-1f);
                        path_name = "600_up";
                        carrot.setY(bunny.getY()-600);
                        current_path.setY(bunny.getY()-900);
                        break;
                    case 1:
                        current_path = path_300;
                        current_path.setScaleY(-1f);
                        path_name = "300_up";
                        carrot.setY(bunny.getY()-300);
                        current_path.setY(bunny.getY()-900);
                        break;
                    case 2:
                        current_path = path_000;
                        current_path.setScaleY(1f);
                        path_name = "000";
                        carrot.setY(bunny.getY());
                        current_path.setY(bunny.getY());
                        break;
                }
                break;
            case 900:
                path_type = r.nextInt(4); //-9,-6,-3,0
                switch(path_type){
                    case 0:
                        current_path = path_900;
                        current_path.setScaleY(-1f);
                        path_name = "900_up";
                        carrot.setY(bunny.getY()-900);
                        current_path.setY(bunny.getY());
                        break;
                    case 1:
                        current_path = path_600;
                        current_path.setScaleY(-1f);
                        path_name = "600_up";
                        carrot.setY(bunny.getY()-600);
                        current_path.setY(bunny.getY()-900);
                        break;
                    case 2:
                        current_path = path_300;
                        current_path.setScaleY(-1f);
                        path_name = "300_up";
                        carrot.setY(bunny.getY()-300);
                        current_path.setY(bunny.getY()-900);
                        break;
                    case 3:
                        current_path = path_000;
                        current_path.setScaleY(1f);
                        path_name = "000";
                        carrot.setY(bunny.getY());
                        current_path.setY(bunny.getY());
                        break;
                }
                break;

        }
        */

        if(!path_enable){
            path_name = "NONE";
            bunny.setY(r.nextInt(900));
            carrot.setY(r.nextInt(900));
        }



        //bunny.setY(r.nextInt(screenHeight - bunny.getHeight()));
        //carrot.setY(r.nextInt(screenHeight - carrot.getHeight()));
        bunny.setVisibility(View.INVISIBLE);
        carrot.setVisibility(View.INVISIBLE);

        final String finalPath_name = path_name;
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                String dat = "START, " + (carrot.getX() + carrot.getWidth() / 2) + ", " + (carrot.getY() + carrot.getHeight() / 2) + ", " + carrot.getWidth() + ", " + carrot.getHeight() + ", " +
                        (bunny.getX() + bunny.getWidth() / 2) + ", " + (bunny.getY() + bunny.getHeight() / 2) + ", " + bunny.getWidth() + ", " + bunny.getHeight() + ", " + finalPath_name;
                WriteDatToFile(File_Event, dat);
                bunny.setVisibility((View.VISIBLE));
                carrot.setVisibility(View.VISIBLE);
                if(path_enable){current_path.setVisibility(View.VISIBLE);}
            }
        }, 2000);

    }

    private View.OnTouchListener carrotTouch() {
        return new View.OnTouchListener() {

            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View view, MotionEvent event) {

                final int x = (int) event.getRawX();
                final int y = (int) event.getRawY();

                switch (event.getAction() & MotionEvent.ACTION_MASK) {

                    case MotionEvent.ACTION_DOWN:
                        RelativeLayout.LayoutParams lParams = (RelativeLayout.LayoutParams)
                                view.getLayoutParams();

                        xDelta = x - lParams.leftMargin;
                        yDelta = y - lParams.topMargin;
                        break;

                    case MotionEvent.ACTION_UP:
                        break;

                    case MotionEvent.ACTION_MOVE:
                        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) view.getLayoutParams();
                        layoutParams.leftMargin = x - xDelta;
                        layoutParams.topMargin = y - yDelta;
                        layoutParams.rightMargin = 0;
                        layoutParams.bottomMargin = 0;
                        view.setLayoutParams(layoutParams);
                        break;
                }
                mConstraintLayout.invalidate();
                return true;
            }
        };
    }

    // DATA STORAGE

    public boolean initializeSaveFile() {
        //Data storage
        //https://www.journaldev.com/9400/android-external-storage-read-write-save-file
        //saves to sdcard/Android/data/com.example.smt_bunny/files/FILEPATH/FILENAME, viewable on explorer or in android studio in Device File Explorer.
        String filepath = "MyFileStorage";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault());
        String currentDateandTime = sdf.format(new Date());

        //int subID = 9999;
        String filename_event = currentDateandTime + "_sub" + subID + "_event.csv";
        String filename_accel = currentDateandTime + "_sub" + subID + "_accel.csv";
        String filename_touch = currentDateandTime + "_sub" + subID + "_touch.csv";

        long time = System.currentTimeMillis();
        String header_boottime = "BOOT_TIME_EPOCH " + time + "\nBOOT_UPTIME_MILLIS " + uptimeMillis();
        String header_time = "time";
        String header_event = "event_marker, " +
                "start_item_center_x, start_item_center_y, start_item_width, start_item_height, " +
                "end_item_center_x, end_item_center_y, end_item_width, end_item_height, path_type";
        String header_accel = "accel_x, accel_y, accel_z"; // landscape tablet, x is along short height, y is along long width, z is through the tablet.
        String header_touch = "touch_ID, touch_x, touch_y, touch_max_diameter, touch_min_diameter, touch_area, touch_pressure";

        String header_eventfile = header_boottime + "\n" + header_time + ", " + header_event + "\n";
        String header_accelfile = header_boottime + "\n" + header_time + ", " + header_accel + "\n";
        String header_touchfile = header_boottime + "\n" + header_time + ", " + header_touch + "\n";

        boolean goodsave = TRUE;

        if(!isExternalStorageAvailable() || isExternalStorageReadOnly())
        {
    //            startBtn.setEnabled(false);
    //            startBtn.setText("NO SAVE FILE");
            goodsave = FALSE;
        }
            else

        {
            File_Event = new File(getExternalFilesDir(filepath), filename_event);
            try {
                FileOutputStream fos = new FileOutputStream(File_Event, false);
                fos.write(header_eventfile.getBytes());
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
                goodsave = FALSE;
            }

            File_Accel = new File(getExternalFilesDir(filepath), filename_accel);
            try {
                FileOutputStream fos = new FileOutputStream(File_Accel, false);
                fos.write(header_accelfile.getBytes());
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
                goodsave = FALSE;
            }

            File_Touch = new File(getExternalFilesDir(filepath), filename_touch);
            try {
                FileOutputStream fos = new FileOutputStream(File_Touch, false);
                fos.write(header_touchfile.getBytes());
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
                goodsave = FALSE;
            }

            //            startBtn.setText("SAVE");//
        }
            return goodsave;
    }

    private static boolean isExternalStorageReadOnly() {
        String extStorageState = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(extStorageState)) {
            return true;
        }
        return false;
    }

    private static boolean isExternalStorageAvailable() {
        String extStorageState = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(extStorageState)) {
            return true;
        }
        return false;
    }

    private static void WriteToFile(File WriteFile, String data) {
        if (can_record) {
            try {
                FileOutputStream fos = new FileOutputStream(WriteFile, true);
                fos.write(data.getBytes());
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
    private static void WriteDatToFile(File WriteFile, String data) {
        WriteToFile(WriteFile, uptimeMillis() + ", " + data + "\n");
    }
}

