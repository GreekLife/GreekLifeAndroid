package fraternityandroid.greeklife;

import java.util.ArrayList;

/**
 * Created by jonahelbaz on 2017-12-28.
 */

public class Options {

    private String option;
    private int votes;
    private String percent;
    private ArrayList<String> voters;

    public Options(String option) {
        this.option = option;
        this.votes = 0;
        this.percent = "0%";
        voters = new ArrayList<>();
    }

    public String getOption(){return this.option;}
    public int getVotes() {return this.votes;}
    public String getPercent() {return this.percent;}
    public ArrayList<String> getVoters() {return voters;}

    public void setVotes(int votes) {this.votes = votes;}
    public void setPercent(String percent){ this.percent = percent;}
    public void setVoters(String voter){
        voters.add(voter);
    }
    public void setVotersArray(ArrayList<String> voters){this.voters = voters;}

}
