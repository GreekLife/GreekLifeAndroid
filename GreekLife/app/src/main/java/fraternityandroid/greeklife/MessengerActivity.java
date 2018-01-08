package fraternityandroid.greeklife;

import android.app.Dialog;
import android.content.res.Configuration;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class MessengerActivity extends AppCompatActivity {

    static Globals globals = Globals.getInstance();
    private DialogueDB dialogueDB = new DialogueDB();

    public  ValueEventListener  channelsListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            dialogueDB.channelsSnap = dataSnapshot;
            updateDialogueList(dialogueDB.channelsSnap);
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };
    public  ValueEventListener  directsListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            dialogueDB.directsSnap = dataSnapshot;
            updateDialogueList(dialogueDB.directsSnap);
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };

    public class DialogueDB {
        public DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference().child(globals.DatabaseNode());
        public  DatabaseReference channelsRef = FirebaseDatabase.getInstance().getReference().child(globals.DatabaseNode()+"/ChannelDialogues");
        public  DatabaseReference directsRef = FirebaseDatabase.getInstance().getReference().child(globals.DatabaseNode()+"/DirectDialogues");
        public  DataSnapshot channelsSnap;
        public  DataSnapshot directsSnap;

        public  void cleanUp () {
            channelsRef.removeEventListener(channelsListener);
            directsRef.removeEventListener(directsListener);
        }


    }

    public class Dialogue {
        public String dialogueID;
        public ArrayList<Message> messages;
        public String[] messengeeIDs;
        public ArrayList<User> messengees;
        public String type;
        public String dialogueName;
        public DataSnapshot dialogueSnap;
        public Message lastMessage;

        public Dialogue (String dialogueID)
        {
            this.dialogueID = dialogueID;

            if (dialogueID.contains(", "))
            {
                this.type = "DirectDialogues";
                this.dialogueSnap = dialogueDB.directsSnap;

                this.messengeeIDs = dialogueID.split(", ").clone();

                this.dialogueName = "";
                for (String messengeeID:this.messengeeIDs) {
                    if (messengeeID != globals.getLoggedIn().UserID) {
                        this.dialogueName += globals.getUserByID(messengeeID).First_Name + " " + globals.getUserByID(messengeeID) + ", ";
                        this.dialogueName = this.dialogueName.substring(0, this.dialogueName.length()-2);
                    }
                }
            }
            else
            {
                this.type = "ChannelDialogues";
                this.dialogueSnap = dialogueDB.channelsSnap;

                this.messengeeIDs = dialogueSnap.child("Messengees").getValue().toString().split(", ").clone();
            }
            this.dialogueSnap = dialogueSnap.child(this.dialogueID);

            for(String messengeeID:messengeeIDs) {
                this.messengees.add(globals.getUserByID(messengeeID));
            }

            for (DataSnapshot messageSnap:dialogueSnap.child("Messages").getChildren())
            {
                String messageID = messageSnap.getKey();
                messages.add(new Message(messageID, this));
            }
            this.lastMessage = this.messages.get(this.messages.size() - 1);

            this.dialogueName = this.dialogueSnap.child("Name").getValue().toString();
        }

        public void sendMessage (Message message) {
            dialogueDB.dbRef.child(this.type+"/Messages/"+message.messageID).setValue(message.messageContent);
        }

    }

    public class Message {
        public String messageID;
        public String messageContent;
        public String senderID;
        public String timeSent;

        public Message (String messageID, Dialogue dialogue)
        {
            this.messageID = messageID;

            String[] idComponents = messageID.split(", ");
            this.timeSent = idComponents[0];
            this.senderID = idComponents[1];

            messageContent = dialogue.dialogueSnap.child(dialogue.dialogueID+"/Messages/"+ messageID).getValue().toString();
        }
        public Message (String senderID, String timeSent, String messageContent){
            this.senderID = senderID;
            this.timeSent = timeSent;
            this.messageContent = messageContent;
            this.messageID = timeSent +", "+ senderID;
        }
    }

    public  ArrayList<Dialogue> directDialogues = new ArrayList<>();
    public  ArrayList<Dialogue> channelDialogues = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messenger);

        this.dialogueDB.directsRef.addValueEventListener(this.directsListener);
        this.dialogueDB.channelsRef.addValueEventListener(this.channelsListener);

    }

    public void updateDialogueList (DataSnapshot dialoguesSnap)
    {
        if(dialoguesSnap.getKey() == "DirectDialogues") {
            for(DataSnapshot dialogueSnap:dialoguesSnap.getChildren()) {
                directDialogues.add(new Dialogue(dialogueSnap.getKey()));
            }
            //Collections.sort(directDialogues, Comparator.comparing((Dialogue dialogue) -> dialogue.lastMessage.timeSent));
        } else if (dialoguesSnap.getKey() == "ChannelDialogues") {
            for(DataSnapshot dialogueSnap:dialoguesSnap.getChildren()) {
                channelDialogues.add(new Dialogue(dialogueSnap.getKey()));
            }
        }

    }
}
