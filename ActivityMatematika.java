package com.example.kvizuma;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.transition.TransitionManager;

import com.google.android.material.textfield.TextInputEditText;

import java.util.List;


public class ActivityMatematika extends AppCompatActivity {
    public static int stage;
    private TextView totalPointsTextView;
    private TextView pointsAddedTextView;
    private TextView newScoreAchievementTextView;
    private TextView newScorePointsAchievedTextView;
    private TextInputEditText newScoreNameInput;
    private Button newScoreConfirmButton;
    private Button scoreBoardButton;
    private Button closeScoreBoardButton;
    private Button addition;
    private Button subtraction;
    private Button multiplication;
    private Button division;
    private Button finale;
    private ListView scoreListView;
    private ListView scoreOpListView;
    private ConstraintLayout constraintLayouts;
    private FrameLayout newScoreFrameLayout;
    private ConstraintSet applyConstraintSet = new ConstraintSet();
    private ConstraintSet resetConstraintSet = new ConstraintSet();
    private double totalScore;
    public static boolean calculationEnded = false;
    public static boolean calculationFinal;
    public static double addToScore;
    public static double scoreEarnedSinceLastTry;
    DBHelper dbHelp = new DBHelper(ActivityMatematika.this);

    public void setTotalPointsTextView(TextView totalPointsTextView) {
        this.totalPointsTextView = totalPointsTextView;
    }

    public void setPointsAddedTextView(TextView pointsAddedTextView) {
        this.pointsAddedTextView = pointsAddedTextView;
    }

    public void setScoreBoardButton(Button scoreBoardButton) {
        this.scoreBoardButton = scoreBoardButton;
    }

    public void setCloseScoreBoardButton(Button closeScoreBoardButton) {
        this.closeScoreBoardButton = closeScoreBoardButton;
    }

    public void setAddition(Button addition) {
        this.addition = addition;
    }

    public void setSubtraction(Button subtraction) {
        this.subtraction = subtraction;
    }

    public void setMultiplication(Button multiplication) {
        this.multiplication = multiplication;
    }

    public void setDivision(Button division) {
        this.division = division;
    }

    public void setFinale(Button finale) {
        this.finale = finale;
    }

    public void setScoreListView(ListView scoreListView) {
        this.scoreListView = scoreListView;
    }

    public void setScoreOpListView(ListView scoreOpListView) {
        this.scoreOpListView = scoreOpListView;
    }

    public void setNewScoreAchievementTextView(TextView newScoreAchievementTextView) {
        this.newScoreAchievementTextView = newScoreAchievementTextView;
    }

    public void setNewScorePointsAchievedTextView(TextView newScorePointsAchievedTextView) {
        this.newScorePointsAchievedTextView = newScorePointsAchievedTextView;
    }

    public void setNewScoreNameInput(TextInputEditText newScoreNameInput) {
        this.newScoreNameInput = newScoreNameInput;
    }

    public void setNewScoreConfirmButton(Button newScoreConfirmButton) {
        this.newScoreConfirmButton = newScoreConfirmButton;
    }

    public void setNewScoreFrameLayout(FrameLayout newScoreFrameLayout) {
        this.newScoreFrameLayout = newScoreFrameLayout;
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_matematika);
        setTotalPointsTextView(findViewById(R.id.textView_TotalScore));
        setAddition(findViewById(R.id.button_sabiranje));
        setSubtraction(findViewById(R.id.button_oduzimanje));
        setMultiplication(findViewById(R.id.button_množenje));
        setDivision(findViewById(R.id.button_deljenje));
        setFinale(findViewById(R.id.button_finale));
        setPointsAddedTextView(findViewById(R.id.textView_pointsAdded));
        setNewScoreAchievementTextView(findViewById(R.id.textView_achievement));
        setNewScorePointsAchievedTextView(findViewById(R.id.textView_pointsAchieved));
        setNewScoreNameInput(findViewById(R.id.enter_name_textInput));
        setNewScoreConfirmButton(findViewById(R.id.buttonConfirmInputName));
        setScoreBoardButton(findViewById(R.id.button_scoreBoard));
        setCloseScoreBoardButton(findViewById(R.id.button_closeScoreboard));
        setScoreListView(findViewById(R.id.scoreListView));
        setScoreOpListView(findViewById(R.id.scoreOPListView));
        setNewScoreFrameLayout(findViewById(R.id.frame_setRecord));
        constraintLayouts = findViewById(R.id.constraintLayout);
        applyConstraintSet.clone(constraintLayouts);
        resetConstraintSet.clone(constraintLayouts);

