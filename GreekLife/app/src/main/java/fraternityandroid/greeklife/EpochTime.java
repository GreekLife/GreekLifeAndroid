package fraternityandroid.greeklife;

import java.util.Calendar;

/**
 * Created by Jon Zlotnik on 2018-01-12.
 */

public class EpochTime {

    private long timeInMillis;
    private long timeInSeconds;

    public EpochTime (long time) {
        if(time < Calendar.getInstance().getTimeInMillis()/1000){
            timeInSeconds = time;
            timeInMillis = timeInSeconds*1000;
        }else {
            timeInMillis = time;
            timeInSeconds = timeInMillis/1000;
        }
    }

    public long getMillis()
    {
        return timeInMillis;
    }
    public long getSeconds()
    {
        return timeInSeconds;
    }
    public long forDB()
    {
        return timeInSeconds;
    }
}
