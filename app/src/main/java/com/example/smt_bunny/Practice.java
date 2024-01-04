package com.example.smt_bunny;

import static android.os.SystemClock.uptimeMillis;
import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.util.Log;
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

public class Practice extends AppCompatActivity implements View.OnTouchListener {

    private String participantID;
    private String participantRun;
    private String participantTimepoint;

    ConstraintLayout myLayout;

    private File File_Practice_Event;
    private File File_Practice_Touch;
    private BufferedOutputStream bos;
    private boolean can_record;

    private ImageView TitleLearnWhich;
    private ImageView TitleChooseWhich;
    private ImageView TitlePressCarrot;
    private ImageView TitleCarrotAccuracy;
    private ImageView TitleCarrotSpeed;
    private TextView which_text;

    private Button finishedButton;
    private Button nextButton;
    private Button resetButton;
    private Button startButton;

    private ImageView bunny_brown;
    private ImageView bunny_white;
    private ImageView bunny;
    private ImageView carrot_default;
    private ImageView carrot;
    private ImageView activeObj;

    private ImageView bunny_brown3;
    private ImageView bunny3;
    private ImageView carrot_default1;
    private ImageView carrot1;
    private ImageView carrot_default2;
    private ImageView carrot2;
    private ImageView carrot_default3;
    private ImageView carrot3;
    private ImageView target_circle1;
    private ImageView target_circle1_white;
    private ImageView target_circle2;
    private ImageView target_circle2_white;
    private ImageView target_circle3;
    private ImageView target_circle3_white;
    private ImageView whiteCirc;

    private ImageView path_diagonal_1600;
    private ImageView blank_object;
    private float carrotOffsetX;
    private float carrotOffsetY;
    private boolean carrotMove = FALSE;
    private boolean enableDrag = FALSE;

    private String practiceState;
    private Integer practiceTrial;
    private float holdTime = 0;
    private float touch_start_time = 0;

    private Integer bunny_align_top = 10;
    private Integer bunny_align_bottom = 10;
    private Integer bunny_align_left = 10;
    private Integer bunny_align_right = -40;

    private Integer carrot_align_top = 0;
    private Integer carrot_align_bottom = 0;
    private Integer carrot_align_left = 0;
    private Integer carrot_align_right = -40;

    //private CanvasView canvasView;

    private int screenHeight = Resources.getSystem().getDisplayMetrics().heightPixels;
    private int screenWidth = Resources.getSystem().getDisplayMetrics().widthPixels;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        practiceState = "learnWhich";
        practiceTrial = 0;


        // Set the layout file for this activity
        setContentView(R.layout.activity_practice);
        myLayout = findViewById(R.id.practiceLayout);



        // buttons
        finishedButton = findViewById(R.id.finished_button); // return to main menu
        nextButton = findViewById(R.id.next_button); // next trial
        resetButton = findViewById(R.id.reset_button); // reset current task to trial 1
        startButton = findViewById(R.id.start_button); // begin current task

        // task objects
        carrot_default = findViewById(R.id.carrot_default);
        bunny_brown = findViewById(R.id.bunny_brown);

        // 300square objects
        bunny_brown3 = findViewById(R.id.bunny_brown3);
        carrot_default1 = findViewById(R.id.carrot_default1);
        carrot_default2 = findViewById(R.id.carrot_default2);
        carrot_default3 = findViewById(R.id.carrot_default3);
        target_circle1 = findViewById(R.id.target_circle1);
        target_circle1_white = findViewById(R.id.target_circle1_white);
        target_circle2 = findViewById(R.id.target_circle2);
        target_circle3 = findViewById(R.id.target_circle3);
        target_circle2_white = findViewById(R.id.target_circle2_white);
        target_circle3_white = findViewById(R.id.target_circle3_white);

        bunny = bunny_brown;
        carrot = carrot_default;

        bunny3 = bunny_brown3;
        carrot1 = carrot_default1;
        carrot2 = carrot_default2;
        carrot3 = carrot_default3;

        // display text
        which_text = findViewById(R.id.which_text);



        //instruction panels
        TitleLearnWhich = findViewById(R.id.title_learn_which);
        TitleChooseWhich = findViewById(R.id.title_choose_which);
        TitlePressCarrot = findViewById(R.id.title_press_carrot);
        TitleCarrotAccuracy = findViewById(R.id.title_drag_carrot_accuracy);
        TitleCarrotSpeed = findViewById(R.id.title_drag_carrot_speed);

