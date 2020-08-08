package com.example.nikit.mypersonalapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;

public class LearnWordsActivity extends AppCompatActivity {

    ArrayList<String> NewWords;
    ArrayList<String> OldWords;
    ArrayList<String> AnswerNewWords;
    ArrayList<String> AnswerOldWords;
    boolean fl;
    int current;
    TextView t;
    @Override
    @SuppressWarnings("unchecked")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_learn_words);

        t = (TextView)findViewById(R.id.Word);
        Intent intent = getIntent();
        fl = intent.getBooleanExtra("fl", false);
        if (fl){
            this.setTitle(R.string.TitleLearnNewWords);
        }
        OldWords = (ArrayList<String>)intent.getSerializableExtra("Old");
        NewWords = (ArrayList<String>)intent.getSerializableExtra("New");
        AnswerOldWords = (ArrayList<String>)intent.getSerializableExtra("AnswerOld");
        AnswerNewWords = (ArrayList<String>)intent.getSerializableExtra("AnswerNew");
        ChooseWord();
    }

    public void MarkAsOld(View view){
        if (fl){
            OldWords.add(NewWords.get(current));
            AnswerOldWords.add(AnswerNewWords.get(current));
            NewWords.remove(current);
            AnswerNewWords.remove(current);
        }
        ChooseWord();
    }

    public void MarkAsNew(View view){
        if (!fl){
            NewWords.add(OldWords.get(current));
            AnswerNewWords.add(AnswerOldWords.get(current));
            OldWords.remove(current);
            AnswerOldWords.remove(current);
        }
        ChooseWord();
    }

    void ChooseWord(){
        if (fl){
            current = (int)(Math.random()*NewWords.size());
            t.setText(String.format("%s - %s", NewWords.get(current), AnswerNewWords.get(current)));
        }
        else{
            current = (int)(Math.random()*OldWords.size());
            t.setText(String.format("%s - %s", OldWords.get(current), AnswerOldWords.get(current)));
        }

    }



    public void Close(View view){
        Intent intent = new Intent();
        intent.putExtra("Old", OldWords);
        intent.putExtra("New", NewWords);
        intent.putExtra("AnswerOld", AnswerOldWords);
        intent.putExtra("AnswerNew", AnswerNewWords);
        setResult(RESULT_OK, intent);
        finish();
    }
}