package fraternityandroid.greeklife;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class HomePage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);
    }

    public void ImIntent() {
        Intent messaging = new Intent(HomePage.this, MessagingActivity.class);
        startActivity(messaging);
    }
    public void ForumIntent() {
        Intent forum = new Intent(HomePage.this, ForumActivity.class);
        startActivity(forum);

    }
    public void CalendarIntent() {
        Intent calendar = new Intent(HomePage.this, CalendarActivity.class);
        startActivity(calendar);
    }
    public void PollIntent() {
        Intent poll = new Intent(HomePage.this, PollsActivity.class);
        startActivity(poll);
    }
    public void MemberIntent() {
        Intent member = new Intent(HomePage.this, MembersActivity.class);
        startActivity(member);
    }
    public void ProfileIntent() {
        Intent profile = new Intent(HomePage.this, ProfileActivity.class);
        startActivity(profile);
    }
    public void GoogleIntent() {
        Intent google = new Intent(HomePage.this, GoogleDriveActivity.class);
        startActivity(google);
    }
    public void InfoIntent() {
        Intent info = new Intent(HomePage.this, InfoActivity.class);
        startActivity(info);
    }
    public void Signout() {
        Intent signout = new Intent(HomePage.this, MainActivity.class);
        startActivity(signout);
    }
    public void MasterControllsIntent() {
        Intent master = new Intent(HomePage.this, MasterControlsActivity.class);
        startActivity(master);
    }


}
