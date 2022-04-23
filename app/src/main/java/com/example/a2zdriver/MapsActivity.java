package com.example.a2zdriver;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.example.a2zdriver.databinding.ActivityMapsBinding;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CustomCap;
import com.google.android.gms.maps.model.Dash;
import com.google.android.gms.maps.model.Dot;
import com.google.android.gms.maps.model.Gap;
import com.google.android.gms.maps.model.JointType;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PatternItem;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.RoundCap;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.shashank.sony.fancytoastlib.FancyToast;

import java.security.Permission;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

public class MapsActivity extends FragmentActivity implements GoogleMap.OnPolylineClickListener,GoogleMap.OnPolygonClickListener {

    private GoogleMap mMap;
    private ActivityMapsBinding binding;
    FusedLocationProviderClient client;
    SupportMapFragment mapFragment;
    FirebaseDatabase database;
    LocationRequest locationRequest;
    public LatLng latLng;
    Marker marker = null;
    PolygonOptions polygonOptions;
    Polygon polygon;


    private static final int COLOR_BLACK_ARGB = 0xff000000;
    private static final int POLYLINE_STROKE_WIDTH_PX = 12;
    private static final int COLOR_WHITE_ARGB = 0xffffffff;
    private static final int COLOR_GREEN_ARGB = 0xff388E3C;
    private static final int COLOR_PURPLE_ARGB = 0xff81C784;
    private static final int COLOR_ORANGE_ARGB = 0xffF57F17;
    private static final int COLOR_BLUE_ARGB = 0xffF9A825;

    private static final int POLYGON_STROKE_WIDTH_PX = 8;
    private static final int PATTERN_DASH_LENGTH_PX = 20;
    private static final int PATTERN_GAP_LENGTH_PX = 20;
    private static final PatternItem DOT = new Dot();
    private static final PatternItem DASH = new Dash(PATTERN_DASH_LENGTH_PX);
    private static final PatternItem GAP = new Gap(PATTERN_GAP_LENGTH_PX);

    private static final List<PatternItem> PATTERN_POLYLINE_DOTTED =
            Arrays.asList(GAP, DOT);
    private static final List<PatternItem> PATTERN_POLYGON_ALPHA =
            Arrays.asList(GAP, DASH);
    private static final List<PatternItem> PATTERN_POLYGON_BETA =
            Arrays.asList(DOT, GAP, DASH, GAP);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        database = FirebaseDatabase.getInstance();
        client = LocationServices.getFusedLocationProviderClient(MapsActivity.this);


