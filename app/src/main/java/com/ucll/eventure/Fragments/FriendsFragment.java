package com.ucll.eventure.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.NotNull;
import com.google.gson.Gson;
import com.ucll.eventure.Adapters.FriendsAdapter;
import com.ucll.eventure.Data.Friend;
import com.ucll.eventure.Data.UserDatabase;
import com.ucll.eventure.R;

import java.util.ArrayList;
import java.util.List;
import java.util.zip.Inflater;

import static com.facebook.FacebookSdk.getApplicationContext;

public class FriendsFragment extends Fragment {

    // Android Layout
    private View view;
    private ListView friendsList;
    private TextView searchText;
    private List<Friend> friends;
    private Context context;
    private LayoutInflater inflater;


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
            getFriends();
        }
    }

    private void getFriends(){
        if (getActivity() == null){
            Toast.makeText(context,"ID NOT GET",Toast.LENGTH_LONG).show();
        } else {
            friends = new ArrayList<>();
            friends.clear();
            firebase = FirebaseDatabase
                    .getInstance()
                    .getReference()
                    .child("friends")
                    .child(new UserDatabase(context).readFromFile().getDatabaseID());

            firebase.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    GenericTypeIndicator<Friend> f2 = new GenericTypeIndicator<Friend>() {
                    };

                    for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                        Friend friend = snapshot.getValue(f2);
                        Toast.makeText(context,friend.getUserID(),Toast.LENGTH_LONG).show();
                        if(friend!=null){
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
        FriendsAdapter adapter = new FriendsAdapter(context,friends,inflater);
        friendsList.setAdapter(adapter);
        /*
        friendsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Friend clickedFriend = friends.get(i);
                String friend = new Gson().toJson(clickedFriend);
                if(getActivity() != null){
                    Intent intent = new Intent(getActivity(), View
                }
            }
        });
        */
    }
}
