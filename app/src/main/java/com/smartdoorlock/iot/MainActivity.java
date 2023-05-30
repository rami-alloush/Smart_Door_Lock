package com.smartdoorlock.iot;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AppCompatActivity;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract;
import com.firebase.ui.auth.IdpResponse;
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private FirebaseAuth mAuth;

    // See: https://developer.android.com/training/basics/intents/result
    private final ActivityResultLauncher<Intent> signInLauncher = registerForActivityResult(
            new FirebaseAuthUIActivityResultContract(),
            this::onSignInResult
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Start writing code!

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        // Check if user is signed in (non-null) and update UI accordingly.
        if (mAuth.getCurrentUser() != null) {
            // already signed in
            userLogged();
        } else {
            // not signed in
            login();
        }
    }

    private void login() {
        // Choose authentication providers
        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.EmailBuilder().build(),
                new AuthUI.IdpConfig.GoogleBuilder().build()
        );

        // Create and launch sign-in intent
        Intent signInIntent = AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(providers)
                .setIsSmartLockEnabled(false)
                .build();
        signInLauncher.launch(signInIntent);
    }

    private void onSignInResult(FirebaseAuthUIAuthenticationResult result) {
        IdpResponse response = result.getIdpResponse();
        if (result.getResultCode() == RESULT_OK) {
            // Successfully signed in
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            userLogged();
            Log.d(TAG, "User logged in: " + (user != null ? user.getUid() : null));
        } else {
            // Sign in failed. If response is null the user canceled the
            // sign-in flow using the back button. Otherwise check
            Log.e(TAG, String.valueOf(response.getError().getErrorCode()));
        }
    }

    private void userLogged() {
        getUserAndRedirect();
        Log.d(TAG, "User already logged: " + mAuth.getUid());
    }

    private void getUserAndRedirect() {
        redirectUser("normal");

        // Get user type
        // Connect to database and get the currentUser information
//        DocumentReference docRef = FirebaseFirestore.getInstance().collection("users")
//                .document(mAuth.getCurrentUser().getUid());
//
//        docRef.get().addOnCompleteListener(task -> {
//            if (task.isSuccessful()) {
//                DocumentSnapshot document = task.getResult();
//                if (document.exists()) {
//                    // if the user exists on the database, check his type
//                    String userType = document.getString("type");
//                    Log.d(TAG, "DocumentSnapshot data: " + userType);
//                    // redirect the user based on his type
//                        redirectUser(userType);
//                } else {
//                    Log.d(TAG, "No such document. Skip Sign Out");
//                    redirectUser("normal");
//                }
//            } else {
//                Log.d(TAG, "get failed with ", task.getException());
//                userSignOut();
//            }
//        });
    }

    /*
Helper method to redirect the user to the proper Activity
based on the user type
 */
    private void redirectUser(String userType) {
        Log.d(TAG, "Current userType: " + userType);
        if (userType != null) {
            if ("normal".equals(userType)) {
                Intent intent_parent = new Intent(getBaseContext(), UserHomeActivity.class);
                startActivity(intent_parent);
                finish();
            } else {
                Toast.makeText(this, R.string.invalid_user_type, Toast.LENGTH_LONG).show();
                userSignOut();
            }
        }
    }

    public void userSignOut() {
        try {
            mAuth.signOut();
            Intent intent = new Intent(getBaseContext(), MainActivity.class);
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Menu Functions
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuItem itemSignOut = menu.add(Menu.NONE, 10, Menu.NONE, getString(R.string.sign_out));
        itemSignOut.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == 10) {
            userSignOut();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}