package com.ucll.eventure;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.ucll.eventure.Adapters.InviteFriendAdapter;
import com.ucll.eventure.Adapters.InviteFriendGroupNameAdapter;
import com.ucll.eventure.Data.Event;
import com.ucll.eventure.Data.Friend;
import com.ucll.eventure.Data.User;
import com.ucll.eventure.Data.UserDatabase;

import java.util.ArrayList;
import java.util.HashMap;

// eventInvite object: ID & public or not
public class InviteFriendsActivity extends AppCompatActivity {
    private HashMap<String, ArrayList<Friend>> groups;
    private ListView friendGroups;
    private ListView friendsListView;
    private ArrayList<String> friendGroupNames,ids;
    private ArrayList<Friend> friendsUserHas;
    private InviteFriendAdapter inviteFriendAdapter;
    private InviteFriendGroupNameAdapter inviteFriendGroupNameAdapter;
    private User me;

    private Event eventToDisplay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite_friends);

        friendGroups = findViewById(R.id.your_friend_groups);
        friendsListView = findViewById(R.id.your_friends);
        groups = new HashMap<>();
        friendGroupNames = new ArrayList<>();
        friendsUserHas = new ArrayList<>();
        ids = new ArrayList<>();
        me = new UserDatabase(getApplicationContext()).readFromFile();

        if (getIntent().getStringExtra("event") != null) {
            eventToDisplay = new Gson().fromJson(getIntent().getStringExtra("event"), Event.class);
            getFriendGroupNames();
        }
    }

    private void getFriendGroupNames() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("admin")
                .child("Users").child(me.getDatabaseID()).child("friendGroups");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    String groupID = dataSnapshot.getKey();
                    ids.add(groupID);
                }

                getFriendGroups();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(), databaseError.getMessage(), Toast.LENGTH_LONG).show();
            }

        });

    }

    private void getFriendGroups(){
        for(final String groupID : ids){
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("friendGroups").child(groupID);
            ref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    GenericTypeIndicator<Friend> t = new GenericTypeIndicator<Friend>() {
                    };
                    ArrayList<Friend> ids = new ArrayList<>();
                    String name = "";
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        if(dataSnapshot.getKey() != null){
                            if(!dataSnapshot.getKey().equals("friendGroupName") && !dataSnapshot.getKey().equals("admin")){
                                ids.add(dataSnapshot.getValue(t));
                            }

                            if(dataSnapshot.getKey().equals("friendGroupName")){
                                name = dataSnapshot.getValue().toString();
                            }
                        }
                    }

                    friendGroupNames.add(name);
                    groups.put(name, ids);
                    getFriends();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(getApplicationContext(), databaseError.getMessage(), Toast.LENGTH_LONG).show();
                }

            });
        }


    }

    private void getFriends() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("admin").child("Users")
                .child(new UserDatabase(getApplicationContext()).readFromFile().getDatabaseID()).child("friends");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                GenericTypeIndicator<Friend> t = new GenericTypeIndicator<Friend>() {
                };
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    //ID, NAME
                    Friend friend = dataSnapshot.getValue(t);

                    if (friend != null && !contains(friend, friendsUserHas))
                        friendsUserHas.add(friend);
                }

                display();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(), databaseError.getMessage(), Toast.LENGTH_LONG).show();
            }

        });

    }

    private void display() {
        inviteFriendAdapter = new InviteFriendAdapter(getApplicationContext(), friendsUserHas);
        friendsListView.setAdapter(inviteFriendAdapter);

        inviteFriendGroupNameAdapter = new InviteFriendGroupNameAdapter(getApplicationContext(), friendGroupNames, groups);
        friendGroups.setAdapter(inviteFriendGroupNameAdapter);
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

    public void submitInvites(View view) {
        String node = "";
        if (eventToDisplay.isTotallyVisible()) {
            node = "PublicEvents";
        } else {
            node = "PrivateEvents";
        }

        ArrayList<Friend> selectedFriends = new ArrayList<>();
        if(inviteFriendAdapter.getSelectedList() != null)
            selectedFriends = inviteFriendAdapter.getSelectedList();

        Log.d("myFreeTime", String.valueOf(selectedFriends.size()));
        for (Friend invitee : selectedFriends) {
            submitInvitesToDatabase(invitee, node);
        }

        ArrayList<String> selectedGroups = inviteFriendGroupNameAdapter.getSelectedList();
        Log.d("myFreeTime", String.valueOf(selectedGroups.size()));
        for (String groupName : selectedGroups) {
            for (Friend invitee : groups.get(groupName)) {
                submitInvitesToDatabase(invitee, node);
            }
        }

        finish();
    }

    private void submitInvitesToDatabase(Friend invitee, String node) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("eventInvites").child(invitee.getUserID()).child(eventToDisplay.getEventID());
        ref.setValue(eventToDisplay.isTotallyVisible());

        Toast.makeText(getApplicationContext(), "Invites sent", Toast.LENGTH_LONG).show();

        DatabaseReference ref2 = FirebaseDatabase.getInstance().getReference().child(node).child(eventToDisplay.getEventID()).child("visibleTo").child(invitee.getUserID());
        ref2.setValue(invitee.getUserID());
    }
}
