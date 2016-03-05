package yu_cse.graduation_project_edit.location;

import android.content.Context;
import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import yu_cse.graduation_project_edit.util.PhpExcute;


/**
 * Created by gyeunguckmin on 10/13/15.
 */
public class GetLocation {

    private JSONObject jsonObject, jo;
    private JSONArray jsonArray;
    private String session;

    public String predictLocations, map;

    FilterSignals filterSignals;
    BssidLevelSet[] bssidLevelSet;

    Context context;

    public GetLocation(Context context, String session, String map)
    {
        //생성자
        this.context = context;
        this.session = session;
        this.map = map;
    }

    public String getCurrentPosition()
    {
        //positionSet = new PositionSet[10];
        bssidLevelSet = new BssidLevelSet[10];
        filterSignals = new FilterSignals();
        bssidLevelSet = filterSignals.setTempResult();
        PhpExcute getPredictPositions = new PhpExcute();
        try {
            /**
             * 받아온 10개의 AP에 대한 데이터(BSSID, LEVEL)들을 php로 전송
             */
            
            String sql = "http://minsdatanetwork.iptime.org:82/iptsPhp/locationService/getLocation.php?mem_id="+session+"&map="+map+"&mac1=" +
                    bssidLevelSet[0].BSSID+"&rssi1="+bssidLevelSet[0].LEVEL+"&mac2="+bssidLevelSet[1].BSSID+
                    "&rssi2="+bssidLevelSet[1].LEVEL +"&mac3="+bssidLevelSet[2].BSSID+"&rssi3"+bssidLevelSet[2].LEVEL +
                    "&mac4="+bssidLevelSet[3].BSSID+"&rssi4="+bssidLevelSet[3].LEVEL +"&mac5="+bssidLevelSet[4].BSSID+
                    "&rssi5="+bssidLevelSet[4].LEVEL +"&mac6="+bssidLevelSet[5].BSSID+ "&rssi6="+bssidLevelSet[5].LEVEL +
                    "&mac7="+bssidLevelSet[6].BSSID+ "&rssi7="+bssidLevelSet[6].LEVEL +"&mac8="+bssidLevelSet[7].BSSID+
                    "&rssi8="+bssidLevelSet[7].LEVEL +"&mac9="+bssidLevelSet[8].BSSID+ "&rssi9="+bssidLevelSet[8].LEVEL +
                    "&mac10="+bssidLevelSet[9].BSSID+ "&rssi10="+bssidLevelSet[9].LEVEL;

            predictLocations = getPredictPositions.execute(sql).get();
            //sql을 php로 전송 및 실행하고 결과를 predictLocation에 저장
            if(!getPredictPositions.isCancelled())
            {
                getPredictPositions.cancel(true);
            }
        }
        catch(InterruptedException e)
        {
            e.printStackTrace();
        }
        catch(ExecutionException e)
        {
            e.printStackTrace();
        }
        return predictLocations;
        //JSON형태의 스트링결과 값을 반환


    }






}
