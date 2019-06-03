package com.ucll.eventure;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
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
import com.ucll.eventure.Data.Friend;
import com.ucll.eventure.Data.GoingDatabase;
import com.ucll.eventure.Data.User;
import com.ucll.eventure.Data.UserDatabase;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class AddEventActivity extends AppCompatActivity {
    private EditText eventTitle, shortDescription, longDescription, countryCity, streetNumber;
    private TextView startTime, endTime;
    private Spinner visibility;
    private ArrayList<String> visibilityOptions;
    private HashMap<String, ArrayList<String>> groups;
    private Event toDisplay;
    private ArrayList<String> oldInvitees;
    private ArrayList<String> oldAttanding;
    private User me;
    private ArrayAdapter<String> adapter;
    private ArrayList<String> ids;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);
        groups = new HashMap<>();
        me = new UserDatabase(getApplicationContext()).readFromFile();
        ids = new ArrayList<>();

        if (getIntent().getStringExtra("mode") != null) {
            String mode = getIntent().getStringExtra("mode");
            if (mode.equals("edit")) {
                if (getIntent().getStringExtra("event") != null) {
                    toDisplay = new Gson().fromJson(getIntent().getStringExtra("event"), Event.class);
                    setupView();
                    getFriendGroupNames();
                    setTexts();
                } else {
                    Toast.makeText(getApplicationContext(), getString(R.string.wrong), Toast.LENGTH_LONG).show();
                }
            } else {
                if (mode.equals("new")) {
                    setupView();
                    getFriendGroupNames();
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

    Calendar date;
    public void showDateTimePicker(final TextView textView) {
        final Context context = this;
        final Calendar currentDate = Calendar.getInstance();
        date = Calendar.getInstance();
        new DatePickerDialog(context, AlertDialog.THEME_DEVICE_DEFAULT_DARK, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        date.set(year, monthOfYear, dayOfMonth);
                        new TimePickerDialog(context, AlertDialog.THEME_DEVICE_DEFAULT_DARK, new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                date.set(Calendar.HOUR_OF_DAY, hourOfDay);
                                date.set(Calendar.MINUTE, minute);
                                Log.v("my tagggs", "The choosen one " + date.getTime());
                                if(date.get(Calendar.MINUTE) < 10){
                                    textView.setText(String.valueOf(date.get(Calendar.DAY_OF_MONTH))+"/"+String.valueOf(date.get(Calendar.MONTH) + 1)+"/"+String.valueOf(date.get(Calendar.YEAR)) +" "+String.valueOf(date.get(Calendar.HOUR_OF_DAY)) + ":0" + String.valueOf(date.get(Calendar.MINUTE)));

                                } else {
                                    textView.setText(String.valueOf(date.get(Calendar.DAY_OF_MONTH))+"/"+String.valueOf(date.get(Calendar.MONTH) + 1)+"/"+String.valueOf(date.get(Calendar.YEAR)) +" "+String.valueOf(date.get(Calendar.HOUR_OF_DAY)) + ":" + String.valueOf(date.get(Calendar.MINUTE)));

                                }
                            }
                        }, currentDate.get(Calendar.HOUR_OF_DAY), currentDate.get(Calendar.MINUTE), false).show();
                    }
                }, currentDate.get(Calendar.YEAR), currentDate.get(Calendar.MONTH), currentDate.get(Calendar.DATE)).show();

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

        startTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDateTimePicker(startTime);
            }
        });

        endTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDateTimePicker(endTime);
            }
        });

        visibilityOptions = new ArrayList<>();
        visibilityOptions.add("Choose who the event will be visible for");
        visibilityOptions.add("Private");
        visibilityOptions.add("Public");

         adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, visibilityOptions);
        visibility.setAdapter(adapter);
    }

    private void getFriendGroups(){
        final Context context = this;
        for(final String groupID : ids){
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("friendGroups").child(groupID);
            ref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    GenericTypeIndicator<Friend> t = new GenericTypeIndicator<Friend>() {
                    };
                    ArrayList<String> ids = new ArrayList<>();
                    String name = "";
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        if(dataSnapshot.getKey() != null){
                            if(!dataSnapshot.getKey().equals("friendGroupName") && !dataSnapshot.getKey().equals("admin")){
                                ids.add(dataSnapshot.getValue(t).getName());
                            }

                            if(dataSnapshot.getKey().equals("friendGroupName")){
                                name = dataSnapshot.getValue().toString();
                                visibilityOptions.add(name);
                                adapter.notifyDataSetChanged();
                            }
                        }

                        groups.put(dataSnapshot.getKey(), ids);
                    }


                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(getApplicationContext(), databaseError.getMessage(), Toast.LENGTH_LONG).show();
                }

            });
        }


    }

    private void getFriendGroupNames(){
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("admin")
                .child("Users").child(me.getDatabaseID()).child("friendGroups");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    String groupID = dataSnapshot.getKey();
                    ids.add(groupID);
                }


                getFriendGroups();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
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
                        short_description, long_description, country_and_city + ", " + street_and_number, start_time, end_time, 1, false));
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
        oldInvitees = new ArrayList<>();
        DatabaseReference ref;
        if(toDisplay.isTotallyVisible() && node.equals("PrivateEvents")){
            Log.d("removeOld", "Private");
            ref = FirebaseDatabase.getInstance().getReference().child("PublicEvents").child(toDisplay.getEventID()).child("visibleTo");
            DatabaseReference no = FirebaseDatabase.getInstance().getReference().child("PublicEvents").child(toDisplay.getEventID());
            makeDynamic(ref, no);
        } else {
            if(!toDisplay.isTotallyVisible() && node.equals("PublicEvents")){
                Log.d("removeOld", "Public");
                ref = FirebaseDatabase.getInstance().getReference().child("PrivateEvents").child(toDisplay.getEventID()).child("visibleTo");
                DatabaseReference no = FirebaseDatabase.getInstance().getReference().child("PrivateEvents").child(toDisplay.getEventID());
                makeDynamic(ref,no);
            }
        }
    }

    private void makeDynamic(final DatabaseReference ref, final DatabaseReference no){
        oldAttanding = new ArrayList<>();
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NotNull DataSnapshot snapshot) {
                if(snapshot != null){
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        oldInvitees.add(dataSnapshot.getKey());
                    }
                }

                DatabaseReference reference = ref.getParent().child("attending");
                reference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot1) {
                        if(dataSnapshot1 != null){
                            for (DataSnapshot dataSnapshot : dataSnapshot1.getChildren()) {
                                oldAttanding.add(dataSnapshot.getKey());
                            }
                        }

                        no.removeValue();
                        makeDynamicInvites(oldInvitees, oldAttanding);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


            }

            @Override
            public void onCancelled(@NotNull DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(), databaseError.getMessage(), Toast.LENGTH_LONG).show();
            }

        });
    }

    private void makeDynamicInvites(ArrayList<String> invites, ArrayList<String> attending){
        Log.d("makeDynamic", String.valueOf(toDisplay != null));
        Log.d("makeDynamic", "makeDynamicInvites");
        if(toDisplay != null) {
            for (String id : invites) {
                for (String attendingID : attending) {
                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("eventInvites").child(id).child(toDisplay.getEventID());
                    if (visibility.getSelectedItem().toString().equals("Public")) {
                        ref.setValue(true);
                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("PublicEvents").child(toDisplay.getEventID()).child("visibleTo");
                        reference.child(id).setValue(id);
                        DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference().child("PublicEvents").child(toDisplay.getEventID()).child("attending");
                        reference1.child(attendingID).setValue(attendingID);
                    } else {
                        ref.setValue(false);
                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("PrivateEvents").child(toDisplay.getEventID()).child("visibleTo");
                        reference.child(id).setValue(id);
                        DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference().child("PrivateEvents").child(toDisplay.getEventID()).child("attending");
                        reference1.child(attendingID).setValue(attendingID);
                    }
                }
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
            Date now = new Date();

            if (starting.before(ending) && starting.after(now) && ending.after(now)) {
                toReturn = true;
            } else {
                Toast.makeText(getApplicationContext(), "Check Your Dates Please", Toast.LENGTH_LONG).show();
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
