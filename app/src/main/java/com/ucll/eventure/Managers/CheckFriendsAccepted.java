package com.ucll.eventure.Managers;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.ucll.eventure.Data.Friend;
import com.ucll.eventure.Data.User;
import com.ucll.eventure.R;

public class CheckFriendsAccepted {
    private User me;
    private Context context;

    public CheckFriendsAccepted(Context context, User me) {
        this.me = me;
        this.context = context;
        init();
    }

    private void init(){
        final DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("friendRequests").child(me.getDatabaseID());
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    GenericTypeIndicator<Friend> t = new GenericTypeIndicator<Friend>() {
                    };
                    if (snapshot != null) {
                        Friend friend = snapshot.getValue(t);
                        if (friend.getAccepted()) {
                            ref.child(snapshot.getKey()).removeValue();
                            DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("admin").child("Users").child(me.getDatabaseID())
                                    .child("friends").child(friend.getUserID());
                            ref.setValue(friend);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(context, context.getString(R.string.wrong), Toast.LENGTH_LONG).show();
            }
        });
    }
}
