package com.ucll.eventure.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.ucll.eventure.Data.Friend;
import com.ucll.eventure.R;

import java.util.ArrayList;

public class InviteFriendGroupMembersAdapter extends BaseAdapter {
    private ArrayList<Friend> names;
    private Context context;

    public InviteFriendGroupMembersAdapter(Context context, ArrayList<Friend> names) {
        this.context = context;
        this.names = names;
    }

    @Override
    public int getCount() {
        return names.size();
    }

    @Override
    public Friend getItem(int position) {
        return names.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, final View convertView, ViewGroup parent) {
        View vi = convertView;
        if (vi == null)
            vi = LayoutInflater.from(context).inflate(R.layout.item_invite_friend_group, parent, false);

        // Get View ID's
        // Set them

        TextView friendName = vi.findViewById(R.id.friendName);

        if (names.get(position) != null) {
            final String toDisplay = names.get(position).getName();
            friendName.setText(toDisplay);
        }
        return vi;
    }

}
