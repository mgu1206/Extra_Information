package yu_cse.graduation_project_edit.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Spanned;
import android.text.TextWatcher;
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
import java.util.concurrent.ExecutionException;
import java.util.regex.Pattern;

import yu_cse.graduation_project_edit.R;
import yu_cse.graduation_project_edit.util.EditTextFilter;

/*
    2015. 8. 10 작성 시작
    민경욱
 */

public class RegistrationActivity extends Activity {

    /*
    회원가입에 필요한 여러가지 데이터를 입력 받는 필드
     */
    EditText inputId;
    EditText inputPw;
    EditText reInputPw;
    EditText inputMail;
    EditText inputName;
    EditText inputNum;

    EditTextFilter etf;

    boolean isNumApplied = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);


        inputId = (EditText) findViewById(R.id.inputTextId);
        inputId.setFilters(new InputFilter[]{filterAlphaNum});
        inputPw = (EditText) findViewById(R.id.inputTextPw);
        reInputPw = (EditText) findViewById(R.id.reinputTextPw);
        inputMail = (EditText) findViewById(R.id.inputTextMail);
        inputName = (EditText) findViewById(R.id.inputTextName);
        inputNum = (EditText) findViewById(R.id.inputPhoneNum);

        inputNum.setInputType(InputType.TYPE_CLASS_PHONE);


        findViewById(R.id.signUp).setOnClickListener(mClickListener);


    }

    Button.OnClickListener mClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String result = new String();
            String id = null;
            String pw = null;
            String rePw = null;
            String mail = null;
            String name = null;
            String phoneNum = null;

            if (inputId.getText().toString().isEmpty())
            {
                showToast("아이디를 입력하세요.");
            }
            else if(inputId.getText().toString().equals("empty") || inputId.getText().toString().equals("root") || inputId.getText().toString().equals("pos"))
            {
                showToast("사용할 수 없는 아이디랍니다!");
            }
            else if(inputId.getText().toString().length()>10)
            {
                showToast("아이디는 10자 이하입니다.");
                inputId.setText("");
            }
            else if (inputName.getText().toString().isEmpty()) {
                showToast("이름을 입력하세요.");
            }
            else if (inputPw.getText().toString().isEmpty())
            {
                showToast("비밀번호를 입력하세요.");
            }
            else if (reInputPw.getText().toString().isEmpty())
            {
                showToast("비밀번호 재입력 부분을 입력하세요.");
            }
            else if (!inputPw.getText().toString().equals(reInputPw.getText().toString())) {
                showToast("두 비밀번호가 다릅니다.");
            }
            else if(inputPw.getText().toString().length()<5 || inputPw.getText().toString().length()>10)
            {
                showToast("비밀번호는 5자 ~ 10자 사이 입니다.");
                inputPw.setText("");
                reInputPw.setText("");
            }
            else if (inputNum.getText().toString().isEmpty()) {
                showToast("휴대폰 번호를 입력하세요.");
            }
            else if (inputMail.getText().toString().isEmpty()) {
                showToast("메일 주소를 입력하세요.");
                /*
                각종 필수입력 데이터를 입력 하지 않았을 경우(NULL)처리
                 */
            }
            else {

                /*
                    가입절차 진행부분
                 */

                phpDown signUpTask = new phpDown();

                try {
                    /*
                    텍스트를 UTF-8로 인코딩
                     */
                    id = java.net.URLEncoder.encode(new String(inputId.getText().toString().getBytes("UTF-8")));
                    name = java.net.URLEncoder.encode(new String(inputName.getText().toString().getBytes("UTF-8")));
                    pw = java.net.URLEncoder.encode(new String(inputPw.getText().toString().getBytes("UTF-8")));
                    mail = java.net.URLEncoder.encode(new String(inputMail.getText().toString().getBytes("UTF-8")));
                    phoneNum = java.net.URLEncoder.encode(new String(inputNum.getText().toString().getBytes("UTF-8")));
                    /*
                    AsyncTask객체를 통해서 php를 실행해서 가입진행
                     */
                    try {
                        result = signUpTask.execute("http://minsdatanetwork.iptime.org:82/iptsPhp/memberManage/registration.php?id="
                                + id + "&pw=" + pw + "&name=" + name + "&mail=" + mail + "&phone=" + phoneNum).get();
                        //php파일 주소 + 각 데이터

                        signUpTask.cancel(true);
                    }
                    catch(Exception e)
                    {
                        e.printStackTrace();
                    }
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

                Intent intent;

                if (result.equals("1")) {
                    //결과값이 1이면 기록된 레코드가 1개 이므로 정상 가입처리
                    showToast("가입이 완료되었습니다.");
                    intent = new Intent(RegistrationActivity.this, MainActivity.class);
                    startActivity(intent);
                } else {
                    showToast(result);
                }

            }


        }
    };

    public void showToast(String str) {
        Toast.makeText(RegistrationActivity.this, str, Toast.LENGTH_SHORT).show();
        //토스트 출력하는 함수
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
