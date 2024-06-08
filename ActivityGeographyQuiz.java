package com.example.kvizuma;

import static com.example.kvizuma.ActivityGeography.*;
import static com.example.kvizuma.ActivityMatematika.stage;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.transition.TransitionManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;
import java.util.Random;

@RequiresApi(api = Build.VERSION_CODES.N)
public class ActivityGeographyQuiz extends AppCompatActivity {

    DBHelper dbHelp = new DBHelper(ActivityGeographyQuiz.this);
    private ImageView img;
    private int a;
    private int geoStageQuiz;
    private CountrySet country;
    private Bitmap bmp;
    private RadioButton[] answers = new RadioButton[6];
    private RadioGroup answersGroup;
    private CountrySet[] currentCountries = new CountrySet[6];
    private int currentCountriesIDs[] = new int[6];
    private int selectedCountryIDs[]= new int[100];
    private int answerCounter = 1;
    private TextView question;
    private TextView countTimer;
    private int place;
    private int continentNum = 0;
    private Button confirm;
    private long timeLeft;
    CountDownTimer timer;
    private TextView scoreText;
    private TextView addedScoreText;
    private double totalScore;
    private double score;
    private ConstraintLayout constraintLayouts;
    private ConstraintSet applyConstraintSet = new ConstraintSet();
    private ConstraintSet resetConstraintSet = new ConstraintSet();
    private boolean finalStage = false;
    private ArrayList<Integer> continentsID = new ArrayList<Integer>();
    private ArrayList<Integer> availableContinentsID;

    public void setQuestion(TextView question) {
        this.question = question;
    }

    public void setConfirm(Button confirm) {
        this.confirm = confirm;
    }

    public void setCountTimer(TextView countTimer) {
        this.countTimer = countTimer;
    }

    public void setScoreText(TextView scoreText) {
        this.scoreText = scoreText;
    }

    public void setAddedScoreText(TextView addedScoreText) {
        this.addedScoreText = addedScoreText;
    }

    public void setTimeLeft(long timeLeft) {
        this.timeLeft = timeLeft;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_geography_quiz);
        img= findViewById(R.id.imageView_countries);
        answers[0] = findViewById(R.id.radioButton_countryAnswer1);
        answers[1] = findViewById(R.id.radioButton_countryAnswer2);
        answers[2] = findViewById(R.id.radioButton_countryAnswer3);
        answers[3] = findViewById(R.id.radioButton_countryAnswer4);
        answers[4] = findViewById(R.id.radioButton_countryAnswer5);
        answers[5] = findViewById(R.id.radioButton_countryAnswer6);
        answersGroup = findViewById(R.id.radioGroup);
        setQuestion(findViewById(R.id.textView_questionCountry));
        setConfirm(findViewById(R.id.button_confirmAnswer));
        setCountTimer(findViewById(R.id.editTextTime));
        setScoreText(findViewById(R.id.textView_scoreGeo));
        setAddedScoreText(findViewById(R.id.textView_addedPointsGeo));
        constraintLayouts = findViewById(R.id.mainGeo);
        applyConstraintSet.clone(constraintLayouts);
        resetConstraintSet.clone(constraintLayouts);


        if(geoStage == 4) finalStage = true;
        else geoStageQuiz = geoStage;

        for(int i = 0; i < 5; i++){
            if(MainActivity.continentList[i]) {
                continentsID.add(i + 1);
                continentNum++;
            }
        }

        availableContinentsID = dbHelp.getAvailableCountriesID(continentsID);

        assignCountries();

