package com.ucll.eventure.Data;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

public class DeclineDatabase {
    private Context context;

    public DeclineDatabase(Context context) {
        this.context = context;
    }

    /**
     * Method that reads the current user's object from local storage.
     *
     * @return the users data in form of the user object.
     */
    public ArrayList<String> readFromFile() {
        ArrayList<String> eventIDs = new ArrayList<>();

        try {
            InputStream inputStream = context.openFileInput("declineevents.txt");

            if (inputStream != null) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString;
                StringBuilder stringBuilder = new StringBuilder();

                while ((receiveString = bufferedReader.readLine()) != null) {
                    stringBuilder.append(receiveString);
                }

                inputStream.close();
                Gson gS = new Gson();
                eventIDs = gS.fromJson(stringBuilder.toString(), ArrayList.class);
            }
        } catch (FileNotFoundException e) {
            Log.d("LocalDBreadFromFile()", "the events.txt was not found");
            return new ArrayList<>();
        } catch (IOException e) {
            Log.d("LocalDBreadFromFile()", "IOException for userobject");
            return new ArrayList<>();
        } catch (Exception e){
            return new ArrayList<>();
        }

        if(eventIDs == null)
            eventIDs = new ArrayList<>();

        return eventIDs;
    }

    /**
     * Method that writes the user object to local storage.
     */
    public void writeToFile(ArrayList<String> events) {
        try {
            Gson gS = new Gson();
            String target = gS.toJson(events);

            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput("declineevents.txt", Context.MODE_PRIVATE));
            outputStreamWriter.write(target);
            outputStreamWriter.close();
        } catch (IOException e) {
            Toast.makeText(context, "Writing your info to your local storage went wrong, please contact support", Toast.LENGTH_LONG).show();
        }
    }
}
