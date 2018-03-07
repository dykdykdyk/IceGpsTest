package ice.rtk.view.base;

import android.app.Activity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/1/10.
 */

public class AppManager {
    public static List<Activity> activities =new ArrayList<Activity>();
    public static void addActivity(Activity activity){
        activities.add(activity);
    }
    public static void removeActivity(Activity activity){
        activities.remove(activity);
    }
    public static void finish(){
        Activity activity = activities.get(activities.size() - 1);
        activities.remove(activity);
        activity.finish();
    }
    public static void finishAll(){
        for (Activity activity:activities) {
            if(!activity.isFinishing()){
                activity.finish();
            }
        }
    }
    /**
     * 退出应用程序
     */
    public static void AppExit() {
        try {
            finishAll();
//            ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
//            activityManager.restartPackage(context.getPackageName());
            System.exit(0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
