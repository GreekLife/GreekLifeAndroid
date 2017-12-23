package fraternityandroid.greeklife;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Build;
import android.os.CountDownTimer;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

import static com.google.android.gms.wearable.DataMap.TAG;

/**
 * Created by jonahelbaz on 2017-12-22.
 */
    /*
    TODO: Timer not functional + Disable user interactions
    */


public class TemporaryBan {


    public static void IsBlocked(final Context context) {
        Globals globals = Globals.getInstance();
        String id = globals.getLoggedIn().UserID;
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        final  DatabaseReference ref = database.child("Blocked/"+id);

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {

                if (((Boolean)snapshot.child("Blocked").getValue())) {
                    long delay = (long) snapshot.child("Delay").getValue();
                    AlertDialog.Builder builder;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        builder = new AlertDialog.Builder(context, android.R.style.Theme_Material_Dialog_Alert);
                    } else {
                        builder = new AlertDialog.Builder(context);
                    }
                    final AlertDialog alert = builder.create();
                    builder.setTitle("Get Wrecked")
                            .setMessage("Your Master has temporarily disabled your access. It will return in " + delay + " minutes")
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();
                    long time = delay * 60;
                    new CountDownTimer(5000, time) {

                        @Override
                        public void onTick(long millisUntilFinished) {
                        }

                        @Override
                        public void onFinish() {
                            alert.dismiss();
                            Map<String, Object> ban = new HashMap<String, Object>();
                            ban.put("Blocked", false);
                            ban.put("Delay", 0);
                            ref.setValue(ban);
                        }
                    }.start();
                    Log.d(TAG, "Users retrieved");

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
