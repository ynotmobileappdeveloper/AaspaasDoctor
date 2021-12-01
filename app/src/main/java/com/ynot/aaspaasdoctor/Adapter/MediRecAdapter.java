package com.ynot.aaspaasdoctor.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ynot.aaspaasdoctor.Model.Medipriscription;
import com.ynot.aaspaasdoctor.R;

import java.util.List;

public class MediRecAdapter extends RecyclerView.Adapter<MediRecAdapter.MyViewHolder> {
    Context context;
    List<Medipriscription> medipriscriptionList;
    Click listener;

    public MediRecAdapter(Context context, List<Medipriscription> medipriscriptionList, Click listener) {
        this.context = context;
        this.medipriscriptionList = medipriscriptionList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view=LayoutInflater.from(parent.getContext()).inflate(R.layout.type_layout,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {
        holder.qty.setText(medipriscriptionList.get(position).getQty());
        holder.no.setText(position + 1 + "");
        holder.days.setText(medipriscriptionList.get(position).getDays());
        holder.name.setText(medipriscriptionList.get(position).getMedicine_name());

        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.delete(position);
            }
        });
    }


    @Override
    public int getItemCount() {
        return medipriscriptionList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView name, no, days, qty;
        ImageView delete;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            no = itemView.findViewById(R.id.no);
            days = itemView.findViewById(R.id.days);
            qty = itemView.findViewById(R.id.qty);
            delete = itemView.findViewById(R.id.delete);
        }
    }

    public interface Click {
        void delete(int position);
    }
}
