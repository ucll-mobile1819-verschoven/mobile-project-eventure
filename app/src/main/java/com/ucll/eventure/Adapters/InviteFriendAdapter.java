package com.ucll.eventure.Adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.ucll.eventure.Data.Friend;
import com.ucll.eventure.R;

import java.util.ArrayList;

public class InviteFriendAdapter extends BaseAdapter {
    private ArrayList<Friend> events;
    private Context context;
    private ArrayList<Friend> selectedList;
    private ArrayList<CheckBox> checkBoxes;

    public InviteFriendAdapter(Context context, ArrayList<Friend> events) {
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
    public Friend getItem(int position) {
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
            final Friend toDisplay = events.get(position);

            friendName.setText(toDisplay.getName());

            vi.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.d("myFreeTime", String.valueOf(selectedList.size()));
                    itemIsSelected(position, toDisplay);
                }
            });
        }
        return vi;
    }

    public void itemIsSelected(int position, Friend select) {
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

    public ArrayList<Friend> getSelectedList() {
        return this.selectedList;
    }

}
