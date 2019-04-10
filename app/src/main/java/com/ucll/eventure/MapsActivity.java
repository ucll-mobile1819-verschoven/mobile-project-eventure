package com.ucll.eventure;

import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;
import com.ucll.eventure.Data.Event;
import com.ucll.eventure.Data.EventDatabase;
import com.ucll.eventure.Data.UserDatabase;

import org.w3c.dom.Text;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private Event eventToDisplay;
    private Button attendingcount;
    private Boolean signedUp;
    private ArrayList<String> events;
    private Button going;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        TextView eventTitle = findViewById(R.id.eventTitle);
        TextView eventDescription = findViewById(R.id.eventDescription);
        TextView startTime = findViewById(R.id.startTime);
        TextView endTime = findViewById(R.id.endTime);
        attendingcount = findViewById(R.id.attendingcount);
        going = findViewById(R.id.going);

        //GET EVENT TO DISPLAY
        if (getIntent().getStringExtra("event") != null) {
            eventToDisplay = new Gson().fromJson(getIntent().getStringExtra("event"), Event.class);

            eventTitle.setText(eventToDisplay.getEventTitle());
            eventDescription.setText(eventToDisplay.getLongDescription());
            startTime.setText(eventToDisplay.getStartTime());
            endTime.setText(eventToDisplay.getEndTime());
            attendingcount.setText(String.valueOf(eventToDisplay.getAttendees()));
            signedUp = false;
            setUpView();
        } else {
            Toast.makeText(getApplicationContext(), getString(R.string.wrong), Toast.LENGTH_SHORT).show();
        }
    }

    private void setUpView() {
        events = new EventDatabase(getApplicationContext()).readFromFile();
        if (events != null && events.contains(eventToDisplay.getEventID())) {
            signedUp = true;
            going.setText("Going");
            going.setTextColor(Color.parseColor("#FFFFFF"));
            going.setBackground(getResources().getDrawable(R.drawable.rounded_corners));
        } else {
            if(events == null){
                events = new ArrayList<>();
            }
        }
    }

    private void addMarker() {
        mMap.clear();
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());

        try {
            List<Address> addresses = geocoder.getFromLocationName(eventToDisplay.getAddress(), 1);
            LatLng sydney = new LatLng(addresses.get(0).getLatitude(), addresses.get(0).getLongitude());
            mMap.addMarker(new MarkerOptions().position(sydney).title("The Location"));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
            //Move the camera to the user's location and zoom in!
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(sydney, 12.0f));
        } catch (IOException e) {
            Toast.makeText(getApplicationContext(), getString(R.string.wrong), Toast.LENGTH_SHORT).show();
        }
    }

    public void setAttending(View v) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Events").child(eventToDisplay.getEventID()).child("attending").child(new UserDatabase(getApplicationContext()).readFromFile().getDatabaseID());

        if (eventToDisplay != null) {
            if (eventToDisplay != null && !signedUp) {
                ref.setValue(new UserDatabase(getApplicationContext()).readFromFile().getDatabaseID());
                attendingcount.setText(String.valueOf(eventToDisplay.getAttendees() + 1));
                events.add(eventToDisplay.getEventID());
                new EventDatabase(getApplicationContext()).writeToFile(events);
                signedUp = true;

                going.setText("Going");
                going.setTextColor(Color.parseColor("#FFFFFF"));
                going.setBackground(getResources().getDrawable(R.drawable.rounded_corners));

            } else {
                signedUp = false;
                ref.removeValue();
                events.remove(eventToDisplay.getEventID());
                new EventDatabase(getApplicationContext()).writeToFile(events);
                attendingcount.setText(String.valueOf(eventToDisplay.getAttendees() - 1));
                going.setText("Going?");
                going.setTextColor(Color.parseColor("#000000"));
                going.setBackground(getResources().getDrawable(R.drawable.textview_rounded_corners));
            }
        } else {
            Toast.makeText(getApplicationContext(), getString(R.string.wrong), Toast.LENGTH_SHORT).show();
        }
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        addMarker();

        // Add a marker in Sydney and move the camera
        // LatLng sydney = new LatLng(-34, 151);
        // mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        // mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }
}
