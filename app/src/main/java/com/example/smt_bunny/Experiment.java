package com.example.smt_bunny;

import static android.os.SystemClock.uptimeMillis;
import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static java.lang.Math.floor;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.AnimationDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Observer;
import java.util.Random;

public class Experiment extends AppCompatActivity implements View.OnTouchListener {

    ConstraintLayout myLayout;

    // Files
    public File File_Event;
    public File File_Accel;
    public File File_Touch;
    private static final String TAG = "MyApp";
    private BufferedOutputStream bos;

    private Button beginButton;

    // Define some fields for your views and widgets
    //private CanvasView canvasView;

    // Define some fields for your data and logic
    private DataCollectorService dataCollectorService;
    private boolean can_record;
    private String participantID;
    private String startingCondition;
    private String straightStartingDirection;
    private String curvedStartingDirection;
    private boolean endGame = false;
    private String path;
    private String direction;
    private Random r = new Random();
    private boolean carrotMove = false;
    private float carrotOffsetX;
    private float carrotOffsetY;
    private HashMap<Integer, Integer> pointerLabels; //the hashmap to store the mapping between pointer IDs and labels
    private int currentLabel; //the current label value

    // Game Elements
    private ImageView bg_grass;
    private ImageView bg;
    private ImageView bunny_brown;
    private ImageView bunny_white;
    private ImageView bunny;
    private ImageView carrot_default;
    private ImageView carrot;
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
    private ImageView path_straight_1600;
    private ImageView path_curved_1600;
    private ImageView path_image;
    private ImageView guide_curve_toright;
    private ImageView guide;

    private int offsetCarrotPath;
    private int offsetBunnyPath;

    private int screenHeight = Resources.getSystem().getDisplayMetrics().heightPixels;
    private int screenWidth = Resources.getSystem().getDisplayMetrics().widthPixels;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Set the layout file for this activity
        setContentView(R.layout.activity_experiment);
        myLayout = findViewById(R.id.experimentLayout);


        bg_grass = findViewById(R.id.bg_grass);
        bunny_white = findViewById(R.id.bunny_white);
        bunny_brown = findViewById(R.id.bunny_brown);
        carrot_default = findViewById(R.id.carrot_default);
       // guide_curve_toright = findViewById(R.id.);
        beginButton = findViewById(R.id.beginButton);
/*
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
*/
        path_straight_1600 = findViewById(R.id.path_straight_1600);
        path_curved_1600 = findViewById(R.id.path_curved_1600);

        bg = bg_grass;
        bunny = bunny_brown;
        carrot = carrot_default;
        path_image = path_curved_1600;

        carrot.setOnTouchListener(this);

        bg.setOnTouchListener(new View.OnTouchListener() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch (View v, MotionEvent event) {
                int action = event.getActionMasked (); //get the masked action of the touch event
                int pointerIndex = event.getActionIndex (); //get the index of the pointer associated with the action
                int pointerId = event.getPointerId (pointerIndex); //get the ID of the pointer associated with the action

                switch (action) { //handle different actions
                    case MotionEvent.ACTION_DOWN: //the first pointer goes down
                    case MotionEvent.ACTION_POINTER_DOWN: //a subsequent pointer goes down
                        pointerLabels.put (pointerId, currentLabel); //put the mapping between pointer ID and label in the hashmap
                        Log.d ("BG", "Pointer " + currentLabel + " down at (" + event.getX (pointerIndex) + ", " + event.getY (pointerIndex) + ")"); //log the label and position of the pointer
                        currentLabel++; //increment the current label value
                        break;
                    case MotionEvent.ACTION_MOVE: //a pointer moves
                        for (int i = 0; i < event.getPointerCount (); i++) { //loop through all pointers
                            pointerId = event.getPointerId (i); //get the ID of each pointer
                            int label = pointerLabels.get (pointerId); //get the label of each pointer from the hashmap
                            Log.d ("BG", "Pointer " + label + " move at (" + event.getX (i) + ", " + event.getY (i) + ")"); //log the label and position of each pointer
                        }
                        break;
                    case MotionEvent.ACTION_UP: //the last pointer goes up
                    case MotionEvent.ACTION_POINTER_UP: //a non-primary pointer goes up
                        int label = pointerLabels.get (pointerId); //get the label of the pointer that went up from the hashmap
                        Log.d ("BG", "Pointer " + label + " up at (" + event.getX (pointerIndex) + ", " + event.getY (pointerIndex) + ")"); //log the label and position of the pointer that went up
                        pointerLabels.remove (pointerId); //remove the mapping between pointer ID and label from the hashmap
                        break;
                }
                return true;
            }
        });
        pointerLabels = new HashMap<> (); //initialize the hashmap
        currentLabel = 1; //initialize the current label value

        beginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                offsetBunnyPath = (int) Math.floor(Math.abs(path_image.getHeight() - bunny.getHeight()) / 2);
                offsetCarrotPath = (int) Math.floor(Math.abs(path_image.getHeight() - carrot.getHeight()) / 2);
                nextTrial();
            }
        });

        final GameState bunnyGame = new GameState();
        //  final CanvasView mCanvasView = new CanvasView(this, bunnyGame);
        viewGame("hide");

        // Get the participant ID and the experimental condition from the intent that started this activity
        Intent intent = getIntent();
        participantID = intent.getStringExtra("participantID");
        startingCondition = intent.getStringExtra("condition");
        straightStartingDirection = intent.getStringExtra("straightDir1");
        curvedStartingDirection = intent.getStringExtra("curvedDir1");

        //Create the savedata files
        can_record = initializeSaveFile(participantID);
        // Create a file output stream for the file "touch_data.txt"
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(File_Touch, true);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        // Create a buffered output stream with a buffer size of 1024 bytes
        bos = new BufferedOutputStream(fos, 1024);

        Intent service = new Intent(this, DataCollectorService.class);
        service.putExtra("f_accel", File_Accel);
