package fraternityandroid.greeklife;

import java.util.List;

/**
 * Created by jonahelbaz on 2017-12-20.
 */

public class Globals {

    private static Globals instance;

    enum PostOrder {
        NEWEST, OLDEST, WEEK, MONTH
    }

    private User LoggedIn;
    private List<User> Users;
    private List<Forum> mPosts;
    private Boolean mDeletePosts = false;
    private PostOrder value = PostOrder.NEWEST;


    private Globals() {}

    public void setLoggedIn(User member) {
        this.LoggedIn = member;
    }
    public User getLoggedIn() {return LoggedIn; }

    public void setUsers(List<User> users){ this.Users = users;}
    public List<User> getUsers(){ return Users;}

    public void setPosts(List<Forum> posts){ mPosts = posts;}
    public List<Forum> getPosts(){return mPosts;}

    public void setDelete(Boolean deletePosts){ mDeletePosts = deletePosts;}
    public Boolean getDelete(){return mDeletePosts;}

    public void setPostOrder(PostOrder val){this.value = val;}
    public PostOrder getPostOrder(){return value;}



    public static synchronized Globals getInstance(){
        if(instance == null){
            instance = new Globals();
        }
        return instance;
    }

}
