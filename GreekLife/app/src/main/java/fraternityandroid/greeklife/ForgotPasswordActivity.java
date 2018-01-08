package fraternityandroid.greeklife;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPasswordActivity extends AppCompatActivity {

    EditText mEmail;
    private static final String TAG = "ForgotPasswordActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        mEmail = findViewById(R.id.resetEmail);
    }

    public void resetPassword(View view) {
        if (!mEmail.getText().toString().equals("")){
            FirebaseAuth.getInstance().sendPasswordResetEmail(mEmail.getText().toString())
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Log.d(TAG, "Email sent.");
                                Toast.makeText(ForgotPasswordActivity.this, "Email sent (check junk mail)",
                                        Toast.LENGTH_SHORT).show();
                                mEmail.setText("");
                            } else {
                                Log.d(TAG, "Email failed.");
                                Toast.makeText(ForgotPasswordActivity.this, "Could not send password reset email",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }
}
