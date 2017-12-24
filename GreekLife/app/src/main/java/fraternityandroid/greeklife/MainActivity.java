package fraternityandroid.greeklife;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.nfc.Tag;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.pusher.android.PusherAndroid;
import com.pusher.android.PusherAndroidOptions;
import com.pusher.android.notifications.ManifestValidator;
import com.pusher.android.notifications.PushNotificationRegistration;
import com.pusher.android.notifications.tokens.PushNotificationRegistrationListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MainActivity extends AppCompatActivity {

    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private static final String Tag = "yourtag";
    EditText mEmail, mPassword;
    EditText code1, code2, code3, code4;
    Button login;
    private FirebaseAuth mAuth;
    private static final String TAG = "MainActivity";
    List<Forum> mForumPosts = new ArrayList<Forum>();


    /*
        WHATS LEFT:
        The number boxes should only hold one or two vals and ideally shift after entered.
        Forgot password is not showing
     */
    PushNotificationRegistrationListener listener = new PushNotificationRegistrationListener() {
        @Override
        public void onSuccessfulRegistration() {
            System.out.println("REGISTRATION SUCCESSFUL!!! YEEEEEHAWWWWW!");

        }

        @Override
        public void onFailedRegistration(int statusCode, String response) {
            System.out.println(
                    "A real sad day. Registration failed with code " + statusCode +
                            " " + response
            );
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getForumPosts();

        if (playServicesAvailable()) {
            PusherAndroid pusher = new PusherAndroid("AIzaSyDoOWPGkVYOx0dUZu8USGJGW00FAAyMwCk");
            PushNotificationRegistration nativePusher = pusher.nativePusher();

            // pulled from your google-services.json
            String defaultSenderId = getString(R.string.gcm_defaultSenderId);
            try {
                nativePusher.registerGCM(this, defaultSenderId);
            } catch (ManifestValidator.InvalidManifestException e) {
                e.printStackTrace();
            }

            // Ready to subscribe to topics!
            nativePusher.subscribe("kittens");
        }


        mEmail = findViewById(R.id.Email);
        mPassword = findViewById(R.id.Password);
        mEmail.requestFocus();
        code1 = findViewById(R.id.code1);
        code2 = findViewById(R.id.code2);
        code3 = findViewById(R.id.code3);
        code4 = findViewById(R.id.code4);
        login = findViewById(R.id.Login);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String username = prefs.getString("Username", null);
        String password = prefs.getString("Password", null);

        if(username != null && password != null) {
            mEmail.setText(username);
            mPassword.setText(password);
            login.performClick();
        }
        else {
            findViewById(R.id.loadingPanel).setVisibility(View.GONE);
        }


    }

    public void Login(View view) {
        findViewById(R.id.loadingPanel).setVisibility(View.VISIBLE);
        Boolean valid = ValidateEntry();
        if(valid) {
                validateExists();
        }
        else {
            Toast.makeText(MainActivity.this, "Please enter a username and password.",
                    Toast.LENGTH_SHORT).show();

        }
    }

    public Boolean ValidateEntry(){
        return !(mEmail.getText().toString().equals("") || mPassword.getText().toString().equals(""));
    }


    public void validateExists() {
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        DatabaseReference ref = database.child("Users");

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                for (DataSnapshot userSnapshot: snapshot.getChildren()) {
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
                    Globals globals = Globals.getInstance();
                    if (mEmail.getText().toString().equals(email)) {
                            Log.d(TAG, "User found!");
                            User user = new User(username, userId, birthday, brother, degree, email, first, last, grad, imageURL, school, position, validated);
                            globals.setLoggedIn(user);
                            authenticate();
                            return;
                        }
                    else if(mEmail.getText().toString().equals(username)) {
                        mEmail.setText(email);
                        User user = new User(username, userId, birthday, brother, degree, email, first, last, grad, imageURL, school, position, validated);
                        globals.setLoggedIn(user);
                        authenticate();
                        return;
                    }
                }
                Log.d(TAG, "User not found");
                Toast.makeText(MainActivity.this, "No such user exists.",
                        Toast.LENGTH_SHORT).show();
                findViewById(R.id.loadingPanel).setVisibility(View.GONE);

            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.d(TAG, "Error finding user");
                findViewById(R.id.loadingPanel).setVisibility(View.GONE);


                //log error
            }
        });
    }

    public void authenticate() {
        mAuth = FirebaseAuth.getInstance();
        System.out.println("Authenticating");
        mAuth.signInWithEmailAndPassword(mEmail.getText().toString(), mPassword.getText().toString())
                .addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            System.out.println("Authenticating Succeeded");
                            Log.d(TAG, "Authenticating Succeeded");
                            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                            FirebaseUser user = mAuth.getCurrentUser();
                            SharedPreferences.Editor editor = prefs.edit();
                            editor.putString("Username", mEmail.getText().toString());
                            editor.putString("Password", mPassword.getText().toString());
                            editor.commit();
                            findViewById(R.id.loadingPanel).setVisibility(View.GONE);

                            Intent goToHomePage = new Intent(MainActivity.this, HomePage.class);
                            startActivity(goToHomePage);
                        } else {
                            Globals globals = Globals.getInstance();
                            globals.setLoggedIn(null);
                            findViewById(R.id.loadingPanel).setVisibility(View.GONE);
                            Log.d(TAG, "Authenticating Failed");
                            Toast.makeText(MainActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }

                });
   }

   public void CreateAccount(View view) {
       if(code1.getText().toString().equals("") || code2.getText().toString().equals("") || code3.getText().toString().equals("") || code4.getText().toString().equals("")) {
           code1.requestFocus();
           return;
       }
       else {
           DatabaseReference database = FirebaseDatabase.getInstance().getReference();
           DatabaseReference ref = database.child("CreateAccount/GeneratedKey");

           ref.addListenerForSingleValueEvent(new ValueEventListener() {
               @Override
               public void onDataChange(DataSnapshot snapshot) {
                   String code = (String) snapshot.getValue();
                   String attempt = code1.getText().toString()+code2.getText().toString()+code3.getText().toString()+code4.getText().toString();
                  if(attempt.equals(code)) {
                      Intent create = new Intent(MainActivity.this, AccountDetailsActivity.class);
                      startActivity(create);
                  }
                 else {
                   AlertDialog.Builder builder;
                   if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                       builder = new AlertDialog.Builder(MainActivity.this, android.R.style.Theme_Material_Dialog_Alert);
                   } else {
                       builder = new AlertDialog.Builder(MainActivity.this);
                   }
                   builder.setTitle("Wrong code")
                           .setMessage("That's the wrong code. You can get the correct one from the groups master")
                           .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                               public void onClick(DialogInterface dialog, int which) {
                                   // continue with delete
                               }
                           })
                           .setIcon(android.R.drawable.ic_dialog_alert)
                           .show();
                  }
               }

               @Override
               public void onCancelled(DatabaseError error) {
                   Log.d(TAG, "Error getting code:", error.toException());

               }
           });
       }

   }

    public void ForgotPassword(View view) {
        Intent forgot = new Intent(MainActivity.this, ForgotPasswordActivity.class);
        startActivity(forgot);

    }



    private boolean playServicesAvailable() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(MainActivity.this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST)
                        .show();
            } else {
                finish();
            }
            return false;
        }
        return true;
    }

    public void getForumPosts() {
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        DatabaseReference ref = database.child("Forum");

        ref.addValueEventListener(new ValueEventListener() {
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
                        Map<String, Object> comments = (Map<String, Object>) userSnapshot.child("Comments").getValue();
                        long numberOfComments;
                        if(comments == null) {
                            numberOfComments = 0;
                        }
                        else {
                            numberOfComments = comments.size();
                        }
                        Forum newPost = new Forum(numberOfComments, epoch, post, postId, postTitle, poster, posterId);
                        posts.add(newPost);
                    }

                }
                Collections.reverse(posts);
                globals.setPosts(posts);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.d(TAG, "Error loading posts");
                findViewById(R.id.loadingPanel).setVisibility(View.GONE);

            }

        });
    }




}
