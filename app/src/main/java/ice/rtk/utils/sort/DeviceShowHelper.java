package ice.rtk.utils.sort;

/**
 * Created by dengyangkang on 2017/9/2.
 */

public class DeviceShowHelper {
    private String Dname;
    private String Daddress;
    private int rssi;
    public DeviceShowHelper(String name, String Daddres, int rss) {
        this.Dname = name;
        this.Daddress = Daddres;
        this.rssi = rss;
    }

    public String getName() {
        return this.Dname;
    }

    public String getAddress() {
        return this.Daddress;
    }
    public int getRssi() {
        return this.rssi;
    }
}