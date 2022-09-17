/**
 * 
 */
package com.mtime.bussiness.ticket.cinema.adapter;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.aspsine.irecyclerview.IViewHolder;
import com.mtime.R;
import com.mtime.bussiness.ticket.cinema.bean.CinemaOffenGoBean;
import com.mtime.mtmovie.widgets.pullrefresh.OnItemClickListener;
import com.mtime.mtmovie.widgets.pullrefresh.OnItemLongClickListener;

import java.util.ArrayList;
import java.util.List;

/**
 * @author wangjin
 * 
 */
public class OffenGoCinemaAdapter extends RecyclerView.Adapter<OffenGoCinemaAdapter.ViewHolder> {
    private final Context context;
    private final List<CinemaOffenGoBean> offenGoBeans = new ArrayList<CinemaOffenGoBean>();
    private OnItemClickListener onItemClickListener;
    private OnItemLongClickListener onItemLongClickListener;
    
    public OffenGoCinemaAdapter(final Context context, final List<CinemaOffenGoBean> offenGoBeans) {
        this.context = context;
        if (null != offenGoBeans) {
            this.offenGoBeans.addAll(offenGoBeans);
        }
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }
    public void setOnItemLongClickListener(OnItemLongClickListener onItemLongClickListener) {
        this.onItemLongClickListener = onItemLongClickListener;
    }

    public void setOffenGobean(final List<CinemaOffenGoBean> offenGoBeans) {
        if (null == offenGoBeans || offenGoBeans.isEmpty()) {
            return;
        }
        
        this.offenGoBeans.clear();
        this.offenGoBeans.addAll(offenGoBeans);
        this.notifyDataSetChanged();
    }
    
    public void addOffenGobean(final List<CinemaOffenGoBean> offenGoBeans) {
        this.offenGoBeans.addAll(offenGoBeans);
        this.notifyDataSetChanged();
    }
    
    public void clear() {
        this.offenGoBeans.clear();
        this.notifyDataSetChanged();
    }
    
    public List<CinemaOffenGoBean> getList() {
        return this.offenGoBeans;
    }
    
    public void removeItem(int pos) {
        this.offenGoBeans.remove(pos);
        this.notifyDataSetChanged();
    }
    
    public void removeItem(CinemaOffenGoBean item) {
        this.offenGoBeans.remove(item);
        this.notifyDataSetChanged();
    }

    public int getCount() {
        return offenGoBeans.size();
    }
    
    public Object getItem(final int arg0) {
        return arg0;
    }
    
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.offengo_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, int position) {
        viewHolder.name.setText(offenGoBeans.get(position).getName());
        viewHolder.adress.setText(offenGoBeans.get(position).getAdress());
        if (onItemClickListener != null) {
            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //注意，这里的position不要用上面参数中的position，会出现位置错乱\
                    onItemClickListener.onItemClick(viewHolder.itemView, viewHolder.getIAdapterPosition());
                }
            });
        }
        if (onItemLongClickListener != null){
            viewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    onItemLongClickListener.onItemLongClick(viewHolder.itemView, viewHolder.getIAdapterPosition());
                    return false;
                }
            });
        }
    }

    public long getItemId(final int arg0) {
        return arg0;
    }

    @Override
    public int getItemCount() {
        return offenGoBeans.size();
    }

    public class ViewHolder extends IViewHolder {
        TextView name;
        TextView adress;

        public ViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.offengo_name);
            adress = itemView.findViewById(R.id.offengo_adress);
        }
    }
    
}