        totalPointsTextView.setText("Poeni: " + dbHelp.getTotalScoreFromDB(DBHelper.Disciplines.MATH));
        totalScore = dbHelp.getTotalScoreFromDB(DBHelper.Disciplines.MATH);
        findViewById(R.id.scoreFLayout).setVisibility(View.INVISIBLE);

        Button back = (Button) findViewById(R.id.button_back);
        back.setOnClickListener(view -> {
            Intent i = new Intent(view.getContext(), MainActivity.class);
            startActivity(i);
            finish();
        });
        closeScoreBoardButton.setOnClickListener(view -> {
            scoreListView.setAdapter(null);
            resetConstraintSet.applyTo(constraintLayouts);
            findViewById(R.id.scoreFLayout).setVisibility(View.INVISIBLE);
        });

        if (calculationEnded){
            calculationEnded = false;
            if(addToScore > 0){
                if(dbHelp.compareScores((int) Math.round(scoreEarnedSinceLastTry), ActivityCalculation.convertStage(stage, calculationFinal), DBHelper.Disciplines.MATH)){
                    newScoreFrameLayout.setVisibility(View.VISIBLE);

                    achievementTextSelection();

                    back.setEnabled(false);

                    newScorePointsAchievedTextView.setText(String.valueOf((int) scoreEarnedSinceLastTry));

                    newScoreConfirmButton.setOnClickListener(view -> {
                        dbHelp.setNewHighScoreInStage(ActivityCalculation.convertStage(stage, calculationFinal),
                                (int) scoreEarnedSinceLastTry,
                                String.valueOf(newScoreNameInput.getText()),
                                DBHelper.Disciplines.MATH);
                        newScoreFrameLayout.setVisibility(View.INVISIBLE);
                        newScoreNameInput.setText("");
                        back.setEnabled(true);
                        if(calculationFinal){
                            calculationFinal=false;
                            finalStageEndAndReset();
                        }
                    });
                }
                changeTotalScore();

            }

        }

        launch(addition, 1);
        launch(subtraction, 2);
        launch(multiplication, 3);
        launch(division, 4);
        launch(finale, 5);

        scoreBoardButton.setOnClickListener(view -> {
            List<RecordsSet> scores = dbHelp.getEveryoneFromScores(DBHelper.Disciplines.MATH);
            ArrayAdapter scoresAdapter = new ArrayAdapter<RecordsSet>(ActivityMatematika.this, android.R.layout.simple_list_item_1, scores);
            scoreListView.setAdapter(scoresAdapter);

            List<RecordsSet> scoresOperations = dbHelp.getEveryoneFromStage(DBHelper.Disciplines.MATH);
            scoreListView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            ArrayAdapter scoresAdapterOP = new ArrayAdapter<RecordsSet>(ActivityMatematika.this, R.layout.row, scoresOperations);
            scoreOpListView.setAdapter(scoresAdapterOP);

            findViewById(R.id.scoreFLayout).setVisibility(View.VISIBLE);

            TransitionManager.beginDelayedTransition(constraintLayouts);
            applyConstraintSet.setMargin(R.id.scoreFLayout, ConstraintSet.BOTTOM,100);
            applyConstraintSet.constrainHeight(R.id.scoreFLayout, 900);
            applyConstraintSet.applyTo(constraintLayouts);

            addition.setVisibility(View.INVISIBLE);
            subtraction.setVisibility(View.INVISIBLE);
            multiplication.setVisibility(View.INVISIBLE);
            division.setVisibility(View.INVISIBLE);
            finale.setVisibility(View.INVISIBLE);
            scoreBoardButton.setVisibility(View.INVISIBLE);
        });

