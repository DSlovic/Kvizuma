package com.example.kvizuma;

import static com.example.kvizuma.ActivityMatematika.calculationFinal;
import static com.example.kvizuma.ActivityMatematika.stage;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.transition.TransitionManager;

import java.util.Locale;
import java.util.Random;


public class ActivityCalculation extends AppCompatActivity {
    private TextView countTimer;
    private long timeLeft;
    CountDownTimer timer;
    private TextView firstLine;
    private TextView secondLine;
    private TextView operatorLine;
    private TextView inputLine;
    private TextView calculationLine;
    private TextView currentScoreTextView;
    private TextView addedScoreTextView;
    private TextView correctTextView;
    private TextView incorrectTextView;
    private TextView answerCounterTextView;
    private Button submit;
    private int result;
    private int answerCounter = 1;
    int level = 1;
    private boolean finalLevel = false;
    private double score;
    private double totalScore = 0;
    private ConstraintLayout constraintLayouts;
    private ConstraintSet applyConstraintSet = new ConstraintSet();
    private ConstraintSet resetConstraintSet = new ConstraintSet();
    DBHelper dbHelp = new DBHelper(ActivityCalculation.this);


    public void setCountTimer(TextView countTimer) {
        this.countTimer = countTimer;
    }

    public void setTimeLeft(long timeLeft) {
        this.timeLeft = timeLeft;
    }

    public void setFirstLine(TextView firstLine) {
        this.firstLine = firstLine;
    }

    public void setSecondLine(TextView secondLine) {
        this.secondLine = secondLine;
    }

    public void setOperatorLine(TextView operatorLine) {
        this.operatorLine = operatorLine;
    }

    public void setInputLine(TextView inputLine) {
        this.inputLine = inputLine;
    }

    public void setSubmit(Button submit) {
        this.submit = submit;
    }

    public void setCalculationLine(TextView calculationLine) {
        this.calculationLine = calculationLine;
    }

    public void setCurrentScoreTextView(TextView currentScoreTextView) {
        this.currentScoreTextView = currentScoreTextView;
    }

    public void setAddedScoreTextView(TextView addedScoreTextView) {
        this.addedScoreTextView = addedScoreTextView;
    }

    public void setCorrectTextView(TextView correctTextView) {
        this.correctTextView = correctTextView;
    }

    public void setIncorrectTextView(TextView incorrectTextView) {
        this.incorrectTextView = incorrectTextView;
    }

    public void setAnswerCounterTextView(TextView answerCounterTextView) {
        this.answerCounterTextView = answerCounterTextView;
    }

    public int getResult() {
        return result;
    }

