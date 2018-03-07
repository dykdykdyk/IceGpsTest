package ice.rtk.view.LinesView;

import java.math.BigDecimal;

import ice.rtk.Utils.LogUtil;

/**
 * Created by Administrator on 2018/1/19.
 */

public class Tools {
    public static double getDouble(double d, int saveCounts) {
        BigDecimal b = new BigDecimal(d);
        d = b.setScale(saveCounts, BigDecimal.ROUND_HALF_UP).doubleValue();
        return d;
    }

    private static int[] temp = {1, 2, 5};
    public static String[] Temp = {temp[0] + "mm", temp[1] + "mm", temp[2] + "mm"
            , temp[0] + "cm", temp[1] + "cm", temp[2] + "cm",temp[0]*10 + "cm", temp[1]*10 + "cm", temp[2]*10 + "cm"
            ,  temp[0] + "m", temp[1] + "m", temp[2] + "m",temp[0]*10 + "m", temp[1]*10 + "m", temp[2]*10 + "m" ,temp[0]*100 + "m", temp[1]*100 + "m", temp[2]*100 + "m"
            ,  temp[0] + "km", temp[1] + "km", temp[2] + "km",temp[0]*10 + "km", temp[1]*10 + "km", temp[2]*10 + "km" ,temp[0]*100 + "km", temp[1]*100 + "km", temp[2]*100 + "km"};

    /**
     *
     * @param string 根据数组计算距离
     * @return 返回单位为mm的距离int类数值
     */
    public static int changeStringtometer(String string){
        int temp =0;
        int result;
         for (int i=0;i<Temp.length;i++){
             if(string.equals(Temp[i])){
                 temp =i;
                 break;
             }
         }
         String resultS=Temp[temp];
         String str =resultS.substring(resultS.length()-2,resultS.length()-1);
         switch(str){
             case "m"://后缀 mm
                 result =Integer.parseInt(resultS.substring(0,1));
                 break;
             case "c"://后缀 cm   1cm =10mm
                 int t =Integer.parseInt(resultS.substring(0,resultS.length()-2));
                 result =t*10;
                 break;
             case "k"://后缀 km   1km =1000 000 mm  Math.pow(10,6)10的6次方
                 int t1 =Integer.parseInt(resultS.substring(0,resultS.length()-2));
                 result =t1*((int)Math.pow(10,6));
                 break;
            default://单位是 m
                int t2 =Integer.parseInt(resultS.substring(0,resultS.length()-1));
                result =t2*1000;
                break;
         }
//        Log.i("TAG","RESULT:"+result);
            return result;
    }
    public  static int getIndex(int value){
        int result = 0;
        for (int i=0;i<Temp.length;i++){
            float f =value /changeStringtometer(Temp[i]);
//            LogUtil.e("f:"+f);
            if(value>changeStringtometer(Temp[Temp.length-1])){
                return Temp.length-1;
            }
            if(f >=1.0 && f<=3){
                result =i;
                LogUtil.e("f:"+f+",result:"+result);
                break;
            }
        }
        return  result;
    }
}
