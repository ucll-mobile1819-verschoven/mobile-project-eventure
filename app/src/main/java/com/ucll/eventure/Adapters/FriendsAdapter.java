package com.ucll.eventure.Adapters;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.ucll.eventure.Data.Friend;
import com.ucll.eventure.R;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class FriendsAdapter extends BaseAdapter {

    private List<Friend> friends;
    private Context context;
    private LayoutInflater inflater;

    public FriendsAdapter(Context context, List<Friend> friends, LayoutInflater inflater){
        this.friends = friends; this.context=context; this.inflater=inflater;
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

        final CircleImageView userPic = vi.findViewById(R.id.friend_pic);
        final TextView userName = vi.findViewById(R.id.user_name);
        LinearLayout friend = vi.findViewById(R.id.friend);

        if(friends.get(i) != null){
            final Friend toDisplay = friends.get(i);

            StorageReference httpsReference = FirebaseStorage.getInstance().getReference().child("profilePictures").child(toDisplay.getUserID()).child("profile_picture.jpg");
            final long ONE_MEGABYTE = 1024 * 1024;
            httpsReference.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                @Override
                public void onSuccess(byte[] bytes) {
                    userPic.setImageBitmap(BitmapFactory.decodeByteArray(bytes, 0, bytes.length));
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle any errors
                }
            });


            userName.setText(toDisplay.getName());

            friend.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Toast.makeText(context, "trying to open "+userName+"\nID: "+toDisplay.getUserID(), Toast.LENGTH_SHORT).show();
                        }
                    }
            );

        }
        return vi;
    }
}
