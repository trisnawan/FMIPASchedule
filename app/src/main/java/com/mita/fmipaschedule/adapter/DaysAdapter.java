package com.mita.fmipaschedule.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mita.fmipaschedule.Interface.ListInterface;
import com.mita.fmipaschedule.R;
import com.mita.fmipaschedule.model.DaysModel;
import com.mita.fmipaschedule.model.MatkulModel;

import java.util.List;

public class DaysAdapter extends RecyclerView.Adapter<DaysAdapter.MyViewHolder> {
    private final Context context;
    private final List<DaysModel> list;
    private ListInterface listInterface;

    public DaysAdapter(Context context, List<DaysModel> list) {
        this.context = context;
        this.list = list;
    }

    public void setListInterface(ListInterface listInterface) {
        this.listInterface = listInterface;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_days, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.name.setText(list.get(position).getName());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder{
        TextView name;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            itemView.setOnClickListener(v -> listInterface.onClick(itemView, getLayoutPosition()));
        }
    }
}
