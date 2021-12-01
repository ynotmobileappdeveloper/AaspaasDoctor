package com.ynot.aaspaasdoctor.ui.home;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.JsonObject;
import com.ynot.aaspaasdoctor.Adapter.BookingAdapter;
import com.ynot.aaspaasdoctor.Booking;
import com.ynot.aaspaasdoctor.Booking2;
import com.ynot.aaspaasdoctor.BookingDetails;
import com.ynot.aaspaasdoctor.Model.BookingModel;
import com.ynot.aaspaasdoctor.PatientDetails;
import com.ynot.aaspaasdoctor.R;
import com.ynot.aaspaasdoctor.Webservice.SharedPrefManager;
import com.ynot.aaspaasdoctor.Webservice.URLs;
import com.ynot.aaspaasdoctor.Webservice.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import cc.cloudist.acplibrary.ACProgressConstant;
import cc.cloudist.acplibrary.ACProgressFlower;

public class HomeFragment extends Fragment {

    RecyclerView book_rec;
    ArrayList<BookingModel> model = new ArrayList<>();
    CardView today, all_booking, hospital, personal;
    TextView total_op, today_op, hospital_op, personal_op;
    ACProgressFlower progress;
    Button viewall;
    String from = "home";
    LinearLayout booking_layout;
    BookingAdapter bookingAdapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_home, container, false);
        progress = new ACProgressFlower.Builder(getContext())
                .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                .themeColor(Color.WHITE)
                .text("Please Wait")
                .textSize(24)
                .themeColor(getResources().getColor(R.color.colorPrimary))
                .petalThickness(7)
                .fadeColor(Color.DKGRAY).build();
        book_rec = root.findViewById(R.id.book_rec);
        today = root.findViewById(R.id.today);
        all_booking = root.findViewById(R.id.all_booking);
        hospital = root.findViewById(R.id.hospital);
        personal = root.findViewById(R.id.personal);
        total_op = root.findViewById(R.id.total);
        today_op = root.findViewById(R.id.today_op);
        hospital_op = root.findViewById(R.id.hospital_op);
        personal_op = root.findViewById(R.id.personal_op);
        viewall = root.findViewById(R.id.viewall);
        booking_layout = root.findViewById(R.id.booking_layout);
        book_rec.setLayoutManager(new LinearLayoutManager(getContext()));

        today.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getContext(), Booking2.class);
                i.putExtra("status", "today");
                startActivity(i);
            }
        });
        all_booking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getContext(), Booking2.class);
                i.putExtra("status", "total");
                startActivity(i);

            }
        });
        hospital.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getContext(), Booking2.class);
                i.putExtra("status", "hospital");
                startActivity(i);
            }
        });
        personal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getContext(), Booking2.class);
                i.putExtra("status", "personal");
                startActivity(i);
            }
        });
        viewall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getContext(), Booking2.class);
                i.putExtra("status", "recent");
                startActivity(i);
            }
        });

        return root;
    }

    private void get_booking() {
        progress.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLs.BOOKINGS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progress.dismiss();
                        Log.e("resp", response);
                        try {
                            JSONObject ob = new JSONObject(response);

                            if (ob.getBoolean("status")) {
                                booking_layout.setVisibility(View.VISIBLE);
                                total_op.setText(ob.getString("total_booking"));
                                today_op.setText(ob.getString("today_booking"));
                                hospital_op.setText(ob.getString("hospital_op"));
                                personal_op.setText(ob.getString("personal_op"));
                                JSONArray array = ob.getJSONArray("data");
                                model = new ArrayList<>();
                                for (int i = 0; i < array.length(); i++) {
                                    JSONObject obb = array.getJSONObject(i);
                                    model.add(new BookingModel(obb.getString("op_id"), obb.getString("name"), obb.getString("gender"), obb.getString("age"), obb.getString("date"), obb.getString("time"), obb.getString("op_type"), obb.getString("op_status"), obb.getString("symptom")));
                                }
                                bookingAdapter = new BookingAdapter(getContext(), model, new BookingAdapter.ItemClick() {
                                    @Override
                                    public void Click(BookingModel model) {

                                        if (model.getStatus().equals("0")) {
                                            Intent i = new Intent(getContext(), BookingDetails.class);
                                            i.putExtra("id", model.getId());
                                            startActivity(i);
                                        } else if (model.getStatus().equals("1")) {
                                            Intent i = new Intent(getContext(), PatientDetails.class);
                                            i.putExtra("id", model.getId());
                                            startActivity(i);
                                        }


                                    }
                                }, from);
                                book_rec.setAdapter(bookingAdapter);

                                if (array.length() > 4) {
                                    viewall.setVisibility(View.VISIBLE);
                                } else {
                                    viewall.setVisibility(View.GONE);
                                }


                            } else {
                                booking_layout.setVisibility(View.GONE);
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progress.dismiss();

            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("user_id", SharedPrefManager.getInstatnce(getContext()).getUser().getId());
                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(90 * 1000, 1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getmInstance(getContext()).addToRequestQueue(stringRequest);
    }

    @Override
    public void onResume() {
        super.onResume();
        get_booking();
    }
}