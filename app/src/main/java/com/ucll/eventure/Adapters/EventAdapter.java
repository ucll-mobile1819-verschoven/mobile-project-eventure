package com.ucll.eventure.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.ucll.eventure.Data.Event;
import com.ucll.eventure.R;

import java.util.ArrayList;

public class EventAdapter extends BaseAdapter {
    private ArrayList<Event> events;
    private LayoutInflater inflater;
    private Context context;

    public EventAdapter(Context context, ArrayList<Event> events, LayoutInflater inflater) {
        this.context = context;
        this.events = events;
        this.inflater = inflater;
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
    public View getView(int position, View convertView, ViewGroup parent) {
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
        }
        return vi;
    }

}
