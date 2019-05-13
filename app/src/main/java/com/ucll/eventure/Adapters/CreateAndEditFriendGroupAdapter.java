package com.ucll.eventure.Adapters;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.ucll.eventure.Data.Friend;
import com.ucll.eventure.R;

import java.util.ArrayList;

public class CreateAndEditFriendGroupAdapter extends BaseAdapter {

    private ArrayList<Friend> friends;
    private Context context;
    private LayoutInflater inflater;

    public CreateAndEditFriendGroupAdapter(Context context, ArrayList<Friend> friends, LayoutInflater inflater){
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
    public View getView(final int i, View view, ViewGroup viewGroup) {
        View vi = view;
        if(vi == null)
            vi = LayoutInflater.from(context).inflate(R.layout.group_friend_layout, viewGroup, false);

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
        final LinearLayout friend = vi.findViewById(R.id.friend);

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

            checkmark.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    friends.remove(toDisplay);
                    notifyDataSetChanged();
                }
            });

        }
        return vi;
    }

    public ArrayList<Friend> getSelected(){
        return this.friends;
    }
}
