package yu_cse.graduation_project_edit.util;

import android.app.ProgressDialog;
import android.content.Context;

import yu_cse.graduation_project_edit.activities.ShowMyFriend;

/**
 * Created by gyeunguckmin on 10/15/15.
 */
public class ShowProgressDialog {
    private ProgressDialog loagindDialog;
    private Context context;

    public ShowProgressDialog(Context context)
    {
        this.context = context;
    }

    public void showDialog(String title, String msg)
    {
        loagindDialog = ProgressDialog.show(context, title, msg);
    }

    public void removeDialog()
    {
        loagindDialog.dismiss();
    }
}
