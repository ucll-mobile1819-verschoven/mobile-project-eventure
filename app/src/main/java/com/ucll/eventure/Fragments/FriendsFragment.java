package com.ucll.eventure.Fragments;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.res.ResourcesCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;

import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;

import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import com.ucll.eventure.Adapters.FriendsAdapter;
import com.ucll.eventure.Data.Friend;
import com.ucll.eventure.Data.User;
import com.ucll.eventure.Data.UserDatabase;
import com.ucll.eventure.CreateAndEditFriendGroupsActivity;
import com.ucll.eventure.QrActivity;
import com.ucll.eventure.R;

import java.util.ArrayList;


public class FriendsFragment extends Fragment {
    private View view;
    private ListView friendsList;
    private Context context;
    private TextView nothingToSee;
    private ImageView arrowDown;
    private EditText searchText;
    private FriendsAdapter adapter;
    private User me;
    private ArrayList<Friend> friendsArrayList, myFriends, invites, filtered;


    // Database Var
    private DatabaseReference firebase;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        /**
         *      Create the View
         */

        view = inflater.inflate(R.layout.friends, container, false);
        return view;

    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public void onStart() {
        super.onStart();
        setupView();
    }

    private void setupView() {
        nothingToSee = view.findViewById(R.id.nothing_to_see);
        arrowDown = view.findViewById(R.id.arrow_down);
        friendsList = view.findViewById(R.id.friends_list);
        Button qr = view.findViewById(R.id.qrcode);
        final Button friendGroups = view.findViewById(R.id.friendgroups);
        searchText = view.findViewById(R.id.SearchText);
        friendsArrayList = new ArrayList<>();
        friendsArrayList.clear();
        myFriends = new ArrayList<>();
        myFriends.clear();
        invites = new ArrayList<>();
        invites.clear();
        me = new UserDatabase(getContext()).readFromFile();
        adapter = new FriendsAdapter(context, friendsArrayList, me);
        if (friendsList != null && qr != null && friendGroups != null && nothingToSee != null && arrowDown != null && searchText != null) {

            friendsList.setAdapter(adapter);


            getFriends();
            qr.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    qrCode();
                }
            });

            friendGroups.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    friendGroups();
                }
            });
        }
    }

    private void qrCode() {
        Intent i = new Intent(getContext(), QrActivity.class);
        startActivity(i);
    }

    private void friendGroups() {
        Intent i = new Intent(getContext(), CreateAndEditFriendGroupsActivity.class);
        startActivity(i);
    }

    private void getFriends() {
        if (getActivity() == null) {
            Toast.makeText(context, "ID NOT GOTTEN", Toast.LENGTH_LONG).show();
        } else {
            firebase = FirebaseDatabase
                    .getInstance()
                    .getReference()
                    .child("admin")
                    .child("Users")
                    .child(me.getDatabaseID())
                    .child("friends");



            firebase.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    myFriends.clear();
                    if(!dataSnapshot.exists()){
                        Log.d("myTestTag _ getF", "its cleared");
                    } else {
                        GenericTypeIndicator<Friend> t = new GenericTypeIndicator<Friend>() {};
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            if (snapshot != null) {
                                Friend friend = snapshot.getValue(t);
                                if (!contains(friend, myFriends) && friend != null) {
                                    myFriends.add(friend);
                                }
                            }
                        }
                    }

                    friendsArrayList.clear();
                    friendsArrayList.addAll(myFriends);
                    adapter.notifyDataSetChanged();
                    if (friendsArrayList != null) {
                        getInvites();
                        //doTheRest();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(context, databaseError.getMessage(), Toast.LENGTH_LONG).show();
                }
            });

        }
    }

    private void getInvites() {
        final DatabaseReference firebase = FirebaseDatabase
                .getInstance()
                .getReference()

                .child("friendRequests")
                .child(me.getDatabaseID());


        firebase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                invites.clear();
                if(dataSnapshot.exists()){
                    GenericTypeIndicator<Friend> t = new GenericTypeIndicator<Friend>() {
                    };
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Log.d("getInvites", snapshot.toString());

                        if (snapshot != null) {
                            Friend friend = snapshot.getValue(t);
                            if (!contains(friend, invites) && friend != null) {
                                if(friend.getAccepted()){
                                    firebase.child(friend.getUserID()).removeValue();
                                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("admin")
                                            .child("Users").child(me.getDatabaseID()).child("friends").child(friend.getUserID());
                                    ref.setValue(friend);
                                    friend.setAccepted(true);
                                }
                                invites.add(friend);
                            }
                        }
                    }

                    friendsArrayList.clear();
                    friendsArrayList.addAll(myFriends);
                    friendsArrayList.addAll(invites);
                    Log.d("getInvites", String.valueOf(friendsArrayList.size()));
                    Log.d("getInvites", String.valueOf(adapter.getCount()));
                    adapter.notifyDataSetChanged();


                }
                    doTheRest();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(context, databaseError.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }


    private boolean contains(Friend friendToCheck, ArrayList<Friend> friends) {
        if(friendToCheck != null && friendToCheck.getUserID() != null) {
            for (Friend friend : friends) {
                if(friend != null && friend.getUserID() != null)
                    if (friend.getUserID().equals(friendToCheck.getUserID()))
                        return true;
            }
        }

        return false;
    }

    private void doTheRest() {
        filtered = new ArrayList<>();
        searchText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(isAdded()){
                    FriendsAdapter adapter = new FriendsAdapter(context, filtered, me);
                    friendsList.setAdapter(adapter);
                    for (Friend friend : friendsArrayList) {
                        if (friend.getName().toLowerCase().contains(s.toString().toLowerCase()))
                            if(!filtered.contains(friend))
                                filtered.add(friend);
                    }

                    if(s.toString().isEmpty()) {
                        filtered = new ArrayList<>();
                        adapter = new FriendsAdapter(context, friendsArrayList, me);
                        friendsList.setAdapter(adapter);
                    }

                    adapter.notifyDataSetChanged();
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        if (friendsArrayList.size() < 1) {
            Typeface custom_font = ResourcesCompat.getFont(getContext(), R.font.font);
            nothingToSee.setTypeface(custom_font);
            nothingToSee.setVisibility(View.VISIBLE);

            friendsList.setVisibility(View.GONE);
            arrowDown.setVisibility(View.VISIBLE);

        } else {
            nothingToSee.setVisibility(View.GONE);

            friendsList.setVisibility(View.VISIBLE);
            arrowDown.setVisibility(View.GONE);
        }
    }
}