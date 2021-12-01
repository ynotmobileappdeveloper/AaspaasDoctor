package com.ynot.aaspaasdoctor.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ynot.aaspaasdoctor.Model.HistoryModel;
import com.ynot.aaspaasdoctor.R;

import java.util.ArrayList;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {

    Context context;
    ArrayList<HistoryModel> model;

    public HistoryAdapter(Context context, ArrayList<HistoryModel> model) {
        this.context = context;
        this.model = model;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.history_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final HistoryModel list = model.get(position);
        holder.name.setText(list.getName());
        holder.gender.setText(list.getGender());
        holder.age.setText(list.getAge());
        holder.date.setText(list.getDate());
        holder.time.setText(list.getTime());
        holder.location.setText(list.getLocation());
        holder.symptoms.setText(list.getSymptoms());

        if (list.getSymptoms().equals("1")) {
            holder.symptoms.setText("Approved");
        } else if (list.getSymptoms().equals("2")) {
            holder.symptoms.setText("Rejected");
            holder.symptoms.setTextColor(context.getResources().getColor(android.R.color.holo_red_dark));
        } else {
            holder.symptoms.setText("Pending");
        }


    }

    @Override
    public int getItemCount() {
        return model.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView name, gender, age, date, time, location, symptoms;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.test);
            gender = itemView.findViewById(R.id.gender);
            age = itemView.findViewById(R.id.age);
            date = itemView.findViewById(R.id.date);
            time = itemView.findViewById(R.id.time);
            location = itemView.findViewById(R.id.location);
            symptoms = itemView.findViewById(R.id.symptoms);
        }
    }


}
