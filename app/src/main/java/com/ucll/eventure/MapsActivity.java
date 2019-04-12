package com.ucll.eventure;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.widget.NestedScrollView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;
import com.ucll.eventure.Adapters.ImageAdapter;
import com.ucll.eventure.Data.Event;
import com.ucll.eventure.Data.EventDatabase;
import com.ucll.eventure.Data.UserDatabase;
import com.vansuita.pickimage.bean.PickResult;
import com.vansuita.pickimage.bundle.PickSetup;
import com.vansuita.pickimage.dialog.PickImageDialog;
import com.vansuita.pickimage.listeners.IPickResult;

import org.w3c.dom.Text;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, IPickResult {

    private GoogleMap mMap;
    private Event eventToDisplay;
    private TextView attendingcount;
    private Boolean signedUp;
    private ArrayList<String> events;
    private Button going;
    private ListView images;
    private ImageAdapter imageAdapter;
    private ArrayList<String> links;
    private ArrayList<Bitmap> bitmaps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        Fresco.initialize(this);


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        links = new ArrayList<>();
        bitmaps = new ArrayList<>();

        images = findViewById(R.id.event_images);
        TextView eventTitle = findViewById(R.id.eventTitle);
        TextView eventDescription = findViewById(R.id.eventDescription);
        TextView startTime = findViewById(R.id.startTime);
        TextView endTime = findViewById(R.id.endTime);
        attendingcount = findViewById(R.id.attendingcount);
        going = findViewById(R.id.going);

        NestedScrollView scrollView = findViewById(R.id.scrollEvent);
        scrollView.scrollTo(0, 0);
        scrollView.fullScroll(ScrollView.FOCUS_UP);
        scrollView.setFocusableInTouchMode(true);
        scrollView.setDescendantFocusability(ViewGroup.FOCUS_BEFORE_DESCENDANTS);

        final TextView toolbarTitle = findViewById(R.id.toolbar_title);

        AppBarLayout appBarLayout = findViewById(R.id.appbar);
        appBarLayout.addOnOffsetChangedListener(new AppBarStateChangeListener() {
            @Override
            public void onStateChanged(AppBarLayout appBarLayout, State state, float percent) {
                attendingcount.setAlpha(percent);
                toolbarTitle.setAlpha(percent);
            }
        });

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
            getImageLinks();
            imageAdapter = new ImageAdapter(getBaseContext(), bitmaps);
            images.setAdapter(imageAdapter);
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

    private void getImageLinks(){
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Events")
                .child(eventToDisplay.getEventID()).child("Images");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                GenericTypeIndicator<ArrayList<String>> t2 = new GenericTypeIndicator<ArrayList<String>>() {
                };

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    links.add(dataSnapshot.getValue().toString());
                }

                getImages(links);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(), databaseError.getMessage(), Toast.LENGTH_LONG).show();
            }

        });

    }

    private void getImages(ArrayList<String> imageURLS){
        //imageAdapter = new ImageAdapter(getBaseContext(), bitmaps);
        for(String link : imageURLS){
            StorageReference httpsReference = FirebaseStorage.getInstance().getReferenceFromUrl(link);
            final long ONE_MEGABYTE = 1024 * 1024;
            httpsReference.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                @Override
                public void onSuccess(byte[] bytes) {
                    bitmaps.add(BitmapFactory.decodeByteArray(bytes, 0, bytes.length));
                    imageAdapter.notifyDataSetChanged();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle any errors
                }
            });

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

    public void addImage(View v){
        PickImageDialog.build(new PickSetup()).show(this);
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

    @Override
    public void onPickResult(PickResult r) {
        if (r.getError() == null) {

            bitmaps.add(r.getBitmap());
            imageAdapter.notifyDataSetChanged();

            StorageReference storageRef = FirebaseStorage.getInstance().getReference().child("Events")
                    .child(eventToDisplay.getEventID()).child(getRandomString());
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            r.getBitmap().compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] data = baos.toByteArray();

            UploadTask uploadTask = storageRef.putBytes(data);
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle unsuccessful uploads
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                    taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Events")
                                    .child(eventToDisplay.getEventID()).child("Images")
                                    .child(getRandomString());
                            ref.setValue(uri.toString());
                            Toast.makeText(getApplicationContext(), "uploaded", Toast.LENGTH_LONG).show();
                        }
                    });

                }
            });

        } else {
            //Handle possible errors
            //TODO: do what you have to do with r.getError();
            Toast.makeText(this, r.getError().getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    public String getRandomString() {
        // chose a Character random from this String
        String AlphaNumericString = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
                + "0123456789"
                + "abcdefghijklmnopqrstuvxyz";

        // create StringBuffer size of AlphaNumericString
        StringBuilder sb = new StringBuilder(10);

        for (int i = 0; i < 10; i++) {

            // generate a random number between
            // 0 to AlphaNumericString variable length
            int index
                    = (int)(AlphaNumericString.length()
                    * Math.random());

            // add Character one by one in end of sb
            sb.append(AlphaNumericString
                    .charAt(index));
        }

        return sb.toString();
    }
}
