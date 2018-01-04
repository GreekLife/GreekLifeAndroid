package fraternityandroid.greeklife;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;

public class MembersActivity extends AppCompatActivity {

    //https://www.raywenderlich.com/127544/android-gridview-getting-started
    final Globals globals = Globals.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_members);
        globals.IsBlocked(MembersActivity.this);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.HomeToolbar);
        TextView header = new TextView(this);
        header.setText("Members");
        header.setTypeface(Typeface.create("monospace", Typeface.NORMAL));
        header.setTextColor(Color.parseColor("#FFDF00"));

        //image button for back <-

        myToolbar.addView(header);

        GridView gridView = (GridView)findViewById(R.id.MemberGrid);
        MembersAdapter memberAdapter = new MembersAdapter(this, globals.getUsers());
        gridView.setAdapter(memberAdapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView parent, View view, int position, long id) {
                Intent member = new Intent(MembersActivity.this, ViewProfileActivity.class);
                member.putExtra("id", globals.getUsers().get(position).UserID);
                startActivity(member);
            }
        });

    }
}
