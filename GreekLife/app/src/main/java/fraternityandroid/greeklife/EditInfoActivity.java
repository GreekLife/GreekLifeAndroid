package fraternityandroid.greeklife;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class EditInfoActivity extends AppCompatActivity {

    EditText mMaster, mChapter, mFounding;
    ImageButton mImage;
    String logoURL;
    String originalURL;
    public static final int PICK_IMAGE = 1;

    Globals globals = Globals.getInstance();

    private static final String TAG = "EditInfoActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_info);

        mImage = (ImageButton) findViewById(R.id.InfoLogo);
        mImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickImage(v);
            }
        });


        Toolbar myToolbar = (Toolbar) findViewById(R.id.EditInfoToolbar);
        Button button = new Button(this);
        button.setText("Save");
        button.setBackgroundColor(Color.TRANSPARENT);
        button.setTextColor(Color.parseColor("#7b1e7ce6"));
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UpdateInfo();
            }
        });
        myToolbar.addView(button);


        getInfo();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data != null) {
            if (requestCode == PICK_IMAGE) {
                try {
                    Uri uri = data.getData();
                    logoURL = uri.toString();
                    final InputStream imageStream = getContentResolver().openInputStream(uri);
                    final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                    mImage.setImageBitmap(selectedImage);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    public void UpdateInfo() {
        final Uri uri = Uri.parse(logoURL);
        if (mMaster.getText().toString().equals("") || mChapter.getText().toString().equals("") || mFounding.getText().toString().equals("")) {
            Toast.makeText(this, "You cannot leave any info empty", Toast.LENGTH_SHORT).show();
        }
        if (logoURL.equals("")) {
            Toast.makeText(this, "You cannot upload an empty picture", Toast.LENGTH_SHORT).show();
        }
        StorageReference mStorageRef = FirebaseStorage.getInstance().getReference();

        StorageReference filepath = mStorageRef.child(globals.DatabaseNode()+"/Info/InfoLogoImage.jpg");
        if (uri == null) {
            Toast.makeText(this, "You need a profile picture", Toast.LENGTH_SHORT).show();
            return;
        }

        if(originalURL.equals(logoURL)) {
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference myRef = database.getReference(globals.DatabaseNode()+"/Info");
            Map<String, Object> info = new HashMap<>();
            info.put("ActiveMaster", mMaster.getText().toString());
            info.put("ChapterName", mChapter.getText().toString());
            info.put("FoundingDate", mFounding.getText().toString());
            info.put("ChapterLogoURL", logoURL);
            myRef.setValue(info);
            Toast.makeText(EditInfoActivity.this, "Info page has been updated", Toast.LENGTH_SHORT).show();
        }
        else {
            filepath.putFile(uri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Uri downloadUrl = taskSnapshot.getDownloadUrl();
                            FirebaseDatabase database = FirebaseDatabase.getInstance();
                            DatabaseReference myRef = database.getReference(globals.DatabaseNode()+"/Info");
                            Map<String, Object> info = new HashMap<>();
                            info.put("ActiveMaster", mMaster.getText().toString());
                            info.put("ChapterName", mChapter.getText().toString());
                            info.put("FoundingDate", mFounding.getText().toString());
                            info.put("ChapterLogoURL", downloadUrl.toString());
                            myRef.setValue(info);
                            Toast.makeText(EditInfoActivity.this, "Info page has been updated", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            Toast.makeText(EditInfoActivity.this, "Updates could not be saved due to internal server error", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    public void pickImage(View view) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
    }

    public void getInfo() {
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        DatabaseReference ref = database.child(globals.DatabaseNode()+"/Info");

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                String master = (String) snapshot.child("ActiveMaster").getValue();
                String url = (String) snapshot.child("ChapterLogoURL").getValue();
                String chapter = (String) snapshot.child("ChapterName").getValue();
                String founding = (String) snapshot.child("FoundingDate").getValue();

                mMaster = (EditText) findViewById(R.id.EditMaster);
                if (master.equals("Empty")) {
                    mMaster.setText("");
                } else {
                    mMaster.setText(master);
                }
                mChapter = (EditText) findViewById(R.id.EditChapterName);
                if (chapter.equals("Empty")) {
                    mChapter.setText("");
                } else {
                    mChapter.setText(chapter);
                }
                mFounding = (EditText) findViewById(R.id.EditFoundingDate);
                if (founding.equals("Empty")) {
                    mFounding.setText("");
                } else {
                    mFounding.setText(founding);
                }

                logoURL = "";
                originalURL = "";
                if (!url.equals("Empty")) {
                    logoURL = url;
                    originalURL = url;
                    Picasso.with(EditInfoActivity.this)
                            .load(url)
                            .resize(mImage.getWidth(), mImage.getHeight())
                            .into(mImage);
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.d(TAG, "Error retrieving users");


                //log error
            }
        });
    }


}
