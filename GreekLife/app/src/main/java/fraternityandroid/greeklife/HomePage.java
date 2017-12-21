package fraternityandroid.greeklife;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class HomePage extends AppCompatActivity {

    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        ImageButton googleDrive = findViewById(R.id.GoogleDrive);
        googleDrive.setEnabled(false);

        ImageButton masterIcon = findViewById(R.id.Master);
        Globals globals = Globals.getInstance();
        String user = globals.getLoggedIn().Username;
        if(!user.equals("Master")) {
            masterIcon.setVisibility(View.GONE);
        }
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
        Globals globals = Globals.getInstance();
        globals.setLoggedIn(null);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("Password", null);
        editor.commit();
        mAuth.getInstance().signOut();
        Intent signout = new Intent(HomePage.this, MainActivity.class);
        startActivity(signout);
    }
    public void MasterControlsIntent(View view) {
        Intent master = new Intent(HomePage.this, MasterControlsActivity.class);
        startActivity(master);
    }


}
