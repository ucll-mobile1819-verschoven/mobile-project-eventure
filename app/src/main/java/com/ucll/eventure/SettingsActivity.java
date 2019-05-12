package com.ucll.eventure;

import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Vibrator;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.preference.SwitchPreference;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.os.Bundle;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.facebook.login.LoginManager;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.ucll.eventure.Data.User;
import com.ucll.eventure.Data.UserDatabase;
import com.ucll.eventure.Preferences.EditTextPreference;
import com.ucll.eventure.Preferences.EditTextPreferenceListener;

import java.util.Iterator;
import java.util.Map;

public class SettingsActivity extends PreferenceActivity implements Preference.OnPreferenceChangeListener {
    private AppCompatDelegate mDelegate;
    private User me;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getDelegate().installViewFactory();
        getDelegate().onCreate(savedInstanceState);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        addPreferencesFromResource(R.xml.app_pref);

        me = new UserDatabase(getApplicationContext()).readFromFile();

        if (me != null) {
            EditTextPreference preference = (EditTextPreference) findPreference("name");
            preference.setSummary(me.getName());
            preference.setClickListener(new EditTextPreferenceListener() {
                @Override
                public void onItemClick(EditTextPreference preference, String result) {
                    preference.setSummary(result);
                    updateUser(me, "name", result);
                }
            });


            Preference image = findPreference("profileimage");
            StorageReference ref = FirebaseStorage.getInstance().getReference().child("profilePictures").child(me.getDatabaseID()).child("profile_picture.jpg");
            final long ONE_MEGABYTE = 1024 * 1024;
            ref.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                @Override
                public void onSuccess(byte[] bytes) {
                    ((ImageView) findViewById(R.id.profile_pic)).setImageBitmap(BitmapFactory.decodeByteArray(bytes, 0, bytes.length));
                    ((ImageView) findViewById(R.id.profile_pic)).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            editPic();
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle any errors
                }
            });

            SwitchPreference notification = (SwitchPreference)findPreference("notifications");
            notification.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    boolean isVibrateOn = (Boolean) newValue;
                    if (isVibrateOn) {
                        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                        v.vibrate(400);
                        setNotifications(String.valueOf(isVibrateOn));
                    } else {
                        setNotifications(String.valueOf(isVibrateOn));
                    }
                    return true;
                }
            });


            EditTextPreference preference1 = (EditTextPreference) findPreference("email");
            preference1.setSummary(me.getEmail());

            preference1.setClickListener(new EditTextPreferenceListener() {
                @Override
                public void onItemClick(EditTextPreference preference, String result) {
                    preference.setSummary(result);
                    if (validEmail(result))
                        updateUser(me, "email", result);
                    else
                        Toast.makeText(getApplicationContext(), getString(R.string.format_input), Toast.LENGTH_LONG).show();
                }
            });

            final Preference delete = findPreference("delete");
            delete.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    deleteAccount();
                    return false;
                }
            });

            final Preference logout = findPreference("logout");
            logout.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    LoginManager.getInstance().logOut();
                    finish();
                    return false;
                }
            });
        }
    }

    private void setNotifications(String string){
        PreferenceManager.getDefaultSharedPreferences(this).edit().putString("MYNOTIFICATIONS", string).apply();
    }

    private void editPic(){
        Intent i = new Intent(SettingsActivity.this, SelectImage.class);
        startActivity(i);
        finish();
    }

    private void updateUser(User currentUser, String whatToUpdate, String updatedProperty) {
        DatabaseReference ref;
        switch (whatToUpdate) {
            case "name":
                currentUser.setName(updatedProperty);
                new UserDatabase(getApplicationContext()).writeToFile(currentUser);
                ref = FirebaseDatabase.getInstance().getReference().child("admin").child("Users").child(currentUser.getDatabaseID()).child("name");
                ref.setValue(updatedProperty);
                break;
            case "email":
                currentUser.setEmail(updatedProperty);
                new UserDatabase(getApplicationContext()).writeToFile(currentUser);
                ref = FirebaseDatabase.getInstance().getReference().child("admin").child("Users").child(currentUser.getDatabaseID()).child("email");
                ref.setValue(updatedProperty);
                break;
            default:
                return;

        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }

    //TODO: FIX Color SCHEME popups

    /**
     * Method that deletes account from Firebase
     */
    public void deleteAccount() {
        User currentUser = new UserDatabase(getApplicationContext()).readFromFile();
        if (currentUser == null) {
            Toast.makeText(getApplicationContext(), getString(R.string.wrong_delete), Toast.LENGTH_LONG).show();
        } else {
            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("admin").child("Users").child(currentUser.getDatabaseID());
            userRef.removeValue();
            new UserDatabase(getApplicationContext()).writeToFile(null);
            LoginManager.getInstance().logOut();

            if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                FirebaseAuth.getInstance().getCurrentUser().delete();
            }
        }

        Intent i = new Intent(SettingsActivity.this, LoginActivity.class);
        FirebaseAuth.getInstance().signOut();
        startActivity(i);
        finish();
    }

    private boolean validEmail(String email) {
        return email.contains("@");
    }


    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        getDelegate().onPostCreate(savedInstanceState);
    }

    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        getDelegate().setContentView(layoutResID);
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        getDelegate().onPostResume();
    }

    @Override
    protected void onStop() {
        super.onStop();
        getDelegate().onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        getDelegate().onDestroy();
    }

    private void setSupportActionBar(@Nullable Toolbar toolbar) {
        getDelegate().setSupportActionBar(toolbar);
    }

    private AppCompatDelegate getDelegate() {
        if (mDelegate == null) {
            mDelegate = AppCompatDelegate.create(this, null);
        }
        return mDelegate;
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object o) {

        return false;
    }
}