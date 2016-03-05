package yu_cse.graduation_project_edit.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Vibrator;
import android.text.InputFilter;
import android.text.Spanned;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gcm.GCMRegistrar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.Pattern;

import yu_cse.graduation_project_edit.R;
import yu_cse.graduation_project_edit.util.EditTextFilter;
import yu_cse.graduation_project_edit.util.PhpExcute;


/**
 Written by Min Gyeong-Uk
 2015-08-05
 */

public class MainActivity extends Activity implements CompoundButton.OnCheckedChangeListener {
    public String regstrationId, myResult, tempId, tempPw, session;

    private ProgressDialog loagindDialog;

    private boolean isItAutoLogin, checkAutoLogin;
    private PhpExcute phpExcute;

    private Vibrator buttonVibe;

    private EditText inputId;
    private EditText inputPw;

    private CheckBox autoLogin;

    private JSONObject jsonObject, jo;
    private JSONArray jsonArray;


    private AsyncTask<?, ?, ?> regIDInsertTask;

    EditTextFilter etf;

    private static Context mContext;

    public static Context getContext() {
        return mContext;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = getApplicationContext();

        buttonVibe = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        autoLogin = (CheckBox) findViewById(R.id.autoLoginBox);
        autoLogin.setOnCheckedChangeListener(this);

        inputId = (EditText) findViewById(R.id.inputTextId);    // 아이디 입력 부분
        inputId.setBackgroundColor(Color.TRANSPARENT);
        inputId.setFilters(new InputFilter[]{filterAlphaNum});
        inputPw = (EditText) findViewById(R.id.inputTextPw);    // 비밀번호 입력 부분
        inputPw.setBackgroundColor(Color.TRANSPARENT);


        findViewById(R.id.loginBtn).setOnClickListener(mClickListener);
        findViewById(R.id.findLostBtn).setOnClickListener(mClickListener);
        findViewById(R.id.registrationBtn).setOnClickListener(mClickListener);


        getPreferences();
        if (checkAutoLogin) {
            loginOperation(tempId, tempPw);

            autoLogin.setChecked(true);
            inputId.setText(tempId);
            inputPw.setText(tempPw);

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked) {
            isItAutoLogin = true;
            Toast.makeText(MainActivity.this, "로그인 정보가 저장됩니다.", Toast.LENGTH_SHORT).show();
        } else {
            isItAutoLogin = false;
            Toast.makeText(MainActivity.this, "로그인 정보가 삭제됩니다.", Toast.LENGTH_SHORT).show();
        }
    }

    Button.OnClickListener mClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            Intent intent;
            buttonVibe.vibrate(13);

