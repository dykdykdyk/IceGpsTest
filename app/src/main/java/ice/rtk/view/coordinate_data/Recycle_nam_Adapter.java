package ice.rtk.view.coordinate_data;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import ice.rtk.R;

/**
 * Created by Administrator on 2018/1/24.
 */

public class Recycle_nam_Adapter extends RecyclerView.Adapter<Recycle_nam_Adapter.ViewHolder>{

    private Context context ;
    private ArrayList<Point_Name> datas;

    public Recycle_nam_Adapter(Context context, ArrayList<Point_Name> datas) {
        this.context = context;
        this.datas = datas;
    }

    public void add(int i, Point_Name str) {
        datas.add(i,str);
        notifyItemInserted(i);
    }

    public void remove(int i) {
        datas.remove(i);
        notifyItemRemoved(i);
    }
    public void set(int i, Point_Name str){
        datas.set(i,str);
        notifyItemChanged(i);
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView =View.inflate(context,R.layout.item_recycle_name,null);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String name =datas.get(position).getName();
        holder.name.setText(name);
    }


    @Override
    public int getItemCount() {
        return datas.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        private TextView name;
        public ViewHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.item_recycle_name);
        }
    }
}
