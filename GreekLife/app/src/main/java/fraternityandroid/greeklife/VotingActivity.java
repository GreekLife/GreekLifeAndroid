package fraternityandroid.greeklife;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class VotingActivity extends AppCompatActivity {

    private static final String TAG = "VotingActivity";
    ArrayList<Options> mOptions;
    ArrayList<Map<String, Object>> mElements = new ArrayList<>();
    Globals globals = Globals.getInstance();
    Poll mPoll;
    Boolean mDrawn = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voting);

        globals.IsBlocked(this);

        mPoll = globals.getSelectedPoll();
        mOptions = mPoll.getOptions();

        getStats();

    }

    public void getStats() {
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        DatabaseReference ref = database.child("PollOptions/"+mPoll.getPostId());

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {

                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                    if (!((String) userSnapshot.getKey()).equals("\"0\"")) {

                        String optionNumber = (String) userSnapshot.getKey();
                        optionNumber = optionNumber.replaceAll("\"", "");
                        int numberValue = Integer.parseInt(optionNumber);
                        Map<String, Object> names = (Map<String, Object>) userSnapshot.getValue();
                        List<Object> ids = new ArrayList<>(names.values());
                        ArrayList<String> allNames = new ArrayList<>();
                        for(Object id: ids) {
                            Map<String, Object> name = (Map<String, Object>) id;
                            for(Object n: name.values()) {
                                String aName = (String) n;
                                allNames.add(aName);
                            }
                        }

                        mOptions.get(numberValue - 1).setVotersArray(allNames);
                        globals.getPollById(mPoll.getPostId()).getOptions().get(numberValue - 1).setVotersArray(allNames);
                    }
                }
                if(!mDrawn) {
                    CreateLayout();
                    mDrawn = true;
                }
            }
            @Override
            public void onCancelled(DatabaseError error) {
                Log.d(TAG, "Error loading polls");
                findViewById(R.id.loadingPanel).setVisibility(View.GONE);

            }

        });
    }

    public void updateLayouts() {
        int count = 0;
        for(Options op: mOptions) {

            int totalVotes = 0;
            for(Options opt: mOptions) {
                totalVotes = totalVotes + opt.getVotes();
            }
            int optionVotes = op.getVotes();
            double percentage;
            if(totalVotes != 0) {
                percentage = ((double) optionVotes / totalVotes) * 100;
            }
            else {
                percentage = 0;
            }

            String percentString =  ((int)percentage) + "%";
            globals.setPollOptionIndexPercent(mPoll.getPostId(), count, percentString);
            ((TextView)mElements.get(count).get("Percent")).setText(percentString);


            String name = globals.getLoggedIn().First_Name+ " " + globals.getLoggedIn().Last_Name;
            ShapeDrawable shapedrawable = new ShapeDrawable();
            shapedrawable.setShape(new RectShape());
            shapedrawable.getPaint().setColor(Color.parseColor("#268cc5"));
            shapedrawable.getPaint().setStrokeWidth(10f);
            shapedrawable.getPaint().setStyle(Paint.Style.STROKE);
            ((Button)mElements.get(count).get("Option")).setBackground(shapedrawable);
                for (String person : op.getVoters()) {
                    if (name.equals(person)) {
                        ((Button) mElements.get(count).get("Option")).setBackgroundColor(Color.parseColor("#FFDF00"));
                    }
                }
            count++;
        }

    }

    public void CreateLayout() {
        TextView pollTitle = (TextView) findViewById(R.id.pollTitle);

        String title = mPoll.getTitle();

        pollTitle.setText(title);

        LinearLayout myRoot = (LinearLayout) findViewById(R.id.OptionLayout);

        int count = 0;
        for(final Options op: mOptions) {
            LinearLayout layout = new LinearLayout(this);
            layout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            layout.setOrientation(LinearLayout.HORIZONTAL);
            layout.setBackgroundColor(Color.parseColor("#268cc5"));

            Map<String, Object> element = new HashMap<>();
            final Button option = new Button(this);
            Button vote = new Button(this);
            TextView percent = new TextView(this);

            LinearLayout.LayoutParams paramsOp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.WRAP_CONTENT, 0.2f); // Width , height
            option.setLayoutParams(paramsOp);
            option.setText(op.getOption());
            option.setTextColor(Color.parseColor("#ffffff"));
            option.setTransformationMethod(null);
            option.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
            option.setTag(count);
            option.setPadding(30,20,20,20);

            ShapeDrawable shapedrawable = new ShapeDrawable();
            shapedrawable.setShape(new RectShape());
            shapedrawable.getPaint().setColor(Color.parseColor("#268cc5"));
            final String name = globals.getLoggedIn().First_Name + " " + globals.getLoggedIn().Last_Name;

            shapedrawable.getPaint().setStrokeWidth(10f);
            shapedrawable.getPaint().setStyle(Paint.Style.STROKE);
            option.setBackground(shapedrawable);
                for (String person : op.getVoters()) {
                    if (name.equals(person)) {
                        option.setBackgroundColor(Color.parseColor("#FFDF00"));
                    }
                }

            element.put("Option", option);
            layout.addView(option);

            LinearLayout.LayoutParams paramsPercent = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.WRAP_CONTENT, 0.7f);
            percent.setLayoutParams(paramsPercent);
            percent.setPadding(3,3,3,3);
            percent.setTextColor(Color.parseColor("#ffffff"));
            percent.setGravity(Gravity.CENTER);
            percent.setText(op.getPercent());
            element.put("Percent", percent);
            layout.addView(percent);

            LinearLayout.LayoutParams paramsVotes = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ActionBar.LayoutParams.WRAP_CONTENT, 0.1f);
            vote.setLayoutParams(paramsVotes);
            vote.setTextSize(5);
            vote.setGravity(Gravity.CENTER);
            vote.setText("Votes");

                vote.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String[] array;
                        if(op.getVoters().size() > 0) {
                            array = new String[op.getVoters().size()];
                            array = op.getVoters().toArray(array);
                        }
                        else {
                            array = new String[1];
                            array[0] = "No one has voted for this option yet";
                        }
                        AlertDialog.Builder builder = new AlertDialog.Builder(VotingActivity.this);
                        final AlertDialog alert = builder.create();
                        builder.setTitle("Voters")
                                .setItems(array, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {

                                    }
                                })
                                .setNegativeButton("Close", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        alert.dismiss();
                                    }
                                })
                                .setIcon(android.R.drawable.btn_star)
                                .show();
                    }
                });

            option.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                        int val = (int) option.getTag();
                        final int tag = val;
                        final String value = Integer.toString(++val);

                    final DatabaseReference databaseCheck = FirebaseDatabase.getInstance().getReference();
                    DatabaseReference ref = databaseCheck.child("PollOptions/" + mPoll.getPostId() + "/\"" + value + "\"/Names/");

                    ref.addListenerForSingleValueEvent(new ValueEventListener() {
                           @Override
                         public void onDataChange(DataSnapshot snapshot) {
                               DatabaseReference database = FirebaseDatabase.getInstance().getReference();
                               String usersName = globals.getLoggedIn().First_Name + " " + globals.getLoggedIn().Last_Name;

                               if (snapshot.child(globals.getLoggedIn().UserID).exists()) {

                                   DatabaseReference delete = database.child("PollOptions/" + mPoll.getPostId() + "/\"" + value + "\"/Names/" + globals.getLoggedIn().UserID);
                                   delete.removeValue();
                                   int hasVoted = 0;
                                   for(Options opt: mOptions) {
                                             for (String person : opt.getVoters()) {
                                                 if (usersName.equals(person)) {
                                                       hasVoted++;
                                                 }
                                             }

                                     }
                                    if(hasVoted == 1) {
                                        DatabaseReference refTotal = database.child("PollOptions/" + mPoll.getPostId() + "/\"0\"/Names/" + globals.getLoggedIn().UserID);
                                        refTotal.removeValue();
                                    }
                                    mOptions.get(tag).getVoters().remove(usersName);
                                    mOptions.get(tag).setVotes(mOptions.get(tag).getVotes() - 1);

                                   int totalVotes = 0;
                                   for(Options opt: mOptions) {
                                       totalVotes = totalVotes + opt.getVotes();
                                   }
                                   for(int i = 0; i<mOptions.size(); i++) {
                                       int optionVotes = mOptions.get(i).getVotes();
                                       double percentage;
                                       if (totalVotes != 0) {
                                           percentage = ((double) optionVotes / totalVotes) * 100;
                                       } else {
                                           percentage = 0;
                                       }

                                       String percentString = ((int) percentage) + "%";
                                       globals.setPollOptionIndexPercent(mPoll.getPostId(), i, percentString);
                                       ((TextView)mElements.get(i).get("Percent")).setText(percentString);
                                   }
                                   ShapeDrawable shapedrawable = new ShapeDrawable();
                                   shapedrawable.setShape(new RectShape());
                                   shapedrawable.getPaint().setColor(Color.parseColor("#141A6E"));
                                   shapedrawable.getPaint().setStrokeWidth(10f);
                                   shapedrawable.getPaint().setStyle(Paint.Style.STROKE);
                                   ((Button)mElements.get(tag).get("Option")).setBackground(shapedrawable);

                               } else {
                                   DatabaseReference ref = database.child("PollOptions/" + mPoll.getPostId() + "/\"" + value + "\"/Names/" + globals.getLoggedIn().UserID);
                                   ref.setValue(usersName);

                                   DatabaseReference refTotal = database.child("PollOptions/" + mPoll.getPostId() + "/\"0\"/Names/" + globals.getLoggedIn().UserID);
                                   refTotal.setValue(globals.getLoggedIn().UserID);

                                   mOptions.get(tag).getVoters().add(usersName);
                                   mOptions.get(tag).setVotes(mOptions.get(tag).getVotes() + 1);

                                   int totalVotes = 0;
                                   for(Options opt: mOptions) {
                                       totalVotes = totalVotes + opt.getVotes();
                                   }
                                   for(int i = 0; i<mOptions.size(); i++) {
                                       int optionVotes = mOptions.get(i).getVotes();
                                       double percentage;
                                       if (totalVotes != 0) {
                                           percentage = ((double) optionVotes / totalVotes) * 100;
                                       } else {
                                           percentage = 0;
                                       }

                                       String percentString = ((int) percentage) + "%";
                                       globals.setPollOptionIndexPercent(mPoll.getPostId(), i, percentString);
                                       ((TextView)mElements.get(i).get("Percent")).setText(percentString);
                                   }
                                   ((Button) mElements.get(tag).get("Option")).setBackgroundColor(Color.parseColor("#FFDF00"));
                                }
                           }
                        @Override
                        public void onCancelled(DatabaseError error) {
                            Log.d(TAG, "Error loading polls");
                            findViewById(R.id.loadingPanel).setVisibility(View.GONE);

                        }

                    });

                    }

            });

            element.put("Vote", vote);
            layout.addView(vote);

            mElements.add(element);
            myRoot.addView(layout);
            count++;
        }
    }

}
