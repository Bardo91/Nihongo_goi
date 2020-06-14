package com.bardo91.nihongo_goi;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Vector;

public class MainActivity extends AppCompatActivity {

    private VocabularyDbHandler dbhelper;
    private SQLiteDatabase db;

    TextView tvWord;
    Button nextButton;
    Button markAll, unmarkAll;
    ToggleButton languageSwitch;

    LinearLayout tablesScrollView;
    final ArrayList<CheckBox> tablesCbx = new ArrayList<>();
    final HashMap<String, Boolean> usedTables = new HashMap<>();


    String currentSpanish = "click next";
    String currentJapanese = "click next";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvWord = findViewById(R.id.tv_word);
        nextButton = findViewById(R.id.next_button);
        markAll = findViewById(R.id.bt_all);
        unmarkAll = findViewById(R.id.bt_none);

        languageSwitch = findViewById(R.id.language_switch);
        languageSwitch.setTextOff("Spanish");
        languageSwitch.setTextOn("Japanese");
        languageSwitch.setChecked(false);

        tablesScrollView = findViewById(R.id.ll_cbx);

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
                ArrayList<VocabularyWord> words = new ArrayList<>();
                for(Map.Entry<String, Boolean> entry : usedTables.entrySet()){
                    if(entry.getValue()){
                        ArrayList<VocabularyWord> toAppend = dbhelper.getAllTable(entry.getKey());
                        words.addAll(toAppend);
                    }
                }

                VocabularyWord word = words.get(new Random().nextInt(words.size()));

                /*
                VocabularyWord word = dbhelper.randomQuery();
                if(word == null)
                    return;
                */
                currentJapanese = word.getJapanese();
                currentSpanish = word.getSpanish();

                if(languageSwitch.isChecked()){
                    tvWord.setText(currentJapanese);
                }else{
                    tvWord.setText(currentSpanish);
                }
            }
        });

        markAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for(CheckBox cb:tablesCbx){
                    cb.setChecked(true);
                }
            }
        });

        unmarkAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for(CheckBox cb:tablesCbx){
                    cb.setChecked(false);
                }
            }
        });


        ArrayList<String> tableList = dbhelper.tableList();
        for(String table:tableList){
            usedTables.put(table, true);
            CheckBox cb = new CheckBox(this);
            cb.setText(table);
            cb.setChecked(true);
            tablesCbx.add(cb);
            tablesScrollView.addView(cb);
            cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    usedTables.put(buttonView.getText().toString(), isChecked);
                }
            });
        }
    }
}
