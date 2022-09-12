package com.example.realtimebustrackingsystem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.realtimebustrackingsystem.Model.BusData;
import com.example.realtimebustrackingsystem.Model.Data;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class BusListActivity extends AppCompatActivity {
    //Firebase Database
    private FirebaseAuth mAuth;
    private DatabaseReference busDatabase;

    //Recyclerview..
    private RecyclerView recyclerView;

    //Data item value..
    private String bname;
    private String bnumber;
    private String bdriver;
    private String bstops;
    private String post_key;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bus_list);
        mAuth=FirebaseAuth.getInstance();
        FirebaseUser mUser=mAuth.getCurrentUser();
        String uid=mUser.getUid();
        busDatabase= FirebaseDatabase.getInstance().getReference().child("BusData");
        recyclerView=findViewById(R.id.recycler_id_bus);
        LinearLayoutManager layoutManager=new LinearLayoutManager(getApplicationContext());
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);
    }
    public void onStart() {
        super.onStart();
        FirebaseRecyclerAdapter<Data, BusListActivity.MyViewHolder> adapter= new FirebaseRecyclerAdapter<Data, BusListActivity.MyViewHolder>(
                Data.class,
                R.layout.buses_layout,
                BusListActivity.MyViewHolder.class,
                busDatabase
        ) {
            protected void populateViewHolder(BusListActivity.MyViewHolder viewHolder, Data model, int position) {
                viewHolder.setName(model.getBname());
                viewHolder.setNumber(model.getBnumber());
                viewHolder.setDriver(model.getBdriver());
                viewHolder.setStops(model.getBstops());
                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        post_key=getRef(position).getKey();
                        bname=model.getBname();
                        bnumber=model.getBnumber();
                        bdriver=model.getBdriver();
                        bstops=model.getBstops();
                        bus_update_delete();
                    }
                });

            }
        };
        //Toast.makeText(getApplicationContext(), test, Toast.LENGTH_SHORT).show();
        recyclerView.setAdapter(adapter);
    }
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        View mView;
        public MyViewHolder(View itemView) {
            super(itemView);
            mView=itemView;
        }
        private void setName(String Name){
            TextView mName=mView.findViewById(R.id.name_txt_bus);
            mName.setText(Name);
        }
        private void setNumber(String Number){
            TextView mNumber=mView.findViewById(R.id.number_txt_bus);
            mNumber.setText(Number);
        }
        private void setDriver(String Driver){
            TextView mDriver=mView.findViewById(R.id.driver_txt_bus);
            mDriver.setText(Driver);
        }
        private void setStops(String Stops){
            TextView mStop=mView.findViewById(R.id.stop_txt_bus);
            mStop.setText(Stops);
        }
    }
    private void bus_update_delete() {
        final Dialog myDialog=new Dialog(BusListActivity.this);
        myDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        myDialog.setContentView(R.layout.update_bus);
        myDialog.setTitle("Update or Delete");
        EditText busname=(EditText)myDialog.findViewById(R.id.update_name_txt_bus);
        EditText busnumber=(EditText)myDialog.findViewById(R.id.update_number_txt_bus);
        EditText busdriver=(EditText)myDialog.findViewById(R.id.update_driver_txt_bus);
        EditText busstop=(EditText)myDialog.findViewById(R.id.update_stop_txt_bus);
        //set data to edit text
        busname.setText(bname);
        busname.setSelection(bname.length());

        busnumber.setText(bnumber);
        busnumber.setSelection(bnumber.length());

        busdriver.setText(bdriver);
        busdriver.setSelection(bdriver.length());

        busstop.setText(bstops);
        busstop.setSelection(bstops.length());

        Button delete=(Button)myDialog.findViewById(R.id.delete_usr_btn);
        Button update=(Button)myDialog.findViewById(R.id.update_usr_btn);

        delete.setEnabled(true);
        update.setEnabled(true);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                busDatabase.child(post_key).removeValue();
                myDialog.cancel();
            }
        });
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bname=busname.getText().toString();
                bnumber=busnumber.getText().toString();
                bdriver=busdriver.getText().toString();
                bstops=busstop.getText().toString();
                BusData data=new BusData(bname,bnumber,bdriver,bstops);
                busDatabase.child(post_key).setValue(data);
                myDialog.cancel();
            }
        });
        myDialog.show();
    }
    public void onBackPressed()
    {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left,
                R.anim.slide_out_right);
    }
}