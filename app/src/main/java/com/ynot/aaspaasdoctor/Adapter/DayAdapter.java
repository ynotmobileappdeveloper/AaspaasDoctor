package com.ynot.aaspaasdoctor.Adapter;

import android.app.TimePickerDialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ynot.aaspaasdoctor.Model.Day;
import com.ynot.aaspaasdoctor.R;

import java.util.ArrayList;
import java.util.Calendar;

public class DayAdapter extends RecyclerView.Adapter<DayAdapter.ViewHolder> {

    Context context;
    ArrayList<Day> model;
    TimePickerDialog timePickerDialog;
    Calendar calendar;
    int currentHour;
    int currentMinute;
    Select listener;
    int status = 0;

    public DayAdapter(Context context, ArrayList<Day> model, Select listener) {
        this.context = context;
        this.model = model;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.work_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        final Day list = model.get(position);

        holder.day.setText(list.getDay().substring(0, 3));
        holder.open.setText(list.getStartime());
        holder.close.setText(list.getEndtime());
        if (list.getStartime().equals("") && list.getEndtime().equals("")) {
            holder.delete.setImageResource(R.drawable.ic_baseline_check_box_24);
            status = 1;

        } else {
            holder.delete.setImageResource(R.drawable.ic_no_entry);

        }

        holder.open.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calendar = Calendar.getInstance();
                currentHour = calendar.get(Calendar.HOUR_OF_DAY);
                currentMinute = calendar.get(Calendar.MINUTE);

                timePickerDialog = new TimePickerDialog(context, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int hourOfDay, int minutes) {
                        calendar.set(Calendar.MINUTE, minutes);

                        calendar.set(Calendar.HOUR, hourOfDay);
                        String hour = String.valueOf(calendar.get(Calendar.HOUR));
                        String time;
                        String min = String.valueOf(calendar.get(Calendar.MINUTE));
                        time = hourOfDay + ":" + minutes;
                        Log.e("hour", String.valueOf(hourOfDay));
                        Log.e("minute", String.valueOf(minutes));


                        if (hour.length() == 2) {
                            time = calendar.get(Calendar.HOUR) + ":" + calendar.get(Calendar.MINUTE)
                                    + " " + ((calendar.get(Calendar.AM_PM) == Calendar.AM) ? "PM" : "AM");
                        } else {
                            time = "0" + calendar.get(Calendar.HOUR) + ":" + calendar.get(Calendar.MINUTE)
                                    + " " + ((calendar.get(Calendar.AM_PM) == Calendar.AM) ? "PM" : "AM");
                        }
                        if (calendar.get(Calendar.MINUTE) < 10) {
                            time = calendar.get(Calendar.HOUR) + ":" + "0" + calendar.get(Calendar.MINUTE)
                                    + " " + ((calendar.get(Calendar.AM_PM) == Calendar.AM) ? "PM" : "AM");
                        }


                        /*if (min.length() == 2) {
                            time = calendar.get(Calendar.HOUR) + ":" + calendar.get(Calendar.MINUTE)
                                    + " " + ((calendar.get(Calendar.AM_PM) == Calendar.AM) ? "AM" : "PM");
                        } else {
                            time = "0" + calendar.get(Calendar.HOUR) + ":" + "0"+calendar.get(Calendar.MINUTE)
                                    + " " + ((calendar.get(Calendar.AM_PM) == Calendar.AM) ? "AM" : "PM");
                        }*/


                        Log.e("time", time);

                        holder.open.setText(time);
                        list.setStartime(time);
//                        model.get(position).setStartime(time);
                        if (holder.open.getText().toString().length() > 2 && holder.close.getText().toString().length() > 2) {
                            model.get(position).setStartime(holder.open.getText().toString());
                            model.get(position).setEndtime(holder.close.getText().toString());
                            holder.delete.setImageResource(R.drawable.ic_no_entry);
                            notifyDataSetChanged();
                            listener.SelectedDays(model);
                            status = 0;
                        }

                    }
                }, currentHour, currentMinute, false);

                timePickerDialog.show();
                listener.SelectedDays(model);
            }
        });
        holder.close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calendar = Calendar.getInstance();
                currentHour = calendar.get(Calendar.HOUR_OF_DAY);
                currentMinute = calendar.get(Calendar.MINUTE);

                timePickerDialog = new TimePickerDialog(context, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int hourOfDay, int minutes) {
                        calendar.set(Calendar.MINUTE, minutes);
                        calendar.set(Calendar.HOUR, hourOfDay);
                        String hour = String.valueOf(calendar.get(Calendar.HOUR));
                        String time;
                        time = hourOfDay + ":" + minutes;
                        if (hour.length() == 2) {
                            time = calendar.get(Calendar.HOUR) + ":" + calendar.get(Calendar.MINUTE)
                                    + " " + ((calendar.get(Calendar.AM_PM) == Calendar.AM) ? "PM" : "AM");
                        } else {
                            time = "0" + calendar.get(Calendar.HOUR) + ":" + calendar.get(Calendar.MINUTE)
                                    + " " + ((calendar.get(Calendar.AM_PM) == Calendar.AM) ? "PM" : "AM");
                        }
                        if (calendar.get(Calendar.MINUTE) < 10) {
                            time = calendar.get(Calendar.HOUR) + ":" + "0" + calendar.get(Calendar.MINUTE)
                                    + " " + ((calendar.get(Calendar.AM_PM) == Calendar.AM) ? "PM" : "AM");
                        }

                        Log.e("time", time);

                        holder.close.setText(time);
                        list.setEndtime(time);
                        //  model.get(position).setEndtime(time);

                        if (holder.open.getText().toString().length() > 2 && holder.close.getText().toString().length() > 2) {
                            model.get(position).setStartime(holder.open.getText().toString());
                            model.get(position).setEndtime(holder.close.getText().toString());
                            holder.delete.setImageResource(R.drawable.ic_no_entry);
                            notifyDataSetChanged();
                            listener.SelectedDays(model);
                            status = 0;
                        }


                    }
                }, currentHour, currentMinute, false);

                timePickerDialog.show();
                listener.SelectedDays(model);
            }
        });
        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //   model.remove(position);
                holder.open.setText("");
                holder.close.setText("");
                model.get(position).setStartime(holder.open.getText().toString());
                model.get(position).setEndtime(holder.close.getText().toString());
                holder.delete.setImageResource(R.drawable.ic_baseline_add_circle_24);
                notifyDataSetChanged();
                listener.SelectedDays(model);

            }
        });
        listener.SelectedDays(model);

    }

    @Override
    public int getItemCount() {
        return model.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView day, open, close;
        ImageView delete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            day = itemView.findViewById(R.id.day);
            open = itemView.findViewById(R.id.open);
            close = itemView.findViewById(R.id.close);
            delete = itemView.findViewById(R.id.delete);
        }
    }

    public interface Select {
        void SelectedDays(ArrayList<Day> model);
    }
}
