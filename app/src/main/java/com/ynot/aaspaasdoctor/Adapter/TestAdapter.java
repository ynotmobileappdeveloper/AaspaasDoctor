package com.ynot.aaspaasdoctor.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.ynot.aaspaasdoctor.Model.SlotModel;
import com.ynot.aaspaasdoctor.R;

import java.util.ArrayList;

public class TestAdapter extends RecyclerView.Adapter<TestAdapter.ViewHolder> {

    Context context;
    ArrayList<SlotModel> model;

    public TestAdapter(Context context, ArrayList<SlotModel> model) {
        this.context = context;
        this.model = model;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.labtest_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        SlotModel list = model.get(position);
        holder.test.setText(list.getTime());

    }

    @Override
    public int getItemCount() {
        return model.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView test;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            test = itemView.findViewById(R.id.test);
        }
    }
}
