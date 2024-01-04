package com.example.smt_bunny;

import static android.os.SystemClock.uptimeMillis;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import java.util.Objects;

public class Menu extends AppCompatActivity {

    //CanvasView myCanvasView;
    ConstraintLayout myLayout;

    private Button practiceButton;
    private Button experimentButton;
    private TextView inputIDlabel;
    private TextView inputID;
    private TextView inputRunlabel;
    private TextView inputRun;
    private TextView inputTimepoint;
    private ToggleButton conditionToggle;
    private boolean conditionIsCurved = false;
    private String startingPath = "null";
    private String startingDir = "null";
    private String straightStartingDirection = "leftright";
    private String curvedStartingDirection = "leftright";
    private String participantID;
    private String participantRun;
    private String participantTimepoint;

    private TextView dirLabel;
    private TextView pathLabel;
    private RadioGroup radioGroupDir;
    private RadioGroup radioGroupPath;
    private RadioButton radioDirLtoR;
    private RadioButton radioDirRtoL;
    private RadioButton radioPathStraight;
    private RadioButton radioPathCurved;

    private boolean idComplete = false;
    private boolean practiceComplete = false;
    private boolean experimentComplete = false;
    private int taskBlockNumber = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set the layout file for this activity
        setContentView(R.layout.activity_menu);
        myLayout = findViewById(R.id.menuLayout);

        // Find your views and widgets by their IDs
        experimentButton = findViewById(R.id.experimentButton);
        practiceButton = findViewById(R.id.practiceButton);
        inputIDlabel = findViewById(R.id.inputIDlabel);
        inputID = findViewById(R.id.editTextID);
        inputRunlabel = findViewById(R.id.inputRun);
        inputRun = findViewById(R.id.editTextRun);
        inputTimepoint = findViewById(R.id.editTextTimepoint);
        conditionToggle = findViewById(R.id.conditionToggle);

        dirLabel = findViewById(R.id.dirlabel);
        radioGroupDir = findViewById(R.id.radio_group_direction);
        radioDirLtoR = findViewById(R.id.radio_direction_left_to_right);
        radioDirRtoL = findViewById(R.id.radio_direction_right_to_left);
        pathLabel = findViewById(R.id.pathlabel);
        radioGroupPath = findViewById(R.id.radio_group_path);
        radioPathStraight = findViewById(R.id.radio_path_straight);
        radioPathCurved = findViewById(R.id.radio_path_curved);

        Intent intent = getIntent();
        participantID = intent.getStringExtra("participantID");
        participantRun = intent.getStringExtra( "participantRun");
        participantTimepoint = intent.getStringExtra("participantTimepoint");
        practiceComplete = intent.getBooleanExtra("practiceComplete", false);
        idComplete = intent.getBooleanExtra("idComplete", false);
        taskBlockNumber = intent.getIntExtra("currentBlockNumber", 1);



        setButtonVisibility();

