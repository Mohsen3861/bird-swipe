package com.birdswipe.mohsenraeisi.game;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Collections;

public class restartActivity extends Activity {

    ListView scoreList ;
    Button restartButton;
    ArrayList<Integer> values;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restart);

        scoreList = (ListView) findViewById(R.id.listViewScore);
        restartButton = (Button) findViewById(R.id.buttonRestart);

        values = new ArrayList<Integer>(5);
        values.add(900);
        values.add(1000);
        values.add(800);
        values.add(1500);
        values.add(3000);

        Collections.sort(values);
        populateList();

    }

    public void populateList(){

            ScoreListeHolder adapter = new ScoreListeHolder(this,values,1000);

        scoreList.setAdapter(adapter);

    }

}
