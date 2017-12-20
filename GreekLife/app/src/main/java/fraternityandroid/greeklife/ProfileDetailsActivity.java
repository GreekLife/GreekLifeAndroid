package fraternityandroid.greeklife;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.UUID;

public class ProfileDetailsActivity extends AppCompatActivity {

    EditText first, last, brother, position, school, grad, birthday, degree, email;
    private static final String TAG = "ProfileDetailsActivity";

    // ImageView image;

    /*
    WHATS LEFT:
    The position tab needs a picker to select possible options. So does the birthday and grad date.
    Image view needs to select a picture from gallery and store it
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_details);

        first = findViewById(R.id.FirstName);
        last = findViewById(R.id.LastName);
        brother = findViewById(R.id.BrotherName);
        position = findViewById(R.id.Position);
        school = findViewById(R.id.School);
        grad = findViewById(R.id.GradDate);
        birthday = findViewById(R.id.Birthday);
        degree = findViewById(R.id.Degree);
        email = findViewById(R.id.Email);
        email.setVisibility(View.GONE);

//        image.findViewById(R.id.Picture);
    }

    public void SaveAccount(View view) {
        if(first.getText().toString().equals("") || last.getText().toString().equals("") || brother.getText().toString().equals("") || position.getText().toString().equals("") || school.getText().toString().equals("") || grad.getText().toString().equals("") || birthday.getText().toString().equals("") || degree.getText().toString().equals("")){
            Toast.makeText(ProfileDetailsActivity.this, "No fields can be left empty",
                    Toast.LENGTH_SHORT).show();
            return;
        }
        if(first.getText().toString().equals("Master") || last.getText().toString().equals("Master") || brother.getText().toString().equals("") || position.getText().toString().equals("Master")) {
            Toast.makeText(ProfileDetailsActivity.this, "Your name cannot be Master",
                    Toast.LENGTH_SHORT).show();
            return;
        }
        AccountDetailsActivity details = new AccountDetailsActivity();
        Bundle bundle = getIntent().getExtras();
        String id = bundle.getString("Id");
        String mail = bundle.getString("Email");
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("Users/"+id);
        User emptyUser = new User(brother.getText().toString(),id,birthday.getText().toString(),brother.getText().toString(),degree.getText().toString(),mail,first.getText().toString(),last.getText().toString(),grad.getText().toString(),"",school.getText().toString(),position.getText().toString(), false);
        myRef.setValue(emptyUser);
        Log.d(TAG, "Profile created");

        Intent login = new Intent(ProfileDetailsActivity.this, MainActivity.class);
        startActivity(login);

    }
}
