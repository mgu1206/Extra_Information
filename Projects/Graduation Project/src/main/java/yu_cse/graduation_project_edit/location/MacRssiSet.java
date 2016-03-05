package yu_cse.graduation_project_edit.location;

import java.util.ArrayList;

/**
 * Created by gyeunguckmin on 11/23/15.
 */
public class MacRssiSet {
    String macAddr;
    ArrayList<Float> rssi;

    public MacRssiSet()
    {
        this.macAddr = new String();
        this.rssi = new ArrayList<>();
    }

    public void setMacAddr(String mac)
    {
        this.macAddr = mac;
    }

    public void setRssi(float rssi)
    {
        this.rssi.add(rssi);
    }

    public String getMacAddr()
    {
        return this.macAddr;
    }

    public float getRssi(int index)
    {
        return this.rssi.get(index);
    }


}
