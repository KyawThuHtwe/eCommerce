package com.cu.ecommerce.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import io.paperdb.Paper;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.cu.ecommerce.Admin.AdminHomeActivity;
import com.cu.ecommerce.Admin.AdminMainActivity;
import com.cu.ecommerce.Buyers.HomeActivity;
import com.cu.ecommerce.Buyers.RegisterActivity;
import com.cu.ecommerce.Model.User;
import com.cu.ecommerce.Prevalent.Prevalent;
import com.cu.ecommerce.R;
import com.cu.ecommerce.Sellers.SellerHomeActivity;
import com.cu.ecommerce.Sellers.SellerMainActivity;
import com.cu.ecommerce.Sellers.SellerRegistrationActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    Button login,joinNow;
    ProgressDialog loadingBar;
    TextView seller_begin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        login=findViewById(R.id.login);
        joinNow=findViewById(R.id.joinNow);
        seller_begin=findViewById(R.id.seller_begin);
        seller_begin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), SellerRegistrationActivity.class));
            }
        });


        loadingBar=new ProgressDialog(this);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),LoginActivity.class));
            }
        });
        joinNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), RegisterActivity.class));
            }
        });
        try {
            Paper.init(this);
            String userPhoneKey = Paper.book().read(Prevalent.UserPhoneKey);
            String userPasswordKey = Paper.book().read(Prevalent.UserPasswordKey);
            String userTypeKey = Paper.book().read(Prevalent.UserTypeKey);
            if (!TextUtils.isEmpty(userPhoneKey) && !TextUtils.isEmpty(userPasswordKey) && !TextUtils.isEmpty(userTypeKey)) {
                allowAccess(userPhoneKey, userPasswordKey, userTypeKey);
                loadingBar.setTitle("Already Logged in");
                loadingBar.setMessage("Please wait...");
                loadingBar.setCanceledOnTouchOutside(false);
                loadingBar.show();
            }
        }catch (Exception e){
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void allowAccess(String phone, String password,String typeKey) {
        final DatabaseReference rootRef;
        rootRef= FirebaseDatabase.getInstance().getReference();
        rootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.child(typeKey).child(phone).exists()){

                    User userData=snapshot.child(typeKey).child(phone).getValue(User.class);
                    if(userData.getPhone().equals(phone)){
                        if(userData.getPassword().equals(password)){
                            Prevalent.currentOnlineUser=userData;
                            Toast.makeText(getApplicationContext(),"Please wait, your already logged in...", Toast.LENGTH_SHORT).show();
                            loadingBar.dismiss();
                            if(typeKey.equals("Admins")){
                                startActivity(new Intent(getApplicationContext(), AdminMainActivity.class));
                            }else if(typeKey.equals("Users")){
                                startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                            }
                            finish();

                        }else {
                            loadingBar.dismiss();
                            Toast.makeText(getApplicationContext(),"Password is incorrect", Toast.LENGTH_SHORT).show();
                        }
                    }

                }else {
                    Toast.makeText(getApplicationContext(),"Account with this, "+phone+" do not exist.",Toast.LENGTH_SHORT).show();
                    loadingBar.dismiss();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        if(firebaseUser!=null){
            startActivity(new Intent(getApplicationContext(), SellerMainActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK));
        }
    }
}