package com.mrhookv.GuitarTunerX2000.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.content.res.ResourcesCompat;

import com.mrhookv.GuitarTunerX2000.R;

@RequiresApi(api = Build.VERSION_CODES.R)
public class FrequencyBar extends View {

    /**
     *  This is the main UI for the frequency-bar that is displayed on the front page
     */

    class Frequency {
        static final float E1 = 82.41f,
                           A = 110.00f,
                           D = 146.83f,
                           G = 196.00f,
                           B = 246.94f,
                           E2 = 329.63f;
    }

    private static final String[] STRINGS = {"E", "A", "D", "G", "B", "E"},
                                  FREQUENCIES = {"82.41 Hz", "110.00 Hz", "146.83 Hz",
                                                "196.00 Hz", "246.94 Hz", "329.63 Hz"};

    private static Bar bar;

    Paint borderPaint, textPaint, pointerPaint, chordPaint;


    public FrequencyBar(Context context) {
        super(context);
        init(null);
    }

    public FrequencyBar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public FrequencyBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    public FrequencyBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(attrs);
    }

    public void setContext(Context context) {
        final Typeface font = ResourcesCompat.getFont(context, R.font.finger_paint);
        textPaint.setTypeface(font);
        chordPaint.setTypeface(font);
        pointerPaint.setTypeface(font);
    }

    private void init(@Nullable AttributeSet set) {
        borderPaint = new Paint();
        textPaint = new Paint();
        pointerPaint = new Paint();
        chordPaint = new Paint();

        // Inside the bar
        borderPaint.setColor(Color.BLACK);
        borderPaint.setStrokeWidth(10);
        borderPaint.setColor(Color.WHITE);

        textPaint.setStrokeWidth(10);
        textPaint.setColor(Color.WHITE);
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setTextSize(60f);

        pointerPaint.setStrokeWidth(10);
        pointerPaint.setTextSize(60f);
        pointerPaint.setTextAlign(Paint.Align.CENTER);

        chordPaint.setTextAlign(Paint.Align.CENTER);
        chordPaint.setTextSize(220f);

        bar = new Bar();

        setBackgroundColor(Color.TRANSPARENT);
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

    public void updateUI(float frequency) {
        bar.setFrequency(frequency);
        invalidate();
    }

    @RequiresApi(api = Build.VERSION_CODES.R)
    @Override
    public void onDraw(Canvas canvas) {
        bar.draw(200, 200, canvas);
    }

    class Bar {

        static final int WIDTH = 250, HEIGHT = 900;
        static final float STEP_SIZE = HEIGHT / 6.0f;
        float startX, startY;
        float frequency;
        Pointer.ChordStruct closestChord;

        Pointer pointer;

        public Bar() {
            frequency = 40.00f;
            pointer = new Pointer();
        }

        void draw(float startX, float startY, Canvas canvas) {
            this.startX = startX;
            this.startY = startY;

            startX = getWidth() / 3;

            canvas.drawLine(startX, startY, startX, startY+HEIGHT, borderPaint);
            canvas.drawLine(startX, startY+HEIGHT, startX+WIDTH, startY+HEIGHT, borderPaint);
            canvas.drawLine(startX+WIDTH, startY+HEIGHT, startX+WIDTH, startY, borderPaint);
            canvas.drawLine(startX+WIDTH, startY, startX, startY, borderPaint);

            float y = startY + 20;
            float midX = startX + (WIDTH/2);
            for (int i = 0; i < 6; i++) {
                y += STEP_SIZE;
                canvas.drawText(FREQUENCIES[i], startX-(WIDTH/1.5f), y-(STEP_SIZE/2), textPaint);
                canvas.drawText(STRINGS[i], midX, y-(STEP_SIZE/2), textPaint);
            }

            pointer.draw(startX+WIDTH+60, startY, canvas);
            canvas.drawText(closestChord.closetChord, getWidth()/2, HEIGHT+500, chordPaint);
            canvas.drawText(String.format("%.2f Hz", frequency), getWidth()/2, HEIGHT+600, pointerPaint);

        }

        public void setFrequency(float frequency) {
            this.frequency = frequency;
        }

        class Pointer {
                float x, y;
                float size = 70;
                float PAD = 50;
                int angle = 30;
                Paint paint;

            public Pointer() {
                closestChord = new Pointer.ChordStruct();
                paint = new Paint();
                paint.setStrokeWidth(10);
            }

            void draw(float x, float startY, Canvas canvas) {

                calculateClosestChord(startY, frequency);
                setColor();

                pointerPaint.setColor(closestChord.color);
                chordPaint.setColor(closestChord.color);
                y = closestChord.newY;

                canvas.drawLine(x, y, x+(float) (size*Math.cos(Math.toRadians(angle))),
                               y-(float) (size*Math.sin(Math.toRadians(angle))), pointerPaint);
                canvas.drawLine(x, y, x+(float) (size*Math.cos(Math.toRadians(angle))),
                        y+(float) (size*Math.sin(Math.toRadians(angle))), pointerPaint);

                canvas.drawLine(x+PAD, y, x+(float) (size*Math.cos(Math.toRadians(angle))),
                        y-(float) (size*Math.sin(Math.toRadians(angle))), pointerPaint);
                canvas.drawLine(x+PAD, y, x+(float) (size*Math.cos(Math.toRadians(angle))),
                        y+(float) (size*Math.sin(Math.toRadians(angle))), pointerPaint);

                canvas.drawText(String.format("%.2f Hz", frequency), x+3.1f*size, y+20, pointerPaint);
            }

            /**
             *   Sets the correct color according to how close we are
             *   to an actual chord.
             */
            private void setColor() {
                if (closestChord.offset > .16)
                    closestChord.color = Color.parseColor("#ff0000");
                else if (closestChord.offset > .12)
                    closestChord.color = Color.parseColor("#ffc100");
                else if (closestChord.offset > .07)
                    closestChord.color = Color.parseColor("#ffff00");
                else if (closestChord.offset > .03)
                    closestChord.color = Color.parseColor("#d6ff00");
                else {
                    closestChord.color = Color.parseColor("#63ff00");
                }
            }


            private void calculateClosestChord(float startY, float frequency) {
                float lowerFrequency = 70f;
                float higherFrequency = 340f;
                float size;

                closestChord.newY = startY;
                closestChord.frequency = frequency;

                if (frequency < lowerFrequency) {
                    closestChord.newY = startY;
                    closestChord.closetChord = "E";
                    closestChord.offset = 1f;
                } else if (frequency < Frequency.E1) {
                    size = STEP_SIZE / 2;
                    setYandChord(lowerFrequency, Frequency.E1, closestChord, size, 'E', 'E');
                } else if (frequency < Frequency.A) {
                    closestChord.newY += STEP_SIZE / 2;
                    size = STEP_SIZE;
                    setYandChord(Frequency.E1, Frequency.A, closestChord, size, 'E', 'A');
                } else if (frequency < Frequency.D) {
                    closestChord.newY += STEP_SIZE + STEP_SIZE / 2;
                    size = STEP_SIZE;
                    setYandChord(Frequency.A, Frequency.D, closestChord, size, 'A', 'D');

                } else if (frequency < Frequency.G) {
                    closestChord.newY += 2*STEP_SIZE + STEP_SIZE / 2;
                    size = STEP_SIZE;
                    setYandChord(Frequency.D, Frequency.G, closestChord, size, 'D', 'G');

                } else if (frequency < Frequency.B) {
                    closestChord.newY += 3*STEP_SIZE + STEP_SIZE / 2;
                    size = STEP_SIZE;
                    setYandChord(Frequency.G, Frequency.B, closestChord, size, 'G', 'B');

                } else if (frequency < Frequency.E2) {
                    closestChord.newY += 4*STEP_SIZE + STEP_SIZE / 2;
                    size = STEP_SIZE;
                    setYandChord(Frequency.B, Frequency.E2, closestChord, size, 'B', 'E');
                } else if (frequency < higherFrequency) {
                    closestChord.newY += 5*STEP_SIZE + STEP_SIZE / 2;
                    size = STEP_SIZE / 2;
                    setYandChord(Frequency.E2, higherFrequency, closestChord, size, 'E', 'E');
                } else if (frequency > higherFrequency) {
                    closestChord.newY = startY + HEIGHT;
                    closestChord.closetChord = "E";
                    closestChord.offset = 1f;
                }
            }

            private void setYandChord(float lower, float higher, ChordStruct chord,
                              float size, char low, char high) {
                float offset = (chord.frequency - lower) / (higher - lower);
                chord.newY += offset * size;
                if (offset > .5) {
                    chord.closetChord = String.valueOf(high);
                    chord.offset = 1f - offset;
                }
                else {
                    chord.closetChord = String.valueOf(low);
                    chord.offset = offset;
                }
            }

        class ChordStruct {
            float newY, offset, frequency;
            String closetChord;
            int color;
        }

        }
    }

}
