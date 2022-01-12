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
import com.mita.fmipaschedule.model.UserModel;

import java.util.List;

public class DosenAdapter extends RecyclerView.Adapter<DosenAdapter.MyViewHolder> {
    private final Context context;
    private final List<UserModel> list;
    private ListInterface listInterface;

    public DosenAdapter(Context context, List<UserModel> list) {
        this.context = context;
        this.list = list;
    }

    public void setListInterface(ListInterface listInterface) {
        this.listInterface = listInterface;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_dosen, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.name.setText(list.get(position).getName());
        holder.reg.setText(list.get(position).getReg());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder{
        TextView name, reg;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            reg = itemView.findViewById(R.id.reg);
            itemView.setOnClickListener(v -> listInterface.onClick(itemView, getLayoutPosition()));
        }
    }
}
