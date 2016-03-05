package yu_cse.graduation_project_edit.wifi_util;

import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by gyeunguckmin on 9/23/15.
 */
public class WifiScan {

    //Context wifiContext;

    private WifiManager wifiManager;
    private WifiReceiver wIfiReceiver;
    public List<ScanResult> tempScanResults;
    public ArrayList<ScanResult> scanResults;

    public WifiScan(Context wifiContext)    //생성자
    {
        //this.wifiContext = wifiContext;
        wifiManager = (WifiManager) wifiContext.getSystemService(wifiContext.WIFI_SERVICE);
        wIfiReceiver = new WifiReceiver();  //와이파이 리시버 객체 생성
    }

    public void scanWifiSignal()    //와이파이 신호 스캔 시작 함수
    {
        wifiManager.startScan();
        tempScanResults = wifiManager.getScanResults(); //스캔 결과를 List<ScanResult> tempScanResults에 저장
    }

    public boolean isWifiOn()   //현재 기기의 WiFi가 켜져있는 상태인지 아닌지를 boolean으로 반환
    {
        return wifiManager.isWifiEnabled();
    }

    class WifiReceiver extends BroadcastReceiver {
        public List<ScanResult> getResults() {
            return scanResults;
        }

        public WifiManager getManager() {
            return wifiManager;
        }

        @Override
        public void onReceive(Context c, Intent intent) {
            tempScanResults = wifiManager.getScanResults();
        }
    }

    public ArrayList<ScanResult> getScanResults()   //List<ScanResult> 형태로 와이파이 스캔 결과를 반환
    {
        scanResults = new ArrayList<ScanResult>(tempScanResults);   //List형태의 결과를 ArrayList로 변환
        return scanResults; //결과를 반환
    }
}