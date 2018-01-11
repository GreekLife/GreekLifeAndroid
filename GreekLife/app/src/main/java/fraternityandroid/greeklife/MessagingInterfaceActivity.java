package fraternityandroid.greeklife;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;

public class MessagingInterfaceActivity extends AppCompatActivity {

    Globals globals = Globals.getInstance();
    public DataSnapshot dialogueSnapshot;
    public Dialogue dialogue;


    public class Dialogue {
        public String dialogueID;
        public ArrayList<Message> messages = new ArrayList<>();
        public String[] messengeeIDs;
        public ArrayList<User> messengees = new ArrayList<>();
        public String type;
        public String dialogueName;
        public DataSnapshot dialogueSnap;
        public Message lastMessage;

        public Dialogue(String dialogueID) {
            this.dialogueID = dialogueID;

            if (dialogueID.contains(", ")) {
                this.type = "DirectDialogues";
                this.dialogueSnap = dialogueSnapshot;

                this.messengeeIDs = dialogueID.split(", ").clone();

                this.dialogueName = "";
                for (String messengeeID : this.messengeeIDs) {
                    if (!messengeeID.equals(globals.getLoggedIn().UserID)) {
                        this.dialogueName += globals.getUserByID(messengeeID).First_Name + " " + globals.getUserByID(messengeeID).Last_Name + ", ";

                    }
                }
                this.dialogueName = this.dialogueName.substring(0, this.dialogueName.length() - 2);
            } else {
                this.type = "ChannelDialogues";
                this.dialogueSnap = dialogueSnapshot;

                this.messengeeIDs = dialogueSnap.child("Messengees").getValue().toString().split(", ").clone();
            }

            for (String messengeeID : messengeeIDs) {
                this.messengees.add(globals.getUserByID(messengeeID));
            }

            for (DataSnapshot messageSnap : dialogueSnap.child("Messages").getChildren()) {
                String messageID = messageSnap.getKey();
                messages.add(new Message(messageID, this));
            }
            this.lastMessage = this.messages.get(this.messages.size() - 1);
            if (this.type.equals("ChannelDialogues")) {
                this.dialogueName = this.dialogueSnap.child("Name").getValue().toString();
            }

        }


        public void sendMessage(Message message) {
            FirebaseDatabase.getInstance().getReference().child(globals.DatabaseNode()+"/"+this.type +"/"+dialogueID+ "/Messages/" + message.messageID).setValue(message.messageContent);
        }

    }

    public class Message {
        public String messageID;
        public String messageContent;
        public String senderID;
        public String timeSent;

        public Message(String messageID, Dialogue dialogue) {
            this.messageID = messageID;

            String[] idComponents = messageID.split(", ");
            this.timeSent = idComponents[0];
            this.senderID = idComponents[1];

            messageContent = dialogue.dialogueSnap.child("/Messages/" + messageID).getValue().toString();
        }

        public Message(String senderID, String timeSent, String messageContent) {
            this.senderID = senderID;
            this.timeSent = timeSent;
            this.messageContent = messageContent;
            this.messageID = timeSent + ", " + senderID;
        }
    }

    public ValueEventListener dialogueListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            dialogueSnapshot = dataSnapshot;
            dialogue = new Dialogue(getIntent().getStringExtra("dialogueID"));
            updateMessages();

