package com.ucll.eventure.Adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.ucll.eventure.ChatActivity;
import com.ucll.eventure.Data.Friend;
import com.ucll.eventure.Data.User;
import com.ucll.eventure.R;

import java.util.List;

public class FriendsAdapter extends BaseAdapter {

    private List<Friend> friends;
    private Context context;
    private User me;

    public FriendsAdapter(Context context, List<Friend> friends, User me){
        this.friends = friends; this.context=context; this.me = me;
    }


    @Override
    public int getCount() {
        return friends.size();
    }

    @Override
    public Friend getItem(int i) {
        return friends.get(i);
    }

    @Override

    public long getItemId(int position) {
        return position;
    }


    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View vi = view;
        if(vi == null)
            vi = LayoutInflater.from(context).inflate(R.layout.friend_layout, viewGroup, false);

        final ImageView userPic;
        if(Build.VERSION.SDK_INT >= 21){
            userPic = vi.findViewById(R.id.friend_pic2);
            userPic.setVisibility(View.VISIBLE);

            ImageView imageView = vi.findViewById(R.id.friend_pic1);
            imageView.setVisibility(View.GONE);
        } else {
            userPic = vi.findViewById(R.id.friend_pic1);
        }


        final TextView userName = vi.findViewById(R.id.user_name);
        final ImageView checkmark = vi.findViewById(R.id.checkMark);

        if(friends.get(i) != null){
            final Friend toDisplay = friends.get(i);

            if(toDisplay != null && toDisplay.getAccepted() != null ){
                if(toDisplay.getAccepted()){
                    checkmark.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.delete_bin));
                }

                StorageReference httpsReference = FirebaseStorage.getInstance().getReference().child("profilePictures").child(toDisplay.getUserID()).child("profile_picture.jpg");
                final long ONE_MEGABYTE = 1024 * 1024;
                httpsReference.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                    @Override
                    public void onSuccess(byte[] bytes) {
                        userPic.setImageBitmap(BitmapFactory.decodeByteArray(bytes, 0, bytes.length));
                        userPic.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                startChat(toDisplay, me);
                            }
                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle any errors
                    }
                });


                userName.setText(toDisplay.getName());
                userName.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startChat(toDisplay, me);
                    }
                });

                checkmark.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(toDisplay.getAccepted()){
                            DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("admin").child("Users").child(me.getDatabaseID()).child("friends").child(toDisplay.getUserID());
                            ref.removeValue();
                            friends.remove(toDisplay);
                            notifyDataSetChanged();
                        } else {
                            DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("friendRequests").child(me.getDatabaseID()).child(toDisplay.getUserID());
                            ref.removeValue();
                            toDisplay.setAccepted(true);
                            DatabaseReference ref2 = FirebaseDatabase.getInstance().getReference().child("admin").child("Users").child(me.getDatabaseID()).child("friends").child(toDisplay.getUserID());
                            ref2.setValue(toDisplay);
                            DatabaseReference ref3 = FirebaseDatabase.getInstance().getReference().child("friendRequests").child(toDisplay.getUserID()).child(me.getDatabaseID());
                            ref3.child("name").setValue(me.getName());
                            ref3.child("userID").setValue(me.getDatabaseID());
                            ref3.child("accepted").setValue(true);
                            checkmark.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.delete_bin));

                        }
                    }
                });
            }



        }
        return vi;
    }

    private void startChat(Friend toDisplay, User me){
        Intent i = new Intent(context, ChatActivity.class);
        i.putExtra("chatID", toDisplay.getUserID()+"_"+me.getDatabaseID());
        i.putExtra("friendName", toDisplay.getName());
        context.startActivity(i);
    }
}
