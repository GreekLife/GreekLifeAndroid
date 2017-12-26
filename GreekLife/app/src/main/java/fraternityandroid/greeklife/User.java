package fraternityandroid.greeklife;

/**
 * Created by jonahelbaz on 2017-12-18.
 */

public class User {

    String Username;
    String UserID;
    String Birthday;
    String BrotherName;
    String Degree;
    String Email;
    String First_Name;
    String Last_Name;
    String GraduationDate;
    String Image;
    String Position;
    String School;
    Boolean Validated;

    public User() {
        this.Username = "";
        this.UserID = "";
        this.Birthday = "";
        this.BrotherName = "";
        this.Degree = "";
        this.Email = "";
        this.First_Name = "";
        this.Last_Name = "";
        this.GraduationDate = "";
        this.Image = "";
        this.Position = "";
        this.School = "";
        this.Validated = false;
    }
    public User(String Username, String userID, String Birthday, String BrotherName, String Degree, String Email, String First_Name, String Last_Name, String GraduationDate
    , String Image, String School, String Position, Boolean Validated) {
        this.Username = Username;
        this.UserID = userID;
        this.Birthday = Birthday;
        this.BrotherName = BrotherName;
        this.Degree = Degree;
        this.Email = Email;
        this.First_Name = First_Name;
        this.Last_Name = Last_Name;
        this.GraduationDate = GraduationDate;
        this.Image = Image;
        this.Position = Position;
        this.School = School;
        this.Validated = Validated;
    }


}
