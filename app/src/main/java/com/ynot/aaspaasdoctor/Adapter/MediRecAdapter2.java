package com.ynot.aaspaasdoctor.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ynot.aaspaasdoctor.Model.Medipriscription;
import com.ynot.aaspaasdoctor.R;

import java.util.List;

public class MediRecAdapter2 extends RecyclerView.Adapter<MediRecAdapter2.MyViewHolder> {
    Context context;
    List<Medipriscription> medipriscriptionList;
    LayoutInflater layoutInflater;

    public MediRecAdapter2(Context context, List<Medipriscription> medipriscriptionList) {
        this.layoutInflater = LayoutInflater.from(context);
        this.context = context;
        this.medipriscriptionList = medipriscriptionList;
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.mediitem, null);
        MyViewHolder myViewHolder = new MyViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.qty.setText(medipriscriptionList.get(position).getQty());
        if (position == 0) {
            holder.no.setText("");
            holder.no.setBackgroundResource(R.color.trans);
            holder.days.setBackgroundResource(R.color.trans);
            holder.qty.setText("Qty");
            holder.days.setText("Days");
            holder.name.setText("Medicine");
            holder.qty.setBackgroundResource(R.color.trans);
            holder.name.setBackgroundResource(R.color.trans);

        } else {
            holder.no.setText(position + "");
            holder.days.setText(medipriscriptionList.get(position).getDays());
            holder.name.setText(medipriscriptionList.get(position).getMedicine_name());
        }
    }

    @Override
    public int getItemCount() {
        return medipriscriptionList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView no, name, qty, days;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            no = itemView.findViewById(R.id.no);
            name = itemView.findViewById(R.id.test);
            qty = itemView.findViewById(R.id.qty);
            days = itemView.findViewById(R.id.day);
        }
    }
}
