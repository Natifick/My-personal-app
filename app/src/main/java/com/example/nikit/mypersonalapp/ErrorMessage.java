package com.example.nikit.mypersonalapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class ErrorMessage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_error_message);
        Intent intent = getIntent();
        ((TextView)findViewById(R.id.Message)).setText(intent.getStringExtra("message"));

    }
}
