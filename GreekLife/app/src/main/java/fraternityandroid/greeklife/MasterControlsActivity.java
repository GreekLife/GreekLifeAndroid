package fraternityandroid.greeklife;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.concurrent.ThreadLocalRandom;

public class MasterControlsActivity extends AppCompatActivity {


    private DatabaseReference mDatabase;
    private static final String TAG = "MainActivity";

    Button code;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_master_controls);
        code = findViewById(R.id.Code);
        getCode();
    }

    public void getCode() {
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        DatabaseReference ref = database.child("CreateAccount/GeneratedKey");

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                 code.setText(snapshot.getValue().toString());

            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.d(TAG, "Error getting code:", error.toException());

            }
        });

    }

    public void GenerateCode(View view) {
        int val1 = ThreadLocalRandom.current().nextInt(0, 10);
        int val2 = ThreadLocalRandom.current().nextInt(0, 10);
        int val3 = ThreadLocalRandom.current().nextInt(0, 10);
        int val4 = ThreadLocalRandom.current().nextInt(0, 10);
        String newCode = Integer.toString(val1) + Integer.toString(val2) + Integer.toString(val3) + Integer.toString(val4);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("CreateAccount/GeneratedKey").setValue(newCode);

    }

    public void EditInfo(View view) {
        Intent info = new Intent(this, EditInfoActivity.class);
        startActivity(info);
    }

    public void PostToHome(View view) {
        Intent post = new Intent(MasterControlsActivity.this, MasterPostToHomeActivity.class);
        post.putExtra("Posting", true);
        startActivity(post);
    }

    public void CustomNotif(View view) {
        Intent notif = new Intent(MasterControlsActivity.this, MasterPostToHomeActivity.class);
        notif.putExtra("Posting", false);
        startActivity(notif);
    }

    public void KickUser(View view) {
        Intent kick = new Intent(MasterControlsActivity.this, ManageUserActivity.class);
        kick.putExtra("List", "KICK");
        startActivity(kick);
    }

    public void Ban(View view) {
        Intent Ban = new Intent(MasterControlsActivity.this, ManageUserActivity.class);
        Ban.putExtra("List", "BAN");
        startActivity(Ban);
    }

    public void Verify(View view) {
        Intent Verify = new Intent(MasterControlsActivity.this, ManageUserActivity.class);
        Verify.putExtra("List", "VERIFY");
        startActivity(Verify);
    }
}
