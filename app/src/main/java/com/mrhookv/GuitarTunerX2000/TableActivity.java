package com.mrhookv.GuitarTunerX2000;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.core.content.res.ResourcesCompat;

public class TableActivity extends Activity {

    private static final float HEADER_SIZE = 20f, TEXT_SIZE = 20f;
    private static final int TEXT_COLOR = Color.WHITE;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.table_activity);

        findViewById(R.id.backButton).setOnClickListener((e) -> back());

        TableLayout table = findViewById(R.id.tableTable);
        TableLayout.LayoutParams params = new TableLayout.LayoutParams(
                                                        TableLayout.LayoutParams.MATCH_PARENT,
                                                        TableLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(20,20,10,10);

        final Typeface font = ResourcesCompat.getFont(getApplicationContext(), R.font.finger_paint);

        // Column headers
        addHeaders(table, params, font);
        fillTable(table, params, font);
    }

    private TextView createTextView(Typeface font, String text, float size, boolean pad) {
        TextView textView = new TextView(this);
        textView.setTypeface(font);
        textView.setText(text);
        textView.setTextSize(size);
        textView.setTextColor(TEXT_COLOR);
        if (pad)
            textView.setPadding(60, 0, 70, 0);
        return textView;
    }

    private void fillTable(TableLayout table, TableLayout.LayoutParams params, Typeface font) {
        for (int i = 0; i < PitchTable.NOTES.length; i++) {
            TableRow row = new TableRow(this);
            row.addView(createTextView(font, PitchTable.NOTES[i], TEXT_SIZE, true));
            row.addView(createTextView(font, PitchTable.FREQUENCIES[i], TEXT_SIZE, true));
            row.setLayoutParams(params);
            table.addView(row);
        }
    }

    private void addHeaders(TableLayout table, TableLayout.LayoutParams params, Typeface font) {
        TableRow headers = new TableRow(this);
        for (String header: PitchTable.HEADERS) {
            headers.addView(createTextView(font, header, HEADER_SIZE, false));
        }
        table.addView(headers);
    }

    private void back() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

}
