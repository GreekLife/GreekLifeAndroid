package fraternityandroid.greeklife;

/**
 * Created by jonahelbaz on 2017-12-24.
 */

public class Comment {

    private String commenter;
    private double commentDate;
    private String comment;
    private String commentId;
    private String commenterId;

    public Comment(String commenter, double commentDate, String comment, String commentId, String commenterId) {
        this.commenter = commenter;
        this.commentDate = commentDate;
        this.comment = comment;
        this.commentId = commentId;
        this.commenterId = commenterId;
    }

    public void setCommenter(String name) { this.commenter = name;}
    public void setCommentDate(double epoch) { this.commentDate = epoch;}
    public void setComment(String comment) { this.comment = comment;}
    public void setCommentId(String id){ this.commentId = id;}
    public void setCommenterId(String id) {this.commenterId = id;}

    public String getCommenter() { return commenter;}
    public double getCommentDate() {return commentDate;}
    public String getComment() { return comment;}
    public String getCommentId() {return commentId;}
    public String getCommenterId() {return commenterId;}
}
