package yu_cse.graduation_project_edit.location;

import android.os.SystemClock;
import java.util.ArrayList;
import yu_cse.graduation_project_edit.activities.MainActivity;
import yu_cse.graduation_project_edit.util.LowPassFilter;

/**
 * Created by gyeunguckmin on 9/25/15.
 */
public class FilterSignals {

    ScanSignals scanSignals;
    BssidLevelSet[][] bssidLevelSet;
    BssidLevelSet[] tempBssidLevelSet;
    BssidLevelSet temp;
    BssidLevelSet filteredSet;
    ArrayList<MacRssiSet> macRssiSet;

    LowPassFilter lowPassFilter;

    public BssidLevelSet[] setTempResult() {
        lowPassFilter = new LowPassFilter();

        bssidLevelSet = new BssidLevelSet[10][10];
        tempBssidLevelSet = new BssidLevelSet[10];
        temp = new BssidLevelSet();
        filteredSet = new BssidLevelSet();

        macRssiSet = new ArrayList<>();

        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                bssidLevelSet[i][j] = new BssidLevelSet(); //BSSID & LEVEL(RSSI)세트 클래스 객체 배열[10][10] 초기화
            }
            tempBssidLevelSet[i] = new BssidLevelSet();
        }

        scanSignals = new ScanSignals(MainActivity.getContext());

        for (int i = 0; i < 10; i++) {
            scanSignals.sortScanResult();

            for (int j = 0; j < scanSignals.getScanedResults().size(); j++) {
                bssidLevelSet[i][j].BSSID = scanSignals.getScanedResults().get(j).BSSID;
                bssidLevelSet[i][j].SSID = scanSignals.getScanedResults().get(j).SSID;
                bssidLevelSet[i][j].LEVEL = scanSignals.getScanedResults().get(j).level;
            }
            SystemClock.sleep(50); //약 0.2 초 동안 슬립
        }
        scanSignals = null;


        try {


            for (int i = 0; i < 10; i++) {

                for (int j = 0; j < 10; j++) {

                    boolean exsit = false;

                    if(bssidLevelSet[i][j].BSSID != null) {

                        for (int k = 0; k < macRssiSet.size(); k++) {

                            if (bssidLevelSet[i][j].BSSID.equals(macRssiSet.get(k).getMacAddr())) {
                                macRssiSet.get(k).setRssi(bssidLevelSet[i][j].LEVEL);
                                exsit = true;
                                break;
                            }
                        }

                        if (!exsit) {
                            MacRssiSet temp = new MacRssiSet();
                            temp.setMacAddr(bssidLevelSet[i][j].BSSID);
                            temp.setRssi(bssidLevelSet[i][j].LEVEL);
                            macRssiSet.add(temp);
                        }
                    }

                }

            }

            for (int i = 0; i < macRssiSet.size(); i++) {
                tempBssidLevelSet[i].BSSID = macRssiSet.get(i).getMacAddr();
                tempBssidLevelSet[i].LEVEL = lowPassFilter.filteredValue(macRssiSet.get(i).rssi);
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        bssidLevelSet = null;
        temp = null;
        filteredSet = null;
        macRssiSet = null;

        return tempBssidLevelSet;
    }

}
