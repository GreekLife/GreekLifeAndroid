package fraternityandroid.greeklife;

import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import static android.content.ContentValues.TAG;
import static fraternityandroid.greeklife.MessengerActivity.globals;

/**
 * Created by jonahelbaz on 2017-12-26.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // ...

        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.d(TAG, "From: " + remoteMessage.getFrom());

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());

            String payloadData = remoteMessage.getData().toString();
            String substr = payloadData.substring(0, 3);

            if(substr.equals("CDM")) {
                String channelId = payloadData.substring(3, payloadData.length());
                Globals.getInstance().addChannelNotification(channelId);

            }
            else if(substr.equals("DDM")) {
                String senderId = payloadData.substring(3, payloadData.length());
                Globals.getInstance().addDirectNotification(senderId);
            }

            if (/* Check if data needs to be processed by long running job */ true) {
                // For long-running tasks (10 seconds or more) use Firebase Job Dispatcher.
            } else {
                // Handle message within 10 seconds
            }

        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());

        }

        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
    }
}
