package com.example.realtimebustrackingsystem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.realtimebustrackingsystem.Model.Data;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AddDriver extends AppCompatActivity {

    //registration
    private EditText mEmail;
    private EditText mcms;
    private EditText mPass;
    private EditText Conf_mPass;
    private EditText name;
    private Button btnReg;

    //FireBase
    private FirebaseAuth mAuth;
    private DatabaseReference driverDatabase;

    //Toolbar..
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_driver);
        toolbar=findViewById(R.id.toolbar_home);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Add Drivers");
        //fire base
        mAuth= FirebaseAuth.getInstance();
        FirebaseUser muser = mAuth.getCurrentUser();
        String uid = muser.getUid();
        driverDatabase= FirebaseDatabase.getInstance().getReference().child("DriverData");
        driverDatabase.keepSynced(true);

        btnReg= findViewById(R.id.driver_btn_reg);
        btnReg.setOnClickListener(new View.OnClickListener()

        {
            @Override
            public void onClick (View v){
                driverDataInsert();

            }

        });

    }
    private void driverDataInsert() {
        name=findViewById(R.id.name_reg);
        mcms=findViewById(R.id.cmsid_reg);
        mEmail = findViewById(R.id.email_reg);
        mPass = findViewById(R.id.password_reg);
        Conf_mPass = findViewById(R.id.conf_password_reg);

        String fullname = name.getText().toString().trim();
        String cmsid = mcms.getText().toString().trim();
        String email = mEmail.getText().toString().trim();
        String pass = mPass.getText().toString().trim();
        String cpass = Conf_mPass.getText().toString().trim();
        if (TextUtils.isEmpty(fullname)) {
            name.setError("Required Field..");
            return;
        }
        if(TextUtils.isEmpty(cmsid))
        {
            mcms.setError("Required Field..");
            return;
        }
        if (TextUtils.isEmpty(email)) {
            mEmail.setError("Required Field..");
            return;
        }
        String regex = "^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+dri$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(email);
        if(!matcher.matches())
        {
            mEmail.setError("Invalid Email(name@gmail.com.dri)");
            return;
        }
        if (TextUtils.isEmpty(pass)) {
            mPass.setError("Required Field..");
            return;
        }

        if (TextUtils.isEmpty(cpass)) {
            Conf_mPass.setError("Required Field..");
            return;
        }
        if (!pass.equals(cpass)) {
            Conf_mPass.setError("please enter the correct password");
            return;
        }

                    String id = driverDatabase.push().getKey();
                    Data data = new Data(email,cmsid,fullname, pass);
                    driverDatabase.child(id).setValue(data);
                    mEmail.setText("");
                    name.setText("");
                    mcms.setText("");
                    mPass.setText("");
                    Conf_mPass.setText("");
                    name.requestFocus();
                    Toast.makeText(getApplicationContext(),"Driver Registration Complete",Toast.LENGTH_SHORT).show();


    }
    public void onBackPressed()
    {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left,
                R.anim.slide_out_right);
    }
}