package com.example.realtimebustrackingsystem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.realtimebustrackingsystem.Model.Data;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class BusInfo extends AppCompatActivity {
    private DatabaseReference busDatabase;

    //Recyclerview..
    private RecyclerView recyclerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bus_info);

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
        FirebaseRecyclerAdapter<Data, BusInfo.MyViewHolder> adapter= new FirebaseRecyclerAdapter<Data, BusInfo.MyViewHolder>(
                Data.class,
                R.layout.buses_layout,
                BusInfo.MyViewHolder.class,
                busDatabase
        ) {
            protected void populateViewHolder(BusInfo.MyViewHolder viewHolder, Data model, int position) {
                viewHolder.setName(model.getBname());
                viewHolder.setNumber(model.getBnumber());
                viewHolder.setDriver(model.getBdriver());
                viewHolder.setStops(model.getBstops());

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
    public void onBackPressed()
    {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left,
                R.anim.slide_out_right);
    }
}