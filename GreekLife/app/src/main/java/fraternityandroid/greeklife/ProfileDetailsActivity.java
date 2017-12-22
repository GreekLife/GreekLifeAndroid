package fraternityandroid.greeklife;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.InputStream;
import java.util.Calendar;
import java.util.UUID;

public class ProfileDetailsActivity extends AppCompatActivity {

    EditText first, last, brother, position, school, grad, birthday, degree, email;
    ImageButton image;
    Button button;
    /*
    WHATS LEFT:
    The position tab needs a picker to select possible options. So does the birthday and grad date.
    Image view needs to select a picture from gallery and store it
     */
    public static final int PICK_IMAGE = 1;
    private static final String TAG = "ProfileDetailsActivity";
    private StorageReference mStorageRef;

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == PICK_IMAGE) {
            if(data != null)
            {
                Bundle extras = data.getExtras();
                Bitmap imageBitmap = (Bitmap) extras.get("data");

                image.setImageBitmap(imageBitmap);

            };
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_details);
        mStorageRef = FirebaseStorage.getInstance().getReference();
        button = findViewById(R.id.SaveAccount);
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

        image = findViewById(R.id.Picture);
        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickImage();
            }
        });

        Bundle bundle = getIntent().getExtras();
        final String type = bundle.getString("Type");
        if(type.equals("UPDATE")) {
            Globals globals = Globals.getInstance();
            User user = globals.getLoggedIn();

            first.setText(user.First_Name);
            last.setText(user.Last_Name);
            brother.setText(user.BrotherName);
            position.setText(user.Position);
            school.setText(user.School);
            grad.setText(user.GraduationDate);
            birthday.setText(user.Birthday);
            degree.setText(user.Degere);
            email.setText(user.Email);
            email.setVisibility(View.VISIBLE);

            button.setText("Save");

            //set image
        }
    }

    public void pickImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
    }

    public void SaveAccount(View view) {
        Globals globals = Globals.getInstance();
        User user = globals.getLoggedIn();

        if (first.getText().toString().equals("") || last.getText().toString().equals("") || brother.getText().toString().equals("") || position.getText().toString().equals("") || school.getText().toString().equals("") || grad.getText().toString().equals("") || birthday.getText().toString().equals("") || degree.getText().toString().equals("")) {
            Toast.makeText(ProfileDetailsActivity.this, "No fields can be left empty",
                    Toast.LENGTH_SHORT).show();
            return;
        }
        if(!user.Username.equals("Master")) {
            if (first.getText().toString().equals("Master") || last.getText().toString().equals("Master") || brother.getText().toString().equals("") || position.getText().toString().equals("Master")) {
                Toast.makeText(ProfileDetailsActivity.this, "Your name cannot be Master",
                        Toast.LENGTH_SHORT).show();
                return;
            }
        }

        AccountDetailsActivity details = new AccountDetailsActivity();
        Bundle bundle = getIntent().getExtras();
        String type = bundle.getString("Type");
        String wId, wMail, wTitle;

        if (type.equals("CREATE")) {
            wId = bundle.getString("Id");
            wTitle = wId;
            wMail = bundle.getString("Email");
        } else {

            wId = user.UserID;
            wTitle = wId;
            wMail = user.Email;
        }

        if(user.Username.equals("Master")) {
            wTitle = "Master";
        }
        final String id = wId;
        final String title = wTitle;
        final String mail = wMail;

            Uri file = Uri.fromFile(new File("")); //some path
            StorageReference riversRef = mStorageRef.child("Images/" + id + ".jpg");

            riversRef.putFile(file)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Uri downloadUrl = taskSnapshot.getDownloadUrl();

                            FirebaseDatabase database = FirebaseDatabase.getInstance();
                            DatabaseReference myRef = database.getReference("Users/" + title);
                            User emptyUser = new User(brother.getText().toString(), id, birthday.getText().toString(), brother.getText().toString(), degree.getText().toString(), mail, first.getText().toString(), last.getText().toString(), grad.getText().toString(), downloadUrl.toString(), school.getText().toString(), position.getText().toString(), false);
                            myRef.setValue(emptyUser);
                            Log.d(TAG, "Profile created");

                            Intent login = new Intent(ProfileDetailsActivity.this, MainActivity.class);
                            startActivity(login);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            Toast.makeText(ProfileDetailsActivity.this, "Error handling your image. Your account could not be created.",
                                    Toast.LENGTH_SHORT).show();
                            Log.d(TAG, "Image error: Profile not created.");
                        }
                    });

        }
}
