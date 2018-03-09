package ice.rtk.ble;

import android.app.Application;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.text.TextUtils;
import android.util.Log;

import com.clj.fastble.BleManager;
import com.clj.fastble.callback.BleGattCallback;
import com.clj.fastble.callback.BleNotifyCallback;
import com.clj.fastble.callback.BleScanCallback;
import com.clj.fastble.callback.BleWriteCallback;
import com.clj.fastble.data.BleDevice;
import com.clj.fastble.exception.BleException;
import com.clj.fastble.scan.BleScanRuleConfig;

import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

import ice.rtk.App;


/**
 * Created by 111 on 2018/1/15.
 */

public class BleHelper {
    private static BleHelper bleHelper;
    private final BleManager bleManager;
    private final BleStatusCallbackManager bleCallbackManager;
    private static String NOTIFY_UUID;
    private static String WHITE_UUID;
    private static String SERVICE_UUID;
    private static boolean isInit;
    private static final String TAG = BleDevice.class.getName();
    private LinkedBlockingQueue<byte[]> queue;
    private BleDevice currentConnectBleDevice;
    private static Application appContext;

    public static BleHelper getInstance() {
        if (bleHelper == null) {
            synchronized (BleHelper.class) {
                if (bleHelper == null) {
                    bleHelper = new BleHelper();
                }
            }
        }
        return bleHelper;
    }

    public static void init(String notifyUUID, String whiteUUID, String seviceUUID, Application application) {
        if (TextUtils.isEmpty(notifyUUID) || TextUtils.isEmpty(whiteUUID) || TextUtils.isEmpty(seviceUUID) || application == null) {
            return;
        }
        appContext = application;
        NOTIFY_UUID = notifyUUID;
        WHITE_UUID = whiteUUID;
        SERVICE_UUID = seviceUUID;
        isInit = true;
    }


    private BleHelper() {
        bleManager = BleManager.getInstance();
        bleManager.init(appContext);
        initScanRule();
        bleCallbackManager = BleStatusCallbackManager.getInstance();
        queue = new LinkedBlockingQueue<>(1000);
        IntentFilter filter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        appContext.registerReceiver(broadcastReceiver, filter);
        new WriteThread().start();
    }

    private void initScanRule() {
        BleScanRuleConfig scanRuleConfig = new BleScanRuleConfig.Builder()
//                .setServiceUuids()      // 只扫描指定的服务的设备，可选
//                .setDeviceName(true, names)         // 只扫描指定广播名的设备，可选
//                .setDeviceMac(mac)                  // 只扫描指定mac的设备，可选
//                .setAutoConnect(isAutoConnect)      // 连接时的autoConnect参数，可选，默认false
                .setScanTimeOut(0)              // 扫描超时时间，可选，默认10秒；小于等于0表示不限制扫描时间
                .build();
        bleManager.initScanRule(scanRuleConfig);
    }

    public void addBleStatusCallback(Callback callback) {
        bleCallbackManager.addBleStatusCallback(callback);
    }

    public void removeBleStatusCallback(Callback callback) {
        bleCallbackManager.removeBleStatusCallback(callback);
    }

    public void scan() {
        if (!isInit) {
            showLog("BleHelper No initialization ");
            return;
        }
        bleManager.scan(bleScanCallback);
    }

    public void cancelScan() {
        bleManager.cancelScan();
    }

    public void connect(BleDevice bleDevice) {
        if (!isInit) {
            showLog("BleHelper No initialization ");
            return;
        }
        bleManager.connect(bleDevice, bleGattCallback);
    }

    public void connect(String mac) {
        if (!isInit) {
            showLog("BleHelper No initialization ");
            return;
        }
        BluetoothDevice bluetoothDevice = BluetoothAdapter.getDefaultAdapter().getRemoteDevice(mac);
        BleDevice bleDevice = bleManager.convertBleDevice(bluetoothDevice);
        bleManager.connect(bleDevice, bleGattCallback);
    }

