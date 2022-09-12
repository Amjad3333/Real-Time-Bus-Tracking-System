package com.example.realtimebustrackingsystem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.realtimebustrackingsystem.Model.Data;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Driver_location_sharing extends AppCompatActivity {
    //Toolbar..
    private Toolbar toolbar;
    private Button share_location;
    private EditText bus_number;
    private boolean check_bnumber;
    private DatabaseReference busDatabase;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_location_sharing);

        toolbar=findViewById(R.id.toolbar_location_sharing);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Location Sharing Activity");

        Intent intent = getIntent();
        String drivername = intent.getStringExtra("driver_name");
        busDatabase= FirebaseDatabase.getInstance().getReference().child("BusData");

        bus_number=findViewById(R.id.sharing_number);
        share_location=findViewById(R.id.btn_Share_location);
        share_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String bnmbr=bus_number.getText().toString().trim();
                busDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(final DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            for (DataSnapshot data1 : dataSnapshot.getChildren()) {
                                if (data1.getValue(Data.class).getBnumber().equals(bnmbr)) {

                                    if(data1.getValue(Data.class).getBdriver().equals(drivername)){
                                        check_bnumber=true;
                                    }

                                }
                            }
                        }
                        if(check_bnumber==true)
                        {
                            check_bnumber=false;
                            String str = bus_number.getText().toString();
                            Intent intent = new Intent(getApplicationContext(), MapsActivity.class);
                            intent.putExtra("bus_number", str);
                            startActivity(intent);
                            overridePendingTransition(R.anim.slide_in_right,
                                    R.anim.slide_out_left);
                            bus_number.setText("");
                        }else{
                            Toast.makeText(getApplicationContext(), "InValid", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });
    }

    public void onBackPressed()
    {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left,
                R.anim.slide_out_right);
    }
}