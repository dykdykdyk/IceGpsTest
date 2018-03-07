package ice.rtk.view.LinesView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Arrays;

import butterknife.BindView;
import butterknife.OnClick;
import ice.rtk.R;
import ice.rtk.Utils.LogUtil;
import ice.rtk.Utils.Util;
import ice.rtk.view.base.BaseActivity;
import ice.rtk.view.customview.ScaleView;


public class LinesActivity extends BaseActivity implements LinesView.OnMapStatusChangeListener {
    @BindView(R.id.mapview)
    LinesView mapview;
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
    @BindView(R.id.ll_height_data_top)
    LinearLayout llHeightDataTop;
    @BindView(R.id.ll_height_data_bottom)
    LinearLayout llHeightDataBottom;

    double[] start = {23.70476950, 113.54358250, 60.800};
    double[] end = {23.60476950, 112.24358250, 30.800};
    double[] current = {24.70476950, 116.24358250, 5.800};

    MapRenderLines mapRenderLines;
    @BindView(R.id.start_distance)
    TextView startDistance;
    @BindView(R.id.end_distance)
    TextView endDistance;
    @BindView(R.id.tv_distance)
    TextView tvDistance;
    @BindView(R.id.high_difference)
    TextView highDifference;
    @BindView(R.id.offset_distance)
    TextView offsetDistance;

    @Override
    protected int layout() {
        return R.layout.activity_lines;
    }

    @Override
    protected void init() {
        Intent intent = getIntent();
        if (intent != null) {
            String lat = intent.getStringExtra(Util.DRAW_POINT_LAT);
            String lon = intent.getStringExtra(Util.DRAW_POINT_LON);
            String ele = intent.getStringExtra(Util.DRAW_POINT_ELE);
            if (lat != null) {
                double[] dou = {Double.parseDouble(lat),
                        Double.parseDouble(lon), Double.parseDouble(ele)};
                LogUtil.e(getClass(), Arrays.toString(dou));
                mapRenderLines = new MapRenderLines(mapview, this);
            }
        }
        mapRenderLines = new MapRenderLines(mapview, this);
    }

    /**
     * 1.收到数据传递给MapRender
     * 2.MapRender解析后传递给Map绘制出来
     */
    @Override
    protected void setListener() {
        mapview.setOnMapStatusChangeListener(this);
        mapRenderLines.setMapDataChangeListener(new MapRenderLines.LinesMapDataChangeListener() {
            @Override
            public void Start_change(String multiple) {
                RunOnUI(startDistance, multiple);
            }

            @Override
            public void End_change(String multiple) {
                RunOnUI(endDistance, multiple);
            }

            @Override
            public void Height_change(String multiple) {
                RunOnUI(highDifference, multiple);
            }

            @Override
            public void Offset_Distance(String multiple) {
                RunOnUI(offsetDistance, multiple);
            }
        });

        mapRenderLines.SetPoints(start,end,current);

    }

    @Override
    public void onScaleChange(String multiple) {
        RunOnUI(tvScale,multiple);
    }

    @OnClick({R.id.tv_enlarge, R.id.tv_narrow, R.id.tv_scale, R.id.tv_changeview})
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
