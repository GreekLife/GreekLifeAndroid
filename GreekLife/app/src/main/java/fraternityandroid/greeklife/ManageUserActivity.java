package fraternityandroid.greeklife;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ManageUserActivity extends AppCompatActivity {

    ListView mListView;
    List<User> mUsers;

    /*
    TODO: implement what happens if the type is veryify on click - go to user profile (not yet defined).
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_user);

        mListView = (ListView) findViewById(R.id.List);

        final Globals globals = Globals.getInstance();
        mUsers = globals.getUsers();
        List<String> names = new ArrayList<String>();
        final List<String> ids = new ArrayList<>();
        Bundle bundle = getIntent().getExtras();
        final String type = bundle.getString("List");
        if(type.equals("KICK") || type.equals("BAN")) {
            for (User user : mUsers) {
                names.add(user.First_Name + " " + user.Last_Name);
            }
        }
        if(type.equals("VERIFY")) {
            for (User user : mUsers) {
                if(user.Validated == false) {
                    names.add(user.First_Name + " " + user.Last_Name);
                    ids.add(user.UserID);
                }
            }
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,  android.R.layout.simple_list_item_1, android.R.id.text1, names);

        mListView.setAdapter(adapter);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                User user = mUsers.get(position);
                final String userId = user.UserID;

                if(type.equals("KICK")) {
                    // ListView Clicked item value
                    AlertDialog.Builder builder;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        builder = new AlertDialog.Builder(ManageUserActivity.this, android.R.style.Theme_Material_Dialog_Alert);
                    } else {
                        builder = new AlertDialog.Builder(ManageUserActivity.this);
                    }
                    final AlertDialog alert = builder.create();
                    builder.setTitle("Kick")
                            .setMessage("Would you like to permanently delete this users account? This cannot be undone.")
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                                    DatabaseReference myRef = database.getReference(globals.DatabaseNode()+"/Users/"+userId);
                                    myRef.removeValue();
                                    alert.dismiss();
                                }
                            })
                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    alert.dismiss();
                                }
                            })
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();
                }
                if(type.equals("BAN")) {
                    AlertDialog.Builder builder;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        builder = new AlertDialog.Builder(ManageUserActivity.this, android.R.style.Theme_Material_Dialog_Alert);
                    } else {
                        builder = new AlertDialog.Builder(ManageUserActivity.this);
                    }
                    String[] delays = {"5 minutes", "30 minutes", "2 hours", "1 day"};
                    final AlertDialog alert = builder.create();
                    builder.setTitle("Ban")
                            .setTitle("How long would you like to ban this user?")
                            .setItems(delays, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                                    DatabaseReference myRef = database.getReference(globals.DatabaseNode()+"/Blocked/"+userId);
                                    Map<String, Object> ban = new HashMap<String, Object>();
                                    long delay = 0;
                                    switch (which) {
                                        case 0:
                                            delay = 5;
                                            break;
                                        case 1:
                                            delay = 30;
                                            break;
                                        case 2:
                                            delay = 120;
                                            break;
                                        case 3:
                                            delay = 1440;
                                            break;
                                    }
                                    ban.put("Blocked", true);
                                    ban.put("Delay", delay);
                                    myRef.setValue(ban);
                                    alert.dismiss();
                                 }
                            })
                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                 @Override
                                 public void onClick(DialogInterface dialog, int which) {
                                     alert.dismiss();
                                }
                    })
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();

                }

                if(type.equals("VERIFY")) {
                    Intent member = new Intent(ManageUserActivity.this, ViewProfileActivity.class);
                    member.putExtra("id", ids.get(position));
                    startActivity(member);

                }



                }

        });

    }
}
