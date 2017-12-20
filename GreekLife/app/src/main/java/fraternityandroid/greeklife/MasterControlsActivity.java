package fraternityandroid.greeklife;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.concurrent.ThreadLocalRandom;

public class MasterControlsActivity extends AppCompatActivity {


    private DatabaseReference mDatabase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_master_controls);
    }

    public void GenerateCode() {
        int val1 = ThreadLocalRandom.current().nextInt(0, 10);
        int val2 = ThreadLocalRandom.current().nextInt(0, 10);
        int val3 = ThreadLocalRandom.current().nextInt(0, 10);
        int val4 = ThreadLocalRandom.current().nextInt(0, 10);
        String newCode = Integer.toString(val1) + Integer.toString(val2) + Integer.toString(val3) + Integer.toString(val4);
// ...
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("CreateAccount/Code").setValue(newCode);

    }
}
