package com.example.quizapp;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    //declaring variables
    private TextView highScore;
    public static final String DATA= "data"; //final means constant


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //initializing variable
        highScore=findViewById(R.id.txtHighscore);
        mainScore();
    }

    //this will display highscore on main page
    private void mainScore() {
        SharedPreferences preferences= getSharedPreferences(DATA, 0);
        double highscore= preferences.getFloat("highscore", 0);
        String txtScore= String.format(getString(R.string.highscore),highscore);
        highScore.setText(txtScore);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater= getMenuInflater();
        inflater.inflate(R.menu.main_manu, menu);
        return true;
    }

    //start button method, start quiz, using bundle to send data to the next activity -> question nr 
    public void onStart(View view) {
        Bundle data = new Bundle();
        data.putInt("questionNumber", 1);
        Intent quiz = new Intent(getApplicationContext(), QuestionActivity.class);
        quiz.putExtras(data);
        startActivity(quiz);
    }

    //overriding back button so there won´t be any strange restults when returning to main page
    @Override
    public void onBackPressed() {
        Intent intent=new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        startActivity(intent);
    }

    //region menu actions
    public void onResetScore(MenuItem item) {
        new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle(getString(R.string.resetHighTitle))
                .setMessage(getString(R.string.resetHighMessage))
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int a) {
                        SharedPreferences preferences= getSharedPreferences(DATA, 0);
                        SharedPreferences.Editor editor=preferences.edit();
                        editor.putFloat("highscore", 0);
                        editor.apply();
                        //call mainscore method again cause smt might have changed
                        mainScore();
                    }
                }).setNegativeButton(android.R.string.no,null).show();
    }

    public void onResetAnswers(MenuItem item) {
        new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle(getString(R.string.resetAnsTitle))
                .setMessage(getString(R.string.resetAnsMessage))
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int a) {
                        SharedPreferences preferences= getSharedPreferences(DATA, 0);
                        SharedPreferences.Editor editor=preferences.edit();
                        //using typedarray to get questions from string.xml where they are in array
                        TypedArray typedArray=getResources().obtainTypedArray(R.array.questions);
                        int total=typedArray.length();
                        typedArray.recycle();
                        for (int i = 1; i <= total; ++i){
                            editor.remove(String.format(Locale.getDefault(),"q%d",i));
                        }
                        editor.apply();
                    }
                }).setNegativeButton(android.R.string.no,null).show();
    }
}
