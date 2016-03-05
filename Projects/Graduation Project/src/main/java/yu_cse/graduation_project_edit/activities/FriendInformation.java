package yu_cse.graduation_project_edit.activities;

import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.app.Activity;
import android.os.Vibrator;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.util.concurrent.ExecutionException;

import yu_cse.graduation_project_edit.R;
import yu_cse.graduation_project_edit.util.PhpExcute;


public class FriendInformation extends Activity implements View.OnClickListener{

    Intent intent;

    private String session;
    String friend_id="", friend_name="";

    private TextView friend_name_textView;
    private ImageButton accept_Btn, cancel_Btn;
    private Switch posSwitch;

    private int pos_Approval;

    private PhpExcute phpExcute;

    private Vibrator buttonVibe;

    private int isSwitchOn, previousSwitch;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);  //타이틀바 없는 액티비티 설정
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        layoutParams.dimAmount = 0.95f; //뒷쪽 배경의 액티비티를 흐리게 설정(어둡게)

        String str="";

        phpExcute = new PhpExcute(); //AyncTask 객체 생성


        getWindow().setAttributes(layoutParams);
        setContentView(R.layout.activity_friend_information);

        buttonVibe = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);



        intent = getIntent();
        session = intent.getSerializableExtra("session").toString(); //이전 액티비티에서 세션 받음(현재 로그인된 아이디)
        friend_id = intent.getSerializableExtra("friend_id").toString();
        friend_name = intent.getSerializableExtra("friend_name").toString();
        pos_Approval = intent.getIntExtra("pos_Approval", 0);

        friend_name_textView = (TextView) findViewById(R.id.nameTextView);

        accept_Btn = (ImageButton) findViewById(R.id.acceptBtn);
        accept_Btn.setOnClickListener(this);
        cancel_Btn = (ImageButton) findViewById(R.id.cancelBtn);
        cancel_Btn.setOnClickListener(this);
        posSwitch = (Switch) findViewById(R.id.pos_share_switch);



        friend_name_textView.setText(friend_name);




        if(pos_Approval==1)
        {
            posSwitch.setChecked(true);
            previousSwitch = 1;
            isSwitchOn=1;
        }
        else
        {
            posSwitch.setChecked(false);
            previousSwitch = 0;
        }

        posSwitch.setOnCheckedChangeListener(new Switch.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                    isSwitchOn = 1;
                else
                    isSwitchOn = 0;
            }
        });
    }

    @Override
    public void onClick(View v) {

        buttonVibe.vibrate(20);

        switch(v.getId())
        {
            case R.id.acceptBtn:
                updateFriendInfo(friend_id);
                finish();

                break;

            case R.id.cancelBtn:
                finish();
                break;
        }

    }


    private void updateFriendInfo(String friendId)
    {
        phpExcute = new PhpExcute(); //AyncTask 객체 생성
        String str="";

        if(previousSwitch != isSwitchOn) {

            try {
            /*
            phpExcute객체가 반환한 JSON형 스트링을 tempFriendList에 저장
             */
                str = phpExcute.execute("http://minsdatanetwork.iptime.org:82/iptsPhp/memberManage/approvelocshare.php?first_user="
                        + session + "&second_user=" + friendId+"&state="+String.valueOf(isSwitchOn)).get();
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (str.equals("1")) {
                Toast.makeText(this, "저장되었습니다!!", Toast.LENGTH_SHORT).show();
            }
        }
        else
        {
            Toast.makeText(this, "이전과 상태가 같아요!", Toast.LENGTH_SHORT).show();
        }


    }



}
