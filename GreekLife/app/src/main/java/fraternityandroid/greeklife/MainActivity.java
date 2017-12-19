package fraternityandroid.greeklife;

import android.app.AlertDialog;
import android.content.Intent;
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
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.Dictionary;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    EditText mEmail;
    EditText mPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mEmail = findViewById(R.id.Email);
        mPassword = findViewById(R.id.Password);
    }

    public void Login(View view) {
        Boolean valid = ValidateEntry();
        if(valid) {
                validateExists(new OnGetDataListener() {
                    @Override
                    public void onStart() {}
                    @Override
                    public void onSuccess(DataSnapshot data) {
                        mAuth.signInWithEmailAndPassword(mEmail.getText().toString(), mPassword.getText().toString())
                                .addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if (task.isSuccessful()) {
                                            // Sign in success, update UI with the signed-in user's information
                                            FirebaseUser user = mAuth.getCurrentUser();
                                            Intent goToHomePage = new Intent(MainActivity.this, HomePage.class);
                                            startActivity(goToHomePage);
                                        } else {
                                            Toast.makeText(MainActivity.this, "Authentication failed.",
                                                    Toast.LENGTH_SHORT).show();
                                        }
                                    }

                                });
                        }
                    @Override
                    public void onFailed(DatabaseError databaseError) {
                        AlertDialog.Builder emptyField = new AlertDialog.Builder(MainActivity.this);
                        emptyField.setMessage("Enter a username and a password");
                        emptyField.setCancelable(true);
                        AlertDialog emptyAlert = emptyField.create();
                        emptyAlert.show();
                    }
                });
            }
    }

    public Boolean ValidateEntry(){
        return !(mEmail.getText().toString().equals("") || mPassword.getText().toString().equals(""));
    }


    public void validateExists(final OnGetDataListener listener) {
        listener.onStart();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("Users");

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                for (DataSnapshot postSnapshot: snapshot.getChildren()) {
                   // User user = postSnapshot.getValue(User.class);
                    GenericTypeIndicator<List<User>> genericTypeIndicator = new GenericTypeIndicator<List<User>>(){};
                    List<User> users =postSnapshot.getValue(genericTypeIndicator);
                    System.out.println(users);
                    /*
                    if(mEmail.getText().toString().equals(users.Username) || mEmail.getText().toString().equals(users.Email) {
                        listener.onSuccess(snapshot);
                    }
                     */
                }
                //listener.onFailed();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                listener.onFailed(error);
                //log error
            }
        });
    }


}
