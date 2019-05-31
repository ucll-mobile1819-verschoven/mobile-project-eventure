package com.ucll.eventure;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.ucll.eventure.Adapters.TabAdapter;
import com.ucll.eventure.Data.UserDatabase;
import com.ucll.eventure.Fragments.FriendsFragment;
import com.ucll.eventure.Fragments.HomeFragment;
import com.ucll.eventure.Fragments.PublicFragment;
import com.ucll.eventure.Messaging.DBM;

public class MainActivity extends AppCompatActivity {
    private TabAdapter adapter;
    private TabLayout tabLayout;
    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        viewPager = findViewById(R.id.viewPager);
        tabLayout = findViewById(R.id.tabLayout);
        adapter = new TabAdapter(getSupportFragmentManager());
        adapter.addFragment(new FriendsFragment(), "Friend");
        adapter.addFragment(new HomeFragment(), "Home");
        adapter.addFragment(new PublicFragment(), "Public");
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(1);
        tabLayout.setupWithViewPager(viewPager);
        new DBM(getApplicationContext());
    }

    public void goToSettings(View v){
        Intent i = new Intent(this, SettingsActivity.class);
        startActivity(i);
        finish();
    }

    //TODO: Firebase messages to user notification chatactivity
    //TODO: Firebase delete friend -> delete user out of groups AND their group node as well
}

