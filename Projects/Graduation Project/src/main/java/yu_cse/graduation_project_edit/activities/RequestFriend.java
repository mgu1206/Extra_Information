package yu_cse.graduation_project_edit.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutionException;

import yu_cse.graduation_project_edit.R;
import yu_cse.graduation_project_edit.util.PhpExcute;

public class RequestFriend extends Activity implements View.OnClickListener{

    private String session, findId, findedFriend, userId, tempFriend;

    private Intent intent;

    private EditText inputBuddyId;

    private TextView buddyNameText;

    private ImageButton findFrBtn, frReqBtn;

    private Vibrator buttonVibe;

    private PhpExcute getFriend;

    private JSONObject jsonObject, jo;
    private JSONArray jsonArray;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setFinishOnTouchOutside(false);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        layoutParams.dimAmount = 0.95f;
        getWindow().setAttributes(layoutParams);
        setContentView(R.layout.activity_request_friend);

        buttonVibe = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);

        intent = getIntent();
        session = intent.getSerializableExtra("session").toString(); //이전 액티비티에서 세션 받음(현재 로그인된 아이디)

        findFrBtn = (ImageButton)findViewById(R.id.findFrBtn);
        findFrBtn.setOnClickListener(this);
        frReqBtn = (ImageButton)findViewById(R.id.fr_req);
        frReqBtn.setOnClickListener(this);

        inputBuddyId = (EditText)findViewById(R.id.inputTextBuddyId);
        inputBuddyId.setBackgroundColor(Color.TRANSPARENT);

        buddyNameText = (TextView)findViewById(R.id.buddyNameText);
    }

    @Override
    public void onClick(View v) {
        buttonVibe.vibrate(20);
        getFriend = new PhpExcute();

        switch(v.getId())
        {
            case R.id.findFrBtn:
                if(inputBuddyId.getText().toString().equals(""))
                {
                    Toast.makeText(RequestFriend.this, "친구 아이디를 입력해요!", Toast.LENGTH_SHORT).show();
                }
                else if(inputBuddyId.getText().toString().equals(session))
                {
                    Toast.makeText(RequestFriend.this, "본인 아이디에요!", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    findedFriend = new String();

                    findId = new String();
                    userId = new String();
                    try
                    {
                        findId = java.net.URLEncoder.encode(new String(inputBuddyId.getText().toString().getBytes("UTF-8")));
                        userId = java.net.URLEncoder.encode(new String(session.getBytes("UTF-8")));
                    }
                    catch(UnsupportedEncodingException e)
                    {
                        e.printStackTrace();
                    }

                    try
                    {
                        findedFriend = getFriend.execute("http://minsdatanetwork.iptime.org:82/iptsPhp/memberManage/findfriend.php?user_id=" + userId + "&find_id=" + findId).get();
                        getFriend.cancel(true);
                    }
                    catch(Exception e)
                    {
                        e.printStackTrace();
                    }

                    try
                    {
                        jsonObject = new JSONObject(findedFriend);
                        jsonArray = jsonObject.getJSONArray("results");


                        for (int i = 0; i < jsonArray.length(); i++)
                        {
                            jo = jsonArray.getJSONObject(i);
                            if(jo.getString("mem_id").equals("already")) {
                                Toast.makeText(this,"이미 친구거나 친구 요청된 상태에요!", Toast.LENGTH_SHORT).show();
                            }
                            else if(jo.getString("mem_id").equals("not_exist"))
                            {
                                Toast.makeText(this,"존재 하지 않는 아이디에요..", Toast.LENGTH_SHORT).show();

                            }
                            else {
                                tempFriend = jo.getString("mem_id");
                                buddyNameText.setText("친구 ID: " +jo.getString("mem_id") + " 친구이름 : "+jo.getString("mem_name"));
                            }
                        }
                    }
                    catch(JSONException e)
                    {
                        e.printStackTrace();
                    }
                }
                /*
                입력한 아이디로 친구 찾기 눌렀을때
                 */
                break;
            case R.id.fr_req:
                if(buddyNameText.getText().toString().equals(""))
                {
                    //buttonVibe.vibrate(100);
                    Toast.makeText(RequestFriend.this, "친구 찾기를 먼저 이용하세요", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    addMyFriend(tempFriend);

                }
                /*
                친구 요청 벼튼 눌렀을때
                 */
                break;
        }

    }

    private void addMyFriend(String second_user)
    {
        phpDown signUpTask = new phpDown();
        String result="", session_value="", mSecond_user="";

        try {
            session_value = java.net.URLEncoder.encode(new String(session.getBytes("UTF-8")));
            mSecond_user = java.net.URLEncoder.encode(new String(second_user.getBytes("UTF-8")));
        }catch(UnsupportedEncodingException e)
        {
            e.printStackTrace();
        }


            try {
                result = signUpTask.execute("http://minsdatanetwork.iptime.org:82/iptsPhp/memberManage/addfriend.php?first_user="
                        + session_value + "&second_user=" + mSecond_user).get();
                //php파일 주소 + 각 데이터

                signUpTask.cancel(true);
            }
            catch(Exception e)
            {
                e.printStackTrace();
            }



        if (result.equals("1")) {
            String temp="";
            PhpExcute phpExcute = new PhpExcute();
            try {
                temp = phpExcute.execute("http://minsdatanetwork.iptime.org:82/iptsPhp/pushService/send_friend_req_push.php?user_id=" + mSecond_user + "&mem_id=" + session+"&mode="+0).get();
                phpExcute.cancel(true);
            }catch(ExecutionException e)
            {
                e.printStackTrace();
            }catch(InterruptedException e)
            {
                e.printStackTrace();
            }

            if(temp.equals("1"))
                showToast("친구 요청 및 푸시알람이 전송 되었습니다.");
            else
                showToast("친구 요청이 완료되었습니다.\n 안타깝게도 푸시알람은 전송되지 못했어요.");



        }
        else if(result.equals("2"))
        {
            showToast("이미 요청이 갔어요!");
        }
        else showToast("죄송해요. 오류가 생겼어요..");

        this.finish();
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

    private class phpDown extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... urls) {
            StringBuilder jsonHtml = new StringBuilder();
            try {
                URL url = new URL(urls[0]);

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                if (conn != null) {
                    conn.setConnectTimeout(10000);
                    conn.setUseCaches(false);

                    if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                        BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
                        String line = br.readLine();
                        jsonHtml.append(line);
                        br.close();
                    }
                    conn.disconnect();
                }

            } catch (Exception ex) {
                ex.printStackTrace();
            }
            return jsonHtml.toString();
        }

        protected void onPostExecute(String str) {



        }

    }
    public void showToast(String str) {
        Toast.makeText(RequestFriend.this, str, Toast.LENGTH_SHORT).show();
        //토스트 출력하는 함수
    }
}