//        service.putExtra("f_touch", File_Touch);
//        service.putExtra("f_event", File_Event);
        service.putExtra("can_record", can_record);
        startService(service);


        GameState.initialize(startingCondition, straightStartingDirection, curvedStartingDirection);
    }



    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    public boolean onTouch (View v, MotionEvent event) {
        if (v == carrot) { //check if the view that was touched is the imageview of a carrot


            switch (event.getAction ()) { //get the action of the touch event
                case MotionEvent.ACTION_DOWN: //the user first touches the view
                    carrotOffsetX = event.getRawX() - carrot.getX();
                    carrotOffsetY = event.getRawY() - carrot.getY();
                    carrotMove = carrotOffsetX >= 0 & carrotOffsetX <= carrot.getWidth() & carrotOffsetY >= 0 & carrotOffsetY <= carrot.getHeight();
                    Log.d ("Carrot", "Touch down at (" + event.getX () + ", " + event.getY () + ")"); //log the xy position of the touch event
                    break;
                case MotionEvent.ACTION_MOVE: //the user moves their finger on the view
                    if (carrotMove) {
                        carrot.setX(event.getRawX() - carrotOffsetX);
                        carrot.setY(event.getRawY() - carrotOffsetY);
                    }
                    carrot.invalidate();
                    break;
                case MotionEvent.ACTION_UP: //the user lifts their finger from the view
                    //snap carrot's position if it's offscreen
                    if (carrot.getX() < 0) {
                        carrot.setX(0); //Carrot is too far left, bring back
                    } else if (carrot.getX() + carrot.getWidth() > screenWidth) {
                        carrot.setX(screenWidth - carrot.getWidth()); //too far right, bring back
                    }
                    if (carrot.getY() < 0) {
                        carrot.setY(0); //Carrot is too far up, bring back
                    } else if (carrot.getY() + carrot.getHeight() > screenHeight) {
                        carrot.setY(screenHeight - carrot.getHeight()); //too far down, bring back
                    }

                    float carrot_center_x = carrot.getX() + (carrot.getWidth() / 2);
                    float carrot_center_y = carrot.getY() + (carrot.getHeight() / 2);
                    float bunny_center_x = bunny.getX() + (bunny.getWidth() / 2);
                    float bunny_center_y = bunny.getY() + (bunny.getHeight() / 2);
                    boolean x_overlap = Math.abs(carrot_center_x - bunny_center_x) <= (bunny.getWidth() / 2 + carrot.getWidth() / 2) * .5;
                    boolean y_overlap = Math.abs(carrot_center_y - bunny_center_y) <= (bunny.getHeight() / 2 + carrot.getHeight() / 2) * .5;

                    if (x_overlap & y_overlap) {
                        nextTrial();
                    }
                    carrot.invalidate();
                    Log.d ("Carrot", "Touch up at (" + event.getX () + ", " + event.getY () + ")"); //log the xy position of the touch event
                    break;
            }
            try {
                saveSamples(event);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return true; //return true to indicate that the touch event was handled
        }
        return false; //return false to indicate that the touch event was not handled (because it wasn't the carrot)
    }

    private void nextTrial() {
        viewGame("hide");

        boolean endOfBlock = GameState.incrementTrial();
        if (endOfBlock) {
            boolean endGame = GameState.endGame();
            if (!endGame) {
                //go to the inter-block screen
                //then start a new trial in this new block
            }
        } else {
            path = GameState.getCurrentPathType();
            direction = GameState.getCurrentDirection();
            startTrial(path, direction);
            //start the next trial in this block
        }
    }
    private void startTrial(final String path, final String direction){

        // Set X Locations of all
        carrot.setX(0);
        path_image.setX(0);
        if (path.equals("curved")){
            bunny.setX(1345);

        } else{
            bunny.setX(screenWidth - bunny.getWidth());
        }

        int ytop = r.nextInt(screenHeight - path_image.getHeight()) + 1;
        carrot.setY(ytop + offsetCarrotPath);
        bunny.setY(ytop + offsetBunnyPath);
        path_image.setY(ytop);

        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                String dat = "START, " + (carrot.getX() + carrot.getWidth() / 2) + ", " + (carrot.getY() + carrot.getHeight() / 2) + ", " + carrot.getWidth() + ", " + carrot.getHeight() + ", " +
                        (bunny.getX() + bunny.getWidth() / 2) + ", " + (bunny.getY() + bunny.getHeight() / 2) + ", " + bunny.getWidth() + ", " + bunny.getHeight() + ", " +
                        GameState.getTrialNumber() + ", " + GameState.getBlockNumber() + ", " + path + ", " + direction;
               // Intent eventIntent = new Intent("EVENT_DATA");
               // eventIntent.putExtra("eventData", dat);
               // sendBroadcast(eventIntent);
               // Log.d(TAG, "event = " + dat);
                WriteDatToFile(File_Event, dat);
                viewGame("show");
            }
        }, 2000);
    }

    private void viewGame(String showhide) {
        int vis = View.VISIBLE;
        if (showhide.equals("hide")) {
            vis = View.INVISIBLE;
        }
        bunny.setVisibility(vis);
        carrot.setVisibility(vis);
        path_image.setVisibility(vis);
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    void saveSamples(MotionEvent ev) throws IOException {
        final int historySize = ev.getHistorySize();
        final int pointerCount = ev.getPointerCount();
        String mydat = "";
        for (int h = 0; h < historySize; h++) {
            long htime = ev.getHistoricalEventTime(h);
            for (int p = 0; p < pointerCount; p++) {
                mydat = mydat+htime + ", " + ev.getPointerId(p) +", "+ ev.getHistoricalX(p,h) + ", " + ev.getHistoricalY(p,h) + ", " + ev.getHistoricalTouchMajor(p,h) + ", " + ev.getHistoricalTouchMinor(p,h) + ", " + ev.getHistoricalTouchMajor(p,h)/2 * ev.getHistoricalTouchMinor(p,h)/2 * Math.PI + ", " + ev.getHistoricalPressure(p,h) + "\n";
            }
        }
        long evtime = ev.getEventTime();
        for (int p = 0; p < pointerCount; p++) {
            mydat = mydat+evtime + ", " + ev.getPointerId(p) +", "+ ev.getRawX(p) + ", " + ev.getRawY(p) + ", " + ev.getTouchMajor(p) + ", " + ev.getTouchMinor(p) + ", " + ev.getTouchMajor(p)/2 * ev.getTouchMinor(p)/2 * Math.PI + ", " + ev.getPressure(p) + "\n";
        }
        // Write the data to the buffered output stream as bytes
        bos.write(mydat.getBytes());
        // Flush the buffered output stream to ensure that all data is written to the file output stream
        bos.flush();

//        WriteToFile(File_Touch,mydat);
    }

    private void WriteDatToFile(File WriteFile, String data) {
        WriteToFile(WriteFile, uptimeMillis() + ", " + data + "\n");
    }
    private void WriteToFile(File WriteFile, String data) {
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

    public boolean initializeSaveFile(String participantID) {
        //Data storage
        //https://www.journaldev.com/9400/android-external-storage-read-write-save-file
        //saves to sdcard/Android/data/com.example.smt_bunny/files/FILEPATH/FILENAME, viewable on explorer or in android studio in Device File Explorer.
        String filepath = "MyFileStorage";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault());
        String currentDateandTime = sdf.format(new Date());

        //e.g., participantID = 9999;
        String filename_event = currentDateandTime + "_sub" + participantID + "_event.csv";
        String filename_accel = currentDateandTime + "_sub" + participantID + "_accel.csv";
        String filename_touch = currentDateandTime + "_sub" + participantID + "_touch.csv";

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
    private boolean isExternalStorageReadOnly() {
        String extStorageState = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(extStorageState)) {
            return true;
        }
        return false;
    }

    private boolean isExternalStorageAvailable() {
        String extStorageState = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(extStorageState)) {
            return true;
        }
        return false;
    }

}