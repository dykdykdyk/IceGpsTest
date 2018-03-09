package ice.rtk.view.coordinate_data.fragment;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import butterknife.BindView;
import ice.rtk.R;
import ice.rtk.view.base.BaseFragment;
import ice.rtk.view.coordinate_data.Point_List;
import ice.rtk.view.coordinate_data.Point_Name;
import ice.rtk.view.coordinate_data.Recycle_list_Adapter;
import ice.rtk.view.coordinate_data.Recycle_nam_Adapter;
import ice.rtk.view.coordinate_data.customView.IHorizontalScrollView;


/**
 * 作者: 邓洋康 on 2018/01/24 21:41
 * 作用:XXX
 */

public class Fragment_Coordinate extends BaseFragment {

    @BindView(R.id.recycle_name)
    RecyclerView recycleName;
    @BindView(R.id.image_left)
    ImageView imageLeft;
    @BindView(R.id.recycle_list)
    RecyclerView recycleList;
    @BindView(R.id.hsv)
    IHorizontalScrollView hsv;
    @BindView(R.id.image_right)
    ImageView imageRight;
    @BindView(R.id.new_point)
    TextView newPoint;
    @BindView(R.id.open)
    TextView open;
    @BindView(R.id.search)
    TextView search;

    ArrayList<Point_Name> name;
    ArrayList<Point_List> list;
    final int left = 0;
    final int right = 1;
    int currentScroll;
    Recycle_nam_Adapter recycle_nam_adapter;
    Recycle_list_Adapter recycle_list_adapter;
    @Override
    protected View childImpl() {
        return View.inflate(getActivity(), R.layout.fragment_coordinate, null);
    }

    @Override
    protected void init() {
        initname();
    }

    @Override
    protected void setListener() {
        recycleName.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (MotionEvent.ACTION_UP == motionEvent.getAction()) {
                    recycleName.stopScroll();
                    recycleList.stopScroll();
                    return true;
                }
                return false;
            }
        });
        recycleList.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (MotionEvent.ACTION_UP == motionEvent.getAction()) {
                    recycleName.stopScroll();
                    recycleList.stopScroll();
                    return true;
                }
                return false;
            }
        });
        recycleName.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                currentScroll = left;
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (currentScroll == left)
                    recycleList.scrollBy(dx, dy);
            }
        });
        recycleList.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                currentScroll = right;
                Log.e("sdf", newState + "");
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (currentScroll == right)
                    recycleName.scrollBy(dx, dy);
            }
        });

        hsv.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_UP)
                {
                    hsv.startScrollerTask();
                }
                return false;
            }
        });
        hsv.setOnScrollStopListner(new IHorizontalScrollView.OnScrollStopListner() {
            @Override
            public void onScrollStoped() {

            }

            @Override
            public void onScrollToLeftEdge() {
                imageRight.setVisibility(View.VISIBLE);
                imageLeft.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onScrollToRightEdge() {
                imageLeft.setVisibility(View.VISIBLE);
                imageRight.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onScrollToMiddle() {
                imageLeft.setVisibility(View.VISIBLE);
                imageRight.setVisibility(View.VISIBLE);
            }
        });
    }

    private void initname() {
        name = new ArrayList<>();
        name.add(new Point_Name(getResources().getString(R.string.point_name)));
        for (int i = 0; i < 10; i++) {
            name.add(new Point_Name(i + ""));
        }
        recycle_nam_adapter = new Recycle_nam_Adapter(getActivity(), name);
        recycleName.setAdapter(recycle_nam_adapter);
        recycleName.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        list = new ArrayList<Point_List>();
        list.add(new Point_List(getResources().getString(R.string.N), getResources().getString(R.string.E), getResources().getString(R.string.Z),
                getResources().getString(R.string.mileage), getResources().getString(R.string.descript)));
        for (int i = 0; i < 10; i++) {
            list.add(new Point_List("12.3432" + i, "554.2321" + i, "54.3" + i, i + "km", "不可" + i));
        }
        recycle_list_adapter = new Recycle_list_Adapter(getActivity(), list);
        recycleList.setAdapter(recycle_list_adapter);
        recycleList.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
    }
}
