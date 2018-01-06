package fraternityandroid.greeklife;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Comment;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ForumCommentActivity extends AppCompatActivity {

    CommentAdapter commentAdapter;
    private static final String TAG = "ForumCommentActivity";
    Globals globals = Globals.getInstance();
    TextView post, title, name, date;
    EditText newComment;
    ListView mList;

    Forum mPost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forum_comment);

        globals.IsBlocked(ForumCommentActivity.this);


        post = (TextView) findViewById(R.id.ClickedPost);
        title = (TextView) findViewById(R.id.ClickedPostTitle);
        name = (TextView) findViewById(R.id.ClickedPoster);
        date = (TextView) findViewById(R.id.ClickedPostDate);
        newComment = (EditText) findViewById(R.id.newComment);

        post.setMovementMethod(new ScrollingMovementMethod());
        title.setMovementMethod(new ScrollingMovementMethod());

        Bundle bundle = getIntent().getExtras();
        final String postId = bundle.getString("PostId");
        mPost = globals.getPostById(postId);

        post.setText(mPost.getPost());
        title.setText(mPost.getPostTitle());
        name.setText(mPost.getPoster());
        long epoch = (long) mPost.getEpoch();
        long currentEpoch =  System.currentTimeMillis() / 1000L;
        long secondsSince = currentEpoch - epoch;
        int hours = (int) (secondsSince / 3600);
        int days = hours/24;
        String display;
        if(days < 1) {
            display = Integer.toString(hours) + "h";
        }
        else {
            display = Integer.toString(days) + "d";
        }
        date.setText(display);

        getComments();

    }

    public void PostComment(View view) {
        String time = Long.toString(System.currentTimeMillis()/1000L);
        Double epoch = new Double(time+".01");
        String id = UUID.randomUUID().toString();
        String user = globals.getLoggedIn().First_Name + " " + globals.getLoggedIn().Last_Name;
        Map<String, Object> newPost = new HashMap<String, Object>();
        newPost.put("Post", newComment.getText().toString());
        newPost.put("Epoch", epoch); //get epoch time
        newPost.put("PosterId", globals.getLoggedIn().UserID);
        newPost.put("CommentId", id);
        newPost.put("Poster", user);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference(globals.DatabaseNode()+"/Forum/"+mPost.getPostId()+"/Comments/"+id);
        myRef.setValue(newPost);
        Toast.makeText(ForumCommentActivity.this, "Your comment has been successfully made.", Toast.LENGTH_SHORT).show();
        newComment.setText("");

    }

    public void getComments() {
            DatabaseReference database = FirebaseDatabase.getInstance().getReference();
            DatabaseReference ref = database.child(globals.DatabaseNode()+"/Forum/"+mPost.getPostId());
            ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    Map<String, Object> comments = (HashMap<String, Object>) snapshot.child("Comments").getValue();
                    List<fraternityandroid.greeklife.Comment> comment = new ArrayList<>();

                    if(comments != null) {
                        for (String key : comments.keySet()) {
                            Map<String, Object> val = (Map<String, Object>) comments.get(key);
                            fraternityandroid.greeklife.Comment countedComment = new fraternityandroid.greeklife.Comment(((String) val.get("Poster")), ((double) val.get("Epoch")), ((String) val.get("Post")), ((String) val.get("CommentId")), ((String) val.get("PosterId")));
                            comment.add(countedComment);
                        }
                    }
                    globals.getPostById(mPost.getPostId()).setComments(comment);
                    mPost.setComments(comment);
                    commentAdapter = new CommentAdapter(ForumCommentActivity.this, mPost.getComments(), mPost);

                    mList = (ListView) findViewById(R.id.CommentList);

                    mList.setAdapter(commentAdapter);

                    mList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView parent, View view, int position, long id) {

                        }
                    });

                    commentAdapter.notifyDataSetChanged();

                }
                @Override
                public void onCancelled(DatabaseError error) {
                    Log.d(TAG, "Error reloading comments");
                    findViewById(R.id.loadingPanel).setVisibility(View.GONE);
                    findViewById(R.id.loadingPanel).setVisibility(View.GONE);

                }

            });

    }
}