        if (ActivityCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            getCurrentLocation();
        }else {
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},44);
            ActivityCompat.requestPermissions(MapsActivity.this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 45);
        }

        Button logout = findViewById(R.id.logout);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT+1:00"));
                Date currentLocalTime = cal.getTime();
                DateFormat date1 = new SimpleDateFormat("HH:mm");
                date1.setTimeZone(TimeZone.getTimeZone("GMT+5:30"));
                String localTime = date1.format(currentLocalTime);
                DateFormat df = new SimpleDateFormat("dd/MM/yyyy | HH:mm");
                String crdate = df.format(Calendar.getInstance().getTime());
                NotificationModel notificationModel = new NotificationModel(crdate,"Your Waste Collection stopped at "+localTime);
                database.getReference()
                        .child("notifications")
                        .setValue(notificationModel);
                getEndlocation();
                FancyToast.makeText(MapsActivity.this,"You have stopped waste collection", FancyToast.LENGTH_SHORT,FancyToast.ERROR,false).show();
                Intent intent = new Intent(MapsActivity.this,StartCollection.class);
                startActivity(intent);
                finish();
            }
        });


    }

    private BitmapDescriptor BitmapFromVector(Context context, int vectorResId) {
        // below line is use to generate a drawable.
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    @SuppressLint("MissingPermission")
    void getCurrentLocation() {
        polygonOptions = new PolygonOptions();
        HashMap<String, Uri> images=new HashMap<String, Uri>();
        locationRequest = locationRequest.create();
        locationRequest.setInterval(100);
        locationRequest.setFastestInterval(50);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationCallback locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult != null) {
                    if (locationResult == null) {
                        return;
                    }
                    //Showing the latitude, longitude and accuracy on the home screen.
                    for (Location location : locationResult.getLocations()) {
                        latLng = new LatLng(location.getLatitude(), location.getLongitude());
                        LocationModel locationModel = new LocationModel(location.getLatitude(),location.getLongitude());
                        database.getReference()
                                .child("driverlocation")
                                .child("driver1")
                                .setValue(locationModel);

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                LocationModel locationModel = new LocationModel(location.getLatitude(),location.getLongitude());
                                Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT+1:00"));
                                Date currentLocalTime = cal.getTime();
                                DateFormat date1 = new SimpleDateFormat("HH:mm:ss");
                                date1.setTimeZone(TimeZone.getTimeZone("GMT+5:30"));
                                String localTime = date1.format(currentLocalTime);
                                database.getReference()
                                        .child("routepoints")
                                        .child(localTime)
                                        .setValue(locationModel);

                            }
                        },1000);


                        mapFragment.getMapAsync(new OnMapReadyCallback() {
                            @Override
                            public void onMapReady(@NonNull GoogleMap googleMap) {
                                mMap = googleMap;
                                if (marker == null) {
                                    MarkerOptions options = new MarkerOptions().position(latLng)
                                            .title("Vehicle Details")
                                            .icon(BitmapFromVector(MapsActivity.this,R.drawable.carveh));
                                    marker = mMap.addMarker(options);
                                }
                                else {
                                    marker.setPosition(latLng);
                                }
                                mMap.setInfoWindowAdapter(new PopupAdapter(MapsActivity.this,
                                        getLayoutInflater(),
                                        images));
                                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,18));

                                Polyline polyline2 = googleMap.addPolyline(new PolylineOptions()
                                        .clickable(true)
                                        .add(new LatLng(28.624869, 77.101426),
                                                new LatLng(28.622401, 77.102541),
                                                new LatLng(28.614770, 77.107344),
                                                new LatLng(28.612979, 77.104917),
                                                new LatLng(28.612432, 77.10454),
                                                new LatLng(28.608588, 77.102353),
                                                new LatLng(28.609324, 77.100528),
                                                new LatLng(28.609124, 77.098824),
                                                new LatLng(28.609123, 77.097049),
                                                new LatLng(28.617658, 77.077939),
                                                new LatLng(28.619350, 77.079089),
                                                new LatLng(28.619688, 77.079356),
                                                new LatLng(28.620568, 77.080531),
                                                new LatLng(28.621061, 77.081501),
                                                new LatLng(28.621590, 77.082988),
                                                new LatLng(28.621484, 77.084459),
                                                new LatLng(28.621932, 77.085984),
                                                new LatLng(28.621704, 77.088890),
                                                new LatLng(28.621596, 77.090647),
                                                new LatLng(28.621929, 77.092625),
                                                new LatLng(28.622356, 77.094155),
                                                new LatLng(28.624722, 77.100466),
                                                new LatLng(28.624863, 77.101423),
                                                new LatLng(28.622356, 77.102568),
                                                new LatLng(28.624869, 77.101426)

                                        ));
                                polyline2.setTag("A");

                                Polygon polygon1 = mMap.addPolygon(new PolygonOptions()
                                        .clickable(true)
                                        .add(new LatLng(28.624869, 77.101426),
                                                new LatLng(28.622401, 77.102541),
                                                new LatLng(28.614770, 77.107344),
                                                new LatLng(28.612979, 77.104917),
                                                new LatLng(28.612432, 77.10454),
                                                new LatLng(28.608588, 77.102353),
                                                new LatLng(28.609324, 77.100528),
                                                new LatLng(28.609124, 77.098824),
                                                new LatLng(28.609123, 77.097049),
                                                new LatLng(28.617658, 77.077939),
                                                new LatLng(28.619350, 77.079089),
                                                new LatLng(28.619688, 77.079356),
                                                new LatLng(28.620568, 77.080531),
                                                new LatLng(28.621061, 77.081501),
                                                new LatLng(28.621590, 77.082988),
                                                new LatLng(28.621484, 77.084459),
                                                new LatLng(28.621932, 77.085984),
                                                new LatLng(28.621704, 77.088890),
                                                new LatLng(28.621596, 77.090647),
                                                new LatLng(28.621929, 77.092625),
                                                new LatLng(28.622356, 77.094155),
                                                new LatLng(28.624722, 77.100466),
                                                new LatLng(28.624863, 77.101423),
                                                new LatLng(28.622356, 77.102568),
                                                new LatLng(28.624869, 77.101426)
                                        ));
                                polygon1.setTag("alpha");
                                stylePolygon(polygon1);
                                stylePolyline(polyline2);
                                mMap.setOnPolylineClickListener(MapsActivity.this);
                                mMap.setOnPolygonClickListener(MapsActivity.this);
                            }
                        });
                    }
                }
            }
        };

        client.requestLocationUpdates(locationRequest,locationCallback, Looper.getMainLooper());




      /*  task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                mapFragment.getMapAsync(new OnMapReadyCallback() {
                    @Override
                    public void onMapReady(@NonNull GoogleMap googleMap) {
                        mMap = googleMap;
                        LatLng sydney = new LatLng(location.getLatitude(), location.getLongitude());
                        mMap.addMarker(new MarkerOptions().position(sydney).title("Vehicle Details")
                                .icon(BitmapFromVector(MapsActivity.this, R.drawable.carveh)));
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(sydney,18));

                        mMap.setInfoWindowAdapter(new PopupAdapter(MapsActivity.this,
                                getLayoutInflater(),
                                images));


                    }
                });
            }
        }); */
    }

    @SuppressLint("MissingPermission")
    void getEndlocation(){
        client.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                LocationModel locationModel = new LocationModel(location.getLatitude(),location.getLongitude());
                database.getReference()
                        .child("endpoint")
                        .setValue(locationModel);
            }
        });
    }

    private void stylePolygon(Polygon polygon) {
        String type = "";
        // Get the data object stored with the polygon.
        if (polygon.getTag() != null) {
            type = polygon.getTag().toString();
        }

        List<PatternItem> pattern = null;
        int strokeColor = R.color.pColor;
        int fillColor = R.color.sColor;

        switch (type) {
            // If no type is given, allow the API to use the default.
            case "alpha":
                // Apply a stroke pattern to render a dashed line, and define
                pattern = PATTERN_POLYGON_ALPHA;
                strokeColor = R.color.pColor;
                fillColor = R.color.sColor;
                break;
            case "beta":
                pattern = PATTERN_POLYGON_BETA;
                strokeColor = R.color.pColor;
                fillColor = R.color.sColor;
                break;
        }

        polygon.setStrokePattern(pattern);
        polygon.setStrokeWidth(POLYGON_STROKE_WIDTH_PX);
        polygon.setStrokeColor(strokeColor);
        polygon.setFillColor(fillColor);
    }
    private void stylePolyline(Polyline polyline) {
        String type = "";
        // Get the data object stored with the polyline.
        if (polyline.getTag() != null) {
            type = polyline.getTag().toString();
        }

        switch (type) {
            // If no type is given, allow the API to use the default.
            case "A":
                // Use a custom bitmap as the cap at the start of the line.
                polyline.setStartCap(
                        new CustomCap(BitmapDescriptorFactory.fromResource(android.R.drawable.arrow_down_float),
                                10));
                break;
            case "B":
                // Use a round cap at the start of the line.
                polyline.setStartCap(new RoundCap());
                break;
        }

        polyline.setEndCap(new RoundCap());
        polyline.setWidth(POLYLINE_STROKE_WIDTH_PX);
        polyline.setColor(R.color.pColor);
        polyline.setJointType(JointType.ROUND);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 44 && requestCode == 45) {
            getCurrentLocation();
        } else {
            Toast.makeText(this, "Location Permission not given", Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    public void onPolygonClick(@NonNull Polygon polygon) {
        mMap.addMarker(new MarkerOptions().position(new LatLng(28.624869, 77.101426)).title("Ward No - 16 Janak Puri"));
    }
    @Override
    public void onPolylineClick(@NonNull Polyline polyline) {
        mMap.addMarker(new MarkerOptions().position(new LatLng(28.624869, 77.101426)).title("Ward No - 16 Janak Puri"));
    }
}