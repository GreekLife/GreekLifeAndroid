package fraternityandroid.greeklife;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Map;

import static java.util.Collections.swap;

public class PollActivity extends AppCompatActivity {

    ExpandableListView mList;
    PollAdapter pollAdapter;
    Button newest, oldest, week, month;


    private static final String TAG = "PollActivity";

    Globals globals = Globals.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_poll);

        newest = (Button)findViewById(R.id.Newest);
        oldest = (Button)findViewById(R.id.Oldest);
        week = (Button)findViewById(R.id.Week);
        month = (Button)findViewById(R.id.Month);
        globals.IsBlocked(this);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.HomeToolbar);
        TextView header = new TextView(this);
        header.setText("Polls");
        header.setTypeface(Typeface.create("monospace", Typeface.NORMAL));
        header.setTextColor(Color.parseColor("#FFDF00"));

        //image button for back <-

        myToolbar.addView(header);

        newest.setBackgroundColor(Color.parseColor("#222222"));

        findViewById(R.id.loadingPanel).setVisibility(View.VISIBLE);
        getPolls();
    }

    public void getPolls() {
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        DatabaseReference ref = database.child(globals.DatabaseNode()+"/Polls");

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                ArrayList<Poll> polls = new ArrayList<>();
                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                    if (!((String) userSnapshot.getKey()).equals("PollIds")) {
                        String title = (String) userSnapshot.child("Title").getValue();
                        String poster = (String) userSnapshot.child("Poster").getValue();
                        final String posterId = (String) userSnapshot.child("PosterId").getValue();
                        String postId = (String) userSnapshot.child("PostId").getValue();
                        double epoch = (double) userSnapshot.child("Epoch").getValue();
                        ArrayList<String> options = (ArrayList<String>) userSnapshot.child("Options").getValue();
                        ArrayList<Options> optionList = new ArrayList<>();
                        for(String op: options) {
                            Options newOption = new Options(op);
                            optionList.add(newOption);
                        }

                        Poll poll = new Poll(title, epoch, postId, posterId, poster, optionList);
                        polls.add(poll);
                        for (int i = 0; i < polls.size(); i++) {
                            for (int j = i; j > 0; j--) {
                                if (((int) polls.get(j).getEpoch()) > ((int) polls.get(j - 1).getEpoch())) {
                                    swap(polls, j, j - 1);
                                }
                                else
                                    break;
                            }
                        }
                        globals.setPolls(polls);

                        final String id = postId;
                        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
                        DatabaseReference ref = database.child(globals.DatabaseNode()+"/PollOptions/"+id);

                        ref.addValueEventListener(new ValueEventListener() {
                            @Override
                          public void onDataChange(DataSnapshot snapshot) {
                                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                                    if (!((String) userSnapshot.getKey()).equals("\"0\"")) {
                                        String optionNumber = (String) userSnapshot.getKey();
                                        optionNumber = optionNumber.replaceAll("\"", "");
                                        int numberValue = Integer.parseInt(optionNumber);
                                        Map<String, Object> op = (Map<String, Object>) userSnapshot.child("Names").getValue();
                                        int votes = op.size();
                                        globals.setPollOptionIndexVotes(id, numberValue, votes);
                                    }
                                }


                                pollAdapter = new PollAdapter(PollActivity.this, globals.getPolls());

                                mList = (ExpandableListView) findViewById(R.id.PollList);

                                mList.setAdapter(pollAdapter);

                                for(int i = 0; i < globals.getPolls().size(); i++) {
                                    mList.expandGroup(i, false);
                                }

                                pollAdapter.notifyDataSetChanged();
                                findViewById(R.id.loadingPanel).setVisibility(View.GONE);

                            }
                            @Override
                            public void onCancelled(DatabaseError error) {
                                Log.d(TAG, "Error loading polls");
                                findViewById(R.id.loadingPanel).setVisibility(View.GONE);

                            }

                        });
                    }
                }

            }
            @Override
            public void onCancelled(DatabaseError error) {
                Log.d(TAG, "Error loading polls");
                findViewById(R.id.loadingPanel).setVisibility(View.GONE);

            }

        });
    }

    public void deleting(View view) {
        if(globals.getDeletePolls()) {
            globals.setDeletePolls(false);
            pollAdapter.notifyDataSetChanged();
        }
        else {
            globals.setDeletePolls(true);
            pollAdapter.notifyDataSetChanged();
        }
    }

    public void create(View view) {
        globals.setDeletePolls(false);
        Intent createPoll = new Intent(PollActivity.this, CreatePollActivity.class);
        startActivity(createPoll);
    }


    public void newest(View view) {
        globals.setPollValue(Globals.PollOrder.NEWEST);
        newest.setBackgroundColor(Color.parseColor("#222222"));
        oldest.setBackgroundColor(Color.parseColor("#00FFFFFF"));
        oldest.setBackground(ContextCompat.getDrawable(this, R.drawable.thick_border));
        month.setBackgroundColor(Color.parseColor("#00FFFFFF"));
        month.setBackground(ContextCompat.getDrawable(this, R.drawable.thick_border));
        week.setBackgroundColor(Color.parseColor("#00FFFFFF"));
        week.setBackground(ContextCompat.getDrawable(this, R.drawable.thick_border));

                 for (int i = 0; i < globals.getPolls().size(); i++) {
                    for (int j = i; j > 0; j--) {
                        if (((int) globals.getPolls().get(j).getEpoch()) > ((int) globals.getPolls().get(j - 1).getEpoch())) {
                            swap(globals.getPolls(), j, j - 1);
                        }
                        else
                            break;
                    }
                }

        pollAdapter = new PollAdapter(PollActivity.this, globals.getPolls());

        mList = (ExpandableListView) findViewById(R.id.PollList);

        mList.setAdapter(pollAdapter);

        for(int i = 0; i < globals.getPolls().size(); i++) {
            mList.expandGroup(i, false);
        }

        pollAdapter.notifyDataSetChanged();
    }
    public void oldest(View view) {
        globals.setPollValue(Globals.PollOrder.OLDEST);
        newest.setBackgroundColor(Color.parseColor("#00FFFFFF"));
        newest.setBackground(ContextCompat.getDrawable(this, R.drawable.thick_border));
        oldest.setBackgroundColor(Color.parseColor("#222222"));
        week.setBackgroundColor(Color.parseColor("#00FFFFFF"));
        week.setBackground(ContextCompat.getDrawable(this, R.drawable.thick_border));
        month.setBackgroundColor(Color.parseColor("#00FFFFFF"));
        month.setBackground(ContextCompat.getDrawable(this, R.drawable.thick_border));

        for (int i = 0; i < globals.getPolls().size(); i++) {
            for (int j = i; j > 0; j--) {
                if (((int) globals.getPolls().get(j).getEpoch()) < ((int) globals.getPolls().get(j - 1).getEpoch())) {
                    swap(globals.getPolls(), j, j - 1);
                }
                else
                    break;
            }
        }

        pollAdapter = new PollAdapter(PollActivity.this, globals.getPolls());

        mList = (ExpandableListView) findViewById(R.id.PollList);

        mList.setAdapter(pollAdapter);

        for(int i = 0; i < globals.getPolls().size(); i++) {
            mList.expandGroup(i, false);
        }

        pollAdapter.notifyDataSetChanged();

    }

    public void week(View view) {
        globals.setPollValue(Globals.PollOrder.WEEK);
        newest.setBackgroundColor(Color.parseColor("#00FFFFFF"));
        newest.setBackground(ContextCompat.getDrawable(this, R.drawable.thick_border));
        oldest.setBackgroundColor(Color.parseColor("#00FFFFFF"));
        oldest.setBackground(ContextCompat.getDrawable(this, R.drawable.thick_border));

        week.setBackgroundColor(Color.parseColor("#222222"));

        month.setBackgroundColor(Color.parseColor("#00FFFFFF"));
        month.setBackground(ContextCompat.getDrawable(this, R.drawable.thick_border));

        for (int i = 0; i < globals.getPolls().size(); i++) {
            for (int j = i; j > 0; j--) {
                if (((int) globals.getPolls().get(j).getEpoch()) > ((int) globals.getPolls().get(j - 1).getEpoch())) {
                    swap(globals.getPolls(), j, j - 1);
                }
                else
                    break;
            }
        }

        ArrayList<Poll> tempList = new ArrayList<>();
        for (int i = 0; i < globals.getPolls().size(); i++) {
            long currentEpochW = System.currentTimeMillis() / 1000L;
            long secondsSinceW = currentEpochW - (int) globals.getPolls().get(i).getEpoch();
            int hoursW = (int) (secondsSinceW / 3600);
            int daysW = hoursW / 24;
            if (!(daysW > 7)) {
                tempList.add(globals.getPolls().get(i));
            }
        }

            globals.setTemporaryPolls(tempList);

            pollAdapter = new PollAdapter(PollActivity.this, globals.getTemporaryPolls());

            mList.setAdapter(pollAdapter);

            for (int i = 0; i < globals.getPolls().size(); i++) {
                mList.expandGroup(i, false);
            }
            pollAdapter.notifyDataSetChanged();
    }

    public void month(View view) {
        globals.setPollValue(Globals.PollOrder.MONTH);
        newest.setBackgroundColor(Color.parseColor("#00FFFFFF"));
        newest.setBackground(ContextCompat.getDrawable(this, R.drawable.thick_border));
        oldest.setBackgroundColor(Color.parseColor("#00FFFFFF"));
        oldest.setBackground(ContextCompat.getDrawable(this, R.drawable.thick_border));
        week.setBackgroundColor(Color.parseColor("#00FFFFFF"));
        week.setBackground(ContextCompat.getDrawable(this, R.drawable.thick_border));
        month.setBackgroundColor(Color.parseColor("#222222"));

        for (int i = 0; i < globals.getPolls().size(); i++) {
            for (int j = i; j > 0; j--) {
                if (((int) globals.getPolls().get(j).getEpoch()) > ((int) globals.getPolls().get(j - 1).getEpoch())) {
                    swap(globals.getPolls(), j, j - 1);
                }
                else
                    break;
            }
        }

        ArrayList<Poll> tempList = new ArrayList<>();
        for (int i = 0; i < globals.getPolls().size(); i++) {
            long currentEpochW = System.currentTimeMillis() / 1000L;
            long secondsSinceW = currentEpochW - (int) globals.getPolls().get(i).getEpoch();
            int hoursW = (int) (secondsSinceW / 3600);
            int daysW = hoursW / 24;
            if (!(daysW > 31)) {
                tempList.add(globals.getPolls().get(i));
            }
        }

            globals.setTemporaryPolls(tempList);

            pollAdapter = new PollAdapter(PollActivity.this, globals.getTemporaryPolls());

            mList.setAdapter(pollAdapter);

            for (int i = 0; i < globals.getTemporaryPolls().size(); i++) {
                mList.expandGroup(i, false);
            }
            pollAdapter.notifyDataSetChanged();

    }
}
