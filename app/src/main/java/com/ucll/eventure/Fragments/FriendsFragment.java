package com.ucll.eventure.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ucll.eventure.Adapters.FriendsAdapter;
import com.ucll.eventure.Data.Friend;
import com.ucll.eventure.Data.UserDatabase;
import com.ucll.eventure.R;

import java.util.ArrayList;
import java.util.List;

public class FriendsFragment extends Fragment {
    // Android Layout
    private ListView friendsList;
    private List<Friend> friends;
    private Context context;


    // Database Var
    private DatabaseReference firebase;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        /**
         *      Create the View
         */

        View view = inflater.inflate(R.layout.friends, container, false);
        return view;

    }


    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public void onStart(){
        super.onStart();
        setHasOptionsMenu(false);
    }

    @Override
    public void onResume(){
        super.onResume();
        setHasOptionsMenu(false);
    }

    @Override
    public void onStop(){
        super.onStop();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
        if (getView() != null){
            friendsList = getView().findViewById(R.id.friends_list);
            if(friendsList != null){
                getFriends();
            }
        }
    }

    private void getFriends(){
        Log.d("getFriendsTag", "getFriends has been called");
        if (getActivity() == null){
            Log.d("getFriendsTag","getactivity is null");
            Toast.makeText(context,"ID NOT GET",Toast.LENGTH_LONG).show();
        } else {
            Log.d("getFriendsTag", "we are in else");
            friends = new ArrayList<>();
            friends.clear();
            firebase = FirebaseDatabase
                    .getInstance()
                    .getReference()
                    .child("admin")
                    .child("Users")
                    .child(new UserDatabase(context).readFromFile().getDatabaseID())
                    .child("friends");

            firebase.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                        Log.d("getFriendsTag", snapshot.toString());
                        if(snapshot!=null){
                            Friend friend = new Friend(snapshot.getKey(), snapshot.getValue().toString());
                            if(!friends.contains(friend)){
                                friends.add(friend);
                            }
                        }
                    }

                    if (friends != null){
                        showFriends();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(context, databaseError.getMessage(), Toast.LENGTH_LONG).show();
                }
            });

        }
    }

    private void showFriends(){
        Log.d("getFriendsTag", "we are in showfriends");
        FriendsAdapter adapter = new FriendsAdapter(context,friends,getLayoutInflater());
        friendsList.setAdapter(adapter);
    }
}