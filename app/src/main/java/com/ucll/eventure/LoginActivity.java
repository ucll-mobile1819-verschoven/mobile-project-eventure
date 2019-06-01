package com.ucll.eventure;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
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
import com.google.android.gms.tasks.OnFailureListener;
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
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.NotNull;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.ucll.eventure.Data.User;
import com.ucll.eventure.Data.UserDatabase;
import com.ucll.eventure.Managers.FirstTimeLaunchedManager;
import com.ucll.eventure.Messaging.DBM;
import com.ucll.eventure.Messaging.MyFirebaseMessagingService;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;

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

        startServices();
    }

    protected void startServices() {
        FirebaseMessaging.getInstance().setAutoInitEnabled(true);
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

    @Override
    public void onBackPressed() {
        Log.d("mylog", "Back button pressed!");
        //your code to go to previous
    }

    /**
     * Switches to the mainactivity of the application
     */
    private void createOrUpdateUser() {
        final FirebaseUser currentUser = mAuth.getCurrentUser();

        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(new OnSuccessListener<InstanceIdResult>() {
            @Override
            public void onSuccess(InstanceIdResult instanceIdResult) {
                final String deviceToken = instanceIdResult.getToken();
                FirstTimeLaunchedManager firstTimeLaunchedManager = new FirstTimeLaunchedManager(getApplicationContext());
                if (firstTimeLaunchedManager.isFirstTimeLaunch()) {
                    if (currentUser != null) {
                        final User toCreate = new User(currentUser.getUid(), currentUser.getDisplayName(), currentUser.getEmail(), deviceToken, "",new HashMap<String, Object>());
                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("admin").child("Users").child(currentUser.getUid());
                        ref.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if(!dataSnapshot.exists()){
                                    continueCreation(currentUser, toCreate, deviceToken);
                                } else {
                                    GenericTypeIndicator<User> t = new GenericTypeIndicator<User>() {
                                    };
                                    User me = dataSnapshot.getValue(t);
                                    new UserDatabase(getApplicationContext()).writeToFile(me);
                                    goToMain();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

                    }
                } else {
                    goToMain();
                }
            }
        });
    }

    private void continueCreation(FirebaseUser currentUser, User toCreate, String deviceToken){
        if (currentUser.getEmail() == null || currentUser.getEmail().isEmpty()) {
            toCreate.setEmail("Not Given");
        }
        final DatabaseReference users = FirebaseDatabase.getInstance().getReference().child("admin").child("Users").child(currentUser.getUid());
        users.setValue(toCreate);
        users.child("databaseID").setValue(currentUser.getUid());
        users.child("email").setValue(currentUser.getEmail());
        if (currentUser.getEmail() == null || currentUser.getEmail().isEmpty()) {
            users.child("email").setValue("Not Given");
        }
        users.child("messageID").setValue(deviceToken);
        users.child("name").setValue(currentUser.getDisplayName());
        uploadPicture(currentUser.getUid());
        new UserDatabase(getApplicationContext()).writeToFile(toCreate);
        goToMain();
    }

    private void uploadPicture(String id){
        Drawable d = ContextCompat.getDrawable(this,R.drawable.person);
        Bitmap bitmap = drawableToBitmap(d);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        StorageReference ref = FirebaseStorage.getInstance().getReference().child("profilePictures").child(id).child("profile_picture.jpg");
        UploadTask uploadTask = ref.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Toast.makeText(getApplicationContext(), getString(R.string.wrong2), Toast.LENGTH_LONG).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {

                    }
                });

            }
        });
    }

    public static Bitmap drawableToBitmap (Drawable drawable) {

        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable)drawable).getBitmap();
        }

        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
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
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://policies.google.com/privacy?hl=en-US"));
        startActivity(browserIntent);
    }


}