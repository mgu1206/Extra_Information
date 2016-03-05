package yu_cse.graduation_project_edit.list_views;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.BitmapDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import yu_cse.graduation_project_edit.R;
import yu_cse.graduation_project_edit.dialog.MapViewDialog;
import yu_cse.graduation_project_edit.util.PhpExcute;

/**
 * Created by gyeunguckmin on 11/2/15.
 */
public class FriendListView_Adapter extends BaseAdapter implements View.OnClickListener{

    // Activity에서 가져온 객체정보를 저장할 변수
    private FriendList_View mUser;
    private Context mContext;
    //private Context myContext;

    private PhpExcute phpExcute;

    // ListView 내부 View들을 가르킬 변수들

    private TextView tvUserName;
    private TextView tvUserId;

    private Intent intent;

    private MapViewDialog mapViewDialog;

    private String session;

    // 리스트 아이템 데이터를 저장할 배열
    private ArrayList<FriendList_View> mUserData;

    public FriendListView_Adapter(Context context, String session) {
        super();
        this.session = session;
        mContext = context;
        //myContext = ShowMyFriend.this;
        mUserData = new ArrayList<FriendList_View>();

    }

    @Override
    /**
     * @return 아이템의 총 개수를 반환
     */
    public int getCount() {
        // TODO Auto-generated method stub
        return mUserData.size();
    }

    @Override
    /**
     * @return 선택된 아이템을 반환
     */
    public FriendList_View getItem(int position) {
        // TODO Auto-generated method stub
        return mUserData.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return 0;
    }





    @Override
    /**
     * getView
     *
     * @param position - 현재 몇 번째로 아이템이 추가되고 있는지 정보를 갖고 있다.
     * @param convertView - 현재 사용되고 있는 어떤 레이아웃을 가지고 있는지 정보를 갖고 있다.
     * @param parent - 현재 뷰의 부모를 지칭하지만 특별히 사용되지는 않는다.
     * @return 리스트 아이템이 저장된 convertView
     */
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        final int pos = position;

        CustomViewHolder holder;
        View v = null;




        // 리스트 아이템이 새로 추가될 경우에는 v가 null값이다.
        // view는 어느 정도 생성된 뒤에는 재사용이 일어나기 때문에 효율을 위해서 해준다.
        if (v == null) {
            holder = new CustomViewHolder();
            // inflater를 이용하여 사용할 레이아웃을 가져옵니다.
            v = ((LayoutInflater) mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                    .inflate(R.layout.friend_list_view, null);
            v.setTag(position);

            // 레이아웃이 메모리에 올라왔기 때문에 이를 이용하여 포함된 뷰들을 참조할 수 있습니다.
            //imgUserIcon = (ImageView) v.findViewById(R.id.user_icon);
            tvUserName = (TextView) v.findViewById(R.id.user_name);
            tvUserId = (TextView) v.findViewById(R.id.user_phone_number);



        }

        // 받아온 position 값을 이용하여 배열에서 아이템을 가져온다.
        mUser = getItem(position);

        mUser.btnGetPos = (ImageButton) v.findViewById(R.id.btn_req_pos);
        mUser.btnGetPos.setFocusable(false);
        mUser.btnApproval = (ImageButton) v.findViewById(R.id.btn_app);
        mUser.btnApproval.setFocusable(false);
        mUser.waitMsg = (TextView) v.findViewById(R.id.textView_Wait);

        mUser.friendStateIcon = (ImageView)v.findViewById(R.id.stateIcon);

        // Tag를 이용하여 데이터와 뷰를 묶습니다.
        mUser.btnGetPos.setTag(mUser);
        mUser.btnApproval.setTag(mUser);

        // 데이터의 실존 여부를 판별합니다.
        if (mUser != null) {
            // 데이터가 있다면 갖고 있는 정보를 뷰에 알맞게 배치시킵니다.

            tvUserName.setText(mUser.getFriendName());
            tvUserId.setText(mUser.getFriendId());
            BitmapDrawable img;
            if(mUser.getState().equals("on"))
                img = (BitmapDrawable)v.getResources().getDrawable(R.drawable.online_icon);
            else
                img = (BitmapDrawable)v.getResources().getDrawable(R.drawable.offline_icon);


            mUser.friendStateIcon.setImageDrawable(img);
            mUser.friendStateIcon.setVisibility(View.VISIBLE);

            if(mUser.getApproval()==0 && mUser.getIsRequest()==0)
            {
                mUser.btnGetPos.setVisibility(View.INVISIBLE);
                mUser.btnApproval.setVisibility(View.INVISIBLE);
                mUser.waitMsg.setVisibility(View.VISIBLE);
            }
            else if(mUser.getApproval()==0 && mUser.getIsRequest()==1)
            {
                mUser.btnGetPos.setVisibility(View.INVISIBLE);
                mUser.btnApproval.setVisibility(View.VISIBLE);
                mUser.btnApproval.setOnClickListener(this);
                mUser.waitMsg.setVisibility(View.INVISIBLE);
            }
            else if(mUser.getApproval()==1)
            {
                mUser.waitMsg.setVisibility(View.INVISIBLE);
                mUser.btnApproval.setVisibility(View.INVISIBLE);

                if(mUser.getPosApproval()==0)
                {
                    mUser.btnGetPos.setVisibility(View.INVISIBLE);
                }
                else
                {
                    mUser.btnGetPos.setVisibility(View.VISIBLE);
                    mUser.btnGetPos.setOnClickListener(this);
                }
            }



        }
        // 완성된 아이템 뷰를 반환합니다.
        return v;
    }




