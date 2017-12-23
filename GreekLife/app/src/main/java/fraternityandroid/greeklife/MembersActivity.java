package fraternityandroid.greeklife;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

public class MembersActivity extends AppCompatActivity {

    //https://www.raywenderlich.com/127544/android-gridview-getting-started

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_members);

        TemporaryBan.IsBlocked(MembersActivity.this);

        final Globals globals = Globals.getInstance();

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
