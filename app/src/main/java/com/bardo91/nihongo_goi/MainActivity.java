package com.bardo91.nihongo_goi;

import androidx.appcompat.app.AppCompatActivity;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private VocabularyDbHandler dbhelper;
    private SQLiteDatabase db;

    TextView tvWord;
    Button nextButton;
    ToggleButton languageSwitch;

    String currentSpanish = "click next";
    String currentJapanese = "click next";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvWord = findViewById(R.id.tv_word);
        nextButton = findViewById(R.id.next_button);
        languageSwitch = findViewById(R.id.language_switch);
        languageSwitch.setTextOff("Spanish");
        languageSwitch.setTextOn("Japanese");

        dbhelper  = new VocabularyDbHandler(getApplicationContext());
        dbhelper.db_delete();
        try {
            dbhelper.createDatabase();
        } catch (IOException e) {
            e.printStackTrace();
        }
        dbhelper.openDatabase();

        languageSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(languageSwitch.isChecked()){
                    tvWord.setText(currentJapanese);
                }else{
                    tvWord.setText(currentSpanish);
                }
            }
        });

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                VocabularyWord word = dbhelper.randomQuery();
                if(word == null)
                    return;

                currentJapanese = word.getJapanese();
                currentSpanish = word.getSpanish();

                if(languageSwitch.isChecked()){
                    tvWord.setText(currentJapanese);
                }else{
                    tvWord.setText(currentSpanish);
                }
            }
        });
    }
}
