package com.example.realtimebustrackingsystem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import android.widget.Toolbar;

import com.example.realtimebustrackingsystem.Model.Data;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.realtimebustrackingsystem.databinding.ActivityMapsTrackingBinding;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.maps.DirectionsApi;
import com.google.maps.DirectionsApiRequest;
import com.google.maps.GeoApiContext;
import com.google.maps.model.DirectionsLeg;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.DirectionsRoute;
import com.google.maps.model.DirectionsStep;
import com.google.maps.model.EncodedPolyline;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.Route;
import okhttp3.internal.connection.RouteException;

public class MapsActivity_Tracking extends FragmentActivity implements OnMapReadyCallback, LocationListener {

    private GoogleMap mMap;
    private ActivityMapsTrackingBinding binding;


    //stop tracking btn;
    private Button stop_tracking;

    private Polyline currentPolyline;
    //Toolbar..
    private Toolbar toolbar;

    DatabaseReference reference;
    DatabaseReference usr_location;

    private String str;
    private String usr_name;

    private LocationManager manager;

    private final int Min_Time = 1000; //1 sec
    private final int Min_Distance = 1;//1 meter

    private LatLng user;
    private LatLng driver;
    //polyline object

    Marker myMarker;
    Marker driverMarker;
    private ArrayList<LatLng> listpoints;

    private static final int Location_Request = 500;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMapsTrackingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Intent intent = getIntent();
        str = intent.getStringExtra("bus_number");
       // usr_name = intent.getStringExtra("usr_name");


