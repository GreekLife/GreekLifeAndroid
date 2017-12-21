package fraternityandroid.greeklife;

/**
 * Created by jonahelbaz on 2017-12-20.
 */

public class Globals {

    private static Globals instance;

    private User LoggedIn;

    private Boolean masterPost;

    private Globals() {}

    public void setLoggedIn(User member) {
        this.LoggedIn = member;
    }
    public User getLoggedIn() {return LoggedIn; }

    public void setMasterPost(Boolean val){ this.masterPost = val; }
    public Boolean getMasterPost(){ return masterPost; }



    public static synchronized Globals getInstance(){
        if(instance == null){
            instance = new Globals();
        }
        return instance;
    }

}