        // Set up listeners for buttons
        inputID.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    idComplete = true;
                    participantID = inputID.getText().toString();
                }
                setButtonVisibility();
                return false;
            }
        });
        // Set up listeners for buttons
        inputRun.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    participantRun = inputRun.getText().toString();

                    if((inputID.getText().toString() == "")) {
                        Log.d("buttoncheck", "its empty dude");
                    }else {
                        idComplete = true;
                        participantID = inputID.getText().toString();
                    }
                }
                setButtonVisibility();
                return false;
            }
        });
        inputTimepoint.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    participantTimepoint = inputTimepoint.getText().toString();

                    if((inputID.getText().toString() == "")) {
                        Log.d("buttoncheck", "its empty dude");
                    }else {
                        idComplete = true;
                        participantID = inputID.getText().toString();
                    }
                }
                setButtonVisibility();
                return false;
            }
        });

        practiceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // practiceComplete = true;
                // setButtonVisibility();
                // Start the practice activity
                startPracticeActivity();
            }
        });

        experimentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                experimentComplete = true;
                setButtonVisibility();
                // Start the main experiment activity
                startExperimentActivity();
            }
        });
        conditionToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                conditionIsCurved = isChecked;
            }
        });

        radioGroupPath.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // Check which radio button was selected
                switch(checkedId) {
                    case R.id.radio_path_straight:
                        // Set path variable to "straight"
                        radioPathStraight.setTextColor(Color.RED);
                        radioPathCurved.setTextColor(Color.GRAY);
                        startingPath = "straight";

                        break;
                    case R.id.radio_path_curved:
                        // Set path variable to "curved"
                        radioPathStraight.setTextColor(Color.GRAY);
                        radioPathCurved.setTextColor(Color.RED);
                        startingPath = "curved";
                        break;
                }
            }
        });
        radioGroupDir.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // Check which radio button was selected
                switch(checkedId) {
                    case R.id.radio_direction_left_to_right:
                        // Set path variable to "straight"
                        radioDirLtoR.setTextColor(Color.RED);
                        radioDirRtoL.setTextColor(Color.GRAY);
                        startingDir = "leftright";


                        break;
                    case R.id.radio_direction_right_to_left:
                        // Set path variable to "curved"
                        radioDirLtoR.setTextColor(Color.GRAY);
                        radioDirRtoL.setTextColor(Color.RED);
                        startingDir = "rightleft";
                        break;
                }
            }
        });


    }

    private void setButtonVisibility() {
        int pvis = View.INVISIBLE;
        int evis = View.INVISIBLE;
        if(idComplete){pvis = View.VISIBLE;}
        if(practiceComplete){
            evis = View.VISIBLE;
            inputID.setText(participantID);
            inputRun.setText(participantRun);
            inputTimepoint.setText(participantTimepoint);
        }

        //if ID has been entered, enable the practice
        practiceButton.setVisibility(pvis);
        //if practice complete, enable the experiment
        experimentButton.setVisibility(evis);
        radioGroupDir.setVisibility(evis);
        radioGroupPath.setVisibility(evis);
        dirLabel.setVisibility(evis);
        pathLabel.setVisibility(evis);
        //conditionToggle.setVisibility(evis);

    }

    private void startPracticeActivity() {
        Intent intent = new Intent(this, Practice.class);
        if((Objects.equals(inputRun.getText().toString(), ""))) {
            Log.d("runcheck", "run is empty, defaulting to 1");
            participantRun = "1";
        }
        if((Objects.equals(inputTimepoint.getText().toString(), ""))) {
            Log.d("runcheck", "timepoint is empty, defaulting to 1");
            participantTimepoint = "1";
        }
        intent.putExtra("participantID", participantID);
        intent.putExtra("participantRun",participantRun);
        intent.putExtra("participantTimepoint",participantTimepoint);
        startActivity(intent);
    }

    private void startExperimentActivity() {
        if(startingDir.equals("null") | startingPath.equals("null")){
            //error, one of the conditions not set
        } else {
            // Create an intent to start the main experiment activity
            Intent intent = new Intent(Menu.this, Experiment.class);

        /*
        // Get the experimental condition from the radio button group
        int conditionRadioButtonId = conditionRadioGroup.getCheckedRadioButtonId();
        String condition = "";
        switch (conditionRadioButtonId) {
            case R.id.straightRadioButton:
                condition = "straight";
                break;
            case R.id.curvedRadioButton:
                condition = "curved";
                break;
            default:
                condition = "unknown";
                break;
        }
        */


            // Put the participant ID and the experimental condition as extras in the intent
            intent.putExtra("participantID", participantID);
            intent.putExtra("participantRun", participantRun);
            intent.putExtra("participantTimepoint",participantTimepoint);
            intent.putExtra("startingPath", startingPath);
            intent.putExtra("straightDir1", startingDir);//straightStartingDirection);
            intent.putExtra("curvedDir1", startingDir);//curvedStartingDirection);
            intent.putExtra("startingBlockNumber", taskBlockNumber);

            // Start the main experiment activity
            startActivity(intent);
        }
    }
}