        confirm.setOnClickListener(view -> {
            checkAnswer();
        });
    }

    private void assignCountries(){
        int[] helper = {-1,-1,-1,-1,-1,-1};
        Random rand = new Random();
        byte[] imageChoice;
        confirm.setEnabled(true);
        if(finalStage){
            geoStageQuiz = rand.nextInt(3) + 1;
        }
        for(int i = 0; i < 6; i++){
            if(i==0){
                do{
                    a = rand.nextInt(availableContinentsID.size());
                }
                while(Arrays.stream(selectedCountryIDs).anyMatch(x -> x == a));
                selectedCountryIDs[answerCounter-1] = a;
                country = dbHelp.getCountryByID(availableContinentsID.get(a));
                switch (geoStageQuiz){
                    case 1:
                        imageChoice = country.getCountryFlagImg();
                        break;
                    case 2:
                        imageChoice = country.getCountryBorderImg();
                        break;
                    case 3:
                        imageChoice = country.getCountryBorderCapitolImg();
                        break;
                    default:
                        throw new IllegalStateException("Unexpected value: " + geoStageQuiz);
                }
                bmp = BitmapFactory.decodeByteArray(imageChoice, 0 , imageChoice.length);
                img.setImageBitmap(bmp);
            }else {
                do {
                    a = rand.nextInt(availableContinentsID.size());
                }
                while (Arrays.stream(currentCountriesIDs).anyMatch(x -> x == a));
            }
            do {
                place = rand.nextInt(6);
            }
            while (Arrays.stream(helper).anyMatch(x -> x == place));
            helper[i] = place;
            currentCountries[place] = dbHelp.getCountryByID(availableContinentsID.get(a));
            currentCountriesIDs[i] = a;
            switch (geoStageQuiz){
                case 1:
                    answers[place].setText(currentCountries[place].getCountryName());
                    question.setText("Koja zemlja ima ovu zastavu?");
                    break;
                case 2:
                    answers[place].setText(currentCountries[place].getCountryName());
                    question.setText("Koja je zemlja obeležena na mapi?");
                    break;
                case 3:
                    answers[place].setText(currentCountries[place].getCountryCapitol());
                    question.setText("Kako se zove glavni grad zemlje " + country.getCountryName() +"?");
                    break;
            }
        }
        addedScoreText.setVisibility(View.INVISIBLE);
        if((!finalStage) || (answerCounter == 1))startTime();
    }

    private void checkAnswer(){
        RadioButton rb = findViewById(answersGroup.getCheckedRadioButtonId());
        if(rb.isChecked()) {
            if (geoStageQuiz < 3) {
                if (country.getCountryName().equals(String.valueOf(rb.getText()))) {
                    correct(rb);
                } else {
                    incorrect(rb);
                }
            } else if (geoStageQuiz == 3) {
                if (country.getCountryCapitol().equals(String.valueOf(rb.getText()))) {
                    correct(rb);
                } else {
                    incorrect(rb);
                }
            }
            answersGroup.setPressed(false);
        }else Toast.makeText(getBaseContext(), "Niste uneli odgovor", Toast.LENGTH_SHORT).show();
    }

    private void correct(RadioButton rb){
        question.setText("TAČNO!");
        question.setTextColor(Color.parseColor("#FF3B8C3E"));
        rb.setTextColor(Color.parseColor("#FF3B8C3E"));
        answerCounter++;
        confirm.setEnabled(false);
        if(!finalStage) {
            timer.cancel();
            score = (timeLeft / 200) * continentNum;
        }else score = 100 * geoStageQuiz * continentNum;
        totalScore = totalScore + score;
        addedScoreText.setText("+" + (int) score);

        TransitionManager.beginDelayedTransition(constraintLayouts);
        applyConstraintSet.setMargin(R.id.textView_addedPointsGeo, ConstraintSet.TOP,10);
        applyConstraintSet.applyTo(constraintLayouts);

        double scoreFivePercent = score / 20;
        Utils.delay(1, () -> {
            CountDownTimer time = new CountDownTimer(1000, 50) {
                double tempScore = totalScore - score;
                @Override
                public void onTick(long l) {
                    score = score - scoreFivePercent;
                    addedScoreText.setText("+" + (int) score);
                    tempScore = tempScore + scoreFivePercent;
                    scoreText.setText(String.valueOf((int) tempScore));
                }

                @Override
                public void onFinish() {
                    scoreText.setText(String.valueOf((int) totalScore));
                }
            }.start();
        });
        Utils.delay(2, () ->{
            question.setTextColor(Color.BLACK);
            rb.setTextColor(Color.BLACK);
            if (((answerCounter < 20) && geoStage != 3) || (finalStage) || (answerCounter < 10)) {
                assignCountries();
            }else {
                int currentScoreDiff = dbHelp.addNewCurrentScore((int) totalScore, stageToOperationsGeoConverter(geoStage), DBHelper.Disciplines.GEOGRAPHY);
                if (currentScoreDiff > 0) {
                    if(!finalStage) dbHelp.setStageAvailabilityInDB(stageToOperationsGeoConverter(geoStage + 1), true, DBHelper.Disciplines.GEOGRAPHY);

                    scoreEarnedSinceLastTryGeo = totalScore;
                    scoreDifference = currentScoreDiff;
                }
                endGame();
            }
        });

    }

    private void incorrect(RadioButton rb){
        int j = 0;
        question.setText("NETAČNO!");
        question.setTextColor(Color.RED);
        confirm.setEnabled(false);
        if(!finalStage)timer.cancel();
        rb.setTextColor(Color.RED);
        for(int i=0; i < 6; i++){
            if((String.valueOf(answers[i].getText()).equals(country.getCountryName())) || (answers[i].getText().equals(country.getCountryCapitol()))) {
                answers[i].setTextColor(Color.parseColor("#FF3B8C3E"));
                j=i;
                break;
            }
        }
        int finalJ = j;
        Utils.delay(2, () ->{
            if((finalStage) && (totalScore >= 50)){
                answerCounter++;
                score = 50;
                totalScore = totalScore - score;
                addedScoreText.setText("-" + score);

                TransitionManager.beginDelayedTransition(constraintLayouts);
                applyConstraintSet.setMargin(R.id.textView_addedPoints, ConstraintSet.TOP,8);
                applyConstraintSet.applyTo(constraintLayouts);

                double scoreFivePercent = score / 20;
                Utils.delay(1, () -> {
                    CountDownTimer time = new CountDownTimer(1000, 50) {
                        @Override
                        public void onTick(long l) {
                            score = score - scoreFivePercent;
                            addedScoreText.setText("-" + Math.round(score));
                            totalScore = totalScore - scoreFivePercent;
                            scoreText.setText(String.valueOf(Math.round(totalScore)));
                        }

                        @Override
                        public void onFinish() {
                            question.setTextColor(Color.BLACK);
                            answers[finalJ].setTextColor(Color.BLACK);
                            rb.setTextColor(Color.BLACK);
                            assignCountries();
                        }
                    }.start();
                });
            }else endGame();

        });
    }

    private void updateTimer(){
        int sec = (int) (timeLeft / 1000) / 60;
        int min = (int) (timeLeft / 1000) % 60;
        String timeFormatted = String.format(Locale.getDefault(), "%02d:%02d", sec, min);
        countTimer.setText(timeFormatted);
        if (timeLeft < 2000){
            countTimer.setTextColor(Color.RED);
        }else{
            countTimer.setTextColor(Color.WHITE);
        }
    }

    private void startTime(){
        if(geoStage < 3) setTimeLeft(10000);
        else if (geoStage == 3) setTimeLeft(15000);
        else setTimeLeft(90000);
        timer = new CountDownTimer(timeLeft, 1000){
            @Override
            public void onTick(long timeToFinish){
                timeLeft = timeToFinish;
                updateTimer();
            }
            @Override
            public void onFinish(){
                timeLeft = 0;
                updateTimer();
                question.setText("VREME JE ISTEKLO!");
                question.setTextColor(Color.RED);
                confirm.setEnabled(false);
                Utils.delay(3, () ->{
                    endGame();
                });

            }
        }.start();
    }

    private void endGame(){
        if(finalStage){
            int currentScoreDiff = dbHelp.addNewCurrentScore((int) totalScore, stageToOperationsGeoConverter(geoStage), DBHelper.Disciplines.GEOGRAPHY);
            scoreEarnedSinceLastTryGeo = totalScore;
            scoreDifference = currentScoreDiff;
        }
        Intent i = new Intent(ActivityGeographyQuiz.this, ActivityGeography.class);
        startActivity(i);
        finish();
    }

    public static DBHelper.Operations stageToOperationsGeoConverter(int stageGeo){
        switch(stageGeo){
            case 1:
                return DBHelper.Operations.FLAGS;
            case 2:
                return DBHelper.Operations.BORDERS;
            case 3:
                return DBHelper.Operations.CAPITOLS;
            case 4:
                return DBHelper.Operations.FINAL_GEO;
            default:
                throw new IllegalStateException("Unexpected value: " + stageGeo);
        }
    }



}
