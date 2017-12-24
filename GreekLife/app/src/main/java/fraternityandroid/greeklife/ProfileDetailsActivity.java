package fraternityandroid.greeklife;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;


public class ProfileDetailsActivity extends AppCompatActivity {

    EditText first, last, brother, position, school, grad, birthday, degree, email;
    ImageButton image;
    Button button;
    private StorageReference mStorage;
    Uri uri;

    String firstEmail = "";

    String text = "";

    final Calendar myCalendar = Calendar.getInstance();

    final String[] mPositions = {"Brother", "Alumni", "Pledge", "LT Master", "Scribe", "Exchequer", "Pledge Master", "Rush Chair"};

    public static final int PICK_IMAGE = 1;
    private static final String TAG = "ProfileDetailsActivity";
    private StorageReference mStorageRef;

    //http://square.github.io/picasso/

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == PICK_IMAGE) {
            try {
                uri = data.getData();
                final InputStream imageStream = getContentResolver().openInputStream(uri);
                final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                image.setImageBitmap(selectedImage);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Toast.makeText(ProfileDetailsActivity.this, "Something went wrong", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void updateLabel() {
        DateFormat sdf = DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.US);
        if(text.equals("GRAD")) {
            grad.setText(sdf.format(myCalendar.getTime()));
        }
        if(text.equals("BDAY")) {
            birthday.setText(sdf.format(myCalendar.getTime()));
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //TemporaryBan.IsBlocked(ProfileDetailsActivity.this);

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


       final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel();
            }

        };

        grad.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                text = "GRAD";
                new DatePickerDialog(ProfileDetailsActivity.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        birthday.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                text = "BDAY";
                new DatePickerDialog(ProfileDetailsActivity.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        position.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ProfileDetailsActivity.this);
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

        Globals globals = Globals.getInstance();
        User user = globals.getLoggedIn();

        image = findViewById(R.id.Picture);
        Picasso.with(ProfileDetailsActivity.this).load(user.Image).into(image);

        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickImage();
            }
        });

        Bundle bundle = getIntent().getExtras();
        final String type = bundle.getString("Type");
        if(type.equals("UPDATE")) {

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
            firstEmail = email.getText().toString();

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
        if (user == null || !user.Username.equals("Master")) {
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

        if(type.equals("UPDATE")) {
            if(!firstEmail.equals(email.getText().toString())) {
                FirebaseUser update = FirebaseAuth.getInstance().getCurrentUser();

                update.updateEmail(email.getText().toString())
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Log.d(TAG, "User email address updated.");
                                }
                            }
                        });
            }
        }

        if (type.equals("CREATE")) {
            wId = bundle.getString("Id");
            wTitle = wId;
            wMail = bundle.getString("Email");
        } else {

            wId = user.UserID;
            wTitle = wId;
            wMail = user.Email;
            if(user.Username.equals("Master")) {
                wTitle = "Master";
            }
        }

        final String id = wId;
        final String title = wTitle;
        final String mail = wMail;

            StorageReference filepath = mStorageRef.child("ProfilePictures/" + id + ".jpg");

        filepath.putFile(uri)
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
