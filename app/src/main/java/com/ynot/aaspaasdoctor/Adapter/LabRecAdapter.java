package com.ynot.aaspaasdoctor.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.ynot.aaspaasdoctor.Model.LabRecModel;
import com.ynot.aaspaasdoctor.PdfActivity;
import com.ynot.aaspaasdoctor.R;

import java.util.List;

public class LabRecAdapter extends RecyclerView.Adapter<LabRecAdapter.MyViewHolder> {
    Context context;
    List<LabRecModel> labRecModelList;
    Click listener;

    public LabRecAdapter(Context context, List<LabRecModel> labRecModelList, Click listener) {
        this.context = context;
        this.labRecModelList = labRecModelList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.labrecitem, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {
        final LabRecModel list = labRecModelList.get(position);


        holder.date.setText(list.getDate());
        holder.name.setText(list.getName());
        holder.gender.setText(list.getGender());
        holder.tests.setText(list.getTest());
        holder.age.setText(list.getAge());

        holder.see.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.Itemclick(list);
            }
        });


    }

    @Override
    public int getItemCount() {
        return labRecModelList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView date, name, gender, age, tests, see, title;
        ImageView image, nodata;
        MaterialCardView card;
        LinearLayout test_layout;
        Button lab_status;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            date = itemView.findViewById(R.id.date);
            name = itemView.findViewById(R.id.name);
            gender = itemView.findViewById(R.id.gender);
            age = itemView.findViewById(R.id.age);
            tests = itemView.findViewById(R.id.tests);
            see = itemView.findViewById(R.id.see);
            title = itemView.findViewById(R.id.title);
            image = itemView.findViewById(R.id.image);
            card = itemView.findViewById(R.id.card);
            nodata = itemView.findViewById(R.id.nodata);
            test_layout = itemView.findViewById(R.id.test_layout);
            lab_status = itemView.findViewById(R.id.lab_status);
        }
    }

    public interface Click {
        void Itemclick(LabRecModel list);
    }
}
