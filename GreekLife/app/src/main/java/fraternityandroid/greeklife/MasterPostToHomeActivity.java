package fraternityandroid.greeklife;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MasterPostToHomeActivity extends AppCompatActivity {

    EditText post;
    TextView text;
    Button value;

    Globals globals = Globals.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_master_post_to_home);
        post = findViewById(R.id.NewPost);
        text = findViewById(R.id.Message);
        value = findViewById(R.id.post);

        Bundle bundle = getIntent().getExtras();
        final Boolean type = bundle.getBoolean("Posting");
        if(!type) {
            text.setText("Create a custom notification to be sent out to everyone.");
            value.setText("Send");
        }

    }

    public void postToHome(View view) {
        Bundle bundle = getIntent().getExtras();
        final Boolean type = bundle.getBoolean("Posting");

        if(type) {
            String id = UUID.randomUUID().toString();
            long epoch = System.currentTimeMillis();
            DatabaseReference database = FirebaseDatabase.getInstance().getReference();
            DatabaseReference ref = database.child(globals.DatabaseNode()+"/News/" + id);

            Map<String, Object> posting = new HashMap<String, Object>();
            posting.put("PostId", id);
            posting.put("Epoch", epoch);
            posting.put("Post", post.getText().toString());

            ref.setValue(posting);

            Intent master = new Intent(MasterPostToHomeActivity.this, MasterControlsActivity.class);
            startActivity(master);

        }
        else {
            DatabaseReference database = FirebaseDatabase.getInstance().getReference();
            DatabaseReference ref = database.child(globals.DatabaseNode()+"/GeneralMessage/Master");
            ref.setValue(post.getText().toString());
            Intent master = new Intent(MasterPostToHomeActivity.this, MasterControlsActivity.class);
            startActivity(master);
        }

    }
}
