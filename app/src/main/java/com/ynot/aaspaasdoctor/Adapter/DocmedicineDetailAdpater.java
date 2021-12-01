package com.ynot.aaspaasdoctor.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ynot.aaspaasdoctor.Model.TypeModel;
import com.ynot.aaspaasdoctor.R;

import java.util.ArrayList;

public class DocmedicineDetailAdpater extends RecyclerView.Adapter<DocmedicineDetailAdpater.ViewHolder> {

    Context context;
    ArrayList<TypeModel> model;


    public DocmedicineDetailAdpater(Context context, ArrayList<TypeModel> model) {
        this.context = context;
        this.model = model;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.type_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        TypeModel list = model.get(position);
        holder.name.setText(list.getName());
        holder.no.setText(list.getId());
        holder.days.setText(list.getDays());
        holder.qty.setText(list.getQty());
        holder.name.setSelected(true);
        holder.delete.setVisibility(View.INVISIBLE);

    }

    @Override
    public int getItemCount() {
        return model.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView name, no, days, qty;
        ImageView delete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            no = itemView.findViewById(R.id.no);
            days = itemView.findViewById(R.id.days);
            qty = itemView.findViewById(R.id.qty);
            delete = itemView.findViewById(R.id.delete);
        }
    }


}
