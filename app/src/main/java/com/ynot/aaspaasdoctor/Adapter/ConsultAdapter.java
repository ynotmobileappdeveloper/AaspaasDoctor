package com.ynot.aaspaasdoctor.Adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.ynot.aaspaasdoctor.EditConsultation;
import com.ynot.aaspaasdoctor.Model.ConsultationModel;
import com.ynot.aaspaasdoctor.R;
import com.ynot.aaspaasdoctor.Webservice.URLs;
import com.ynot.aaspaasdoctor.Webservice.VolleySingleton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConsultAdapter extends RecyclerView.Adapter<ConsultAdapter.MyViewHolder> {
    Context applicationContext;
    List<ConsultationModel> consultationModelList;
    LayoutInflater inflater;
    public ConsultAdapter(Context applicationContext, List<ConsultationModel> consultationModelList) {
        this.applicationContext=applicationContext;
        this.consultationModelList=consultationModelList;
        this.inflater=LayoutInflater.from(applicationContext);
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view=inflater.inflate(R.layout.consultrec,null);
        MyViewHolder myViewHolder=new MyViewHolder(view);
        return myViewHolder;
    }



    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {
holder.name.setText(consultationModelList.get(position).getName());
String type;
final int value;
if(consultationModelList.get(position).getType().equals("Personal")){
    type="Personal OP";
    value=1;
}
else if(consultationModelList.get(position).getType().equals("Hospital")){
    type="Hospital OP";
    value=2;
}
else {
    type="Clinical OP";
    value=3;
}
holder.type.setText(type);
holder.pincode.setText("Pin:"+consultationModelList.get(position).getPincode());
holder.dist.setText("District:"+consultationModelList.get(position).getDistrict());
holder.city.setText("City:"+consultationModelList.get(position).getCity());
holder.edit.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View view) {
        Intent intent=new Intent(applicationContext, EditConsultation.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("name",consultationModelList.get(position).getName());
        intent.putExtra("city",consultationModelList.get(position).getCity());

        intent.putExtra("id",consultationModelList.get(position).getConsult_id());
        intent.putExtra("type",String.valueOf(value));
        intent.putExtra("district",consultationModelList.get(position).getDistrict());
        intent.putExtra("pin",consultationModelList.get(position).getPincode());
        intent.putExtra("lat",consultationModelList.get(position).getLatitiude());
        intent.putExtra("longi",consultationModelList.get(position).getLongititude());
        applicationContext.startActivity(intent);



    }
});
holder.delete.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View view) {
        deleteConsultation(consultationModelList.get(position).getConsult_id());
        consultationModelList.remove(position);
        notifyDataSetChanged();


    }
});
    }

    private void deleteConsultation(final String consult_id) {
        final StringRequest stringRequest = new StringRequest(Request.Method.POST, URLs.DELETECONSULT,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // progress.dismiss();
                        Log.e("deleteconsult", response);

                        try {
                            JSONObject ob = new JSONObject(response);
                            if (ob.getBoolean("status")) {
                                Toast.makeText(applicationContext, ob.getString("message"), Toast.LENGTH_SHORT).show();
                                //  Toast.makeText(Consultation.this, "Saved Successfully !", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(applicationContext, ob.getString("message"), Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // progress.dismiss();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("id",consult_id);
                Log.e("delete(", String.valueOf(params));
                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(90 * 1000, 1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getmInstance(applicationContext).addToRequestQueue(stringRequest);

    }


    @Override
    public int getItemCount() {
        return consultationModelList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView name,type,dist,city,pincode;
        ImageView edit,delete;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            name=itemView.findViewById(R.id.name);
            type=itemView.findViewById(R.id.type);
            dist=itemView.findViewById(R.id.dist);
            city=itemView.findViewById(R.id.city);
            pincode=itemView.findViewById(R.id.pincode);
            edit=itemView.findViewById(R.id.edit);
            delete=itemView.findViewById(R.id.delete);
        }
    }
}
