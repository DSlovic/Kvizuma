package com.example.kvizuma;

import static com.example.kvizuma.MainActivity.continentList;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
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

public class ActivityGeography extends AppCompatActivity {

    DBHelper dbHelp = new DBHelper(ActivityGeography.this);
    private Button flags;
    private Button borders;
    private Button capitols;
    private Button finale;
    private Button records;
    private Button closeScoreBoardButton;
    private Button back;
    private TextView totalScoreTextView;
    private TextView pointsAddedTextView;
    private ListView scoreListViewGeo;
    private ListView scoreOpListViewGeo;
    private ConstraintLayout constraintLayouts;
    private ConstraintSet applyConstraintSet = new ConstraintSet();
    private ConstraintSet resetConstraintSet = new ConstraintSet();
    private int totalScore;
    private TextView newScoreAchievementTextView;
    private TextView newScorePointsAchievedTextView;
    private TextInputEditText newScoreNameInput;
    private Button newScoreConfirmButton;
    private ImageView imageEuro, imageAsia, imageAfrica, imageNAmerica, imageSAmerica, imageAustralia;
    public static int geoStage;
    public static double scoreDifference;
    public static double scoreEarnedSinceLastTryGeo;

    public void setFlags(Button flags) {
        this.flags = flags;
    }

    public void setBorders(Button borders) {
        this.borders = borders;
    }

    public void setFinale(Button finale) {
        this.finale = finale;
    }

    public void setCapitols(Button capitols) {
        this.capitols = capitols;
    }

    public void setRecords(Button records) {
        this.records = records;
    }

    public void setTotalScoreTextView(TextView totalScoreTextView) {
        this.totalScoreTextView = totalScoreTextView;
    }
    public void setPointsAddedTextView(TextView pointsAddedTextView) {
        this.pointsAddedTextView = pointsAddedTextView;
    }

    public void setScoreListViewGeo(ListView scoreListViewGeo) {
        this.scoreListViewGeo = scoreListViewGeo;
    }

    public void setScoreOpListViewGeo(ListView scoreOpListViewGeo) {
        this.scoreOpListViewGeo = scoreOpListViewGeo;
    }

