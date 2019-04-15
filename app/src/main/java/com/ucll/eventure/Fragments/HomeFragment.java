package com.ucll.eventure.Fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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
import com.ucll.eventure.Adapters.EventAdapter;
import com.ucll.eventure.Adapters.EventAttendingAdapter;
import com.ucll.eventure.AddEventActivity;
import com.ucll.eventure.Data.DeclineDatabase;
import com.ucll.eventure.Data.Event;
import com.ucll.eventure.Data.GoingDatabase;
import com.ucll.eventure.Data.UserDatabase;
import com.ucll.eventure.R;

import java.util.ArrayList;
//TODO: PLACEHOLDER TILL EVENTS ARE LOADED?

public class HomeFragment extends Fragment {
    private Context context;
    private ArrayList<Event> myOtherEvents;
    private ArrayList<Event> myAttendingEvents;
    private ListView otherEventsListView;
    private RecyclerView attendingListView;
    private ArrayList<String> inviteIDs;
    private TextView title1;
    private TextView title2;
    private View view1;
    private View view2;
    private EventAdapter eventAdapter;
    private EventAttendingAdapter adapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.home, container, false);
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

        if (getView() != null) {
            otherEventsListView = getView().findViewById(R.id.home_listview);
            attendingListView = getView().findViewById(R.id.attending_listview);
            title1 = getView().findViewById(R.id.title1);
            title2 = getView().findViewById(R.id.title2);
            view1 = getView().findViewById(R.id.view1);
            view2 = getView().findViewById(R.id.view2);
            FloatingActionButton fab = getView().findViewById(R.id.fab);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    addEvent();
                }
            });
            if (otherEventsListView != null && attendingListView != null && title1 != null && title2 != null && view1 != null && view2 != null) {
                getInvites();
            }
        }
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (getView() != null) {
            otherEventsListView = getView().findViewById(R.id.home_listview);
            attendingListView = getView().findViewById(R.id.attending_listview);
            title1 = getView().findViewById(R.id.title1);
            title2 = getView().findViewById(R.id.title2);
            view1 = getView().findViewById(R.id.view1);
            view2 = getView().findViewById(R.id.view2);
            if (otherEventsListView != null && attendingListView != null && title1 != null && title2 != null && view1 != null && view2 != null) {
                getInvites();
            }
        }


    }

    private void getInvites() {
        inviteIDs = new ArrayList<>();
        inviteIDs.clear();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("eventInvites").child(new UserDatabase(getActivity()).readFromFile().getDatabaseID());
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                GenericTypeIndicator<String> t2 = new GenericTypeIndicator<String>() {
                };

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    String eventID = dataSnapshot.getValue(t2);
                    if (!inviteIDs.contains(eventID))
                        inviteIDs.add(eventID);

                }

                if (inviteIDs != null) {
                    getPrivateEvents();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(context, databaseError.getMessage(), Toast.LENGTH_LONG).show();
            }

        });
    }

    private void getPrivateEvents() {
        if (getActivity() == null) {
            Toast.makeText(context, getString(R.string.wrong), Toast.LENGTH_LONG).show();
        } else {
            myOtherEvents = new ArrayList<>();
            myOtherEvents.clear();
            myAttendingEvents = new ArrayList<>();
            myAttendingEvents.clear();
            eventAdapter = new EventAdapter(getActivity(), myOtherEvents);
            otherEventsListView.setAdapter(eventAdapter);
            adapter = new EventAttendingAdapter(getActivity(), myAttendingEvents);
            attendingListView.setAdapter(adapter);
            final ArrayList<String> goingEvents = new GoingDatabase(getActivity()).readFromFile();
            final ArrayList<String> declinedEvents = new DeclineDatabase(getActivity()).readFromFile();
            inviteIDs.addAll(goingEvents);
            for (String id : inviteIDs) {

                DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("PrivateEvents").child(id);
                ref.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        GenericTypeIndicator<Event> t2 = new GenericTypeIndicator<Event>() {
                        };

                        Event event = snapshot.getValue(t2);
                        if (event != null) {
                            if (!contains(event, myOtherEvents) && !contains(event, myAttendingEvents)) {
                                if (!declinedEvents.contains(event.getEventID())) {
                                    if (goingEvents.contains(event.getEventID())) {
                                        myAttendingEvents.add(event);
                                    } else {
                                        myOtherEvents.add(event);

                                    }
                                }
                            }


                        }

                        if (myOtherEvents != null && myAttendingEvents != null) {
                            getEvents();
                            Log.d("interest", "getEvents called");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.d("eventure", databaseError.getMessage());
                        //Toast.makeText(context, databaseError.getMessage() + ", please contact support", Toast.LENGTH_LONG).show();
                    }

                });
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
            final ArrayList<String> goingEvents = new GoingDatabase(getActivity()).readFromFile();
            final ArrayList<String> declinedEvents = new DeclineDatabase(getActivity()).readFromFile();

            DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("PublicEvents");
            ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    GenericTypeIndicator<Event> t2 = new GenericTypeIndicator<Event>() {
                    };

                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        Event event = dataSnapshot.getValue(t2);
                        if (event != null) {
                            if (!contains(event, myOtherEvents) && !contains(event, myAttendingEvents)) {
                                if (!declinedEvents.contains(event.getEventID())) {
                                    if (goingEvents.contains(event.getEventID())) {
                                        myAttendingEvents.add(event);
                                    }
                                }
                            }
                        }
                    }

                    if (myOtherEvents != null && myAttendingEvents != null) {
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

    public void addEvent() {
        Intent i = new Intent(context, AddEventActivity.class);
        context.startActivity(i);
    }

    private void showEvents() {
        Log.d("interest", "called");
        if (myOtherEvents.size() == 0) {
            view2.setVisibility(View.GONE);
            title2.setVisibility(View.GONE);
            otherEventsListView.setVisibility(View.GONE);
        } else {
            view2.setVisibility(View.VISIBLE);
            title2.setVisibility(View.VISIBLE);
            otherEventsListView.setVisibility(View.VISIBLE);
            eventAdapter.notifyDataSetChanged();
        }

        if (myAttendingEvents.size() == 0) {
            view1.setVisibility(View.GONE);
            title1.setVisibility(View.GONE);
            attendingListView.setVisibility(View.GONE);
        } else {
            view1.setVisibility(View.VISIBLE);
            title1.setVisibility(View.VISIBLE);
            attendingListView.setVisibility(View.VISIBLE);
            LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
            attendingListView.setLayoutManager(layoutManager);
            adapter.notifyDataSetChanged();
        }
    }

    /**
     * Method that checks whether an event is already in the array or not
     *
     * @param event event to check for duplicate
     * @return boolean that indicated presence duplicate object
     */
    private boolean contains(@NonNull Event event, ArrayList<Event> events) {
        for (Event event1 : events) {
            if (event1 != null && event1.getEventID().equals(event.getEventID()))
                return true;
        }

        return false;
    }
}
