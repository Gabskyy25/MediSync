package com.example.medisync;

import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.os.VibratorManager;
import android.speech.tts.TextToSpeech;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import java.util.Locale;

public class alarmring extends AppCompatActivity {

    private TextToSpeech tts;
    private Handler handler = new Handler();
    private boolean active = true;
    private Vibrator vibrator;

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_alarmring);

        getWindow().addFlags(
                android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON |
                        android.view.WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON |
                        android.view.WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
        );

        String text = getIntent().getStringExtra("DESC");
        if (text == null || text.isEmpty()) text = "Alarm";

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            vibrator = ((VibratorManager) getSystemService(VIBRATOR_MANAGER_SERVICE)).getDefaultVibrator();
        } else {
            vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        }

        startVibration();

        String finalText = text;
        tts = new TextToSpeech(this, s -> {
            if (s == TextToSpeech.SUCCESS) {
                tts.setLanguage(Locale.US);
                speak(finalText);
            }
        });

        Button dismiss = findViewById(R.id.btnDismiss);
        dismiss.setOnClickListener(v -> stopAlarm());
    }

    private void speak(String t) {
        if (!active) return;
        tts.speak(t, TextToSpeech.QUEUE_FLUSH, null, "ALARM");
        handler.postDelayed(() -> speak(t), 2500);
    }

    private void startVibration() {
        long[] pattern = {0, 800, 400, 800, 400};
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createWaveform(pattern, 0));
        } else {
            vibrator.vibrate(pattern, 0);
        }
    }

    private void stopAlarm() {
        active = false;
        handler.removeCallbacksAndMessages(null);
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
        if (vibrator != null) vibrator.cancel();
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (vibrator != null) vibrator.cancel();
    }
}