    public void setCloseScoreBoardButton(Button closeScoreBoardButton) {
        this.closeScoreBoardButton = closeScoreBoardButton;
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

    public void setImageEuro(ImageView imageEuro) {
        this.imageEuro = imageEuro;
    }

    public void setImageAsia(ImageView imageAsia) {
        this.imageAsia = imageAsia;
    }

    public void setImageAfrica(ImageView imageAfrica) {
        this.imageAfrica = imageAfrica;
    }

    public void setImageNAmerica(ImageView imageNAmerica) {
        this.imageNAmerica = imageNAmerica;
    }

    public void setImageSAmerica(ImageView imageSAmerica) {
        this.imageSAmerica = imageSAmerica;
    }

    public void setImageAustralia(ImageView imageAustralia) {
        this.imageAustralia = imageAustralia;
    }

    public void setBack(Button back) {
        this.back = back;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_geography);
        setFlags(findViewById(R.id.button_zastave));
        setBorders(findViewById(R.id.button_granice));
        setCapitols(findViewById(R.id.button_glavniGradovi));
        setFinale(findViewById(R.id.button_finaleGeo));
        setRecords(findViewById(R.id.button_rekordi));
        setTotalScoreTextView(findViewById(R.id.textView_TotalScore));
        setPointsAddedTextView(findViewById(R.id.textView_pointsAddedGeo));
        setScoreListViewGeo(findViewById(R.id.scoreListViewGeo));
        setScoreOpListViewGeo(findViewById(R.id.scoreOPListViewGeo));
        setCloseScoreBoardButton(findViewById(R.id.button_closeScoreboardGeo));
        setNewScoreAchievementTextView(findViewById(R.id.textView_achievement));
        setNewScoreConfirmButton(findViewById(R.id.buttonConfirmInputName));
        setNewScoreNameInput(findViewById(R.id.enter_name_textInput));
        setNewScorePointsAchievedTextView(findViewById(R.id.textView_pointsAchieved));
        setImageAfrica(findViewById(R.id.imageView_africa));
        setImageAsia(findViewById(R.id.imageView_asia));
        setImageAustralia(findViewById(R.id.imageView_australia));
        setImageEuro(findViewById(R.id.imageView_europe));
        setImageNAmerica(findViewById(R.id.imageView_nAmerica));
        setImageSAmerica(findViewById(R.id.imageView_sAmerica));
        setBack(findViewById(R.id.button_back));

        constraintLayouts = findViewById(R.id.constraintLayoutGeo);
        applyConstraintSet.clone(constraintLayouts);
        resetConstraintSet.clone(constraintLayouts);

        findViewById(R.id.scoreFLayoutGeo).setVisibility(View.INVISIBLE);

        enableOrDisableAll();

        launch(flags, 1);
        launch(borders, 2);
        launch(capitols, 3);
        launch(finale, 4);

        checkContinentList();

        records.setOnClickListener(view -> {
            List<RecordsSet> scores = dbHelp.getEveryoneFromScores(DBHelper.Disciplines.GEOGRAPHY);
            ArrayAdapter scoresAdapter = new ArrayAdapter<RecordsSet>(ActivityGeography.this, android.R.layout.simple_list_item_1, scores);
            scoreListViewGeo.setAdapter(scoresAdapter);

            List<RecordsSet> scoresOperations = dbHelp.getEveryoneFromStage(DBHelper.Disciplines.GEOGRAPHY);
            ArrayAdapter scoresAdapterOP = new ArrayAdapter<RecordsSet>(ActivityGeography.this, R.layout.row, scoresOperations);
            scoreOpListViewGeo.setAdapter(scoresAdapterOP);

            findViewById(R.id.scoreFLayoutGeo).setVisibility(View.VISIBLE);

            TransitionManager.beginDelayedTransition(constraintLayouts);
            applyConstraintSet.setMargin(R.id.scoreFLayoutGeo, ConstraintSet.BOTTOM,100);
            applyConstraintSet.constrainHeight(R.id.scoreFLayoutGeo, 900);
            applyConstraintSet.applyTo(constraintLayouts);

            changeButtonsVisibility(false);

        });

        closeScoreBoardButton.setOnClickListener(view -> {
            scoreListViewGeo.setAdapter(null);
            resetConstraintSet.applyTo(constraintLayouts);
            findViewById(R.id.scoreFLayoutGeo).setVisibility(View.INVISIBLE);
        });

        totalScoreTextView.setText("Poeni: " + dbHelp.getTotalScoreFromDB(DBHelper.Disciplines.GEOGRAPHY));
        totalScore = dbHelp.getTotalScoreFromDB(DBHelper.Disciplines.GEOGRAPHY);

        back.setOnClickListener(view -> {
            Intent i = new Intent(ActivityGeography.this, MainActivity.class);
            startActivity(i);
            finish();
        });

        if (scoreDifference > 0) {
            if(dbHelp.compareScores((int) Math.round(scoreEarnedSinceLastTryGeo), ActivityGeographyQuiz.stageToOperationsGeoConverter(geoStage), DBHelper.Disciplines.GEOGRAPHY)) {
                findViewById(R.id.frame_setRecord).setVisibility(View.VISIBLE);

                newScorePointsAchievedTextView.setText(String.valueOf((int) scoreEarnedSinceLastTryGeo));

                changeButtonsVisibility(false);
                back.setEnabled(false);

                achievementTextSelection();

                newScoreConfirmButton.setOnClickListener(view -> {
                    if(newScoreNameInput.getText().equals("")) Toast.makeText(getBaseContext(), "Niste uneli ime!", Toast.LENGTH_SHORT).show();
                    else if(newScoreNameInput.getText().length() > 15) Toast.makeText(getBaseContext(), "Uneto ime je predugačko. Unesite kraće ime.", Toast.LENGTH_SHORT).show();
                    else{
                        dbHelp.setNewHighScoreInStage(ActivityGeographyQuiz.stageToOperationsGeoConverter(geoStage),
                                (int) scoreEarnedSinceLastTryGeo,
                                String.valueOf(newScoreNameInput.getText()),
                                DBHelper.Disciplines.GEOGRAPHY);
                        findViewById(R.id.frame_setRecord).setVisibility(View.INVISIBLE);
                        newScoreNameInput.setText("");
                        changeButtonsVisibility(true);
                        back.setEnabled(true);
                        if(geoStage == 4) finalStageEndAndReset();
                    }
                });
            }
            changeTotalScore();
        }

    }

    private void changeTotalScore() {
        double fivePercent = scoreDifference / 20;
        double scoreDifferenceHolder = scoreDifference;
        pointsAddedTextView.setVisibility(View.VISIBLE);
        pointsAddedTextView.setText("+" + scoreDifference);
        Utils.delay(1, () -> {
            CountDownTimer time = new CountDownTimer(1000, 50) {
                double totalScoreTemp = totalScore;
                @Override
                public void onTick(long l) {
                    scoreDifference = scoreDifference - fivePercent;
                    pointsAddedTextView.setText("+" + (int) scoreDifference);
                    totalScoreTemp = totalScoreTemp + fivePercent;
                    totalScoreTextView.setText("Poeni: " + (int) totalScoreTemp);
                }

                @Override
                public void onFinish() {
                    totalScore = (int) (totalScore + scoreDifferenceHolder);
                    pointsAddedTextView.setVisibility(View.INVISIBLE);
                    dbHelp.setNewTotalScoreAtDB(totalScore, DBHelper.Disciplines.GEOGRAPHY);
                    totalScoreTextView.setText("Poeni: " + totalScore);
                    scoreDifference = 0;
                }
            }.start();
        });
    }

