package com.mrhookv.GuitarTunerX2000;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;


public class ChordActivity extends Activity {

    private static final int IMG_PAD_LEFT = 10, IMG_PAD_RIGHT = 10, IMG_PAD_TOP = 0,
                             IMG_PAD_BOTTOM = 0, ROW_PAD_LEFT = 80, ROW_PAD_RIGHT = 80,
                             ROW_PAD_TOP = 10, ROW_PAD_BOTTOM = 10;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chord_activity);

        findViewById(R.id.backButton).setOnClickListener((e) -> back());

        TableLayout table = findViewById(R.id.chordTable);
        fillTable(table);
    }

    /**
     * Fills the tablelayout with images of the different chords.
     * ChordTable.NAMES contains the filenames for all the chord-images.
     * Each tablerow consists of a linearlayout and 3 ImageViews, with some padding.
     */
    private void fillTable(TableLayout table) {
        Resources res = getResources();
        for (int i = 0; i < ChordTable.NAMES.length-1; i+=3) {
            TableRow row = new TableRow(getApplicationContext());
            LinearLayout layout = new LinearLayout(this);
            layout.setPadding(ROW_PAD_LEFT, ROW_PAD_TOP, ROW_PAD_RIGHT, ROW_PAD_BOTTOM);
            layout.addView(createImageView(res, ChordTable.NAMES[i]));
            layout.addView(createImageView(res, ChordTable.NAMES[i+1]));
            layout.addView(createImageView(res, ChordTable.NAMES[i+2]));
            row.addView(layout);
            table.addView(row);
        }
    }

    /**
     * Creates an ImageView from the name of the file as a string.
     * This is just a convenient helper-method.
     */
    private ImageView createImageView(Resources res, String name) {
        ImageView view = new ImageView(this);
        Drawable drawable = res.getDrawable(res.getIdentifier(name,
                "drawable", getPackageName()));
        view.setImageDrawable(drawable);
        view.setPadding(IMG_PAD_LEFT, IMG_PAD_TOP, IMG_PAD_RIGHT, IMG_PAD_BOTTOM);
        return view;
    }

    /**
     * Callback for the back-button.
     */
    private void back() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

}
