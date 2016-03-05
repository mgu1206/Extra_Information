package yu_cse.graduation_project_edit.location;

import android.content.Context;
import android.net.wifi.ScanResult;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import yu_cse.graduation_project_edit.wifi_util.WifiScan;

/**
 * Created by gyeunguckmin on 9/23/15.
 */
public class ScanSignals {
    WifiScan wifiScan;

    protected ArrayList<ScanResult> scanedResults;

    public ScanSignals(Context context)  //기본 생성자
    {
        wifiScan = new WifiScan(context); //와이파이 신호 스캔 클래스 객체 생성
    }

    public void sortScanResult() //신호 스캔 결과를 level(RSSI)값을 기준으로 정렬
    {
        wifiScan.scanWifiSignal(); //wifiScan객체의 와이파이 스캔 메소드 실행
        scanedResults = new ArrayList<ScanResult>(wifiScan.getScanResults());

        Comparator<ScanResult> comparator = new Comparator<ScanResult>() {
            @Override
            public int compare(ScanResult lhs, ScanResult rhs) {
                return (lhs.level > rhs.level ? -1 : (lhs.level == rhs.level ? 0 : 1));
            }
        };

        Collections.sort(scanedResults, comparator);    //스캔 결과(scanedResults)를 신호세기가 센 기준으로 정렬

        if (scanedResults.size() > 10) {
            int resultSize = scanedResults.size();
            for (int index = resultSize - 1; ; index--) {
                scanedResults.remove(index);
                if (scanedResults.size() == 10) break;
            }   //결과를 신호가 센 상위 10개만을 남기고 나머지 결과는 삭제
        }

        for(int i = scanedResults.size()-1;;i--)
        {
            if(scanedResults.get(i).level <= -61) scanedResults.remove(i);
            if(i==0) break;
        }
    }

    public ArrayList<ScanResult> getScanedResults() {
        return scanedResults;   //정렬된 ArrayList<ScanResult>를 반환
    }
}