    public class CustomViewHolder{

        public TextView mUserNameTextView;
        public TextView mUserIdTextView;
        public TextView mWaitMsgTextView;

        public Button mBtnSend;
        public Button mBtnGetPos;
        public Button mBtnApproval;
    }

    // 데이터를 추가하는 것을 위해서 만들어 준다.
    public void add(FriendList_View user) {
        mUserData.add(user);
        notifyDataSetChanged();
    }







    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub

        // Tag를 이용하여 Data를 가져옵니다.
        FriendList_View clickItem = (FriendList_View) v.getTag();


        switch (v.getId()) {

            case R.id.btn_req_pos:
                String login_state="off", friend_id;
                String[] result;
                friend_id = clickItem.getFriendId();
                phpExcute = new PhpExcute();
                try {
                    login_state = phpExcute.execute("http://minsdatanetwork.iptime.org:82/iptsPhp/memberManage/getState.php?friend_id=" + friend_id).get();
                    phpExcute.cancel(true);
                }catch(ExecutionException e)
                {
                    e.printStackTrace();
                }catch(InterruptedException e)
                {
                    e.printStackTrace();
                }
                result = login_state.split(",");

                if(result[0].equals("on"))
                {

                    String mapPath = "/storage/sdcard0/indoorMapData/"+result[1]+".svg";
                    File mapFile = new File(mapPath);

                    if(mapFile.isFile()){

                        mapViewDialog = new MapViewDialog(mContext, session, friend_id);
                        Window window = mapViewDialog.getWindow();
                        window.setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
                        window.setGravity(Gravity.CENTER);

                        WindowManager.LayoutParams wlp = window.getAttributes();

                        wlp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
                        wlp.dimAmount = 0.7f;
                        window.setAttributes(wlp);

                        mapViewDialog.show();
                    }
                    else if(result[1].equals("nulll"))
                    {
                        Toast.makeText(mContext, "친구가 온라인 이지만 자신의 위치를\n찾고 있지 않네요~", Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        Toast.makeText(mContext, "지도가 존재하지 않아요!\n지도를 다운받아주세요!", Toast.LENGTH_SHORT).show();
                    }
                }

                else
                {
                    Toast.makeText(mContext, "친구가 오프라인 이네요!", Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.btn_app:
                phpExcute = new PhpExcute();
                String str;
                try {
                    str = phpExcute.execute("http://minsdatanetwork.iptime.org:82/iptsPhp/memberManage/accept_friend_req.php?user_id=" + getSessionInfo()+"&friend_id="+clickItem.getFriendId().toString()).get();
                    phpExcute.cancel(true);
                    if(str.equals("1"))
                    Toast.makeText(mContext, "승인됨", Toast.LENGTH_SHORT).show();
                }
                catch(InterruptedException e)
                {
                    e.printStackTrace();
                } catch(ExecutionException e)
                {
                    e.printStackTrace();
                }

                clickItem.waitMsg.setVisibility(View.INVISIBLE);
                clickItem.btnApproval.setVisibility(View.INVISIBLE);

                if(clickItem.getPosApproval()==0)
                {
                    clickItem.btnGetPos.setVisibility(View.INVISIBLE);
                }
                else
                {
                    clickItem.btnGetPos.setVisibility(View.VISIBLE);
                    clickItem.btnGetPos.setOnClickListener(this);
                }

                clickItem.approval = 1;

                break;
        }
    }

    @Override
    public boolean isEnabled(int position)
    {

        return true;
    }

    public String getSessionInfo()
    {
        String session;
        SharedPreferences sessionInfo = mContext.getSharedPreferences("sessinInfo", 0);

        session = sessionInfo.getString("session","");

        return session;
    }



}
