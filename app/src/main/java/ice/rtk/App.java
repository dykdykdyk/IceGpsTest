package ice.rtk;

import android.app.Application;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;

import ice.rtk.ble.BleHelper;
import ice.rtk.model.DaoMaster;
import ice.rtk.model.DaoSession;

/**
 * Created by Administrator on 2018/1/15.
 */

public class App extends Application{
    private static App app;
    private DaoMaster.DevOpenHelper devOpenHelper;
    private SQLiteDatabase db;
    private DaoMaster daoMaster;
    private DaoSession daoSession;
//    private String serviceUUID = "0000f1f0-0000-1000-8000-00805f9b34fb";
//    private String writeUUID = "0000f1f1-0000-1000-8000-00805f9b34fb";
//    private String notifyUUID = "0000f1f2-0000-1000-8000-00805f9b34fb";

    private String serviceUUID = "0000ffe0-0000-1000-8000-00805f9b34fb";
    private String writeUUID = "0000ffe1-0000-1000-8000-00805f9b34fb";
    private String notifyUUID = "0000ffe1-0000-1000-8000-00805f9b34fb";
    Handler handler;
    @Override
    public void onCreate() {
        super.onCreate();
        app = this;
        setDatabase();
        BleHelper.init(notifyUUID, writeUUID, serviceUUID, this);
        handler = new Handler();
    }

    public static App getInstance() {
        return app;
    }
    private void setDatabase() {
        devOpenHelper = new DaoMaster.DevOpenHelper(this, "mapdata-db", null);
        db = devOpenHelper.getWritableDatabase();
        daoMaster = new DaoMaster(db);
        daoSession = daoMaster.newSession();
    }
    public DaoSession getDaoSession() {
        return daoSession;
    }
    public void post(Runnable runnable) {
        handler.post(runnable);
    }
}
