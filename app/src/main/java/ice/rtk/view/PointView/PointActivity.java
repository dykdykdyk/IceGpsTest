package ice.rtk.view.PointView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ice.rtk.R;
import ice.rtk.Utils.LogUtil;
import ice.rtk.Utils.Util;
import ice.rtk.view.base.BaseActivity;
import ice.rtk.view.customview.ScaleView;


public class PointActivity extends BaseActivity implements MapView.OnMapStatusChangeListener {
    @BindView(R.id.mapview)
    MapView mapview;
    @BindView(R.id.tv_enlarge)
    TextView tvEnlarge;
    @BindView(R.id.tv_narrow)
    TextView tvNarrow;
    @BindView(R.id.tv_scale)
    TextView tvScale;
    @BindView(R.id.scaleview)
    ScaleView scaleview;
    @BindView(R.id.tv_changeview)
    TextView tvChangeview;
    @BindView(R.id.s_n_offset)
    TextView sNOffset;
    @BindView(R.id.e_w_offset)
    TextView eWOffset;
    @BindView(R.id.tv_distance)
    TextView tvDistance;
    @BindView(R.id.tv_distance_value)
    TextView tvDistanceValue;
    @BindView(R.id.tv_high_difference)
    TextView tvHighDifference;
    @BindView(R.id.ll_height_data_top)
    LinearLayout llHeightDataTop;
    @BindView(R.id.ll_height_data_botton)
    LinearLayout llHeightDataBotton;

    MapRender mapRender;

    double[] current =new double[3];
    @Override
    protected int layout() {
        return R.layout.activity_point;
    }

    @Override
    protected void init() {

        Intent intent =getIntent();
        if(intent !=null){
            String lat=intent.getStringExtra(Util.DRAW_POINT_LAT);
            String lon=intent.getStringExtra(Util.DRAW_POINT_LON);
            String ele=intent.getStringExtra(Util.DRAW_POINT_ELE);
            if(lat !=null){
                double[] dou= {Double.parseDouble(lat),
                        Double.parseDouble(lon),Double.parseDouble(ele)};
                LogUtil.d(getClass(), Arrays.toString(dou));

//                current= {22.7052679964,114.2435153866,50};
                current[0] =22.7052679964;
                current[1] =114.2435153866;
                current[2] =50;
                LogUtil.e(getClass(), Arrays.toString(dou));
                mapRender =new MapRender(mapview, this);
            }
        }
        mapRender =new MapRender(mapview,this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        double[] dou= {22.6052679964,114.1435153866,70};
        LogUtil.d(getClass(), Arrays.toString(dou));
//        double[] current= {22.7052679964,114.2435153866,50};
        LogUtil.e(getClass(), Arrays.toString(dou));
        mapRender.SetPoints(dou,current);
    }

    @Override
    protected void setListener() {
        mapview.setOnMapStatusChangeListener(this);
        mapRender.setMapDataChangeListener(new MapRender.MapDataChangeListener() {
            @Override
            public void S_N_change(String multiple) {
                RunOnUI(sNOffset,multiple);
            }

            @Override
            public void E_W_change(String multiple) {
                RunOnUI(eWOffset,multiple);
            }

            @Override
            public void Distance_change(String multiple) {
                RunOnUI(tvDistanceValue,multiple);
            }

            @Override
            public void Height_change(String multiple) {
                RunOnUI(tvHighDifference,multiple);
            }
        });
    }


    @Override
    public void onScaleChange(final String multiple) {
//        LogUtil.e(getClass(),"multiple"+multiple);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tvScale.setText(multiple);
            }
        });

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }

    @OnClick({R.id.tv_enlarge, R.id.tv_narrow, R.id.tv_changeview})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_enlarge:
                mapview.enlarge();
                break;
            case R.id.tv_narrow:
                mapview.narrow();
                break;
            case R.id.tv_changeview:
                mapview.changeView();
                break;
        }
    }
}
