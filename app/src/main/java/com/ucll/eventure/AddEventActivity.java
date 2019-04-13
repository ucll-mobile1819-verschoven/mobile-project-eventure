package com.ucll.eventure;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.ucll.eventure.Data.Event;
import com.ucll.eventure.Data.UserDatabase;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AddEventActivity extends AppCompatActivity {
    private EditText eventTitle, shortDescription, longDescription, countryCity, streetNumber, startTime, endTime;
    private Spinner visibility;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);

        setupView();
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

        //TODO: SET SPINNER ADAPTER
    }

    public void checkInput(View view) {
        String event_Title = eventTitle.getText().toString();
        String short_description = shortDescription.getText().toString();
        String long_description = longDescription.getText().toString();
        String country_and_city = countryCity.getText().toString();
        String street_and_number = streetNumber.getText().toString();
        String start_time = startTime.getText().toString();
        String end_time = endTime.getText().toString();

        //TODO: GET VISIBILITY INFO & INCORPORATE IN EVENT OBJECT
        if (!event_Title.isEmpty() && !short_description.isEmpty() && !long_description.isEmpty() &&
                !country_and_city.isEmpty() && !street_and_number.isEmpty() && !start_time.isEmpty() && !end_time.isEmpty()) {
            if (validCountryAndCity(country_and_city) && validStreetAndNumber(street_and_number) && validTime(start_time, end_time)) {
                submitToDatabase(new Event("eventID", new UserDatabase(getApplicationContext()).readFromFile().getDatabaseID(), event_Title,
                        short_description, long_description, country_and_city + ", " + street_and_number, start_time, end_time, 0, false));
            } else {
                Toast.makeText(getApplicationContext(), "The format of your input is incorrect, please follow the hints", Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(getApplicationContext(), "Please fill in all fields", Toast.LENGTH_LONG).show();
        }
    }

    private void submitToDatabase(Event toSubmit) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Events");
        String id = ref.push().getKey();
        toSubmit.setEventID(id);
        ref.child(id).setValue(toSubmit);

        finish();
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