            switch (v.getId()) {

                case R.id.loginBtn:

                    if (inputId.getText().toString().isEmpty()) {
                        showToast("아이디를 입력하세요.");
                    } else if (inputPw.getText().toString().isEmpty()) {
                        showToast("비밀번호를 입력하세요.");
                    } else {
                        //로그인 기능 함수 실행
                        loginOperation(inputId.getText().toString(), inputPw.getText().toString());
                        phpExcute.cancel(true); //AysncTask 정지

                    }
                    break;

                case R.id.findLostBtn:
                    intent = new Intent(MainActivity.this, FindLostActivity.class);
                    mainDataInit();
                    startActivity(intent);

                    // 아이디-비밀번호 찾기 버튼 눌렀을때 액티비티 전환
                    break;

                case R.id.registrationBtn:
                    intent = new Intent(MainActivity.this, RegistrationActivity.class);
                    mainDataInit();
                    startActivity(intent);

                    // 회원가입 버튼 눌렀을 때 액티비티 전환
                    break;
            }
        }
    };

    private void loginOperation(String id, String pw) {

        phpExcute = new PhpExcute(); //AsyncTask는 재실행이 안되기때문에 그때 그때 새로운 객체로 생성

        String tempResult = new String();
        Intent intent;
        session = id;

        try {
            String inputedId = java.net.URLEncoder.encode(new String(id.getBytes("UTF-8")));
            String inputedPw = java.net.URLEncoder.encode(new String(pw.getBytes("UTF-8")));
            try {
                tempResult = phpExcute.execute("http://minsdatanetwork.iptime.org:82/iptsPhp/memberManage/login.php?id=" + id + "&pw=" + pw).get();
                phpExcute.cancel(true);
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                jsonObject = new JSONObject(tempResult);
                jsonArray = jsonObject.getJSONArray("results");

                jo = jsonArray.getJSONObject(0);

                if (jo.getString("loginResult").equals("success")) {
                    showToast("로그인 되었습니다.");

                    saveSessionInfo(id);

                    if(isItAutoLogin) {
                        saveAutoLoginInfo(id, pw);
                    }
                    /**
                     * registerGcm() -> GCM에 기기를 등록하는 함수
                     *                  에뮬레이터에서 테스트 시 주석처리 하여 작동하지 않도록해야 오류 안남.
                     */
                    registerGcm();

                    intent = new Intent(MainActivity.this, LoginMainActivity.class);
                    intent.putExtra("session", id);
                    //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                } else if(jo.getString("loginResult").equals("alreadyLogin"))
                {
                    /**
                     * 이미 로그인 되어있는 계정으로
                     * 재 로그인 시도를 할 경우 처리
                     */
                    showToast("이미 로그인 되어 있는 계정입니다.");
                    phpExcute.cancel(true);
                }
                else {
                    showToast("일치하는 계정이 없습니다.");
                    phpExcute.cancel(true);
                    isItAutoLogin = false;
                    removeAutoLoginInfo();

                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }


    }

    public void mainDataInit() {
        inputId.setText("");
        inputPw.setText("");
    }


    public void showToast(String str) {
        Toast.makeText(MainActivity.this, str, Toast.LENGTH_SHORT).show();
        //토스트 출력하는 함수
    }


    private void registerGcm() {
        GCMRegistrar.checkDevice(this);
        GCMRegistrar.checkManifest(this);

        regstrationId = GCMRegistrar.getRegistrationId(this);

        if (regstrationId.equals("")) {

            GCMRegistrar.register(this, "738361865831");
            regstrationId = GCMRegistrar.getRegistrationId(this);
            //GCM 등록하기
        }
        sendAPIkey();
    }

    private void sendAPIkey() {
        regIDInsertTask = new regIDInsertTask().execute(regstrationId, session);
    }

    private class regIDInsertTask extends AsyncTask<String, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //loagindDialog = ProgressDialog.show(MainActivity.this, "통신 중입니다..",
                //    "Please wait..", true, false);
            //loagindDialog = Pro
        }

        @Override
        protected Void doInBackground(String... params) {
            HttpPostData(params[0], params[1]);
            return null;
        }

        protected void onPostExecute(Void result) {
            //loagindDialog.dismiss();
        }
    }

    public void HttpPostData(String reg_id, String user_id) {
        try {
            URL url = new URL("http://minsdatanetwork.iptime.org:82/iptsPhp/pushService/gcm_reg_insert.php");       // URL 설정
            HttpURLConnection http = (HttpURLConnection) url.openConnection();   // 접속

            http.setDefaultUseCaches(false);
            http.setDoInput(true);
            http.setDoOutput(true);
            http.setRequestMethod("POST");

            http.setRequestProperty("content-type", "application/x-www-form-urlencoded");
            StringBuffer buffer = new StringBuffer();
            buffer.append("reg_id").append("=").append(reg_id).append("&");                 // php 변수에 값 대입
            buffer.append("user_id").append("=").append(user_id);

            OutputStreamWriter outStream = new OutputStreamWriter(http.getOutputStream(), "EUC-KR");
            PrintWriter writer = new PrintWriter(outStream);
            writer.write(buffer.toString());
            writer.flush();
            InputStreamReader tmp = new InputStreamReader(http.getInputStream(), "EUC-KR");
            BufferedReader reader = new BufferedReader(tmp);
            StringBuilder builder = new StringBuilder();
            String str;
            while ((str = reader.readLine()) != null) {
                builder.append(str + "\n");
            }

            myResult = builder.toString();

        } catch (MalformedURLException e) {
            //
        } catch (IOException e) {
            //
        } // try
    }

    public void getPreferences() {
        SharedPreferences pref = getSharedPreferences("loginInfo", MODE_PRIVATE);
        checkAutoLogin = pref.getBoolean("isItAutoLogin", false);
        tempId = pref.getString("mem_id", "");
        tempPw = pref.getString("mem_pw", "");

    }

    // 값 저장하기

    public void saveSessionInfo(String id)
    {
        SharedPreferences sessionInfo = getSharedPreferences("sessinInfo", MODE_PRIVATE);
        SharedPreferences.Editor editor = sessionInfo.edit();
        editor.putString("session", id);

        editor.commit();
    }

    public String getSessionInfo()
    {
        String session;
        SharedPreferences sessionInfo = getSharedPreferences("sessinInfo", MODE_PRIVATE);

        session = sessionInfo.getString("session","");

        return session;
    }

    public void saveAutoLoginInfo(String id, String pw) {
        /*
        자동 로그인이 체크되어 있을경우 SharedPreference에 데이터를 저장
         */
        SharedPreferences pref = getSharedPreferences("loginInfo", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean("isItAutoLogin", isItAutoLogin);

        editor.putString("mem_id", id);
        editor.putString("mem_pw", pw);


        editor.commit();
    }

    // 값(Key Data) 삭제하기
    private void removeAutoLoginInfo() {
        SharedPreferences pref = getSharedPreferences("loginInfo", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.remove("isItAutoLogin");
        editor.remove("mem_id");
        editor.remove("mem_pw");

        editor.commit();
    }


    // 값(ALL Data) 삭제
    public void removeAllPreferences() {
        SharedPreferences pref = getSharedPreferences("loginInfo", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.clear();
        editor.commit();
    }

    public InputFilter filterAlphaNum = new InputFilter() {
        public CharSequence filter(CharSequence source, int start, int end,
                                   Spanned dest, int dstart, int dend) {

            Pattern ps = Pattern.compile("^[a-zA-Z0-9]+$");
            if (!ps.matcher(source).matches()) {
                return "";
            }
            return null;
        }
    };

}
