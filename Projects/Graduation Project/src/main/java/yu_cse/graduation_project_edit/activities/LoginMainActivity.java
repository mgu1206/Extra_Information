package yu_cse.graduation_project_edit.activities;

import android.app.Activity;
import android.app.AlertDialog;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;

import android.graphics.Bitmap;
import android.graphics.PointF;

import android.graphics.drawable.AnimationDrawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Message;
import android.os.Vibrator;
import android.os.Handler;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.jiahuan.svgmapview.SVGMapView;
import com.jiahuan.svgmapview.SVGMapViewListener;
import com.jiahuan.svgmapview.overlay.SVGMapLocationOverlay;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.lang.ref.WeakReference;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.concurrent.ExecutionException;

import de.uvwxy.footpath.core.StepDetection;
import de.uvwxy.footpath.core.StepTrigger;

import yu_cse.graduation_project_edit.R;
import yu_cse.graduation_project_edit.location.BuildingListSet;
import yu_cse.graduation_project_edit.location.GetMapNameText;
import yu_cse.graduation_project_edit.location.GetLocation;
import yu_cse.graduation_project_edit.util.AssetsHelper;
import yu_cse.graduation_project_edit.util.PhpExcute;
import yu_cse.graduation_project_edit.util.TaskRemoveCheckService;

public class LoginMainActivity extends Activity implements AdapterView.OnItemSelectedListener, SensorEventListener, View.OnClickListener {


    final Context context = this;

    final float MAX_ZOOM = 3.0f;   //최대 줌 배율

    private float presuureValue;

    private LocationRefresh locationRefresh = null;
    private MapFinder mapFinder;

    private int currentPosX = 0, currentPosY = 0,
            currentDirection = 0,
            previousPosX = 0, previousPosY = 0,
            previousDirection = 0,
            pPreviousPosX = 0, pPreviousPosY = 0,
            pPreviousDirection = 0,
            tempPosX = 0, tempPosY = 0, overPosCnt = 0, isItReverse = 0;

    private SensorManager m_sensor_manager;
    private Sensor m_acc_sensor, m_mag_sensor, preSensor;

    private float[] m_acc_data = null, m_mag_data = null;
    private float[] m_rotation = new float[9];
    private float[] m_result_data = new float[3];

    public GetLocation getLocation;

    private PhpExcute phpExcute;

    private SVGMapView mapView;  //지도가 표현될 커스텀 뷰
    private SVGMapLocationOverlay locationOverlay;  //위치가 포현될 오버레이

    private Vibrator buttonVibe;

    private boolean isLocationRef = false, isItPressSensor = false;  //현재위치가 갱신 되고 있는지 여부를 저장하는 불린값

    private ImageButton showMyLocation, showMyFreind, editInformation, delAccount;
    private Intent activityChangeIntent, intent;  // 인텐트
    private Intent service;

    private String session, currentMap = "";

    public TextView mapTextView;

    private JSONObject jsonObject, jo;
    private JSONArray jsonArray;

    public PositionSet positionSet;
    public PositionSet tempPos;

    private Handler handler;
    private Handler uiHandler;

    boolean checkAutoLogin, isMapExist;

    private ImageView locationIconOff, locationIconOn, pressSensorIcon;

    ArrayList<PositionSet> tempposi;

    GetMapNameText getMapNameText;

    Message msg, uiChMsg, altiMsg;

    private StepDetection stepDetection;

    private StepTrigger stepTrigger;

    private Spinner mapListSpinner;

    private String preCurrentMap = "";

