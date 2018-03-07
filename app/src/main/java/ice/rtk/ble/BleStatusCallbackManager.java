package ice.rtk.ble;

import android.bluetooth.BluetoothGatt;

import com.clj.fastble.data.BleDevice;
import com.clj.fastble.exception.BleException;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by 111 on 2018/1/15.
 */

public class BleStatusCallbackManager {
    private static BleStatusCallbackManager bleCallbackManager;
    private CopyOnWriteArrayList<Callback> callbacks;

    public static BleStatusCallbackManager getInstance() {
        if (bleCallbackManager == null) {
            synchronized (BleStatusCallbackManager.class) {
                if (bleCallbackManager == null) {
                    bleCallbackManager = new BleStatusCallbackManager();
                }
            }
        }
        return bleCallbackManager;
    }

    private BleStatusCallbackManager() {
        callbacks = new CopyOnWriteArrayList<>();
    }

    protected void addBleStatusCallback(Callback callback) {
        if (callbacks != null) {
            callbacks.add(callback);
        }
    }

    protected void removeBleStatusCallback(Callback callback) {
        if (callbacks != null) {
            callbacks.remove(callback);
        }
    }

    protected void onDisConnected(boolean isActiveDisConnected, BleDevice device, BluetoothGatt gatt, int status) {
        if (callbacks != null) {
            for (Callback callback : callbacks) {
                callback.onDisConnected(isActiveDisConnected, device, gatt, status);
            }
        }
    }

    protected void onConnectSuccess(BleDevice bleDevice, BluetoothGatt gatt, int status) {
        if (callbacks != null) {
            for (Callback callback : callbacks) {
                callback.onConnectSuccess(bleDevice, gatt, status);
            }
        }
    }

    protected void onConnectFail(BleException exception) {
        if (callbacks != null) {
            for (Callback callback : callbacks) {
                callback.onConnectFail(exception);
            }
        }
    }

    protected void onStartConnect() {
        if (callbacks != null) {
            for (Callback callback : callbacks) {
                callback.onStartConnect();
            }
        }
    }

    protected void onScanFinished(List<BleDevice> scanResultList) {
        if (callbacks != null) {
            for (Callback callback : callbacks) {
                callback.onScanFinished(scanResultList);
            }
        }
    }

    protected void onScanning(BleDevice result) {
        if (callbacks != null) {
            for (Callback callback : callbacks) {
                callback.onScanning(result);
            }
        }
    }

    protected void onScanStarted(boolean success) {
        if (callbacks != null) {
            for (Callback callback : callbacks) {
                callback.onScanStarted(success);
            }
        }
    }

    public void onNotifySuccess() {
        if (callbacks != null) {
            for (Callback callback : callbacks) {
                callback.onNotifySuccess();
            }
        }
    }

    public void onNotifyFailure(BleException exception) {
        if (callbacks != null) {
            for (Callback callback : callbacks) {
                callback.onNotifyFailure(exception);
            }
        }
    }

    public void onCharacteristicChanged(byte[] data) {
        if (callbacks != null) {
            for (Callback callback : callbacks) {
                callback.onCharacteristicChanged(data);
            }
        }
    }

    public void onWriteSuccess() {
        if (callbacks != null) {
            for (Callback callback : callbacks) {
                callback.onWriteSuccess();
            }
        }
    }

    public void onWriteFailure(BleException exception) {
        if (callbacks != null) {
            for (Callback callback : callbacks) {
                callback.onWriteFailure(exception);
            }
        }
    }

    public void onBleClose() {
        if (callbacks != null) {
            for (Callback callback : callbacks) {
                callback.onBleClose();
            }
        }
    }

    public void onBleOpen() {
        if (callbacks != null) {
            for (Callback callback : callbacks) {
                callback.onBleOpen();
            }
        }
    }
}
