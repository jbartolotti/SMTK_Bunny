package com.example.smt_bunny;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class Practice extends AppCompatActivity {

    private String participantID;

    ConstraintLayout myLayout;

    private ImageView TitleLearnWhich;
    private ImageView TitleChooseWhich;
    private ImageView TitlePressCarrot;
    private TextView which_text;

    private Button finishedButton;
    private Button nextButton;

    private ImageView bunny_brown;
    private ImageView bunny_white;
    private ImageView bunny;
    private ImageView carrot_default;
    private ImageView carrot;

    private ImageView target_circle;

    private String practiceState;
    private Integer practiceTrial;
    // Define some fields for your views and widgets
    //private CanvasView canvasView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        practiceState = "chooseWhich";
        practiceTrial = 1;

        // Set the layout file for this activity
        setContentView(R.layout.activity_practice);
        myLayout = findViewById(R.id.practiceLayout);

        finishedButton = findViewById(R.id.finished_button);
        nextButton = findViewById(R.id.next_button);

        bunny_brown = findViewById(R.id.bunny_brown);
        carrot_default = findViewById(R.id.carrot_default);
        target_circle = findViewById(R.id.target_circle);

        which_text = findViewById(R.id.which_text);
        which_text.setText("Target Circle?");

        // Find your views and widgets by their IDs
        //   canvasView = findViewById(R.id.canvasView);

        // Get the participant ID from the intent that started this activity
        Intent intent = getIntent();
        participantID = intent.getStringExtra("participantID");


        setObjectVisibility();

        finishedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start the practice activity
                returnMenuActivity();
            }
        });
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (practiceState) {
                    case "chooseWhich":
                        practiceTrial = practiceTrial + 1;
                        switch (practiceTrial){
                            case 2:
                                which_text.setText("Carrot?");
                                break;
                            case 3:
                                which_text.setText("Bunny?");
                                break;
                            case 4:
                                practiceTrial = 1;
                                practiceState = "pressCarrot";
                                break;
                        }
                        break;
                    case "pressCarrot":
                        which_text.setText("testing");
                        break;


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

    private void setObjectVisibility() {



    }



}