package ice.rtk.view.coordinate_data;

import java.io.Serializable;

/**
 * Created by Administrator on 2018/1/24.
 */

public class Point_List implements Serializable {
    private String lat;
    private String lon;
    private String ele;
    private String mileage;//里程
    private String discript;

    public Point_List(String lat, String lon, String ele, String mileage, String discript) {
        this.lat = lat;
        this.lon = lon;
        this.ele = ele;
        this.mileage = mileage;
        this.discript = discript;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLon() {
        return lon;
    }

    public void setLon(String lon) {
        this.lon = lon;
    }

    public String getEle() {
        return ele;
    }

    public void setEle(String ele) {
        this.ele = ele;
    }

    public String getMileage() {
        return mileage;
    }

    public void setMileage(String mileage) {
        this.mileage = mileage;
    }

    public String getDiscript() {
        return discript;
    }

    public void setDiscript(String discript) {
        this.discript = discript;
    }

    @Override
    public String toString() {
        return "Data_Bean{" +
                "lat='" + lat + '\'' +
                ", lon='" + lon + '\'' +
                ", ele='" + ele + '\'' +
                ", mileage='" + mileage + '\'' +
                ", discript='" + discript + '\'' +
                '}';
    }
}
