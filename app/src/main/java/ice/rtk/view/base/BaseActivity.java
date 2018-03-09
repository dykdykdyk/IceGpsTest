package ice.rtk.view.base;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by 111 on 2017/12/28.
 */

public abstract class BaseActivity extends AppCompatActivity {

    private Unbinder bind;
    protected Activity activity;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(layout());
        activity = this;
        bind = ButterKnife.bind(this);
        init();
        setListener();
        //隐藏标题栏
        getSupportActionBar().hide();
//        initAdaptation();
    }

    protected abstract int layout();

    protected abstract void init();

    protected abstract void setListener();

    @Override
    protected void onDestroy() {
        super.onDestroy();
        bind.unbind();
    }

    protected void startActivity(Class clazz) {
        Intent intent = new Intent(this, clazz);
        startActivity(intent);
    }

    //    public void initAdaptation(){//适配状态栏
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            Window window = getWindow();
//            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
//            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
//            window.setStatusBarColor(Color.TRANSPARENT);
//        }
//    }
    public  void RunOnUI(final TextView textView, final String str) {
       runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(textView !=null){
                    textView.setText(str);
                }
            }
        });
    }

}
