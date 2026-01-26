package com.example.medisync;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class signupcaregiver extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signupcaregiver);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        EditText emailField = findViewById(R.id.editEmail);
        EditText passField = findViewById(R.id.editPassword);
        EditText confirmPassField = findViewById(R.id.confPass);
        EditText phoneField = findViewById(R.id.phonenumber);
        Button signupBtn = findViewById(R.id.btnsignup);
        TextView goback = findViewById(R.id.gobacklogin);

        ProgressDialog loading = new ProgressDialog(this);
        loading.setMessage("Creating account...");
        loading.setCancelable(false);

        signupBtn.setOnClickListener(v -> {
            String email = emailField.getText().toString().trim();
            String password = passField.getText().toString().trim();
            String confirmPassword = confirmPassField.getText().toString().trim();
            String phone = phoneField.getText().toString().trim();

            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                emailField.setError("Invalid email format");
                return;
            }

            if (password.length() < 6) {
                passField.setError("Password must be at least 6 characters");
                return;
            }

            if (!password.equals(confirmPassword)) {
                confirmPassField.setError("Passwords do not match");
                return;
            }

            if (phone.length() < 8) {
                phoneField.setError("Invalid phone number");
                return;
            }

            loading.show();

            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        if (!task.isSuccessful()) {
                            loading.dismiss();
                            Toast.makeText(this,
                                    "Signup Failed: " + task.getException().getMessage(),
                                    Toast.LENGTH_LONG).show();
                            return;
                        }

                        String uid = mAuth.getCurrentUser().getUid();

                        Map<String, Object> userData = new HashMap<>();
                        userData.put("email", email);
                        userData.put("phone", phone);
                        userData.put("role", "Caregiver");
                        userData.put("createdAt", System.currentTimeMillis());

                        db.collection("users")
                                .document(uid)
                                .set(userData)
                                .addOnSuccessListener(unused -> {
                                    loading.dismiss();
                                    Toast.makeText(this, "Account created!", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(this, MainActivity.class));
                                    finish();
                                })
                                .addOnFailureListener(e -> {
                                    loading.dismiss();
                                    Toast.makeText(this,
                                            "Firestore Error: " + e.getMessage(),
                                            Toast.LENGTH_LONG).show();
                                });
                    });
        });

        goback.setOnClickListener(v -> {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        });
    }
}
