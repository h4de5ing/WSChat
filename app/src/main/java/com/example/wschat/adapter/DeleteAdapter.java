package com.example.wschat.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.wschat.R;
import com.example.wschat.db.MessageItem;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class DeleteAdapter extends RecyclerView.Adapter<DeleteAdapter.Holder> {
    private Context mContent;
    private List<MessageItem> list;
    private HashMap<Long, Boolean> Maps = new HashMap<>();

    public DeleteAdapter(Context mContent, List<MessageItem> list) {
        this.mContent = mContent;
        this.list = list;
        initMap();
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(mContent).inflate(R.layout.delete_item, parent, false);
        return new Holder(itemView);
    }

    private void initMap() {
        for (int i = 0; i < list.size(); i++) {
            Maps.put(list.get(i).getId(), false);
        }
    }

    //全选方法
    public void All(boolean isChecked) {
        Set<Map.Entry<Long, Boolean>> entries = Maps.entrySet();
        for (Map.Entry<Long, Boolean> entry : entries) {
            entry.setValue(isChecked);
        }
        notifyDataSetChanged();
    }

    //反选
    public void neverAll() {
        Set<Map.Entry<Long, Boolean>> entries = Maps.entrySet();
        for (Map.Entry<Long, Boolean> entry : entries) {
            entry.setValue(!entry.getValue());
        }
        notifyDataSetChanged();
    }


    //获取最终的map存储数据
    public Map<Long, Boolean> getMap() {
        return Maps;
    }
    public void clear(){
        Maps.clear();
    }
    public RecyclerViewChangeListener onChangeListener;

    public void setChangeListener(RecyclerViewChangeListener changeListener) {
        this.onChangeListener = changeListener;
    }

    @Override
    public void onBindViewHolder(Holder holder, final int position) {
        holder.province_name.setText(list.get(position).toString());
        holder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            Maps.put(list.get(position).getId(), isChecked);
            onChangeListener.changed(position);
        });
        if (Maps.get(list.get(position).getId()) == null)
            Maps.put(list.get(position).getId(), false);
        holder.checkBox.setChecked(Maps.get(list.get(position).getId()));
        holder.setOnClickListener(v -> {
            Boolean aBoolean = Maps.get(list.get(position).getId());
            Maps.put(list.get(position).getId(), !aBoolean);
            holder.checkBox.setChecked(!aBoolean);
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class Holder extends RecyclerView.ViewHolder {
        private CheckBox checkBox;
        private TextView province_name;
        private View.OnClickListener listener;

        public void setOnClickListener(View.OnClickListener l) {
            listener = l;
        }

        public Holder(View itemView) {
            super(itemView);
            checkBox = itemView.findViewById(R.id.checkbox);
            province_name = itemView.findViewById(R.id.province_name);
            itemView.setOnClickListener(v -> {
                if (listener != null) listener.onClick(itemView);
            });
        }
    }

    public interface RecyclerViewChangeListener {
        void changed(int position);
    }
}