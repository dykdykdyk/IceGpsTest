package ice.rtk.view.activity;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import ice.rtk.R;
import ice.rtk.view.base.BaseActivity;
import ice.rtk.view.fragment.Fragment_Lines;
import ice.rtk.view.fragment.Fragment_Coordinate;
import ice.rtk.view.fragment.Fragment_Point;

public class CoordinateDataActivity extends BaseActivity implements OnClickListener {
    Fragment_Lines mFragment_control;
    Fragment_Coordinate mFragment_coordinate;
    Fragment_Point mFragment_lofting;
    private FragmentManager supportFragmentManager;
    @BindView(R.id.coordinate_point)
    TextView mCoordinatePoint;
    @BindView(R.id.loft_point)
    TextView mLoftPoint;
    @BindView(R.id.control_point)
    TextView mControlPoint;

    @Override
    protected int layout() {
        return R.layout.activity_coordinate_data;
    }

    private void initFrament() {
        mFragment_control = new Fragment_Lines();
        mFragment_coordinate = new Fragment_Coordinate();
        mFragment_lofting = new Fragment_Point();
        supportFragmentManager = getSupportFragmentManager();
        switchContent(null, mFragment_coordinate, "", "");
        mCoordinatePoint.setTextColor(getResources().getColor(R.color.colorPrimary));
    }

    public void switchContent(Fragment from, Fragment to, String stackName, String TAGname) {
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction tx = manager.beginTransaction();
        if (!to.isAdded()) {    // 先判断是否被add过
            if (from == null) {
                tx.add(R.id.coordinate_continaer, to, TAGname).commit(); // 隐藏当前的fragment，add下一个到Activity中
            } else {
                tx.hide(from).add(R.id.coordinate_continaer, to, TAGname).commit(); // 隐藏当前的fragment，add下一个到Activity中
            }
        } else {
            if (from == null){
                tx.show(to).commit();
            }else{
                tx.hide(from).show(to).commit(); // 隐藏当前的fragment，显示下一个
            }
        }
//        FragmentTransaction fragmentTransaction = supportFragmentManager.beginTransaction();
//        fragmentTransaction.replace(R.id.coordinate_continaer, to);
//        fragmentTransaction.commit();
    }

    @Override
    protected void init() {
        initFrament();
    }

    @Override
    protected void setListener() {
        mLoftPoint.setOnClickListener(this);
        mControlPoint.setOnClickListener(this);
        mCoordinatePoint.setOnClickListener(this);
    }

    public Fragment getVisibleFragment() {
        FragmentManager fragmentManager =getSupportFragmentManager();
        List<Fragment> fragments = fragmentManager.getFragments();
        for (Fragment fragment : fragments) {
            if (fragment != null && fragment.isVisible())
                return fragment;
        }
        return null;
    }
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.coordinate_point:
                mCoordinatePoint.setTextColor(getResources().getColor(R.color.colorPrimary));
                mLoftPoint.setTextColor(getResources().getColor(R.color.common_white));
                mControlPoint.setTextColor(getResources().getColor(R.color.common_white));
                switchContent(getVisibleFragment(),mFragment_coordinate,"","");
                break;
            case R.id.loft_point:
                mCoordinatePoint.setTextColor(getResources().getColor(R.color.common_white));
                mLoftPoint.setTextColor(getResources().getColor(R.color.colorPrimary));
                mControlPoint.setTextColor(getResources().getColor(R.color.common_white));
                switchContent(getVisibleFragment(),mFragment_lofting,"","");
                break;
            case R.id.control_point:
                mCoordinatePoint.setTextColor(getResources().getColor(R.color.common_white));
                mLoftPoint.setTextColor(getResources().getColor(R.color.common_white));
                mControlPoint.setTextColor(getResources().getColor(R.color.colorPrimary));
                switchContent(getVisibleFragment(),mFragment_control,"","");
                break;
        }
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            Fragment f =getVisibleFragment();
            if (f instanceof Fragment_Point) {
                if(((Fragment_Point) f).cancleSelect()){
                    return true;
                }{
                    return super.onKeyDown(keyCode, event);
                }
            }else if(f instanceof Fragment_Lines){
                if(((Fragment_Lines) f).cancleSelect()){
                    return true;
                }{
                    return super.onKeyDown(keyCode, event);
                }
            }
        }
        return super.onKeyDown(keyCode, event);
    }
}
