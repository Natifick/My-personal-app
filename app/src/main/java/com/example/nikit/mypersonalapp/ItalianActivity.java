package com.example.nikit.mypersonalapp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import static android.os.SystemClock.sleep;
import static java.lang.String.valueOf;


public class ItalianActivity extends AppCompatActivity {


    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    ArrayList<String> NewWords;
    ArrayList<String> OldWords;
    ArrayList<String> AnswerNewWords;
    ArrayList<String> AnswerOldWords;
    TextView counter; //Часто обращаемся к счётчику слов, полезно запомнить

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //HashMap<String, String> a = (HashMap<String, String>)i.getSerializableExtra("map");
        setContentView(R.layout.activity_italian);
        verifyStoragePermissions(this); // Проверяем, можем ли считывать из external

        counter = (TextView) findViewById(R.id.Quantity);
        AnswerNewWords = new ArrayList<>();
        AnswerOldWords = new ArrayList<>();
        NewWords = new ArrayList<>();
        OldWords = new ArrayList<>();

        // Читаем файл NewWords.txt если он пуст, то просим пользователя выбрать уже существующий
        ReadMap(true);
        if (NewWords.isEmpty()){
            counter.setText(String.format("%s0 слов", counter.getText()));
            Intent intent = new Intent()
                    .setType("text/plain")
                    .setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Select a file"), 100);
        }
        ReadMap(false);
        if (OldWords.isEmpty()){
            DisableButton((Button)findViewById(R.id.OldWordsButton));
        }
        CountWords();
    }



    public void StartLearning(View view){
        boolean flag = false;
        if (view.getId()==R.id.NewWordsButton) {
            flag = true;
        }
        Intent intent = new Intent(this, LearnWordsActivity.class);
        intent.putExtra("IsNew", flag);
        intent.putExtra("Old", OldWords);
        intent.putExtra("New", NewWords);
        intent.putExtra("AnswerOld", AnswerOldWords);
        intent.putExtra("AnswerNew", AnswerNewWords);
        startActivityForResult(intent, 200);
    }
    public void AddWord(View view){
        Intent intent = new Intent(this, AddWordActivity.class);
        intent.putExtra("Old", OldWords);
        intent.putExtra("New", NewWords);
        intent.putExtra("AnswerOld", AnswerOldWords);
        intent.putExtra("AnswerNew", AnswerNewWords);
        startActivityForResult(intent, 200);
    }

    @SuppressLint("DefaultLocale")
    public void CountWords(){
        counter.setText(String.format("%s %d\n%s %d\n%s %d", getResources().getString(R.string.HowMuch1), OldWords.size()+NewWords.size(),
                getResources().getString(R.string.HowMuch2), NewWords.size(),
                getResources().getString(R.string.HowMuch3), OldWords.size()));
    }


    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }

    public void DisableButton(Button butt){ // Блокируем выбранную кнопку
        butt.setEnabled(false);
        butt.setBackground(getResources().getDrawable(R.drawable.button_shape_disabled, null));
    }
    public void EnableButton(Button butt){ // Разблокируем выбранную кнопку
        butt.setEnabled(true);
        butt.setBackground(getResources().getDrawable(R.drawable.button_shape, null));
    }



    public void ReadMap(boolean fl){ // Считываем из файла слова
        String path = new String("OldWords.txt");
        if (fl){
            path = new String("NewWords.txt");
        }
        try{
            FileInputStream f = openFileInput(path);
            InputStreamReader file = new InputStreamReader(f);
            Scanner sc = new Scanner(file);
            String line;
            while(sc.hasNextLine()) {
                line = sc.nextLine();
                if (line.contains(" - ") && line.indexOf(" - ") < line.length() - 3 && line.indexOf(" - ") > 2) {
                    String substring = line.substring(line.indexOf(" - ") + 2, line.length());
                    if (fl) {
                        NewWords.add(line.substring(0, line.indexOf(" - ")));
                        AnswerNewWords.add(substring);
                    }
                    else {
                        OldWords.add(line.substring(0, line.indexOf(" - ")));
                        AnswerOldWords.add(substring);
                    }
                }
            }
        }
        catch (IOException e){
            Log.e("in file "+ path, "Unable to read file", e);
        }
    }

    private void MakeNewFile(Uri file){  // Производим первоначальное создание файла с новыми словами
        try (BufferedReader br = new BufferedReader(new InputStreamReader(getContentResolver().openInputStream(file), StandardCharsets.UTF_8))){
            FileOutputStream f = openFileOutput("NewWords.txt", MODE_PRIVATE);
            OutputStreamWriter FileWords = new OutputStreamWriter(f);
            int count = 0;
            String line = br.readLine();
            if (line != null && line.contains(" - ") && line.indexOf(" - ") < line.length()-3 && line.indexOf(" - ") > 2) {
                NewWords.add(line.substring(0, line.indexOf('-')));
                AnswerNewWords.add(line.substring(line.indexOf('-') + 2, line.length()));
                FileWords.write(line);
                count += 1;
            }
            while ((line = br.readLine()) != null) {
                if (!line.equals("")) {
                    if (line.contains(" - ") && line.indexOf(" - ") < line.length()-3 && line.indexOf(" - ") > 2) {
                        NewWords.add(line.substring(0, line.indexOf('-')));
                        AnswerNewWords.add(line.substring(line.indexOf('-') + 2, line.length()));
                        FileWords.write("\n" + line);
                        count += 1;
                    }
                }
            }
            FileWords.flush();
            FileWords.close();
            CountWords();
        }
        catch (IOException e) {
            DisableButton((Button)findViewById(R.id.NewWordsButton));
            Toast.makeText(getApplicationContext(), "Нельзя открыть файл", Toast.LENGTH_SHORT).show();
            Log.wtf("Can't open or create file", Log.getStackTraceString(e));
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100){
            if(resultCode == RESULT_OK) {
                MakeNewFile(data.getData());
            }
            else{
                Toast.makeText(this, "Вы не выбрали файл", Toast.LENGTH_SHORT).show();
                DisableButton((Button)findViewById(R.id.NewWordsButton));
            }
        }
        else if (requestCode == 200){
            if (resultCode==RESULT_OK){
                OldWords = (ArrayList<String>)data.getSerializableExtra("Old");
                NewWords = (ArrayList<String>)data.getSerializableExtra("New");
                AnswerOldWords = (ArrayList<String>)data.getSerializableExtra("AnswerOld");
                AnswerNewWords = (ArrayList<String>)data.getSerializableExtra("AnswerNew");
                CountWords();
                SaveAll();
            }
            else{
                Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void ShowMessage(String ErrMessage){ // Показываем сообщение в отдельной активности
        Intent intent = new Intent(this, ErrorMessage.class);
        intent.putExtra("message", ErrMessage);
        startActivity(intent);
    }

    public void Close(View view){
        SaveAll();
        finish();
    }

    void SaveAll(){
        if (!NewWords.isEmpty()){
            try{
                // По неведомой причине try с ресурсами не очень хорошо обходится с записью файлов
                FileOutputStream file = openFileOutput("NewWords.txt", MODE_PRIVATE);
                OutputStreamWriter FileWords = new OutputStreamWriter(file);
                for (int i=0;i<NewWords.size();i++){
                    FileWords.write(NewWords.get(i) + " - " + AnswerNewWords.get(i) + '\n');
                }
                FileWords.flush();
                FileWords.close();
            }
            catch (IOException e){
                ShowMessage("Не удалось открыть файл с новыми словами, вы потеряете эти данные");
                Log.e("before destroy", "Не удалось сохранить файл", e);
            }
        }
        if (!OldWords.isEmpty()){
            try{
                FileOutputStream file = openFileOutput("OldWords.txt", MODE_PRIVATE);
                OutputStreamWriter FileWords = new OutputStreamWriter(file);
                for (int i=0;i<OldWords.size();i++){
                    FileWords.write(OldWords.get(i) + " - " + AnswerOldWords.get(i) + '\n');
                }
                FileWords.flush();
                FileWords.close();
            }
            catch (IOException e){
                ShowMessage("Не удалось открыть файл со старыми словами, вы потеряете эти данные");
                Log.e("before destroy", "Не удалось сохранить файл", e);
            }
        }
    }
}
