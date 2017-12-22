package fraternityandroid.greeklife;

import java.util.List;

/**
 * Created by jonahelbaz on 2017-12-20.
 */

public class Globals {

    private static Globals instance;

    private User LoggedIn;
    private List<User> Users;


    private Globals() {}

    public void setLoggedIn(User member) {
        this.LoggedIn = member;
    }
    public User getLoggedIn() {return LoggedIn; }

    public void setUsers(List<User> users){ this.Users = users;}
    public List<User> getUsers(){ return Users;}



    public static synchronized Globals getInstance(){
        if(instance == null){
            instance = new Globals();
        }
        return instance;
    }

}
