package ice.rtk.view.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.clj.fastble.data.BleDevice;

import java.util.ArrayList;

import ice.rtk.R;


/**
 * Created by dengyangkang on 2017/5/15.
 */

public class ScannerAdapter extends BaseAdapter{
    LayoutInflater la;
    ArrayList<BleDevice> device_list;
    Context cx;
    int tempLevel;
    public ScannerAdapter(Context c, ArrayList<BleDevice> list){
        System.out.println("xxxxxxxxxxxxxx");
        device_list =list;
        cx = c;
        la =  LayoutInflater.from(c);
    }
    /**
     * How many items are in the data set represented by this Adapter.
     *
     * @return Count of items.
     */
    @Override
    public int getCount() {
        return device_list.size();
    }
    @Override
    public Object getItem(int position) {
        return device_list.get(position);
    }
    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;
         ViewHolder holder;
        // 判断convertView的状态，来达到复用效果
        if(null ==convertView){
            //如果convertView为空，则表示第一次显示该条目，需要创建一个view
            view =la.inflate(R.layout.scanner_listview_adapter_item,null);
            //新建一个viewholder对象
            holder =new ViewHolder();
            //将findviewbyID的结果赋值给holder对应的成员变量
            holder.bledevice_name =(TextView)view.findViewById(R.id.bledevice_name);
            holder.bledevice_mac =(TextView)view.findViewById(R.id.bledevice_mac);
//            holder.bledevice_rssi =(ImageView)view.findViewById(R.id.rssi_imageview);
            // 将holder与view进行绑定
            view.setTag(holder);
        }else{
            //否则表示可以复用convertView
            view =convertView;
            holder=(ViewHolder)view.getTag();
        }
        // 直接操作holder中的成员变量即可，不需要每次都findViewById
        holder.bledevice_name.setText(device_list.get(position).getName());
        holder.bledevice_mac.setText(device_list.get(position).getMac());
            tempLevel =device_list.get(position).getRssi();
//        holder.bledevice_rssi.setImageResource(R.drawable.iv_rssi_barp);
//            if(tempLevel<-97){
//                holder.bledevice_rssi.setImageLevel(10);
//            }else if(tempLevel<-80){
//                holder.bledevice_rssi.setImageLevel(28);
//            }else if(tempLevel<-55){
//                holder.bledevice_rssi.setImageLevel(45);
//            } else {
//                holder.bledevice_rssi.setImageLevel(100);
//            }
        return view;
    }
    private static class ViewHolder {
        private TextView bledevice_name;
        private TextView bledevice_mac;
//        private ImageView bledevice_rssi;
    }
}
