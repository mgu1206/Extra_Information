package yu_cse.graduation_project_edit.activities;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.Spanned;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Random;
import java.util.regex.Pattern;

import yu_cse.graduation_project_edit.R;
import yu_cse.graduation_project_edit.util.EditTextFilter;

public class FindLostActivity extends Activity {

    /*
    분실된 정보를 찾을때 정보 입력란
    차례대로 이름 메일 아이디 휴대폰번호
     */
    EditText inputName;
    EditText inputMail;
    EditText inputId;
    EditText inputPhone;

    /*
    입력받을 정보를 저장할 변수를 초기화
     */
    String id = "";
    String name = "";
    String mail = "";
    String phoneNum = "";

    EditTextFilter etf;

    /*
    존재하는 계정인지 확인할 불변수
     */
    boolean isExistAccount = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_lost);

        inputName = (EditText) findViewById(R.id.inputName);
        inputMail = (EditText) findViewById(R.id.inputMail);

        inputId = (EditText) findViewById(R.id.inputId);
        inputId.setFilters(new InputFilter[]{filterAlphaNum});
        inputPhone = (EditText) findViewById(R.id.inputPhoneNum);

        findViewById(R.id.find_id_btn).setOnClickListener(mClickListener);
        findViewById(R.id.find_pw_btn).setOnClickListener(mClickListener);

    }
/*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_find_lost, menu);
        return true;
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
    */


    Button.OnClickListener mClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            /*
            아이디 찾기 기능 수행
             */
            switch (v.getId()) {
                case R.id.find_id_btn:
                    if (inputName.getText().toString().isEmpty() || inputMail.getText().toString().isEmpty()) {
                        /*
                        입력된 정보가 NULL일경우 처리
                         */
                        showToast("가입시 등록한 이름과 이메일을 입력하세요.");
                    } else {

                        FindMyId findId = new FindMyId();

                        try {
                            mail = java.net.URLEncoder.encode(new String(inputMail.getText().toString().getBytes("UTF-8")));
                            name = java.net.URLEncoder.encode(new String(inputName.getText().toString().getBytes("UTF-8")));
                            findId.execute("http://minsdatanetwork.iptime.org:82/iptsPhp/memberManage/findId.php?name=" + name + "&mail=" + mail);
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                    }
                    break;

                case R.id.find_pw_btn:
                    isExistAccount = false;
                    if (inputId.getText().toString().isEmpty() || inputPhone.getText().toString().isEmpty()) {
                        showToast("아이디와 휴대폰번호를 대쉬(-) 없이 입력하세요.");
                    } else {
                        String first = null;
                        String mid = null;
                        String last = null;

                        FindMyId findId = new FindMyId();

                        phoneNum = inputPhone.getText().toString();

                        if (phoneNum.length() == 11) {
                            first = phoneNum.substring(0, 3);
                            mid = phoneNum.substring(3, 7);
                            last = phoneNum.substring(7, 11);
                        } else if (phoneNum.length() == 10) {
                            first = phoneNum.substring(0, 3);
                            mid = phoneNum.substring(3, 6);
                            last = phoneNum.substring(6, 10);
                        }
                        phoneNum = first + "-" + mid + "-" + last;


                        try {
                            id = java.net.URLEncoder.encode(new String(inputId.getText().toString().getBytes("UTF-8")));
                            phoneNum = java.net.URLEncoder.encode(new String(inputPhone.getText().toString().getBytes("UTF-8")));
                            findId.execute("http://minsdatanetwork.iptime.org:82/iptsPhp/memberManage/findId.php?name=" + name + "&mail=" + mail);

                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                            ;
                        }


                        /*
                        비밀번호 초기화 기능 수행 작성중
                         */
                    }
                    break;
            }

        }
    };


    public void showToast(String str) {
        Toast.makeText(FindLostActivity.this, str, Toast.LENGTH_SHORT).show();
        //토스트 메세지를 짧게 출력하는 함수
    }

    public void showToastLong(String str) {
        Toast.makeText(FindLostActivity.this, str, Toast.LENGTH_LONG).show();
        //토스트 메세지를 길게 출력하는 함수
    }


    private class FindMyId extends AsyncTask<String, Integer, String> {
        /*
        php를 실행하며 AsyncTask를 상속받는 FindMyId 클래스
         */

        @Override
        protected String doInBackground(String... urls) {
            /*
            //해당 기능은 doInBackground 함수 안에서 실행된다..//
            */
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
            /*
            실행하고 난뒤 php에서 echo로 돌려준 값 (str)로 상태 확인
             */

            if (str.equals("1")) {
                showToast("일치하는 정보가 없습니다.");
            } else if (str.equals("2")) {
                showToast("알수 없는 오류 발생");
            } else if (str.equals("3")) {
                showToast("서버 접속에 실패했습니다.");
            } else if (str.equals("4")) {
                isExistAccount = true;
            } else {
                showToastLong("회원님의 아이디는 " + str + " 입니다.\n 비밀번호를 찾으시려면 아래를 이용하세요.");

                inputId.setText(str);

                showToast("비밀번호 찾기 아이디 입력란에\n자동으로 입력되었습니다.");
            }
        }
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
