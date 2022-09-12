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

public class TrackingActivity extends AppCompatActivity {
    //Toolbar..
    private Toolbar toolbar;

    private EditText bus_number;
    private Button track_bus;

    private boolean check_bnumber;
    private DatabaseReference busDatabase;

    private String usr_name;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tracking);

        toolbar=findViewById(R.id.toolbar_tracking);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Tracking Activity");



        busDatabase= FirebaseDatabase.getInstance().getReference().child("BusData");

        bus_number=findViewById(R.id.tracking_number);
        track_bus=findViewById(R.id.btn_track_bus);
        track_bus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String bnmbr=bus_number.getText().toString().trim();
                busDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(final DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            for (DataSnapshot data1 : dataSnapshot.getChildren()) {
                                if (data1.getValue(Data.class).getBnumber().equals(bnmbr)) {
                                        check_bnumber=true;


                                }
                            }
                        }
                        if(check_bnumber==true)
                        {
                            check_bnumber=false;
                            String str = bus_number.getText().toString();
                            Intent intent = new Intent(getApplicationContext(), MapsActivity_Tracking.class);
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
