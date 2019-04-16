package com.ucll.eventure.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.ucll.eventure.Data.Invite;
import com.ucll.eventure.R;

import java.util.ArrayList;

//TODO: MAKE ADAPTER TO INVITE FRIENDS
public class InviteFriendGroupMembersAdapter extends BaseAdapter {
    private ArrayList<Invite> names;
    private Context context;

    public InviteFriendGroupMembersAdapter(Context context, ArrayList<Invite> names) {
        this.context = context;
        this.names = names;
    }

    @Override
    public int getCount() {
        return names.size();
    }

    @Override
    public Invite getItem(int position) {
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
            final String toDisplay = names.get(position).getUserName();
            friendName.setText(toDisplay);
        }
        return vi;
    }

}
