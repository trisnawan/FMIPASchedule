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
import com.mita.fmipaschedule.app.AppHelper;
import com.mita.fmipaschedule.app.NumberHelper;
import com.mita.fmipaschedule.model.DaysModel;
import com.mita.fmipaschedule.model.ScheduleModel;

import java.util.List;

public class ScheduleAdapter extends RecyclerView.Adapter<ScheduleAdapter.MyViewHolder> {
    private final Context context;
    private final List<ScheduleModel> list;
    private ListInterface listInterface;

    public ScheduleAdapter(Context context, List<ScheduleModel> list) {
        this.context = context;
        this.list = list;
    }

    public void setListInterface(ListInterface listInterface) {
        this.listInterface = listInterface;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_schedule, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.time.setText(NumberHelper.timeFormat(list.get(position).getTimeStart(), list.get(position).getTimeEnd(), "-"));
        holder.room.setText(list.get(position).getKelas());
        holder.code.setText(list.get(position).getMatkul().getId());
        holder.title.setText(list.get(position).getMatkul().getName());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder{
        TextView time, room, code, title;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            time = itemView.findViewById(R.id.time);
            room = itemView.findViewById(R.id.room);
            code = itemView.findViewById(R.id.code);
            title = itemView.findViewById(R.id.title);
            itemView.setOnClickListener(v -> listInterface.onClick(itemView, getLayoutPosition()));
        }
    }
}
