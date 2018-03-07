package ice.rtk.Utils.FoundSdk;

import android.content.Context;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.qx.wz.exception.WzException;
import com.qx.wz.sdk.rtcm.RtcmSnippet;
import com.qx.wz.sdk.rtcm.WzRtcmFactory;
import com.qx.wz.sdk.rtcm.WzRtcmListener;
import com.qx.wz.sdk.rtcm.WzRtcmManager;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

import ice.rtk.Utils.LogUtil;

/**
 * Created by Administrator on 2018/1/23.
 * 千寻SDK接入工具类
 */

public class Found {
    WzRtcmManager mWzRtcmManager = null;
    //初始化File
    FileOutputStream fos;

    public Found(WzRtcmManager mWzRtcmManager, FileOutputStream fos,Context context) {
        this.mWzRtcmManager = mWzRtcmManager;
        this.fos = fos;
        mWzRtcmManager(context);
        initListener();
        initData();
    }

    /**
     * 获取WzRtcmManager
     */

    public void  mWzRtcmManager(Context context){
        String appKey = "541285";
        String appSecret = "167a2c3e5e5223d2a2519982ab70b7dafe54f5ec867dacedf180dd002b4a949c";
        String deviceId = "123";
        String deviceType = "234";
        try {
            mWzRtcmManager = WzRtcmFactory.getWzRtcmManager(context, appKey, appSecret, deviceId, deviceType, null);
        } catch (WzException e) {
            e.printStackTrace();
        }
    }

    private void initData() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        Date curDate = new Date(System.currentTimeMillis());// 获取当前时间
        String str = formatter.format(curDate);
        String SavePath = Environment.getExternalStorageDirectory() + "/icegps/";
        File saveFile =new File(SavePath);
        if(!saveFile.exists()){
            saveFile.mkdirs();
        }
        String SaveFile = Environment.getExternalStorageDirectory() + "/icegps/"+str+".log";
        try {
            fos=new FileOutputStream(SaveFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
    //获取RTCM数据
    public void initListener(){
        try {
            mWzRtcmManager.requestRtcmUpdate(new WzRtcmListener() {
                @Override
                public void onRtcmDatachanged(RtcmSnippet rtcmSnippet) {
                    Log.i("onRtcmDatachanged","rtcmSnippet.toString()"+rtcmSnippet.toString());
                    Log.i("onRtcmDatachanged","rtcmSnippet.getBuffer()"+ Arrays.toString(rtcmSnippet.getBuffer()));
                    Log.i("onRtcmDatachanged","rtcmSnippet.getCount()"+rtcmSnippet.getCount());
                    Log.i("onRtcmDatachanged","rtcmSnippet.getCreateTime()"+changetime(rtcmSnippet.getCreateTime()));
                    Log.i("onRtcmDatachanged","rtcmSnippet.getOffset()"+rtcmSnippet.getOffset());
                    saveDatatoFile(rtcmSnippet.getBuffer());
                }

                @Override
                public void onStatusChanaged(int i, String s) {
                    Log.i("onStatusChanaged","i:"+i+",s:"+s);
                }
            },22,121.5, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public String changetime(long time){
        Date date = new Date(time);
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
//        System.out.println(df.format(date));
        return df.format(date);
    }
    public void saveDatatoFile(byte[] by){
        LogUtil.d("savedata:"+Arrays.toString(by));
        if(getfile()){//如果外部存储可用
            //写数据
            try {
                if(fos !=null){
                    LogUtil.d(getClass(),"开始写入数据.");
                    fos.write(by);
                    fos.write("\r\n".toString().getBytes());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }else{
            LogUtil.d(getClass(), ": 外部存储卡不可用");
        }
    }
    public boolean getfile(){
        if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){//如果外部存储可用
            return true;
        }else{
            return false;
        }
    }

    //模拟发送数据
    /**
     * 发送GGA
     */
    public void sendData(View view){
        //示范:发送gga获取差分数据
        try {
            String gga = "$GNGGA,025243.00,3104.78554,N,12130.81349,E,2,10,0.91,23.6,M,10.0,M,3.0,0000*5A";
            mWzRtcmManager.sendGGA(gga);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取差分账号信息
     */
    public void getRtcmAccount(View view){
//示范: 获取用户差分账号信息
        Log.i("getRtcmAccount",   ""+ mWzRtcmManager.getRtcmAccount());
        Log.i("getRtcmAccount",   ""+ mWzRtcmManager.getRtcmAccount().getWzAppkey()); //获取应用appkey
        Log.i("getRtcmAccount",   ""+  mWzRtcmManager.getRtcmAccount().getWzNtripUserName()); //获取Ntrip用户名
        Log.i("getRtcmAccount",   ""+ mWzRtcmManager.getRtcmAccount().getWzNtripPassword()); //获取Ntrip用户密码
        Log.i("getRtcmAccount",   ""+ mWzRtcmManager.getRtcmAccount().getWzDeviceID()); //获取设备唯一编号
        Log.i("getRtcmAccount",   ""+  mWzRtcmManager.getRtcmAccount().getWzDeviceType()); //获取设备类型
        Log.i("getRtcmAccount",   ""+  mWzRtcmManager.getRtcmAccount().getExpireTime()); //获取差分帐号过期时间
        Log.i("getRtcmAccount",   ""+ mWzRtcmManager.getRtcmAccount().getWzServiceType()); //获取服务类型
    }
}
