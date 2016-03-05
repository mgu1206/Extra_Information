package yu_cse.graduation_project_edit.util;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;

/**
 * Created by gyeunguckmin on 11/3/15.
 * 작업관리자에서 스와이핑(Swiping)으로 앱을 종료시 / 비정상 종료시
 * 계정을 로그아웃 처리 하기 위한 서비스
 */
public class TaskRemoveCheckService extends Service
{
    private String session="";
    private boolean checkAutoLogin;

    private PhpExcute phpExcute;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        session = intent.getStringExtra("session");
        checkAutoLogin = intent.getBooleanExtra("isAutoLogin",false);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        logoutOperation(1);

    }



    @Override
    public void onTaskRemoved(Intent rootIntent) {
        /**
         * 작업이 종료될때 (스와이핑으로 삭제) onTaskRemoved가 콜백됨.
         */
        super.onTaskRemoved(rootIntent);
        logoutOperation(1);
    }

    private void logoutOperation(int mode) {

        String tempResult = new String();
        boolean temp;
        phpExcute = new PhpExcute();
        //Intent intent;
        if(mode==1)
        {
            SharedPreferences pref = getSharedPreferences("loginInfo", MODE_PRIVATE);
            if(pref.getBoolean("isItAutoLogin",false))
            {
                mode = 1;
            }
            else mode = 0;
        }

        try {
            tempResult = phpExcute.execute("http://minsdatanetwork.iptime.org:82/iptsPhp/memberManage/change_state_to_off.php?id="+session+"&mode="+mode).get();
            phpExcute.cancel(true);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