    ArrayList<String> list;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_main);

        getMapNameText = new GetMapNameText();


        SharedPreferences temp = getSharedPreferences("sessinInfo", MODE_PRIVATE);
        SharedPreferences pref = getSharedPreferences("loginInfo", MODE_PRIVATE);
        checkAutoLogin = pref.getBoolean("isItAutoLogin", false);

        if (checkAutoLogin) {
            session = pref.getString("mem_id", "");    //이전 액티비티에서 세션값을 넘겨 받음(로그인 상태)

        } else {
            session = temp.getString("session", "");
        }

        mapListSpinner = (Spinner) findViewById(R.id.mapList);

        service = new Intent(this, TaskRemoveCheckService.class);
        service.putExtra("session", session);
        service.putExtra("isAutoLogin", checkAutoLogin);
        startService(service);

        pressSensorIcon = (ImageView) findViewById(R.id.pressSensorIcon);

        mapTextView = (TextView) findViewById(R.id.mapNameTextView);

        m_sensor_manager = (SensorManager) getSystemService(SENSOR_SERVICE);

        m_acc_sensor = m_sensor_manager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        m_mag_sensor = m_sensor_manager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

        boolean test = false;
        /**
         * 기압센서 사용가능한지 확인.
         */
        if (m_sensor_manager.getDefaultSensor(Sensor.TYPE_PRESSURE) != null) {
            preSensor = m_sensor_manager.getDefaultSensor(Sensor.TYPE_PRESSURE);
            isItPressSensor = true;
            mapListSpinner.setVisibility(View.INVISIBLE);
            pressSensorIcon.setImageResource(R.drawable.circle_green);
            Toast.makeText(this, "기압센서로 층을 구분합니다.", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "해당기기엔 기압센서가 없습니다.\n맵뷰 우측 상단에서 지도 선택 가능합니다.", Toast.LENGTH_SHORT).show();
            getTitleList();
            isItPressSensor = false;
            pressSensorIcon.setImageResource(R.drawable.circle_red);

            mapListSpinner.setVisibility(View.VISIBLE);
        }


        tempposi = new ArrayList<>();


        showMyFreind = (ImageButton) findViewById(R.id.fr_loc_bt);
        showMyFreind.setOnClickListener(this);  // 버튼 클릭 리스너 등록(아래동일)
        showMyLocation = (ImageButton) findViewById(R.id.my_loc_bt);
        showMyLocation.setOnClickListener(this);
        editInformation = (ImageButton) findViewById(R.id.edit_info_bt);
        editInformation.setOnClickListener(this);
        delAccount = (ImageButton) findViewById(R.id.del_account_bt);
        delAccount.setOnClickListener(this);

        mapView = (SVGMapView) findViewById(R.id.map_view);


        buttonVibe = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        locationOverlay = new SVGMapLocationOverlay(mapView); //locationOveray 객체를 맵뷰에 등록
        locationOverlay.setMode(SVGMapLocationOverlay.MODE_NORMAL);

        locationIconOff = (ImageView) findViewById(R.id.loc_icon_off);
        locationIconOn = (ImageView) findViewById(R.id.loc_icon_on);
        locationIconOn.setImageResource(R.drawable.my_blinking_drawable);
        AnimationDrawable frameAnimation = (AnimationDrawable) locationIconOn.getDrawable();
        frameAnimation.start();
        locationIconOn.setVisibility(View.INVISIBLE);
        locationIconOff.setVisibility(View.VISIBLE);

        getMapNameText = new GetMapNameText();

        stepTrigger = new StepTrigger() {
            @Override
            public void trigger(long now_ms, double compDir) {

            }

            @Override
            public void dataHookAcc(long now_ms, double x, double y, double z) {

            }

            @Override
            public void dataHookComp(long now_ms, double x, double y, double z) {

            }

            @Override
            public void timedDataHook(long now_ms, double[] acc, double[] comp) {

            }
        };

        stepDetection = new StepDetection(this, stepTrigger, 0.31, 1.4, 250);

        handler = new MyHandler(this);
        uiHandler = new UiChagneHandler(this);
    }

    class PositionSet {

        public int pos = 0;
        public int pos_x = 0;
        public int pos_y = 0;
        public int cnt = 1;

    }

    private static class MyHandler extends Handler {
        /**
         * 외부 쓰레드에서 액티비티UI변경을 위한 핸들러
         */
        private final WeakReference<LoginMainActivity> mActivity;
        //this.ob

        public MyHandler(LoginMainActivity activity) {
            mActivity = new WeakReference<LoginMainActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            this.obtainMessage();

            LoginMainActivity activity = mActivity.get();
            if (activity != null) {

                activity.mapTextView.setText(String.valueOf(msg.obj));
            }
        }
    }


    private static class UiChagneHandler extends Handler {
        /**
         * 외부 쓰레드에서 액티비티UI변경을 위한 핸들러
         */
        private final WeakReference<LoginMainActivity> mActivity;
        //this.ob

        public UiChagneHandler(LoginMainActivity activity) {
            mActivity = new WeakReference<LoginMainActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            this.obtainMessage();

            LoginMainActivity activity = mActivity.get();
            if (activity != null) {
                if (msg.arg1 == 1) {
                    activity.locationIconOn.setVisibility(View.VISIBLE);
                    activity.locationIconOff.setVisibility(View.INVISIBLE);
                } else {
                }

            }
        }
    }


    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    public void onSensorChanged(SensorEvent event) {
        float var0 = event.values[0];
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            // 가속 센서가 전달한 데이터인 경우
            // 수치 데이터를 복사한다.
            m_acc_data = event.values.clone();
        } else if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            // 자기장 센서가 전달한 데이터인 경우
            // 수치 데이터를 복사한다.
            m_mag_data = event.values.clone();
        }

        // 데이터가 존재하는 경우
        if (m_acc_data != null && m_mag_data != null) {
            SensorManager.getRotationMatrix(m_rotation, null, m_acc_data, m_mag_data);
            SensorManager.getOrientation(m_rotation, m_result_data);

            String str;
            m_result_data[0] = (float) Math.toDegrees(m_result_data[0]);

            if (m_result_data[0] < 0) m_result_data[0] += 360;

            str = "azimuth(z) : " + (int) m_result_data[0];

        }

        if (event.sensor.getType() == Sensor.TYPE_PRESSURE) {
            presuureValue = var0;
            //altituteTextView.setText(String.valueOf(var0));
            //mapTextView.setText(String.valueOf(var0));
        }
    }

    private int getDirection() {
        /**
         * 0~360도를 4개의 범위로 나누어서
         * 1~4의 정수 값으로 반환
         */
        int azimuth = (int) m_result_data[0];
        int direction;

        if (((315 <= azimuth) && (azimuth < 360)) && ((0 <= azimuth) && (azimuth < 45))) {
            direction = 1;
        } else if ((45 <= azimuth) && (azimuth < 135)) {
            direction = 2;
        } else if ((135 <= azimuth) && (azimuth < 225)) {
            direction = 3;
        } else if ((225 <= azimuth) && (azimuth < 315)) {
            direction = 4;
        } else direction = -1;


        return direction;
    }


    private double calDistance(int cPosX, int pPosX, int cPosY, int pPosY) {
        /**
         * 상동하나 두 지점의 거리를 구하는 함수
         */
        double dist;
        dist = Math.sqrt(Math.pow(pPosX - cPosX, 2) + Math.pow(pPosY - cPosY, 2));
        return dist;
    }

    private double calDistance(int cPosX, int cPosY) {
        /**
         * 상동하나 두 지점의 거리를 구하는 함수
         */
        double dist;
        dist = Math.sqrt(Math.pow(cPosX, 2) + Math.pow(cPosY, 2));
        return dist;
    }


    public static int f_direct(int x1, int y1, int x2, int y2) {        ////이전전,이전 좌표를 통해 방향성 구성

        int lx1, ly1;
        int direct_point = 0;
        lx1 = x2 - x1;
        ly1 = y2 - y1;

        if (lx1 > 0) {
            if (ly1 > 0) {
                direct_point = 1; // 오른쪽 위
            } else if (ly1 == 0) {
                direct_point = 2; // 오른쪽
            } else if (ly1 < 0) {
                direct_point = 3; // 오른쪽 밑
            }
        } else if (lx1 == 0) {
            if (ly1 < 0) {
                direct_point = 4;  // 밑
            } else if (ly1 == 0) {
                //direct_point=9;// (0,0) 그대로 다시 측정
            } else if (ly1 > 0)
                direct_point = 8;  // 위
        } else if (lx1 < 0) {
            if (ly1 > 0) {
                direct_point = 7;  // 왼쪽 위
            } else if (ly1 == 0) {
                direct_point = 6;  // 왼쪽
            } else if (ly1 < 0) {
                direct_point = 5;  // 왼쪽 밑
            }
        }

        return direct_point;
    }

    public static int s_direct(int x1, int y1, int x2, int y2, int x3, int y3, int direct_point) {   //이전전,이전, 현재 좌표 들의 각도 구하여 방향성

        double lx1, ly1, lx2, ly2;
        lx1 = x2 - x1;                                //두점을 이은 직선의 길이 구하기
        ly1 = y2 - y1;
        lx2 = x2 - x3;
        ly2 = y2 - y3;

        double inner = (lx1 * lx2 + ly1 * ly2);  //내적

        double i1 = Math.sqrt(lx1 * lx1 + ly1 * ly1);   //벡터 정규화
        double i2 = Math.sqrt(lx2 * lx2 + ly2 * ly2);   //벡터 정규화

        lx1 = (lx1 / i1);                           //단위 벡터
        ly1 = (ly1 / i1);
        lx2 = (lx2 / i2);
        ly2 = (ly2 / i2);

        inner = (lx1 * lx2 + ly1 * ly2);  //내적 다시 구하기

        double result = Math.acos(inner) * 180 / (Math.PI);   //각도 구하기

        if (lx1 > lx2) result = 360 - result;  //360도 까지 표현

        if (result >= 360) result = result - 360; //360도 이상일 시 각도를 재구성

        if (result >= 0 && result <= 30) direct_point = direct_point + 4;                //방향 재구성
        else if (result > 30 && result <= 60) direct_point = direct_point - 1;
        else if (result > 60 && result <= 105) direct_point = direct_point - 2;
        else if (result > 105 && result <= 150) direct_point = direct_point - 3;
        else if (result > 150 && result <= 195) ;
        else if (result > 195 && result <= 240) direct_point = direct_point + 3;
        else if (result > 240 && result <= 285) direct_point = direct_point + 2;
        else if (result > 285 && result <= 330) direct_point = direct_point + 1;
        else if (result > 330 && result <= 360) direct_point = direct_point - 4;


        if (direct_point <= 0) direct_point = direct_point + 8;                    //8방향 설정
        else if (direct_point > 8) direct_point = direct_point - 8;

        return direct_point;

    }

    private void positionWeightor() {
        /**
         *  각 위치들은 결과에서 얼마만큼 중복되어 나타났는지 cnt의 값을 가지고 이를 기준으로 소팅됨.
         *
         *  어느정도 까지의 결과들에서 cnt를 모두 합하여 이를 100으로 보고 각 위치마다 cnt를 기준으로
         *
         *  가중치(확률)을 곱하여 간단하게 위치를 보정.
         */

        float cntSum = 0;
        float persenTage;
        float temp[] = new float[10];
        tempPos = new PositionSet();
        for (int i = 0; i < 10; i++) {
            temp[i] = 0f;
        }

        sortResultPositions();

        try {
            Comparator<PositionSet> comparator = new Comparator<PositionSet>() {
                @Override
                public int compare(PositionSet lhs, PositionSet rhs) {
                    return (lhs.cnt > rhs.cnt ? -1 : (lhs.cnt == rhs.cnt ? 0 : 1));
                }
            };

            Collections.sort(tempposi, comparator); //중복된 결과가 많은 순(cnt값)으로 정렬

            for (int i = 0; i < tempposi.size(); i++) {
                cntSum += tempposi.get(i).cnt;

            }


            persenTage = 1 / cntSum;

            for (int i = 0; i < tempposi.size(); i++) {
                temp[i] = tempposi.get(i).cnt * persenTage;
            }


            for (int i = 0; i < tempposi.size(); i++) {
                tempPos.pos_x += (int) (((float) tempposi.get(i).pos_x * temp[i]));
            }

            for (int i = 0; i < tempposi.size(); i++) {
                tempPos.pos_y += (int) (((float) tempposi.get(i).pos_y * temp[i]));
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

    }

    private void setMyLocation(String map, int posx, int posy) {
        PhpExcute phpExcute1 = new PhpExcute();
        try {
            String result = phpExcute1.execute("http://minsdatanetwork.iptime.org:82/iptsPhp/locationService/insertlocation.php?mem_id=" + session + "&map=" + map + "&posx=" + posx + "&posy=" + posy).get();
            phpExcute1.cancel(true);
        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    class MapFinder extends Thread {
        private boolean mapRefState;
        //Message msg1 = handler.obtainMessage();
        //Message msg = handler.obtainMessage();
        //Message mapMessage = mapHandler.obtainMessage();
        String[] temp;


        public void setRunningState(boolean state) {
            mapRefState = state;


        }

        public void run() {

            //if(mapRefState) msg1.arg1 = 3;
            //else msg1.arg1 = 4;

            //handler.sendMessage(msg1);

            while (mapRefState) {
                altiMsg = new Message();
                altiMsg = uiHandler.obtainMessage();

                msg = new Message();
                msg = handler.obtainMessage();

                uiChMsg = new Message();
                uiChMsg = uiHandler.obtainMessage();

                String mapName;
                PhpExcute mPhpExcute = new PhpExcute();
                try {
                    mapName = mPhpExcute.execute("http://minsdatanetwork.iptime.org:82/iptsPhp/locationService/getMapbyPress.php?press=" + presuureValue).get();
                    temp = mapName.split(",");
                    mapName = temp[1];
                    altiMsg.arg1 = 2;
                    altiMsg.obj = temp[0];
                    uiHandler.sendMessage(altiMsg);
                    //mapMessage.arg1 = 5;
                    // mapMessage.obj = stepDetection.getstepCount();
                    // mapHandler.sendMessage(mapMessage);

                    if (!mapName.equals("") && !currentMap.equals(mapName)) {
                        isMapExist = loadIndoorMap(mapName);
                        currentMap = mapName;


                    }

                    if (!isMapExist) {

                        isLocationRef = false;
                        setRunningState(false);
                        currentMap = "";


                    }

                    if (isMapExist) {

                        msg.arg1 = 0;
                        /**
                         * currentMap 변수가 널일경우를 대비
                         *
                         */
                        try {
                            msg.obj = getMapNameText.getMapName(currentMap);
                        } catch (NullPointerException e) {
                            e.printStackTrace();
                        }

                        //handler.dispatchMessage(msg);
                        handler.sendMessage(msg);

                        uiChMsg.arg1 = 1;
                        uiHandler.sendMessage(uiChMsg);

                        if (!locationRefresh.locationRefState) {
                            locationRefresh.setRunningState(true);
                            locationRefresh.start();

                        }
                    }

                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }


            }


        }
    }


    class LocationRefresh extends Thread {
        public boolean locationRefState, needSleep=false;
        private int delayTime = 0, maxRadius = 0, maxOverCount = 0;


        public void setRunningState(boolean state) {
            locationRefState = state;
        };
        public void setSuspendMode(boolean suspend){
            this.needSleep = suspend;
        };
        public boolean getSuspendMode()
        {
            return needSleep;
        }

        public void run() {
            try {
                while (locationRefState) {
                    msg = new Message();
                    msg = handler.obtainMessage();

                    if (!isItPressSensor) {
                        uiChMsg = new Message();
                        uiChMsg = uiHandler.obtainMessage();
                        uiChMsg.arg1 = 1;
                        uiHandler.sendMessage(uiChMsg);


                        if (!preCurrentMap.equals("") && !currentMap.equals(preCurrentMap)) {
                            isMapExist = loadIndoorMap(preCurrentMap);
                            currentMap = preCurrentMap;

                        }


                        if (isMapExist) {

                            msg.arg1 = 0;
                            /**
                             * currentMap 변수가 널일경우를 대비
                             *
                             */
                            try {
                                msg.obj = getMapNameText.getMapName(currentMap);
                            } catch (NullPointerException e) {
                                e.printStackTrace();
                            }

                            //handler.dispatchMessage(msg);
                            handler.sendMessage(msg);
                        }
                    }

                    if (stepDetection.getstepCount() != 0) {
                        delayTime = 0;
                        maxRadius = 80;
                        maxOverCount = 2;
                    } else {
                        delayTime = 1000;
                        maxRadius = 10;
                        maxOverCount = 5;
                    }

                    try {

                        positionWeightor();
                        String str = "POSX : " + tempPos.pos_x + " | POSY: " + tempPos.pos_y;

                        currentPosX = tempPos.pos_x;
                        currentPosY = tempPos.pos_y;
                        currentDirection = getDirection();

                        //loadIndoorMap(currentMap);

                        if ((previousPosX == 0 && previousPosY == 0) || overPosCnt == maxOverCount || isItReverse == 1) {
                            /**
                             * (previousPosX == 0 && previousPosY == 0) : 위치참조가 어플실행후 처음일경우
                             *
                             * overPosCnt : 이전위치 값에 비해 현재위치 값이 현저히 차이가 날경우의 횟수
                             *              현저한 위치의 변화가 두번이상 될경우 그 곳으로 위치 변경
                             *
                             * isItReverse : pPrevious위치 -> previous위치 -> current위치 의 이동변화에서
                             *              갑작스런 위치 변화가 있을경우 위치를 옮기지 않고 이 값을증가
                             *              두번째부터는 위치를 변경
                             */
                            locationOverlay.setPosition(new PointF(currentPosX, currentPosY));
                            //locationOveray의 위치를 변경

                            pPreviousPosX = previousPosX;
                            pPreviousPosY = previousPosY;
                            pPreviousDirection = previousDirection;
                            //previousPos를 pPreviousPos에 저장

                            previousPosX = currentPosX;
                            previousPosY = currentPosY;
                            previousDirection = currentDirection;
                            //currentPos를 previousPos에 저장

                            overPosCnt = 0;
                            isItReverse = 0;

                            mapView.refresh();
                            //맵뷰 갱신
                        } else {

                            /**
                             *  지도의 (0,0)에서부터 현재위치와 지난 위치들의 거리를 측정하고, 갑작스런 위치
                             *  변동시에 적용.
                             */
                            boolean flag = false;
                            double ppDist = calDistance(pPreviousPosX, pPreviousPosY);
                            double pDist = calDistance(previousPosX, previousPosY);
                            double cDist = calDistance(currentPosX, currentPosY);

                            if (ppDist <= pDist) {
                                if (pDist <= cDist) flag = true;
                                else flag = false;
                            } else {
                                if (pDist > cDist) flag = true;
                                else flag = false;
                            }


                            if (((pPreviousDirection == previousDirection) && (previousDirection == currentDirection)) && (flag)) {

                                double distance = calDistance(previousPosX, currentPosX, previousPosY, currentPosY);


                                //현재 위치와 이전위치의 거리를 계산

                                if ((distance < maxRadius) && (previousDirection == pPreviousDirection) && (previousDirection == currentDirection)) {
                                    /**
                                     *  위치변화가 100픽셀 미만 일경우에만 위치를 변동시키고 그보다 클경우 예외상황으로 overPosCnt값 증가.
                                     */

                                    tempPosX = previousPosX;
                                    tempPosY = previousPosY;

                                    while (true) {

                                        /**
                                         * tempPos에 previousPos를 저장하고, tempPos의 값을 1픽셀 씩 증가시키며
                                         * currentPos가 될때 까지 위치를 갱신
                                         * ----> 현재 위치를 표시하는 오버레이가 부드럽게 진행됨.
                                         */


                                        if (currentPosX > tempPosX) {
                                            tempPosX += 1;
                                        } else if (currentPosX < tempPosX) {
                                            tempPosX -= 1;
                                        } else {
                                            //nothing to do...
                                        }

                                        if (currentPosY > tempPosY) {
                                            tempPosY += 1;
                                        } else if (currentPosY < tempPosY) {
                                            tempPosY -= 1;
                                        } else {
                                            //nothing to do
                                        }

                                        locationOverlay.setPosition(new PointF(tempPosX, tempPosY));

                                        mapView.refresh();

                                        if (tempPosX == currentPosX && tempPosY == currentPosY) {
                                            pPreviousPosX = previousPosX;
                                            pPreviousPosY = previousPosY;
                                            pPreviousDirection = previousDirection;
                                            previousPosX = currentPosX;
                                            previousPosY = currentPosY;
                                            previousDirection = currentDirection;
                                            break;
                                        }

                                    }
                                } else {
                                    overPosCnt++;
                                    //현재위치와 이전위치의 거리가 80픽셀 이상(8 미터) 날경우
                                }
                            } else {
                                isItReverse++;
                                //이동 경로상 특이한(갑작스런) 방향전환이 생긴경우
                            }
                        }

                        /**
                         * Insert current my location to DataBase table.
                         *
                         */


                        setMyLocation(currentMap, currentPosX, currentPosY);


                    } catch (IndexOutOfBoundsException e) {
                        e.printStackTrace();
                    }

                    buttonVibe.vibrate(30); //진동

                    tempPos.pos_x = 0;
                    tempPos.pos_y = 0;


                    try {
                        Thread.sleep(delayTime);  //쓰레드 0.5초 슬립
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    tempposi.clear();   //위치들의 값을 가지고 있는 tempposi어레이 리스트를 초기화


                }

                while(needSleep)
                {

                }

            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        }
    }

    public void sortResultPositions() {

        /**
         *  php실행 결과로 가지고온 위치결과들을 JSON으로 스플릿하고
         *  중복되는 위치들의 cnt를 증가시키는 함수
         */
        String[] result;
        String mapName = "";


        getLocation = new GetLocation(LoginMainActivity.this, session, currentMap);


        try {

            jsonObject = new JSONObject(getLocation.getCurrentPosition());
            jsonArray = jsonObject.getJSONArray("results");


            for (int i = 0; i < jsonArray.length(); i++) {
                jo = jsonArray.getJSONObject(i);

                if (jo.getString("pos").equals("empty")) {
                    //textView.setText("못찾음");
                } else {

                    positionSet = new PositionSet();
                    //currentMap = jo.getString("map_id");
                    positionSet.pos = Integer.parseInt(jo.getString("pos"));
                    positionSet.pos_x = Integer.parseInt(jo.getString("pos_x"));
                    positionSet.pos_y = Integer.parseInt(jo.getString("pos_y"));

                    tempposi.add(positionSet);

                }
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }


        try {
            for (int i = 0; i < tempposi.size() - 1; i++) {
                for (int j = i + 1; j < tempposi.size(); j++) {
                    if (tempposi.get(i).pos == tempposi.get(j).pos) {
                        /**
                         * pos의 값이 같은 것이 있을경우
                         * cnt를 증가시키고 확인한 곳은 삭제
                         */
                        tempposi.get(i).cnt++; //cnt 증가
                        tempposi.remove(j); //삭제
                    }
                }
            }
        } catch (IndexOutOfBoundsException e) {
            e.printStackTrace();
        }
        // }

    }

    @Override
    public void onClick(View v) {

        buttonVibe.vibrate(20);

        switch (v.getId()) {
            case R.id.my_loc_bt:    //현재 위치 표시 버튼 클릭시 실행


                if (!isLocationRef) {
                    isLocationRef = true;
                    mapFinder = new MapFinder();
                    locationRefresh = new LocationRefresh();
                    if (isItPressSensor) {
                        mapFinder.setRunningState(true);
                        mapFinder.start();
                    } else {
                        if (preCurrentMap.equals("") || preCurrentMap == null) {
                            isLocationRef = false;
                            Toast.makeText(this, "먼저 우측 상단에서 맵을 고르세요!\n맵이 없다면 옵션->지도관리 에서 다운!", Toast.LENGTH_SHORT).show();
                        } else {
                            //loadIndoorMap(currentMap);
                            locationRefresh.setRunningState(true);
                            locationRefresh.start();
                        }
                    }

                    mapView.getOverLays().add(locationOverlay);
                    mapView.refresh();

                } else {

                    locationIconOn.setVisibility(View.INVISIBLE);
                    locationIconOff.setVisibility(View.VISIBLE);
                    isLocationRef = false;
                    mapFinder.setRunningState(false);
                    locationRefresh.setRunningState(false);

                    if (mapFinder.isAlive()) {
                        mapFinder.interrupt();
                        mapFinder = null;
                    }

                    if (locationRefresh.isAlive()) {
                        locationRefresh.interrupt();
                        locationRefresh = null;
                    }

                    try {
                        mapView.getOverLays().remove(1); //위치오버레이 삭제
                        currentPosX = 0;
                        currentPosY = 0;
                    } catch (IndexOutOfBoundsException e) {
                        e.printStackTrace();
                    }
                    mapView.refresh();
                    setMyLocation("nulll", 0, 0);

                }


                break;

            case R.id.fr_loc_bt:

                activityChangeIntent = new Intent(LoginMainActivity.this, ShowMyFriend.class);


                //친구 리스트보는 화면으로 변경
                activityChangeIntent.putExtra("session", session);
                startActivity(activityChangeIntent);
                break;

            case R.id.edit_info_bt:

                activityChangeIntent = new Intent(LoginMainActivity.this, InformationEditActivity.class);
                activityChangeIntent.putExtra("session", session);
                startActivity(activityChangeIntent);

                break;

            case R.id.del_account_bt:
                AlertDialog.Builder askAlert = new AlertDialog.Builder(this);
                askAlert.setTitle("정말 계정을 삭제할까요?");
                askAlert.setMessage("친구들의 위치를 알수없게 됩니다!");

                askAlert.setPositiveButton("네", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        showDialog();
                    }
                });
                askAlert.setNegativeButton("취소!", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                    }
                });
                askAlert.show();
                break;

            default:
                break;
        }
    }   //LoginMainActivity의 버튼들 온클릭 메소드


    public void showDialog() {
        phpExcute = new PhpExcute();
        final Context mContext = getApplicationContext();
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.custom_dialog, (ViewGroup) findViewById(R.id.layout_root));

        final EditText passwdInput = (EditText) layout.findViewById(R.id.passwd_input);

        AlertDialog.Builder askAlert = new AlertDialog.Builder(this);
        askAlert.setTitle("비밀번호 확인");
        askAlert.setMessage("본인 계정의 비밀번호를 입력해주세요.");
        askAlert.setView(layout);
        askAlert.setPositiveButton("삭제", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                try {
                    String result = phpExcute.execute("http://minsdatanetwork.iptime.org:82/iptsPhp/memberManage/unregistration.php?mem_id=" + session + "&mem_pw=" + passwdInput.getText().toString()).get();
                    if (result.equals("incorrect pw")) {
                        Toast.makeText(context, "비밀번호가 틀렸습니다.", Toast.LENGTH_SHORT).show();
                    } else if (result.equals("delete success")) {
                        Toast.makeText(context, "사용자의 모든 정보가 삭제되었습니다.\n사용해주셔서 감사합니다.", Toast.LENGTH_SHORT).show();
                        intent = new Intent(LoginMainActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    } else
                        Toast.makeText(context, "오류가 발생한거 같습니다.\n잠시뒤 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        askAlert.setNegativeButton("취소!", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

            }
        });
        AlertDialog ad = askAlert.create();
        ad.show();
    }

    protected boolean loadIndoorMap(String mapName) {

        String mapPath = "/storage/sdcard0/indoorMapData/" + mapName + ".svg";
        File mapFile = new File(mapPath);

        if (mapFile.isFile()) {

            mapView.registerMapViewListener(new SVGMapViewListener() {
                @Override
                public void onMapLoadComplete() {
                /*
                지도가 로딩된후에 설정되는 값들
                 */
                    mapView.setDrawingCacheEnabled(true);
                    mapView.setAnimationCacheEnabled(true);
                    mapView.setAlwaysDrawnWithCacheEnabled(true);
                    mapView.getController().setRotationGestureEnabled(false);   //지도 회전이 안되도록 설정
                    mapView.getController().setRotateWithTouchEventCenterEnabled(true);
                    mapView.getController().setZoomWithTouchEventCenterEnabled(true);
                    mapView.getController().setMaxZoomValue(MAX_ZOOM);  //최대 줌 배율 설정
                }

                @Override
                public void onMapLoadError() {
                    //지도 로딩에 에러가 발생했을 경우
                    showToastMessage("Map Load Error.");
                }

                @Override
                public void onGetCurrentMap(Bitmap bitmap) {

                }
            });


            mapView.loadMap(AssetsHelper.getContent(this, mapPath));    //지도 로딩 메소드
            return true;

        } else {
            Message msg = handler.obtainMessage();
            msg.arg1 = 1;
            msg.obj = "맵이 존재하지 않아요! 다운받아주세요!";
            handler.sendMessage(msg);
            return false;
        }


    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
        m_sensor_manager.registerListener(this, m_acc_sensor, SensorManager.SENSOR_DELAY_UI);
        m_sensor_manager.registerListener(this, m_mag_sensor, SensorManager.SENSOR_DELAY_UI);
        m_sensor_manager.registerListener(this, preSensor, SensorManager.SENSOR_DELAY_NORMAL); // �з�
        stepDetection.load();

        try {
            if (locationRefresh.getSuspendMode()) {
                locationRefresh.setSuspendMode(false);
            }
        }catch(NullPointerException e)
        {
            e.printStackTrace();
        }


    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
        m_sensor_manager.unregisterListener(this);
        try {
            if(!locationRefresh.getSuspendMode())
            {
                locationRefresh.setSuspendMode(true);
            }
            stepDetection.unload();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }




    }


    @Override
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        //GCMRegistrar.unregister(this);
        mapView.onDestroy();
        try {
            if (mapFinder.isAlive()) {
                mapFinder.setRunningState(false);
            }
            if (locationRefresh.isAlive()) {
                locationRefresh.setRunningState(false);
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        m_sensor_manager.unregisterListener(this);
        stopService(service);
        try {
            stepDetection.unload();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }


        logoutOperation(1);


    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

        alertDialogBuilder.setTitle("프로그램 종료 확인");

        alertDialogBuilder.setMessage("정말 종료할까요??!").setCancelable(false).setPositiveButton("종료",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        LoginMainActivity.this.finish();
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
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login_main, menu);
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

            boolean checkAutoLogin;
            SharedPreferences pref = getSharedPreferences("loginInfo", MODE_PRIVATE);
            checkAutoLogin = pref.getBoolean("isItAutoLogin", false);
            if (checkAutoLogin == true) {

                removeAutoLoginInfo();
                showToastMessage("로그인 정보가 삭제되었어요!~");
            } else {
                showToastMessage("설정 되어 있지 않아요~");
            }
            return true;
        }

        if (id == R.id.logOut) {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

            alertDialogBuilder.setTitle("로그아웃 확인 확인");

            alertDialogBuilder.setMessage("정말 로그아웃 할까요?").setCancelable(false).setPositiveButton("네",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //GCM 언레지 등록
                            logoutOperation(0);
                            removeLoginInfo();
                            intent = new Intent(LoginMainActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    }).setNegativeButton("아니욥",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        }

        if (id == R.id.manageMap) {
            Intent intent = new Intent(LoginMainActivity.this, MapListActivity.class);
            startActivity(intent);

        }
        return super.onOptionsItemSelected(item);
    }

    private void removeLoginInfo() {
        SharedPreferences pref = getSharedPreferences("loginInfo", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.remove("isItAutoLogin");
        editor.remove("mem_id");
        editor.remove("mem_pw");

        editor.commit();
    }

    private void showToastMessage(String str) {
        Toast.makeText(LoginMainActivity.this, str, Toast.LENGTH_SHORT).show();
        //토스트 출력하는 함수
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


    private void logoutOperation(int mode) {

        String tempResult = new String();
        boolean temp;
        phpExcute = new PhpExcute();
        //Intent intent;
        if (mode == 1) {
            SharedPreferences pref = getSharedPreferences("loginInfo", MODE_PRIVATE);
            if (pref.getBoolean("isItAutoLogin", false)) {
                mode = 1;
            } else mode = 0;
        }

        try {
            tempResult = phpExcute.execute("http://minsdatanetwork.iptime.org:82/iptsPhp/memberManage/change_state_to_off.php?id=" + session + "&mode=" + mode).get();
            phpExcute.cancel(true);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}