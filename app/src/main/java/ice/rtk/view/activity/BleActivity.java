package ice.rtk.view.activity;

import android.Manifest;
import android.bluetooth.BluetoothGatt;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.clj.fastble.data.BleDevice;
import com.clj.fastble.exception.BleException;
import com.tbruyelle.rxpermissions.RxPermissions;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ice.rtk.R;
import ice.rtk.Utils.LogUtil;
import ice.rtk.Utils.Sort.DeviceShowHelper;
import ice.rtk.ble.BleHelper;
import ice.rtk.ble.Callback;
import ice.rtk.ble.Util.Cmd;
import ice.rtk.view.adapter.ScannerAdapter;
import ice.rtk.view.base.BaseActivity;
import rx.functions.Action1;

/**
 * Created by Administrator on 2018/1/23.
 */

public class BleActivity extends BaseActivity {
    RxPermissions rxPermissions;
    @BindView(R.id.listview)
    ListView listview;
    @BindView(R.id.send)
    Button send;
    private BleHelper bleHelper;
    private boolean scaning = false; // 是否正在扫描
    private boolean is_canwrite = false; // 是否正在扫描
    ArrayList<BleDevice> list;
    ScannerAdapter scannerAdapter;
    byte[] by =new byte[1024 *10];

    @Override
    protected int layout() {
        return R.layout.activity_bleactivity;
    }

    @Override
    protected void init() {
        listview = (ListView) findViewById(R.id.listview);
        bleHelper = BleHelper.getInstance();
        bleHelper.addBleStatusCallback(callback);
        list = new ArrayList<>();
        scannerAdapter = new ScannerAdapter(this, list);
        listview.setAdapter(scannerAdapter);
        initPermission();
        for (int i = 0; i < 1024*10; i++) {
            by[i]=1;
        }
    }


    @Override
    protected void setListener() {
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                if (scaning)
                bleHelper.cancelScan();//先判断是否正在扫描
                bleHelper.connect(list.get(position));
            }
        });
    }

    private void initPermission() {
        rxPermissions = new RxPermissions(this);
        rxPermissions.request(
                Manifest.permission.BLUETOOTH,
                Manifest.permission.ACCESS_FINE_LOCATION)//这里填写所需要的权限
                .subscribe(new Action1<Boolean>() {
                    @Override
                    public void call(Boolean aBoolean) {
                        if (aBoolean) {
                            //当所有权限都允许之后，返回true
                            Toast.makeText(BleActivity.this, "true", Toast.LENGTH_LONG).show();
                            bleHelper.scan();
                        } else {
                            //只要有一个权限禁止，返回false，
                            //下一次申请只申请没通过申请的权限
                            Toast.makeText(BleActivity.this, "false", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    Comparator<DeviceShowHelper> comparator = new Comparator<DeviceShowHelper>() {
        public int compare(DeviceShowHelper s1, DeviceShowHelper s2) {
            //先排年龄
            if (s1.getRssi() != s2.getRssi()) {
                return s2.getRssi() - s1.getRssi();
            }
            return 0;
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        bleHelper.removeBleStatusCallback(callback);
    }

    Callback callback = new Callback() {
        @Override
        public void onScanStarted(boolean success) {
            //搜索开始
        }

        @Override
        public void onScanning(BleDevice result) {
            //搜索中
            list.add(result);
            scannerAdapter.notifyDataSetChanged();
        }

        @Override
        public void onScanFinished(List<BleDevice> scanResultList) {
            //搜索结束
        }

        @Override
        public void onStartConnect() {
            //开始连接
        }

        @Override
        public void onConnectFail(BleException exception) {
            //连接失败
        }

        @Override
        public void onConnectSuccess(BleDevice bleDevice, BluetoothGatt gatt, int status) {
            //连接成功
            LogUtil.d(getClass(), "onConnectSuccess:连接成功" + bleDevice + "," + status);

        }

        /**
         *
         * @param isActiveDisConnected   是否是你手动调用断开。。
         * @param device
         * @param gatt
         * @param status
         */
        @Override
        public void onDisConnected(boolean isActiveDisConnected, BleDevice device, BluetoothGatt gatt, int status) {
            //断开连接
        }

        @Override
        public void onNotifySuccess() {
            is_canwrite =true;

        }

        @Override
        public void onNotifyFailure(BleException exception) {
            //通知开启失败
        }

        @Override
        public void onCharacteristicChanged(byte[] data) {
            //通知数据
        }

        @Override
        public void onWriteSuccess() {
            //写入成功
        }

        @Override
        public void onWriteFailure(BleException exception) {
            //写入失败
        }

        @Override
        public void onBleOpen() {
            //蓝牙开启
        }

        @Override
        public void onBleClose() {
            //蓝牙关闭
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }

    @OnClick(R.id.send)
    public void onViewClicked() {
        long start =System.currentTimeMillis();
//        for (int i = 0; i < 512; i++) {
            if(bleHelper !=null)
            bleHelper.write(by);
            //开启通知成功
//        }
        long end =System.currentTimeMillis();
        LogUtil.e(getClass(),"time:"+(end-start));
    }



}
