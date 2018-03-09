package ice.rtk.view.coordinate_data.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nononsenseapps.filepicker.FilePickerActivity;
import com.nononsenseapps.filepicker.Utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import ice.rtk.R;
import ice.rtk.Utils.LogUtil;
import ice.rtk.Utils.PopupWindow.CommonPopupWindow;
import ice.rtk.Utils.Util;
import ice.rtk.Utils.gpxparser.GPXParser;
import ice.rtk.Utils.gpxparser.modal.GPX;
import ice.rtk.Utils.gpxparser.modal.Route;
import ice.rtk.Utils.gpxparser.modal.Waypoint;
import ice.rtk.view.LinesView.LinesActivity;
import ice.rtk.view.PointView.PointActivity;
import ice.rtk.view.base.BaseFragment;
import ice.rtk.view.coordinate_data.Activity_Addpoint;
import ice.rtk.view.coordinate_data.Point_List;
import ice.rtk.view.coordinate_data.Point_Name;
import ice.rtk.view.coordinate_data.Recycle_list_Adapter;
import ice.rtk.view.coordinate_data.Recycle_nam_Adapter;
import ice.rtk.view.coordinate_data.customView.IHorizontalScrollView;

/**
 * 作者: 邓洋康 on 2018/01/24 21:41
 * 作用:XXX
 */

public class Fragment_Lines extends BaseFragment implements View.OnClickListener {
    @BindView(R.id.recycle_name)
    RecyclerView mRecycleName;
    @BindView(R.id.image_left)
    ImageView mImageLeft;
    @BindView(R.id.recycle_list)
    RecyclerView mRecycleList;
    @BindView(R.id.hsv)
    IHorizontalScrollView mHsv;
    @BindView(R.id.image_right)
    ImageView mImageRight;
    @BindView(R.id.new_point)
    TextView mNewPoint;
    @BindView(R.id.open)
    TextView mOpen;
    @BindView(R.id.search)
    TextView mSearch;
    @BindView(R.id.more)
    TextView mMore;
    @BindView(R.id.title_bottom)
    LinearLayout titleBottom;
    @BindView(R.id.delete)
    TextView delete;
    @BindView(R.id.edit)
    TextView edit;
    @BindView(R.id.title_edit_or_delete)
    LinearLayout titleEditOrDelete;
    @BindView(R.id.insert_pre)
    TextView insertPre;
    @BindView(R.id.insert_after)
    TextView insertAfter;
    @BindView(R.id.lofting_point)
    TextView loftingPoint;
    @BindView(R.id.look)
    TextView look;
    Unbinder unbinder;

    private CommonPopupWindow popupWindow;
    ArrayList<Point_Name> name;
    ArrayList<Point_List> list;
    final int left = 0;
    final int right = 1;
    int currentScroll;
    Recycle_nam_Adapter recycle_nam_adapter;
    Recycle_list_Adapter recycle_list_adapter;
    private static final int FILE_CODE = 2;
    private static final int BACK_POINT_LIST = 1;
    private static final int BACK_POINT_LIST_EDIT = 3;
    private static final int INSERT_POINT_PRE = 4;
    private static final int INSERT_POINT_AFTER = 5;

    //recycle_list_adapter选中的位置
    private int selece_position = 0;

    @Override
    protected View childImpl() {
        return View.inflate(getActivity(), R.layout.fragment_lines, null);
    }

    @Override
    protected void init() {
        initname();
    }

