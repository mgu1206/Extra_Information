package yu_cse.graduation_project_edit.list_views;

import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by gyeunguckmin on 11/2/15.
 */
public class FriendList_View {

    private String friendId;
    private String friendName;
    private String state;

    public int approval;
    private int posApproval;
    private int isRequest;

    public TextView waitMsg;
    public ImageButton btnGetPos, btnApproval;
    public ImageView friendStateIcon;

    public FriendList_View(String id, String name, int mApproval, int mPosApproval, int mIsRequest, String state)
    {
        friendId = id;
        friendName = name;
        approval = mApproval;
        posApproval = mPosApproval;
        isRequest = mIsRequest;
        this.state = state;
    }

    public String getFriendId(){
        return friendId;
    }

    public String getFriendName()
    {
        return friendName;
    }

    public int getApproval()
    {
        return approval;
    }
    public int getIsRequest()
    {
        return isRequest;
    }
    public int getPosApproval()
    {
        return posApproval;
    }

    public String getState()
    {
        return state;
    }
}
