package fraternityandroid.greeklife;

/**
 * Created by jonahelbaz on 2017-12-28.
 */

public class Options {

    private String option;
    private int votes;
    private String percent;

    public Options(String option) {
        this.option = option;
        this.votes = 0;
        this.percent = "0%";
    }

    public String getOption(){return this.option;}
    public int getVotes() {return this.votes;}
    public String getPercent() {return this.percent;}

    public void setVotes(int votes) {this.votes = votes;}
    public void setPercent(String percent){ this.percent = percent;}
}
