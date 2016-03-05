package yu_cse.graduation_project_edit.util;


import java.util.ArrayList;

/**
 * Created by gyeunguckmin on 11/23/15.
 */
public class LowPassFilter {
    float alpha = (float) 0.7;      //가중치
    float reave = 0;      //이전 평균값


    public float filteredValue(ArrayList<Float> rssi) {
        float filteredValue = 0;
        int count = rssi.size();

        for (int i = 0; i < count; i++) {
            if (reave == 0) {
                filteredValue = rssi.get(i);
            } else {
                filteredValue = alpha * reave + (1 - alpha) * rssi.get(i);
            }
            reave = filteredValue;
        }
        return filteredValue;

    }
}