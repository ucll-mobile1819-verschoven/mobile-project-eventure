package com.ucll.eventure.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.NotNull;
import com.ucll.eventure.Adapters.EventAdapter;
import com.ucll.eventure.Data.DeclineDatabase;
import com.ucll.eventure.Data.Event;
import com.ucll.eventure.Data.GoingDatabase;
import com.ucll.eventure.R;

import java.util.ArrayList;

public class PublicFragment extends Fragment {
    private Context context;
    private ArrayList<Event> publicEvents;
    private ListView otherEventsListView;
    private EventAdapter eventAdapter;
    private TextView title;
    private View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.public_event, container, false);
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

        if (getView() != null) {
            otherEventsListView = getView().findViewById(R.id.home_listview);
            title = getView().findViewById(R.id.title2);
            view = getView().findViewById(R.id.view2);
            if (otherEventsListView != null) {
                getEvents();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        setHasOptionsMenu(false);

        if (getView() != null) {
            otherEventsListView = getView().findViewById(R.id.home_listview);
            title = getView().findViewById(R.id.title2);
            view = getView().findViewById(R.id.view2);
            if (otherEventsListView != null) {
                getEvents();
            }
        }
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onActivityCreated(@NotNull Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (getView() != null) {
            otherEventsListView = getView().findViewById(R.id.home_listview);
            if (otherEventsListView != null) {
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
            publicEvents = new ArrayList<>();
            publicEvents.clear();
            eventAdapter = new EventAdapter(getActivity(), publicEvents);
            otherEventsListView.setAdapter(eventAdapter);
            final ArrayList<String> goingEvents = new GoingDatabase(getActivity()).readFromFile();
            final ArrayList<String> declinedEvents = new DeclineDatabase(getActivity()).readFromFile();
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("PublicEvents");
            ref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NotNull DataSnapshot snapshot) {
                    GenericTypeIndicator<Event> t2 = new GenericTypeIndicator<Event>() {
                    };

                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        Event event = dataSnapshot.getValue(t2);
                        if (event != null && !contains(event, publicEvents)) {
                            if (!goingEvents.contains(event.getEventID()) && !declinedEvents.contains(event.getEventID())) {
                                    publicEvents.add(event);
                            }
                        }

                    }

                    if (publicEvents != null) {
                        showEvents();
                    }
                }

                @Override
                public void onCancelled(@NotNull DatabaseError databaseError) {
                    Toast.makeText(context, databaseError.getMessage(), Toast.LENGTH_LONG).show();
                }

            });
        }

    }

    private void showEvents() {
        otherEventsListView.setVisibility(View.VISIBLE);
        eventAdapter.notifyDataSetChanged();
        otherEventsListView.setVisibility(View.VISIBLE);
        title.setVisibility(View.VISIBLE);
        view.setVisibility(View.VISIBLE);
    }

    private boolean contains(@NotNull Event event, ArrayList<Event> events) {
        for (Event event1 : events) {
            if (event1 != null && event1.getEventID().equals(event.getEventID()))
                return true;
        }

        return false;
    }
}
