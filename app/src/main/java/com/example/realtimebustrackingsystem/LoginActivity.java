package com.example.realtimebustrackingsystem;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.realtimebustrackingsystem.Model.Data;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LoginActivity extends AppCompatActivity {
    private EditText mEmail;
    private EditText mPass;
    private Button btnLogin;
    private TextView mForgetPassword;
    private ProgressDialog mDialog;

    //checked variables
    private String profile_email;
    private String email;
    private String pass;

    //Firebase...
    private FirebaseAuth mAuth;
    //private DatabaseReference studentDatabase;
    //private DatabaseReference driverDatabase;

    //SharedRef sharedRef=new SharedRef(getApplicationContext());
    private SharedPreferences ShredRef;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES); //For night mode theme
        setContentView(R.layout.activity_login);
        mAuth = FirebaseAuth.getInstance();
        mEmail = findViewById(R.id.email_login);
        mPass = findViewById(R.id.password_login);
        ShredRef=getApplicationContext().getSharedPreferences("myRef",Context.MODE_PRIVATE);
        String Email_chk = mEmail.getText().toString();
        // student regular expression
        String std_regex = "^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+usr$";
        Pattern spattern = Pattern.compile(std_regex);
        Matcher smatcher = spattern.matcher(Email_chk);
        // driver regular expression
        String dri_regex = "^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+dri$";
        Pattern dpattern = Pattern.compile(dri_regex);
        Matcher dmatcher = dpattern.matcher(Email_chk);

        //admin regular expression
        String adm_regex = "^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+adm$";
        Pattern apattern = Pattern.compile(adm_regex);
        Matcher amatcher = apattern.matcher(Email_chk);


        String FileContent=ShredRef.getString("UserEmail","No name");
        //Toast.makeText(getApplicationContext(), FileContent, Toast.LENGTH_SHORT).show();
        smatcher = spattern.matcher(FileContent);
        if(smatcher.matches()) {
            profile_email=FileContent;
            Intent intent=new Intent(getApplicationContext(), StudentActivity.class);
            intent.putExtra("profile_email", profile_email);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right,
                    R.anim.slide_out_left);
        }
        dmatcher = dpattern.matcher(FileContent);
        if(dmatcher.matches()) {
            profile_email=FileContent;
            Intent intent=new Intent(getApplicationContext(), DriverActivity.class);
            intent.putExtra("profile_email", profile_email);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right,
                    R.anim.slide_out_left);
        }

        if (mAuth.getCurrentUser() != null) {
             Email_chk=mAuth.getCurrentUser().getEmail().toString();
            amatcher = apattern.matcher(Email_chk);
            if(amatcher.matches()) {
                profile_email=mAuth.getCurrentUser().getEmail().toString();
                Intent intent=new Intent(getApplicationContext(), AdminActivity.class);
                intent.putExtra("profile_email", profile_email);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right,
                        R.anim.slide_out_left);
            }
    }
        mDialog=new ProgressDialog(this);
        btnLogin=findViewById(R.id.btn_login);
        mForgetPassword=findViewById(R.id.forget_password);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String Email_chk = mEmail.getText().toString();
                // student regular expression
                String std_regex = "^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+usr$";
                Pattern spattern = Pattern.compile(std_regex);
                Matcher smatcher = spattern.matcher(Email_chk);
                // driver regular expression
                String dri_regex = "^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+dri$";
                Pattern dpattern = Pattern.compile(dri_regex);
                Matcher dmatcher = dpattern.matcher(Email_chk);

                //admin regular expression
                String adm_regex = "^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+adm$";
                Pattern apattern = Pattern.compile(adm_regex);
                Matcher amatcher = apattern.matcher(Email_chk);

                    email = mEmail.getText().toString().trim();
                    pass = mPass.getText().toString().trim();

                    if (TextUtils.isEmpty(email)) {
                        mEmail.setError("Email Required..");
                        return;
                    }
                    if (TextUtils.isEmpty(pass)) {
                        mPass.setError("Password Required..");
                        return;
                    }
                  if (smatcher.matches()) {
                    mDialog.setMessage("Processing..");
                    mDialog.show();
                    student_login(email,pass);
                }
                  else if(dmatcher.matches())
                  {
                    mDialog.setMessage("Processing..");
                    mDialog.show();
                   driver_login(email,pass);
                }
                  else if(amatcher.matches())
                  {
                    mDialog.setMessage("Processing..");
                    mDialog.show();
                    mAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                mDialog.dismiss();
                                mEmail.setText("");
                                mPass.setText("");
                                mEmail.requestFocus();

                                Toast.makeText(getApplicationContext(), "Login Successful", Toast.LENGTH_SHORT).show();
                                profile_email=mAuth.getCurrentUser().getEmail();
                                Intent intent=new Intent(getApplicationContext(), AdminActivity.class);
                                intent.putExtra("profile_email", profile_email);
                                startActivity(intent);
                                overridePendingTransition(R.anim.slide_in_right,
                                        R.anim.slide_out_left);
                            } else {
                                mDialog.dismiss();
                                Toast.makeText(getApplicationContext(), "Login Failed Check Your Email or Password", Toast.LENGTH_SHORT).show();
                            }

                        }


                    });
                }
                  else{
                      mEmail.setError("Invalid Email");
                  }

            }
        });

        mForgetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), ResetActivity.class));
                overridePendingTransition(R.anim.slide_in_right,
                        R.anim.slide_out_left);
            }
        });

    }
     boolean login_failed;

    private void student_login(String email,String pass)
    {
        DatabaseReference studentDatabase;
        studentDatabase= FirebaseDatabase.getInstance().getReference().child("StudentData");
        studentDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(final DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot data1 : dataSnapshot.getChildren()) {
                        if (data1.getValue(Data.class).getEmail().equals(email)&&data1.getValue(Data.class).getPassword().equals(pass)) {
                            mDialog.dismiss();
                            mEmail.setText("");
                            mPass.setText("");
                            mEmail.requestFocus();
                            profile_email=email;
                            login_failed=true;
                            SharedPreferences.Editor editor=ShredRef.edit();
                            editor.putString("UserEmail",email);
                            editor.commit();
                            Intent intent=new Intent(getApplicationContext(), StudentActivity.class);
                            intent.putExtra("profile_email", profile_email);
                            startActivity(intent);
                            Toast.makeText(getApplicationContext(), "Login Successfully", Toast.LENGTH_SHORT).show();
                            overridePendingTransition(R.anim.slide_in_right,
                                    R.anim.slide_out_left);
                            break;
                            }


                        }
                    }
                if(login_failed!=true)
                {
                    mDialog.dismiss();
                    Toast.makeText(getApplicationContext(), "Login Failed", Toast.LENGTH_SHORT).show();
                }
                }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }

        });

    }
    private void driver_login(String email,String pass)
    {

        DatabaseReference driverDatabase;
        driverDatabase= FirebaseDatabase.getInstance().getReference().child("DriverData");
        driverDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(final DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot data1 : dataSnapshot.getChildren()) {
                        if (data1.getValue(Data.class).getEmail().equals(email)&&data1.getValue(Data.class).getPassword().equals(pass)) {
                            mDialog.dismiss();

                            mEmail.setText("");
                            mPass.setText("");
                            mEmail.requestFocus();
                            profile_email=email;
                            login_failed=true;
                            SharedPreferences.Editor editor=ShredRef.edit();
                            editor.putString("UserEmail",email);
                            editor.commit();
                            Intent intent=new Intent(getApplicationContext(), DriverActivity.class);
                            intent.putExtra("profile_email", profile_email);
                            startActivity(intent);
                            Toast.makeText(getApplicationContext(), "Login Successfully", Toast.LENGTH_SHORT).show();
                            overridePendingTransition(R.anim.slide_in_right,
                                    R.anim.slide_out_left);
                            break;
                        }
                    }
                }
                if(login_failed!=true)
                {
                    mDialog.dismiss();
                    Toast.makeText(getApplicationContext(), "Login Failed", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }

        });

    }
    public void onBackPressed()
    {
        super.onBackPressed();
        finishAffinity(); // Close all activites
    }

}



