package fraternityandroid.greeklife;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

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

        newest = (Button)findViewById(R.id.Newest);
        oldest = (Button)findViewById(R.id.Oldest);
        week = (Button)findViewById(R.id.Week);
        month = (Button)findViewById(R.id.Month);

        forumAdapter = new ForumAdapter(ForumActivity.this, globals.getPosts());

        mList = (ListView) findViewById(R.id.ForumList);

        mList.setAdapter(forumAdapter);

        mList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView parent, View view, int position, long id) {

            }
        });

        forumAdapter.notifyDataSetChanged();

    }

    @Override
    public void onBackPressed()
    {
        globals.setDelete(false);
    }


    public void newest(View view) {
        globals.setPostOrder(Globals.PostOrder.NEWEST);
        newest.setBackgroundColor(Color.parseColor("#8080ff"));
        oldest.setBackgroundColor(Color.parseColor("#141A6E"));
        week.setBackgroundColor(Color.parseColor("#141A6E"));
        month.setBackgroundColor(Color.parseColor("#141A6E"));

        forumAdapter.notifyDataSetChanged();
    }
    public void oldest(View view) {
        globals.setPostOrder(Globals.PostOrder.OLDEST);
        newest.setBackgroundColor(Color.parseColor("#141A6E"));
        oldest.setBackgroundColor(Color.parseColor("#8080ff"));
        week.setBackgroundColor(Color.parseColor("#141A6E"));
        month.setBackgroundColor(Color.parseColor("#141A6E"));

        forumAdapter.notifyDataSetChanged();
    }
    public void week(View view) {
        globals.setPostOrder(Globals.PostOrder.WEEK);
        newest.setBackgroundColor(Color.parseColor("#141A6E"));
        oldest.setBackgroundColor(Color.parseColor("#141A6E"));
        week.setBackgroundColor(Color.parseColor("#8080ff"));
        month.setBackgroundColor(Color.parseColor("#141A6E"));

        forumAdapter.notifyDataSetChanged();
    }
    public void month(View view) {
        globals.setPostOrder(Globals.PostOrder.MONTH);
        newest.setBackgroundColor(Color.parseColor("#141A6E"));
        oldest.setBackgroundColor(Color.parseColor("#141A6E"));
        week.setBackgroundColor(Color.parseColor("#141A6E"));
        month.setBackgroundColor(Color.parseColor("#8080ff"));

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

}
