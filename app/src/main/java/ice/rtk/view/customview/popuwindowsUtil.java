package ice.rtk.view.customview;

import android.app.Activity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.PopupWindow;
import android.widget.TextView;

import ice.rtk.R;

/**
 * Created by Administrator on 2018/1/23.
 */

public class popuwindowsUtil {

    private void showPopupWindow(final Activity activity) {
        //设置contentView
        PopupWindow mPopWindow;
        View contentView = LayoutInflater.from(activity).inflate(R.layout.item_popu_lofting, null);
        mPopWindow = new PopupWindow(contentView,
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, true);
        mPopWindow.setContentView(contentView);
        //设置各个控件的点击响应
//        TextView tv1 = (TextView)contentView.findViewById(R.id.pop_computer);
//        TextView tv2 = (TextView)contentView.findViewById(R.id.pop_financial);
//        TextView tv3 = (TextView)contentView.findViewById(R.id.pop_manage);
//        tv1.setOnClickListener(this);
//        tv2.setOnClickListener(this);
//        tv3.setOnClickListener(this);
        //显示PopupWindow
        View rootview = LayoutInflater.from(activity).inflate(R.layout.activity_main, null);
        mPopWindow.showAtLocation(rootview, Gravity.BOTTOM, 0, 0);
        backgroundAlpha(0.5f,activity);
        mPopWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                backgroundAlpha(1f,activity);
            }
        });
    }

    /**
     *
     * @param bgAlpha
     * @param v
     */
    public void backgroundAlpha(float bgAlpha,  Activity v) {
        WindowManager.LayoutParams lp =v.getWindow().getAttributes();
        lp.alpha = bgAlpha; //0.0-1.0
        v.getWindow().setAttributes(lp);
    }
}
