package ice.rtk.utils.viewtools;

import android.app.Activity;
import android.os.SystemClock;
import android.util.Log;

import java.math.BigDecimal;
import java.util.Arrays;

import ice.rtk.R;
import ice.rtk.utils.LogUtil;
import ice.rtk.utils.viewtools.MapUtils;
import ice.rtk.utils.ThreadPool;
import ice.rtk.view.customview.LinesLoftingView;

/**
 * Created by Administrator on 2018/1/31.
 * 主要负责计算两点之间的距离,以及给MapView提供数据 和如何绘制的方法
 * 数据处理来源中心
 */

public class LinesRender {
    private final Activity activity;
    private final LinesLoftingView mapview;
    ThreadPool threadPool;
    double[] start;
    double[] end;
    double[] current;
    private final int MAX_COUNT=1000;
    public LinesRender(LinesLoftingView mapview, Activity activity){
        threadPool =ThreadPool.getInstance();
        this.activity =activity;
        this.mapview =mapview;
        threadPool =ThreadPool.getInstance();
    }
    public void SetPoints(double[] start,double[] end, double[] current) {
        this.start =start;
        this.end =end;
        this.current =current;
        calcute(start,end,current,activity);
        threadPool.execute(new simulationRun());
    }

    /**
     * 计算偏移距离 左边为正 右边为负
     * @param start
     * @param end
     * @param current
     * @param activity
     */
    public void calcute(double[] start,double[] end, double[] current, Activity activity){
        if(DataChange==null)
            return;
        //1.计算起点距离
        double startdistance = MapUtils.calculateStartDistance(start, current);
        LogUtil.e("TAG", "距离:" + startdistance);
        DataChange.Start_change(getfloatScale((float)startdistance,3)+"");
        //2.计算终点距离
        double enddistance = MapUtils.calculateStartDistance(end, current);
        DataChange.End_change(getfloatScale((float)enddistance,3)+"");
        //3.计算起点与终点之间的距离
        double distanceLength = MapUtils.calculateStartDistance(start, end);
        double MaxD=  getMaxDistance(startdistance,enddistance,distanceLength);
        mapview.initLocation((int)(MaxD*MAX_COUNT));
        Log.i("tag", "startdistance:" + startdistance + ",enddistance:" + enddistance + ",distanceLength:" + distanceLength);
        //4.计算偏差距离
        //* @return 计算偏移距离 左边为正 右边为负
        String up =activity.getResources().getString(R.string.up);//上
        String down =activity.getResources().getString(R.string.down);//下
        String left =activity.getResources().getString(R.string.left);//左
        String right =activity.getResources().getString(R.string.right);//右
        double offsetDistance = MapUtils.calculateOffsetDistance(start, end, current);
        if(offsetDistance>0){
            DataChange.Offset_Distance(left+getfloatScale((float)offsetDistance,3)+"");
        }else if(offsetDistance<0){
            DataChange.Offset_Distance(right+getfloatScale((float)offsetDistance,3)+"");
        }else {
            DataChange.Offset_Distance(getfloatScale((float)offsetDistance,3)+"");
        }
        //5.计算高差  正为上 负为下
        double highDifference = MapUtils.calculateHightOffsetDistance(start, end, current);
        Log.i("tag", ",offsetDistance:" + offsetDistance+",highDifference:"+highDifference);
        if(highDifference>0){
            DataChange.Height_change(up+getfloatScale((float)highDifference,3)+"");
        }else if(highDifference<0){
            DataChange.Height_change(down+getfloatScale((float)highDifference,3)+"");
        }else {
            DataChange.Height_change(getfloatScale((float)highDifference,3)+"");
        }

        //计算偏移距离与方向 p1与s1
        int E_W_start_end=-1,N_S_start_end=-1,E_W_start_current=-1,N_S_start_current=-1;
        double[] enu_start_end= MapUtils.calculatecoordinatedifference(start,end);
        double[] enu_start_current= MapUtils.calculatecoordinatedifference(start,current);
        LogUtil.i(getClass(),"enu_start_end："+ Arrays.toString(enu_start_end));
        LogUtil.i(getClass(),"enu_start_current："+ Arrays.toString(enu_start_current));
        //latitude(纬度-- 东西差值,经度-- 南北差值，海拔高度差) 北东正  西南负
        //东西差值
        /**
         * E_W_start_end =-1 表示0； >0 偏东方向; <0 偏西方向;
         */
        if(enu_start_end[0] ==0){
            E_W_start_end =-1;
        }else  if(enu_start_end[0] >0){
            E_W_start_end =1;
        }else  if(enu_start_end[0] <0){
            E_W_start_end =0;
        }
        /**
         * E_W_start_end =-1 表示0； >0 偏北方向; <0 偏南方向;
         */
        if(enu_start_end[1] ==0){
            N_S_start_end =-1;
        }else  if(enu_start_end[1] >0){
            N_S_start_end =1;
        }else  if(enu_start_end[1] <0){
            N_S_start_end =0;
        }
        mapview.setData_S_E( getfloatScale((float)enu_start_end[1],3)*MAX_COUNT,
                getfloatScale((float)enu_start_end[0],3)*MAX_COUNT,N_S_start_end,E_W_start_end);

        /**
         * E_W_start_end =-1 表示0； >0 偏东方向; <0 偏西方向;
         */
        if(enu_start_current[0] ==0){
            E_W_start_current =-1;
        }else  if(enu_start_current[0] >0){
            E_W_start_current =1;
        }else  if(enu_start_current[0] <0){
            E_W_start_current =0;
        }
        /**
         * E_W_start_end =-1 表示0； >0 偏北方向; <0 偏南方向;
         */
        if(enu_start_current[1] ==0){
            N_S_start_current =-1;
        }else  if(enu_start_current[1] >0){
            N_S_start_current =1;
        }else  if(enu_start_current[1] <0){
            N_S_start_current =0;
        }
        mapview.setData_S_C(getfloatScale((float)enu_start_current[1],3)*MAX_COUNT,
                getfloatScale((float)enu_start_current[0],3)*MAX_COUNT,N_S_start_current,E_W_start_current);
    }

