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

    HashMap<String, String> NewWords;
    HashMap<String, String> OldWords; //Будем хранить все данные в хэш-таблицах
    TextView counter; //Часто обращаемся к счётчику слов, полезно запомнить

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //HashMap<String, String> a = (HashMap<String, String>)i.getSerializableExtra("map");
        setContentView(R.layout.activity_italian);
        verifyStoragePermissions(this); // Проверяем, можем ли считывать из external

        counter = (TextView) findViewById(R.id.Quantity);
        NewWords = new HashMap<>();
        OldWords = new HashMap<>();

        // Читаем файл NewWords.txt если он пуст, то просим пользователя выбрать уже существующий
        ReadMap(true);
        if (NewWords.isEmpty()){
            counter.setText(String.format("%s0 слов", counter.getText()));
            Intent intent = new Intent()
                    .setType("text/plain")
                    .setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Select a file"), 123);
        }
        CountWords(NewWords.size());
        ReadMap(false);
        if (OldWords.isEmpty()){
            DisableButton((Button)findViewById(R.id.OldWordsButton));
        }
    }



    public void StartLearning(View view){
        if (view.getId()==R.id.NewWordsButton) {

        }
        else{

        }
    }

    public void CountWords(int count){
        counter.setText(String.format("%s%d", getResources().getString(R.string.HowMuch), count));
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
                    String substring = line.substring(line.indexOf(" - ") + 3, line.length());
                    if (fl) {
                        NewWords.put(line.substring(0, line.indexOf(" - ")), substring);
                    }
                    else {
                        OldWords.put(line.substring(0, line.indexOf(" - ")), substring);
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
                NewWords.put(line.substring(0, line.indexOf('-')), line.substring(line.indexOf('-') + 3, line.length()));
                FileWords.write(line);
                count += 1;
            }
            while ((line = br.readLine()) != null) {
                if (!line.equals("")) {
                    if (line.contains(" - ") && line.indexOf(" - ") < line.length()-3 && line.indexOf(" - ") > 2) {
                        NewWords.put(line.substring(0, line.indexOf('-')), line.substring(line.indexOf('-') + 3, line.length()));
                        FileWords.write("\n" + line);
                        count += 1;
                    }
                }
            }
            FileWords.flush();
            counter.setText(String.format("%s%s слов", counter.getText().subSequence(0, getResources().getString(R.string.HowMuch).length()), count));
        }
        catch (IOException e) {
            DisableButton((Button)findViewById(R.id.NewWordsButton));
            Toast.makeText(getApplicationContext(), "Нельзя открыть файл", Toast.LENGTH_SHORT).show();
            Log.wtf("Can't open or create file", Log.getStackTraceString(e));
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 123){
            if(resultCode == RESULT_OK) {
                MakeNewFile(data.getData());
            }
            else{
                Toast.makeText(this, "Вы не выбрали файл", Toast.LENGTH_SHORT).show();
                DisableButton((Button)findViewById(R.id.NewWordsButton));
            }
        }

    }

    public void ShowMessage(String ErrMessage){ // Показываем сообщение в отдельной активности
        Intent intent = new Intent(this, ErrorMessage.class);
        intent.putExtra("message", ErrMessage);
        startActivity(intent);
    }

    @Override
    protected void onDestroy() { // Перед  уничтожением записываем все слова в файлы
        if (!NewWords.isEmpty()){
            try{
                // По неведомой причине try с ресурсами не очень хорошо обходится с записью файлов
                FileOutputStream file = openFileOutput("NewWords.txt", MODE_PRIVATE);
                OutputStreamWriter FileWords = new OutputStreamWriter(file);
                for (String s: NewWords.keySet()){
                    FileWords.write(s + " - " + NewWords.get(s) + '\n');
                }
                FileWords.flush();
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
                for (String s: OldWords.keySet()){
                    FileWords.write(s + " - " + OldWords.get(s) + '\n');
                }
                FileWords.flush();
            }
            catch (IOException e){
                ShowMessage("Не удалось открыть файл со старыми словами, вы потеряете эти данные");
                Log.e("before destroy", "Не удалось сохранить файл", e);
            }
        }
        super.onDestroy();
    }
}
