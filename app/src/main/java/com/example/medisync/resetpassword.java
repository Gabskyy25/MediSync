package com.example.medisync;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class resetpassword extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private EditText emailField;
    private Button resetBtn, backBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_resetpassword);

        mAuth = FirebaseAuth.getInstance();

        emailField = findViewById(R.id.resetEmail);
        resetBtn = findViewById(R.id.resetButton);
        backBtn = findViewById(R.id.backToLogin);

        resetBtn.setOnClickListener(v -> {
            String email = emailField.getText().toString().trim();

            if (email.isEmpty()) {
                Toast.makeText(this, "Enter your email", Toast.LENGTH_SHORT).show();
                return;
            }

            mAuth.sendPasswordResetEmail(email)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(this,
                                    "Password reset email sent",
                                    Toast.LENGTH_LONG).show();
                            finish(); // return to login
                        } else {
                            Toast.makeText(this,
                                    "Error: " + task.getException().getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }
                    });
        });

        backBtn.setOnClickListener(v -> finish());
    }
}