    public void setResult_(int result) {
        this.result = result;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calculation);
        setFirstLine(findViewById(R.id.textNum_firstNumber));
        setSecondLine(findViewById(R.id.textNum_secondNumber));
        setOperatorLine(findViewById(R.id.textNum_operator));
        setInputLine(findViewById(R.id.textNum_input));
        setSubmit(findViewById(R.id.button_submit));
        setCalculationLine(findViewById(R.id.textNum_calculation));
        setCurrentScoreTextView(findViewById(R.id.textView_score));
        setAddedScoreTextView(findViewById(R.id.textView_addedPoints));
        setCorrectTextView(findViewById(R.id.textView_messageCorrect));
        setIncorrectTextView(findViewById(R.id.textView_messageIncorrect));
        setAnswerCounterTextView(findViewById(R.id.textView_finalCounter));
        setCountTimer(findViewById(R.id.editTextTime));
        constraintLayouts = findViewById(R.id.main);
        currentScoreTextView.setText(String.valueOf(Math.round(totalScore)));
        applyConstraintSet.clone(constraintLayouts);
        resetConstraintSet.clone(constraintLayouts);
        ActivityMatematika.calculationEnded = true;
        generate();

    }

    private void updateTimer(){
        int sec = (int) (timeLeft / 1000) / 60;
        int min = (int) (timeLeft / 1000) % 60;
        String timeFormated = String.format(Locale.getDefault(), "%02d:%02d", sec, min);
        countTimer.setText(timeFormated);
        if (timeLeft < 5000){
            countTimer.setTextColor(Color.RED);
        }else{
            countTimer.setTextColor(Color.WHITE);
        }
    }

    private void startTime(){
        if((stage == 1) || (stage == 2)) setTimeLeft(31000);
        else if ((stage == 3) || (stage == 4)) {
            if (level > 3) setTimeLeft(75000);
            else setTimeLeft(45000);
        }
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
                incorrectTextView.setText("VREME JE ISTEKLO!");
                endGame();
            }
        }.start();
    }

    private void generate(){
        if (stage == 5)  finalLevel = true;
        if (finalLevel) {
            Random rand = new Random();
            stage = rand.nextInt(4) + 1;
            level = rand.nextInt(5) + 1;
            answerCounterTextView.setText(answerCounter + "/20");
            calculationFinal=true;
        }else answerCounterTextView.setVisibility(View.INVISIBLE);
        int a = 0;
        int b = 0;
        if ((stage == 1) || (stage == 2)) {
            a = 10;
            b = 10;
            if (level == 2) {
                a = 100;
                b = 100;
            } else if (level == 3) {
                a = 1000;
                b = 1000;
            } else if (level == 4) {
                a = 10000;
                b = 10000;
            } else if (level == 5) {
                a = 100000;
                b = 100000;
            }
        } else if ((stage == 3) || (stage == 4)) {
            a = 10;
            b = 10;
            if (level == 2) {
                a = 100;
                b = 10;
            } else if (level == 3) {
                a = 1000;
                b = 10;
            } else if (level == 4) {
                a = 100;
                b = 100;
            } else if (level == 5) {
                a = 1000;
                b = 100;
            }
        }

        int[] number = generateMixer(a, b);
        //changes depending on what was chosen
        if (stage == 1) {
            setResult_(number[0] + number[1]);
            operatorLine.setText("+");
        }
        else if (stage == 2) {
            setResult_(number[0] - number[1]);
            operatorLine.setText("-");
        }
        else if (stage == 3) {
            setResult_(number[0] * number[1]);
            operatorLine.setText("*");
        }
        else if (stage == 4) {
            setResult_(number[0]);
            operatorLine.setText("/");
        }

        addedScoreTextView.setVisibility(View.INVISIBLE);
        if (stage == 4) firstLine.setText(String.valueOf(number[0] * number[1]));
        else firstLine.setText(String.valueOf(number[0]));
        secondLine.setText(String.valueOf(number[1]));
        submit.setOnClickListener(view -> {
            if (!String.valueOf(inputLine.getText()).equals("")) {
                int submition = Integer.parseInt(String.valueOf(inputLine.getText()));
                if ((submition == getResult()) && (((level < 5) || (finalLevel)) && (answerCounter < 20))) {
                    correctAnswer();
                    Utils.delay(2, () -> {
                        if (finalLevel) answerCounter++;
                        level++;
                        generate();
                    });
                } else if (submition != getResult()) {
                    endGame();
                } else if ((submition == getResult()) && (((level == 5) && (!finalLevel)) || (answerCounter == 20))) {
                    correctAnswer();
                    int currentScoreDiff = dbHelp.addNewCurrentScore(Integer.parseInt(String.valueOf(currentScoreTextView.getText())), convertStage(stage, finalLevel), DBHelper.Disciplines.MATH);
                    if (currentScoreDiff > 0){
                        ActivityMatematika.addToScore = currentScoreDiff;
                        ActivityMatematika.scoreEarnedSinceLastTry = totalScore;
                    }
                    if(!finalLevel){
                        dbHelp.setStageAvailabilityInDB(convertStage(stage + 1, false), true, DBHelper.Disciplines.MATH);
                    }
                    Utils.delay(2, () -> {
                        Intent i = new Intent(ActivityCalculation.this, ActivityMatematika.class);
                        startActivity(i);
                        finish();
                    });
                }
            }else Toast.makeText(getBaseContext(), "Niste uneli odgovor", Toast.LENGTH_SHORT).show();
        });
        startTime();
    }

    private void correctAnswer(){
        double scoreFivePercent;
        if ((stage == 1) || (stage == 2)) {
            if (timeLeft > 5000) {
                score = (5 * level) * (int) timeLeft / 5000;

            } else {
                score = (5 * level);
            }
        } else {
            if (timeLeft > 5000) {
                score = (8 * level) * (int) timeLeft / 5000;

            } else {
                score = (8 * level);
            }
        }
        if (finalLevel) score = score * 2;
        showResult();
        addedScoreTextView.setText("+" + Math.round(score));
        correctTextView.setVisibility(View.VISIBLE);
        inputLine.setTextColor(Color.parseColor("#FF3B8C3E"));
        timer.cancel();
        addedScoreTextView.setVisibility(View.VISIBLE);
        totalScore = totalScore + score;

        TransitionManager.beginDelayedTransition(constraintLayouts);
        applyConstraintSet.setMargin(R.id.textView_addedPoints,ConstraintSet.TOP,8);
        applyConstraintSet.applyTo(constraintLayouts);
         if (!finalLevel) answerCounterTextView.setVisibility(View.INVISIBLE);
        scoreFivePercent = score / 20;
        Utils.delay(1, () -> {
            CountDownTimer time = new CountDownTimer(1000, 50) {
                double tempScore = totalScore - score;
                @Override
                public void onTick(long l) {
                    score = score - scoreFivePercent;
                    addedScoreTextView.setText("+" + ((int) score));
                    tempScore = tempScore + scoreFivePercent;
                    currentScoreTextView.setText(String.valueOf((int)tempScore));
                }

                @Override
                public void onFinish() {
                    currentScoreTextView.setText(String.valueOf((int)totalScore));
                }
            }.start();
        });
        Utils.delay(2, () -> {
            inputLine.setTextColor(Color.BLACK);
            correctTextView.setVisibility(View.INVISIBLE);
            resetConstraintSet.applyTo(constraintLayouts);
            hideResult();
            deleteSubmission();
        });
        //score goes up depending on difficulty level
    }

    private void showResult(){
        calculationLine.setText(String.valueOf(getResult()));
    }
    private void hideResult(){
        calculationLine.setText(" ");
    }
    private void deleteSubmission(){
        inputLine.setText("");
    }
    private void endGame(){
        inputLine.setTextColor(Color.RED);
        incorrectTextView.setVisibility(View.VISIBLE);
        timer.cancel();
        showResult();
        Utils.delay(5, () -> {
            incorrectTextView.setVisibility(View.INVISIBLE);
            incorrectTextView.setText("NETAČNO!");
            level = 1;
            if (finalLevel){
                int currentScoreDiff = dbHelp.addNewCurrentScore(Integer.parseInt(String.valueOf(currentScoreTextView.getText())), DBHelper.Operations.FINAL_MATH, DBHelper.Disciplines.MATH);
                if (currentScoreDiff > 0){
                    ActivityMatematika.addToScore = currentScoreDiff;
                    ActivityMatematika.scoreEarnedSinceLastTry = totalScore;
                }
            }
            Intent i = new Intent(ActivityCalculation.this, ActivityMatematika.class);
            startActivity(i);
            finish();
        });
    }
    private int[] generateMixer(int a, int b){
        int[] mixed = new int[2];
        Random rand = new Random();
        int num1 = rand.nextInt(a - 1) + 1;
        int num2 = rand.nextInt(b - 1) + 1;
        if (num2 > num1) {
            mixed[0] = num2;
            mixed[1] = num1;
        }
        else {
            mixed[0] = num1;
            mixed[1] = num2;
        }
        return mixed;
    }

    public static DBHelper.Operations convertStage(int st, boolean finalLevel){
        if(finalLevel)return DBHelper.Operations.FINAL_MATH;
        else {
            switch (st) {
                case 1:
                    return DBHelper.Operations.ADDITION;
                case 2:
                    return DBHelper.Operations.SUBTRACTION;
                case 3:
                    return DBHelper.Operations.MULTIPLICATION;
                case 4:
                    return DBHelper.Operations.DIVISION;
                case 5:
                    return DBHelper.Operations.FINAL_MATH;
                default:
                    throw new IllegalStateException("Unexpected value: " + st);
            }
        }
    }


}