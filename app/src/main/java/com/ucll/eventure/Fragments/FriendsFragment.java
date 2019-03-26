package com.ucll.eventure.Fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.widget.ArrayAdapter;
import android.widget.Toast;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ucll.eventure.Data.Friends;
import com.ucll.eventure.R;

import java.util.ArrayList;
import java.util.List;

import static com.facebook.FacebookSdk.getApplicationContext;

public class FriendsFragment extends Fragment {

    // Android Layout
    private View view;
    private ListView friendsList;
    private TextView searchText;
    private List<String> friends;


    // Database Var
    private DatabaseReference firebase;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        /**
         *      Create the View
         */

        View view = inflater.inflate(R.layout.friends, container, false);

        /**
         *      Get the User ID
         */

        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        /**
         *      Create a new List of Strings
         */

        friends = new ArrayList<>();

        /**
         *      Navigate to the users friend list
         */

        firebase = FirebaseDatabase.getInstance().getReference().child("admin").child("Friends").child(userId);

        /**
         *      Add dummy list elements
         */

        friends.add("Jacob");
        friends.add("Jan");
        friends.add("Elke");

        /**
         *      Create adapter
         */
        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_1, friends);

        /**
         *      Set the adapter to the ListView
         */

        friendsList.setAdapter(arrayAdapter);

        /**
         *      Listen for any updates on the friends list
         */

        firebase.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        friendsList = view.findViewById(R.id.friends_list);

        return view;

    }

}
