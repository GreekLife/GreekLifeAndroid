package fraternityandroid.greeklife;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.nfc.Tag;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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

import java.util.HashMap;
import java.util.Map;


public class MainActivity extends AppCompatActivity {

    EditText mEmail;
    EditText mPassword;
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mEmail = findViewById(R.id.Email);
        mPassword = findViewById(R.id.Password);
    }

    public void Login(View view) {
        System.out.println("Clickedddd!!!");
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
        System.out.println("Validating!!!");
        return !(mEmail.getText().toString().equals("") || mPassword.getText().toString().equals(""));
    }


    public void validateExists() {
        System.out.println("Valdating!!!");
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        DatabaseReference ref = database.child("Users");

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                for (DataSnapshot postSnapshot: snapshot.getChildren()) {
                    Map<String, Object> post = (HashMap<String, Object>) postSnapshot.getValue();
                    for (Map.Entry<String, Object> entry : post.entrySet()) {
                        if (mEmail.getText().toString().equals(entry.getValue())) {
                            System.out.println("found it!!!");
                            //authenticate();
                            Intent goToHomePage = new Intent(MainActivity.this, HomePage.class);
                            startActivity(goToHomePage);
                            return;
                        }
                    }
                    System.out.println("Didnt find it!!!");
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                System.out.println("Errorrrr it!!!");

                //log error
            }
        });
    }

    public void authenticate() {
        mAuth = FirebaseAuth.getInstance();
        System.out.println("Authenticating"+mAuth);
        mAuth.signInWithEmailAndPassword(mEmail.getText().toString(), mPassword.getText().toString())
                .addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            System.out.println("Authenticating Succeeded");
                            //SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                            FirebaseUser user = mAuth.getCurrentUser(); //find a way to retain thisf
                            // SharedPreferences.Editor editor = prefs.edit();
                            Intent goToHomePage = new Intent(MainActivity.this, HomePage.class);
                            startActivity(goToHomePage);
                        } else {
                            System.out.println("Authenticating FAiled");
                            Toast.makeText(MainActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }

                });
   }


   public void CreateAccount(View view) {
       final EditText code1 = findViewById(R.id.code1);
       final EditText code2 = findViewById(R.id.code2);
       final EditText code3 = findViewById(R.id.code3);
       final EditText code4 = findViewById(R.id.code4);
       if(code1 == null || code2 == null || code3 == null || code4 == null) {
           code1.requestFocus();
           return;
       }
       else {
           DatabaseReference database = FirebaseDatabase.getInstance().getReference();
           DatabaseReference ref = database.child("CreateAccount");

           ref.addListenerForSingleValueEvent(new ValueEventListener() {
               @Override
               public void onDataChange(DataSnapshot snapshot) {
                  Object code = snapshot.getValue(Object.class);

                  String attempt = code1.getText().toString()+code2.getText().toString()+code3.getText().toString()+code4.getText().toString();
//                  if(attempt.equals(code)) {
//                      Intent create = new Intent(MainActivity.this, AccountDetailsActivity.class);
//                      startActivity(create);
//                  }
//                  else {
                   AlertDialog.Builder builder;
                   if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                       builder = new AlertDialog.Builder(MainActivity.this, android.R.style.Theme_Material_Dialog_Alert);
                   } else {
                       builder = new AlertDialog.Builder(MainActivity.this);
                   }
                   builder.setTitle("Wrong code")
                           .setMessage("Thats the wrong code. You can get the correct one from the groups master")
                           .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                               public void onClick(DialogInterface dialog, int which) {
                                   // continue with delete
                               }
                           })
                           .setIcon(android.R.drawable.ic_dialog_alert)
                           .show();
//                  }
               }

               @Override
               public void onCancelled(DatabaseError error) {
                   System.out.println("Error getting code: " + error);
               }
           });
       }

   }

    public void ForgotPassword(View view) {
        Intent forgot = new Intent(MainActivity.this, ForgotPasswordActivity.class);
        startActivity(forgot);
    }


}
