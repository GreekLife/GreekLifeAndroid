package fraternityandroid.greeklife;

import android.app.ActionBar;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CreatePollActivity extends AppCompatActivity {

    ArrayList<EditText> mOptions = new ArrayList<>();
    Globals globals = Globals.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_poll);
        globals.IsBlocked(this);

        LinearLayout myRoot = (LinearLayout) findViewById(R.id.ScrollViewLayout);
        EditText newOption = new EditText(this);
        newOption.setHint("Option 1");
        newOption.setTextSize(12);
        myRoot.addView(newOption);
        mOptions.add(newOption);

    }

    private EditText createEditText()
    {
        final LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.WRAP_CONTENT); // Width , height
        final EditText option = new EditText(this);
        option.setLayoutParams(params);
        return option;
    }

    public void addOption(View view) {
        LinearLayout myRoot = (LinearLayout) findViewById(R.id.ScrollViewLayout);
        EditText newOption = createEditText();
        newOption.setTextSize(12);
        myRoot.addView(newOption);
        mOptions.add(newOption);
        newOption.setHint("Option " + mOptions.size());
    }

    public void removeOption(View view) {
        if(mOptions.size() > 1) {
            LinearLayout myRoot = (LinearLayout) findViewById(R.id.ScrollViewLayout);
            myRoot.removeView(mOptions.get(mOptions.size() - 1));
            mOptions.remove(mOptions.size() - 1);
        }
        else {
            Toast.makeText(this, "You need at least one option", Toast.LENGTH_SHORT).show();
        }
    }


    public void validate(View view) {
        Boolean valid = true;
        ArrayList<String> options = new ArrayList<>();
        for (EditText op : mOptions) {
            options.add(op.getText().toString());
        }
        EditText question = (EditText) findViewById(R.id.Question);
        if (question.getText().toString().length() < 1 || options.get(0).length() < 1) {
            Toast.makeText(this, "You must ask a question with at least one option", Toast.LENGTH_SHORT).show();
            valid = false;
        }
        for (String op : options) {
            if (op.length() < 1) {
                Toast.makeText(this, "You cannot leave any empty options", Toast.LENGTH_SHORT).show();
                valid = false;
            }
        }
        if (valid) {
            String id = UUID.randomUUID().toString();
            String time = Long.toString(System.currentTimeMillis() / 1000L);
            Double epoch = new Double(time + ".01");

            DatabaseReference database = FirebaseDatabase.getInstance().getReference();
            DatabaseReference ref = database.child(globals.DatabaseNode()+"/Polls/" + id);

            Map<String, Object> newPoll = new HashMap<String, Object>();


            newPoll.put("Options", options);
            newPoll.put("Title", question.getText().toString());
            newPoll.put("Epoch", epoch);
            newPoll.put("PostId", id);
            newPoll.put("PosterId", globals.getLoggedIn().UserID);
            newPoll.put("Poster", (globals.getLoggedIn().First_Name + " " + globals.getLoggedIn().Last_Name));

            ref.setValue(newPoll);

            DatabaseReference opRef = database.child(globals.DatabaseNode()+"/PollOptions/" + id + "/\"0\"/Names/" + globals.getLoggedIn().UserID);
            opRef.setValue(globals.getLoggedIn().UserID);

            DatabaseReference idRef = database.child(globals.DatabaseNode()+"/Polls/PollIds/" + id);
            idRef.setValue(id);


            Toast.makeText(this, "Post Created", Toast.LENGTH_SHORT).show();


        }
    }

}
