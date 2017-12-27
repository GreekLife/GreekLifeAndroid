package fraternityandroid.greeklife;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Paint;
import android.icu.text.IDNA;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;

public class InfoActivity extends AppCompatActivity {

    Globals globals = Globals.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);
        globals.IsBlocked(InfoActivity.this);

        Button classes = (Button) findViewById(R.id.FoundingFathers);
        classes.setPaintFlags(classes.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

    }
    public void displayConstitution(View view) {
        Intent constitution = new Intent(InfoActivity.this, ConstitutionViewer.class);
        startActivity(constitution);
    }

}
