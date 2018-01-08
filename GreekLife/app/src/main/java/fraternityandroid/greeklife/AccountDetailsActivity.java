package fraternityandroid.greeklife;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.HashMap;
import java.util.Map;

public class AccountDetailsActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    public EditText mEmail;
    public EditText mPassword;
    public EditText mRePassword;
    public String mId = null;
    private static final String TAG = "AccountDetailsActivity";
    Globals globals = Globals.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_details);
        globals.IsBlocked(this);

        mAuth = FirebaseAuth.getInstance();
        mEmail = findViewById(R.id.Email);
        mPassword = findViewById(R.id.Password);
        mRePassword = findViewById(R.id.RePassword);
    }

    public void submitDetails(View view) {
        if(mEmail.getText().toString().equals("") || mPassword.getText().toString().equals("") || mRePassword.getText().toString().equals("")) {
            Toast.makeText(AccountDetailsActivity.this, "You must fill in all fields",
                    Toast.LENGTH_SHORT).show();
            return;
        }
        else if(!(mPassword.getText().toString().equals(mRePassword.getText().toString()))){
            Toast.makeText(AccountDetailsActivity.this, "Your passwords do not match",
                    Toast.LENGTH_SHORT).show();
            return;
        }
        mAuth.createUserWithEmailAndPassword(mEmail.getText().toString(), mPassword.getText().toString())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseDatabase database = FirebaseDatabase.getInstance();
                            String id = mAuth.getCurrentUser().getUid();
                            mId = id;
                            DatabaseReference myRef = database.getReference(globals.DatabaseNode()+"/Users/"+id);

                            String refreshedToken = FirebaseInstanceId.getInstance().getToken();
                            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                            SharedPreferences.Editor editor = prefs.edit();
                            editor.putString("NotificationKey", refreshedToken);
                            editor.apply();

                            Map<String, Object> userEmpty = new HashMap<>(); //using a hashmap becuase im stupid and the name keys need a space.
                            User emptyUser = new User("",id,"","","",mEmail.getText().toString(),"","","","","","", false);
                            userEmpty.put("Username", "");
                            userEmpty.put("Birthday", "");
                            userEmpty.put("BrotherName", "");
                            userEmpty.put("Degree", "");
                            userEmpty.put("Email", mEmail.getText().toString());
                            userEmpty.put("First Name", "");
                            userEmpty.put("Last Name", "");
                            userEmpty.put("Image", "");
                            userEmpty.put("School", "");
                            userEmpty.put("Position", "");
                            userEmpty.put("GraduationDate", "");
                            userEmpty.put("NotificationId", refreshedToken);
                            userEmpty.put("Validated", false);
                            userEmpty.put("UserID", id);


                            myRef.setValue(userEmpty);
                            Intent completeAuth = new Intent(AccountDetailsActivity.this, ProfileDetailsActivity.class);
                            completeAuth.putExtra("Id", id);
                            completeAuth.putExtra("Email", mEmail.getText().toString());
                            completeAuth.putExtra("Type", "CREATE");
                            startActivity(completeAuth);
                        } else {
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(AccountDetailsActivity.this, "This email is either invalid or already exists. Your password must be at least 6 characters long",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }
}
