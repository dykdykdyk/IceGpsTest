package ice.rtk.view.PointView;

import android.app.Activity;
import android.os.SystemClock;

import java.math.BigDecimal;

import ice.rtk.R;
import ice.rtk.Utils.LogUtil;
import ice.rtk.Utils.MapTools.MapUtils;
import ice.rtk.Utils.ThreadPool;

/**
 * Created by Administrator on 2018/1/31.
 * 主要负责计算两点之间的距离,以及给MapView提供数据 和如何绘制的方法
 * 数据处理来源中心
 */

public class MapRender {
    private final Activity activity;
    private final MapView mapview;
    ThreadPool threadPool;
    double[] start;
    double[] current;
    private final int MAX_COUNT=1000;
    public MapRender(MapView mapview, Activity activity){
        threadPool =ThreadPool.getInstance();
        this.activity =activity;
        this.mapview =mapview;
        threadPool =ThreadPool.getInstance();
    }
    public void SetPoints(double[] start, double[] current) {
        this.start =start;
        this.current =current;
        calcute(start,current,activity);
        threadPool.execute(new simulationRun());

    }

    public void calcute(double[] start, double[] current, Activity activity){
        //1.计算起点
        //2.当前位置
        double distance = MapUtils.calculateStartDistance(start, current);
        LogUtil.e("TAG", "距离:" + distance);
        //距离
        if(DataChange==null)
            return;
        DataChange.Distance_change(getfloatScale((float) distance,3)+"");
        mapview.initLocation((int)(distance*MAX_COUNT));
        String south =activity.getResources().getString(R.string.south);//南
        String north =activity.getResources().getString(R.string.north);//北
        String west =activity.getResources().getString(R.string.west);//西
        String east =activity.getResources().getString(R.string.east);//东
        String up =activity.getResources().getString(R.string.up);//上
        String down =activity.getResources().getString(R.string.down);//下
        double[] enu=  MapUtils.calculatecoordinatedifference(start,current);
        //latitude(纬度-- 东西差值,经度-- 南北差值，海拔高度差) 北东正  西南负
        LogUtil.i(getClass(),"");
        //东西差值
        enu[0] =getfloatScale((float) enu[0],3);
        int flg_e_w =-1;
        if(enu[0]<0){//西
            DataChange.E_W_change(west+getfloatScale((float) enu[0],3));
            flg_e_w =0;
        }else  if(enu[0]>0){//东
            DataChange.E_W_change(east+getfloatScale((float) enu[0],3));
            flg_e_w =1;
        }else if(enu[0] ==0){
            DataChange.E_W_change(""+getfloatScale((float) enu[0],3));
            flg_e_w =-1;
        }
        //南北差值
        int flg_s_n =-1;
        if(enu[1]<0){//南
            DataChange.S_N_change(south+getfloatScale((float) enu[1],3));
            flg_s_n=0;
        }else  if(enu[1]>0){//北
            DataChange.S_N_change(north+getfloatScale((float) enu[1],3));
            flg_s_n=1;
        }else if(enu[1] ==0){
            DataChange.S_N_change(""+getfloatScale((float) enu[1],3));
            flg_s_n=-1;
        }
        mapview.setData(enu[1]*MAX_COUNT,enu[0]*MAX_COUNT,flg_s_n,flg_e_w);
        //高差 正为上 负为下
        double hight=start[2] -current[2];
        if(hight>0){
            DataChange.Height_change(up+getfloatScale((float) hight,3));
        }else if(hight<0){
            DataChange.Height_change(down+getfloatScale((float) hight,3));
        }

    }
    class  simulationRun implements Runnable{
        int flag =100;
        double offset =(double)1/1000;
        @Override
        public void run() {
            for (int i = 0; i <flag ; i++) {
                SystemClock.sleep(100);
                current[0] =current[0]+offset*(double)1/100;
                current[1] =current[1]+offset;
                current[2] =current[2]+0.06;
                calcute(start,current,activity);
            }
        }
    }

    public  float getfloatScale(float a,int keepnumber){
        BigDecimal bb  =   new BigDecimal(a);
        float   f1   =  bb.setScale(keepnumber, BigDecimal.ROUND_HALF_UP).floatValue();
        return f1;
    }
    //添加对外部的接口
    public interface MapDataChangeListener {
        //南北偏差数据返回接口
        void S_N_change(String multiple);
        //东西偏差数据返回接口
        void E_W_change(String multiple);
        //距离数据返回接口
        void Distance_change(String multiple);
        //高差数据返回接口
        void Height_change(String multiple);
    }

    MapDataChangeListener DataChange;

    public void setMapDataChangeListener(MapDataChangeListener onMapDataChangeListener) {
        DataChange = onMapDataChangeListener;
    }
}
