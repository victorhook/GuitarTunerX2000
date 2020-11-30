package com.mrhookv.GuitarTunerX2000;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;

import com.mrhookv.GuitarTunerX2000.views.FrequencyBar;
import com.mrhookv.GuitarTunerX2000.views.NoteView;

import be.tarsos.dsp.pitch.FastYin;
import be.tarsos.dsp.pitch.PitchDetectionResult;


public class MainActivity extends AppCompatActivity {

    private final static int SAMPLERATE = 44100,
            CHANNELS = AudioFormat.CHANNEL_IN_MONO,
            AUDIO_ENCODING = AudioFormat.ENCODING_PCM_FLOAT,
            BUFF_SIZE = 1024;
    private float[] buffer;
    private AudioRecord recorder;
    private Thread recorderThread;
    private Handler handler;
    private boolean isRecording;

    // Requesting permission to RECORD_AUDIO
    private boolean permissionToRecordAccepted = false;
    private String [] permissions = {Manifest.permission.RECORD_AUDIO};
    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;

    private static final String TAG = "Recorder";

    FastYin pitchDetector;

    private FrequencyBar frequencyBar;
    private NoteView noteView;

    @RequiresApi(api = Build.VERSION_CODES.R)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        findViewById(R.id.buttonTable).setOnClickListener((e) -> showTable());
        findViewById(R.id.buttonImages).setOnClickListener((e) -> showImages());

         ActivityCompat.requestPermissions(this, permissions,
                                          REQUEST_RECORD_AUDIO_PERMISSION);

        buffer = new float[BUFF_SIZE];

        frequencyBar = findViewById(R.id.frequencyWheel);
        noteView = findViewById(R.id.notes);

        // Not the cleanest way but this works...
        Context context = getApplicationContext();
        frequencyBar.setContext(context);
        noteView.setContext(context);

        handler = new Handler();
        isRecording = false;
        recorder = null;
        recorderThread = null;
        pitchDetector = new FastYin(SAMPLERATE, BUFF_SIZE);

        startRecord();
    }

    private void showImages() {
        Intent intent = new Intent(this, ChordActivity.class);
        startActivity(intent);
    }

    private void showTable() {
        Intent intent = new Intent(this, TableActivity.class);
        startActivity(intent);
    }

    private void stopRecord() {
        if (isRecording) {
            ((RecorderThread) recorderThread).cancel();
            isRecording = false;
            recorder.stop();
        }
    }

    private void startRecord() {
        recorder = new AudioRecord(MediaRecorder.AudioSource.MIC, SAMPLERATE, CHANNELS,
                                   AUDIO_ENCODING, BUFF_SIZE);
        recorderThread = new RecorderThread();
        recorder.startRecording();
        isRecording = true;
        recorderThread.start();
    }

    class RecorderThread extends Thread {

        volatile boolean isRunning;

        @RequiresApi(api = Build.VERSION_CODES.R)
        @Override
        public void run() {
            isRunning = true;

            final int SAMPLES = 2;
            int counter = 0;
            float pitchVal, pitchAv = 0;

            while (isRunning) {
                recorder.read(buffer, 0, BUFF_SIZE, AudioRecord.READ_BLOCKING);
                PitchDetectionResult res = pitchDetector.getPitch(buffer);

                pitchVal = res.getPitch();

                if (pitchVal > 0) {
                    pitchAv += pitchVal;
                    counter++;

                    if (counter == SAMPLES) {
                        final float pitch = pitchAv / counter;
                        handler.post(() -> frequencyBar.updateUI(pitch));
                        counter = 0;
                        pitchAv = 0;
                    }
                }

            }
        }

        void cancel() {
            isRunning = false;
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case REQUEST_RECORD_AUDIO_PERMISSION:
                permissionToRecordAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                break;
        }
    }

}