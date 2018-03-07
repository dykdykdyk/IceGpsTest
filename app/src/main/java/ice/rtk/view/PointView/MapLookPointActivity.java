package ice.rtk.view.PointView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ice.rtk.R;
import ice.rtk.Utils.LogUtil;
import ice.rtk.Utils.Util;
import ice.rtk.view.base.BaseActivity;
import ice.rtk.view.customview.ScaleView;


public class MapLookPointActivity extends BaseActivity implements MapLookView.OnMapStatusChangeListener {

    @BindView(R.id.mapview)
    MapLookView mapview;
    @BindView(R.id.tv_enlarge)
    TextView tvEnlarge;
    @BindView(R.id.tv_narrow)
    TextView tvNarrow;
    @BindView(R.id.tv_scale)
    TextView tvScale;
    @BindView(R.id.scaleview)
    ScaleView scaleview;

    @Override
    protected int layout() {
        return R.layout.activity_pointmaplook;
    }

    @Override
    protected void init() {

        Intent intent = getIntent();
        if (intent != null) {
            String lat = intent.getStringExtra(Util.DRAW_POINT_LAT);
            String lon = intent.getStringExtra(Util.DRAW_POINT_LON);
            String ele = intent.getStringExtra(Util.DRAW_POINT_ELE);
        }
    }

    @Override
    protected void setListener() {
        mapview.setOnMapStatusChangeListener(this);
    }


    @Override
    public void onScaleChange(final String multiple) {
        LogUtil.e(getClass(),"multiple"+multiple);
        RunOnUI(tvScale,multiple);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }

    @OnClick({R.id.tv_enlarge, R.id.tv_narrow})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_enlarge:
              mapview.enlarge();
                break;
            case R.id.tv_narrow:
                mapview.narrow();
                break;
        }
    }
}
