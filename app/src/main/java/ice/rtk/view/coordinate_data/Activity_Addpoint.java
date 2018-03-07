package ice.rtk.view.coordinate_data;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.pop.android.common.util.ToastUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ice.rtk.R;
import ice.rtk.Utils.LogUtil;
import ice.rtk.Utils.ToastUtil;
import ice.rtk.Utils.Util;
import ice.rtk.view.base.BaseActivity;

public class Activity_Addpoint extends BaseActivity {


    @BindView(R.id.loft_point)
    TextView loftPoint;
    @BindView(R.id.point_name)
    TextView pointName;
    @BindView(R.id.add_point_edit_name)
    EditText addPointEditName;
    @BindView(R.id.point_lat)
    TextView pointLat;
    @BindView(R.id.add_point_edit_lat)
    EditText addPointEditLat;
    @BindView(R.id.point_lon)
    TextView pointLon;
    @BindView(R.id.add_point_edit_lon)
    EditText addPointEditLon;
    @BindView(R.id.point_ele)
    TextView pointEle;
    @BindView(R.id.add_point_edit_ele)
    EditText addPointEditEle;
    @BindView(R.id.point_descript)
    TextView pointDescript;
    @BindView(R.id.add_point_edit_descript)
    EditText addPointEditDescript;
    @BindView(R.id.point_mileage)
    TextView pointMileage;
    @BindView(R.id.add_point_edit_mileage)
    EditText addPointEditMileage;
    @BindView(R.id.point_cancel)
    Button pointCancel;
    @BindView(R.id.point_ok)
    Button pointOk;
    private static final int BACK_POINT_LIST=1;
    @Override
    protected int layout() {
        return R.layout.activity_add_loft_point;
    }

    @Override
    protected void init() {
        //默认全部隐藏
        HindRightDrawable(addPointEditName);
        HindRightDrawable(addPointEditLat);
        HindRightDrawable(addPointEditLon);
        HindRightDrawable(addPointEditEle);
        HindRightDrawable(addPointEditDescript);
        HindRightDrawable(addPointEditMileage);
    }

    /**
     * @param editText 删除按钮隐藏
     */
    private void HindRightDrawable(EditText editText) {
        //设置editText的隐藏 和显示
        editText.setCompoundDrawables(null, null, null, null);
    }

    /**
     * @param editText 删除按钮显示
     */
    private void ShowRightDrawable(EditText editText) {
        //设置editText的隐藏 和显示
        // 使用代码设置drawableleft
        Drawable drawable = getResources().getDrawable(
                R.drawable.ic_edit_delete);
        // / 这一步必须要做,否则不会显示.
        drawable.setBounds(0, 0, drawable.getMinimumWidth(),
                drawable.getMinimumHeight());
        editText.setCompoundDrawables(null, null, drawable, null);
    }
    Point_List point_list;
    String name;
    String insert_point;
    @Override
    protected void setListener() {
        setTouchEvent(addPointEditName);
        setTouchEvent(addPointEditLat);
        setTouchEvent(addPointEditLon);
        setTouchEvent(addPointEditEle);
        setTouchEvent(addPointEditDescript);
        setTouchEvent(addPointEditMileage);
        setFocusChangeEvent(addPointEditName);
        setFocusChangeEvent(addPointEditLat);
        setFocusChangeEvent(addPointEditLon);
        setFocusChangeEvent(addPointEditEle);
        setFocusChangeEvent(addPointEditDescript);
        setFocusChangeEvent(addPointEditMileage);

        //获取编辑数据
        Intent intent =getIntent();
        if(intent!=null){
            Bundle bundle =intent.getExtras();
            if(bundle !=null){
                insert_point =bundle.getString(Util.INSERT_POINT);
                if(insert_point !=null){

                }else {
                    name = bundle.getString(Util.ADD_POINT_NAME);
                    point_list = (Point_List) bundle.getSerializable(Util.ADD_POINT_BUNDLE);
                    addPointEditName.getText().toString();
                    String Lat =point_list.getLat();
                    String Lon =point_list.getLon();
                    String Ele =point_list.getEle();
                    String Descript =point_list.getDiscript();
                    String Mileage =point_list.getMileage();
                    LogUtil.e(getClass(),point_list+","+name);
                    addPointEditName.setText(name);
                    addPointEditLat.setText(Lat);
                    addPointEditLon.setText(Lon);
                    addPointEditEle.setText(Ele);
                    addPointEditDescript.setText(Descript);
                    addPointEditMileage.setText(Mileage);
                }
            }
        }
    }

    private void setFocusChangeEvent(final EditText editText) {
        editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    ShowRightDrawable(editText);
                } else {
                    HindRightDrawable(editText);
                }
            }
        });
    }

    //处理点击右边的图片事件
    private void setTouchEvent(final EditText editText) {
        editText.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // et.getCompoundDrawables()得到一个长度为4的数组，分别表示左右上下四张图片
                Drawable drawable = editText.getCompoundDrawables()[2];
                //如果右边没有图片，不再处理
                if (drawable == null)
                    return false;
                //如果不是按下事件，不再处理
                if (event.getAction() != MotionEvent.ACTION_UP)
                    return false;
                if (event.getX() > editText.getWidth()
                        - editText.getPaddingRight()
                        - drawable.getIntrinsicWidth()) {
                    editText.setText("");
                }
                return false;
            }
        });
    }

    @OnClick({R.id.point_cancel, R.id.point_ok})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.point_cancel:
//                Intent intent1 =new Intent();
//                Bundle bundle1 =new Bundle();
//                bundle1.putSerializable(Util.ADD_POINT_BUNDLE,point_list);
//                bundle1.putString(Util.ADD_POINT_NAME,name);
//                intent1.putExtras(bundle1);
//                setResult(BACK_POINT_LIST,intent1);
                finish();
                break;
            case R.id.point_ok:
                String name =addPointEditName.getText().toString();
                String Lat =addPointEditLat.getText().toString();
                String Lon =addPointEditLon.getText().toString();
                String Ele =addPointEditEle.getText().toString();
                String Descript =addPointEditDescript.getText().toString();
                String Mileage =addPointEditMileage.getText().toString();
                if(!TextUtils.isEmpty(Lat) || !TextUtils.isEmpty(Lon) || !TextUtils.isEmpty(Ele) ||
                        !TextUtils.isEmpty(Descript) || !TextUtils.isEmpty(Mileage)){
                    Point_List point_list =new Point_List(Lat,Lon,Ele,Mileage,Descript);
                    Intent intent =new Intent();
                    Bundle bundle =new Bundle();
                    bundle.putSerializable(Util.ADD_POINT_BUNDLE,point_list);
                    bundle.putString(Util.ADD_POINT_NAME,name);
                    intent.putExtras(bundle);
                    setResult(BACK_POINT_LIST,intent);
                    finish();
                }else {
                    ToastUtil.Long(this,getResources().getString(R.string.add_fail));
                }

                break;
        }
    }
}
