package com.ucll.eventure;

import android.content.Context;
import android.annotation.*;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.NotNull;
import com.google.gson.Gson;
import com.ucll.eventure.Data.Event;
import com.ucll.eventure.Data.GoingDatabase;
import com.ucll.eventure.Data.UserDatabase;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class AddEventActivity extends AppCompatActivity {
    private EditText eventTitle, shortDescription, longDescription, countryCity, streetNumber, startTime, endTime;
    private Spinner visibility;
    private ArrayList<String> visibilityOptions;
    private HashMap<String, ArrayList<String>> groups;
    private Event toDisplay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);
        groups = new HashMap<>();

        if (getIntent().getStringExtra("mode") != null) {
            String mode = getIntent().getStringExtra("mode");
            if (mode.equals("edit")) {
                if (getIntent().getStringExtra("event") != null) {
                    toDisplay = new Gson().fromJson(getIntent().getStringExtra("event"), Event.class);
                    setupView();
                    getFriendGroups(getApplicationContext());
                    setTexts();
                } else {
                    Toast.makeText(getApplicationContext(), getString(R.string.wrong), Toast.LENGTH_LONG).show();
                }
            } else {
                if (mode.equals("new")) {
                    setupView();
                    getFriendGroups(getApplicationContext());
                }
            }
        }
    }

    private void setTexts() {
        TextView title = findViewById(R.id.title_add_or_edit);
        title.setText(getString(R.string.edit_event));

        eventTitle.setText(toDisplay.getEventTitle());
        shortDescription.setText(toDisplay.getShortDescription());
        longDescription.setText(toDisplay.getLongDescription());

        startTime.setText(toDisplay.getStartTime());
        endTime.setText(toDisplay.getEndTime());

        String[] address = toDisplay.getAddress().split(",");
        ArrayList<String> addresses = new ArrayList<>();
        for (String s : address)
            addresses.add(s.replace(" ", ""));

        if (addresses.size() >= 4) {
            countryCity.setText(addresses.get(0) + ", " + addresses.get(1));
            streetNumber.setText(addresses.get(2) + ", " + addresses.get(3));
        }
    }

    private void setupView() {
        eventTitle = findViewById(R.id.add_event_title);
        shortDescription = findViewById(R.id.add_event_short_description);
        longDescription = findViewById(R.id.add_event_long_description);
        countryCity = findViewById(R.id.add_event_location_country_city);
        streetNumber = findViewById(R.id.add_event_location_street_number);
        startTime = findViewById(R.id.add_event_starttime);
        endTime = findViewById(R.id.add_event_endtime);
        visibility = findViewById(R.id.add_event_visible_to);

        visibilityOptions = new ArrayList<>();
        visibilityOptions.add("Choose who the event will be visible for");
        visibilityOptions.add("Private");
        visibilityOptions.add("Public");
    }

    private void getFriendGroups(final Context context) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("admin").child("Users")
                .child(new UserDatabase(getApplicationContext()).readFromFile().getDatabaseID()).child("friendGroups");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NotNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    visibilityOptions.add(dataSnapshot.getKey());
                    ArrayList<String> ids = new ArrayList<>();
                    for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                        ids.add(dataSnapshot1.getKey());
                    }

                    groups.put(dataSnapshot.getKey(), ids);
                }

                ArrayAdapter<String> adapter = new ArrayAdapter<>(context,
                        android.R.layout.simple_spinner_item, visibilityOptions);
                visibility.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NotNull DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(), databaseError.getMessage(), Toast.LENGTH_LONG).show();
            }

        });

    }

    public void checkInput(View view) {
        String event_Title = eventTitle.getText().toString();
        String short_description = shortDescription.getText().toString();
        String long_description = longDescription.getText().toString();
        String country_and_city = countryCity.getText().toString();
        String street_and_number = streetNumber.getText().toString();
        String start_time = startTime.getText().toString();
        String end_time = endTime.getText().toString();

        if (!event_Title.isEmpty() && !short_description.isEmpty() && !long_description.isEmpty() &&
                !country_and_city.isEmpty() && !street_and_number.isEmpty() && !start_time.isEmpty() && !end_time.isEmpty() && !visibility.getSelectedItem().toString().equals("Choose who the event will be visible for")) {
            if (validCountryAndCity(country_and_city) && validStreetAndNumber(street_and_number) && validTime(start_time, end_time)) {
                checkBeforesubmitToDatabase(new Event("eventID", new UserDatabase(getApplicationContext()).readFromFile().getDatabaseID(), event_Title,
                        short_description, long_description, country_and_city + ", " + street_and_number, start_time, end_time, 0, false));
            } else {
                Toast.makeText(getApplicationContext(), "The format of your input is incorrect, please follow the hints", Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(getApplicationContext(), "Please fill in all fields", Toast.LENGTH_LONG).show();
        }
    }

    private void checkBeforesubmitToDatabase(Event toSubmit) {
        if (visibility.getSelectedItem().toString().equals("Public")) {
            toSubmit.setTotallyVisible(true);
            simpleSubmitToDatabase(toSubmit, "PublicEvents");
        } else {
            if (visibility.getSelectedItem().toString().equals("Private")) {
                toSubmit.setTotallyVisible(false);
                simpleSubmitToDatabase(toSubmit, "PrivateEvents");
            } else {
                toSubmit.setTotallyVisible(false);
                setVisibility(simpleSubmitToDatabase(toSubmit, "PrivateEvents"), groups.get(visibility.getSelectedItem().toString()));
            }
        }

        finish();
    }

    private String simpleSubmitToDatabase(Event toSubmit, String node) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child(node);
        String id = ref.push().getKey();
        if(toDisplay != null){
            id = toDisplay.getEventID();
            removeOld(node);
        }

        toSubmit.setEventID(id);
        ref.child(id).setValue(toSubmit);

        ArrayList<String> going = new GoingDatabase(getApplicationContext()).readFromFile();
        going.add(toSubmit.getEventID());
        new GoingDatabase(getApplicationContext()).writeToFile(going);

        String userID = new UserDatabase(getApplicationContext()).readFromFile().getDatabaseID();
        ref.child(id).child("attending").child(userID).setValue(userID);
        ref.child(id).child("visibleTo").child(userID).setValue(userID);

        return id;
    }

    private void removeOld(String node){
        if(toDisplay.isTotallyVisible() && node.equals("PrivateEvents")){
            DatabaseReference no = FirebaseDatabase.getInstance().getReference().child("PublicEvents").child(toDisplay.getEventID());
            no.removeValue();
        } else {
            if(!toDisplay.isTotallyVisible() && node.equals("PublicEvents")){
                DatabaseReference no = FirebaseDatabase.getInstance().getReference().child("PrivateEvents").child(toDisplay.getEventID());
                no.removeValue();
            }
        }
    }

    private void setVisibility(String nodeID, ArrayList<String> ids) {
        if(toDisplay != null){
            if(toDisplay.isTotallyVisible()){
                removeOld("PublicEvents");
            } else {
                removeOld("PrivateEvents");
            }

            nodeID = toDisplay.getEventID();
        }

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("PrivateEvents").child(nodeID).child("visibleTo");
        for (String id : ids) {
            ref.child(id).setValue(id);
        }

        String myID = new UserDatabase(getApplicationContext()).readFromFile().getDatabaseID();
        ref.child(myID).setValue(myID);
        sendInvites(nodeID, ids);
    }

    private void sendInvites(String nodeID, ArrayList<String> ids) {
        if(toDisplay != null){
            nodeID = toDisplay.getEventID();
        }

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("eventInvites");
        for (String id : ids) {
            ref.child(id).child(nodeID).setValue(nodeID);
        }
    }

    private Boolean validTime(String start, String end) {
        boolean toReturn = false;
        try {
            DateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
            Date starting = format.parse(start);
            Date ending = format.parse(end);

            if (starting.before(ending)) {
                toReturn = true;
            } else {
                Toast.makeText(getApplicationContext(), "The start should be before the end of your event", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {

        }

        Log.d("eventuretaggTime", String.valueOf(toReturn));
        return toReturn;
    }

    private Boolean validCountryAndCity(String toCheck) {
        String[] splitted = toCheck.split(",");
        Log.d("eventuretaggC&C", String.valueOf(splitted.length > 1));
        return splitted.length > 1;
    }

    private Boolean validStreetAndNumber(String toCheck) {
        boolean toReturn = false;
        String[] splitted = toCheck.split(",");

        if (splitted.length <= 1)
            toReturn = false;

        try {
            int x = Integer.valueOf(splitted[1].replace(" ", ""));
            toReturn = true;
        } catch (Exception e) {

        }
        Log.d("eventuretaggC&C", String.valueOf(toReturn));
        return toReturn;
    }
}
