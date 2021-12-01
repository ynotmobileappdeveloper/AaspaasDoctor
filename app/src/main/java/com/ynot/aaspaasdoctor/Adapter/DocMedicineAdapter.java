package com.ynot.aaspaasdoctor.Adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.material.card.MaterialCardView;
import com.ynot.aaspaasdoctor.Model.DocmedicineModel;
import com.ynot.aaspaasdoctor.R;

import java.util.ArrayList;

public class DocMedicineAdapter extends RecyclerView.Adapter<DocMedicineAdapter.ViewHolder> {

    Context context;
    ArrayList<DocmedicineModel> model;
    Click listener;

    public DocMedicineAdapter(Context context, ArrayList<DocmedicineModel> model, Click listener) {
        this.context = context;
        this.model = model;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.doc_medicine_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        final DocmedicineModel list = model.get(position);
        holder.date.setText(list.getDate());
        holder.name.setText(list.getName());
        holder.gender.setText(list.getGender());
        holder.age.setText(list.getAge());
        holder.see.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.ItemClick(list);
            }
        });

        if (!list.getStatus().isEmpty()) {
            holder.card.setVisibility(View.VISIBLE);
            Glide.with(context).load(list.getStatus()).addListener(new RequestListener<Drawable>() {
                @Override
                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                    holder.nodata.setVisibility(View.VISIBLE);
                    return false;
                }

                @Override
                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                    holder.nodata.setVisibility(View.GONE);
                    return false;
                }
            }).into(holder.image);
            holder.image.setVisibility(View.VISIBLE);
            holder.test_layout.setVisibility(View.GONE);
        } else {
            holder.card.setVisibility(View.GONE);
            holder.image.setVisibility(View.GONE);
            holder.test_layout.setVisibility(View.VISIBLE);
            holder.tests.setText(list.getMedicine());
        }


    }

    @Override
    public int getItemCount() {
        return model.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView date, name, gender, age, tests, see, title;
        ImageView image, nodata;
        LinearLayout test_layout;
        MaterialCardView card;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            date = itemView.findViewById(R.id.date);
            name = itemView.findViewById(R.id.name);
            gender = itemView.findViewById(R.id.gender);
            age = itemView.findViewById(R.id.age);
            tests = itemView.findViewById(R.id.tests);
            see = itemView.findViewById(R.id.see);
            title = itemView.findViewById(R.id.title);
            image = itemView.findViewById(R.id.image);
            test_layout = itemView.findViewById(R.id.test_layout);
            nodata = itemView.findViewById(R.id.nodata);
            card = itemView.findViewById(R.id.card);
        }
    }

    public interface Click {
        void ItemClick(DocmedicineModel list);
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }
}
