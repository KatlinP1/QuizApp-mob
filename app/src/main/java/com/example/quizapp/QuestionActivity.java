package com.example.quizapp;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.util.Locale;
import java.util.Objects;
import java.util.Stack;

public class QuestionActivity extends AppCompatActivity {

    public static final String DATA= "data";
    Stack<Integer> previousQuestions = new Stack<>();
    int questionNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question);

        //using bundle to get info from previous activity
        Bundle data= this.getIntent().getExtras();
        if(data != null)displayQuestion(data.getInt("questionNumber"));
        //this is needed for adding back button onto the actionbar
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
    }
//This will close action when given to previous activity
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater= getMenuInflater();
        inflater.inflate(R.menu.question_menu, menu);
        return true;
    }

    @SuppressLint("StringFormatMatches")
    private void displayQuestion(int n) {
        questionNumber =n;
        Resources resources= getResources();
        TypedArray questions = resources.obtainTypedArray(R.array.questions);
        String[] question=resources.getStringArray(questions.getResourceId(n -1, -1));

        //if user has reached the last question next button text will change to submit
        if (n== questions.length()){
            Button next = findViewById(R.id.btnNext);
            next.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    submit(); //Method that will submit answer
                }
            });
            next.setText(getString(R.string.subAns));
        }else{
            Button next = findViewById(R.id.btnNext);
            next.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onNext(view); //Method that will submit answer
                }
            });
            next.setText(getString(R.string.subAns));
        }
        // if it is the first question then previous button will be disabled
        if (n==1) findViewById(R.id.btnPrevious).setEnabled(false);
        else findViewById(R.id.btnPrevious).setEnabled(true);

        //displaying questions number
        TextView textQuestionNumber = findViewById(R.id.txtQuestionNumber);
        textQuestionNumber.setText(String.format(getString(R.string.questionNumber),n,questions.length()));
        ((TextView)findViewById(R.id.txtQuestion)).setText(question[0]); //display the questions in textview

        //displaying the answer option in radiobuttons
        for(int i=1; i<=4; i++){
            String optId = String.format(Locale.getDefault(),"opt%d", i);
            RadioButton rbtn = findViewById(getResources().getIdentifier(optId, "id", this.getPackageName()));
            rbtn.setText(question[i]);
            rbtn.setChecked(false);
        }

        //Check to see if there are previously saved answers
        SharedPreferences preferences= getSharedPreferences(DATA, 0);
        RadioButton rbtn = findViewById(getResources().getIdentifier(preferences.getString(String.format(Locale.getDefault(),"q%d",n),
                "opt0"),"id", this.getPackageName()));
        rbtn.setChecked(true);
        questions.recycle(); //recycle the typedarray after not using it anymore

    }

    public void onPrevious(View view) {
        previousQuestions.push(questionNumber);
        saveAnswer(questionNumber);
        displayQuestion(questionNumber-1);
    }

    public void onNext(View view) {
        previousQuestions.push(questionNumber);
        saveAnswer(questionNumber);
        displayQuestion(questionNumber+1);
    }

    private void saveAnswer(int number) {
        SharedPreferences preferences= getSharedPreferences(DATA, 0);
        SharedPreferences.Editor editor= preferences.edit();
        RadioGroup radioGroup = findViewById(R.id.options);
        editor.putString(String.format(Locale.getDefault(),"q%d", number),
                getResources().getResourceEntryName(radioGroup.getCheckedRadioButtonId()));
        editor.apply();
    }
    //saving the answers first moving to new activity where the user can see result
    private void submit() {
        saveAnswer(questionNumber);

        new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle(getString(R.string.submitTitle)) //confirm submission
                .setMessage(getString(R.string.submitMessage))// are you sure?
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int a) {
                        Intent results= new Intent(getApplicationContext(),ResultActivity.class);
                        startActivity(results);
                    }
                }).setNegativeButton(android.R.string.no,null).show();
    }

    public void onBackPressed() {
        if (!previousQuestions.empty()){
            int previous= previousQuestions.pop();
            saveAnswer(questionNumber);
            displayQuestion(previous);
        }
    }


    public void onMenuSubmit(MenuItem item) {
        submit();
    }


}