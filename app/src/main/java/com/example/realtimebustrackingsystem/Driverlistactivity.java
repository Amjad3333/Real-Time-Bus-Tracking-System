package com.example.realtimebustrackingsystem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.realtimebustrackingsystem.Model.Data;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Driverlistactivity extends AppCompatActivity {
    //Firebase Database
    private FirebaseAuth mAuth;
    private DatabaseReference driverDatabase;

    //Recyclerview..
    private RecyclerView recyclerView;

    //Data item value..
    private String name;
    private String email;
    private String cmsid;
    private String pass;
    private String post_key;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driverlistactivity);
        mAuth=FirebaseAuth.getInstance();
        FirebaseUser mUser=mAuth.getCurrentUser();
        String uid=mUser.getUid();
        driverDatabase= FirebaseDatabase.getInstance().getReference().child("DriverData");
        recyclerView=findViewById(R.id.recycler_id_driver);
        LinearLayoutManager layoutManager=new LinearLayoutManager(getApplicationContext());
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);
    }
    public void onStart() {
        super.onStart();
        FirebaseRecyclerAdapter<Data, Driverlistactivity.MyViewHolder> adapter= new FirebaseRecyclerAdapter<Data, Driverlistactivity.MyViewHolder>(
                Data.class,
                R.layout.driver_list,
                Driverlistactivity.MyViewHolder.class,
                driverDatabase
        ) {
            protected void populateViewHolder(Driverlistactivity.MyViewHolder viewHolder, Data model, int position) {
                viewHolder.setName(model.getFullname());
                viewHolder.setCmsid(model.getCmsid());
                viewHolder.setEmail(model.getEmail());
                viewHolder.setPass(model.getPassword());
                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        post_key=getRef(position).getKey();
                        name=model.getFullname();
                        email=model.getEmail();
                        cmsid=model.getCmsid();
                        pass=model.getPassword();
                        user_update_delete();
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
            TextView mName=mView.findViewById(R.id.name_txt_student);
            mName.setText(Name);
        }
        private void setCmsid(String Cmsid){
            TextView mCmsid=mView.findViewById(R.id.cmsid_txt_student);
            mCmsid.setText(Cmsid);
        }
        private void setEmail(String Email){
            TextView mEmail=mView.findViewById(R.id.emial_txt_student);
            mEmail.setText(Email);
        }
        private void setPass(String Pass){
            TextView mPass=mView.findViewById(R.id.pass_txt_student);
            mPass.setText(Pass);
        }
    }

    private void user_update_delete() {
        final Dialog myDialog=new Dialog(Driverlistactivity.this);
        myDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        myDialog.setContentView(R.layout.update_driver);
        myDialog.setTitle("Update or Delete");
        EditText uname=(EditText)myDialog.findViewById(R.id.update_name_txt_student);
        EditText ucmsid=(EditText)myDialog.findViewById(R.id.update_cmsid_txt_student);
        EditText uemail=(EditText)myDialog.findViewById(R.id.update_emial_txt_student);

        //set data to edit text
        uname.setText(name);
        uname.setSelection(name.length());

        ucmsid.setText(cmsid);
        ucmsid.setSelection(cmsid.length());

        uemail.setText(email);
        uemail.setSelection(email.length());

        Button delete=(Button)myDialog.findViewById(R.id.delete_usr_btn);
        Button update=(Button)myDialog.findViewById(R.id.update_usr_btn);

        delete.setEnabled(true);
        update.setEnabled(true);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                driverDatabase.child(post_key).removeValue();
                myDialog.cancel();
            }
        });
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                name=uname.getText().toString();
                cmsid=ucmsid.getText().toString();
                email=uemail.getText().toString();
                Data data=new Data(email,cmsid,name,pass);
                driverDatabase.child(post_key).setValue(data);
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