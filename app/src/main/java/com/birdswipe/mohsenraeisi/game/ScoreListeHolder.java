package com.birdswipe.mohsenraeisi.game;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by mohsen raeisi on 24/12/2015.
 */
public class ScoreListeHolder extends ArrayAdapter<Integer> {

    private final Context context;
    private final ArrayList<Integer> values;
    private final int currentScore;

    public ScoreListeHolder(Context context, ArrayList<Integer> values,int currentScore) {
        super(context, -1, values);
        this.context = context;
        this.values = values;
        this.currentScore = currentScore;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View rowView = inflater.inflate(R.layout.layout_score, parent, false);
        CardView card = (CardView) rowView.findViewById(R.id.card_view);

        TextView numberText = (TextView) rowView.findViewById(R.id.score_number);
        TextView scoreText = (TextView) rowView.findViewById(R.id.score_value);
        numberText.setText(""+(position+1)+".");
        scoreText.setText(""+values.get(position));

        if(values.get(position) == currentScore)
           // card.setCardBackgroundColor(context.getColor(R.color.scoreColor));
        if(position == 0 ){
            numberText.setTextSize(25);
            scoreText.setTextSize(25);
        }

        // change the icon for Windows and iPhone

        return rowView;
    }
}