    public void write(byte[] data) {
        if (!isInit) {
            showLog("BleHelper No initialization");
            return;
        }
        try {
            queue.put(data);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void openNotify(BleDevice bleDevice) {
        if (!isInit) {
            showLog("BleHelper No initialization ");
            return;
        }
        bleManager.notify(bleDevice, SERVICE_UUID, NOTIFY_UUID, bleNotifyCallback);
    }

    public void disconnect(BleDevice bleDevice) {
        bleManager.disconnect(bleDevice);
    }

    public void disconnectAllDevice() {
        bleManager.disconnectAllDevice();
    }

    private void destroy() {
        bleManager.destroy();
    }

    public void openBle() {
        bleManager.enableBluetooth();
    }

    public void closeBle() {
        bleManager.disableBluetooth();
    }

    class WriteThread extends Thread {
        @Override
        public void run() {
            super.run();
            while (true) {
                try {
                    final byte[] data = queue.take();
                    App.getInstance().post(new Runnable() {
                        @Override
                        public void run() {
                            bleManager.write(currentConnectBleDevice, SERVICE_UUID, WHITE_UUID, data, bleWriteCallback);
                        }
                    });
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    int temp =0;
    BleWriteCallback bleWriteCallback = new BleWriteCallback() {
        @Override
        public void onWriteSuccess() {
            temp++;
            showLog("onWriteSuccess:次数"+temp);
            App.getInstance().post(new Runnable() {
                @Override
                public void run() {
                    bleCallbackManager.onWriteSuccess();
                }
            });
        }

        @Override
        public void onWriteFailure(final BleException exception) {
            temp++;
            showLog("onWriteFailure: " + exception.getDescription()+",次数："+temp);
            App.getInstance().post(new Runnable() {
                @Override
                public void run() {
                    bleCallbackManager.onWriteFailure(exception);
                }
            });

        }
    };

    BleNotifyCallback bleNotifyCallback = new BleNotifyCallback() {
        @Override
        public void onNotifySuccess() {
            showLog("onNotifySuccess");
            App.getInstance().post(new Runnable() {
                @Override
                public void run() {
                    bleCallbackManager.onNotifySuccess();
                }
            });

        }

        @Override
        public void onNotifyFailure(final BleException exception) {
            showLog("onNotifyFailure: " + exception.getDescription());
            App.getInstance().post(new Runnable() {
                @Override
                public void run() {
                    bleCallbackManager.onNotifyFailure(exception);
                }
            });

        }

        @Override
        public void onCharacteristicChanged(final byte[] data) {
            App.getInstance().post(new Runnable() {
                @Override
                public void run() {
                    bleCallbackManager.onCharacteristicChanged(data);
                }
            });
        }
    };
    BleScanCallback bleScanCallback = new BleScanCallback() {
        @Override
        public void onScanStarted(final boolean success) {
            showLog("onScanStarted: " + success);
            App.getInstance().post(new Runnable() {
                @Override
                public void run() {
                    bleCallbackManager.onScanStarted(success);
                }
            });

        }

        @Override
        public void onScanning(final BleDevice result) {
            showLog("onScanStarted: " + result.getName() + "-->" + result.getMac());
            App.getInstance().post(new Runnable() {
                @Override
                public void run() {
                    bleCallbackManager.onScanning(result);
                }
            });

        }

        @Override
        public void onScanFinished(final List<BleDevice> scanResultList) {
            showLog("onScanFinished");
            App.getInstance().post(new Runnable() {
                @Override
                public void run() {
                    bleCallbackManager.onScanFinished(scanResultList);
                }
            });

        }
    };

    BleGattCallback bleGattCallback = new BleGattCallback() {
        @Override
        public void onStartConnect() {
            showLog("onStartConnect");
            App.getInstance().post(new Runnable() {
                @Override
                public void run() {
                    bleCallbackManager.onStartConnect();
                }
            });

        }

        @Override
        public void onConnectFail(final BleException exception) {
            showLog("onConnectFail: " + exception.getDescription());
            App.getInstance().post(new Runnable() {
                @Override
                public void run() {
                    bleCallbackManager.onConnectFail(exception);
                }
            });

        }

        @Override
        public void onConnectSuccess(final BleDevice bleDevice, final BluetoothGatt gatt, final int status) {
            showLog("onConnectSuccess");
            App.getInstance().post(new Runnable() {
                @Override
                public void run() {
                    bleCallbackManager.onConnectSuccess(bleDevice, gatt, status);
                    BleHelper.this.currentConnectBleDevice = bleDevice;
                }
            });
            openNotify(bleDevice);
        }

        @Override
        public void onDisConnected(final boolean isActiveDisConnected, final BleDevice device, final BluetoothGatt gatt, final int status) {
            showLog("onDisConnected:-->" + isActiveDisConnected);
            App.getInstance().post(new Runnable() {
                @Override
                public void run() {
                    bleCallbackManager.onDisConnected(isActiveDisConnected, device, gatt, status);
                }
            });

        }
    };


    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
                final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE,
                        BluetoothAdapter.ERROR);
                switch (state) {
                    case BluetoothAdapter.STATE_OFF:
                        showLog("Bluetooth off");
                        bleCallbackManager.onBleClose();
                        break;
                    case BluetoothAdapter.STATE_ON:
                        bleCallbackManager.onBleOpen();
                        showLog("Bluetooth on");
                        break;
                    case BluetoothAdapter.STATE_TURNING_OFF:
                        showLog("Turning Bluetooth off");
                        break;
                    case BluetoothAdapter.STATE_TURNING_ON:
                        showLog("Turning Bluetooth on");
                        break;
                }
            }
        }
    };

    private void showLog(String msg) {
        Log.e(TAG, msg);
    }
}
