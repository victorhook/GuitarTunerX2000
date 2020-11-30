package com.mrhookv.GuitarTunerX2000.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.content.res.ResourcesCompat;

import com.mrhookv.GuitarTunerX2000.R;

import java.util.Random;

@RequiresApi(api = Build.VERSION_CODES.R)
public class NoteView extends View {

    private Context context;
    private static final int NOTES = 8;
    private static final Random RAND = new Random();
    private Note[] notes;
    private int width, height;

    public NoteView(Context context) {
        super(context);
        init(null);
    }

    public NoteView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public NoteView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    public NoteView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(attrs);
    }

    public void setContext(Context context) {
        final Typeface font = ResourcesCompat.getFont(context, R.font.finger_paint);
        this.context = context;
    }

    private void init(@Nullable AttributeSet set) {
        notes = new Note[NOTES];
        Drawable drawables[] = {
                getResources().getDrawable(R.drawable.note1, null),
                getResources().getDrawable(R.drawable.note2, null),
                getResources().getDrawable(R.drawable.note3, null),
                getResources().getDrawable(R.drawable.note4, null),
        };
        for (int i = 0; i < notes.length; i++) {
            notes[i] = new Note(drawables[i%drawables.length]);
        }

        System.out.printf("SIZE: %s", this.getResources().getDisplayMetrics());

        post(() ->  {
                width = getWidth();
                height = getHeight();
                for (Note note: notes)
                    note.init(width, height);
        });
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int desiredWidth = 100;
        int desiredHeight = 100;

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        int width;
        int height;

        //Measure Width
        if (widthMode == MeasureSpec.EXACTLY) {
            //Must be this size
            width = widthSize;
        } else if (widthMode == MeasureSpec.AT_MOST) {
            //Can't be bigger than...
            width = Math.min(desiredWidth, widthSize);
        } else {
            //Be whatever you want
            width = desiredWidth;
        }

        //Measure Height
        if (heightMode == MeasureSpec.EXACTLY) {
            //Must be this size
            height = heightSize;
        } else if (heightMode == MeasureSpec.AT_MOST) {
            //Can't be bigger than...
            height = Math.min(desiredHeight, heightSize);
        } else {
            //Be whatever you want
            height = desiredHeight;
        }

        //MUST CALL THIS
        setMeasuredDimension(width, height);
    }

    @RequiresApi(api = Build.VERSION_CODES.R)
    @Override
    public void onDraw(Canvas canvas) {
        for (Note note: notes) {
            note.move();
            note.draw(canvas);
        }
        invalidate();
    }

    class Note {
        private Drawable note;
        private int x, y, width, height;
        private float speed, angle, scale;
        private static final int MAX_SPEED = 5;

        public Note(Drawable note) {
            this.note = note;
        }

        void init(int width, int height) {
            x = RAND.nextInt(width);
            y = RAND.nextInt(height);
            //speed = MAX_SPEED * RAND.nextFloat();
            speed = 1.5f;
            angle = RAND.nextFloat() * 180;
            scale = RAND.nextFloat() * .1f;
            scale = .3f;
        }

        void move() {
            angle = angle == 360 ? 180 : angle + scale;
            x += 3 * Math.cos(Math.toRadians(angle));
            y += speed * 2 * RAND.nextFloat();

            width = getWidth();
            height = getHeight();
            if (x + 64 > width)
                x = 0;
            else if (x < 0)
                x = width - 64;

            if (y - 64 > height)
                y = 0;
            else if (y < 0)
                y = height - 64;

        }

        void draw(Canvas canvas) {
            note.setBounds(x, y, x+64, y+64);
            note.draw(canvas);
        }

    }





}
