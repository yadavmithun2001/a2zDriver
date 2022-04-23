package com.example.a2zdriver;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.CancellationToken;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.OnTokenCanceledListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.shashank.sony.fancytoastlib.FancyToast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class StartCollection extends AppCompatActivity {

    Button startcollection,showallcomplaints,logout;
    FirebaseDatabase firebaseDatabase;
    FusedLocationProviderClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_collection);
        startcollection = findViewById(R.id.startcollection);
        logout = findViewById(R.id.logout);
        firebaseDatabase = FirebaseDatabase.getInstance();

        client = LocationServices.getFusedLocationProviderClient(StartCollection.this);



        startcollection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT+1:00"));
                Date currentLocalTime = cal.getTime();
                DateFormat date1 = new SimpleDateFormat("HH:mm");
                date1.setTimeZone(TimeZone.getTimeZone("GMT+5:30"));
                String localTime = date1.format(currentLocalTime);
                DateFormat df = new SimpleDateFormat("dd/MM/yyyy | HH:mm");
                String crdate = df.format(Calendar.getInstance().getTime());

                NotificationModel notificationModel = new NotificationModel(crdate,"Waste Collection started in your area at "+localTime);
                firebaseDatabase.getReference()
                        .child("notifications")
                        .setValue(notificationModel);
                FancyToast.makeText(StartCollection.this,"You have successfully started collection", FancyToast.LENGTH_SHORT,FancyToast.SUCCESS,false).show();
                Intent intent = new Intent(StartCollection.this,MapsActivity.class);
                startActivity(intent);
                setStartLocation();

            }
        });

        showallcomplaints = findViewById(R.id.showcomplaints);
        showallcomplaints.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(StartCollection.this, Showallcomplaints.class);
                startActivity(intent);
            }
        });



        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                FancyToast.makeText(StartCollection.this,"Logged Out Successfully", FancyToast.LENGTH_SHORT,FancyToast.SUCCESS,false).show();
                Intent intent = new Intent(StartCollection.this,SplashScreen.class);
                startActivity(intent);
                finish();
            }
        });


    }
    @SuppressLint("MissingPermission")
    void setStartLocation(){
        client.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                LocationModel locationModel = new LocationModel(location.getLatitude(),location.getLongitude());
                firebaseDatabase.getReference()
                        .child("startingpoint")
                        .setValue(locationModel);
            }
        });
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 44 && requestCode == 45) {
            setStartLocation();
        } else {
            Toast.makeText(this, "Location Permission not given", Toast.LENGTH_SHORT).show();
        }
    }
}