        manager = (LocationManager) getSystemService(LOCATION_SERVICE);
        //usr_location = FirebaseDatabase.getInstance().getReference().child(usr_name);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        toolbar = findViewById(R.id.toolbar_map_tracking);
        setActionBar(toolbar);
        getActionBar().setTitle("Google Map");
        //getLocationUpdate();
        //readChanges();
        stop_tracking=findViewById(R.id.btn_stop_tracking);
        stop_tracking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), TrackingActivity.class));
                overridePendingTransition(R.anim.slide_in_left,
                        R.anim.slide_out_right);
            }
        });
        listpoints=new ArrayList<>();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Change the map type based on the user's selection.
        switch (item.getItemId()) {
            case R.id.normal_map:
                mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                return true;
            case R.id.hybrid_map:
                mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                return true;
            case R.id.satellite_map:
                mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                return true;
            case R.id.terrain_map:
                mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
  /*  private void readChanges() {
        usr_location.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.exists()) {
                    try {
                        MyLocation location = snapshot.getValue(MyLocation.class);
                        if (location != null) {

                            myMarker.setPosition(new LatLng(location.getLatitude(), location.getLongitude()));
                        }
                    } catch (Exception e) {
                        Toast.makeText(MapsActivity_Tracking.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }*/

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        reference = FirebaseDatabase.getInstance().getReference().child(str);
        ValueEventListener listener = reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()) {
                    Double latitude = snapshot.child("latitude").getValue(Double.class);
                    Double longitude = snapshot.child("longitude").getValue(Double.class);
                    driver = new LatLng(latitude, longitude);
                    if(listpoints.size()==1)
                    {
                        mMap.clear();
                        listpoints.clear();
                    }
                    addBusPoints();
                    listpoints.add(driver);
                    MarkerOptions markerOptions=new MarkerOptions();
                    markerOptions.position(driver).title(str)
                            .icon(BitmapFromVector(getApplicationContext(), R.drawable.bus_location));
                    mMap.addMarker(markerOptions);
                    drawCircle(new LatLng(latitude, longitude));


                }
                else{
                    Toast.makeText(getApplicationContext(), "Bus is Not Available", Toast.LENGTH_SHORT).show();
                }
                //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location,14f));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        if(manager!=null){
            if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED)
            {
                if (manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, Min_Time, Min_Distance, this);
                } else if (manager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                    manager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, Min_Time, Min_Distance, this);
                } else {
                    Toast.makeText(getApplicationContext(), "No Provider Enable", Toast.LENGTH_SHORT).show();
                }
            }else{
                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},101);
            }

        }
        mMap.setMyLocationEnabled(true);

        user = new LatLng(31.3713, 69.4138);

       // myMarker = mMap.addMarker(new MarkerOptions().position(user).title("user"));
        mMap.setMinZoomPreference(14);
        mMap.getUiSettings().isRotateGesturesEnabled();
        mMap.getUiSettings().setMapToolbarEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setAllGesturesEnabled(true);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(user));
     }
 /*
    private void getLocationUpdate() {
        if(manager!=null){
            if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED)
            {
                if (manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, Min_Time, Min_Distance, this);
                } else if (manager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                    manager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, Min_Time, Min_Distance, this);
                } else {
                    Toast.makeText(getApplicationContext(), "No Provider Enable", Toast.LENGTH_SHORT).show();
                }
            }else{

                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},101);
            }

        }
    }
*/
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==101){
            if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
               // getLocationUpdate();
            }else{
                Toast.makeText(getApplicationContext(), "Permission Required", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
    }
    private BitmapDescriptor BitmapFromVector(Context context, int vectorResId) {
        // below line is use to generate a drawable.
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);

        // below line is use to set bounds to our vector drawable.
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());

        // below line is use to create a bitmap for our
        // drawable which we have added.
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);

        // below line is use to add bitmap in our canvas.
        Canvas canvas = new Canvas(bitmap);

        // below line is use to draw our
        // vector drawable in canvas.
        vectorDrawable.draw(canvas);

        // after generating our bitmap we are returning our bitmap.
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }
    private void drawCircle(LatLng point){

        // Instantiating CircleOptions to draw a circle around the marker
        CircleOptions circleOptions = new CircleOptions();
        // Specifying the center of the circle
        circleOptions.center(point);
        // Radius of the circle
        circleOptions.radius(15);
        // Border color of the circle
        circleOptions.strokeColor(Color.BLACK);
        // Fill color of the circle
        circleOptions.fillColor(0x30ff0000);
        // Border width of the circle
        circleOptions.strokeWidth(2);
        // Adding the circle to the GoogleMap
        mMap.addCircle(circleOptions);

    }
    private void addBusPoints()
    {
        //buses stops mark
        if(str.equals("9006")){
            //stop1
            LatLng point1=new LatLng(31.340568,69.439431);
            mMap.addMarker(new MarkerOptions().position(point1).title("1.Tableghi Markaz")
                    .icon(BitmapFromVector(getApplicationContext(), R.drawable.bus_stops))).showInfoWindow();
            //stop2
            LatLng point2=new LatLng(31.332336,69.430857);
            mMap.addMarker(new MarkerOptions().position(point2).title("2.M Ali Pump")
                    .icon(BitmapFromVector(getApplicationContext(), R.drawable.bus_stops))).showInfoWindow();
            //stop3
            LatLng point3=new LatLng(31.329908,69.449266);
            mMap.addMarker(new MarkerOptions().position(point3).title("3.M Yar khan chowk")
                    .icon(BitmapFromVector(getApplicationContext(), R.drawable.bus_stops))).showInfoWindow();
            //stop4
            LatLng point4=new LatLng(31.324363,69.462256);
            mMap.addMarker(new MarkerOptions().position(point4).title("4.Shaikhan Road")
                    .icon(BitmapFromVector(getApplicationContext(), R.drawable.bus_stops))).showInfoWindow();
            //stop5
            LatLng point5=new LatLng(31.333938,69.449902);
            mMap.addMarker(new MarkerOptions().position(point5).title("5.60ft Road Center")
                    .icon(BitmapFromVector(getApplicationContext(), R.drawable.bus_stops))).showInfoWindow();
            //stop6
            LatLng point6=new LatLng(31.339387,69.447650);
            mMap.addMarker(new MarkerOptions().position(point6).title("6.Central jail")
                    .icon(BitmapFromVector(getApplicationContext(), R.drawable.bus_stops))).showInfoWindow();
            //stop7
            LatLng point7=new LatLng(31.345459,69.451637);
            mMap.addMarker(new MarkerOptions().position(point7).title("7.Kamal Khan Chowk")
                    .icon(BitmapFromVector(getApplicationContext(), R.drawable.bus_stops))).showInfoWindow();
            //stop8
            LatLng point8=new LatLng(31.345177,69.444936);
            mMap.addMarker(new MarkerOptions().position(point8).title("8.Shaheen Chowk")
                    .icon(BitmapFromVector(getApplicationContext(), R.drawable.bus_stops))).showInfoWindow();
            //stop9
            LatLng point9=new LatLng(31.355659,69.437393);
            mMap.addMarker(new MarkerOptions().position(point9).title("9.Grid Station")
                    .icon(BitmapFromVector(getApplicationContext(), R.drawable.bus_stops))).showInfoWindow();
        }
        if(str.equals("9007")){
            //stop1
            LatLng point1=new LatLng(31.340568,69.439431);
            mMap.addMarker(new MarkerOptions().position(point1).title("1.Tableghi Markaz")
                    .icon(BitmapFromVector(getApplicationContext(), R.drawable.bus_stops))).showInfoWindow();
            //stop2
            LatLng point2=new LatLng(31.338955,69.427536);
            mMap.addMarker(new MarkerOptions().position(point2).title("2.Appozai School")
                    .icon(BitmapFromVector(getApplicationContext(), R.drawable.bus_stops))).showInfoWindow();

            //stop3
            LatLng point3=new LatLng(31.332336,69.430857);
            mMap.addMarker(new MarkerOptions().position(point3).title("2.M Ali Pump")
                    .icon(BitmapFromVector(getApplicationContext(), R.drawable.bus_stops))).showInfoWindow();
            //stop4
            LatLng point4=new LatLng(31.329908,69.449266);
            mMap.addMarker(new MarkerOptions().position(point4).title("3.M Yar khan chowk")
                    .icon(BitmapFromVector(getApplicationContext(), R.drawable.bus_stops))).showInfoWindow();
            //stop5
            LatLng point5=new LatLng(31.333938,69.449902);
            mMap.addMarker(new MarkerOptions().position(point5).title("5.60ft Road Center")
                    .icon(BitmapFromVector(getApplicationContext(), R.drawable.bus_stops))).showInfoWindow();
            //stop6
            LatLng point6=new LatLng(31.339387,69.447650);
            mMap.addMarker(new MarkerOptions().position(point6).title("6.Central jail")
                    .icon(BitmapFromVector(getApplicationContext(), R.drawable.bus_stops))).showInfoWindow();
            //stop7
            LatLng point7=new LatLng(31.345459,69.451637);
            mMap.addMarker(new MarkerOptions().position(point7).title("7.Kamal Khan Chowk")
                    .icon(BitmapFromVector(getApplicationContext(), R.drawable.bus_stops))).showInfoWindow();
            //stop8
            LatLng point8=new LatLng(31.344332,69.454847);
            mMap.addMarker(new MarkerOptions().position(point8).title("8.Degree College")
                    .icon(BitmapFromVector(getApplicationContext(), R.drawable.bus_stops))).showInfoWindow();
            //stop9
            LatLng point9=new LatLng(31.341830,69.469510);
            mMap.addMarker(new MarkerOptions().position(point9).title("9.Al Mahmmod Hotel")
                    .icon(BitmapFromVector(getApplicationContext(), R.drawable.bus_stops))).showInfoWindow();
            //stop10
            LatLng point10=new LatLng(31.354069,69.462859);
            mMap.addMarker(new MarkerOptions().position(point10).title("10.Airport")
                    .icon(BitmapFromVector(getApplicationContext(), R.drawable.bus_stops))).showInfoWindow();
            //stop11
            LatLng point11=new LatLng(31.355659,69.437393);
            mMap.addMarker(new MarkerOptions().position(point11).title("11.Grid Station")
                    .icon(BitmapFromVector(getApplicationContext(), R.drawable.bus_stops))).showInfoWindow();
        }
    }
    public void onBackPressed()
    {

        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left,
                R.anim.slide_out_right);
    }
}

