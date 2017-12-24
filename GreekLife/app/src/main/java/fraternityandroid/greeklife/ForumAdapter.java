package fraternityandroid.greeklife;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by jonahelbaz on 2017-12-23.
 */

public class ForumAdapter extends BaseAdapter {


    private final Context mContext;
    private List<Forum> mPosts = new ArrayList<>();

    // 1
    public ForumAdapter(Context context, List<Forum> post) {
        this.mContext = context;
        this.mPosts = post;
    }

    // 2
    @Override
    public int getCount() {
        return mPosts.size();
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

        Globals globals = Globals.getInstance();

        if (convertView == null) {
            final LayoutInflater layoutInflater = LayoutInflater.from(mContext);
            convertView = layoutInflater.inflate(R.layout.forum_cell, null);
        }
        final ImageView profilePicture = (ImageView)convertView.findViewById(R.id.ProfilePicture);
        final TextView name = (TextView)convertView.findViewById(R.id.Name);
        final TextView title = (TextView)convertView.findViewById(R.id.Title);
        final TextView post = (TextView)convertView.findViewById(R.id.Post);
        final TextView date = (TextView)convertView.findViewById(R.id.Date);
        final TextView comments = (TextView)convertView.findViewById(R.id.Comments);
        final TextView delete = (TextView)convertView.findViewById(R.id.Delete);

        Globals.PostOrder order = globals.getPostOrder();
        //Set the order of the posts
        if(order == Globals.PostOrder.NEWEST) {
                //order polls
        }
        else if(order == Globals.PostOrder.OLDEST) {
            //order polls

        }
        else if(order == Globals.PostOrder.WEEK) {
            //order polls

        }
        else if(order == Globals.PostOrder.MONTH) {
            //order polls

        }

        if(!globals.getDelete()) {
            delete.setVisibility(View.GONE);
        }
        else {
            String loggedInUser = globals.getLoggedIn().UserID;
            String loggedInUsername = globals.getLoggedIn().Username;

            if(mPosts.get(position).getPosterId().equals(loggedInUser) || loggedInUsername.equals("Master")) {
                delete.setVisibility(View.VISIBLE);
            }
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
                        .setMessage("Would you like to delete this post?")
                        .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                FirebaseDatabase database = FirebaseDatabase.getInstance();
                                DatabaseReference myRef = database.getReference("Forum/"+mPosts.get(position).getPostId());

                                myRef.removeValue();
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

        //set variables.

        name.setText(mPosts.get(position).getPoster());
        title.setText(mPosts.get(position).getPostTitle());
        post.setText(mPosts.get(position).getPost());

        long epoch = (long) mPosts.get(position).getEpoch();


        DateFormat sdf = new SimpleDateFormat("MMM d, yyyy");

        date.setText(sdf.format(epoch)); //format epoch

        String numOfCom = Long.toString(mPosts.get(position).getNumberOfComments());
        comments.setText(numOfCom + " Comments");

        String id = mPosts.get(position).getPosterId();
        List<User> users = globals.getUsers();
        for(User mem: users) {
            if(mem.UserID.equals(id)) {
                Picasso.with(mContext).load(users.get(position).Image).into(profilePicture);
            }
        }

        return convertView;
    }
}