            enableOrDisableAll();
    }

    private void launch(Button b, int stageNum){
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stage = stageNum;
                Intent i = new Intent(view.getContext(), ActivityCalculation.class);
                startActivity(i);
                finish();
            }
        });
    }

    private void changeTotalScore() {
        double fivePercent = addToScore / 20;
        pointsAddedTextView.setVisibility(View.VISIBLE);
        pointsAddedTextView.setText("+" + addToScore);
        totalScore = totalScore + addToScore;
        Utils.delay(1, () -> {
            CountDownTimer time = new CountDownTimer(1000, 50) {
                double tempScore = totalScore - addToScore;
                @Override
                public void onTick(long l) {
                    addToScore = addToScore - fivePercent;
                    pointsAddedTextView.setText("+" + (int)addToScore);
                    tempScore = tempScore + fivePercent;
                    totalPointsTextView.setText("Poeni: " + (int) tempScore);
                }

                @Override
                public void onFinish() {
                    totalPointsTextView.setText("Poeni: " + (int) totalScore);
                    pointsAddedTextView.setVisibility(View.INVISIBLE);
                    dbHelp.setNewTotalScoreAtDB((int) totalScore, DBHelper.Disciplines.MATH);
                    addToScore = 0;
                }
            }.start();
        });
    }

    private void achievementTextSelection(){
        if(!calculationFinal) {
            switch (stage) {
                case 1:
                    newScoreAchievementTextView.setText("Postavili ste novi rekord u sabiranju!");
                    break;
                case 2:
                    newScoreAchievementTextView.setText("Postavili ste novi rekord u oduzimanju!");
                    break;
                case 3:
                    newScoreAchievementTextView.setText("Postavili ste novi rekord u množenju!");
                    break;
                case 4:
                    newScoreAchievementTextView.setText("Postavili ste novi rekord u deljenju!");
                    break;
            }
        }else newScoreAchievementTextView.setText("Postavili ste novi rekord u finalnom nivou!");
    }

    private void finalStageEndAndReset(){
        newScoreFrameLayout.setVisibility(View.VISIBLE);
        newScoreAchievementTextView.setText("Prošli ste konačni nivo! Upišite vaš rekord!");
        newScorePointsAchievedTextView.setText(String.valueOf((int) totalScore));
        newScoreConfirmButton.setOnClickListener(view -> {
            RecordsSet newSet = new RecordsSet();
            newSet.setName(String.valueOf(newScoreNameInput.getText()));
            newSet.setScore((int) totalScore);
            dbHelp.addNewRecord(newSet, DBHelper.Disciplines.MATH);
            dbHelp.setNewTotalScoreAtDB(0, DBHelper.Disciplines.MATH);
            dbHelp.resetCurrentScore(DBHelper.Disciplines.MATH);
            dbHelp.setStageAvailabilityInDB(DBHelper.Operations.SUBTRACTION, false, DBHelper.Disciplines.MATH);
            dbHelp.setStageAvailabilityInDB(DBHelper.Operations.MULTIPLICATION, false, DBHelper.Disciplines.MATH);
            dbHelp.setStageAvailabilityInDB(DBHelper.Operations.DIVISION, false, DBHelper.Disciplines.MATH);
            dbHelp.setStageAvailabilityInDB(DBHelper.Operations.FINAL_MATH, false, DBHelper.Disciplines.MATH);
            totalScore = 0;
            totalPointsTextView.setText("Poeni: " + 0);
            newScoreFrameLayout.setVisibility(View.INVISIBLE);
            enableOrDisableAll();
        });

    }

    private void enableOrDisableButton(Button b, boolean availability){
        if (availability){
            b.setEnabled(true);
            b.setBackgroundColor(Color.parseColor("#FF6200EE"));
        }else{
            b.setEnabled(false);
            b.setBackgroundColor(Color.GRAY);
        }
    }

    private void enableOrDisableAll(){
        enableOrDisableButton(subtraction, dbHelp.getStageAvailabilityInDB(DBHelper.Operations.SUBTRACTION, DBHelper.Disciplines.MATH));
        enableOrDisableButton(multiplication, dbHelp.getStageAvailabilityInDB(DBHelper.Operations.MULTIPLICATION, DBHelper.Disciplines.MATH));
        enableOrDisableButton(division, dbHelp.getStageAvailabilityInDB(DBHelper.Operations.DIVISION, DBHelper.Disciplines.MATH));
        enableOrDisableButton(finale, dbHelp.getStageAvailabilityInDB(DBHelper.Operations.FINAL_MATH, DBHelper.Disciplines.MATH));
    }


}