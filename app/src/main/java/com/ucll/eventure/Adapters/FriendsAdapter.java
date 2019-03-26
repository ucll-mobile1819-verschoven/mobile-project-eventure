package com.ucll.eventure.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ucll.eventure.Data.Friend;
import com.ucll.eventure.R;

import java.util.List;

public class FriendsAdapter extends BaseAdapter {

    private List<Friend> friends;
    private Context context;
    private LayoutInflater inflater;

    public FriendsAdapter(){

    }

    public FriendsAdapter(Context context, List<Friend> friends, LayoutInflater inflater){
        this.friends = friends; this.context=context; this.inflater=inflater;
    }


    @Override
    public int getCount() {
        return friends.size();
    }

    @Override
    public Object getItem(int i) {
        return friends.get(i);
    }

    @Override
    public long getItemId(int i) {
        return Long.parseLong(friends.get(i).getUserID());
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View vi = view;
        if(vi == null)
            vi = LayoutInflater.from(context).inflate(R.layout.friend_layout, viewGroup, false);

        ImageView userPic = vi.findViewById(R.id.friend_pic);
        final TextView userName = vi.findViewById(R.id.user_name);
        TextView eventAmount = vi.findViewById(R.id.event_amount);
        LinearLayout friend = vi.findViewById(R.id.friend);

        if(friends.get(i) != null){
            final Friend toDisplay = friends.get(i);


            userName.setText(toDisplay.getName());
            eventAmount.setText(toDisplay.getEventAmount());

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
