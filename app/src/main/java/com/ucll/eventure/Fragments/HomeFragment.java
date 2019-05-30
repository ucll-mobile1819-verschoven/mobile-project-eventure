package com.ucll.eventure.Fragments;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.ybq.android.spinkit.SpinKitView;
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

public class HomeFragment extends Fragment {
    private Context context;
    private ArrayList<Event> myOtherEvents;
    private ArrayList<Event> myAttendingEvents;
    private ListView otherEventsListView;
    private RecyclerView attendingListView;
    private ArrayList<String> publicInvites;
    private ArrayList<String> privateInvites;
    private TextView title1;
    private TextView title2;
    private View view1;
    private View view2;
    private EventAdapter eventAdapter;
    private EventAttendingAdapter adapter;
    private SpinKitView load;
    private TextView empty;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        context = getContext();
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

        if (getView() != null) {
            setupView();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        setHasOptionsMenu(false);

        if (getView() != null) {
            setupView();
        }
    }

    private void setupView(){
        otherEventsListView = getView().findViewById(R.id.home_listview);
        attendingListView = getView().findViewById(R.id.attending_listview);
        title1 = getView().findViewById(R.id.title1);
        title2 = getView().findViewById(R.id.title2);
        view1 = getView().findViewById(R.id.view1);
        view2 = getView().findViewById(R.id.view2);
        load = getView().findViewById(R.id.spin_kit);
        FloatingActionButton fab = getView().findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addEvent();
            }
        });
        empty = getView().findViewById(R.id.empty_field);
        if (otherEventsListView != null && attendingListView != null && title1 != null && title2 != null && view1 != null && view2 != null && load != null && empty != null) {
            getInvites();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (getView() != null) {
            setupView();
        }


    }

    private void getInvites() {
        publicInvites = new ArrayList<>();
        publicInvites.clear();
        privateInvites = new ArrayList<>();
        privateInvites.clear();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("eventInvites").child(new UserDatabase(getActivity()).readFromFile().getDatabaseID());
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                GenericTypeIndicator<Boolean> t2 = new GenericTypeIndicator<Boolean>() {
                };

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    boolean visible = dataSnapshot.getValue(t2);
                    if (visible){
                        publicInvites.add(dataSnapshot.getKey());
                    } else {
                        privateInvites.add(dataSnapshot.getKey());
                    }
                }

                if (publicInvites != null && privateInvites != null) {
                    getPrivateEvents();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
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
            getEvents(publicInvites, "PublicEvents", "start");
        }
    }

    private void getEvents(ArrayList<String> ids, final String node, final String step){
        final ArrayList<String> goingEvents = new GoingDatabase(getActivity()).readFromFile();
        final ArrayList<String> declinedEvents = new DeclineDatabase(getActivity()).readFromFile();
        if(ids.isEmpty())
            checkMe(step, goingEvents);

        for (String id : ids) {
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child(node).child(id);
            ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
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
                        checkMe(step, goingEvents);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.d("eventure", databaseError.getMessage());
                    if(context != null){
                        if (myOtherEvents != null && myAttendingEvents != null) {
                            checkMe(step, goingEvents);
                        }
                    }

                    //Toast.makeText(context, databaseError.getMessage() + ", please contact support", Toast.LENGTH_LONG).show();
                }

            });
        }
    }

    private void checkMe(String step, ArrayList<String> goingEvents){
        if(context != null && getActivity() != null){
            if(step.equals("start")){
                getEvents(goingEvents, "PrivateEvents","second");
            } else {
                if(step.equals("second")){
                    getEvents(privateInvites, "PrivateEvents", "third");
                } else {
                    if(step.equals("third")){
                        getPublicEvents();
                        Log.d("interest", "getEvents called");
                    }
                }
            }
        }

    }

    /**
     * Get's events from firebase
     */
    private void getPublicEvents() {
        if (getActivity() == null) {
            Toast.makeText(context, getString(R.string.wrong), Toast.LENGTH_LONG).show();
        } else {
            final ArrayList<String> goingEvents = new GoingDatabase(getActivity()).readFromFile();
            final ArrayList<String> declinedEvents = new DeclineDatabase(getActivity()).readFromFile();

            DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("PublicEvents");
            ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
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
                public void onCancelled(DatabaseError databaseError) {
                    //Toast.makeText(context, databaseError.getMessage(), Toast.LENGTH_LONG).show();
                }

            });
        }

    }

    public void addEvent() {
        Intent i = new Intent(context, AddEventActivity.class);
        i.putExtra("mode","new");
        context.startActivity(i);
    }

    private void showEvents() {
        Log.d("interest", "called");
        load.setVisibility(View.GONE);

        if(myOtherEvents.isEmpty() && myAttendingEvents.isEmpty()){
            Typeface custom_font = ResourcesCompat.getFont(getContext(), R.font.font);
            empty.setTypeface(custom_font);
            empty.setVisibility(View.VISIBLE);

            view2.setVisibility(View.GONE);
            title2.setVisibility(View.GONE);
            otherEventsListView.setVisibility(View.GONE);

            view1.setVisibility(View.GONE);
            title1.setVisibility(View.GONE);
            attendingListView.setVisibility(View.GONE);
        } else {
            empty.setVisibility(View.GONE);
        }
        if (myOtherEvents.size() == 0) {
            view2.setVisibility(View.GONE);
            title2.setVisibility(View.GONE);
            otherEventsListView.setVisibility(View.GONE);
        } else {
            if(!myOtherEvents.isEmpty()){
                view2.setVisibility(View.VISIBLE);
                title2.setVisibility(View.VISIBLE);
                otherEventsListView.setVisibility(View.VISIBLE);
                eventAdapter.notifyDataSetChanged();
            }
        }

        if (myAttendingEvents.size() == 0) {
            view1.setVisibility(View.GONE);
            title1.setVisibility(View.GONE);
            attendingListView.setVisibility(View.GONE);
        } else {
            if(!myAttendingEvents.isEmpty()){
                view1.setVisibility(View.VISIBLE);
                title1.setVisibility(View.VISIBLE);
                attendingListView.setVisibility(View.VISIBLE);
                LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
                attendingListView.setLayoutManager(layoutManager);
                adapter.notifyDataSetChanged();
            }
        }
    }

    /**
     * Method that checks whether an event is already in the array or not
     *
     * @param event event to check for duplicate
     * @return boolean that indicated presence duplicate object
     */
    private boolean contains(Event event, ArrayList<Event> events) {
        if(event != null && event.getEventID() != null){
            for (Event event1 : events) {
                if (event1 != null && event1.getEventID() != null && event1.getEventID().equals(event.getEventID()))
                    return true;
            }
        }

        return false;
    }
}


