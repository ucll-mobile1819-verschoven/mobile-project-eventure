package com.ucll.eventure.Fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.ucll.eventure.Adapters.EventAdapter;
import com.ucll.eventure.Data.Event;
import com.ucll.eventure.R;
import com.ucll.eventure.ViewEvent;

import java.util.ArrayList;

public class HomeFragment extends Fragment {
    private Context context;
    private LayoutInflater inflater;
    private ArrayList<Event> events;
    private ListView eventsListview;
    private ArrayList<String> orgs;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.home, container, false);
        return v;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public void onStart() {
        super.onStart();
        setHasOptionsMenu(false);
    }

    @Override
    public void onResume() {
        super.onResume();
        setHasOptionsMenu(false);
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (getView() != null) {
            eventsListview = getView().findViewById(R.id.home_listview);
            if(eventsListview != null){
                getEvents();
            }
        }


    }

    /**
     * Get's events from firebase
     */
    private void getEvents() {
        if (getActivity() == null) {
            Toast.makeText(context, getString(R.string.wrong), Toast.LENGTH_LONG).show();
        } else {
            events = new ArrayList<>();
            events.clear();
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Events");
            ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    GenericTypeIndicator<Event> t2 = new GenericTypeIndicator<Event>() {
                    };

                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        Event event = dataSnapshot.getValue(t2);
                        if (event != null) {
                            if (!contains(event))
                                events.add(event);

                        }

                    }

                    if (events != null) {
                        showEvents();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(context, databaseError.getMessage(), Toast.LENGTH_LONG).show();
                }

            });
        }

    }

    private void showEvents() {
        EventAdapter eventAdapter = new EventAdapter(context, events, inflater);
        eventsListview.setAdapter(eventAdapter);
        eventsListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Event clickedEvent = events.get(position);
                String event = new Gson().toJson(clickedEvent);
                if (getActivity() != null) {
                    Intent i = new Intent(getActivity(), ViewEvent.class);
                    i.putExtra("event", event);
                    startActivity(i);
                }
            }
        });
    }

    /**
     * Method that checks whether an event is already in the array or not
     *
     * @param event event to check for duplicate
     * @return boolean that indicated presence duplicate object
     */
    private boolean contains(@NonNull Event event) {
        for (Event event1 : events) {
            if (event1.getEventID().equals(event.getEventID()))
                return true;
        }

        return false;
    }
}
