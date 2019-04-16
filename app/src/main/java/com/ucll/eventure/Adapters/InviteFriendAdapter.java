package com.ucll.eventure.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.ucll.eventure.Data.InviteAndUser;
import com.ucll.eventure.R;

import java.util.ArrayList;

//TODO: MAKE ADAPTER TO INVITE FRIENDS
public class InviteFriendAdapter extends BaseAdapter {
    private ArrayList<InviteAndUser> events;
    private Context context;
    private ArrayList<InviteAndUser> selectedList;
    private ArrayList<CheckBox> checkBoxes;

    public InviteFriendAdapter(Context context, ArrayList<InviteAndUser> events) {
        this.context = context;
        this.events = events;
        this.selectedList = new ArrayList<>();
        this.checkBoxes = new ArrayList<>();
    }

    @Override
    public int getCount() {
        return events.size();
    }

    @Override
    public InviteAndUser getItem(int position) {
        return events.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, final View convertView, ViewGroup parent) {
        View vi = convertView;
        if (vi == null)
            vi = LayoutInflater.from(context).inflate(R.layout.item_invite_friend, parent, false);

        // Get View ID's
        // Set them

        TextView friendName = vi.findViewById(R.id.friendName);
        CheckBox selector = vi.findViewById(R.id.selected);

        checkBoxes.add(selector);

        if (events.get(position) != null) {
            final InviteAndUser toDisplay = events.get(position);

            friendName.setText(toDisplay.getUserName());

            vi.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    itemIsSelected(position, toDisplay);
                }
            });


        }
        return vi;
    }

    private void itemIsSelected(int position, InviteAndUser select) {
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

    public ArrayList<InviteAndUser> getSelectedList() {
        return this.selectedList;
    }

}
