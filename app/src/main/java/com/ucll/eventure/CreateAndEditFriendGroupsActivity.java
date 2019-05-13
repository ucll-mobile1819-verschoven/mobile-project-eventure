package com.ucll.eventure;

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
    private InviteFriendAdapter inviteFriendAdapter;
    private InviteFriendGroupNameAdapter inviteFriendGroupNameAdapter;
    private CreateAndEditFriendGroupAdapter createAndEditFriendGroupAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_groups);

        friendGroups = findViewById(R.id.your_friend_groups);
        friendsListView = findViewById(R.id.your_friends);
        groups = new HashMap<>();
        friendGroupNames = new ArrayList<>();
        friendsUserHas = new ArrayList<>();

        getFriendGroups();
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
            setGroupName();
        } else {
            if(!inviteFriendAdapter.getSelectedList().isEmpty() && inviteFriendGroupNameAdapter.getSelectedList().isEmpty()){
                createAndEditFriendGroupAdapter = new CreateAndEditFriendGroupAdapter(getApplicationContext(),inviteFriendAdapter.getSelectedList(),getLayoutInflater());
                setLayoutSelectedFriends();
            } else {
                if(inviteFriendAdapter.getSelectedList().isEmpty() && !inviteFriendGroupNameAdapter.getSelectedList().isEmpty() && inviteFriendGroupNameAdapter.getSelectedList().size() <= 1){
                    createAndEditFriendGroupAdapter = new CreateAndEditFriendGroupAdapter(getApplicationContext(),groups.get(inviteFriendGroupNameAdapter.getSelectedList().get(0)),getLayoutInflater());
                    setLayoutSelectedFriends();
                    setGroupName();
                }
            }
        }

    }

    private void setLayoutSelectedFriends(){
        setContentView(R.layout.create_new_friendgroup);
        ListView members = findViewById(R.id.selected_friends);
        members.setAdapter(createAndEditFriendGroupAdapter);
    }

    private void setGroupName(){
        EditText groupname = findViewById(R.id.group_nametext);
        groupname.setText(inviteFriendGroupNameAdapter.getSelectedList().get(0));
    }

    public void createNewFriendGroup(View v){
        //TODO: Set new adapter with delete option!
        //TODO: Get members & name
        //TODO: UPDATE EVERYTHING IN DATABASE

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
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("admin")
                .child("Users").child(new UserDatabase(getApplicationContext()).readFromFile().getDatabaseID()).child("friendGroups");
        if(inviteFriendGroupNameAdapter.getSelectedList().isEmpty()){
            for(Friend friend : createAndEditFriendGroupAdapter.getSelected()){
                ref.child(groupName).child(friend.getUserID()).setValue(friend);
            }
        } else {
            Toast.makeText(getApplicationContext(), "else", Toast.LENGTH_LONG).show();
            ref.child(inviteFriendGroupNameAdapter.getSelectedList().get(0)).removeValue();
            for(Friend friend : createAndEditFriendGroupAdapter.getSelected()){
                ref.child(groupName).child(friend.getUserID()).setValue(friend);
            }
        }

        finish();

    }

    private void getFriendGroups(){
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("admin").child("Users")
                .child(new UserDatabase(getApplicationContext()).readFromFile().getDatabaseID()).child("friendGroups");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                GenericTypeIndicator<Friend> t = new GenericTypeIndicator<Friend>() {
                };
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    friendGroupNames.add(dataSnapshot.getKey());
                    ArrayList<Friend> ids = new ArrayList<>();
                    for(DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()){
                        ids.add(dataSnapshot1.getValue(t));
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
                GenericTypeIndicator<Friend> t = new GenericTypeIndicator<Friend>() {
                };
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    //ID, NAME
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

    private void display(){
        inviteFriendAdapter = new InviteFriendAdapter(getApplicationContext(), friendsUserHas);
        friendsListView.setAdapter(inviteFriendAdapter);

        inviteFriendGroupNameAdapter = new InviteFriendGroupNameAdapter(getApplicationContext(), friendGroupNames, groups);
        friendGroups.setAdapter(inviteFriendGroupNameAdapter);
    }
}
