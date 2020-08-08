package com.example.nikit.mypersonalapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioButton;
import android.widget.TextView;

import java.util.ArrayList;

public class AddWordActivity extends AppCompatActivity {

    ArrayList<String> NewWords;
    ArrayList<String> OldWords;
    ArrayList<String> AnswerNewWords;
    ArrayList<String> AnswerOldWords;
    TextView first;
    TextView second;
    RadioButton IsNew;
    RadioButton IsOld;

    @Override
    @SuppressWarnings("unchecked")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_word);
        IsNew = (RadioButton) findViewById(R.id.ItIsNewRb);
        IsOld = (RadioButton) findViewById(R.id.ItIsOldRb);
        first = (TextView) findViewById(R.id.FirstWord);
        second = (TextView) findViewById(R.id.SecondWord);

        Intent intent = getIntent();
        OldWords = (ArrayList<String>)intent.getSerializableExtra("Old");
        NewWords = (ArrayList<String>)intent.getSerializableExtra("New");
        AnswerOldWords = (ArrayList<String>)intent.getSerializableExtra("AnswerOld");
        AnswerNewWords = (ArrayList<String>)intent.getSerializableExtra("AnswerNew");

    }

    public void ChangeRadio(View view){
        RadioButton rb = (RadioButton)view;
        if (rb.getId() ==  IsNew.getId() && !rb.isChecked()){
            IsOld.setChecked(false);
            IsNew.setChecked(true);
        }
        else if (rb.getId() ==  IsOld.getId() && !rb.isChecked()){
            IsOld.setChecked(true);
            IsNew.setChecked(false);
        }
    }
    public void CheckWords(View view){
        if (!first.getText().equals("") && !second.getText().equals("")){
            if (IsNew.isChecked()){
                NewWords.add(String.valueOf(first.getText()));
                AnswerNewWords.add(String.valueOf(second.getText()));
            }
            else{
                OldWords.add(String.valueOf(first.getText()));
                AnswerOldWords.add(String.valueOf(second.getText()));
            }
            first.setText("");
            second.setText("");
        }
        if (view.getId() == R.id.SaveAndExit){
            exit();
        }
    }

    void exit(){
        Intent intent = new Intent();
        intent.putExtra("Old", OldWords);
        intent.putExtra("New", NewWords);
        intent.putExtra("AnswerOld", AnswerOldWords);
        intent.putExtra("AnswerNew", AnswerNewWords);
        setResult(RESULT_OK, intent);
        finish();
    }



}