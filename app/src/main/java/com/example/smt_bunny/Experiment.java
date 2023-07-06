package com.example.smt_bunny;

import static android.os.SystemClock.uptimeMillis;
import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.util.SparseArray;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

public class Experiment extends AppCompatActivity implements View.OnTouchListener {

    private static final int FLIP_HORIZONTAL = 100;
    ConstraintLayout myLayout;

    // Files
    public File File_Event;
    public File File_Accel;
    public File File_Touch;
    private static final String TAG = "MyApp";
    private BufferedOutputStream bos;

    private Button beginButton;
    private Button nextBlockButton;
    private Button quitButton;

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
    private SparseArray<Integer> pointerLabels;
    //private SparseArray<Integer> pointerLabelsCarrot;
    //private HashMap<Integer, Integer> pointerLabels; //the hashmap to store the mapping between pointer IDs and labels
    private int currentLabel; //the current label value
    //private int currentLabelCarrot; //the current label value for the carrot touches


    // Game Elements
    private ImageView bg_grass;
    private ImageView bg;
    private ImageView bunny_brown;
    private ImageView bunny_white;
    private ImageView bunny;
    private ImageView carrot_default;
    private ImageView carrot;

    private boolean bunny_brown_canflip = TRUE;
    private boolean carrot_default_canflip = FALSE;
    private boolean bunny_canflip;
    private boolean carrot_canflip;
    private Bitmap bunny_original;
    private Bitmap bunny_flip;
    private Bitmap carrot_original;
    private Bitmap carrot_flip;

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
    public TextView thankyoucheer;

    private ImageView path_straight_1600;
    private ImageView path_curved_1600;
    private ImageView path_image;
    private ImageView guide_curve_toright;
    private ImageView guide_curve_toleft;
    private ImageView guide_straight_toright;
    private ImageView guide_straight_toleft;

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
        guide_curve_toright = findViewById(R.id.guide_curve_toright);
        guide_curve_toleft = findViewById(R.id.guide_curve_toleft);
        guide_straight_toright = findViewById(R.id.guide_line_toright);
        guide_straight_toleft = findViewById(R.id.guide_line_toleft);
        beginButton = findViewById(R.id.beginButton);
        nextBlockButton = findViewById(R.id.nextBlockButton);
        quitButton = findViewById(R.id.quitButton);
        guide = guide_curve_toright;

 //   bunnyCheer  = findViewById(R.id.bunnyCheerSingle);
 //   bunnyCheerAnim = (AnimationDrawable) bunnyCheer.getDrawable();

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
    thankyoucheer = findViewById(R.id.thankyoucheer);

        path_straight_1600 = findViewById(R.id.path_straight_1600);
        path_curved_1600 = findViewById(R.id.path_curved_1600);

        bg = bg_grass;
        bunny = bunny_brown;
        bunny_canflip = bunny_brown_canflip;
        carrot = carrot_default;
        carrot_canflip = carrot_default_canflip;
        path_image = path_curved_1600;

        bunny_original = ((BitmapDrawable)bunny.getDrawable()).getBitmap();
        carrot_original = ((BitmapDrawable)carrot.getDrawable()).getBitmap();
        if(bunny_canflip) {
            bunny_flip = flip(bunny_original, FLIP_HORIZONTAL);
        } else{
            bunny_flip = bunny_original;
        }
        if(carrot_canflip){
            carrot_flip = flip(carrot_original, FLIP_HORIZONTAL);
        } else{
            carrot_flip = carrot_original;
        }

        carrot.setOnTouchListener(this);

