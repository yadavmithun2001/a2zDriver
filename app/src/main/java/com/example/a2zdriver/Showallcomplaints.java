package com.example.a2zdriver;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class Showallcomplaints extends AppCompatActivity {
    FirebaseAuth auth;
    FirebaseDatabase database;
    ImageView back;
    RecyclerView recyclerView;
    ComplaintAdapter complaintAdapter;
    ArrayList<ComplaintModel> list;
    ComplaintModel complaintModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_showallcomplaints);

        recyclerView = findViewById(R.id.complaints);
        list = new ArrayList<>();
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        back = findViewById(R.id.imageback);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        database.getReference()
                .child("complaints")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for(DataSnapshot snapshot1 : snapshot.getChildren()){
                            complaintModel = snapshot1.getValue(ComplaintModel.class);
                            list.add(complaintModel);
                        }
                        complaintAdapter = new ComplaintAdapter(list,Showallcomplaints.this);
                        complaintAdapter.notifyDataSetChanged();
                        recyclerView.setAdapter(complaintAdapter);
                        recyclerView.setLayoutManager(new LinearLayoutManager(Showallcomplaints.this));
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

    }
}