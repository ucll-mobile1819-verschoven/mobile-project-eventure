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

/**
 * This class handles the reading and writing of the user's info to local storage.
 */
public class UserDatabase {
    private Context context;

    public UserDatabase(Context context) {
        this.context = context;
    }

    /**
     * Method that reads the current user's object from local storage.
     *
     * @return the users data in form of the user object.
     */
    public User readFromFile() {
        User ret = null;

        try {
            InputStream inputStream = context.openFileInput("UserObject.txt");

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
                ret = gS.fromJson(stringBuilder.toString(), User.class);
            }
        } catch (FileNotFoundException e) {
            Log.d("LocalDBreadFromFile()", "the UserObject.txt was not found");
            return null;
        } catch (IOException e) {
            Log.d("LocalDBreadFromFile()", "IOException for userobject");
            return null;
        }

        return ret;
    }

    /**
     * Method that writes the user object to local storage.
     */
    public void writeToFile(User data) {
        try {
            Gson gS = new Gson();
            String target = gS.toJson(data);

            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput("UserObject.txt", Context.MODE_PRIVATE));
            outputStreamWriter.write(target);
            outputStreamWriter.close();
        } catch (IOException e) {
            Toast.makeText(context, "Writing your info to your local storage went wrong, please contact support", Toast.LENGTH_LONG).show();
        }
    }

}
