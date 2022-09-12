package com.example.realtimebustrackingsystem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.viewpager.widget.ViewPager;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.realtimebustrackingsystem.Model.Data;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.tbuonomo.viewpagerdotsindicator.DotsIndicator;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

public class StudentActivity extends AppCompatActivity {
    //private FirebaseAuth mAuth;
    private DatabaseReference databaseReference;
    //for navigation menu
    public DrawerLayout drawerLayout;
    public ActionBarDrawerToggle actionBarDrawerToggle;
    private NavigationView nv;
    //Buttons
    private ImageButton buses_info;
    private ImageButton Track_bus;
    private String usr_name;


    //profile setting
    private String str_email;
    private String str_name;

    private SharedPreferences ShredRef;

    private boolean check_bnumber;
    private DatabaseReference user_reference;
    //Toolbar..
    private Toolbar toolbar;
    //setting profile
    private ImageView navUseravatar;
    private ImageView profile_avatar;
    private ImageView avatar1;
    private Uri imageUri;
    private String myUri="";
    private StorageTask uploadTask;
    private StorageReference storageProfilepicsref;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student);
        ShredRef=getApplicationContext().getSharedPreferences("myRef", Context.MODE_PRIVATE);
        Intent intent = getIntent();
        str_email=intent.getStringExtra("profile_email");
        toolbar=findViewById(R.id.toolbar_home);
        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle("               Student View");

        //mAuth= FirebaseAuth.getInstance();
        //usr_name=mAuth.getCurrentUser().getEmail().toString();
        user_reference= FirebaseDatabase.getInstance().getReference().child("StudentData");
        databaseReference=FirebaseDatabase.getInstance().getReference().child("User");
        storageProfilepicsref=FirebaseStorage.getInstance().getReference().child("Profile Pic");
        buses_info=findViewById(R.id.bus_info_btn);
        Track_bus=findViewById(R.id.track_bus_btn);
        buses_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), BusInfo.class));
                overridePendingTransition(R.anim.slide_in_right,
                        R.anim.slide_out_left);
            }
        });

        Track_bus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), TrackingActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right,
                        R.anim.slide_out_left);
            }
        });
        user_reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(final DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot data1 : dataSnapshot.getChildren()) {
                        if (data1.getValue(Data.class).getEmail().equals(str_email)) {
                            str_name=data1.getValue(Data.class).getFullname();
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        drawerLayout = findViewById(R.id.my_drawer_layout);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.nav_open, R.string.nav_close);

        // pass the Open and Close toggle for the drawer layout listener
        // to toggle the button
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        // to make the Navigation drawer icon always appear on the action bar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        nv = (NavigationView)findViewById(R.id.nv);
        View headerView = nv.getHeaderView(0);
        TextView navUseremail = (TextView) headerView.findViewById(R.id.profile_email);
        TextView navUsername = (TextView) headerView.findViewById(R.id.profile_name);
        navUseremail.setText(str_email);
        navUseravatar = (ImageView) headerView.findViewById(R.id.edit_profile);
        profile_avatar = (ImageView) headerView.findViewById(R.id.avatar);
        user_reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(final DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot data1 : dataSnapshot.getChildren()) {
                        if (data1.getValue(Data.class).getEmail().equals(str_email)) {
                            str_name=data1.getValue(Data.class).getFullname();
                            navUsername.setText(str_name);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        nv.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                switch (id) {
                    case R.id.nav_buses_info:
                        startActivity(new Intent(getApplicationContext(), BusInfo.class));
                        overridePendingTransition(R.anim.slide_in_right,
                                R.anim.slide_out_left);
                        break;
                    case R.id.nav_track_bus:
                        startActivity(new Intent(getApplicationContext(), TrackingActivity.class));
                        overridePendingTransition(R.anim.slide_in_right,
                                R.anim.slide_out_left);
                        break;
                    case R.id.visit:
                        startActivity(new Intent(getApplicationContext(), Webview.class));
                        overridePendingTransition(R.anim.slide_in_right,
                                R.anim.slide_out_left);
                        break;
                    case R.id.logout:
                        SharedPreferences.Editor editor=ShredRef.edit();
                        editor.remove("UserEmail");
                        editor.apply();
                        startActivity(new Intent(getApplicationContext(),LoginActivity.class));
                        overridePendingTransition(R.anim.slide_in_left,
                                R.anim.slide_out_right);
                        break;
                    default:
                        return true;
                }
                return true;
            }

        });
        navUseravatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Avatarlist();
            }
        });
        getUserProfile();
        CheckUserPermsions();
    }

    private void getUserProfile() {

        StorageReference storageReference= FirebaseStorage.getInstance().getReference().child("Profile Pic/"+str_email+".jpg");
        try{
            final File localFile=File.createTempFile(str_email,"jpg");
            storageReference.getFile(localFile)
                    .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                            //Toast.makeText(getApplicationContext(), "retrive", Toast.LENGTH_SHORT).show();
                            Bitmap bitmap= BitmapFactory.decodeFile(localFile.getAbsolutePath());
                            profile_avatar.setImageBitmap(bitmap);

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                }
            });

        } catch (IOException e) {
            e.printStackTrace();
        }

     /*  databaseReference.child(mAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists() && snapshot.getChildrenCount()>0)
                {
                   if(snapshot.hasChild("image"))
                    {
                        String image=snapshot.child("image").getValue().toString();
                        Toast.makeText(getApplicationContext(), image, Toast.LENGTH_SHORT).show();
                            Picasso.get().load(image).into(profile_avatar);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });*/
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode== CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode==RESULT_OK && data!=null)
        {
            CropImage.ActivityResult result=CropImage.getActivityResult(data);
            imageUri=result.getUri();
            avatar1.setImageURI(imageUri);
        }
        else
        {
            Toast.makeText(getApplicationContext(), "Error, Try again", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateprofile() {
        final ProgressDialog progressDialog=new ProgressDialog(this);
        progressDialog.setTitle("Set Your Profile");
        progressDialog.setMessage("Please Wait");
        progressDialog.show();
        if(imageUri!=null)
        {
            final StorageReference fileRef=storageProfilepicsref
                    .child(str_email+".jpg");
            uploadTask=fileRef.putFile(imageUri);
            uploadTask.continueWithTask(new Continuation() {
                @Override
                public Object then(@NonNull Task task) throws Exception {
                    if(!task.isSuccessful())
                    {
                        throw task.getException();
                    }
                    return fileRef.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if(task.isSuccessful())
                    {
                        Uri downloadUri=task.getResult();
                        myUri=downloadUri.toString();

                        HashMap<String,Object> userMap=new HashMap<>();
                        userMap.put("image",myUri);
                        //databaseReference.child(str_email).updateChildren(userMap);
                        profile_avatar.setImageURI(imageUri);
                        Toast.makeText(getApplicationContext(), "Profile updated", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();

                    }
                }
            });
        }
        else{
            progressDialog.dismiss();
            Toast.makeText(getApplicationContext(), "Image not selected", Toast.LENGTH_SHORT).show();
        }
    }

    private void Avatarlist() {
        final Dialog myDialog=new Dialog(StudentActivity.this);
        myDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        myDialog.setContentView(R.layout.avatar_list);
        myDialog.setTitle("Select Avatar");
        Button close=(Button)myDialog.findViewById(R.id.close_dialog);
        Button save=(Button)myDialog.findViewById(R.id.save_dialog);
        avatar1=(ImageView)myDialog.findViewById(R.id.profile);
        StorageReference storageReference= FirebaseStorage.getInstance().getReference().child("Profile Pic/"+str_email+".jpg");
        try{
            final File localFile=File.createTempFile(str_email,"jpg");
            storageReference.getFile(localFile)
                    .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                            //Toast.makeText(getApplicationContext(), "retrive", Toast.LENGTH_SHORT).show();
                            Bitmap bitmap= BitmapFactory.decodeFile(localFile.getAbsolutePath());
                            avatar1.setImageBitmap(bitmap);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                }
            });

        } catch (IOException e) {
            e.printStackTrace();
        }
        close.setEnabled(true);
        save.setEnabled(true);
        avatar1.setEnabled(true);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myDialog.cancel();
            }
        });
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateprofile();
                myDialog.cancel();
            }
        });
        avatar1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CropImage.activity().setAspectRatio(1,1).start(StudentActivity.this);
            }
        });
        myDialog.show();
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    void CheckUserPermsions(){
        if ( Build.VERSION.SDK_INT >= 23){
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) !=
                    PackageManager.PERMISSION_GRANTED  ){
                requestPermissions(new String[]{
                                android.Manifest.permission.ACCESS_FINE_LOCATION},
                        REQUEST_CODE_ASK_PERMISSIONS);
                return ;
            }
        }
        // init the contact list

    }
    //get acces to location permsion
    final private int REQUEST_CODE_ASK_PERMISSIONS = 123;



    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_PERMISSIONS:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // init the contact list
                } else {
                    // Permission Denied
                    Toast.makeText( this,"Permission Required" , Toast.LENGTH_SHORT)
                            .show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }


    public void onBackPressed()
    {
        super.onBackPressed();
        finishAffinity(); // Close all activites
    }
}