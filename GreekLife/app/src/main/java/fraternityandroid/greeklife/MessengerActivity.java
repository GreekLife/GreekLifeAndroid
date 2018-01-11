package fraternityandroid.greeklife;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Messenger;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MessengerActivity extends AppCompatActivity {

    static Globals globals = Globals.getInstance();
    private DialogueDB dialogueDB = new DialogueDB();

    public ValueEventListener channelsListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            dialogueDB.channelsSnap = dataSnapshot;
            updateDialogueList(dialogueDB.channelsSnap);
            writeDialoguesToScreen();
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };
    public ValueEventListener directsListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            dialogueDB.directsSnap = dataSnapshot;
            updateDialogueList(dialogueDB.directsSnap);
            writeDialoguesToScreen();
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };

    public class DialogueDB {
        public DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference().child(globals.DatabaseNode());
        public DatabaseReference channelsRef = FirebaseDatabase.getInstance().getReference().child(globals.DatabaseNode() + "/ChannelDialogues");
        public DatabaseReference directsRef = FirebaseDatabase.getInstance().getReference().child(globals.DatabaseNode() + "/DirectDialogues");
        public DataSnapshot channelsSnap;
        public DataSnapshot directsSnap;

        public void cleanUp() {
            channelsRef.removeEventListener(channelsListener);
            directsRef.removeEventListener(directsListener);
        }


    }

    public class Dialogue {
        public String dialogueID;
        public ArrayList<Message> messages = new ArrayList<>();
        public ArrayList<String> messengeeIDs;
        public ArrayList<User> messengees = new ArrayList<>();
        public String type;
        public String dialogueName;
        public DataSnapshot dialogueSnap;
        public Message lastMessage;

        public Dialogue(String dialogueID) {
            this.dialogueID = dialogueID;

            if (dialogueID.contains(", ")) {
                this.type = "DirectDialogues";
                this.dialogueSnap = dialogueDB.directsSnap.child(this.dialogueID);

                this.messengeeIDs = new ArrayList<>(Arrays.asList(dialogueID.split(", ")));

                this.dialogueName = "";
                for (String messengeeID : this.messengeeIDs) {
                    boolean userInDialogueExists = false;
                    for(User user:globals.getUsers()){
                        if(user.UserID.equals(messengeeID)){
                            userInDialogueExists = true;
                        }
                    }
                    if (!userInDialogueExists){
                        FirebaseDatabase.getInstance().getReference().child(globals.DatabaseNode()+"/"+type+"/"+dialogueID).removeValue();
                        dialogueID = "deleted";
                    }else{
                        if (!messengeeID.equals(globals.getLoggedIn().UserID)) {
                            this.dialogueName += globals.getUserByID(messengeeID).First_Name + " " + globals.getUserByID(messengeeID).Last_Name + ", ";
                            this.dialogueName = this.dialogueName.substring(0, this.dialogueName.length() - 2);
                        }
                        this.messengees.add(globals.getUserByID(messengeeID));
                    }

                }

            } else {
                this.type = "ChannelDialogues";
                this.dialogueSnap = dialogueDB.channelsSnap.child(this.dialogueID);

                this.messengeeIDs = new ArrayList<>(Arrays.asList(dialogueSnap.child("Messengees").getValue().toString().split(", ")));

                for (String messengeeID : messengeeIDs) {
                    boolean userExists = false;
                    for (User user : globals.getUsers()) {
                        if (user.UserID.equals(messengeeID)) {
                            userExists = true;
                        }
                    }
                    if (userExists) {
                        this.messengees.add(globals.getUserByID(messengeeID));
                    }
                }
                String messengeeIDsString = "";
                for (User user:messengees){
                    messengeeIDsString += user.UserID + ", ";
                }
                messengeeIDsString = messengeeIDsString.substring(0, messengeeIDsString.length()-2);
                this.messengeeIDs = new ArrayList<>(Arrays.asList(messengeeIDsString.split(", ")));
                FirebaseDatabase.getInstance().getReference().child(globals.DatabaseNode()+"/"+type+"/"+dialogueID+"/Messengees").setValue(messengeeIDsString);
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

        public Dialogue(String type, String dialogueName, String messengeeIDList, Message welcomeMessage) {
            DatabaseReference dialogueRef = dialogueDB.dbRef.child(type).push();
            dialogueRef.child("Name").setValue(dialogueName);
            dialogueRef.child("Messengees").setValue(messengeeIDList);
            dialogueRef.child("Messages/" + welcomeMessage.messageID).setValue(welcomeMessage.messageContent);
        }

        public void sendMessage(Message message) {
            dialogueDB.dbRef.child(this.type + "/Messages/" + message.messageID).setValue(message.messageContent);
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

    public ArrayList<Dialogue> directDialogues = new ArrayList<>();
    public ArrayList<Dialogue> channelDialogues = new ArrayList<>();

    @Override
    protected void onResume() {
        super.onResume();
        reload();
    }
    protected void reload() {
        this.dialogueDB.directsRef.addListenerForSingleValueEvent(this.directsListener);
        this.dialogueDB.channelsRef.addListenerForSingleValueEvent(this.channelsListener);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messenger);
        reload();

        // Buttons for Adjusting UI
        ((ToggleButton) findViewById(R.id.directMessagingBTN)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                ValueAnimator animation;
                final View view = findViewById(R.id.directsView);
                if (b) {
                    animation = ValueAnimator.ofFloat(1f, 0f);
                    animation.setDuration(500);
                    animation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                        @Override
                        public void onAnimationUpdate(ValueAnimator valueAnimator) {
                            view.setLayoutParams(new LinearLayout.LayoutParams(
                                    ViewGroup.LayoutParams.MATCH_PARENT,
                                    ViewGroup.LayoutParams.WRAP_CONTENT,
                                    (float) valueAnimator.getAnimatedValue()
                            ));
                            //view.invalidate();
                        }
                    });
                    if (((ToggleButton) findViewById(R.id.channelsBTN)).isChecked()) {
                        ((ToggleButton) findViewById(R.id.channelsBTN)).setChecked(false);
                    }
                    animation.start();
                } else {
                    animation = ValueAnimator.ofFloat(0f, 1f);
                    animation.setDuration(500);
                    animation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                        @Override
                        public void onAnimationUpdate(ValueAnimator valueAnimator) {
                            view.setLayoutParams(new LinearLayout.LayoutParams(
                                    ViewGroup.LayoutParams.MATCH_PARENT,
                                    ViewGroup.LayoutParams.WRAP_CONTENT,
                                    (float) valueAnimator.getAnimatedValue()
                            ));
                            // view.invalidate();
                        }
                    });
                    animation.start();
                }
            }
        });
        ((ToggleButton) findViewById(R.id.channelsBTN)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                ValueAnimator animation;
                final View view = findViewById(R.id.channelsView);
                if (b) {
                    animation = ValueAnimator.ofFloat(1f, 0f);
                    animation.setDuration(500);
                    animation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                        @Override
                        public void onAnimationUpdate(ValueAnimator valueAnimator) {
                            view.setLayoutParams(new LinearLayout.LayoutParams(
                                    ViewGroup.LayoutParams.MATCH_PARENT,
                                    ViewGroup.LayoutParams.MATCH_PARENT,
                                    (float) valueAnimator.getAnimatedValue()
                            ));
                            //view.invalidate();
                        }
                    });
                    if (((ToggleButton) findViewById(R.id.directMessagingBTN)).isChecked()) {
                        ((ToggleButton) findViewById(R.id.directMessagingBTN)).setChecked(false);
                    }
                    animation.start();
                } else {
                    animation = ValueAnimator.ofFloat(0f, 1f);
                    animation.setDuration(500);
                    animation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                        @Override
                        public void onAnimationUpdate(ValueAnimator valueAnimator) {
                            view.setLayoutParams(new LinearLayout.LayoutParams(
                                    ViewGroup.LayoutParams.MATCH_PARENT,
                                    ViewGroup.LayoutParams.MATCH_PARENT,
                                    (float) valueAnimator.getAnimatedValue()
                            ));
                            //view.invalidate();
                        }
                    });
                    animation.start();
                }
            }
        });

        // Make new channel
        ((Button) findViewById(R.id.createChannel)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final List<String> messengeeIDList = new ArrayList<>();

                final AlertDialog.Builder builder = new AlertDialog.Builder(MessengerActivity.this);
                final View newChannelView = getLayoutInflater().inflate(R.layout.create_channel, null);
                for (final User user : globals.getUsers()) {
                    View messengeeCell = getLayoutInflater().inflate(R.layout.messengee_cell, null);
                    messengeeCell.setTag(user.UserID);
                    ((TextView) messengeeCell.findViewById(R.id.messengeeName)).setText(user.First_Name + " " + user.Last_Name);
                    ((CheckBox) messengeeCell.findViewById(R.id.messengeeCheckbox)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                            if (b) {
                                messengeeIDList.add(user.UserID);
                            } else {
                                messengeeIDList.remove(user.UserID);
                            }
                        }
                    });
                    if (user.UserID.equals(globals.getLoggedIn().UserID)) {
                        ((CheckBox) messengeeCell.findViewById(R.id.messengeeCheckbox)).setChecked(true);
                    }
                    ((LinearLayout) newChannelView.findViewById(R.id.channelMembersField)).addView(messengeeCell);
                }
                final AlertDialog alertDialog = builder.create();

                ((Button) newChannelView.findViewById(R.id.cancelBTN)).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        alertDialog.dismiss();
                    }

                    });


                ((Button) newChannelView.findViewById(R.id.createChannelBTN)).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (!((EditText) newChannelView.findViewById(R.id.channelTitleField)).getText().toString().isEmpty()) {
                            if (!((EditText) newChannelView.findViewById(R.id.welcomeMessageField)).getText().toString().isEmpty()) {
                                if (messengeeIDList.size() > 0) {
                                    String messengeeIDListString = "";
                                    for (String id : messengeeIDList) {
                                        messengeeIDListString += id + ", ";
                                    }
                                    messengeeIDListString = messengeeIDListString.substring(0, messengeeIDListString.length() - 2);
                                    new Dialogue(
                                            "ChannelDialogues",
                                            ((EditText) newChannelView.findViewById(R.id.channelTitleField)).getText().toString(),
                                            messengeeIDListString,
                                            new Message(
                                                    globals.getLoggedIn().UserID,
                                                    String.valueOf(((Long) (Calendar.getInstance().getTimeInMillis() * 1000)).intValue()),
                                                    ((EditText) newChannelView.findViewById(R.id.welcomeMessageField)).getText().toString())
                                    );
                                    reload();
                                    alertDialog.dismiss();
                                } else {
                                    Toast toast = Toast.makeText(MessengerActivity.this, "You'll be very lonely...", Toast.LENGTH_SHORT);
                                    toast.show();
                                    Toast toasty = Toast.makeText(MessengerActivity.this, "Please add members to your channel!", Toast.LENGTH_LONG);
                                    toasty.show();
                                }
                            } else {
                                Toast toast = Toast.makeText(MessengerActivity.this, "Don't be rude!", Toast.LENGTH_SHORT);
                                toast.show();
                                Toast toasty = Toast.makeText(MessengerActivity.this, "A welcome message is required.", Toast.LENGTH_LONG);
                                toasty.show();
                            }
                        } else {
                            Toast toast = Toast.makeText(MessengerActivity.this, "Your channel needs a name!", Toast.LENGTH_LONG);
                            toast.show();
                        }
                    }
                });
                alertDialog.setView(newChannelView);
                alertDialog.show();
            }
        });
    }

    public void updateDialogueList(DataSnapshot dialoguesSnap) {
        if (dialoguesSnap.getKey().equals("DirectDialogues")) {
            directDialogues.clear();
            for (User user:globals.getUsers()){
                if(!user.UserID.equals(globals.getLoggedIn().UserID)){
                    boolean dialogueExists = false;
                    for(DataSnapshot snapshot:dialoguesSnap.getChildren()){
                        if(snapshot.getKey().contains(user.UserID) && snapshot.getKey().contains(globals.getLoggedIn().UserID)){
                            dialogueExists = true;
                        }
                    }
                    if(!dialogueExists){
                        List<String> ids = new ArrayList<>();
                        ids.add(user.UserID);
                        ids.add(globals.getLoggedIn().UserID);
                        Collections.sort(ids);
                        Message welcomeMessage = new Message(globals.getLoggedIn().UserID, String.valueOf(Calendar.getInstance().getTimeInMillis()),"Welcome to FratBase Direct Messenger!");
                        FirebaseDatabase.getInstance().getReference().child(globals.DatabaseNode()+"/DirectDialogues/"+ids.get(0)+", "+ids.get(1)+"/Messages/"+welcomeMessage.messageID).setValue(welcomeMessage.messageContent);
                        reload();
                    }
                }
            }
            for (DataSnapshot dialogueSnap : dialoguesSnap.getChildren()) {
                if (dialogueSnap.getKey().contains(globals.getLoggedIn().UserID)) {
                    directDialogues.add(new Dialogue(dialogueSnap.getKey()));
                }
            }
            //Collections.sort(directDialogues, Comparator.comparing((Dialogue dialogue) -> dialogue.lastMessage.timeSent)); !!!!!!!!!!!!!!!!!!******************!!!!!!!!!!!!!!!!!!!!!!!!
        } else if (dialoguesSnap.getKey().equals("ChannelDialogues")) {
            channelDialogues.clear();
            for (DataSnapshot dialogueSnap : dialoguesSnap.getChildren()) {
                if (dialogueSnap.child("Messengees").getValue().toString().contains(globals.getLoggedIn().UserID)) {
                    channelDialogues.add(new Dialogue(dialogueSnap.getKey()));
                }
            }
        }
    }

    public void writeDialoguesToScreen() {
        LinearLayout channelContainer = findViewById(R.id.channelsContainer);
        channelContainer.removeAllViews();
        for (final Dialogue dialogue : channelDialogues) {
            View channelCell = getLayoutInflater().inflate(R.layout.dialogue_cell, null);
            putInfoInCell(channelCell, dialogue, channelContainer);
            channelContainer.addView(channelCell);
            channelCell.setLongClickable(true);
            channelCell.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent messagingInterface = new Intent(MessengerActivity.this, MessagingInterfaceActivity.class);
                    messagingInterface.putExtra("dialogueID", dialogue.dialogueID);
                    messagingInterface.putExtra("dialogueType", dialogue.type);
                    messagingInterface.putExtra("dialogueName", dialogue.dialogueName);
                    startActivity(messagingInterface);
                }
            });
            channelCell.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    if ((globals.getLoggedIn().Position.equals("Master") && globals.getLoggedIn().Validated) || (globals.getLoggedIn().Position.equals("LT Master") && globals.getLoggedIn().Validated)) {
                        final AlertDialog.Builder builder = new AlertDialog.Builder(MessengerActivity.this);
                        builder.setTitle("Delete Channel?");
                        builder.setMessage("Are you sure you would like to delete this channel?");
                        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogueDB.channelsRef.child(dialogue.dialogueID).removeValue();
                                reload();
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

        }
        LinearLayout directContainer = findViewById(R.id.directsContainer);
        directContainer.removeAllViews();
        for (final Dialogue dialogue : directDialogues) {
            if (dialogue.dialogueID != "deleted"){
                View directCell = getLayoutInflater().inflate(R.layout.dialogue_cell, null);
                putInfoInCell(directCell, dialogue, directContainer);
                directCell.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent messagingInterface = new Intent(MessengerActivity.this, MessagingInterfaceActivity.class);
                        messagingInterface.putExtra("dialogueID", dialogue.dialogueID);
                        messagingInterface.putExtra("dialogueType", dialogue.type);
                        messagingInterface.putExtra("dialogueName", dialogue.dialogueName);
                        startActivity(messagingInterface);
                    }
                });
                directContainer.addView(directCell);
            }
        }
    }

    private void putInfoInCell(View dialogueCell, Dialogue dialogue, LinearLayout dialoguesContainer) {
        ((TextView) dialogueCell.findViewById(R.id.dialogueName)).setText(dialogue.dialogueName);
        Calendar lastMessageTimeSent = Calendar.getInstance();
        lastMessageTimeSent.setTimeInMillis(Long.parseLong(dialogue.lastMessage.timeSent));
        String lastMessageTimeSentString;
        if ((Long.parseLong(dialogue.lastMessage.timeSent) - Calendar.getInstance().getTimeInMillis()) < 64800000) {
            lastMessageTimeSentString = CalendarTools.formatTime(lastMessageTimeSent);
        } else {
            lastMessageTimeSentString = lastMessageTimeSent.get(Calendar.YEAR) + "-" + lastMessageTimeSent.get(Calendar.MONTH) + "-" + lastMessageTimeSent.get(Calendar.DAY_OF_MONTH);
        }
        ((TextView) dialogueCell.findViewById(R.id.lastMessageTimeSent)).setText(lastMessageTimeSentString);
        if (globals.getUserByID(dialogue.lastMessage.senderID) == null){
            ((TextView) dialogueCell.findViewById(R.id.lastSenderAndMessage)).setText("Deleted User: " + dialogue.lastMessage.messageContent);
        }else {
            ((TextView) dialogueCell.findViewById(R.id.lastSenderAndMessage)).setText(
                    globals.getUserByID(dialogue.lastMessage.senderID).First_Name + " " + globals.getUserByID(dialogue.lastMessage.senderID).Last_Name + ": " + dialogue.lastMessage.messageContent);
        }
    }
}
