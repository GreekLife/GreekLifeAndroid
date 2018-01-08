package fraternityandroid.greeklife;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

import static java.util.Collections.swap;

/**
 * Created by jonahelbaz on 2017-12-24.
 */

public class CommentAdapter extends BaseAdapter {


    private final Context mContext;
    private List<fraternityandroid.greeklife.Comment> mComments;
    private Forum mPost;

    // 1
    public CommentAdapter(Context context, List<fraternityandroid.greeklife.Comment> comments, Forum post) {
        this.mContext = context;
        this.mComments = comments;
        this.mPost = post;
    }

    // 2
    @Override
    public int getCount() {
        return mComments.size();
    }

    // 3
    @Override
    public long getItemId(int position) {
        return 0;
    }

    // 4
    @Override
    public Object getItem(int position) {
        return null;
    }

    // 5
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        final Globals globals = Globals.getInstance();

        if (convertView == null) {
            final LayoutInflater layoutInflater = LayoutInflater.from(mContext);
            convertView = layoutInflater.inflate(R.layout.comment_cell, null);
        }

        for (int i = 0; i < mComments.size(); i++) {
            for (int j = i; j > 0; j--) {
                if(((int)mComments.get(j).getCommentDate()) < ((int)mComments.get(j - 1).getCommentDate()))
                    swap(mComments, j, j-1);
                else
                    break;
            }
        }

        final TextView comment = (TextView)convertView.findViewById(R.id.Comment);
        final TextView date = (TextView)convertView.findViewById(R.id.CommentDate);
        final TextView name = (TextView)convertView.findViewById(R.id.CommenterName);
        final TextView delete = (TextView)convertView.findViewById(R.id.DeleteComment);

        final fraternityandroid.greeklife.Comment com = mComments.get(position);

        comment.setText(com.getComment());
        name.setText(com.getCommenter());

        long currentEpoch =  System.currentTimeMillis() / 1000L;
        long secondsSince = currentEpoch - (int)com.getCommentDate();
        int hours = (int) (secondsSince / 3600);
        int min = (int) (hours / 60);
        int days = hours/24;
        String display;
        if(days < 1) {
            display = Integer.toString(hours) + "h";
        }
        else if(hours > 0){
            display = Integer.toString(days) + "d";
        }
        else {
            display = Integer.toString(min) + "m";
        }

        date.setText(display);
        delete.setVisibility(View.GONE);

        if(com.getCommenterId().equals(globals.getLoggedIn().UserID) || globals.getLoggedIn().Username.equals("Master")) {
            delete.setVisibility(View.VISIBLE);
        }

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    builder = new AlertDialog.Builder(mContext, android.R.style.Theme_Material_Dialog_Alert);
                } else {
                    builder = new AlertDialog.Builder(mContext);
                }
                builder.setTitle("Delete")
                        .setMessage("Would you like to delete your comment?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // continue with delete

                                FirebaseDatabase database = FirebaseDatabase.getInstance();
                                DatabaseReference myRef = database.getReference(globals.DatabaseNode()+"/Forum/"+mPost.getPostId()+"/Comments/"+com.getCommentId());
                                myRef.removeValue();
                                //test for success
                                Toast.makeText(mContext, "Comment removed", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            }
        });

        if(globals.getLoggedIn().UserID.equals(mComments.get(position).getCommenterId())) {
            name.setTextColor(Color.parseColor("#4894EC"));
        }
        else {
            name.setTextColor(Color.BLACK);
        }

        //Set the order of the posts
        return convertView;
    }
}
