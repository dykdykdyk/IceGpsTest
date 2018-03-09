package ice.rtk.ble;

import android.bluetooth.BluetoothGatt;

import com.clj.fastble.data.BleDevice;
import com.clj.fastble.exception.BleException;

import java.util.List;

/**
 * Created by 111 on 2018/1/15.
 */

public class BleStatusCallback implements Callback{


    @Override
    public void onScanStarted(boolean success) {

    }

    @Override
    public void onScanning(BleDevice result) {

    }

    @Override
    public void onScanFinished(List<BleDevice> scanResultList) {

    }

    @Override
    public void onStartConnect() {

    }

    @Override
    public void onConnectFail(BleException exception) {

    }

    @Override
    public void onConnectSuccess(BleDevice bleDevice, BluetoothGatt gatt, int status) {

    }

    @Override
    public void onDisConnected(boolean isActiveDisConnected, BleDevice device, BluetoothGatt gatt, int status) {

    }

    @Override
    public void onNotifySuccess() {

    }

    @Override
    public void onNotifyFailure(BleException exception) {

    }

    @Override
    public void onCharacteristicChanged(byte[] data) {

    }

    @Override
    public void onWriteSuccess() {

    }

    @Override
    public void onWriteFailure(BleException exception) {

    }

    @Override
    public void onBleOpen() {

    }

    @Override
    public void onBleClose() {

    }
}
