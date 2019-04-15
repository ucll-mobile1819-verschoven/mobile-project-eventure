package com.ucll.eventure.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.AppCompatCheckBox;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.ucll.eventure.Data.Event;
import com.ucll.eventure.Data.Invite;
import com.ucll.eventure.MapsActivity;
import com.ucll.eventure.R;

import java.util.ArrayList;

//TODO: MAKE ADAPTER TO INVITE FRIENDS
public class InviteFriendAdapter extends BaseAdapter {
    private ArrayList<Invite> events;
    private Context context;
    private ArrayList<Invite> selectedList;

    public InviteFriendAdapter(Context context, ArrayList<Invite> events) {
        this.context = context;
        this.events = events;
        this.selectedList = new ArrayList<>();
    }

    @Override
    public int getCount() {
        return events.size();
    }

    @Override
    public Invite getItem(int position) {
        return events.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, final View convertView, ViewGroup parent) {
        View vi = convertView;
        if (vi == null)
            vi = LayoutInflater.from(context).inflate(R.layout.item_invite_friend, parent, false);

        // Get View ID's
        // Set them

        TextView friendName = vi.findViewById(R.id.friendName);
        final CheckBox eventDescription = vi.findViewById(R.id.selected);

        if (events.get(position) != null) {
            final Invite toDisplay = events.get(position);

            friendName.setText(toDisplay.getUserName());

            eventDescription.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    itemIsSelected(eventDescription.isSelected(), toDisplay, eventDescription, false);
                }
            });

            friendName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    itemIsSelected(eventDescription.isSelected(), toDisplay, eventDescription, true);
                }
            });
        }
        return vi;
    }

    private void itemIsSelected(boolean selected, Invite select, CheckBox check, boolean text) {
        if (selected) {
            selectedList.add(select);
            if(text)
                check.setChecked(true);
        } else {
            selectedList.remove(select);
            if(text)
                check.setChecked(false);
        }
    }

    public ArrayList<Invite> getSelectedList() {
        return this.selectedList;
    }

}
