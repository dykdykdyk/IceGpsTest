package ice.rtk.view.base;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by Administrator on 2018/1/25.
 */

public abstract class BaseFragment extends Fragment {
    private Unbinder unBinder;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view =childImpl();
        unBinder = ButterKnife.bind(this,view);
        init();
        setListener();
        return view;
    }

    protected abstract  View childImpl();
    protected abstract  void init();
    protected abstract  void setListener();

    @Override
    public void onDestroy() {
        super.onDestroy();
        unBinder.unbind();
    }

    protected void startActivity(Class clazz) {
        Intent intent = new Intent(this.getActivity(), clazz);
        startActivity(intent);
    }
}
