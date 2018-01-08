package fraternityandroid.greeklife;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class InfoActivity extends AppCompatActivity {

    private static final String TAG = "InfoActivity";
    Globals globals = Globals.getInstance();
    TextView mMaster, mChapter, mFounding;
    ImageView mImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);
        globals.IsBlocked(InfoActivity.this);

        Button classes = (Button) findViewById(R.id.FoundingFathers);
        classes.setPaintFlags(classes.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

        getInfo();

    }
    public void displayConstitution(View view) {
        Intent constitution = new Intent(InfoActivity.this, ConstitutionViewer.class);
        startActivity(constitution);
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

                mMaster = (TextView) findViewById(R.id.ActiveMaster);
                if (master.equals("Empty")) {
                    mMaster.setText("");
                } else {
                    mMaster.setText(master);
                }
                mChapter = (TextView) findViewById(R.id.ChapterName);
                if (chapter.equals("Empty")) {
                    mChapter.setText("");
                } else {
                    mChapter.setText(chapter);
                }
                mFounding = (TextView) findViewById(R.id.FoundingDate);
                if (founding.equals("Empty")) {
                    mFounding.setText("");
                } else {
                    mFounding.setText(founding);
                }

                mImage = (ImageView) findViewById(R.id.logoImageView);
                if (!url.equals("Empty")) {
                    Picasso.with(InfoActivity.this)
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
