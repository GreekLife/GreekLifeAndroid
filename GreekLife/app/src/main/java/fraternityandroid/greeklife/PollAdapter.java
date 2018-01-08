package fraternityandroid.greeklife;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

/**
 * Created by jonahelbaz on 2017-12-28.
 */

public class PollAdapter extends BaseExpandableListAdapter {

    private Context context;
    private ArrayList<Poll> mPolls;

    public PollAdapter(Context context, ArrayList<Poll> polls) {
        this.context = context;
        this.mPolls = polls;
    }

    @Override
    public Object getChild(int groupPosition, int childPosititon) {
        return mPolls.get(groupPosition).getOptions();
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return 0;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        try {
            return mPolls.get(groupPosition).getOptions().size();
        }
        catch(IndexOutOfBoundsException e) {
            return 0;
        }
    }


    @Override
    public View getChildView(int groupPosition, final int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {

        Globals globals = Globals.getInstance();

        if (convertView == null) {
            final LayoutInflater layoutInflater = LayoutInflater.from(context);
            convertView = layoutInflater.inflate(R.layout.poll_minor_cell, null);
        }
        TextView percent = (TextView) convertView.findViewById(R.id.InnerPercent);
        TextView votes = (TextView) convertView.findViewById(R.id.InnerVote);
        TextView option = (TextView) convertView.findViewById(R.id.InnerOption);
        ArrayList<Options> wOptions = mPolls.get(groupPosition).getOptions();
        Options thisOption = mPolls.get(groupPosition).getOptions().get(childPosition);

        int totalVotes = 0;
        for(Options op: wOptions) {
            totalVotes = totalVotes + op.getVotes();
        }
        int optionVotes = thisOption.getVotes();
        double percentage;
        if(totalVotes != 0) {
            percentage = ((double) optionVotes / totalVotes) * 100;
        }
        else {
            percentage = 0;
        }

        String percentString =  ((int)percentage) + "%";
        globals.setPollOptionIndexPercent(mPolls.get(groupPosition).getPostId(), childPosition, percentString);

        option.setText(thisOption.getOption());
        percent.setText(thisOption.getPercent());
        votes.setText(Integer.toString(thisOption.getVotes()));


        return convertView;
    }


    @Override
    public Object getGroup(int groupPosition) {
        return mPolls.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return mPolls.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return 0;
    }

    @Override
    public View getGroupView(final int position, boolean isLastChild, View convertView,
                             ViewGroup parent) {
        if (mPolls != null) {
            final Globals globals = Globals.getInstance();

            if (convertView == null) {
                final LayoutInflater layoutInflater = LayoutInflater.from(context);
                convertView = layoutInflater.inflate(R.layout.poll_greater_cell, null);
            }

            final ImageView profilePicture = (ImageView) convertView.findViewById(R.id.ProfilePicture);
            final TextView name = (TextView) convertView.findViewById(R.id.Name);
            final TextView title = (TextView) convertView.findViewById(R.id.Title);
            final TextView date = (TextView) convertView.findViewById(R.id.Date);
            final Button vote = (Button) convertView.findViewById(R.id.Vote);
            final Button delete = (Button) convertView.findViewById(R.id.DeletePoll);

            name.setText(mPolls.get(position).getPoster());
            title.setText(mPolls.get(position).getTitle());

            if(globals.getDeletePolls()) {
                if(globals.getLoggedIn().UserID.equals(mPolls.get(position).getPosterId()) || globals.getLoggedIn().Position.equals("Master")){
                    delete.setVisibility(View.VISIBLE);
                }
                else{
                    delete.setVisibility(View.INVISIBLE);
                }
            }
            else {
                delete.setVisibility(View.INVISIBLE);
            }



            long currentEpoch = System.currentTimeMillis() / 1000L;
            long secondsSince = currentEpoch - (int) mPolls.get(position).getEpoch();
            int hours = (int) (secondsSince / 3600);
            int min = (int) (hours / 60);
            int days = hours / 24;
            String display;
            if (days < 1) {
                display = Integer.toString(hours) + "h";
            } else if (hours > 0) {
                display = Integer.toString(days) + "d";
            } else {
                display = Integer.toString(min) + "m";
            }

            date.setText(display);

            String id = mPolls.get(position).getPosterId();
            String url = globals.getImageUrl(id);

            try {
                Glide.with(context)
                        .load(globals.getImageForId(mPolls.get(position).getPosterId()).image)
                        .apply(RequestOptions.circleCropTransform())
                        .into(profilePicture);
            }
            catch (NullPointerException e) {
                Glide.with(context)
                        .load(url)
                        .apply(RequestOptions.circleCropTransform())
                        .into(profilePicture);
            }



            final int wPosition = position;
            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle("Delete")
                            .setMessage("Would you like to delete this post?")
                            .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {

                                        FirebaseDatabase database = FirebaseDatabase.getInstance();
                                        DatabaseReference myRef = database.getReference(globals.DatabaseNode() + "/Polls/" + mPolls.get(wPosition).getPostId());
                                        myRef.removeValue();
                                        DatabaseReference nextRef = database.getReference(globals.DatabaseNode() + "/PollOptions/" + mPolls.get(wPosition).getPostId());
                                        nextRef.removeValue();

                                        globals.setDelete(false);

                                }
                            })
                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    globals.setDelete(false);
                                }
                            })
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();

                }
            });

            vote.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent vote = new Intent(context, VotingActivity.class);
                    globals.setSelectedPoll(mPolls.get(position));
                    context.startActivity(vote);
                }
            });

        }

        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

}
