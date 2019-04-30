package com.ucll.eventure.Adapters;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.ucll.eventure.Data.Event;
import com.ucll.eventure.MapsActivity;
import com.ucll.eventure.R;

import java.util.ArrayList;

public class EventAdapter extends BaseAdapter {
    private ArrayList<Event> events;
    private Activity context;

    public EventAdapter(Activity context, ArrayList<Event> events) {
        this.context = context;
        this.events = events;
    }

    @Override
    public int getCount() {
        return events.size();
    }

    @Override
    public Event getItem(int position) {
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
            vi = LayoutInflater.from(context).inflate(R.layout.item_event, parent, false);

        // Get View ID's
        // Set them

        TextView eventTitle = vi.findViewById(R.id.eventTitle);
        TextView eventDescription = vi.findViewById(R.id.eventDescription);
        ImageButton likeButton = vi.findViewById(R.id.likeButton);

        if (events.get(position) != null) {
            final Event toDisplay = events.get(position);

            eventTitle.setText(toDisplay.getEventTitle());
            eventDescription.setText(toDisplay.getShortDescription());

            likeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(context, "button clicked in eventadapter", Toast.LENGTH_SHORT).show();
                }
            });







            eventDescription.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    openView(toDisplay, context);
                }
            });

            eventTitle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    openView(toDisplay,context);
                }
            });
        }
        return vi;
    }

    private void openView(Event clickedEvent, Activity activity){
        String event = new Gson().toJson(clickedEvent);
        if (activity != null) {
            Intent i = new Intent(activity, MapsActivity.class);
            i.putExtra("event", event);
            context.startActivity(i);
        }
    }

}
