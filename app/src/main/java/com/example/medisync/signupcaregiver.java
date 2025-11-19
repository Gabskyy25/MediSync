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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class signupcaregiver extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signupcaregiver);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference("Users");

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
                        if (task.isSuccessful()) {

                            String userId = mAuth.getCurrentUser().getUid();

                            User user = new User(email, phone, "Caregiver");

                            mDatabase.child(userId).child("profile").setValue(user)
                                    .addOnCompleteListener(dbTask -> {
                                        loading.dismiss();
                                        if (dbTask.isSuccessful()) {
                                            Toast.makeText(this, "Account created!", Toast.LENGTH_SHORT).show();
                                            startActivity(new Intent(this, MainActivity.class));
                                            finish();
                                        } else {
                                            Toast.makeText(this, "Database Error: " +
                                                    dbTask.getException().getMessage(), Toast.LENGTH_LONG).show();
                                        }
                                    });

                        } else {
                            loading.dismiss();
                            Toast.makeText(this, "Signup Failed: " +
                                    task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
        });

        goback.setOnClickListener(v -> {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        });
    }

    public static class User {
        public String email, phone, role;

        public User() { }

        public User(String email, String phone, String role) {
            this.email = email;
            this.phone = phone;
            this.role = role;
        }
    }
}
