package com.example.quizapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Locale;
import java.util.Objects;

public class ResultActivity extends AppCompatActivity {

    public static final String DATA ="data";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        //method that will display result
        displayResults();
    }

    private void displayResults() {
        SharedPreferences preferences=getSharedPreferences(DATA, 0);
        Resources resources= getResources();
        TypedArray questions= resources.obtainTypedArray(R.array.questions);

        double total= questions.length();
        double score=0;

        LinearLayout linearLayout= findViewById(R.id.resultsLayout);

        for (int i = 0; i<total; i++){
            String answered = preferences.getString(String.format(Locale.getDefault(),"q%d", i+1),"opt0");
            String[] question= resources.getStringArray(questions.getResourceId(i, -1));

            //question title
            TextView title= new TextView(this);
            title.setPadding(0,20,0,10);
            title.setText(String.format(getString(R.string.questionNumber),i+1));
            title.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.colorPrimaryDark));
            title.setTextSize(18);
            linearLayout.addView(title);

            //question itself
            TextView txtQuestion= new TextView(this);
            txtQuestion.setText(question[0]);
            linearLayout.addView(txtQuestion);

            //user answer
            TextView txtUserAnswer = new TextView(this);
            int index =Integer.parseInt(answered.substring(3));
            String answerdText;
            if (index==0){
                answerdText =getString(R.string.dontknow);
            } else {answerdText =question[index]; }
            txtUserAnswer.setText(String.format(getString(R.string.userAnswer),answerdText));//string res.value = Your answer: %s
            txtUserAnswer.setTextColor(Color.BLACK);
            linearLayout.addView(txtUserAnswer);

        //if answer is correct we add points, if not we display the right one
        if (Objects.equals(question[5], answered)){
            TextView correct= new TextView(this);
            correct.setText(getString(R.string.correct)); //Correct answer!
            correct.setTextColor(Color.GREEN);
            linearLayout.addView(correct);
            ++score;
        } else{
            TextView incorrect= new TextView(this);
            index =Integer.parseInt(question[5].substring(3));
            incorrect.setText(String.format(getString(R.string.incorrect),question[index])); //Correct amswer is: %s
            incorrect.setTextColor(Color.RED);
            linearLayout.addView(incorrect);
        }

        }//end of for-cycle

        //converting score to %
        score=(score/total)*100;
        //displaying the score
        TextView txtScores = findViewById(R.id.txtResults);
        txtScores.setText(String.format(getString(R.string.resultsTitle), score));

        //saving the score
        if (score > preferences.getFloat("highscore", 0)){
            TextView highscore = new TextView(this);
            highscore.setPadding(0,20,0,10);
            highscore.setText(getString(R.string.newHighscore));//New highscore!
            highscore.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            highscore.setTextSize(24);
            highscore.setTextColor(Color.MAGENTA);
            linearLayout.addView(highscore);

            SharedPreferences.Editor editor= preferences.edit();
            editor.putFloat("highscore", (float)score );
            editor.apply();
        }
        questions.recycle();

    }//end of the displayResults method


    //overriding back button so it will take to main page
    @Override
    public void onBackPressed() {
        finish();
        onHome(null);
    }

    //button activity will change activity
    public void onHome(View view) {
        startActivity(new Intent(getApplicationContext(),MainActivity.class));
    }
}


