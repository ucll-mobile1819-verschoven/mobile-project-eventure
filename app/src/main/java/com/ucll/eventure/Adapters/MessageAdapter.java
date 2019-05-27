package com.ucll.eventure.Adapters;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.google.gson.Gson;
import com.ucll.eventure.Data.Event;
import com.ucll.eventure.Data.Message;
import com.ucll.eventure.Data.User;
import com.ucll.eventure.Data.UserDatabase;
import com.ucll.eventure.MapsActivity;
import com.ucll.eventure.R;

import java.util.ArrayList;

public class MessageAdapter extends BaseAdapter {
    private ArrayList<Message> events;
    private Activity context;
    private User me;

    public MessageAdapter(Activity context, ArrayList<Message> events, User me) {
        this.context = context;
        this.events = events;
        this.me = me;
    }

    @Override
    public int getCount() {
        return events.size();
    }

    @Override
    public Message getItem(int position) {
        return events.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, final View convertView, ViewGroup parent) {
        View vi = convertView;
        if (events.get(position) != null) {
            final Message toDisplay = events.get(position);

        if (vi == null){
            if(toDisplay.getSentBy().equals(me.getDatabaseID())){
                vi = LayoutInflater.from(context).inflate(R.layout.item_sent_message, parent, false);
            } else {
                vi = LayoutInflater.from(context).inflate(R.layout.item_received_message, parent, false);
            }
        }


        // Get View ID's
        // Set them

        TextView message = vi.findViewById(R.id.message);




            message.setText(toDisplay.getMessage());
        }
        return vi;
    }

}
