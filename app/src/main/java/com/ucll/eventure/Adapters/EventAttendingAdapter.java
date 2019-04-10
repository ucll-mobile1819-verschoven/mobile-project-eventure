package com.ucll.eventure.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.Gson;
import com.ucll.eventure.Data.Event;
import com.ucll.eventure.MapsActivity;
import com.ucll.eventure.R;

import java.util.ArrayList;

public class EventAttendingAdapter extends RecyclerView.Adapter<MyHolder>  {
    private int numCreated;
    private ArrayList<Event> events;
    private Activity context;

    public EventAttendingAdapter(Activity context, ArrayList<Event> events){
        this.numCreated = 0;
        this.context = context;
        this.events = events;

        Log.d("testmij", String.valueOf(events.size()));
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        numCreated++;
        Log.d("RV", "OncreateViewHolder ["+numCreated+"]");
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_layout, parent, false);

        MyHolder mh = new MyHolder(v);
        return mh;
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, final int position) {
        Log.d("RV", "OnBindViewHolder");
        Event event = events.get(position);
        Log.d("testmij", String.valueOf(event.getEventTitle()));
        String s = event.getEventTitle();
        holder.txt1.setText(s.substring(0, Math.min(s.length(), 13)));
        holder.txt1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String event = new Gson().toJson(events.get(position));
                Intent i = new Intent(context, MapsActivity.class);
                i.putExtra("event", event);
                context.startActivity(i);
            }
        });
    }

    @Override
    public int getItemCount() {
        return events.size();
    }
}

class MyHolder extends RecyclerView.ViewHolder {
    protected TextView txt1;

    MyHolder(View v) {
        super(v);
        this.txt1 = v.findViewById(R.id.txt1);
    }
}
