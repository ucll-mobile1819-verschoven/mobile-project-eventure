package com.ucll.eventure;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.ucll.eventure.Adapters.MessageAdapter;
import com.ucll.eventure.Data.Message;
import com.ucll.eventure.Data.User;
import com.ucll.eventure.Data.UserDatabase;

import java.util.ArrayList;

public class ChatActivity extends AppCompatActivity {
    private String chatID;
    private DatabaseReference chatRef;
    private MessageAdapter messageAdapter;
    private ArrayList<Message> messages;
    private ListView listView;
    private EditText messageFied;
    private User me;
    private boolean hasMessages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        me = new UserDatabase(getApplicationContext()).readFromFile();

        if(getIntent().getStringExtra("chatID") != null && !getIntent().getStringExtra("chatID").isEmpty()){
            chatID = getIntent().getStringExtra("chatID");
            chatRef = FirebaseDatabase.getInstance().getReference().child("Chats");
            messages = new ArrayList<>();
            hasMessages = false;
            messages.clear();
            messageAdapter = new MessageAdapter(this, messages, me);
            listView = findViewById(R.id.message_list);
            messageFied = findViewById(R.id.edittext_chatbox);
            listView.setAdapter(messageAdapter);
            messageAdapter.notifyDataSetChanged();
            getMessages(chatID);
        }

    }

    private void getMessages(final String chatIDs) {
        chatRef.child(chatIDs).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                GenericTypeIndicator<Message> t = new GenericTypeIndicator<Message>() {
                };
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Message toSee = snapshot.getValue(t);
                    if(!messages.contains(toSee))
                        messages.add(toSee);
                }

                if(!messages.isEmpty()){
                    hasMessages = true;
                } else {
                    String[] index = chatID.split("_");
                    getMessages(index[1]+"_"+index[0]);
                    chatID = index[1]+"_"+index[0];
                }
                displayMessages();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void displayMessages(){
        messageAdapter.notifyDataSetChanged();
        Toast.makeText(getApplicationContext(), "made", Toast.LENGTH_LONG).show();
    }

    public void sendMessage(View view){
        Message message = new Message(me.getDatabaseID(), false, messageFied.getText().toString());
        messageFied.setText("");

        if(!hasMessages){
            String[] x = chatID.split("_");
            chatRef.child(chatID).child("visibleTo").setValue(x[0]);
            chatRef.child(chatID).child("visibleTo").setValue(x[1]);
        }

        chatRef.child(chatID).push().setValue(message);
        messages.add(message);
        messageAdapter.notifyDataSetChanged();
    }
}
