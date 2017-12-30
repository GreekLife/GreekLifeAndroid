package fraternityandroid.greeklife;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by jonahelbaz on 2017-12-28.
 */

public class Poll {

    private String title;
    private String postId;
    private String posterId;
    private String poster;
    private double epoch;
    ArrayList<Options> options;

    public Poll() {
        this.title = "";
        this.poster = "";
        this.posterId = "";
        this.postId = "";
        this.epoch = 0.01;
        this.options = new ArrayList<Options>();
    }

    public Poll(String title, double epoch, String postId, String posterId, String poster, ArrayList<Options> options) {
        this.title = title;
        this.poster = poster;
        this.posterId = posterId;
        this.postId = postId;
        this.options = options;
        this.epoch = epoch;
    }

    public String getTitle() {return title;}
    public String getPostId() {return postId;}
    public String getPosterId() {return posterId;}
    public String getPoster() {return poster;}
    public double getEpoch() {return epoch;}
    public ArrayList<Options> getOptions() {return options;}

    public void addOption(Options value) {
        options.add(value);
    }
}
