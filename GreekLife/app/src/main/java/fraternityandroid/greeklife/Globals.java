package fraternityandroid.greeklife;

import com.squareup.picasso.Picasso;

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



    public static synchronized Globals getInstance(){
        if(instance == null){
            instance = new Globals();
        }
        return instance;
    }

}
