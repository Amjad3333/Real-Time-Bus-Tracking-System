package com.example.realtimebustrackingsystem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.realtimebustrackingsystem.Model.BusData;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AddBuses extends AppCompatActivity {


    //bus registration
    private EditText busname;
    private EditText busnumber;
    private EditText busdriver;
    private EditText busstops;
    private Button add_btn;


    //firebase
    private FirebaseAuth mAuth;
    private DatabaseReference busDatabase;

    //Toolbar..
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_buses);

        toolbar=findViewById(R.id.toolbar_home);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Add Buses");

        mAuth= FirebaseAuth.getInstance();
        FirebaseUser muser=mAuth.getCurrentUser();
        String uid=muser.getUid();

        busDatabase= FirebaseDatabase.getInstance().getReference().child("BusData");
        busDatabase.keepSynced(true);

        add_btn=findViewById(R.id.btn_Addbus);
        add_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                busDataInsert();
            }
        });
    }
    private void busDataInsert() {
        busname=findViewById(R.id.bus_name);
        busnumber=findViewById(R.id.bus_number);
        busdriver=findViewById(R.id.bus_driver);
        busstops=findViewById(R.id.bus_stop);
        String bname = busname.getText().toString().trim();
        String bnumber = busnumber.getText().toString().trim();
        String bdriver = busdriver.getText().toString().trim();
        String bstop = busstops.getText().toString().trim();

        if (TextUtils.isEmpty(bname)) {
            busname.setError("Required Field..");
            return;
        }
        if (TextUtils.isEmpty(bnumber)) {
            busnumber.setError("Required Field..");
            return;
        }
        if (TextUtils.isEmpty(bdriver)) {
            busdriver.setError("Required Field..");
            return;
        }
        if (TextUtils.isEmpty(bstop)) {
            busstops.setError("Required Field..");
            return;
        }
        String id = busDatabase.push().getKey();
        BusData data = new BusData(bname,bnumber,bdriver,bstop);
        busDatabase.child(id).setValue(data);
        Toast.makeText(getApplicationContext(), "bus Registered", Toast.LENGTH_SHORT).show();
        busnumber.setText("");
        busname.setText("");
        busdriver.setText("");
        busstops.setText("");

    }
    public void onBackPressed()
    {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left,
                R.anim.slide_out_right);
    }
}