            scrollToBottom();
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messaging_interface);
        String path = getIntent().getStringExtra("dialogueType")+"/"+getIntent().getStringExtra("dialogueID");
        Toolbar toolbar = (Toolbar) findViewById(R.id.MessengerInterfaceToolbar);
        TextView header = new TextView(this);
        header.setTextColor(Color.parseColor("#c1ffdf00"));
        header.setGravity(Gravity.CENTER);
        toolbar.addView(header);
        header.setText(getIntent().getStringExtra("dialogueName"));

        FirebaseDatabase.getInstance().getReference()
                .child(globals.DatabaseNode()+"/"+path).addValueEventListener(dialogueListener);
        ((Button)findViewById(R.id.sendBTN)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(((EditText)findViewById(R.id.messageField)).getText().toString().isEmpty()){
                    Toast toast = Toast.makeText(MessagingInterfaceActivity.this, "There is no message to send...", Toast.LENGTH_SHORT);
                    toast.show();
                }else{
                    dialogue.sendMessage(new Message(globals.getLoggedIn().UserID, String.valueOf(((Long)(Calendar.getInstance().getTimeInMillis()/1000)).intValue()), ((EditText)findViewById(R.id.messageField)).getText().toString()));
                    ((EditText)findViewById(R.id.messageField)).setText("");
                }
            }
        });

        ((TextView) findViewById(R.id.messageField)).setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {

                  scrollToBottom();
            }
        });

        //((ScrollView)findViewById(R.id.messagesScrollView)).draw


        ((ScrollView)findViewById(R.id.messagesScrollView)).post(new Runnable() {

            @Override
            public void run() {
                    scrollToBottom();
            }

        });



        scrollToBottom();
    }


    public void updateMessages () {
        scrollToBottom();
        ((LinearLayout)findViewById(R.id.messagesContainer)).removeAllViews();
        for(final Message message:dialogue.messages){
            View messageCell = getLayoutInflater().inflate(R.layout.message_cell, null);

            ((TextView) messageCell.findViewById(R.id.messageSender)).setText(globals.userFirstLastNameByID(message.senderID));

            String timeSent;
            long currentEpoch = System.currentTimeMillis() / 1000L;
            long secondsSince = currentEpoch - Long.parseLong(message.timeSent);
            int hours = (int) (secondsSince / 3600);
            int min = (int) (hours / 60);
            int days = hours / 24;
            String display;
            if (days < 1) {
                display = Integer.toString(hours) + "h";
            } else if (hours < 0) {
                display = Integer.toString(min) + "m";
            } else {
                display = Integer.toString(days) + "d";
            }
            timeSent = display;

            ((TextView)messageCell.findViewById(R.id.timeSent)).setText(timeSent);
            if(message.messageContent.equals("Deleted Message*")) {
                message.messageContent = "This message has been removed";
                ((TextView) messageCell.findViewById(R.id.messageContent)).setText(message.messageContent);
                ((TextView) messageCell.findViewById(R.id.messageContent)).setTextColor(Color.RED);
            }
            else {
                ((TextView) messageCell.findViewById(R.id.messageContent)).setText(message.messageContent);
                ((TextView) messageCell.findViewById(R.id.messageContent)).setTextColor(Color.WHITE);
            }
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) messageCell.findViewById(R.id.messageContainer).getLayoutParams();
            if(!message.senderID.equals(globals.getLoggedIn().UserID)){
                ((LinearLayout)messageCell.findViewById(R.id.messageContainer)).setBackgroundColor(Color.rgb(5,5,3));
                params.gravity= Gravity.LEFT;
                params.setMarginEnd(200);
                messageCell.findViewById(R.id.messageContainer).setLayoutParams(params);

            }else{
                params.setMarginStart(200);
                messageCell.findViewById(R.id.messageContainer).setLayoutParams(params);
                messageCell.findViewById(R.id.messageHeaderContainer).setVisibility(View.GONE);
            }
            messageCell.setLongClickable(true);
            messageCell.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    if ((globals.getLoggedIn().Position.equals("Master") && globals.getLoggedIn().Validated) || message.senderID.equals(globals.getLoggedIn().UserID)) {
                        final AlertDialog.Builder builder = new AlertDialog.Builder(MessagingInterfaceActivity.this);
                        builder.setTitle("Delete Message?");
                        builder.setMessage("Are you sure you would like to delete this Message?");
                        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                FirebaseDatabase.getInstance().getReference().child(globals.DatabaseNode()+"/"+dialogue.type+"/"+dialogue.dialogueID+"/Messages/"+message.messageID).setValue("Deleted Message*");
                                updateMessages();
                            }
                        });
                        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                            }
                        });
                        AlertDialog alertDialog = builder.create();
                        alertDialog.show();
                    }
                    return true;
                }
            });
            ((LinearLayout)findViewById(R.id.messagesContainer)).addView(messageCell);
            scrollToBottom();
        }
        scrollToBottom();
    }

    public void scrollToBottom(){

        ((ScrollView)findViewById(R.id.messagesScrollView)).invalidate();
        //((ScrollView)findViewById(R.id.messagesScrollView)).scrollTo(0, ((ScrollView)findViewById(R.id.messagesScrollView)).getBottom());
        ((ScrollView)findViewById(R.id.messagesScrollView)).fullScroll(View.FOCUS_DOWN);
    }
}
