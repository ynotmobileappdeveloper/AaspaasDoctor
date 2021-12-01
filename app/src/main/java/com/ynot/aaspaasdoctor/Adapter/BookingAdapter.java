package com.ynot.aaspaasdoctor.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ynot.aaspaasdoctor.Model.BookingModel;
import com.ynot.aaspaasdoctor.R;

import java.util.ArrayList;

public class BookingAdapter extends RecyclerView.Adapter<BookingAdapter.ViewHolder> {
    Context context;
    ArrayList<BookingModel> model;
    ItemClick listener;
    String page = "";

    public BookingAdapter(Context context, ArrayList<BookingModel> model, ItemClick listener, String page) {
        this.context = context;
        this.model = model;
        this.listener = listener;
        this.page = page;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.book_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final BookingModel list = model.get(position);
        holder.sym.setVisibility(View.VISIBLE);
        holder.name.setText(list.getName());
        holder.gender.setText(list.getGender());
        holder.age.setText(list.getAge());
        holder.date.setText(list.getDate());
        holder.time.setText(list.getTime());
        holder.location.setText(list.getLocation());
        holder.symptom.setText(list.getSymptom());
        if (list.getSymptom().equals(null) || list.getSymptom().equals("")) {
            holder.sym.setVisibility(View.GONE);
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.Click(list);
            }
        });

        if (list.getStatus().equals("1")) {
            holder.viewall.setBackgroundResource(R.drawable.save_btn);
            holder.viewall.setText("Approved");
            holder.viewall.setTextColor(context.getResources().getColor(android.R.color.white));
        } else if (list.getStatus().equals("2")) {
            holder.viewall.setBackgroundResource(R.drawable.reject_btn);
            holder.viewall.setText("Rejected");
            holder.viewall.setTextColor(context.getResources().getColor(android.R.color.holo_red_dark));
        } else {
            holder.viewall.setBackgroundResource(R.drawable.view_btn);
            holder.viewall.setText("Pending");
        }

    }

    @Override
    public int getItemCount() {

        if (page.equals("home")) {
            if (model.size() > 4) {
                return 4;
            } else {
                return model.size();
            }

        } else {
            return model.size();
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView name, gender, age, date, time, location, symptom;
        Button viewall;

        LinearLayout sym;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            sym = itemView.findViewById(R.id.sym);
            symptom = itemView.findViewById(R.id.symptom);
            name = itemView.findViewById(R.id.test);
            gender = itemView.findViewById(R.id.gender);
            age = itemView.findViewById(R.id.age);
            date = itemView.findViewById(R.id.date);
            time = itemView.findViewById(R.id.time);
            location = itemView.findViewById(R.id.location);
            viewall = itemView.findViewById(R.id.viewall);
        }
    }

    public interface ItemClick {
        void Click(BookingModel model);
    }
}
