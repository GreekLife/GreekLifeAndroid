package fraternityandroid.greeklife;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ForumActivity extends AppCompatActivity {

    ListView mList;
    Button newest, oldest, week, month;
    private static final String TAG = "ForumActivity";
    Globals globals = Globals.getInstance();
    ForumAdapter forumAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forum);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.HomeToolbar);
        TextView header = new TextView(this);
        header.setText("Forum");
        header.setTypeface(Typeface.create("monospace", Typeface.NORMAL));
        header.setTextColor(Color.parseColor("#FFDF00"));


        myToolbar.addView(header);

        globals.IsBlocked(ForumActivity.this);
        findViewById(R.id.loadingPanel).setVisibility(View.VISIBLE);
        getForumPosts();

        newest = (Button)findViewById(R.id.Newest);
        oldest = (Button)findViewById(R.id.Oldest);
        week = (Button)findViewById(R.id.Week);
        month = (Button)findViewById(R.id.Month);

        globals.setPostOrder(Globals.PostOrder.NEWEST);
        newest.setBackgroundColor(Color.parseColor("#222222"));

    }


    public void newest(View view) {
        globals.setPostOrder(Globals.PostOrder.NEWEST);
        newest.setBackgroundColor(Color.parseColor("#222222"));
        oldest.setBackgroundColor(Color.parseColor("#00FFFFFF"));
        oldest.setBackground(ContextCompat.getDrawable(this, R.drawable.thick_border));
        month.setBackgroundColor(Color.parseColor("#00FFFFFF"));
        month.setBackground(ContextCompat.getDrawable(this, R.drawable.thick_border));
        week.setBackgroundColor(Color.parseColor("#00FFFFFF"));
        week.setBackground(ContextCompat.getDrawable(this, R.drawable.thick_border));

        forumAdapter.notifyDataSetChanged();
    }
    public void oldest(View view) {
        globals.setPostOrder(Globals.PostOrder.OLDEST);
        newest.setBackgroundColor(Color.parseColor("#00FFFFFF"));
        newest.setBackground(ContextCompat.getDrawable(this, R.drawable.thick_border));
        oldest.setBackgroundColor(Color.parseColor("#222222"));
        week.setBackgroundColor(Color.parseColor("#00FFFFFF"));
        week.setBackground(ContextCompat.getDrawable(this, R.drawable.thick_border));
        month.setBackgroundColor(Color.parseColor("#00FFFFFF"));
        month.setBackground(ContextCompat.getDrawable(this, R.drawable.thick_border));

        forumAdapter.notifyDataSetChanged();
    }
    public void week(View view) {
        globals.setPostOrder(Globals.PostOrder.WEEK);
        newest.setBackgroundColor(Color.parseColor("#00FFFFFF"));
        newest.setBackground(ContextCompat.getDrawable(this, R.drawable.thick_border));
        oldest.setBackgroundColor(Color.parseColor("#00FFFFFF"));
        oldest.setBackground(ContextCompat.getDrawable(this, R.drawable.thick_border));

        week.setBackgroundColor(Color.parseColor("#222222"));

        month.setBackgroundColor(Color.parseColor("#00FFFFFF"));
        month.setBackground(ContextCompat.getDrawable(this, R.drawable.thick_border));

        forumAdapter.notifyDataSetChanged();
    }
    public void month(View view) {
        globals.setPostOrder(Globals.PostOrder.MONTH);
        newest.setBackgroundColor(Color.parseColor("#00FFFFFF"));
        newest.setBackground(ContextCompat.getDrawable(this, R.drawable.thick_border));
        oldest.setBackgroundColor(Color.parseColor("#00FFFFFF"));
        oldest.setBackground(ContextCompat.getDrawable(this, R.drawable.thick_border));
        week.setBackgroundColor(Color.parseColor("#00FFFFFF"));
        week.setBackground(ContextCompat.getDrawable(this, R.drawable.thick_border));
        month.setBackgroundColor(Color.parseColor("#222222"));


        forumAdapter.notifyDataSetChanged();
    }

    public void CreatePost(View view) {
        globals.setDelete(false);
        Intent create = new Intent(ForumActivity.this, CreateForumActivity.class);
          startActivity(create);
    }

    public void DeletePost(View view) {
        if(!globals.getDelete()) {
            globals.setDelete(true);
        }
        else {
            globals.setDelete(false);
        }
        forumAdapter.notifyDataSetChanged();
    }

    public void getForumPosts() {
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        DatabaseReference ref = database.child(globals.DatabaseNode()+"/Forum");

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                Globals globals = Globals.getInstance();
                List<Forum> posts = new ArrayList<>();
                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                    if(!((String) userSnapshot.getKey()).equals("ForumIds")) {
                        String post = (String) userSnapshot.child("Post").getValue();
                        double epoch = (double) userSnapshot.child("Epoch").getValue();
                        String postId = (String) userSnapshot.child("PostId").getValue();
                        String postTitle = (String) userSnapshot.child("PostTitle").getValue();
                        String poster = (String) userSnapshot.child("Poster").getValue();
                        String posterId = (String) userSnapshot.child("PosterId").getValue();
                        Map<String, Object> comments = (HashMap<String, Object>) userSnapshot.child("Comments").getValue();

                        List<Comment> comment = new ArrayList<>();

                        if(comments != null) {
                            for (String key : comments.keySet()) {
                                Map<String, Object> val = (Map<String, Object>) comments.get(key);
                                Comment countedComment = new Comment(((String) val.get("Poster")), ((double) val.get("Epoch")), ((String) val.get("Post")), ((String) val.get("CommentId")), ((String) val.get("PosterId")));
                                comment.add(countedComment);
                            }
                        }
                        Collections.reverse(comment);

                        long numberOfComments;
                        if(comments == null) {
                            numberOfComments = 0;
                        }
                        else {
                            numberOfComments = comments.size();
                        }
                        Forum newPost = new Forum(numberOfComments, epoch, post, postId, postTitle, poster, posterId, comment);
                        posts.add(newPost);
                    }

                }
                Collections.reverse(posts); //dont need this?
                globals.setPosts(posts);
                forumAdapter = new ForumAdapter(ForumActivity.this, globals.getPosts());

                mList = (ListView) findViewById(R.id.ForumList);

                mList.setAdapter(forumAdapter);

                mList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView parent, View view, int position, long id) {

                    }
                });

                forumAdapter.notifyDataSetChanged();
                findViewById(R.id.loadingPanel).setVisibility(View.GONE);

            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.d(TAG, "Error loading posts");
                findViewById(R.id.loadingPanel).setVisibility(View.GONE);
                findViewById(R.id.loadingPanel).setVisibility(View.GONE);

            }

        });
    }

}
