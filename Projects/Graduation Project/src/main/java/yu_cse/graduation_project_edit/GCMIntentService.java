package yu_cse.graduation_project_edit;


import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.AudioManager;
import android.media.SoundPool;
import android.net.Uri;
import android.os.Vibrator;
import android.util.Log;

import com.google.android.gcm.GCMBaseIntentService;

import java.util.List;

import yu_cse.graduation_project_edit.activities.LoginMainActivity;
import yu_cse.graduation_project_edit.dialog.NotificationDialog;
import yu_cse.graduation_project_edit.util.NotificationClass;

/**
 * Created by gyeunguckmin on 10/9/15.
 */
public class GCMIntentService extends GCMBaseIntentService {
    static String re_message=null;

    private void initSound()
    {

    }



    private static void generateNotification(Context context, String message, String title, String appName) {
        int icon = R.drawable.friend_req;
        //int icon;
        long when = System.currentTimeMillis();




        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notification = new Notification(icon, message, when);
        //notification.sound = Uri.parse("android.resource://"+context.getPackageName()+"/"+R.raw.alarm);
        //notification.sound.
        notification.defaults |= Notification.DEFAULT_SOUND;
        re_message=message;


        ActivityManager am = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> runList = am.getRunningTasks(10);
        ComponentName name = runList.get(0).topActivity;
        String className = name.getClassName();
        boolean isAppRunning = false;
        //notification.defaults |= Notification.DEFAULT_SOUND;
        //NotificationManager nm = (NotificationManager) context.getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        //nm.notify((int)System.currentTimeMillis(), notification);
        //notification.sound = Uri.parse("android.resource://"+context.getPackageName()+"/"+context.getPackageName()+"/"+R.raw.alarm);

        if(className.contains("yu_cse.graduation_project_edit"))
        {
            isAppRunning = true;
        }
        if(isAppRunning)
        {
            Intent intent = new Intent(context, NotificationDialog.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("title", title);
            intent.putExtra("message", message);
            context.startActivity(intent);
            //notification.flags |= Notification.FLAG_AUTO_CANCEL;
            //notificationManager.notify((int) System.currentTimeMillis(), notification);
        }
        else
        {
            title = appName + " " + title;
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,
                    new Intent(context, LoginMainActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK), 0);

            notification.setLatestEventInfo(context, title, message, pendingIntent);
            notification.flags |= Notification.FLAG_AUTO_CANCEL;
            notificationManager.notify((int)System.currentTimeMillis(), notification);
        }





    }

    @Override
    protected void onError(Context arg0, String arg1) {

    }

    @Override
    protected void onMessage(Context context, Intent intent) {
        Vibrator buttonVibe = (Vibrator)getSystemService(context.VIBRATOR_SERVICE);
        String title = intent.getStringExtra("title");
        String appName = intent.getStringExtra("appname");
        String msg = intent.getStringExtra("msg");
        Log.e("getmessage", "getmessage:" + msg);
        //SoundPool soundPool = new SoundPool(5, AudioManager.STREAM_MUSIC, 0);
        //int sound_beep = soundPool.load(context, R.raw.alarm, 1);
        //soundPool.play(sound_beep, 1f, 1f, 0, 0, 1f);
        buttonVibe.vibrate(1000);

            generateNotification(context, msg, title,appName);
    }

    @Override
    protected void onRegistered(Context context, String reg_id) {
        Log.e("키를 등록합니다.(GCM INTENTSERVICE)", reg_id);
    }

    @Override
    protected void onUnregistered(Context arg0, String arg1) {
        Log.e("키를 제거합니다.(GCM INTENTSERVICE)", "제거되었습니다.");
    }
}