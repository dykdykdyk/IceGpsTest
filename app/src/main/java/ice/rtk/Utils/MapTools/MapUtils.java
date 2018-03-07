package ice.rtk.Utils.MapTools;

import android.util.Log;

import java.util.Arrays;

/**
 * Created by Administrator on 2018/1/11.
 */

public class MapUtils {
    /**
     *
     * @param start 开始点的坐标
     * @param current 终点的坐标
     * @return 计算两点之间的距离
     * 第一个是 latitude(纬度-- 东北差值,经度-- 南北差值，海拔高度差) 北东正  西南负
     */
    public static double calculateStartDistance(double[] start,double[] current){
         XYH xyh=new XYH();
          double[] bPos =new double[3];
          double[] bECEF =new double[3];
          double[] rPos  =new double[3];
          double[] rECEF  =new double[3];
          double[] enu  =new double[3];
          double[] vECEF  =new double[3];
        bPos[0] = (start[0]) * xyh.D2R;
        bPos[1] = (start[1]) * xyh.D2R;
        bPos[2] = (start[2]);
        Log.i("tag","latitude1:"+start[0]+",longitude1:"+start[1]+",altitude1:"+start[2]);
        bECEF = xyh.pos2ecef(bPos);
        rPos[0] = (current[0]) * xyh.D2R;
        rPos[1] = (current[1]) * xyh.D2R;
        rPos[2] = (current[2]);
        Log.i("tag","latitude2:"+current[0]+",longitude2:"+current[1]+",altitude2:"+current[2]);
        rECEF = xyh.pos2ecef(rPos);
        vECEF[0] = rECEF[0] - bECEF[0];
        vECEF[1] = rECEF[1] - bECEF[1];
        vECEF[2] = rECEF[2] - bECEF[2];
        enu = xyh.ecef2enu(bPos, vECEF);
        //第一个是 latitude(纬度-- 东北差值,经度-- 南北差值，海拔高度差) 北东正  西南 负
        double distance=Math.sqrt(enu[0] * enu[0] + enu[1] * enu[1]);
        return distance;
    }
    /**
     *
     * @param start 开始点的坐标
     * @param current 终点的坐标
     * @return 两点之间的坐标差值
     * latitude(纬度-- 东西差值,经度-- 南北差值，海拔高度差) 北东正  西南负
     */
    public static double[] calculatecoordinatedifference(double[] start,double[] current){
        XYH xyh=new XYH();
        double[] bPos =new double[3];
        double[] bECEF =new double[3];
        double[] rPos  =new double[3];
        double[] rECEF  =new double[3];
        double[] enu  =new double[3];
        double[] vECEF  =new double[3];

        bPos[0] = (start[0]) * xyh.D2R;
        bPos[1] = (start[1]) * xyh.D2R;
        bPos[2] = (start[2]);
        bECEF = xyh.pos2ecef(bPos);
        rPos[0] = (current[0]) * xyh.D2R;
        rPos[1] = (current[1]) * xyh.D2R;
        rPos[2] = (current[2]);
        rECEF = xyh.pos2ecef(rPos);
        vECEF[0] = rECEF[0] - bECEF[0];
        vECEF[1] = rECEF[1] - bECEF[1];
        vECEF[2] = rECEF[2] - bECEF[2];
        enu = xyh.ecef2enu(bPos, vECEF);
        return enu;
    }

    /**
     *
     * @param p1 起始点
     * @param p2 终点
     * @param s 当前位置
     * @return 计算偏移距离 左边为正 右边为负
     * latitude(纬度-- 东西差值,经度-- 南北差值，海拔高度差) 北东正  西南负
     */
    public static double calculateOffsetDistance(double[] p1,double[] p2,double[] s){
        //1.向量u
        double[] u = MapUtils.calculatecoordinatedifference(p1,s);
        Log.i("LOG1", Arrays.toString(u));
        //2.向量v
        double[] v = MapUtils.calculatecoordinatedifference(p2,s);
        Log.i("LOG2", Arrays.toString(v));
        double offsetDistance =calculateOffsetDistance(u,v);
        return offsetDistance;
    }

    /**
     *
     * @param p1 起始点
     * @param p2 终点
     * @param s 当前位置
     * @return 计算高差  上 为正 下为负
     */
    public static double calculateHightOffsetDistance(double[] p1,double[] p2,double[] s){
        //1.向量u
        double[] u = calculatecoordinatedifference(p1,s);
        //2.向量v
        double[] v = calculatecoordinatedifference(p2,s);
        double distanceLength =calculateStartDistance(p1,p2);//距离
        double p2c =(p2[2] -p1[2])/distanceLength;//梯度
        double p2h = p2c*calculateDotDistance(u,v);
        //高差
        double hight = s[2]-p2h;
        return hight;
    }
    /**
     * 计算偏差 叉乘(sin)
     * 当前位置到航线之间的偏移距离
     * @param u
     * @param v
     * @return
     */
    public static double calculateOffsetDistance(double[] u,double[] v){
        //1.(x1y2-x2y1)/Math.sqrt(x2 * x2 + y2 * y2);
        if(v[0]*v[0]+v[1]*v[1] ==0){
            return 0;
        }
         double temp =(u[0]*v[1] -u[1]*v[0])/Math.sqrt(v[0]*v[0]+v[1]*v[1]);
        return temp;
    }
    /**
     * 计算偏差 点乘(cos)
     * :当前位置到航线之间的偏移距离
     * @param u
     * @param v
     * @return
     */
    public static double calculateDotDistance(double[] u,double[] v){
        //1.(x1y2-x2y1)/Math.sqrt(x2 * x2 + y2 * y2);
        if(v[0]*v[0]+v[1]*v[1] ==0){
            return 0;
        }
        double temp =(u[0]*v[0]+u[1]*v[1])/Math.sqrt(v[0]*v[0]+v[1]*v[1]);
        return temp;
    }
}
