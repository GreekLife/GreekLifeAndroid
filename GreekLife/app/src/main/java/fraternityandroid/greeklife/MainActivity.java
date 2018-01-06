package fraternityandroid.greeklife;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

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



public class MainActivity extends AppCompatActivity {

    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private static final String Tag = "MainActivity";
    EditText mEmail, mPassword;
    EditText code1, code2, code3, code4;
    Button login;
    private FirebaseAuth mAuth;
    private static final String TAG = "MainActivity";
    Globals globals = Globals.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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

        code1.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence s, int start,int before, int count)
            {
                if(code1.getText().toString().length()==1) { code2.requestFocus();}
            }
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            public void afterTextChanged(Editable s) {
                if(code1.getText().toString().length() > 1) {String cutStrng = code1.getText().toString().substring(0, 1); code1.setText(cutStrng);}
            }
        });
        code2.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence s, int start,int before, int count)
            {
                if(code2.getText().toString().length()==1) { code3.requestFocus();}
            }
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            public void afterTextChanged(Editable s) {
                if(code2.getText().toString().length() > 1) {String cutStrng = code2.getText().toString().substring(0, 1); code2.setText(cutStrng);}

            }
        });
        code3.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence s, int start,int before, int count)
            {
                if(code3.getText().toString().length()==1) { code4.requestFocus();}
            }
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            public void afterTextChanged(Editable s) {
                if(code3.getText().toString().length() > 1) {String cutStrng = code3.getText().toString().substring(0, 1); code3.setText(cutStrng);}
            }
        });
        code4.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence s, int start,int before, int count) {}
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            public void afterTextChanged(Editable s) {
                if(code4.getText().toString().length() > 1) {String cutStrng = code4.getText().toString().substring(0, 1); code4.setText(cutStrng);}
            }
        });


    }

    public void Login(View view) {
        findViewById(R.id.loadingPanel).setVisibility(View.VISIBLE);
        Boolean valid = ValidateEntry();
        if(valid) {
                validateExists();
        }
        else {
            findViewById(R.id.loadingPanel).setVisibility(View.GONE);
            Toast.makeText(MainActivity.this, "Please enter a username and password.",
                    Toast.LENGTH_SHORT).show();

        }
    }

    public Boolean ValidateEntry(){
        return !(mEmail.getText().toString().equals("") || mPassword.getText().toString().equals(""));
    }


    public void validateExists() {
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        DatabaseReference ref = database.child(globals.DatabaseNode()+"/Users");

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
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
                            Log.d(TAG, "Authenticating process failed");
                            Toast.makeText(MainActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }

                });
   }

   public void CreateAccount(View view) {
       if(code1.getText().toString().equals("") || code2.getText().toString().equals("") || code3.getText().toString().equals("") || code4.getText().toString().equals("")) {
           code1.requestFocus();
           Toast.makeText(MainActivity.this, "You must enter the correct code", Toast.LENGTH_SHORT).show();
           return;
       }
       else {
           DatabaseReference database = FirebaseDatabase.getInstance().getReference();
           DatabaseReference ref = database.child(globals.DatabaseNode()+"/CreateAccount/GeneratedKey");

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


}
