package com.example.medisync;

import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.os.VibratorManager;
import android.speech.tts.TextToSpeech;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
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

        String originalText = getIntent().getStringExtra("DESC");
        if (originalText == null || originalText.isEmpty()) originalText = "Alarm";

        getWindow().addFlags(
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON |
                        WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON |
                        WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
        );

        String text = getIntent().getStringExtra("DESC");
        if (text == null || text.isEmpty()) text = "Alarm";

        TextView tvDesc = findViewById(R.id.tvAlarmDesc);
        if (tvDesc != null) tvDesc.setText(text);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            VibratorManager vm = (VibratorManager) getSystemService(VIBRATOR_MANAGER_SERVICE);
            vibrator = vm.getDefaultVibrator();
        } else {
            vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        }

        startVibration();

        final String textToSpeak = originalText;



        tts = new TextToSpeech(this, s -> {
            if (s == TextToSpeech.SUCCESS) {
                tts.setLanguage(Locale.US);
                speak(textToSpeak);
            }
        });

        Button dismiss = findViewById(R.id.btnDismiss);
        dismiss.setOnClickListener(v -> stopAlarm());
    }

    private void speak(String t) {
        if (!active || tts == null) return;
        tts.speak(t, TextToSpeech.QUEUE_FLUSH, null, "ALARM");
        handler.postDelayed(() -> speak(t), 3000);
    }

    private void startVibration() {
        if (vibrator == null) return;
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
        active = false;
        handler.removeCallbacksAndMessages(null);
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
        if (vibrator != null) vibrator.cancel();
        super.onDestroy();
    }
}