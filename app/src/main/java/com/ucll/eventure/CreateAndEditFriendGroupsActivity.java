package com.ucll.eventure;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.ucll.eventure.Adapters.CreateAndEditFriendGroupAdapter;
import com.ucll.eventure.Adapters.InviteFriendAdapter;
import com.ucll.eventure.Adapters.InviteFriendGroupNameAdapter;
import com.ucll.eventure.Data.Friend;
import com.ucll.eventure.Data.User;
import com.ucll.eventure.Data.UserDatabase;

import java.util.ArrayList;
import java.util.HashMap;

public class CreateAndEditFriendGroupsActivity extends AppCompatActivity {
    private HashMap<String, ArrayList<Friend>> groups;
    private ListView friendGroups;
    private ListView friendsListView;
    private ArrayList<String> friendGroupNames;
    private ArrayList<Friend> friendsUserHas;
    private HashMap<String, String> groupIDs;
    private User me;
    private InviteFriendAdapter inviteFriendAdapter;
    private InviteFriendGroupNameAdapter inviteFriendGroupNameAdapter;
    private CreateAndEditFriendGroupAdapter createAndEditFriendGroupAdapter;
    private DatabaseReference deleteRef;
    private boolean editing;
    private ArrayList<String> ids;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_groups);

        friendGroups = findViewById(R.id.your_friend_groups);
        friendsListView = findViewById(R.id.your_friends);
        groups = new HashMap<>();
        friendGroupNames = new ArrayList<>();
        friendsUserHas = new ArrayList<>();
        groupIDs = new HashMap<>();
        ids = new ArrayList<>();

        me = new UserDatabase(getApplicationContext()).readFromFile();
        editing = false;
        getFriendGroupNames();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }

    public void continueThru (View v){
        if(!inviteFriendAdapter.getSelectedList().isEmpty() && !inviteFriendGroupNameAdapter.getSelectedList().isEmpty()){
            inviteFriendAdapter.getSelectedList().addAll(groups.get(inviteFriendGroupNameAdapter.getSelectedList().get(0)));
            createAndEditFriendGroupAdapter = new CreateAndEditFriendGroupAdapter(getApplicationContext(),inviteFriendAdapter.getSelectedList(),getLayoutInflater());
            setLayoutSelectedFriends();
        } else {
            if(!inviteFriendAdapter.getSelectedList().isEmpty() && inviteFriendGroupNameAdapter.getSelectedList().isEmpty()){
                createAndEditFriendGroupAdapter = new CreateAndEditFriendGroupAdapter(getApplicationContext(),inviteFriendAdapter.getSelectedList(),getLayoutInflater());
                setLayoutSelectedFriends();
            } else {
                if(inviteFriendAdapter.getSelectedList().isEmpty() && !inviteFriendGroupNameAdapter.getSelectedList().isEmpty() && inviteFriendGroupNameAdapter.getSelectedList().size() == 1){
                    createAndEditFriendGroupAdapter = new CreateAndEditFriendGroupAdapter(getApplicationContext(),groups.get(inviteFriendGroupNameAdapter.getSelectedList().get(0)),getLayoutInflater());
                    setLayoutSelectedFriends();
                }
            }
        }

    }

    private void setLayoutSelectedFriends(){
        final Context context = this;
        if(!inviteFriendGroupNameAdapter.getSelectedList().isEmpty()){
            String groupID = groupIDs.get(inviteFriendGroupNameAdapter.getSelectedList().get(0));

            deleteRef = FirebaseDatabase.getInstance().getReference().child("friendGroups").child(groupID);
            DatabaseReference updateRef = deleteRef.child("admin");
            updateRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    GenericTypeIndicator<String> t = new GenericTypeIndicator<String>() {
                    };
                    if(me.getDatabaseID().equals(dataSnapshot.getValue().toString())){
                        completeView();
                        editing = true;
                    } else {
                        Toast.makeText(context, context.getString(R.string.nao), Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        } else {
            completeView();
        }
    }

    private void completeView(){
        setContentView(R.layout.create_new_friendgroup);
        ListView members = findViewById(R.id.selected_friends);
        members.setAdapter(createAndEditFriendGroupAdapter);
        setGroupName();
    }

    private void setGroupName(){
        EditText groupname = findViewById(R.id.group_nametext);
        if(!inviteFriendGroupNameAdapter.getSelectedList().isEmpty())
        groupname.setText(inviteFriendGroupNameAdapter.getSelectedList().get(0));
    }

    public void createNewFriendGroup(View v){
        EditText groupname = findViewById(R.id.group_nametext);

        if(groupname != null){
            String groupnamea = groupname.getText().toString();
            if(!groupnamea.isEmpty()){
                updateFirebaseGroup(groupnamea);
            } else {
                Toast.makeText(getApplicationContext(), getString(R.string.wrong), Toast.LENGTH_LONG).show();
            }
        }
    }

    private void updateFirebaseGroup(String groupName){
        DatabaseReference startRef = FirebaseDatabase.getInstance().getReference().child("friendGroups");
        DatabaseReference ref = startRef.push();
        if(editing){
            ref = deleteRef;
            ref.removeValue();
        }
        if(inviteFriendGroupNameAdapter.getSelectedList().isEmpty()){
            for(Friend friend : createAndEditFriendGroupAdapter.getSelected()){
                ref.child(friend.getUserID()).setValue(friend);
            }
        } else {
            ref.child(inviteFriendGroupNameAdapter.getSelectedList().get(0)).removeValue();
            for(Friend friend : createAndEditFriendGroupAdapter.getSelected()) {
                ref.child(friend.getUserID()).setValue(friend);
            }
        }

        if(!createAndEditFriendGroupAdapter.isEmpty()){
            ref.child("friendGroupName").setValue(groupName);
            ref.child("admin").setValue(new UserDatabase(getApplicationContext()).readFromFile().getDatabaseID());
        }


        finish();

    }

    private void getFriendGroups(){
        if(ids.isEmpty())
            getFriends();

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
                    groupIDs.put(name, groupID);
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

    private void getFriendGroupNames(){
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

    private void getFriends(){
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("admin").child("Users")
                .child(new UserDatabase(getApplicationContext()).readFromFile().getDatabaseID()).child("friends");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                GenericTypeIndicator<Friend> t = new GenericTypeIndicator<Friend>() {
                };
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    if(!contains(dataSnapshot.getValue(t), friendsUserHas))
                        friendsUserHas.add(dataSnapshot.getValue(t));
                }

                display();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(), databaseError.getMessage(), Toast.LENGTH_LONG).show();
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

    private void display(){
        inviteFriendAdapter = new InviteFriendAdapter(getApplicationContext(), friendsUserHas);
        friendsListView.setAdapter(inviteFriendAdapter);

        inviteFriendGroupNameAdapter = new InviteFriendGroupNameAdapter(getApplicationContext(), friendGroupNames, groups);
        friendGroups.setAdapter(inviteFriendGroupNameAdapter);
    }
}
