package ice.rtk.Utils;

import android.content.Context;
import android.os.Looper;
import android.widget.Toast;

/**
 * Created by dengyangkang on 2017/5/10.
 * 上线后 将 TOASTLEVEL 改为2
 */
public class ToastUtil {
    public static String TAG="ToastUtil";
    public static final int TOASTNOSHOW =1;
    public static final int TOASTLEVEL=0;
    //长
    public static void Long(Context context,String msg){
        if(TOASTLEVEL<=TOASTNOSHOW){
            if(DistenguishMainThread()){
                Toast.makeText(context,msg,Toast.LENGTH_LONG).show();
            }
               return ;
        }
    }
    public static boolean DistenguishMainThread(){
        return Looper.myLooper() ==Looper.getMainLooper();
    }
    //短
    public static void Short(Context context,String msg){
        if(TOASTLEVEL<=TOASTNOSHOW){
            if(DistenguishMainThread()) {
                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
            }
        }
    }
}
