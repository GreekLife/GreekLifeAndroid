package fraternityandroid.greeklife;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class HomePage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);
    }

    public void ImIntent(View view) {
       // Intent messaging = new Intent(HomePage.this, MessagingActivity.class);
        //startActivity(messaging);
    }
    public void ForumIntent(View view) {
       // Intent forum = new Intent(HomePage.this, ForumActivity.class);
        //startActivity(forum);

    }
    public void CalendarIntent(View view) {
        //Intent calendar = new Intent(HomePage.this, CalendarActivity.class);
        //startActivity(calendar);
    }
    public void PollIntent(View view) {
       // Intent poll = new Intent(HomePage.this, PollsActivity.class);
        //startActivity(poll);
    }
    public void MemberIntent(View view) {
        Intent member = new Intent(HomePage.this, MembersActivity.class);
        startActivity(member);
    }
    public void ProfileIntent(View view) {
        Intent profile = new Intent(HomePage.this, ProfileDetailsActivity.class);
        startActivity(profile);
    }
    public void GoogleIntent(View view) {
     //   Intent google = new Intent(HomePage.this, GoogleDriveActivity.class);
       // startActivity(google);
    }
    public void InfoIntent(View view) {
        Intent info = new Intent(HomePage.this, InfoActivity.class);
        startActivity(info);
    }
    public void Signout(View view) {
        Intent signout = new Intent(HomePage.this, MainActivity.class);
        startActivity(signout);
    }
    public void MasterControllsIntent(View view) {
        Intent master = new Intent(HomePage.this, MasterControlsActivity.class);
        startActivity(master);
    }


}
