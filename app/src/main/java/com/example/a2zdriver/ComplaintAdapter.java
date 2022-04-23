package com.example.a2zdriver;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ComplaintAdapter extends RecyclerView.Adapter<ComplaintAdapter.viewholder> {
    ArrayList<ComplaintModel> list = new ArrayList<>();
    Context context;

    public ComplaintAdapter(ArrayList<ComplaintModel> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.sample_complaints,parent,false);
        return new viewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull viewholder holder, int position) {
        ComplaintModel complaintModel = list.get(position);
        holder.complant_date.setText(complaintModel.getDate());
        holder.complaint_name.setText(complaintModel.getCompalaint());
        holder.complaint_status.setText(complaintModel.getStatus());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class viewholder extends RecyclerView.ViewHolder {
        TextView complant_date,complaint_name,complaint_status;
        public viewholder(@NonNull View itemView) {
            super(itemView);
            complant_date = itemView.findViewById(R.id.complaint_date);
            complaint_name = itemView.findViewById(R.id.complaint_name);
            complaint_status = itemView.findViewById(R.id.complaint_status);
        }
    }
}
