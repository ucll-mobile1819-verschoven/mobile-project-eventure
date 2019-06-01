package com.ucll.eventure;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

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

        if(getIntent().getStringExtra("chatID") != null && !getIntent().getStringExtra("chatID").isEmpty() && getIntent().getStringExtra("friendName") != null){
            chatID = getIntent().getStringExtra("chatID");
            TextView title = findViewById(R.id.toolbar_title);
            title.setText(getIntent().getStringExtra("friendName"));
            chatRef = FirebaseDatabase.getInstance().getReference().child("Chats");
            messages = new ArrayList<>();
            hasMessages = false;
            messages.clear();
            messageAdapter = new MessageAdapter(this, messages, me);
            listView = findViewById(R.id.message_list);
            messageFied = findViewById(R.id.edittext_chatbox);
            messageFied.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if(hasFocus){
                        listView.setSelection(messageAdapter.getCount() - 1);
                    }
                }
            });
            listView.setAdapter(messageAdapter);
            messageAdapter.notifyDataSetChanged();
            listView.setSelection(messageAdapter.getCount() - 1);
            getMessages(chatID);
        }

    }

    private void getMessages(final String chatIDs) {
        chatRef.child(chatIDs).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    GenericTypeIndicator<Message> t = new GenericTypeIndicator<Message>() {
                    };
                    for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                        Message toSee = snapshot.getValue(t);
                        if(!contains(toSee, messages) && !snapshot.getKey().equals("visibleTo"))
                            messages.add(toSee);
                    }
                    hasMessages = true;
                    displayMessages();
                } else {
                    String[] index = chatID.split("_");
                    getMessages(index[1]+"_"+index[0]);
                    chatID = index[1]+"_"+index[0];
                    getMessages(chatID);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private boolean contains(Message message, ArrayList<Message> messages){
        for(Message message1 : messages){
            if(message != null && message1 != null && message.getMessage() != null && message1.getMessage() != null){
                if(message.getMessage().equals(message1.getMessage())){
                    return true;
                }
            }
        }

        return false;
    }

    private void displayMessages(){
        messageAdapter.notifyDataSetChanged();
        listView.setSelection(messageAdapter.getCount() - 1);
    }

    public void sendMessage(View view){
        if(!messageFied.getText().toString().isEmpty()){
            String[] index = chatID.split("_");
            String receiver = "";
            if(!index[0].equals(me.getDatabaseID())){
                receiver = index[0];
            } else {
                if(!index[1].equals(me.getDatabaseID())){
                    receiver = index[1];
                }
            }

            Message message = new Message(me.getDatabaseID(), false, messageFied.getText().toString(), receiver);
            messageFied.setText("");

            if(!hasMessages){
                String[] x = chatID.split("_");
                chatRef.child(chatID).child("visibleTo").child(x[0]).setValue(x[0]);
                chatRef.child(chatID).child("visibleTo").child(x[1]).setValue(x[1]);
            }
            chatRef.child(chatID).push().setValue(message);
            messages.add(message);
            messageAdapter.notifyDataSetChanged();
            listView.setSelection(messageAdapter.getCount() - 1);
        }

    }
}