    private void changeButtonsVisibility(boolean visible){
        int view;
        if(visible) view = View.VISIBLE;
        else view = View.INVISIBLE;

        flags.setVisibility(view);
        borders.setVisibility(view);
        capitols.setVisibility(view);
        finale.setVisibility(view);
        records.setVisibility(view);
    }

    private void achievementTextSelection(){
        switch(geoStage){
            case 1:
                newScoreAchievementTextView.setText("Postavili ste novi rekord u kvizu sa zastavama!");
                break;
            case 2:
                newScoreAchievementTextView.setText("Postavili ste novi rekord u kvizu sa granicama!");
                break;
            case 3:
                newScoreAchievementTextView.setText("Postavili ste novi rekord u kvizu sa  glavnim gradovima!");
                break;
            case 4:
                newScoreAchievementTextView.setText("Postavili ste novi rekord u finalnom nivou!");
                break;

        }
    }

    private void finalStageEndAndReset(){
        changeButtonsVisibility(false);
        back.setEnabled(false);
        findViewById(R.id.frame_setRecord).setVisibility(View.VISIBLE);
        newScoreAchievementTextView.setText("Prošli ste konačni nivo! Upišite vaš rekord!");
        newScorePointsAchievedTextView.setText(String.valueOf(totalScore));
        newScoreConfirmButton.setOnClickListener(view -> {
            RecordsSet newSet = new RecordsSet();
            newSet.setName(String.valueOf(newScoreNameInput.getText()));
            newSet.setScore(totalScore);

            dbHelp.addNewRecord(newSet, DBHelper.Disciplines.GEOGRAPHY);
            dbHelp.setNewTotalScoreAtDB(0, DBHelper.Disciplines.GEOGRAPHY);
            dbHelp.resetCurrentScore(DBHelper.Disciplines.GEOGRAPHY);

            dbHelp.setStageAvailabilityInDB(DBHelper.Operations.BORDERS, false, DBHelper.Disciplines.GEOGRAPHY);
            dbHelp.setStageAvailabilityInDB(DBHelper.Operations.CAPITOLS, false, DBHelper.Disciplines.GEOGRAPHY);
            dbHelp.setStageAvailabilityInDB(DBHelper.Operations.FINAL_GEO, false, DBHelper.Disciplines.GEOGRAPHY);

            totalScore = 0;
            totalScoreTextView.setText("Poeni: " + 0);
            findViewById(R.id.frame_setRecord).setVisibility(View.INVISIBLE);
            enableOrDisableAll();
            changeButtonsVisibility(true);
            back.setEnabled(true);
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
        enableOrDisableButton(borders, dbHelp.getStageAvailabilityInDB(DBHelper.Operations.BORDERS, DBHelper.Disciplines.GEOGRAPHY));
        enableOrDisableButton(capitols, dbHelp.getStageAvailabilityInDB(DBHelper.Operations.CAPITOLS, DBHelper.Disciplines.GEOGRAPHY));
        enableOrDisableButton(finale, dbHelp.getStageAvailabilityInDB(DBHelper.Operations.FINAL_GEO, DBHelper.Disciplines.GEOGRAPHY));
    }

    private void launch(Button bt,int stageNum){
        bt.setOnClickListener(view -> {
                Intent i = new Intent(view.getContext(), ActivityGeographyQuiz.class);
                geoStage = stageNum;
                startActivity(i);
                finish();
        });
    }


    @SuppressLint("UseCompatLoadingForDrawables")
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void checkContinentList(){
        for(int i = 0; continentList.length > i; i++){
            imageSwitch(continentList[i], i);
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void imageSwitch(boolean on, int num){
        if(on){
            switch(num){
                case 0:
                    imageEuro.setImageDrawable(getDrawable(R.drawable.europe_on));
                    break;
                case 1:
                    imageAsia.setImageDrawable(getDrawable(R.drawable.asia_on));
                    break;
                case 2:
                    imageAfrica.setImageDrawable(getDrawable(R.drawable.africa_on));
                    break;
                case 3:
                    imageNAmerica.setImageDrawable(getDrawable(R.drawable.n_america_on));
                    break;
                case 4:
                    imageSAmerica.setImageDrawable(getDrawable(R.drawable.s_america_on));
                    imageAustralia.setImageDrawable(getDrawable(R.drawable.australia_on));
                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + num);
            }
        }else{
            switch(num){
                case 0:
                    imageEuro.setImageDrawable(getDrawable(R.drawable.europe_off));
                    break;
                case 1:
                    imageAsia.setImageDrawable(getDrawable(R.drawable.asia_off));
                    break;
                case 2:
                    imageAfrica.setImageDrawable(getDrawable(R.drawable.africa_off));
                    break;
                case 3:
                    imageNAmerica.setImageDrawable(getDrawable(R.drawable.n_america_off));
                    break;
                case 4:
                    imageSAmerica.setImageDrawable(getDrawable(R.drawable.s_america_off));
                    imageAustralia.setImageDrawable(getDrawable(R.drawable.australia_off));
                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + num);
            }
        }
    }

}

