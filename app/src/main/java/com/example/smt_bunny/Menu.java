package com.example.smt_bunny;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

public class Menu extends AppCompatActivity {

    //CanvasView myCanvasView;
    ConstraintLayout myLayout;

    private Button practiceButton;
    private Button experimentButton;
    private TextView inputIDlabel;
    private TextView inputID;
    private ToggleButton conditionToggle;
    private boolean conditionIsCurved = false;
    private String straightStartingDirection = "leftright";
    private String curvedStartingDirection = "leftright";
    private String participantID;

    private boolean idComplete = false;
    private boolean practiceComplete = false;
    private boolean experimentComplete = false;

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
        conditionToggle = findViewById(R.id.conditionToggle);
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

        practiceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                practiceComplete = true;
                setButtonVisibility();
                // Start the practice activity
                //startPracticeActivity();
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
    }

    private void setButtonVisibility() {
        int pvis = View.INVISIBLE;
        int evis = View.INVISIBLE;
        if(idComplete){pvis = View.VISIBLE;}
        if(practiceComplete){evis = View.VISIBLE;}

        //if ID has been entered, enable the practice
        practiceButton.setVisibility(pvis);
        //if practice complete, enable the experiment
        experimentButton.setVisibility(evis);
        conditionToggle.setVisibility(evis);

    }

    private void startPracticeActivity() {
        Intent intent = new Intent(this, Practice.class);
        startActivity(intent);
    }

    private void startExperimentActivity() {
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
        String condition = "";
        if (conditionIsCurved) {
            condition = "curved";
        } else {
            condition = "straight";
        }


        // Put the participant ID and the experimental condition as extras in the intent
        intent.putExtra("participantID", participantID);
        intent.putExtra("condition", condition);
        intent.putExtra("straightDir1", straightStartingDirection);
        intent.putExtra("curvedDir1", curvedStartingDirection);

        // Start the main experiment activity
        startActivity(intent);
    }
}