    @Override
    protected void setListener() {
        mRecycleName.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (MotionEvent.ACTION_UP == motionEvent.getAction()) {
                    mRecycleName.stopScroll();
                    mRecycleList.stopScroll();
                    return true;
                }
                return false;
            }
        });
        mRecycleList.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (MotionEvent.ACTION_UP == motionEvent.getAction()) {
                    mRecycleName.stopScroll();
                    mRecycleList.stopScroll();
                    return true;
                }
                return false;
            }
        });
        mRecycleName.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                currentScroll = left;
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (currentScroll == left)
                    mRecycleList.scrollBy(dx, dy);
            }
        });
        mRecycleList.addOnScrollListener(new RecyclerView.OnScrollListener() {
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
                    mRecycleName.scrollBy(dx, dy);
            }
        });


        mHsv.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    mHsv.startScrollerTask();
                }
                return false;
            }
        });
        mHsv.setOnScrollStopListner(new IHorizontalScrollView.OnScrollStopListner() {
            @Override
            public void onScrollStoped() {

            }

            @Override
            public void onScrollToLeftEdge() {
                if (getActivity() != null) {
                    mImageRight.setVisibility(View.VISIBLE);
                    mImageLeft.setVisibility(View.INVISIBLE);
                }

            }

            @Override
            public void onScrollToRightEdge() {
                if (getActivity() != null) {
                    mImageLeft.setVisibility(View.VISIBLE);
                    mImageRight.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onScrollToMiddle() {
                if (getActivity() != null) {
                    mImageLeft.setVisibility(View.VISIBLE);
                    mImageRight.setVisibility(View.VISIBLE);
                }
            }
        });
        //设置list的点击时间监听
        recycle_list_adapter.setOnItemClickListener(new Recycle_list_Adapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                titleBottom.setVisibility(View.GONE);
                titleEditOrDelete.setVisibility(View.VISIBLE);
                selece_position = position;
            }
        });
        mNewPoint.setOnClickListener(this);
        mOpen.setOnClickListener(this);
        mSearch.setOnClickListener(this);
        mMore.setOnClickListener(this);
    }

    private void initname() {
        name = new ArrayList<>();
        name.add(new Point_Name(getResources().getString(R.string.point_name)));
//        for (int i = 0; i < 5; i++) {
//            name.add(new Point_Name(i + ""));
//        }
        recycle_nam_adapter = new Recycle_nam_Adapter(getActivity(), name);
        mRecycleName.setAdapter(recycle_nam_adapter);
        mRecycleName.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        list = new ArrayList<Point_List>();
        list.add(new Point_List(getResources().getString(R.string.lat), getResources().getString(R.string.lon), getResources().getString(R.string.ele),
                getResources().getString(R.string.mileage), getResources().getString(R.string.descript)));
//        for (int i = 0; i < 5; i++) {
//            list.add(new Point_List("12.3432" + i, "554.2321" + i, "54.3" + i, i + "km", "不可" + i));
//        }
        recycle_list_adapter = new Recycle_list_Adapter(getActivity(), list);
        mRecycleList.setAdapter(recycle_list_adapter);
        mRecycleList.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.new_point:
                startActivityForResult(new Intent(getActivity(), Activity_Addpoint.class), BACK_POINT_LIST);
                break;
            case R.id.open:
//                startActivity(new Intent(getActivity(), FileActivity.class));
                Intent i = new Intent(getContext(), FilePickerActivity.class);
                // This works if you defined the intent filter
                // Intent i = new Intent(Intent.ACTION_GET_CONTENT);

                // Set these depending on your use case. These are the defaults.
                i.putExtra(FilePickerActivity.EXTRA_ALLOW_MULTIPLE, false);
                i.putExtra(FilePickerActivity.EXTRA_ALLOW_CREATE_DIR, false);
                i.putExtra(FilePickerActivity.EXTRA_MODE, FilePickerActivity.MODE_FILE);

                // Configure initial directory by specifying a String.
                // You could specify a String like "/storage/emulated/0/", but that can
                // dangerous. Always use Android's API calls to get paths to the SD-card or
                // internal memory.
                File file = new File(Environment.getExternalStorageDirectory().getPath() + "/" + Util.APP_SDCARD_PATH);
                if (!file.exists()) {
                    file.mkdirs();
                }
                i.putExtra(FilePickerActivity.EXTRA_START_PATH, file.getAbsolutePath());
                startActivityForResult(i, FILE_CODE);

//                ParseFile(file);
                break;
            case R.id.search:
//                startActivity(new Intent(getActivity(), FileActivity.class));
                break;
            case R.id.more:
//                startActivity(new Intent(getActivity(), FileActivity.class));
                if (popupWindow != null && popupWindow.isShowing())
                    return;
                popupWindow = new CommonPopupWindow.Builder(getActivity())
                        .setView(R.layout.item_popu_lofting)
                        .setWidthAndHeight(350, ViewGroup.LayoutParams.WRAP_CONTENT)
                        .setViewOnclickListener(new CommonPopupWindow.ViewInterface() {
                            @Override
                            public void getChildView(View view, int layoutResId) {
                                switch (layoutResId) {
                                    case R.layout.item_popu_lofting:
                                        TextView setting = view.findViewById(R.id.setting);
                                        TextView batch = view.findViewById(R.id.batch);
                                        TextView add = view.findViewById(R.id.add);
                                        TextView create_new_file = view.findViewById(R.id.create_new_file);
                                        setting.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {

                                            }
                                        });
                                        batch.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {

                                            }
                                        });
                                        add.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                startActivity(Activity_Addpoint.class);
                                            }
                                        });
                                        create_new_file.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
