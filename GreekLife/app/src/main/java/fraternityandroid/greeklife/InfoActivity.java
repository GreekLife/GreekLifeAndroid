package fraternityandroid.greeklife;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Paint;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;

public class InfoActivity extends AppCompatActivity {

    /*
        Whats left:
        Need to write the pdf from assets to the sd card so it can be read from there.
     */

    Button mConstitution;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        Button foundingfathers = (Button) findViewById(R.id.FoundingFathers);
        foundingfathers.setPaintFlags(foundingfathers.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

    }
    public void displayConstitution(View view) {
        Intent constitution = new Intent(InfoActivity.this, ConstitutionViewer.class);
        startActivity(constitution);
    }

}
