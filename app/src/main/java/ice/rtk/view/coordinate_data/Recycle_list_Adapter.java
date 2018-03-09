package ice.rtk.view.coordinate_data;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import ice.rtk.R;
import ice.rtk.Utils.LogUtil;

/**
 * Created by Administrator on 2018/1/24.
 */

public class Recycle_list_Adapter extends RecyclerView.Adapter<Recycle_list_Adapter.ViewHolder> {
    private Context context;
    private ArrayList<Point_List> datas;
    private int select_flag =0;//是否已经选中某一项的标志位
    public int getSelect_flag() {
        return select_flag;
    }

    public Recycle_list_Adapter(Context context, ArrayList<Point_List> datas) {
        this.context = context;
        this.datas = datas;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//        View itemView =View.inflate()
        View  itemView =View.inflate(context, R.layout.item_recycle_list,null);
        return new ViewHolder(itemView);
    }
    public void add(int i, Point_List str) {
        datas.add(i,str);
        notifyItemInserted(i);
    }

    public void remove(int i) {
        datas.remove(i);
        notifyItemRemoved(i);
    }
    public void set(int i, Point_List str){
        datas.set(i,str);
        notifyItemChanged(i);
    }
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.point_n.setText(datas.get(position).getLat());
        holder.point_e.setText(datas.get(position).getLon());
        holder.point_z.setText(datas.get(position).getEle());
        holder.point_mileage.setText(datas.get(position).getMileage());
        holder.point_descript.setText(datas.get(position).getDiscript());
        holder.sll.setTag(position);
//        holder.itemView.setTag(position);
    }

    @Override
    public int getItemCount() {
        return datas.size();
    }

    LinearLayout temp;
    class ViewHolder extends RecyclerView.ViewHolder{
        private TextView point_n;
        private TextView point_e;
        private TextView point_z;
        private TextView point_mileage;
        private TextView point_descript;
        private LinearLayout sll;
        public ViewHolder(View itemView) {
            super(itemView);
            point_n =itemView.findViewById(R.id.point_n);
            point_e =itemView.findViewById(R.id.point_e);
            point_z =itemView.findViewById(R.id.point_z);
            point_mileage =itemView.findViewById(R.id.point_mileage);
            point_descript =itemView.findViewById(R.id.point_descript);
            sll =itemView.findViewById(R.id.sll);
            temp =sll;
//            itemView.setOnLongClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//
//                }
//            });
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
//                    Toast.makeText(context, "data=="+datas.get(getLayoutPosition()), Toast.LENGTH_SHORT).show();
                    if(onItemClickListener != null){
//                        sll.setBackgroundColor(context.getResources().getColor(R.color.white));
                        if(select_flag==0){
                            temp =sll;
                            select_flag =1;
                            onItemClickListener.onItemClick(view,getLayoutPosition());
                            LogUtil.i(getClass(),"sll:"+sll);
                            sll.setBackgroundColor(context.getResources().getColor(R.color.tired_bg));
                        }
                    }
                    return true;
                }
            });
        }
    }
    public void setBackColor(){
        if(onItemClickListener != null  &&  temp !=null){
            LogUtil.i(getClass(),"sll:"+temp);
            temp.setBackgroundColor(context.getResources().getColor(R.color.white));
            select_flag =0;
        }
    }

    public void setView(){

    }
    /**
     * 点击RecyclerView某条的监听
     */
    public interface OnItemClickListener{

        /**
         * 当RecyclerView某个被点击的时候回调
         * @param view 点击item的视图
         * @param position 点击的位置
         */
        public void onItemClick(View view,int position);

    }

    private  OnItemClickListener onItemClickListener;

    /**
     * 设置RecyclerView某个的监听
     * @param onItemClickListener
     */
    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }
}
