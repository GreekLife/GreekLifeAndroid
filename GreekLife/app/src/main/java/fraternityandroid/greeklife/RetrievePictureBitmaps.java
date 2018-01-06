package fraternityandroid.greeklife;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by jonahelbaz on 2018-01-05.
 */

public class RetrievePictureBitmaps extends AsyncTask<String, String, Bitmap> {

    private static final String TAG = "RetrievePictureBitmaps";
    Globals globals = Globals.getInstance();

    @Override
    protected Bitmap doInBackground(String... params) {
        if (!globals.getPicturesRetrieved()) {
            ArrayList<Image> images = new ArrayList<>();
            for (User user : globals.getUsers()) {
                try {
                    URL url = new URL(user.Image);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setDoInput(true);
                    connection.connect();
                    InputStream input = connection.getInputStream();
                    Bitmap myBitmap = BitmapFactory.decodeStream(input);
                    images.add(new Image(myBitmap, user.UserID));
                } catch (IOException e) {
                    Log.d(TAG, "Couldn't get users image");
                    return null;
                }
            }
            globals.setPicturesRetrieved(true);
            globals.setProfilePictures(images);
        }
        return null;
    }
}
