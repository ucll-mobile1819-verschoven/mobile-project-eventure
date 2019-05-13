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
import com.ucll.eventure.Data.UserDatabase;
import com.ucll.eventure.CreateAndEditFriendGroupsActivity;
import com.ucll.eventure.QrActivity;
import com.ucll.eventure.R;

import java.util.ArrayList;


public class FriendsFragment extends Fragment {
    // Android Layout
    private ListView friendsList;
    private ArrayList<Friend> friends;
    private Context context;
    private TextView nothingToSee;
    private ImageView arrowDown;
    private ArrayList<Friend> filtered;
    private EditText searchText;
    private FriendsAdapter adapter;


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
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public void onStart() {
        super.onStart();
        setHasOptionsMenu(false);
    }

    @Override
    public void onResume() {
        super.onResume();
        setHasOptionsMenu(false);
        if (getView() != null) {
            setupView();
        }
    }

    private void setupView() {
        nothingToSee = getView().findViewById(R.id.nothing_to_see);
        arrowDown = getView().findViewById(R.id.arrow_down);
        friendsList = getView().findViewById(R.id.friends_list);
        Button qr = getView().findViewById(R.id.qrcode);
        final Button friendGroups = getView().findViewById(R.id.friendgroups);
        searchText = getView().findViewById(R.id.SearchText);
        if (friendsList != null && qr != null && friendGroups != null && nothingToSee != null && arrowDown != null && searchText != null) {
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

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (getView() != null) {
            setupView();
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
        Log.d("getFriendsTag", "getFriends has been called");
        if (getActivity() == null) {
            Log.d("getFriendsTag", "getactivity is null");
            Toast.makeText(context, "ID NOT GET", Toast.LENGTH_LONG).show();
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


            firebase.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Log.d("getFriendsTag", snapshot.toString());
                        GenericTypeIndicator<Friend> t = new GenericTypeIndicator<Friend>() {
                        };
                        if (snapshot != null) {
                            Friend friend = snapshot.getValue(t);
                            if (!contains(friend, friends) && friend != null) {
                                friends.add(friend);
                            }
                        }
                    }

                    if (friends != null) {
                        getInvites();
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
        DatabaseReference firebase = FirebaseDatabase
                .getInstance()
                .getReference()

                .child("friendRequests")
                .child(new UserDatabase(context).readFromFile().getDatabaseID());


        firebase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Log.d("getFriendsTag", snapshot.toString());
                    GenericTypeIndicator<Friend> t = new GenericTypeIndicator<Friend>() {
                    };
                    if (snapshot != null) {
                        Friend friend = snapshot.getValue(t);
                        if (!contains(friend, friends) && friend != null) {
                            friends.add(friend);
                        }
                    }
                }

                if (friends != null) {
                    showFriends();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(context, databaseError.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private boolean contains(Friend friendToCheck, ArrayList<Friend> friends) {
        for (Friend friend : friends) {
            if (friend.getUserID().equals(friendToCheck.getUserID()))
                return true;
        }

        return false;
    }

    private void showFriends() {
        filtered = new ArrayList<>();
        adapter = new FriendsAdapter(context, filtered, getLayoutInflater());
        friendsList.setAdapter(adapter);
        searchText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                for (Friend friend : friends) {
                    if (friend.getName().contains(s))
                        filtered.add(friend);
                }


                adapter.notifyDataSetChanged();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        if (friends.size() < 1) {
            Typeface custom_font = ResourcesCompat.getFont(getContext(), R.font.font);
            nothingToSee.setTypeface(custom_font);
            nothingToSee.setVisibility(View.VISIBLE);

            friendsList.setVisibility(View.GONE);
            arrowDown.setVisibility(View.VISIBLE);

        } else {
            Log.d("getFriendsTag", "we are in showfriends");
            FriendsAdapter adapter = new FriendsAdapter(context, friends, getLayoutInflater());
            friendsList.setAdapter(adapter);
        }
    }
}