package com.ucll.eventure;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.ucll.eventure.Adapters.TabAdapter;
import com.ucll.eventure.Data.User;
import com.ucll.eventure.Data.UserDatabase;
import com.ucll.eventure.Fragments.FriendsFragment;
import com.ucll.eventure.Fragments.HomeFragment;
import com.ucll.eventure.Fragments.PublicFragment;
import com.ucll.eventure.Messaging.DBM;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        ViewPager viewPager = findViewById(R.id.viewPager);
        TabLayout tabLayout = findViewById(R.id.tabLayout);
        TabAdapter adapter = new TabAdapter(getSupportFragmentManager());
        adapter.addFragment(new FriendsFragment(), "Friend");
        adapter.addFragment(new HomeFragment(), "Home");
        adapter.addFragment(new PublicFragment(), "Public");
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(1);
        tabLayout.setupWithViewPager(viewPager);
        new DBM(getApplicationContext());
        getDeviceToken();
    }

    public void goToSettings(View v){
        Intent i = new Intent(this, SettingsActivity.class);
        startActivity(i);
    }

    private void getDeviceToken(){
        final User me = new UserDatabase(getApplicationContext()).readFromFile();
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            Log.w("unsucc", "getInstanceId failed", task.getException());
                            return;
                        }

                        if(task.getResult() != null){
                            // Get new Instance ID token
                            String token = task.getResult().getToken();

                            DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("admin").child("Users").child(me.getDatabaseID()).child("regToken");
                            ref.setValue(token);
                        } else {
                            Toast.makeText(getApplicationContext(), "Please restart the app", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }
}