//                                                startActivity(Activity_Addpoint.class);
                                                showDia();
                                            }
                                        });
                                        break;
                                }
                            }
                        }).create();
                popupWindow.showAsDropDown(mRecycleList, mRecycleList.getWidth(), -mRecycleList.getHeight());
                break;
        }
    }

    private void showDia() {
        AlertDialog.Builder ad = new AlertDialog.Builder(getActivity());
        View view = getLayoutInflater().inflate(R.layout.editdata, null);
        final EditText editText = view.findViewById(R.id.edit);
        ad.setView(view);
        editText.setText("Line1");
        ad.setTitle(getResources().getString(R.string.create_new_file))
                .setMessage("请输入文件名？")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            File file = new File(Util.FILESPATHLINES + editText.getText().toString() + ".gpx");
                            if (!file.exists()) {
                                file.getParentFile().mkdirs();
                            }
                            file.createNewFile();
                            if (file.length() == 0) {
                                writeData(file, editText.getText().toString());
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                })
                .setNegativeButton("取消", null).create().show();
    }

    /**
     * @param file     创建的航线文件名
     * @param linename 默认航线里面的航线名和文件名字相同
     */
    public void writeData(File file, String linename) {
        FileWriter fw = null;
        BufferedWriter bw = null;
        try {
            fw = new FileWriter(file, true);
            bw = new BufferedWriter(fw);
            bw.write(Util.CreateLinesHead(linename));
            bw.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (bw != null) {
                try {
                    bw.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (fw != null) {
                try {
                    fw.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    File file;

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (requestCode == FILE_CODE && resultCode == Activity.RESULT_OK) {
            // Use the provided utility method to parse the result
            List<Uri> files = Utils.getSelectedFilesFromResult(intent);
            for (Uri uri : files) {
                File file_ = Utils.getFileForUri(uri);
                Log.i("TAG 选中的文件:", file_.getAbsolutePath());
                //解析数据
                if (file_ != null) {
                    this.file = file_;
//                    Tools.parseFileData(file);
                    ParseFile(file);
                }
            }
            //添加的点
        } else if (requestCode == BACK_POINT_LIST && resultCode == Activity.RESULT_FIRST_USER) {
            Point_List point_list = (Point_List) intent.getSerializableExtra(Util.ADD_POINT_BUNDLE);
            Point_Name point_name = new Point_Name(intent.getExtras().getString(Util.ADD_POINT_NAME));

            name.add(point_name);
            recycle_nam_adapter.notifyItemInserted(name.size() - 1);
            //第四项 默认选择添加时间
            list.add(point_list);
            recycle_list_adapter.notifyItemInserted(list.size() - 1);

            String time = point_name.getName();
//            if(time==null){
            time = Util.getDataTZ();
//            }

            float lat = 11.1111111111f;
            float lon = 144.23234324f;
            float ele = 60.8f;
            //添加点 到当前文件 默认插入到最后点的最后面
            String str = "<rtept  lat=\"" + point_list.getLat() + "" + "\"  lon=\"" + point_list.getLon() + "" + "\">\n" +
                    "       <ele>" + ele + "</ele>\n" +
                    "       <time>" + time + "</time>\n" +
                    "       <name>P000W</name>\n" +
                    "       <cmt></cmt>\n" +
                    "       <desc>2016-06-24 04:12</desc>\n" +
                    "       <sym>0</sym>\n" +
                    "     </rtept>\n" +
                    "</rte></gpx>\n";
            Util.AddData(str, list.get(selece_position).getLat(), 1, file);
            //编辑的
        } else if (requestCode == BACK_POINT_LIST_EDIT && resultCode == Activity.RESULT_FIRST_USER) {
            if (intent == null)
                return;
            Point_List point_list = (Point_List) intent.getSerializableExtra(Util.ADD_POINT_BUNDLE);
            Point_Name point_name = new Point_Name(intent.getExtras().getString(Util.ADD_POINT_NAME));
            //保存到文件流中
            Util.SaveChange(name.get(selece_position), list.get(selece_position), point_name, point_list);
//            name.add(point_name);
            name.set(selece_position, point_name);
            recycle_nam_adapter.set(selece_position, point_name);
            LogUtil.e(getClass(), selece_position + ",selece_position");
//            recycle_nam_adapter.notifyItemInserted(selece_position);
            //第四项 默认选择添加时间
            list.set(selece_position, point_list);
            recycle_list_adapter.set(selece_position, point_list);
        } else if (requestCode == INSERT_POINT_PRE && resultCode == Activity.RESULT_FIRST_USER) {
            if (intent == null)
                return;
            float ele = 60.8f;
            String time = Util.getDataTZ();
//            if(time==null){
            Point_List point_list = (Point_List) intent.getSerializableExtra(Util.ADD_POINT_BUNDLE);
            Point_Name point_name = new Point_Name(intent.getExtras().getString(Util.ADD_POINT_NAME));
            String str = "<rtept  lat=\"" + point_list.getLat() + "" + "\"  lon=\"" + point_list.getLon() + "" + "\">\n" +
                    "       <ele>" + ele + "</ele>\n" +
                    "       <time>" + time + "</time>\n" +
                    "       <name>P000W</name>\n" +
                    "       <cmt></cmt>\n" +
                    "       <desc>2016-06-24 04:12</desc>\n" +
                    "       <sym>0</sym>\n" +
                    "     </rtept>\n";
            Util.AddData(str, list.get(selece_position).getLat(), 0, file);

//                name.add(selece_position,point_name);
//                list.add(selece_position, point_list);
            recycle_nam_adapter.add(selece_position, point_name);
            recycle_list_adapter.add(selece_position, point_list);

            LogUtil.e(getClass(), selece_position + ",selece_position");
        } else if (requestCode == INSERT_POINT_AFTER && resultCode == Activity.RESULT_FIRST_USER) {
            if (intent == null)
                return;
            float ele = 60.8f;
            String time = Util.getDataTZ();
//            if(time==null){
            Point_List point_list = (Point_List) intent.getSerializableExtra(Util.ADD_POINT_BUNDLE);
            Point_Name point_name = new Point_Name(intent.getExtras().getString(Util.ADD_POINT_NAME));
            String str = "<rtept  lat=\"" + point_list.getLat() + "" + "\"  lon=\"" + point_list.getLon() + "" + "\">\n" +
                    "       <ele>" + ele + "</ele>\n" +
                    "       <time>" + time + "</time>\n" +
                    "       <name>P000W</name>\n" +
                    "       <cmt></cmt>\n" +
                    "       <desc>2016-06-24 04:12</desc>\n" +
                    "       <sym>0</sym>\n" +
                    "     </rtept>\n";
            Util.AddData(str, list.get(selece_position).getLat(), 1, file);
//            name.add(selece_position + 1, point_name);
//            list.add(selece_position + 1, point_list);
            recycle_nam_adapter.add(selece_position + 1, point_name);
            recycle_list_adapter.add(selece_position + 1, point_list);
            LogUtil.i(getClass(), "requestCode:" + requestCode + ",resultCode:" + resultCode + ",intent:" + intent);
        }
    }

    private void ParseFile(File file) {
        GPXParser p = new GPXParser();
        try {
//            InputStream in =getClass().getClassLoader().getResourceAsStream("assets/default.gpx");
//            InputStream in = getContext().getAssets().open("default.gpx");
            InputStream in = new FileInputStream(file);
            GPX gpx = p.parseGPX(in);
            ArrayList<Waypoint> arrayList = gpx.getWaypoints();
            //清空点
            initAdapter();
            for (Route way : gpx.getRoutes()) {
                for (Waypoint waypoint : way.getRoutePoints()) {
                    Log.i("TAG", "waypoint:" + waypoint);
                    Point_Name point_name = new Point_Name(waypoint.getName());
                    Point_List point_list = new Point_List(waypoint.getLatitude() + "", waypoint.getLongitude() + "", waypoint.getElevation() + "",
                            waypoint.getTime().toString(), waypoint.getDescription());
                    name.add(point_name);
                    recycle_nam_adapter.notifyItemInserted(name.size() - 1);
                    //第四项 默认选择添加时间
                    list.add(point_list);
                    recycle_list_adapter.notifyItemInserted(list.size() - 1);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initAdapter() {
        name.clear();
        recycle_nam_adapter.notifyDataSetChanged();
        list.clear();
        recycle_list_adapter.notifyDataSetChanged();
        name.add(new Point_Name(getResources().getString(R.string.point_name)));
        list.add(new Point_List(getResources().getString(R.string.lat), getResources().getString(R.string.lon), getResources().getString(R.string.ele),
                getResources().getString(R.string.mileage), getResources().getString(R.string.descript)));

    }

    @OnClick({R.id.delete, R.id.edit, R.id.insert_pre, R.id.insert_after,R.id.lofting_point, R.id.look})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.delete:
                name.remove(selece_position);
                recycle_nam_adapter.notifyItemRemoved(selece_position);
                //第四项 默认选择添加时间
                list.remove(selece_position);
                recycle_list_adapter.notifyItemRemoved(selece_position);

                //更新UI
                titleBottom.setVisibility(View.VISIBLE);
                titleEditOrDelete.setVisibility(View.GONE);
                recycle_list_adapter.setBackColor();
                break;
            case R.id.edit:
//                name.remove(selece_position);
//                recycle_nam_adapter.notifyItemRemoved(selece_position);
//                //第四项 默认选择添加时间
//                list.remove(selece_position);
//                recycle_list_adapter.notifyItemRemoved(selece_position);
                //更新UI
                titleBottom.setVisibility(View.VISIBLE);
                titleEditOrDelete.setVisibility(View.GONE);
                recycle_list_adapter.setBackColor();
                Point_Name point_name = name.get(selece_position);
                Point_List point_list = list.get(selece_position);
                Intent intent = new Intent(getActivity(), Activity_Addpoint.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable(Util.ADD_POINT_BUNDLE, point_list);
                bundle.putString(Util.ADD_POINT_NAME, point_name.getName());
                intent.putExtras(bundle);
                recycle_list_adapter.setBackColor();
                startActivityForResult(intent, BACK_POINT_LIST_EDIT);
                break;

            case R.id.insert_pre:
                titleBottom.setVisibility(View.VISIBLE);
                titleEditOrDelete.setVisibility(View.GONE);
                recycle_list_adapter.setBackColor();
//                Point_Name point_name_pre = name.get(selece_position);
//                Point_List point_list_pre = list.get(selece_position);
                Intent intent_pre = new Intent(getActivity(), Activity_Addpoint.class);
                Bundle bundle_pre = new Bundle();
//                bundle_pre.putSerializable(Util.ADD_POINT_BUNDLE, point_list_pre);
//                bundle_pre.putString(Util.ADD_POINT_NAME, point_name_pre.getName());
                bundle_pre.putString(Util.INSERT_POINT, Util.INSERT_POINT_PRE);
                intent_pre.putExtras(bundle_pre);
                recycle_list_adapter.setBackColor();
                startActivityForResult(intent_pre, INSERT_POINT_PRE);
                break;
            case R.id.insert_after:
                titleBottom.setVisibility(View.VISIBLE);
                titleEditOrDelete.setVisibility(View.GONE);
                recycle_list_adapter.setBackColor();
//                Point_Name point_name_after = name.get(selece_position);
//                Point_List point_list_after = list.get(selece_position);
                Intent intent_after = new Intent(getActivity(), Activity_Addpoint.class);
                Bundle bundle_after = new Bundle();
//                bundle_after.putSerializable(Util.ADD_POINT_BUNDLE, point_list_after);
//                bundle_after.putString(Util.ADD_POINT_NAME, point_name_after.getName());
                bundle_after.putString(Util.INSERT_POINT, Util.INSERT_POINT_AFTER);
                intent_after.putExtras(bundle_after);
                recycle_list_adapter.setBackColor();
                startActivityForResult(intent_after, INSERT_POINT_AFTER);
                break;

            case R.id.lofting_point:
                Intent inten1 =new Intent(getActivity(), LinesActivity.class);
                inten1.putExtra(Util.DRAW_POINT_LAT, list.get(selece_position).getLat());
                inten1.putExtra(Util.DRAW_POINT_LON,list.get(selece_position).getLon());
                inten1.putExtra(Util.DRAW_POINT_ELE,list.get(selece_position).getEle());
                startActivity(inten1);
                break;
            case R.id.look:
                break;
        }
    }

    public boolean cancleSelect() {
        LogUtil.i(getClass(), "Select_flag:" + recycle_list_adapter.getSelect_flag());
        if (recycle_list_adapter.getSelect_flag() == 0) {
            LogUtil.i(getClass(), "=0:");
            return false;
        } else {
            LogUtil.i(getClass(), "=1:");
            titleBottom.setVisibility(View.VISIBLE);
            titleEditOrDelete.setVisibility(View.GONE);
            recycle_list_adapter.setBackColor();
            return true;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // TODO: inflate a fragment view
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        unbinder = ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
