package fraternityandroid.greeklife;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class BETAActivity extends AppCompatActivity {

    EditText email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beta);

        email = (EditText) findViewById(R.id.Email);

        email.setMaxLines(10);
    }

    public void SendEmail(View view) {

        String[] to = {"Fraternity.ios.dev@gmail.com"};
        String[] cc = {"Jonah-elbaz@hotmail.com", "Jonzlotnik@live.ca"};
        Intent emailIntent = new Intent(Intent.ACTION_SEND);

        emailIntent.setData(Uri.parse("mailto:"));
        emailIntent.setType("text/plain");
        emailIntent.putExtra(Intent.EXTRA_EMAIL, to);
        emailIntent.putExtra(Intent.EXTRA_CC, cc);
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "BETA feedback");
        emailIntent.putExtra(Intent.EXTRA_TEXT, email.getText().toString());

        try {
            startActivity(Intent.createChooser(emailIntent, "Send email..."));
            finish();
        }
        catch(android.content.ActivityNotFoundException ex) {
            Toast.makeText(BETAActivity.this, "The email could not be sent. Please try again or contact the developers directly through your master.", Toast.LENGTH_LONG).show();
        }
    }
}
