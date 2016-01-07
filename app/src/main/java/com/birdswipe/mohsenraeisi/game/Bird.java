package com.birdswipe.mohsenraeisi.game;

import android.content.Context;

/**
 * Created by mohsen raeisi on 19/12/2015.
 */
public class Bird {

    private String name;
    private int id;
    private int answer;
    Context context;

    public Bird(int id , Context context){
        this.id = id;


        this.name = context.getResources().getResourceName(id);

        if(name.contains("left"))
            this.answer= 1;
        else if(name.contains("up"))
            this.answer = 2;
        else if(name.contains("right"))
            this.answer = 3;
        else if(name.contains("down"))
            this.answer = 4;
        else
            this.answer = -1;

    }

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getAnswer() {

        return answer;
    }

    public void setAnswer(int answer) {
        this.answer = answer;
    }

    public void setName(String name) {
        this.name = name;
    }
}
