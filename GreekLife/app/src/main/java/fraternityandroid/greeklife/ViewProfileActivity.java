package fraternityandroid.greeklife;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

public class ViewProfileActivity extends AppCompatActivity {

    EditText name,brotherName, position, school, grad, birthday, degree, email;
    TextView verified;
    Button verify;
    ImageView pic;
    Globals globals = Globals.getInstance();

    final String[] mPositions = {"Brother", "Alumni", "Pledge", "LT Master", "Scribe", "Exchequer", "Pledge Master", "Rush Chair"};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_profile);

        globals.IsBlocked(this);

        verify = findViewById(R.id.Verify);
        name = findViewById(R.id.Name);
        name.setInputType(0);

        brotherName = findViewById(R.id.BrotherName);
        brotherName.setEnabled(false);

        position = findViewById(R.id.Position);
        position.setEnabled(false);

        school = findViewById(R.id.School);
        school.setInputType(0);

        grad = findViewById(R.id.GradDate);
        grad.setInputType(0);

        birthday = findViewById(R.id.Birthday);
        birthday.setInputType(0);

        degree = findViewById(R.id.Degree);
        degree.setInputType(0);

        email = findViewById(R.id.Email);
        email.setInputType(0);

        verified = findViewById(R.id.Status);
        pic = findViewById(R.id.ProfilePicture);




        Bundle bundle = getIntent().getExtras();
        final String id = bundle.getString("id");

        Globals globals = Globals.getInstance();

        for(User member: globals.getUsers()) {
            if(member.UserID.equals(id)) {
                Glide.with(this)
                        .load(member.Image)
                        .into(pic);
                verified.setText(member.Validated.toString());
                name.setText(member.First_Name + " " + member.Last_Name);
                brotherName.setText(member.BrotherName);
                position.setText(member.Position);
                school.setText(member.School);
                grad.setText(member.GraduationDate);
                birthday.setText(member.Birthday);
                degree.setText(member.Degree);
                email.setText(member.Email);
                verify.setVisibility(View.GONE);

                if(!member.Validated && globals.getLoggedIn().Position.equals("Master")) {
                    verify.setVisibility(View.VISIBLE);
                    position.setEnabled(true);
                    brotherName.setEnabled(true);
                    DisableCopyPaste(position);

                    position.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(ViewProfileActivity.this);
                            builder.setTitle("Position")
                                    .setItems(mPositions, new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            position.setText(mPositions[which]);
                                        }
                                    });

                            AlertDialog alert = builder.create();

                            alert.show();
                        }
                    });
                }
            }
        }
    }

    public void Verify(View view) {
        Bundle bundle = getIntent().getExtras();
        final String id = bundle.getString("id");
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("Users/"+id+"/Validated");
        myRef.setValue(true);
        brotherName.setEnabled(false);
        position.setEnabled(false);
        verified.setText("true");
        verify.setVisibility(View.GONE);
    }


    public void DisableCopyPaste(EditText text) {
        text.setCustomSelectionActionModeCallback(new ActionMode.Callback() {

            public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
                return false;
            }

            public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
                return false;
            }

            public boolean onActionItemClicked(ActionMode actionMode, MenuItem item) {
                return false;
            }

            public void onDestroyActionMode(ActionMode actionMode) {
            }
        });

        text.setLongClickable(false);
    }

}
