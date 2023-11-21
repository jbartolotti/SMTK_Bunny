package com.example.smt_bunny;

import static java.lang.Boolean.TRUE;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class Practice extends AppCompatActivity implements View.OnTouchListener {

    private String participantID;

    ConstraintLayout myLayout;

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
    private ImageView target_circle2;
    private ImageView target_circle3;

    private ImageView path_diagonal_1600;
    private ImageView blank_object;
    private float carrotOffsetX;
    private float carrotOffsetY;
    private boolean carrotMove = false;

    private String practiceState;
    private Integer practiceTrial;
    private float holdTime = 0;

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
        target_circle2 = findViewById(R.id.target_circle2);
        target_circle3 = findViewById(R.id.target_circle3);

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

    }

    private void returnMenuActivity() {
        Intent intent = new Intent(this, Menu.class);
        intent.putExtra("participantID", participantID);
        intent.putExtra("practiceComplete", true);
        intent.putExtra("idComplete",true);
        startActivity(intent);
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    public boolean onTouch (View v, MotionEvent event){
        switch (practiceState){
            case "pressCarrot" :

                if (v == activeObj){
                    int action = event.getActionMasked();
//                    int pointerIndex = event.getActionIndex();
//                    int pointerID = event.getPointerId(pointerIndex);
                    Log.d ("pressCarrot", "holdTime "+ (SystemClock.uptimeMillis() - holdTime));

                    switch (action){
                        // At beginning of touch, record the system time to holdTime.
                        // Then, when the touch ends, if more than 1000 ms has elapsed since the start
                        // of the touch, go to the next trial.
                        // Whenever a touch ends, reset the hold timer so that it can get initiated
                        // on the next press.
                        case MotionEvent.ACTION_DOWN:
                            if (holdTime == 0) {
                                holdTime = SystemClock.uptimeMillis();
                            }
                            break;
                        case MotionEvent.ACTION_UP:
                            if (SystemClock.uptimeMillis() - holdTime > 1000){
                                practiceTrial = practiceTrial + 1;
                                if(practiceTrial == 4) {
                                    practiceState = "dragCarrotAcc";
                                    practiceTrial = 0;
                                    setObjectVisibility();
                                }
                                setObjectLayout();
                            }
                            holdTime = 0;

                            break;

                    }
                    return true; // the touch event was consumed
                }
                return false; // the touch was not consumed and is still live
            case "dragCarrotAcc":
            case "dragCarrotSpeed":
                if (v == carrot){
                    int action = event.getActionMasked();
                    switch (action) { //get the action of the touch event
                        case MotionEvent.ACTION_DOWN: //the user first touches the view
                            carrotOffsetX = event.getRawX() - carrot.getX();
                            carrotOffsetY = event.getRawY() - carrot.getY();
                            carrotMove = carrotOffsetX >= 0 & carrotOffsetX <= carrot.getWidth() & carrotOffsetY >= 0 & carrotOffsetY <= carrot.getHeight();
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
                                practiceTrial = practiceTrial + 1;
                                if(practiceTrial > 2){
                                    switch (practiceState){
                                        case "dragCarrotAcc":
                                            practiceTrial = 0;
                                            practiceState = "dragCarrotSpeed";
                                            setObjectVisibility();
                                            break;
                                        case "dragCarrotSpeed":
                                            practiceTrial = 0;
                                            practiceState = "end";
                                            setObjectVisibility();
                                            break;

                                    }
                                }

                                setObjectLayout();
                            }
                            carrot.invalidate();
                            break;

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

        switch (practiceState){
            case "learnWhich":
                switch (practiceTrial) {
                    case 1:
                        targetObj = target_circle1;
                        targetText = "This is the \nTarget Circle";
                        break;
                    case 2:
                        targetObj = carrot2;
                        targetText = "This is the \nCarrot";
                        break;
                    case 3:
                        targetObj = bunny3;
                        targetText = "This is the \nBunny";
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
                    case 1:
                        targetText = "Which is the Target Circle?";
                        break;
                    case 2:
                        targetText = "Which is the Carrot?";
                        break;
                    case 3:
                        targetText = "Which is the Bunny?";
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
                        targetObj = target_circle1;
                        activeObj = carrot1;
                        break;
                    case 2:
                        targetObj = target_circle2;
                        prevObj = target_circle1;
                        activeObj = carrot2;
                        break;
                    case 3:
                        targetObj = target_circle3;
                        prevObj = target_circle2;
                        activeObj = carrot3;
                        break;
                    default:
                        targetObj = null;
                        prevObj = target_circle3;
                        break;
                }
                targetObj.setVisibility(View.VISIBLE);
                if(prevObj != null) {
                    prevObj.setVisibility(View.INVISIBLE);
                }
                Log.d("pressCarrot", "pressed at " + activeObj.getX());
                break;


            case "dragCarrotAcc":
            case "dragCarrotSpeed":

                switch (practiceTrial) {
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
                    break;
                case 2:

                    //drag carrot topright to bottomleft
                    //carrot: align end to end, and top to top
                    carrot.setX(path_diagonal_1600.getX() + path_diagonal_1600.getWidth() - carrot.getWidth() + carrot_align_right);
                    carrot.setY(path_diagonal_1600.getY() + carrot_align_top);
                    //bunny: align start to start, and bottom to bottom
                    bunny.setX(path_diagonal_1600.getX()+bunny_align_left);
                    bunny.setY(path_diagonal_1600.getY()+path_diagonal_1600.getHeight() - bunny.getHeight() + bunny_align_bottom);

                    break;
                default:
                    break;

            }

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
                break;
            case "dragCarrotSpeed":
                TitleCarrotSpeed.setVisibility(vis);
                startButton.setVisibility(vis);
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
        target_circle2.setVisibility(vis);
        target_circle3.setVisibility(vis);

        path_diagonal_1600.setVisibility(vis);
        blank_object.setVisibility(vis);
    }



}