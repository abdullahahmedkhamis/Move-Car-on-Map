package commerce.amazoncommerce.traning.ReAndLog;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.app.INotificationSideChannel;

import androidx.core.app.NotificationCompat;

import commerce.amazoncommerce.traning.R;
import commerce.amazoncommerce.traning.ReAndLog.Services.MyFirebaseMessagingService;

public class Common {
    public static final String DRIVER_INFO_REFERENCE = "DriverInfo";
    public static final String DRIVER_LOCATION_REFERENCE = "DriversLocation";
    public static final String TOKEN_REFRANCE = "Token";
    public static final String NOTI_TITLE = "title";
    public static final String NOTI_CONTENT = "body";
    private static final String NOTIFCATION_CHANNEL_ID = "Notifcation";


    public static DriverInfoModel currentUser;

    public static String buildWelcomeMessage() {
       if(Common.currentUser !=null)
       {
           return new StringBuilder("Welcome")
                   .append( Common.currentUser.getFirstname() )
                   .append( "" )
                   .append( Common.currentUser.getLastname()).toString();
       }
       else
           return "";
    }


    public static void showNotification(Context context, int id, String title, String body, Intent intent){
        PendingIntent pendingIntent =null;
        if(intent != null)
            pendingIntent = PendingIntent.getActivity( context,id,intent,PendingIntent.FLAG_UPDATE_CURRENT );
        String NOTIFICATION = "edmt_dev_uber_remake";
        NotificationManager notificationManager = (NotificationManager)context.getSystemService( Context.NOTIFICATION_SERVICE );
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            NotificationChannel notificationChannel = new NotificationChannel( NOTIFCATION_CHANNEL_ID,
                    "Uber Remake", NotificationManager.IMPORTANCE_HIGH);
            notificationChannel.setDescription("Uber Remake");
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor( Color.RED );
            notificationChannel.setVibrationPattern(new long[]{0,1000,500,1000});
            notificationChannel.enableVibration(true);


            notificationManager.createNotificationChannel( notificationChannel );

        }
        NotificationCompat.Builder builder = new NotificationCompat.Builder( context,NOTIFCATION_CHANNEL_ID );
        builder.setContentTitle( title )
                .setContentText( body )
                .setAutoCancel( false )
                .setPriority( NotificationCompat.PRIORITY_HIGH )
                .setDefaults( Notification.DEFAULT_VIBRATE )
                .setSmallIcon( R.drawable.ic_baseline_directions_car_24 )
                .setLargeIcon( BitmapFactory.decodeResource( context.getResources(),R.drawable.ic_baseline_directions_car_24 ) );

        if(pendingIntent !=null)
        {
            builder.setContentIntent( pendingIntent );
        }
        Notification notification = builder.build();
        notificationManager.notify(id,notification);
    }
}
