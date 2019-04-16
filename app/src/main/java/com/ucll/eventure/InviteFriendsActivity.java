package com.ucll.eventure;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.ucll.eventure.Adapters.InviteFriendAdapter;
import com.ucll.eventure.Adapters.InviteFriendGroupNameAdapter;
import com.ucll.eventure.Data.Event;
import com.ucll.eventure.Data.InviteAndUser;
import com.ucll.eventure.Data.UserDatabase;

import java.util.ArrayList;
import java.util.HashMap;

//TODO: CREATE HOOK WHEN INVITE FRIENDS SELECTED

// eventInvite object: ID & public or not
public class InviteFriendsActivity extends AppCompatActivity {
    private HashMap<String, ArrayList<InviteAndUser>> groups;
    private ListView friendGroups;
    private ListView friendsListView;
    private ArrayList<String> friendGroupNames;
    private ArrayList<InviteAndUser> friendsUserHas;
    private InviteFriendAdapter inviteFriendAdapter;
    private InviteFriendGroupNameAdapter inviteFriendGroupNameAdapter;

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

        if(getIntent().getStringExtra("event") != null){
            eventToDisplay = new Gson().fromJson(getIntent().getStringExtra("event"), Event.class);
            getFriendGroups();
        }
    }

    private void getFriendGroups(){
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("admin").child("Users")
                .child(new UserDatabase(getApplicationContext()).readFromFile().getDatabaseID()).child("friendGroups");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    friendGroupNames.add(dataSnapshot.getKey());
                    ArrayList<InviteAndUser> ids = new ArrayList<>();
                    for(DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()){
                        ids.add(new InviteAndUser(dataSnapshot1.getKey(), eventToDisplay.getEventID(), dataSnapshot1.getValue().toString()));
                    }

                    groups.put(dataSnapshot.getKey(), ids);
                }

                getFriends();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(), databaseError.getMessage(), Toast.LENGTH_LONG).show();
            }

        });

    }

    private void getFriends(){
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("admin").child("Users")
                .child(new UserDatabase(getApplicationContext()).readFromFile().getDatabaseID()).child("friends");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    //ID, NAME
                    friendsUserHas.add(new InviteAndUser(dataSnapshot.getKey(), eventToDisplay.getEventID(), dataSnapshot.getValue().toString()));
                }

                display();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(), databaseError.getMessage(), Toast.LENGTH_LONG).show();
            }

        });

    }

    private void display(){
        inviteFriendAdapter = new InviteFriendAdapter(getApplicationContext(), friendsUserHas);
        friendsListView.setAdapter(inviteFriendAdapter);

        inviteFriendGroupNameAdapter = new InviteFriendGroupNameAdapter(getApplicationContext(), friendGroupNames, groups);
        friendGroups.setAdapter(inviteFriendGroupNameAdapter);
    }

    public void submitInvites(View view){
        ArrayList<InviteAndUser> selectedFriends = inviteFriendAdapter.getSelectedList();

        for(InviteAndUser invitee : selectedFriends){
            submitInvitesToDatabase(invitee);
        }

        ArrayList<String> selectedGroups = inviteFriendGroupNameAdapter.getSelectedList();
        for(String groupName : selectedGroups){
            for(InviteAndUser invitee : groups.get(groupName)){
                submitInvitesToDatabase(invitee);
            }
        }

        Toast.makeText(getApplicationContext(), "submitinvites",Toast.LENGTH_LONG).show();

    }

    private void submitInvitesToDatabase(InviteAndUser invitee){
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("eventInvites").child(invitee.getUserID()).child(invitee.getEventID());
        ref.setValue(eventToDisplay.isTotallyVisible());
        finish();
    }
}
