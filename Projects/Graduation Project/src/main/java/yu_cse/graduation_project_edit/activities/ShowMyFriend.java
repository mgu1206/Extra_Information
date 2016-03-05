package yu_cse.graduation_project_edit.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import yu_cse.graduation_project_edit.R;
import yu_cse.graduation_project_edit.list_views.FriendListView_Adapter;
import yu_cse.graduation_project_edit.list_views.FriendList_View;
import yu_cse.graduation_project_edit.util.PhpExcute;

public class ShowMyFriend extends Activity implements AdapterView.OnItemClickListener,AdapterView.OnItemLongClickListener, View.OnClickListener {

    private ListView friendList;
    private FriendListView_Adapter adapter;
    private String session;
    private String tempFriendList;

    private PhpExcute phpExcute, phpExcute1;

    private JSONObject jsonObject, jo;
    private JSONArray jsonArray;

    private Intent intent;

    private ImageButton requestBtn;

    private Vibrator buttonVibe;

    private int friendCount = 0;

    private ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);  //타이틀바 없는 액티비티 설정
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        layoutParams.dimAmount = 0.95f; //뒷쪽 배경의 액티비티를 흐리게 설정(어둡게)


        getWindow().setAttributes(layoutParams);
        setContentView(R.layout.activity_show_my_friend);

        buttonVibe = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        requestBtn = (ImageButton) findViewById(R.id.request_btn);
        requestBtn.setOnClickListener(this);

        //friendList = (ListView) findViewById(R.id.fr_List_View); //리스트뷰 등록

        tempFriendList = new String();

        intent = getIntent();
        session = intent.getSerializableExtra("session").toString(); //이전 액티비티에서 세션 받음(현재 로그인된 아이디)


        friendList = (ListView) findViewById(R.id.fr_List_View);


        friendList.setOnItemClickListener(this);
        friendList.setOnItemLongClickListener(this);

        adapter = new FriendListView_Adapter(this, session);
    }


    @Override
    public void onClick(View v) {
        buttonVibe.vibrate(20);
        Intent intent;
        intent = new Intent(ShowMyFriend.this, RequestFriend.class);
        intent.putExtra("session", session);

        startActivity(intent);
    }





    public void setFriendList(int mode)
    {
        adapter = new FriendListView_Adapter(this, session);
        phpExcute = new PhpExcute(); //AyncTask 객체 생성
        String str = "";
        try {
            str = phpExcute.execute("http://minsdatanetwork.iptime.org:82/iptsPhp/memberManage/getfriend.php?user_id=" + session).get();
        } catch (Exception e) {
            e.printStackTrace();
        }
        phpExcute.cancel(true);

        if (!str.equals("")) {
            try {
                jsonObject = new JSONObject(str);
                jsonArray = jsonObject.getJSONArray("results");

                for (int i = 0; i < jsonArray.length(); i++)
                {
                    jo = jsonArray.getJSONObject(i);

                    if (!jo.getString("friend").equals("empty"))
                    {
                        FriendList_View tp = new FriendList_View(jo.getString("friend"), jo.getString("name"), jo.getInt("approval"), jo.getInt("pos_approval"), jo.getInt("isrequest"), jo.getString("is_login"));
                        adapter.add(tp);
                    }
                    else
                    {
                        //친구 리스트가 empty일경우
                        showToastCenter("친구가 없어요..");

                    }
                }
            }
            catch (JSONException e)
            {
                e.printStackTrace();
            }
        }
        friendList.setAdapter(adapter);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        //친구 리스트에서 친구를 클릭 할 경우 액션 추가..//
        //위치 보기 액션 등등..
        buttonVibe.vibrate(20);


        String str="";
        final int pos = position;
        //현재 클릭된 아이템의 위치, 인덱스.

        FriendList_View mFriend = (FriendList_View) parent.getItemAtPosition(pos);
        //현재 클릭된 아이템의 데이터 내용을 mFriend로 대입.

        if(mFriend.getApproval()==1) {

            phpExcute1 = new PhpExcute();
            try {
            /*
            phpExcute객체가 반환한 JSON형 스트링을 tempFriendList에 저장
             */
                str = phpExcute1.execute("http://minsdatanetwork.iptime.org:82/iptsPhp/memberManage/getposapp.php?first_user="+session+"&second_user="+mFriend.getFriendId()).get();
            } catch (Exception e) {
                e.printStackTrace();
            }
            phpExcute1.cancel(true);

            Intent intent;
            intent = new Intent(ShowMyFriend.this, FriendInformation.class);
            intent.putExtra("session", session);
            intent.putExtra("friend_id", mFriend.getFriendId());
            intent.putExtra("friend_name", mFriend.getFriendName());
            intent.putExtra("pos_Approval", Integer.parseInt(str));

            startActivity(intent);

        }
        else
        {
            Toast.makeText(this, "아직 서로 친구가 이니에요!ㅜㅜ..", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

        buttonVibe.vibrate(5);
        buttonVibe.vibrate(5);
        buttonVibe.vibrate(5);


        final int pos = position;
        FriendList_View mFriend = (FriendList_View) parent.getItemAtPosition(pos);



            final String friendId = mFriend.getFriendId();

            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

            alertDialogBuilder.setTitle("친구 삭제 확인!");

            alertDialogBuilder.setMessage("정말 친구를 삭제 할까요??").setCancelable(false).setPositiveButton("종료",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            deleteFriend(friendId);
                            setFriendList(0);
                        }
                    }).setNegativeButton("취소",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();

        buttonVibe.vibrate(5);
        buttonVibe.vibrate(5);
        buttonVibe.vibrate(5);




       // FriendList_View mFriend = (FriendList_View)parent.getItemAtPosition(pos);

        return false;
    }

    private void deleteFriend(String friendId)
    {
        phpExcute = new PhpExcute(); //AyncTask 객체 생성
        String str="";


        try {
            /*
            phpExcute객체가 반환한 JSON형 스트링을 tempFriendList에 저장
             */
            str = phpExcute.execute("http://minsdatanetwork.iptime.org:82/iptsPhp/memberManage/delfriend.php?first_user="
                    + session + "&second_user=" + friendId).get();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if(str.equals("1"))
        {
            Toast.makeText(this, "삭제됨", Toast.LENGTH_SHORT).show();
        }


    }

    @Override
    public void onResume() {
        super.onResume();
        //setFriendList(tempFriendList);
        Log.d("TestAppActivity", "onResume");
        setFriendList(0);


    }

    @Override
    public void onRestart() {
        super.onRestart();
        Log.d("TestAppActivity", "onRestart");


    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d("TestAppActivity", "onPause");

    }

    @Override
    public void onBackPressed() {
        //뒤로 가기 버튼을 누를경우 현재 액티비티를 완전히 종료
        super.onBackPressed();
        finish();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        finish();
        Log.d("TestAppActivity", "onDestroy");
    }

    public void showToastCenter(String str) {

        Toast toast;
        toast = Toast.makeText(this, str, Toast.LENGTH_SHORT);
        int x = 0;
        int y = 0;
        toast.setGravity(Gravity.CENTER, x, y);
        toast.show();
    }
}
