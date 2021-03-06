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
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.cu.ecommerce.Admin.AdminHomeActivity;
import com.cu.ecommerce.Admin.AdminMainActivity;
import com.cu.ecommerce.Buyers.HomeActivity;
import com.cu.ecommerce.Buyers.ResetPasswordActivity;
import com.cu.ecommerce.Model.User;
import com.cu.ecommerce.Prevalent.Prevalent;
import com.cu.ecommerce.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {

    EditText inputPhone,inputPassword;
    Button login;
    ProgressDialog loadingBar;
    CheckBox rememberMe;
    TextView adminLink,notAdminLink,forgetPassword;

    String parentDbName="Users";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        inputPhone=findViewById(R.id.phone);
        inputPassword=findViewById(R.id.password);
        login=findViewById(R.id.login);
        rememberMe=findViewById(R.id.rememberMe);
        adminLink=findViewById(R.id.adminLink);
        notAdminLink=findViewById(R.id.notAdminLink);
        forgetPassword=findViewById(R.id.forgetPassword);
        forgetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =new Intent(getApplicationContext(), ResetPasswordActivity.class);
                intent.putExtra("check","login");
                startActivity(intent);            }
        });

        loadingBar=new ProgressDialog(this);

        adminLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login.setText("Admin Login");
                parentDbName="Admins";
                adminLink.setVisibility(View.GONE);
                notAdminLink.setVisibility(View.VISIBLE);
            }
        });

        notAdminLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login.setText("Login");
                parentDbName="Users";
                adminLink.setVisibility(View.VISIBLE);
                notAdminLink.setVisibility(View.GONE);
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUser(parentDbName);
            }
        });
    }

    private void loginUser(String parentDbName) {
        String phone=inputPhone.getText().toString();
        String password=inputPassword.getText().toString();
        if(TextUtils.isEmpty(phone)){
            Toast.makeText(getApplicationContext(),"Please write your phone number",Toast.LENGTH_SHORT).show();
        }else if(TextUtils.isEmpty(password)){
            Toast.makeText(getApplicationContext(),"Please write your password",Toast.LENGTH_SHORT).show();
        }else {
            loadingBar.setTitle("Login Account");
            loadingBar.setMessage("Please wait, while we are checking the credentials.");
            loadingBar.setCanceledOnTouchOutside(false);
            loadingBar.show();

            allowAccessToAccount(phone,password,parentDbName);
        }
    }

    private void allowAccessToAccount(String phone, String password, String type) {

        if(rememberMe.isChecked()){
            Paper.book().write(Prevalent.UserPhoneKey,phone);
            Paper.book().write(Prevalent.UserPasswordKey,password);
            Paper.book().write(Prevalent.UserTypeKey,type);
        }

        final DatabaseReference rootRef;
        rootRef= FirebaseDatabase.getInstance().getReference();
        rootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.child(type).child(phone).exists()){

                    User userData=snapshot.child(type).child(phone).getValue(User.class);
                    String exist_phone=snapshot.child(type).child(phone).child("phone").getValue().toString();
                    String exist_image=snapshot.child(type).child(phone).child("image").getValue().toString();
                    if(userData.getPhone().equals(phone)){
                        if(type.equals("Admins")){
                            if(userData.getPassword().equals(password)) {
                                User user = new User();
                                user.setPhone(exist_phone);
                                user.setImage(exist_image);
                                Toast.makeText(getApplicationContext(), "Welcome Admin, your in Logged in Successfully", Toast.LENGTH_SHORT).show();
                                Prevalent.currentOnlineUser = user;
                                loadingBar.dismiss();
                                startActivity(new Intent(getApplicationContext(), AdminMainActivity.class));
                                finish();
                            }
                            else {
                                loadingBar.dismiss();
                                Toast.makeText(getApplicationContext(),"Password is incorrect", Toast.LENGTH_SHORT).show();
                            }
                        }else if(type.equals("Users")){
                            if(userData.getPassword().equals(password)) {
                                Toast.makeText(getApplicationContext(), "Logged in Successfully", Toast.LENGTH_SHORT).show();
                                Prevalent.currentOnlineUser = userData;
                                loadingBar.dismiss();
                                startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                                finish();
                            }
                            else {
                                loadingBar.dismiss();
                                Toast.makeText(getApplicationContext(),"Password is incorrect", Toast.LENGTH_SHORT).show();
                            }
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
}