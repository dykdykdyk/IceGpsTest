package ice.rtk.ble;

import android.bluetooth.BluetoothGatt;

import com.clj.fastble.data.BleDevice;
import com.clj.fastble.exception.BleException;

import java.util.List;

/**
 * Created by 111 on 2018/1/15.
 */

public interface Callback {
    void onScanStarted(boolean success);

    void onScanning(BleDevice result);

    void onScanFinished(List<BleDevice> scanResultList);

    void onStartConnect();

    void onConnectFail(BleException exception);

    void onConnectSuccess(BleDevice bleDevice, BluetoothGatt gatt, int status);

    void onDisConnected(boolean isActiveDisConnected, BleDevice device, BluetoothGatt gatt, int status);

    void onNotifySuccess();

    void onNotifyFailure(BleException exception);

    void onCharacteristicChanged(byte[] data);

    void onWriteSuccess();

    void onWriteFailure(BleException exception);

    void onBleOpen();

    void onBleClose();
}
