package com.ucll.eventure.Adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.ucll.eventure.Data.InviteAndUser;
import com.ucll.eventure.R;

import java.util.ArrayList;
import java.util.HashMap;

public class InviteFriendGroupNameAdapter extends BaseAdapter {
    private ArrayList<String> groupNames;
    private HashMap<String, ArrayList<InviteAndUser>> groupMembers;
    private Context context;
    private ArrayList<String> selectedList;
    private ArrayList<CheckBox> checkBoxes;

    public InviteFriendGroupNameAdapter(Context context, ArrayList<String> groupNames, HashMap<String, ArrayList<InviteAndUser>> groupMembers) {
        this.context = context;
        this.groupNames = groupNames;
        this.groupMembers = groupMembers;
        this.selectedList = new ArrayList<>();
        this.checkBoxes = new ArrayList<>();
    }

    @Override
    public int getCount() {
        return groupNames.size();
    }

    @Override
    public String getItem(int position) {
        return groupNames.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, final View convertView, ViewGroup parent) {
        View vi = convertView;
        if (vi == null)
            vi = LayoutInflater.from(context).inflate(R.layout.item_invite_friend_groupname, parent, false);

        // Get View ID's
        // Set them

        TextView friendName = vi.findViewById(R.id.friendName);
        CheckBox selector = vi.findViewById(R.id.selected);
        ListView members = vi.findViewById(R.id.group_members);

        checkBoxes.add(selector);

        if (groupNames.get(position) != null && groupMembers.get(groupNames.get(position)) != null) {
            final String toDisplay = groupNames.get(position);

            friendName.setText(toDisplay);

            InviteFriendGroupMembersAdapter adapter = new InviteFriendGroupMembersAdapter(context, groupMembers.get(groupNames.get(position)));
            members.setAdapter(adapter);

            vi.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    itemIsSelected(position, toDisplay);

                }
            });
        }
        return vi;
    }

    public void itemIsSelected(int position, String select) {
        CheckBox toCheck = checkBoxes.get(position);
        boolean selected = toCheck.isChecked();
        if (selected) {
            selectedList.remove(select);
            toCheck.setChecked(false);
        } else {
            selectedList.add(select);
            toCheck.setChecked(true);
        }
    }

    public ArrayList<String> getSelectedList() {
        return this.selectedList;
    }

}
