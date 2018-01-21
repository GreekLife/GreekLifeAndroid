package fraternityandroid.greeklife;

import java.util.List;

/**
 * Created by jonahelbaz on 2017-12-23.
 */

public class Forum implements Comparable<Forum>{

    private long numberOfComments;
    private double epoch;
    private String post;
    private String postId;
    private String postTitle;
    private String poster;
    private String posterId;
    private List<Comment> comments;
    private List<String> gotIt;

    public Forum(long size, double epoch, String post, String postId, String postTitle, String poster, String posterId, List<Comment> comments, List<String> gotIt) {
        this.numberOfComments = size;
        this.epoch = epoch;
        this.post = post;
        this.postId = postId;
        this.postTitle = postTitle;
        this.poster = poster;
        this.posterId = posterId;
        this.comments = comments;
        this.gotIt = gotIt;
    }

    public long getNumberOfComments(){return numberOfComments;}
    public double getEpoch(){return epoch;}
    public String getPost(){return post;}
    public String getPostId(){return postId;}
    public String getPostTitle(){return postTitle;}
    public String getPoster(){return poster;}
    public String getPosterId(){return posterId;}
    public List<Comment> getComments(){return comments;}
    public List<String> getGotIt(){return this.gotIt;}

    public void setNumberOfComments(long numberOfComments){this.numberOfComments = numberOfComments;}
    public void setEpoch(double epoch){this.epoch = epoch;}
    public void setPost(String post){this.post = post;}
    public void setPostId(String postId){this.postId = postId;}
    public void setPostTitle(String postTitle){this.postTitle = postTitle;}
    public void setPoster(String poster){this.poster = poster;}
    public void setPosterId(String posterId){this.posterId = posterId;}
    public void setComments(List<Comment> comments){ this.comments = comments;}
    public void setGotIt(List<String> gotit){this.gotIt = gotit;}

    @Override
    public int compareTo(Forum b){
        if(this.epoch > b.epoch)
            return 1;
        if(this.epoch < b.epoch)
            return -1;
        else
            return 0;
    }
}
