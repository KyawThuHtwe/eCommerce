package com.cu.ecommerce.Buyers;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.cu.ecommerce.Activities.LoginActivity;
import com.cu.ecommerce.Activities.MainActivity;
import com.cu.ecommerce.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    EditText inputName,inputPhone,inputPassword;
    Button register;
    ProgressDialog loadingBar;
    String saveCurrentDate,saveCurrentTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        inputName=findViewById(R.id.name);
        inputPhone=findViewById(R.id.phone);
        inputPassword=findViewById(R.id.password);
        register=findViewById(R.id.register);

        loadingBar=new ProgressDialog(this);

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    createAccount();
                }catch (Exception e){
                    Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void createAccount() {
        String name=inputName.getText().toString();
        String phone=inputPhone.getText().toString();
        String password=inputPassword.getText().toString();
        if(TextUtils.isEmpty(name)){
            Toast.makeText(getApplicationContext(),"Please write your name",Toast.LENGTH_SHORT).show();
        }else if(TextUtils.isEmpty(phone)){
            Toast.makeText(getApplicationContext(),"Please write your phone number",Toast.LENGTH_SHORT).show();
        }else if(TextUtils.isEmpty(password)){
            Toast.makeText(getApplicationContext(),"Please write your password",Toast.LENGTH_SHORT).show();
        }else {
            loadingBar.setTitle("Create Account");
            loadingBar.setMessage("Please wait, while we are checking the credentials.");
            loadingBar.setCanceledOnTouchOutside(false);
            loadingBar.show();
            
            validatePhoneNumber(name,phone,password);
        }
    }

    private void validatePhoneNumber(String name, String phone, String password) {

        Calendar calendar=Calendar.getInstance();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat currentDate=new SimpleDateFormat("MMM dd, yyyy");
        saveCurrentDate=currentDate.format(calendar.getTime());

        @SuppressLint("SimpleDateFormat") SimpleDateFormat currentTime=new SimpleDateFormat("HH:mm:ss a");
        saveCurrentTime=currentTime.format(calendar.getTime());

        final DatabaseReference rootRef;
        rootRef= FirebaseDatabase.getInstance().getReference();
        rootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(!(snapshot.child("Users").child(phone).exists())){

                    HashMap<String,Object> userdataMap=new HashMap<>();
                    userdataMap.put("name",name);
                    userdataMap.put("phone",phone);
                    userdataMap.put("password",password);
                    userdataMap.put("date",saveCurrentDate);
                    userdataMap.put("time",saveCurrentTime);

                    rootRef.child("Users").child(phone).updateChildren(userdataMap)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        Toast.makeText(getApplicationContext(),"Congratulations, your account has been created.",Toast.LENGTH_SHORT).show();
                                        loadingBar.dismiss();
                                        startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                                        finish();
                                    }else {
                                        loadingBar.dismiss();
                                        Toast.makeText(getApplicationContext(),"Network Error: Please try again after some time...",Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });

                }else {
                    Toast.makeText(getApplicationContext(),"This "+phone+" already exist.",Toast.LENGTH_SHORT).show();
                    loadingBar.dismiss();
                    Toast.makeText(getApplicationContext(),"Please try again using another phone number",Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}