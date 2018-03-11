package ice.rtk.view.activity;

import android.Manifest;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.tbruyelle.rxpermissions.RxPermissions;

import java.awt.Desktop;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import ice.rtk.R;
import ice.rtk.Utils.Util;
import ice.rtk.view.base.BaseActivity;
import rx.functions.Action1;

public class MainActivity extends BaseActivity {
    RxPermissions rxPermissions;
    @BindView(R.id.gv)
    GridView gv;
    private GridView gridView;
    private List<Map<String, Object>> dataList;
    private SimpleAdapter adapter;
    private String TAG = MainActivity.this.getClass().getName();

    @Override
    protected int layout() {
        return R.layout.activity_main;
    }

    @Override
    protected void init() {
        initPermission();
        initData();

        File file =new File(Environment.getExternalStorageDirectory().getPath() + "/" + Util.APP_SDCARD_PATH+"/ellips.dat");
        try {
            FileOutputStream fileOutputStream =new FileOutputStream(file);
            try {
                fileOutputStream.write("11111111111111111111111111111111111111111111111".getBytes());
                fileOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void initPermission() {
        rxPermissions = new RxPermissions(this);
        rxPermissions.request(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)//这里填写所需要的权限
                .subscribe(new Action1<Boolean>() {
                    @Override
                    public void call(Boolean aBoolean) {
                        if (aBoolean) {
                            //当所有权限都允许之后，返回true
                            Toast.makeText(MainActivity.this, "TRUE", Toast.LENGTH_LONG).show();
                            File file =new File(Environment.getExternalStorageDirectory().getPath()+"/"+ Util.APP_SDCARD_PATH);
                            if(!file.exists()){
                                file.mkdirs();
                            }
                        } else {
                            //只要有一个权限禁止，返回false，
                            //下一次申请只申请没通过申请的权限
                            Toast.makeText(MainActivity.this, "false", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    void initData() {
        //图标
        int icno[] = {R.drawable.ic_root_file_raw, R.drawable.ic_root_file_system, R.drawable.ic_root_file_data, R.drawable.bluetooth};
        //图标下的文字
        String name[] = {"点放样", "线放样", "坐标数据", "蓝牙通信"};
        dataList = new ArrayList<Map<String, Object>>();
        for (int i = 0; i < icno.length; i++) {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("img", icno[i]);
            map.put("text", name[i]);
            dataList.add(map);
        }
        String[] from={"img","text"};

        int[] to={R.id.img,R.id.text};
        adapter =new SimpleAdapter(this,dataList,R.layout.item_gridview,from,to);
        gv.setAdapter(adapter);
        gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                switch(position){
                    case 0:
                        startActivity(PointLoftingActivity.class);
                        break;
                    case 1:
                        startActivity(LinesLoftingActivity.class);
                        break;
                    case 2:
                        startActivity(CoordinateDataActivity.class);
                        break;
                    case 3://blue
                        startActivity(BleActivity.class);
                        break;
                }
            }
        });
    }

    @Override
    protected void setListener() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }
}
