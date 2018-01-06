package fraternityandroid.greeklife;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HomePage extends AppCompatActivity {

    /*
    TODO: Super weird, everytime a user value updates, we get redirected back to the home page???
     */

    private FirebaseAuth mAuth;
    private static final String TAG = "HomePage";
    ListView mListView;
    List<String> mNews = new ArrayList<String>();
    List<String> mNewsIds = new ArrayList<String>();
    Globals globals = Globals.getInstance();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.HomeToolbar);
        ImageButton button = new ImageButton(this);
        button.setImageResource(R.drawable.signout);
        button.setBackgroundColor(Color.alpha(0));

         button.setOnClickListener(new View.OnClickListener() {
             @Override
            public void onClick(View v) {
               Signout();
            }
        });
         myToolbar.addView(button);

        ActivityManager am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        int memoryClass;
        try {
            memoryClass = am.getMemoryClass();
            Log.v("onCreate", "memoryClass:" + Integer.toString(memoryClass));
        }
        catch(NullPointerException e) {
            //do nothing with it
        }
        globals.IsBlocked(HomePage.this);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();

        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("NotificationKey", refreshedToken);
        editor.apply();

        updateNotificationKey(refreshedToken);

        //getPictures();

        ImageButton googleDrive = findViewById(R.id.GoogleDrive);
        googleDrive.setEnabled(false);

        ImageButton masterIcon = findViewById(R.id.Master);
        String user = globals.getLoggedIn().Position;
        if(!user.equals("Master")) {
            masterIcon.setVisibility(View.GONE);
        }

        getNews();
        GetUsers();
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
                mNewsIds.clear();
                mNews.clear();
                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                    String post = (String) userSnapshot.child("Post").getValue();
                    String id = (String) userSnapshot.child("PostId").getValue();
                    mNewsIds.add(id);
                    mNews.add(post);
                }
                mListView = findViewById(R.id.List);


                LinearLayout layout = findViewById(R.id.HomeNewsCell);
                HomeAdapter adapter = new HomeAdapter(HomePage.this, mNews);


                mListView.setAdapter(adapter);
                adapter.notifyDataSetChanged();
                Globals globals = Globals.getInstance();
                String user = globals.getLoggedIn().Position;

                if(user.equals("Master")) {
                    mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                            AlertDialog.Builder builder;
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                builder = new AlertDialog.Builder(HomePage.this, android.R.style.Theme_Material_Dialog_Alert);
                            } else {
                                builder = new AlertDialog.Builder(HomePage.this);
                            }
                            builder.setTitle("Delete")
                                    .setMessage("Would you like to delete this post?")
                                    .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            String id = mNewsIds.get(position);
                                            FirebaseDatabase database = FirebaseDatabase.getInstance();
                                            DatabaseReference myRef = database.getReference("News/"+id);
                                            myRef.removeValue();
                                        }
                                    })
                                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    dialog.dismiss();
                                                }
                                            })
                                    .setIcon(android.R.drawable.ic_dialog_alert)
                                    .show();
                        }
                    });
                }

                Log.d(TAG, "News retrieved");
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.d(TAG, "Error retrieving news");
            }
        });
    }

    public void GetUsers() {
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        DatabaseReference ref = database.child("Users");

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                List<User> users = new ArrayList<User>();
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
                }
                globals.setUsers(users);
                getImages();

            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.d(TAG, "Error retrieving users");


                //log error
            }
        });
    }
    public void getImages() {
        new RetrievePictureBitmaps().execute();
    }

    public void BETA(View view) {
         Intent beta = new Intent(HomePage.this, BETAActivity.class);
         startActivity(beta);
    }

    public void ImIntent(View view) {
       // Intent messaging = new Intent(HomePage.this, MessagingActivity.class);
        //startActivity(messaging);
    }
    public void ForumIntent(View view) {
        Intent forum = new Intent(HomePage.this, ForumActivity.class);
        startActivity(forum);

    }
    public void CalendarIntent(View view) {
        Intent calendar = new Intent(HomePage.this, CalendarActivity.class);
        startActivity(calendar);
        Log.d("blah","Tried to start calendar app");
    }
    public void PollIntent(View view) {
        Intent poll = new Intent(HomePage.this, PollActivity.class);
        startActivity(poll);
    }
    public void MemberIntent(View view) {
        Intent member = new Intent(HomePage.this, MembersActivity.class);
        startActivity(member);
    }
    public void ProfileIntent(View view) {
        Intent profile = new Intent(HomePage.this, ProfileDetailsActivity.class);
        profile.putExtra("Type", "UPDATE");
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
    public void Signout() {
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

    public void updateNotificationKey(String token) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef2 = database.getReference("NotificationIds/Android"); //Does this successfully add the object?
        Map<String, Object> keyObj = new HashMap<>();
        keyObj.put("Id", token);
        keyObj.put("UserId", globals.getLoggedIn().UserID);
        keyObj.put("Username", globals.getLoggedIn().Username);
        myRef2.child(token).setValue(keyObj);

        String id = globals.getLoggedIn().UserID;

        DatabaseReference updateProf = database.getReference("Users/"+id+"/NotificationId");
        updateProf.setValue(token);
    }


}
