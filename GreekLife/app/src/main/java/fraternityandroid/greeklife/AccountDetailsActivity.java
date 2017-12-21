package fraternityandroid.greeklife;

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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.UUID;


public class AccountDetailsActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    public EditText mEmail;
    public EditText mPassword;
    public EditText mRePassword;
    public String mId = null;
    private static final String TAG = "AccountDetailsActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_details);

        mAuth = FirebaseAuth.getInstance();
        mEmail = findViewById(R.id.Email);
        mPassword = findViewById(R.id.Password);
        mRePassword = findViewById(R.id.RePassword);
    }

    public void submitDetails(View view) {
        if(!(mPassword.getText().toString().equals(mRePassword.getText().toString())) || mEmail.getText().toString().equals("")){
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
                            String id = UUID.randomUUID().toString();
                            mId = id;
                            DatabaseReference myRef = database.getReference("Users/"+id);
                            User emptyUser = new User("",id,"","","",mEmail.getText().toString(),"","","","","","", false);
                            myRef.setValue(emptyUser);
                            Intent completeAuth = new Intent(AccountDetailsActivity.this, ProfileDetailsActivity.class);
                            completeAuth.putExtra("Id", id);
                            completeAuth.putExtra("Email", mEmail.getText().toString());
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