        // other
        path_diagonal_1600 = findViewById(R.id.path_diagonal_1600);
        blank_object = findViewById(R.id.blank_object);
        activeObj = blank_object;

        // Find your views and widgets by their IDs
        //   canvasView = findViewById(R.id.canvasView);

        // Get the participant ID from the intent that started this activity
        Intent intent = getIntent();
        participantID = intent.getStringExtra("participantID");
        participantRun = intent.getStringExtra("participantRun");
        participantTimepoint = intent.getStringExtra("participantTimepoint");

        // the three carrots that get pressed in the "Press Carrot" task
        carrot1.setOnTouchListener(this);
        carrot2.setOnTouchListener(this);
        carrot3.setOnTouchListener(this);

        // the carrot that gets dragged along the path to the bunny
        carrot.setOnTouchListener(this);

        setObjectVisibility();

        finishedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start the practice activity
                returnMenuActivity();
            }
        });




        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (practiceState){
                    case "learnWhich":
                    case "chooseWhich":
                        practiceTrial = 1;
                        nextButton.setVisibility(View.VISIBLE);
                        startButton.setVisibility(View.INVISIBLE);
                        setObjectLayout();
                        which_text.setVisibility(View.VISIBLE);
                        break;
                    case "pressCarrot":
                        practiceTrial = 1;
                        startButton.setVisibility(View.INVISIBLE);
                        setObjectLayout();
                        nextButton.setVisibility(View.VISIBLE);
                        break;
                    case "dragCarrotAcc":
                    case "dragCarrotSpeed":
                        practiceTrial = 1;
                        enableDrag = TRUE;
                        startButton.setVisibility(View.INVISIBLE);
                        setObjectLayout();
                        break;

                }

            }

        });

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newState = null;
                Integer newTrial = null;
                Boolean updateStateTrial = Boolean.FALSE;
                switch (practiceState) {
                    case "learnWhich":
                        // Advance to the next trial and put the text box in the right spot.
                        // If we finished the last trial, then update to the chooseWhich task.
                        practiceTrial = practiceTrial + 1;
                        setObjectLayout();
                        if (practiceTrial == 4){
                            newState = "chooseWhich";
                            newTrial = 0;
                            updateStateTrial = Boolean.TRUE;
                        }
                        break;
                    case "chooseWhich":
                        practiceTrial = practiceTrial + 1;
                        setObjectLayout();
                        if (practiceTrial == 4){
                            newState = "pressCarrot";
                            newTrial = 0;
                            updateStateTrial = Boolean.TRUE;
                        }
                        break;
                    case "pressCarrot":
                        which_text.setText("testing");
                        practiceTrial = practiceTrial + 1;
                        setObjectLayout();
                        break;


                }
                if (updateStateTrial){
                    practiceTrial = newTrial;
                    practiceState = newState;
                    setObjectVisibility();
                }
            }
        });

        can_record = initializeSaveFile(participantID);
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(File_Practice_Touch, true);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        // Create a buffered output stream with a buffer size of 1024 bytes
        bos = new BufferedOutputStream(fos, 1024);
    }

    private void returnMenuActivity() {
        Intent intent = new Intent(this, Menu.class);
        intent.putExtra("participantID", participantID);
        intent.putExtra("participantRun",participantRun);
        intent.putExtra("participantTimepoint",participantTimepoint);
        intent.putExtra("practiceComplete", true);
        intent.putExtra("idComplete",true);
        startActivity(intent);
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    public boolean onTouch (View v, MotionEvent event){
        String printState = practiceState;
        int printTrial = practiceTrial;
        switch (practiceState){
            case "pressCarrot" :

                if (v == activeObj ){
                    String actionName = null;
                    int action = event.getActionMasked();
//                    int pointerIndex = event.getActionIndex();
//                    int pointerID = event.getPointerId(pointerIndex);
                    Log.d ("pressCarrot", "holdTime "+ (uptimeMillis() - holdTime));

                    switch (action){
                        // At beginning of touch, record the system time to holdTime.
                        // Then, when the touch ends, if more than 1000 ms has elapsed since the start
                        // of the touch, go to the next trial.
                        // Whenever a touch ends, reset the hold timer so that it can get initiated
                        // on the next press.
                        case MotionEvent.ACTION_DOWN:
                            actionName = "TOUCH_START";
                            if (holdTime == 0) {
                                holdTime = uptimeMillis();
                                touch_start_time = uptimeMillis();
                            }
                            break;
                        case MotionEvent.ACTION_UP:
                            actionName = "TOUCH_END";
                            if (uptimeMillis() - holdTime > 1000){
                                practiceTrial = practiceTrial + 1;
                                if(practiceTrial == 4) {
                                    practiceState = "dragCarrotAcc";
                                    practiceTrial = 0;
                                    setObjectVisibility();
                                    enableDrag = FALSE;
                                }
                                setObjectLayout();
                            }
                            holdTime = 0;

                            break;
                        default:
                            if(uptimeMillis() - holdTime > 1000){
                                whiteCirc.setVisibility(View.VISIBLE);
                            }
                            actionName = "TOUCH_HOLD";
                            break;

                    }
//                    StringBuilder ddat = new StringBuilder();
//                    ddat.append(uptimeMillis()).append("-").append(holdDur).append("-").append(holdTime);
                    try {
                        saveSamples(event, "carrot", actionName, "none", "none", printState, printTrial);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return true; // the touch event was consumed
                }
                return false; // the touch was not consumed and is still live
            case "dragCarrotAcc":
            case "dragCarrotSpeed":
                if (v == carrot && enableDrag){
                    String actionName = null;
                    String thisdir = "";
                    if(practiceTrial == 1){
                        thisdir = "leftright";
                    } else{
                        thisdir = "rightleft";
                    }
                    int action = event.getActionMasked();
                    switch (action) { //get the action of the touch event
                        case MotionEvent.ACTION_DOWN: //the user first touches the view
                            actionName = "TOUCH_START";
                            carrotOffsetX = event.getRawX() - carrot.getX();
                            carrotOffsetY = event.getRawY() - carrot.getY();
                            carrotMove = carrotOffsetX >= 0 & carrotOffsetX <= carrot.getWidth() & carrotOffsetY >= 0 & carrotOffsetY <= carrot.getHeight();
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
                                practiceTrial = practiceTrial + 1;
                                if(practiceTrial > 2){
                                    switch (practiceState){
                                        case "dragCarrotAcc":
                                            practiceTrial = 0;
                                            practiceState = "dragCarrotSpeed";
                                            setObjectVisibility();
                                            setObjectLayout();
                                            enableDrag = FALSE;
                                            break;
                                        case "dragCarrotSpeed":
                                            practiceTrial = 0;
                                            practiceState = "end";
                                            setObjectVisibility();
                                            setObjectLayout();
                                            break;

                                    }
                                }

                                setObjectLayout();
                            }
                            carrot.invalidate();
                            break;

                    }
                    try {
                        saveSamples(event, "carrot", actionName, "diagonal", thisdir, printState, printTrial);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return true; // the touch event was consumed
                }
                return false; // the touch was not consumed and is still live
            default:
                return false; // touch was not in an active task, and was not consumed




        }


    }

    private void setObjectLayout(){
        String targetText = null;
        ImageView targetObj = null;
        ImageView prevObj = null;
        ImageView prevObjWhite = null;
        boolean write_event = FALSE;

        switch (practiceState){
            case "learnWhich":
                switch (practiceTrial) {
                    case 1:
                        targetObj = target_circle1;
                        targetText = "This is the \nTarget Circle";
                        write_event = TRUE;
                        break;
                    case 2:
                        targetObj = carrot2;
                        targetText = "This is the \nCarrot";
                        write_event = TRUE;
                        break;
                    case 3:
                        targetObj = bunny3;
                        targetText = "This is the \nBunny";
                        write_event = TRUE;
                        break;
                    default:
                        targetObj = target_circle1;
                        targetText = "";
                        break;
                }
                which_text.setText(targetText);
                which_text.setWidth(targetObj.getWidth());
                which_text.setX(targetObj.getX());
                which_text.setY(targetObj.getY() +targetObj.getHeight());
                break;
            case "chooseWhich":
                switch (practiceTrial) {
                    case 2:
                        targetText = "Which is the Target Circle?";
                        write_event = TRUE;
                        break;
                    case 1:
                        targetText = "Which is the Carrot?";
                        write_event = TRUE;
                        break;
                    case 3:
                        targetText = "Which is the Bunny?";
                        write_event = TRUE;
                        break;
                    default:
                        targetText = "";
                        break;

                }
                which_text.setText(targetText);
                which_text.setX(screenWidth/2 - which_text.getWidth()/2);
                which_text.setY(TitleChooseWhich.getY() + TitleChooseWhich.getHeight());

                break;
            case "pressCarrot":
                switch (practiceTrial) {
                    case 1:
                        prevObj = target_circle3;
                        prevObjWhite = target_circle3_white;
                        targetObj = target_circle1;
                        activeObj = carrot1;
                        whiteCirc = target_circle1_white;
                        write_event = TRUE;
                        break;
                    case 2:
                        targetObj = target_circle2;
                        prevObj = target_circle1;
                        prevObjWhite = target_circle1_white;
                        activeObj = carrot2;
                        whiteCirc = target_circle2_white;
                        write_event = TRUE;
                        break;
                    case 3:
                        targetObj = target_circle3;
                        prevObj = target_circle2;
                        prevObjWhite = target_circle2_white;
                        activeObj = carrot3;
                        whiteCirc = target_circle3_white;
                        write_event = TRUE;
                        break;
                    default:
                        targetObj = null;
                        prevObj = target_circle3;
                        prevObjWhite = target_circle3_white;
                        whiteCirc = target_circle3_white;
                        write_event = TRUE;
                        break;
                }
                targetObj.setVisibility(View.VISIBLE);
                if(prevObj != null) {
                    prevObj.setVisibility(View.INVISIBLE);
                    prevObjWhite.setVisibility(View.INVISIBLE);

                }
                Log.d("pressCarrot", "pressed at " + activeObj.getX());
                break;


            case "dragCarrotAcc":
            case "dragCarrotSpeed":

                switch (practiceTrial) {
                case 0:
                case 1:
                    carrot.setVisibility(View.VISIBLE);
                    bunny.setVisibility(View.VISIBLE);
                    path_diagonal_1600.setVisibility(View.VISIBLE);
                    //drag carrot bottomleft to topright
                    //carrot: align start to start, and bottom to bottom
                    carrot.setX(path_diagonal_1600.getX() + carrot_align_left);
                    carrot.setY(path_diagonal_1600.getY() + path_diagonal_1600.getHeight() - carrot.getHeight() + carrot_align_bottom);
                    //bunny: align end to end, and top to top
                    bunny.setX(path_diagonal_1600.getX()+path_diagonal_1600.getWidth() - bunny.getWidth() + bunny_align_right);
                    bunny.setY(path_diagonal_1600.getY()+bunny_align_top);
                    write_event = TRUE;
                    break;
                case 2:

                    //drag carrot topright to bottomleft
                    //carrot: align end to end, and top to top
                    carrot.setX(path_diagonal_1600.getX() + path_diagonal_1600.getWidth() - carrot.getWidth() + carrot_align_right);
                    carrot.setY(path_diagonal_1600.getY() + carrot_align_top);
                    //bunny: align start to start, and bottom to bottom
                    bunny.setX(path_diagonal_1600.getX()+bunny_align_left);
                    bunny.setY(path_diagonal_1600.getY()+path_diagonal_1600.getHeight() - bunny.getHeight() + bunny_align_bottom);
                    write_event = TRUE;
                    break;
                default:
                    break;

            }

        }
        if(write_event) {
            // Time, Sub, State, Trial
            StringBuilder dat = new StringBuilder();
            dat.append(participantID).append(", ")
                    .append(practiceState).append(", ")
                    .append(practiceTrial);
            WriteDatToFile(File_Practice_Event, dat.toString());
        }

    }



    private void setObjectVisibility() {
        hideAll();
        int vis = View.VISIBLE;
        switch (practiceState) {
            case "learnWhich":
                TitleLearnWhich.setVisibility(vis);
                bunny3.setVisibility(vis);
                carrot2.setVisibility(vis);
                target_circle1.setVisibility(vis);
                startButton.setVisibility(vis);
                break;
            case "chooseWhich":
                TitleChooseWhich.setVisibility(vis);
                which_text.setVisibility(vis);
                bunny3.setVisibility(vis);
                carrot2.setVisibility(vis);
                target_circle1.setVisibility(vis);
                startButton.setVisibility(vis);
                break;
            case "pressCarrot":
                TitlePressCarrot.setVisibility(vis);
                carrot1.setVisibility(vis);
                carrot2.setVisibility(vis);
                carrot3.setVisibility(vis);
                startButton.setVisibility(vis);
                break;
            case "dragCarrotAcc":
                TitleCarrotAccuracy.setVisibility(vis);
                startButton.setVisibility(vis);
                bunny.setVisibility(vis);
                carrot.setVisibility(vis);
                path_diagonal_1600.setVisibility(vis);
                break;
            case "dragCarrotSpeed":
                TitleCarrotSpeed.setVisibility(vis);
                startButton.setVisibility(vis);
                bunny.setVisibility(vis);
                carrot.setVisibility(vis);
                path_diagonal_1600.setVisibility(vis);
                break;
            case "end":
                finishedButton.setVisibility(vis);
                break;


        }
    }

    private void hideAll() {
        int vis = View.INVISIBLE;

        TitleLearnWhich.setVisibility(vis);
        TitleChooseWhich.setVisibility(vis);
        TitlePressCarrot.setVisibility(vis);
        TitleCarrotAccuracy.setVisibility(vis);
        TitleCarrotSpeed.setVisibility(vis);
        which_text.setVisibility(vis);

        finishedButton.setVisibility(vis);
        nextButton.setVisibility(vis);
        resetButton.setVisibility(vis);

        bunny.setVisibility(vis);
        carrot.setVisibility(vis);

        bunny3.setVisibility(vis);
        carrot1.setVisibility(vis);
        carrot2.setVisibility(vis);
        carrot3.setVisibility(vis);
        target_circle1.setVisibility(vis);
        target_circle1_white.setVisibility(vis);
        target_circle2.setVisibility(vis);
        target_circle2_white.setVisibility(vis);
        target_circle3.setVisibility(vis);
        target_circle3_white.setVisibility(vis);

        path_diagonal_1600.setVisibility(vis);
        blank_object.setVisibility(vis);
    }


    @RequiresApi(api = Build.VERSION_CODES.Q)
    void saveSamples(MotionEvent ev, String imagetype, String actionName, String path, String direction, String printState, int printTrial) throws IOException {

        final int historySize = ev.getHistorySize();
        final int pointerCount = ev.getPointerCount();
        StringBuilder sb = new StringBuilder();
        float offsetX = ev.getRawX() - ev.getX(); //historical x/y are only relative to the view, not rawx/y. So calc offsets and apply to the getHistoricalX/Y
        float offsetY = ev.getRawY() - ev.getY();
        for (int h = 0; h < historySize; h++) {
            long htime = ev.getHistoricalEventTime(h);
            for (int p = 0; p < pointerCount; p++) {
                int pointerID = ev.getPointerId(p);
                int pointerLabel = 0; //pointerLabels.get(pointerID);
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
                        .append(printState).append(", ")
                        .append(printTrial).append(", ")
                        .append(path).append(", ")
                        .append(direction).append("\n");

            }
        }
        long evtime = ev.getEventTime();
        for (int p = 0; p < pointerCount; p++) {
            int pointerID = ev.getPointerId(p);
            int pointerLabel = 0; //pointerLabels.get(pointerID);
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
                    .append(printState).append(", ")
                    .append(printTrial).append(", ")
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

    private boolean initializeSaveFile(String participantID) {
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
                .append("_Practice_sub").append(participantID)
                .append("_").append(participantTimepoint)
                .append("x").append(participantRun)
                .append("_event.csv");
//        filename_accel.append(currentDateandTime)
//                .append("_Practice_sub").append(participantID)
//                .append("_accel.csv");
        filename_touch.append(currentDateandTime)
                .append("_Practice_sub").append(participantID)
                .append("_").append(participantTimepoint)
                .append("x").append(participantRun)
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
        String header_event = "participant_id, practice_block, practice_trial";
        String header_touch = "touch_action, touch_ID, touch_image, touch_x, touch_y, touch_max_diameter, touch_min_diameter, touch_area, touch_pressure,  block_number, trial_number, path_type, path_direction, hold_duration";

        StringBuilder header_eventfile = new StringBuilder();
        header_eventfile.append(header_boottime).append("\n")
                .append(header_time).append(", ")
                .append(header_event).append("\n");
        StringBuilder header_touchfile = new StringBuilder();
        header_touchfile.append(header_boottime).append("\n")
                .append(header_time).append(", ")
                .append(header_touch).append("\n");

//        String header_eventfile = header_boottime + "\n" + header_time + ", " + header_event + "\n";
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
            File_Practice_Event = new File(getExternalFilesDir(filepath), filename_event.toString());
            try {
                FileOutputStream fos = new FileOutputStream(File_Practice_Event, false);
                fos.write(header_eventfile.toString().getBytes());
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
                goodsave = FALSE;
            }


            File_Practice_Touch = new File(getExternalFilesDir(filepath), filename_touch.toString());
            try {
                FileOutputStream fos = new FileOutputStream(File_Practice_Touch, false);
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


}