package com.example.medisync;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class AccountFragment extends Fragment {

    private TextView nameText, emailText, phoneText;
    private Button logOutBtn;

    private FirebaseAuth mAuth;
    private FirebaseFirestore firestore;

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        View view = inflater.inflate(R.layout.fragment_account, container, false);

        nameText = view.findViewById(R.id.Name);
        emailText = view.findViewById(R.id.Email);
        phoneText = view.findViewById(R.id.phone);
        logOutBtn = view.findViewById(R.id.button);

        mAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) return view;

        loadUserInfo(currentUser.getUid());

        logOutBtn.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            if (getActivity() != null) getActivity().finish();
        });

        return view;
    }

    private void loadUserInfo(String uid) {
        firestore.collection("users")
                .document(uid)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (!documentSnapshot.exists()) return;

                    String email = documentSnapshot.getString("email");
                    String phone = documentSnapshot.getString("phone");

                    emailText.setText(email != null ? email : "N/A");
                    phoneText.setText(phone != null ? phone : "N/A");

                    if (email != null && email.contains("@")) {
                        String prefix = email.substring(0, email.indexOf("@"));
                        nameText.setText(capitalize(prefix));
                    } else {
                        nameText.setText("Caregiver");
                    }
                });
    }

    private String capitalize(String s) {
        if (s == null || s.isEmpty()) return s;
        return s.substring(0, 1).toUpperCase() + s.substring(1);
    }
}
