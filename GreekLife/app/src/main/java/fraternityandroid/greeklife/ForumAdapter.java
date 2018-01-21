package fraternityandroid.greeklife;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Collections.swap;

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
        int size = 0; {
            if(mPosts != null) {
                size = mPosts.size();
            }
        }
        return size;
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
        if(mPosts != null) {
            final Globals globals = Globals.getInstance();

            if (convertView == null) {
                final LayoutInflater layoutInflater = LayoutInflater.from(mContext);
                convertView = layoutInflater.inflate(R.layout.forum_cell, null);
            }
            final ImageView profilePicture = (ImageView) convertView.findViewById(R.id.ProfilePicture);
            final TextView name = (TextView) convertView.findViewById(R.id.Name);
            final TextView title = (TextView) convertView.findViewById(R.id.Title);
            final TextView post = (TextView) convertView.findViewById(R.id.Post);
            final TextView date = (TextView) convertView.findViewById(R.id.Date);
            final TextView comments = (TextView) convertView.findViewById(R.id.Comments);
            final TextView delete = (TextView) convertView.findViewById(R.id.Delete);

            Globals.PostOrder order = globals.getPostOrder();
            //Set the order of the posts
            if (order == Globals.PostOrder.NEWEST) {
                for (int i = 0; i < mPosts.size(); i++) {
                    for (int j = i; j > 0; j--) {
                        if (((int) mPosts.get(j).getEpoch()) > ((int) mPosts.get(j - 1).getEpoch()))
                            swap(mPosts, j, j - 1);
                        else
                            break;
                    }
                }
                convertView.setVisibility(View.VISIBLE);
            } else if (order == Globals.PostOrder.OLDEST) {
                for (int i = 0; i < mPosts.size(); i++) {
                    for (int j = i; j > 0; j--) {
                        if (((int) mPosts.get(j).getEpoch()) < ((int) mPosts.get(j - 1).getEpoch()))
                            swap(mPosts, j, j - 1);
                        else
                            break;
                    }
                }
                convertView.setVisibility(View.VISIBLE);
            } else if (order == Globals.PostOrder.WEEK) {
                for (int i = 0; i < mPosts.size(); i++) {
                    for (int j = i; j > 0; j--) {
                        if (((int) mPosts.get(j).getEpoch()) > ((int) mPosts.get(j - 1).getEpoch()))
                            swap(mPosts, j, j - 1);
                        else
                            break;
                    }
                }
                long currentEpoch = System.currentTimeMillis() / 1000L;
                long secondsSince = currentEpoch - (int) mPosts.get(position).getEpoch();
                int hours = (int) (secondsSince / 3600);
                int days = hours / 24;
                if (days > 7) {
                    convertView.setVisibility(View.GONE);
                } else {
                    convertView.setVisibility(View.VISIBLE);
                }

            } else if (order == Globals.PostOrder.MONTH) {
                for (int i = 0; i < mPosts.size(); i++) {
                    for (int j = i; j > 0; j--) {
                        if (((int) mPosts.get(j).getEpoch()) > ((int) mPosts.get(j - 1).getEpoch()))
                            swap(mPosts, j, j - 1);
                        else
                            break;
                    }
                }
                long currentEpoch = System.currentTimeMillis() / 1000L;
                long secondsSince = currentEpoch - (int) mPosts.get(position).getEpoch();
                int hours = (int) (secondsSince / 3600);
                int days = hours / 24;
                if (days > 31) {
                    convertView.setVisibility(View.GONE);
                } else {
                    convertView.setVisibility(View.VISIBLE);
                }
            }

            if (!globals.getDelete()) {
                delete.setVisibility(View.GONE);
            }
            else {
                String loggedInUserId = globals.getLoggedIn().UserID;
                String loggedInPosition = globals.getLoggedIn().Position;

                if (mPosts.get(position).getPosterId().equals(loggedInUserId) || loggedInPosition.equals("Master")) {
                    delete.setVisibility(View.VISIBLE);
                }
            }
            final Button gotit = (Button) convertView.findViewById(R.id.GotIt);

            if(mPosts.get(position).getGotIt().contains(globals.getLoggedIn().UserID)) {
                gotit.setTextColor(Color.parseColor("#FFDF00"));
            }
            else {
                gotit.setTextColor(Color.parseColor("#7b1e7ce6"));
            }

                ((Button) convertView.findViewById(R.id.GotIt)).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(mPosts.get(position).getGotIt().contains(globals.getLoggedIn().UserID)) {
                            String[] array;
                            if(mPosts.get(position).getGotIt().size() > 0) {
                                array = new String[(mPosts.get(position).getGotIt().size())];

                                List<String> names = new ArrayList<>();
                                for(String id : mPosts.get(position).getGotIt()) {
                                    names.add(globals.userFirstLastNameByID(id));
                                }

                                array = (names.toArray(array));
                            }
                            else {
                                array = new String[1];
                                array[0] = "No one has seen this post yet";
                            }

                            gotit.setTextColor(Color.parseColor("#FFDF00"));
                            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                            final AlertDialog alert = builder.create();
                            builder.setTitle("Got It!")
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
                        else {
                            FirebaseDatabase database = FirebaseDatabase.getInstance();
                            DatabaseReference myRef = database.getReference(globals.DatabaseNode() + "/Forum/" + mPosts.get(position).getPostId() + "/GotIt");
                            final Object userNameObj = globals.getLoggedIn().Username;
                            Map<String, Object> map = new HashMap<String, Object>() {{
                                put(globals.getLoggedIn().UserID, userNameObj);
                            }};
                            myRef.updateChildren(map);

                            mPosts.get(position).getGotIt().add(globals.getLoggedIn().UserID);

                            gotit.setTextColor(Color.parseColor("#FFDF00"));
                        }
                    }
                });

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

                                    if(mPosts.get(position).getPosterId().equals(globals.getLoggedIn().UserID)) {
                                        FirebaseDatabase database = FirebaseDatabase.getInstance();
                                        DatabaseReference myRef = database.getReference(globals.DatabaseNode() + "/Forum/" + mPosts.get(position).getPostId());

                                        myRef.removeValue();
                                        globals.setDelete(false);
                                    }
                                    else {
                                        globals.setDelete(false);
                                    }
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

            //set variables.

            name.setText(mPosts.get(position).getPoster());
            title.setText(mPosts.get(position).getPostTitle());
            post.setText(mPosts.get(position).getPost());

            long currentEpoch = System.currentTimeMillis() / 1000L;
            long secondsSince = currentEpoch - (int) mPosts.get(position).getEpoch();
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

            date.setText(display); //format epoch

            String numOfCom = Long.toString(mPosts.get(position).getNumberOfComments());
            comments.setText(numOfCom + " Comments");

            String id = mPosts.get(position).getPosterId();
            String url = globals.getImageUrl(id);

            try {
                Glide.with(mContext)
                        .load(globals.getImageForId(id).image)
                        .apply(RequestOptions.circleCropTransform())
                        .into(profilePicture);
            }
            catch (NullPointerException e) {
                Glide.with(mContext)
                        .load(url)
                        .apply(RequestOptions.circleCropTransform())
                        .into(profilePicture);
            }

            comments.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent comment = new Intent(mContext, ForumCommentActivity.class);
                    comment.putExtra("PostId", mPosts.get(position).getPostId());
                    mContext.startActivity(comment);
                }
            });

        }
        return convertView;
    }
}
