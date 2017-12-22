package fraternityandroid.greeklife;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Build;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;

public class HomePage extends AppCompatActivity {

    /*
    TODO: Super weird, everytime a user value updates, we get redirected back to the home page???

    Ban:
    TODO: Make ban run no matter what page youre on.
    TODO: Ban phrase to large for dialog
    TODO: Timer not functional
     */

    private FirebaseAuth mAuth;
    private static final String TAG = "HomePage";

    List<User> users = new ArrayList<User>();
    ListView mListView;
    List<String> mNews;

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

        List<String> list = new ArrayList<String>(Arrays.asList("111,222,333,444,555,666".split(",")));
        mListView.setAdapter(new NewsCellAdapter(mNews, HomePage.this) );

        //https://stackoverflow.com/questions/40862154/how-to-create-listview-items-button-in-each-row

        //getNews();
        GetUsers();
        IsBlocked();

    }

    @Override
    public void onBackPressed() {
        //do nothing
    }

    public void getNews() {

        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        DatabaseReference ref = database.child("News");

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                    String post = (String) userSnapshot.child("Post").getValue();
                    mNews.add(post);
                }
                mListView = (ListView) findViewById(R.id.List);

                ArrayAdapter<String> adapter = new ArrayAdapter<String>(HomePage.this,  android.R.layout.simple_list_item_1, android.R.id.text1, mNews);

                mListView.setAdapter(adapter);

                Log.d(TAG, "News retrieved");
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.d(TAG, "Error retrieving news");
            }
        });
    }


    public void IsBlocked() {
        Globals globals = Globals.getInstance();
        String id = globals.getLoggedIn().UserID;
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
       final  DatabaseReference ref = database.child("Blocked/"+id);

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {

                if (((Boolean)snapshot.child("Blocked").getValue())) {
                    getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    long delay = (long) snapshot.child("Delay").getValue();
                     AlertDialog.Builder builder;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        builder = new AlertDialog.Builder(HomePage.this, android.R.style.Theme_Material_Dialog_Alert);
                    } else {
                        builder = new AlertDialog.Builder(HomePage.this);
                    }
                    final AlertDialog alert = builder.create();
                    builder.setTitle("Get Wrecked")
                            .setTitle("Your Master has temporarily disabled your access. It will return in " + delay + " minutes")
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();

                    long time = delay * 60;
                    new CountDownTimer(5000, time) {

                        @Override
                        public void onTick(long millisUntilFinished) {
                        }

                        @Override
                        public void onFinish() {
                            alert.dismiss();
                            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                            Map<String, Object> ban = new HashMap<String, Object>();
                            ban.put("Blocked", false);
                            ban.put("Delay", 0);
                            ref.setValue(ban);
                        }
                    }.start();
                    Log.d(TAG, "Users retrieved");

                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.d(TAG, "Error retrieving users");


                //log error
            }
        });
    }

    public void GetUsers() {
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        DatabaseReference ref = database.child("Users");

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                    String email = (String) userSnapshot.child("Email").getValue();
                    String username = (String) userSnapshot.child("Username").getValue();
                    String birthday = (String) userSnapshot.child("Birthday").getValue();
                    String brother = (String) userSnapshot.child("BrotherName").getValue();
                    String degree = (String) userSnapshot.child("Degree").getValue();
                    String first = (String) userSnapshot.child("First Name").getValue();
                    String last = (String) userSnapshot.child("Last Name").getValue();
                    String grad = (String) userSnapshot.child("GraduationDate").getValue();
                    String imageURL = (String) userSnapshot.child("Image").getValue();
                    String position = (String) userSnapshot.child("Position").getValue();
                    String school = (String) userSnapshot.child("School").getValue();
                    String userId = (String) userSnapshot.child("UserID").getValue();
                    Boolean validated = (Boolean) userSnapshot.child("Validated").getValue();
                    User user = new User(username, userId, birthday, brother, degree, email, first, last, grad, imageURL, school, position, validated);
                    users.add(user);
                    Log.d(TAG, "Users retrieved");

                }
                Globals globals = Globals.getInstance();
                globals.setUsers(users);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.d(TAG, "Error retrieving users");


                //log error
            }
        });
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
