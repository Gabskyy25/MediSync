package com.example.medisync;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Arrays;

public class AccountFragment extends Fragment {

    private TextView nameText, emailText, phoneText;
    private TextView linkGoogle, linkFacebook, deleteAccount;
    private Button logOutBtn;

    private FirebaseAuth mAuth;
    private FirebaseFirestore firestore;
    private GoogleSignInClient googleClient;
    private CallbackManager callbackManager;

    /* ---------- GOOGLE RESULT ---------- */
    private final ActivityResultLauncher<Intent> googleLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    result -> {
                        if (result.getData() == null) return;
                        try {
                            GoogleSignInAccount account =
                                    GoogleSignIn.getSignedInAccountFromIntent(result.getData())
                                            .getResult(Exception.class);
                            linkGoogleToFirebase(account);
                        } catch (Exception e) {
                            toast("Google sign-in failed");
                        }
                    });

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        View view = inflater.inflate(R.layout.fragment_account, container, false);

        nameText = view.findViewById(R.id.Name);
        emailText = view.findViewById(R.id.gmail1);
        phoneText = view.findViewById(R.id.phone1);
        linkGoogle = view.findViewById(R.id.LinkGoogle);
        linkFacebook = view.findViewById(R.id.LinkFacebook);
        deleteAccount = view.findViewById(R.id.deleteAccount);
        logOutBtn = view.findViewById(R.id.button);

        mAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        setupGoogle();
        setupFacebook();

        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            loadUserInfo(user);
        }

        linkGoogle.setOnClickListener(v -> {
            googleClient.signOut().addOnSuccessListener(aVoid ->
                    googleLauncher.launch(googleClient.getSignInIntent()));
        });

        linkFacebook.setOnClickListener(v ->
                LoginManager.getInstance().logInWithReadPermissions(
                        this, Arrays.asList("email", "public_profile"))
        );

        logOutBtn.setOnClickListener(v -> {
            mAuth.signOut();
            if (getActivity() != null) getActivity().finish();
        });

        deleteAccount.setOnClickListener(v -> showDeleteConfirmation());

        return view;
    }

    /* ================= GOOGLE ================= */

    private void setupGoogle() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(
                GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        googleClient = GoogleSignIn.getClient(requireActivity(), gso);
    }

    private void linkGoogleToFirebase(GoogleSignInAccount acct) {
        if (acct == null || acct.getIdToken() == null) {
            toast("Missing Google token");
            return;
        }

        AuthCredential credential =
                GoogleAuthProvider.getCredential(acct.getIdToken(), null);

        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) return;

        user.linkWithCredential(credential)
                .addOnSuccessListener(r -> toast("Google linked successfully"))
                .addOnFailureListener(e -> toast(e.getMessage()));
    }

    /* ================= FACEBOOK ================= */

    private void setupFacebook() {
        callbackManager = CallbackManager.Factory.create();

        LoginManager.getInstance().registerCallback(
                callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult result) {
                        handleFacebookToken(result.getAccessToken());
                    }

                    @Override
                    public void onCancel() {
                        toast("Facebook login cancelled");
                    }

                    @Override
                    public void onError(FacebookException error) {
                        toast(error.getMessage());
                    }
                });
    }

    private void handleFacebookToken(AccessToken token) {
        AuthCredential credential =
                FacebookAuthProvider.getCredential(token.getToken());

        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) return;

        user.linkWithCredential(credential)
                .addOnSuccessListener(r -> toast("Facebook linked successfully"))
                .addOnFailureListener(e -> toast(e.getMessage()));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    /* ================= USER INFO ================= */

    private void loadUserInfo(FirebaseUser user) {

        emailText.setText(user.getEmail());

        if (user.getPhoneNumber() != null) {
            phoneText.setText(user.getPhoneNumber());
        } else {
            firestore.collection("users")
                    .document(user.getUid())
                    .get()
                    .addOnSuccessListener(doc -> {
                        if (doc.exists() && doc.contains("phone")) {
                            phoneText.setText(doc.getString("phone"));
                        } else {
                            phoneText.setText("Not set");
                        }
                    })
                    .addOnFailureListener(e ->
                            phoneText.setText("Not set")
                    );
        }

        if (user.getDisplayName() != null) {
            nameText.setText(user.getDisplayName());
        } else {
            nameText.setText("");
        }
    }

    /* ================= DELETE ACCOUNT ================= */

    private void showDeleteConfirmation() {
        new AlertDialog.Builder(requireContext())
                .setTitle("Delete Account")
                .setMessage("Are you sure you want to permanently delete your account? This action cannot be undone.")
                .setPositiveButton("Delete", (dialog, which) -> deleteAccount())
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void deleteAccount() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) return;

        String uid = user.getUid();

        firestore.collection("users")
                .document(uid)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    user.delete()
                            .addOnSuccessListener(v -> {
                                toast("Account deleted");
                                mAuth.signOut();
                                if (getActivity() != null) getActivity().finish();
                            })
                            .addOnFailureListener(e ->
                                    toast("Re-login required to delete account")
                            );
                })
                .addOnFailureListener(e ->
                        toast("Failed to delete user data")
                );
    }

    private void toast(String msg) {
        Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
    }
}
