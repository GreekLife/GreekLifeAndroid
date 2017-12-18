package fraternityandroid.greeklife;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    EditText mEmail = (EditText) findViewById(R.id.Email);
    EditText mPassword = (EditText) findViewById(R.id.Password);

    public void Login(View view) {


        
        Intent goToHomePage = new Intent(this, HomePage.class);
        startActivity(goToHomePage);
    }
}
