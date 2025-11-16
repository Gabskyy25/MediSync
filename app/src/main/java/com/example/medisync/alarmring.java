package com.example.medisync;

import android.os.Bundle;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.Locale;

public class alarmring extends AppCompatActivity implements TextToSpeech.OnInitListener {

    private TextToSpeech textToSpeech;
    private String alarmDescription;
    private boolean isActive = true;
    private Handler repeatHandler;
    private Handler autoStopHandler;

    private static final long AUTO_STOP_DURATION = 2 * 60 * 1000;
    private static final long REPEAT_INTERVAL = 1500;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarmring);

        getWindow().addFlags(
                android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON |
                        android.view.WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON |
                        android.view.WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
        );

        Button btnDismiss = findViewById(R.id.btnDismiss);

        alarmDescription = getIntent().getStringExtra("ALARM_DESCRIPTION");
        if (alarmDescription == null || alarmDescription.isEmpty()) {
            alarmDescription = "Alarm";
        }

        textToSpeech = new TextToSpeech(this, this);

        repeatHandler = new Handler();
        autoStopHandler = new Handler();

        // Auto stop after chosen duration
        autoStopHandler.postDelayed(() -> {
            if (isActive) {
                Toast.makeText(this, "Alarm auto-stopped", Toast.LENGTH_LONG).show();
                stopAlarmAndClose();
            }
        }, AUTO_STOP_DURATION);

        btnDismiss.setOnClickListener(v -> stopAlarmAndClose());
    }

    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {

            int result = textToSpeech.setLanguage(Locale.US);

            if (result == TextToSpeech.LANG_MISSING_DATA ||
                    result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Toast.makeText(this, "TTS language not supported", Toast.LENGTH_SHORT).show();
                return;
            }

            speakRepeatedly();
        } else {
            Toast.makeText(this, "TTS initialization failed", Toast.LENGTH_SHORT).show();
        }
    }

    private void speakRepeatedly() {
        if (!isActive || textToSpeech == null) return;

        textToSpeech.speak(alarmDescription, TextToSpeech.QUEUE_FLUSH, null, "alarmID");

        repeatHandler.postDelayed(this::speakRepeatedly, REPEAT_INTERVAL);
    }

    private void stopAlarmAndClose() {
        isActive = false;

        if (textToSpeech != null) {
            textToSpeech.stop();
        }

        if (repeatHandler != null) repeatHandler.removeCallbacksAndMessages(null);
        if (autoStopHandler != null) autoStopHandler.removeCallbacksAndMessages(null);

        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (textToSpeech != null) {
            textToSpeech.shutdown();
        }
    }
}