    private double getMaxDistance(double startdistance, double enddistance, double distanceLength) {
        double result =0;
        if(startdistance>=enddistance){
            if(startdistance <distanceLength){
                result= distanceLength;
            }else
                result = startdistance;
        }else if(startdistance<enddistance){
            if(enddistance <distanceLength){
                result = distanceLength;
            }else
                result = enddistance;
        }
        return result;
    }

    class  simulationRun implements Runnable{
        int flag =10000;
        double offset =(double)1/100;
        @Override
        public void run() {
            for (int i = 0; i <flag ; i++) {
                SystemClock.sleep(100);
                current[0] =current[0]+offset*(double)1/100;
                current[1] =current[1]-offset;
                current[2] =current[2]+0.4;
                calcute(start,end,current,activity);
            }
        }
    }

    public  float getfloatScale(float a,int keepnumber){
        a =Math.abs(a);
        BigDecimal bb  =   new BigDecimal(a);
        float   f1   =  bb.setScale(keepnumber, BigDecimal.ROUND_HALF_UP).floatValue();
        return f1;
    }
    //添加对外部的接口
    public interface LinesMapDataChangeListener {
        //南北偏差数据返回接口
        void Start_change(String multiple);
        //东西偏差数据返回接口
        void End_change(String multiple);
        //高差数据返回接口
        void Height_change(String multiple);
        //偏移距离数据返回接口
        void Offset_Distance(String multiple);
    }

    LinesMapDataChangeListener DataChange;

    public void setMapDataChangeListener(LinesMapDataChangeListener onMapDataChangeListener) {
        DataChange = onMapDataChangeListener;
    }
}
