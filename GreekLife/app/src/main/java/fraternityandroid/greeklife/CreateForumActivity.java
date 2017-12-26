package fraternityandroid.greeklife;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CreateForumActivity extends AppCompatActivity {

    EditText title, post;
    TextView poster;
    ImageView postPic;

    Globals globals = Globals.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_forum);

        title = (EditText) findViewById(R.id.PostTitle);
        post = (EditText) findViewById(R.id.Posting);
        poster = (TextView) findViewById(R.id.Poster);
        postPic = (ImageView) findViewById(R.id.PosterPic);

        post.setMaxLines(10);
        title.setMaxLines(3);

        Picasso.with(CreateForumActivity.this).load(globals.getLoggedIn().Image).into(postPic);
        String name = globals.getLoggedIn().First_Name + " " + globals.getLoggedIn().Last_Name;
        poster.setText(name);

    }

    public void postToForum(View view) {
        if(title.getText().toString().equals("") || title.getText().toString().length()<15)  {
            Toast.makeText(CreateForumActivity.this, "Your title must be at least 30 characters", Toast.LENGTH_SHORT).show();
        }
        if(post.getText().toString().equals("") || post.getText().toString().length()<40)  {
            Toast.makeText(CreateForumActivity.this, "Your post must be at least 40 characters", Toast.LENGTH_SHORT).show();
        }
        String id = UUID.randomUUID().toString();
        String time = Long.toString(System.currentTimeMillis()/1000L);
        Double epoch = new Double(time+".01");
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("Forum/"+id);
        Map<String, Object> newPost = new HashMap<String, Object>();
        newPost.put("Post", post.getText().toString());
        newPost.put("PostTitle", title.getText().toString());
        newPost.put("Epoch", epoch); //get epoch time
        newPost.put("PosterId", globals.getLoggedIn().UserID);
        newPost.put("PostId", id);
        newPost.put("Username", globals.getLoggedIn().Username);
        newPost.put("Poster", (globals.getLoggedIn().First_Name + " " + globals.getLoggedIn().Last_Name));
        //Test for success
        myRef.setValue(newPost);
        DatabaseReference myRef2 = database.getReference("Forum/ForumIds"); //Does this successfully add the object?
        myRef2.child(id).setValue(id);

        Toast.makeText(CreateForumActivity.this, "Your post has been successfully made.", Toast.LENGTH_SHORT).show();
        title.setText("");
        post.setText("");
    }
}
