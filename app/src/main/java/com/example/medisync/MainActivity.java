package com.example.medisync;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private RadioButton rememberMe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();

        EditText emailField = findViewById(R.id.editEmail);
        EditText passField = findViewById(R.id.editPassword);
        Button login = findViewById(R.id.btnLogin);
        TextView createacc = findViewById(R.id.createAcc);
        TextView forgotPass = findViewById(R.id.ForgotPassword);
        TextView click = findViewById(R.id.click);
        TextView here = findViewById(R.id.Here);
        rememberMe = findViewById(R.id.radio);

        login.setOnClickListener(view -> {
            String email = emailField.getText().toString().trim();
            String password = passField.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Enter email and password", Toast.LENGTH_SHORT).show();
                return;
            }

            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {

                            if (rememberMe.isChecked()) {
                                getSharedPreferences("MediSyncPrefs", MODE_PRIVATE)
                                        .edit()
                                        .putString("savedEmail", email)
                                        .putString("savedPassword", password)
                                        .apply();
                            }

                            Toast.makeText(this, "Login Successful", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(MainActivity.this, MainMenu.class));
                            finish();
                        } else {
                            Toast.makeText(this, "Login Failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        });

        createacc.setOnClickListener(view -> {
            startActivity(new Intent(MainActivity.this, signupcaregiver.class));
        });

        click.setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, signupcaregiver.class)));

        here.setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, signupcaregiver.class)));

        forgotPass.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, resetpassword.class));
        });

        loadSavedLogin(emailField, passField);
    }

    private void loadSavedLogin(EditText emailField, EditText passField) {
        String savedEmail = getSharedPreferences("MediSyncPrefs", MODE_PRIVATE)
                .getString("savedEmail", "");

        String savedPassword = getSharedPreferences("MediSyncPrefs", MODE_PRIVATE)
                .getString("savedPassword", "");

        if (!savedEmail.isEmpty() && !savedPassword.isEmpty()) {
            emailField.setText(savedEmail);
            passField.setText(savedPassword);
            rememberMe.setChecked(true);
        }
    }
}
