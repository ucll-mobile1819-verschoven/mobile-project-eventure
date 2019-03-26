package com.ucll.eventure;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.ucll.eventure.Adapters.TabAdapter;
import com.ucll.eventure.Data.UserDatabase;
import com.ucll.eventure.Fragments.FriendsFragment;
import com.ucll.eventure.Fragments.HomeFragment;
import com.ucll.eventure.Fragments.PublicFragment;

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
        adapter.addFragment(new FriendsFragment(), "Friends");
        adapter.addFragment(new HomeFragment(), "Home");
        adapter.addFragment(new PublicFragment(), "Public");
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(1);
        tabLayout.setupWithViewPager(viewPager);


        //
        new UserDatabase(getApplicationContext()).readFromFile().getName();
    }
}