        bg.setOnTouchListener(new View.OnTouchListener() {
            @RequiresApi(api = Build.VERSION_CODES.Q)
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch (View v, MotionEvent event) {
                int action = event.getActionMasked (); //get the masked action of the touch event
                int pointerIndex = event.getActionIndex (); //get the index of the pointer associated with the action
                int pointerId = event.getPointerId (pointerIndex); //get the ID of the pointer associated with the action
                boolean removeLabel = FALSE;
                String actionName = "UNKNOWN_ACTION";

                switch (action) { //handle different actions
                    case MotionEvent.ACTION_DOWN: //the first pointer goes down
                        actionName = "TOUCH_START";
                        pointerLabels.put (pointerId, currentLabel); //put the mapping between pointer ID and label in the hashmap
                        //  Log.d ("BG", "Pointer " + currentLabel + " down at (" + event.getX (pointerIndex) + ", " + event.getY (pointerIndex) + ")"); //log the label and position of the pointer
                        currentLabel++; //increment the current label value
                        break;
                    case MotionEvent.ACTION_POINTER_DOWN: //a subsequent pointer goes down
                        actionName = "SECONDARY_TOUCH_START";
                        pointerLabels.put (pointerId, currentLabel); //put the mapping between pointer ID and label in the hashmap
                      //  Log.d ("BG", "Pointer " + currentLabel + " down at (" + event.getX (pointerIndex) + ", " + event.getY (pointerIndex) + ")"); //log the label and position of the pointer
                        currentLabel++; //increment the current label value
                        break;
                    case MotionEvent.ACTION_MOVE: //a pointer moves
                        actionName = "TOUCH_MOVE";
                        //for (int i = 0; i < event.getPointerCount (); i++) { //loop through all pointers
                         //   pointerId = event.getPointerId (i); //get the ID of each pointer
                         //   int label = pointerLabels.get (pointerId); //get the label of each pointer from the hashmap
                        //    Log.d ("BG", "Pointer " + label + " move at (" + event.getX (i) + ", " + event.getY (i) + ")"); //log the label and position of each pointer
                        //}
                        break;
                    case MotionEvent.ACTION_UP: //the last pointer goes up
                        actionName = "TOUCH_END";
                        removeLabel = TRUE; //flag to remove the label, after saveSamples
                        break;
                    case MotionEvent.ACTION_POINTER_UP: //a non-primary pointer goes up
                        actionName = "SECONDARY_TOUCH_END";
                        removeLabel = TRUE;
                        //int label = pointerLabels.get (pointerId); //get the label of the pointer that went up from the hashmap
                        //Log.d ("BG", "Pointer " + label + " up at (" + event.getX (pointerIndex) + ", " + event.getY (pointerIndex) + ")"); //log the label and position of the pointer that went up
                        break;
                }
                try {
                    saveSamples(event, "background", actionName);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if(removeLabel){
                    pointerLabels.remove (pointerId); //remove the mapping between pointer ID and label from the hashmap
                }
                return true;
            }
        });
//        pointerLabels = new HashMap<> (); //initialize the hashmap
        pointerLabels = new SparseArray<Integer>(20); //initialize pointerLabels with an initial capacity of 10
        currentLabel = 1; //initialize the current label value
        //pointerLabelsCarrot = new SparseArray<Integer>(20); //initialize pointerLabels with an initial capacity of 10
        //currentLabelCarrot = 1; //initialize the current label value

        quitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            finishAffinity();
            }
        });

        beginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewGuide("hide");
                offsetBunnyPath = (int) Math.floor(Math.abs(path_image.getHeight() - bunny.getHeight()) / 2);
                offsetCarrotPath = (int) Math.floor(Math.abs(path_image.getHeight() - carrot.getHeight()) / 2);
                nextTrial();
            }
        });

        nextBlockButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewGuide("show");
                viewInterblock("hide");
            }
        });

        final GameState bunnyGame = new GameState();
        //  final CanvasView mCanvasView = new CanvasView(this, bunnyGame);
        quitButton.setVisibility(View.INVISIBLE);

        // Get the participant ID and the experimental condition from the intent that started this activity
        Intent intent = getIntent();
        participantID = intent.getStringExtra("participantID");
        startingCondition = intent.getStringExtra("startingPath");
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
        viewGame("hide");
        viewGuide("show");
        viewInterblock("hide");
    }



    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    public boolean onTouch (View v, MotionEvent event) {
        if (v == carrot) { //check if the view that was touched is the imageview of a carrot
            int action = event.getActionMasked();
            int pointerIndex = event.getActionIndex();
            int pointerID = event.getPointerId(pointerIndex);
            boolean removeLabel = FALSE;
            String actionName = null;


            switch (action) { //get the action of the touch event
                case MotionEvent.ACTION_DOWN: //the user first touches the view
                    actionName = "TOUCH_START";
                    carrotOffsetX = event.getRawX() - carrot.getX();
                    carrotOffsetY = event.getRawY() - carrot.getY();
                    carrotMove = carrotOffsetX >= 0 & carrotOffsetX <= carrot.getWidth() & carrotOffsetY >= 0 & carrotOffsetY <= carrot.getHeight();
                    pointerLabels.put(pointerID, currentLabel);
                    currentLabel++;
                    break;
                case MotionEvent.ACTION_POINTER_DOWN:
                    actionName = "SECONDARY_TOUCH_START";
                    pointerLabels.put(pointerID, currentLabel);
                    currentLabel++;
                    //Log.d ("Carrot", "Touch down at (" + event.getX () + ", " + event.getY () + ")"); //log the xy position of the touch event
                    break;
                case MotionEvent.ACTION_MOVE: //the user moves their finger on the view
                    actionName = "TOUCH_MOVE";
                    if (carrotMove) {
                        carrot.setX(event.getRawX() - carrotOffsetX);
                        carrot.setY(event.getRawY() - carrotOffsetY);
                    }
                    carrot.invalidate();
                    break;
                case MotionEvent.ACTION_UP: //the user lifts their finger from the view
                    actionName = "TOUCH_END";
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
                    removeLabel = TRUE;
                    break;
                case MotionEvent.ACTION_POINTER_UP:
                    actionName = "SECONDARY_TOUCH_END";
                    removeLabel = TRUE; //Remove it after saving the sample so that the pointer ID can get recorded.
                    //Log.d ("Carrot", "Touch up at (" + event.getX () + ", " + event.getY () + ")"); //log the xy position of the touch event
                    break;
            }
            try {
                saveSamples(event, "carrot", actionName);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if(removeLabel) {
                pointerLabels.remove(pointerID);
            }
            return true; //return true to indicate that the touch event was handled
        }
        return false; //return false to indicate that the touch event was not handled (because it wasn't the carrot)
    }

    @Override
    public void onBackPressed() {
        // do nothing
    }


    private void nextTrial() {
        viewGame("hide");
        currentLabel = 1000; //just in case there are touches between trials that don't get lifted before trial starts, they'll get the 1000 label
        boolean endOfBlock = GameState.incrementTrial();
        if (endOfBlock) {
            boolean endGame = GameState.endGame();
            viewGame("hide");
            viewInterblock("show");
            //go to the inter-block screen
            //then start a new trial in this new block
            if (endGame) {
                nextBlockButton.setVisibility(View.INVISIBLE);
                quitButton.setVisibility(View.VISIBLE);
            }
        } else {
            path = GameState.getCurrentPathType();
            direction = GameState.getCurrentDirection();
            startTrial(path, direction);
            //start the next trial in this block
        }
    }
    private void startTrial(final String path, final String direction){
        currentLabel = 1; //reset the touch counter
        int carrotX = 0;
        int bunnyX = 0;
        switch(direction){
            case "leftright":
                switch(path){
                    case "straight":
                        carrotX = 0;
                        bunnyX = screenWidth - bunny.getWidth();
                        path_image = path_straight_1600;
                        break;
                    case "curved":
                        carrotX = 0;
                        bunnyX = 1345;
                        path_image = path_curved_1600;
                        break;
                }
                bunny.setImageBitmap(bunny_original);
                carrot.setImageBitmap(carrot_original);
                break;
            case "rightleft":
                switch(path) {
                    case "straight":
                        carrotX = screenWidth - carrot.getWidth();
                        bunnyX = 0;
                        path_image = path_straight_1600;
                        break;
                    case "curved":
                        carrotX = 1345;
                        bunnyX = 0;
                        path_image = path_curved_1600;
                        break;
                }
                bunny.setImageBitmap(bunny_flip);
                carrot.setImageBitmap(carrot_flip);
                break;
        }

        // Set X Locations of all
        carrot.setX(carrotX);
        bunny.setX(bunnyX);
        path_image.setX(0);

        int ytop = r.nextInt(screenHeight - path_image.getHeight()) + 1;
        carrot.setY(ytop + offsetCarrotPath);
        bunny.setY(ytop + offsetBunnyPath);
        path_image.setY(ytop);

        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                StringBuilder dat = new StringBuilder();
                dat.append("START, ")
                        .append("CARROT").append(", ")
                        .append(carrot.getX() + carrot.getWidth() / 2).append(", ")
                        .append(carrot.getY() + carrot.getHeight() / 2).append(", ")
                        .append(carrot.getWidth()).append(", ")
                        .append(carrot.getHeight()).append(", ")
                        .append("BUNNY").append(", ")
                        .append((bunny.getX() + bunny.getWidth() / 2)).append(", ")
                        .append((bunny.getY() + bunny.getHeight() / 2)).append(", ")
                        .append(bunny.getWidth()).append(", ")
                        .append(bunny.getHeight()).append(", ")
                        .append(GameState.getBlockNumber()).append(", ")
                        .append(GameState.getTrialNumber()).append(", ")
                        .append(path).append(", ")
                        .append(direction);

                        //String dat = "START, " + (carrot.getX() + carrot.getWidth() / 2) + ", " + (carrot.getY() + carrot.getHeight() / 2) + ", " + carrot.getWidth() + ", " + carrot.getHeight() + ", " +
                        //(bunny.getX() + bunny.getWidth() / 2) + ", " + (bunny.getY() + bunny.getHeight() / 2) + ", " + bunny.getWidth() + ", " + bunny.getHeight() + ", " +
                        //GameState.getTrialNumber() + ", " + GameState.getBlockNumber() + ", " + path + ", " + direction;
               // Intent eventIntent = new Intent("EVENT_DATA");
               // eventIntent.putExtra("eventData", dat);
               // sendBroadcast(eventIntent);
               // Log.d(TAG, "event = " + dat);
                WriteDatToFile(File_Event, dat.toString());
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
    private void viewGuide(String showhide){
        setGuide();
        int vis = View.VISIBLE;
        if (showhide.equals("hide")){
            vis = View.INVISIBLE;
        }
        guide_straight_toright.setVisibility(View.INVISIBLE);
        guide_straight_toleft.setVisibility(View.INVISIBLE);
        guide_curve_toright.setVisibility(View.INVISIBLE);
        guide_curve_toleft.setVisibility(View.INVISIBLE);

        guide.setVisibility(vis);
        beginButton.setVisibility(vis);
    }
    private void setGuide() {
        switch (GameState.getCurrentPathType()) {
            case "straight":
                switch (GameState.getCurrentDirection()) {
                    case "leftright":
                        guide = guide_straight_toright;
                        break;
                    case "rightleft":
                        guide = guide_straight_toleft;
                        break;
                }
                break;
            case "curved":
                switch (GameState.getCurrentDirection()) {
                    case "leftright":
                        guide = guide_curve_toright;
                        break;
                    case "rightleft":
                        guide = guide_curve_toleft;
                        break;
                }
                break;
        }
    }
    private void viewInterblock(String showhide){
        int vis = View.VISIBLE;
        if (showhide.equals("hide")){
            vis = View.INVISIBLE;
        }
        viewBunnyCheerFive(showhide);
        nextBlockButton.setVisibility(vis);
        thankyoucheer.setVisibility(vis);
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

    @RequiresApi(api = Build.VERSION_CODES.Q)
    void saveSamples(MotionEvent ev, String imagetype, String actionName) throws IOException {

        final int historySize = ev.getHistorySize();
        final int pointerCount = ev.getPointerCount();
        StringBuilder sb = new StringBuilder();
        float offsetX = ev.getRawX() - ev.getX(); //historical x/y are only relative to the view, not rawx/y. So calc offsets and apply to the getHistoricalX/Y
        float offsetY = ev.getRawY() - ev.getY();
        for (int h = 0; h < historySize; h++) {
            long htime = ev.getHistoricalEventTime(h);
            for (int p = 0; p < pointerCount; p++) {
                int pointerID = ev.getPointerId(p);
                int pointerLabel = pointerLabels.get(pointerID);
                sb.append(htime).append(", ")
                        .append(actionName).append(", ")
                        .append(pointerLabel).append(", ")
                        .append(imagetype).append(", ")
                        .append(ev.getHistoricalX(p,h)+offsetX).append(", ")
                        .append(ev.getHistoricalY(p,h)+offsetY).append(", ")
                        .append(ev.getHistoricalTouchMajor(p,h)).append(", ")
                        .append(ev.getHistoricalTouchMinor(p,h)).append(", ")
                        .append(ev.getHistoricalTouchMajor(p,h)/2 * ev.getHistoricalTouchMinor(p,h)/2 * Math.PI).append(", ")
                        .append(ev.getHistoricalPressure(p,h)).append(",")
                        .append(GameState.getBlockNumber()).append(", ")
                        .append(GameState.getTrialNumber()).append(", ")
                        .append(path).append(", ")
                        .append(direction).append("\n");

            }
        }
        long evtime = ev.getEventTime();
        for (int p = 0; p < pointerCount; p++) {
            int pointerID = ev.getPointerId(p);
            int pointerLabel = pointerLabels.get(pointerID);
            sb.append(evtime).append(", ")
                    .append(actionName).append(", ")
                    .append(pointerLabel).append(", ")
                    .append(imagetype).append(", ")
                    .append(ev.getRawX(p)).append(", ")
                    .append(ev.getRawY(p)).append(", ")
                    .append(ev.getTouchMajor(p)).append(", ")
                    .append(ev.getTouchMinor(p)).append(", ")
                    .append(ev.getTouchMajor(p)/2 * ev.getTouchMinor(p)/2 * Math.PI).append(", ")
                    .append(ev.getPressure(p)).append(", ")
                    .append(GameState.getBlockNumber()).append(", ")
                    .append(GameState.getTrialNumber()).append(", ")
                    .append(path).append(", ")
                    .append(direction).append("\n");

        }
        // Write the data to the buffered output stream as bytes
        bos.write(sb.toString().getBytes());
        // Flush the buffered output stream to ensure that all data is written to the file output stream
        bos.flush();
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
        StringBuilder filename_event = new StringBuilder();
        StringBuilder filename_accel = new StringBuilder();
        StringBuilder filename_touch = new StringBuilder();
        filename_event.append(currentDateandTime)
                .append("_sub").append(participantID)
                .append("_event.csv");
        filename_accel.append(currentDateandTime)
                .append("_sub").append(participantID)
                .append("_accel.csv");
        filename_touch.append(currentDateandTime)
                .append("_sub").append(participantID)
                .append("_touch.csv");

//        String filename_event = currentDateandTime + "_sub" + participantID + "_event.csv";
//        String filename_accel = currentDateandTime + "_sub" + participantID + "_accel.csv";
//        String filename_touch = currentDateandTime + "_sub" + participantID + "_touch.csv";

        long time = System.currentTimeMillis();
        StringBuilder header_boottime = new StringBuilder();
        header_boottime.append("BOOT_TIME_EPOCH").append(time)
                .append("\nBOOT_UPTIME_MILLIS ").append(uptimeMillis());

//        String header_boottime = "BOOT_TIME_EPOCH " + time + "\nBOOT_UPTIME_MILLIS " + uptimeMillis();
        String header_time = "time";
        String header_event = "event_marker, start_item_name, start_item_center_x, start_item_center_y, start_item_width, start_item_height, " +
                "end_item_name, end_item_center_x, end_item_center_y, end_item_width, end_item_height, block_number, trial_number, path_type, path_direction";
        String header_accel = "accel_x, accel_y, accel_z"; // landscape tablet, x is along short height, y is along long width, z is through the tablet.
        String header_touch = "touch_action, touch_ID, touch_image, touch_x, touch_y, touch_max_diameter, touch_min_diameter, touch_area, touch_pressure,  block_number, trial_number, path_type, path_direction";

        StringBuilder header_eventfile = new StringBuilder();
        header_eventfile.append(header_boottime).append("\n")
                .append(header_time).append(", ")
                .append(header_event).append("\n");
        StringBuilder header_accelfile = new StringBuilder();
        header_accelfile.append(header_boottime).append("\n")
                .append(header_time).append(", ")
                .append(header_accel).append("\n");
        StringBuilder header_touchfile = new StringBuilder();
        header_touchfile.append(header_boottime).append("\n")
                .append(header_time).append(", ")
                .append(header_touch).append("\n");

//        String header_eventfile = header_boottime + "\n" + header_time + ", " + header_event + "\n";
//        String header_accelfile = header_boottime + "\n" + header_time + ", " + header_accel + "\n";
//        String header_touchfile = header_boottime + "\n" + header_time + ", " + header_touch + "\n";

        boolean goodsave = TRUE;

        if(!isExternalStorageAvailable() || isExternalStorageReadOnly())
        {
            //            startBtn.setEnabled(false);
            //            startBtn.setText("NO SAVE FILE");
            goodsave = FALSE;
        }
        else

        {
            File_Event = new File(getExternalFilesDir(filepath), filename_event.toString());
            try {
                FileOutputStream fos = new FileOutputStream(File_Event, false);
                fos.write(header_eventfile.toString().getBytes());
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
                goodsave = FALSE;
            }

            File_Accel = new File(getExternalFilesDir(filepath), filename_accel.toString());
            try {
                FileOutputStream fos = new FileOutputStream(File_Accel, false);
                fos.write(header_accelfile.toString().getBytes());
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
                goodsave = FALSE;
            }

            File_Touch = new File(getExternalFilesDir(filepath), filename_touch.toString());
            try {
                FileOutputStream fos = new FileOutputStream(File_Touch, false);
                fos.write(header_touchfile.toString().getBytes());
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

    public static Bitmap flip(Bitmap src, int type) {
        // create new matrix for transformation
        Matrix matrix = new Matrix();
        // if horizontal
        if (type == FLIP_HORIZONTAL) {
            // x = x * -1
            matrix.preScale(-1.0f, 1.0f);
        }
        // return transformed image
        return Bitmap.createBitmap(src, 0, 0, src.getWidth(), src.getHeight(), matrix, true);
    }
}