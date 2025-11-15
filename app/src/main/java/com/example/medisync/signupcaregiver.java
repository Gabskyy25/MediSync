package com.example.medisync;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Intent;

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

        signupBtn.setOnClickListener(v -> {
            String email = emailField.getText().toString().trim();
            String password = passField.getText().toString().trim();
            String confirmPassword = confirmPassField.getText().toString().trim();
            String phone = phoneField.getText().toString().trim();
            String role = "Caregiver";

            if (email.isEmpty() || password.isEmpty() || phone.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!password.equals(confirmPassword)) {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                return;
            }

            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            String userId = mAuth.getCurrentUser().getUid();
                            User user = new User(email, phone, role);

                            mDatabase.child(userId).setValue(user)
                                    .addOnCompleteListener(dbTask -> {
                                        if (dbTask.isSuccessful()) {
                                            Toast.makeText(this, "Account created successfully", Toast.LENGTH_SHORT).show();
                                            startActivity(new Intent(this, MainActivity.class));
                                            finish();
                                        } else {
                                            Toast.makeText(this, "Database Error: " + dbTask.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        } else {
                            Toast.makeText(this, "Signup Failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
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

        public User() {}

        public User(String email, String phone, String role) {
            this.email = email;
            this.phone = phone;
            this.role = role;
        }
    }
}
