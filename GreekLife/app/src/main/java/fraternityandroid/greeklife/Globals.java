package fraternityandroid.greeklife;

import android.app.AlertDialog;
import android.content.Context;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import static com.google.android.gms.wearable.DataMap.TAG;

/**
 * Created by jonahelbaz on 2017-12-20.
 */

public class Globals {

    private static Globals instance;

    enum PostOrder {
        NEWEST, OLDEST, WEEK, MONTH
    }

    enum PollOrder {
        NEWEST, OLDEST, WEEK, MONTH
    }

    private String DatabaseNode = "GammaLambda";

    private User LoggedIn;
    private List<User> Users;
    private HashMap<String,User> UsersByID;
    private List<Forum> mPosts;
    private Boolean mDeletePosts = false;
    private Boolean mDeletePolls = false;
    private PostOrder value = PostOrder.NEWEST;
    private PollOrder PollValue = PollOrder.NEWEST;
    private Poll mSelectedPoll;

    private ArrayList<Image> mProfilePictures;
    private Boolean mPicturesRetrieved = false;

    private ArrayList<Poll> mPolls;
    private ArrayList<Poll> temporaryPolls;

    private Context mContext;


    private Globals() {}

    public String DatabaseNode() {return DatabaseNode;}
    public void setDatabaseNode(String node) {this.DatabaseNode = node;}

    public void setLoggedIn(User member) {
        this.LoggedIn = member;
    }
    public User getLoggedIn() {return LoggedIn; }

    public void setUsers(List<User> users){ this.Users = users;}
    public List<User> getUsers(){ return Users;}
    public void setUsersByID(HashMap<String,User> usersByID){this.UsersByID = usersByID;}
    public HashMap<String,User> getUsersByID(){return UsersByID;}
    public User getUserByID(String id) {return UsersByID.get(id);}

    public String userFirstLastNameByID(String id){
        return getUserByID(id).First_Name + " " + getUserByID(id).Last_Name;
    }

    public void setPosts(List<Forum> posts){ mPosts = posts;}
    public List<Forum> getPosts(){return mPosts;}
    public Forum getPostById(String id) {
        Forum postForIndex = null;
        for(Forum post: mPosts) {
            if(post.getPostId().equals(id)) {
                postForIndex = post;
            }
        }
        return postForIndex;
    }

    public String getImageUrl(String id){
        String image = "";
        for (User mem : Users) {
            if (mem.UserID.equals(id)) {
                image = mem.Image;
            }
        }
        return image;
    }

    public void setDelete(Boolean deletePosts){ mDeletePosts = deletePosts;}
    public Boolean getDelete(){return mDeletePosts;}

    public void setPostOrder(PostOrder val){this.value = val;}
    public PostOrder getPostOrder(){return value;}

    public void setPolls(ArrayList<Poll> polls){ mPolls = polls;}
    public ArrayList<Poll> getPolls(){return mPolls;}
    public Poll getPollById(String id) {
        Poll postForIndex = null;
        for(Poll post: mPolls) {
            if(post.getPostId().equals(id)) {
                postForIndex = post;
            }
        }
        return postForIndex;
    }


    public void setPollValue(PollOrder val){this.PollValue = val;}
    public PollOrder getPollValue(){return PollValue;}
    public void setDeletePolls(Boolean deletePosts){ mDeletePolls = deletePosts;}
    public Boolean getDeletePolls(){return mDeletePolls;}

    public void setPollOptionIndexVotes(String id, int optionIndex, int votes) {
        Poll poll = getPollById(id);
        poll.getOptions().get(optionIndex - 1).setVotes(votes);
    }

    public void setPollOptionIndexPercent(String id, int optionIndex, String percent) {
        Poll poll = getPollById(id);
        poll.getOptions().get(optionIndex).setPercent(percent);
    }

    public void setTemporaryPolls(ArrayList<Poll> polls){this.temporaryPolls = polls;}
    public ArrayList<Poll> getTemporaryPolls(){return this.temporaryPolls;}


    public static synchronized Globals getInstance(){
        if(instance == null){
            instance = new Globals();
        }
        return instance;
    }

    public void setSelectedPoll(Poll poll) { mSelectedPoll = poll;}
    public Poll getSelectedPoll(){ return mSelectedPoll;}

    public ArrayList<Image> getProfilePictures() {
        return mProfilePictures;
    }
    public void setProfilePictures(ArrayList<Image> mProfilePictures) {
        this.mProfilePictures = mProfilePictures;
    }
    public Image getImageForId(String id) {
        Image image = null;
        for(Image pic: mProfilePictures) {
            if(pic.id.equals(id)) {
                image = pic;
            }
        }
        return image;
    }
    public void setPicturesRetrieved(Boolean answer) {
        this.mPicturesRetrieved = answer;
    }
    public Boolean getPicturesRetrieved(){return this.mPicturesRetrieved;}

    public void IsBlocked(final Context context) {

        Boolean connected = false;

        try {
            InetAddress ipAddr = InetAddress.getByName("google.com"); //You can replace it with your name
            if (!ipAddr.equals("")) {
                connected = true;
            }

        } catch (Exception e) {
            connected = false;
        }

        if (connected == false) {
            //Toast.makeText(mContext, "You are not connected to the internet", Toast.LENGTH_SHORT).show();
        } else {

            mContext = context;
            String id = getLoggedIn().UserID;

            DatabaseReference database = FirebaseDatabase.getInstance().getReference();
            final DatabaseReference ref = database.child("Blocked/" + id);
            ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    final Context wContext = mContext;
                    if (((Boolean) snapshot.child("Blocked").getValue())) {
                        long delay = (long) snapshot.child("Delay").getValue();

                        final AlertDialog.Builder dialog = new AlertDialog.Builder(wContext)
                                .setTitle("Get Wrecked").setMessage(
                                        "Your Master has temporarily disabled your access. It will return in " + delay + " minutes").setCancelable(false);
                        final AlertDialog alert = dialog.create();
                        alert.show();
                        long time = delay * 60;
                        Timer timer = new Timer();
                        timer.schedule(new TimerTask() {
                            @Override
                            public void run() {
                                if (alert.isShowing()) {
                                    alert.dismiss();
                                }
                                Map<String, Object> ban = new HashMap<String, Object>();
                                ban.put("Blocked", false);
                                ban.put("Delay", 0);
                                ref.setValue(ban);
                            }
                        }, time * 1000);
                    }
                }

                @Override
                public void onCancelled(DatabaseError error) {
                    Log.d(TAG, "Error reading block");
                }
            });
        }
    }

}
