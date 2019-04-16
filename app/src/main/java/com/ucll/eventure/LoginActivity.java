package com.ucll.eventure;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.NotNull;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.ucll.eventure.Data.User;
import com.ucll.eventure.Data.UserDatabase;

import java.util.ArrayList;

public class LoginActivity extends AppCompatActivity {
    private CallbackManager mCallbackManager;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        final LoginButton loginButton = new LoginButton(this);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginManager.getInstance().logOut();
            }
        });

        ImageView image = findViewById(R.id.first);
        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginButton.performClick();
            }
        });

        mCallbackManager = CallbackManager.Factory.create();
        loginButton.setReadPermissions("email", "public_profile");
        loginButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Toast.makeText(getApplicationContext(), "cancelled", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onError(FacebookException error) {
                Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if User is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        User me = new UserDatabase(getApplicationContext()).readFromFile();

        FirstTimeLaunchedManager firstTimeLaunchedManager = new FirstTimeLaunchedManager(getApplicationContext());
        if (firstTimeLaunchedManager.isFirstTimeLaunch() && me == null) {
            createOrUpdateUser();
        } else {
            if (currentUser != null && me != null) {
                goToMain();
            }
        }

    }

    /**
     * Uses the facebook API to get an access token
     *
     * @param token is the facebook api user's gotten token
     */
    private void handleFacebookAccessToken(final AccessToken token) {
        if (token != null) {
            AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
            if (mAuth != null) {
                mAuth.signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NotNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in User's information
                            // FirebaseUser user = mAuth.getCurrentUser();
                            createOrUpdateUser();
                        } else {
                            // If sign in fails, display a message to the User.
                            if (task.getException() != null)
                                Toast.makeText(LoginActivity.this, task.getException().getMessage(),
                                        Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        } else {
            Toast.makeText(getApplicationContext(), "Currently experiencing problems with Facebook, please try again later", Toast.LENGTH_LONG).show();
        }

    }

    /**
     * Switches to the mainactivity of the application
     */
    private void createOrUpdateUser() {
        final FirebaseUser currentUser = mAuth.getCurrentUser();

        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(new OnSuccessListener<InstanceIdResult>() {
            @Override
            public void onSuccess(InstanceIdResult instanceIdResult) {
                String deviceToken = instanceIdResult.getToken();
                FirstTimeLaunchedManager firstTimeLaunchedManager = new FirstTimeLaunchedManager(getApplicationContext());
                if (firstTimeLaunchedManager.isFirstTimeLaunch()) {
                    if (currentUser != null) {
                        final User toCreate = new User(currentUser.getUid(), currentUser.getDisplayName(), currentUser.getEmail(), deviceToken);
                        final DatabaseReference users = FirebaseDatabase.getInstance().getReference().child("admin").child("Users").child(currentUser.getUid());
                        users.setValue(toCreate);
                        new UserDatabase(getApplicationContext()).writeToFile(toCreate);
                        goToMain();
                    }
                } else {
                    goToMain();
                }
            }
        });
    }

    private void goToMain() {
        Intent i = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(i);
        finish();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (mCallbackManager != null)
            mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }

    public void goToPolicy(View v) {
        Toast.makeText(getApplicationContext(), "To Be Implemented", Toast.LENGTH_LONG).show();
    }

}