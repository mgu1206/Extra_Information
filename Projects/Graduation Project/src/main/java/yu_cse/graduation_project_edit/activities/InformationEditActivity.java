package yu_cse.graduation_project_edit.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.app.Activity;
import android.os.Vibrator;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.concurrent.ExecutionException;

import yu_cse.graduation_project_edit.R;
import yu_cse.graduation_project_edit.util.PhpExcute;

public class InformationEditActivity extends Activity implements View.OnClickListener {
    TextView id_View, name_View, mail_Vew, phone_View;
    EditText new_Mail, new_Phone, new_pw, new_pw_chk;
    ImageButton editButton, cancelButton;

    Intent intent;

    String session;
    String info="";
    String[] result;

    PhpExcute phpExcute, getMyInfo;

    private Vibrator buttonVibe;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);  //타이틀바 없는 액티비티 설정
        setContentView(R.layout.activity_information_edit);
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        layoutParams.dimAmount = 0.95f; //뒷쪽 배경의 액티비티를 흐리게 설정(어둡게)

        intent = getIntent();
        session = intent.getSerializableExtra("session").toString(); //이전 액티비티에서 세션 받음(현재 로그인된 아이디)

        id_View = (TextView) findViewById(R.id.idTextView);
        name_View = (TextView) findViewById(R.id.nameTextView);
        mail_Vew = (TextView) findViewById(R.id.mailTextView);
        phone_View = (TextView) findViewById(R.id.phonetextView);

        new_Mail = (EditText) findViewById(R.id.new_mail);
        new_Mail.setBackgroundColor(Color.TRANSPARENT);
        new_Phone = (EditText) findViewById(R.id.new_phone);
        new_Phone.setBackgroundColor(Color.TRANSPARENT);
        new_pw = (EditText) findViewById(R.id.new_pass_word);
        new_pw.setBackgroundColor(Color.TRANSPARENT);
        new_pw_chk = (EditText) findViewById(R.id.new_pass_word_chk);
        new_pw_chk.setBackgroundColor(Color.TRANSPARENT);

        editButton = (ImageButton) findViewById(R.id.info_acceptBtn);
        editButton.setOnClickListener(this);
        cancelButton = (ImageButton) findViewById(R.id.info_cancelBtn);
        cancelButton.setOnClickListener(this);

        buttonVibe = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);


        setInitData();
    }

    public void setInitData()
    {
        getMyInfo = new PhpExcute();

        try {
            info = getMyInfo.execute("http://minsdatanetwork.iptime.org:82/iptsPhp/memberManage/getMyInfo.php?mem_id="+session).get();
            getMyInfo.cancel(true);
        }catch(ExecutionException e)
        {
            e.printStackTrace();
        }catch(InterruptedException e)
        {
            e.printStackTrace();
        }

        result = info.split(",");

        id_View.setText("아이디 : "+session);
        name_View.setText("이름 : "+result[0]);
        mail_Vew.setText("이메일 : "+result[1]);
        phone_View.setText("전화번호 : " + result[2]);

        new_Mail.setText("");
        new_Phone.setText("");
        new_pw.setText("");
        new_pw_chk.setText("");

    }

    @Override
    public void onClick(View v) {

        buttonVibe.vibrate(20);

        switch (v.getId()) {
            case R.id.info_acceptBtn:
                String changeResult="";
                String[] resultList;
                if (new_Mail.getText().toString().equals("") && new_Phone.getText().toString().equals("")
                        && new_pw.getText().toString().equals("") && new_pw_chk.getText().toString().equals("")) {
                    Toast.makeText(this, "입력된 내용이 없습니다.\n변경을 원치 않으면 취소를 눌러주세요!", Toast.LENGTH_SHORT).show();
                } else {
                    String newInforValue = "http://minsdatanetwork.iptime.org:82/iptsPhp/memberManage/change_info.php?mem_id=" + session;
                    String result="";

                    phpExcute = new PhpExcute();

                    if (!new_Mail.getText().toString().equals("")) {
                        newInforValue += "&mail=" + new_Mail.getText().toString();
                    }

                    if (!new_Phone.getText().toString().equals("")) {
                        newInforValue += "&phone=" + new_Phone.getText().toString();
                    }

                    if ((!new_pw.getText().toString().equals("")) &&
                            (!new_pw_chk.getText().toString().equals("")) &&
                            (new_pw.getText().toString().equals(new_pw_chk.getText().toString()))) {
                        newInforValue += "&pw=" + new_pw.getText().toString();
                    }

                    try {
                        result = phpExcute.execute(newInforValue).get();
                        phpExcute.cancel(true);
                    } catch (ExecutionException e) {
                        e.printStackTrace();

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    resultList = result.split(",");

                    for(int i=0; i<resultList.length; i++)
                    {
                        if(!resultList[i].equals("2"))
                        {
                            if(resultList[i].equals("mail exist"))
                            {
                                changeResult += "같은 메일 주소가 존재하여 변경하지 못했습니다.\n";
                            }
                            else if(resultList[i].equals("mail success"))
                            {
                                changeResult += "메일 주소를 변경하였습니다.\n";
                            }
                            else if(resultList[i].equals("phone exist"))
                            {
                                changeResult += "같은 전화번호가 존재하여 변경하지 못했습니다.\n";
                            }
                            else if(resultList[i].equals("phone success"))
                            {
                                changeResult += "전화번호를 변경하였습니다.\n";
                            }
                            else if(resultList[i].equals("password changed"))
                            {
                                changeResult += "비밀번호를 변경하였습니다.\n";
                            }
                        }
                    }

                        Toast.makeText(this, changeResult, Toast.LENGTH_SHORT).show();
                    }
                    setInitData();
                this.finish();

                break;

            case R.id.info_cancelBtn:
                this.finish();
                break;
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
