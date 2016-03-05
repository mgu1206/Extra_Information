package yu_cse.graduation_project_edit.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import yu_cse.graduation_project_edit.activities.MainActivity;

/**
 * Created by gyeunguckmin on 11/18/15.
 */
public class NotificationClass extends Activity{
    Intent intent;

    public NotificationClass(Context context) {
        intent=new Intent(context, MainActivity.class);
    }

    public void startMainActivity()
    {
        startActivity(intent);
    }
}

