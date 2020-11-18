package com.cu.ecommerce.Buyers;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.cu.ecommerce.Prevalent.Prevalent;
import com.cu.ecommerce.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class ConfirmFinalOrderActivity extends AppCompatActivity {

    EditText name,phone,address,city;
    Button confirm;
    String totalAmount="",agentID="";
    ImageView back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_final_order);

        agentID=getIntent().getStringExtra("agentID");
        totalAmount=getIntent().getStringExtra("Total Price");
        Toast.makeText(getApplicationContext(),"Total Price = "+totalAmount,Toast.LENGTH_SHORT).show();

        back=findViewById(R.id.back);
        name=findViewById(R.id.name);
        phone=findViewById(R.id.phone);
        address=findViewById(R.id.address);
        city=findViewById(R.id.city);
        confirm=findViewById(R.id.confirm);
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                check();
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void check() {
        if(TextUtils.isEmpty(name.getText().toString())){
            Toast.makeText(getApplicationContext(),"Please provide your full name",Toast.LENGTH_SHORT).show();
        }else  if(TextUtils.isEmpty(phone.getText().toString())){
            Toast.makeText(getApplicationContext(),"Please provide your phone number",Toast.LENGTH_SHORT).show();
        }else  if(TextUtils.isEmpty(address.getText().toString())){
            Toast.makeText(getApplicationContext(),"Please provide your address",Toast.LENGTH_SHORT).show();
        }else  if(TextUtils.isEmpty(city.getText().toString())){
            Toast.makeText(getApplicationContext(),"Please provide your city name",Toast.LENGTH_SHORT).show();
        }else{
            confirmOrder();
        }

    }

    private void confirmOrder() {

        String saveCurrentDate,saveCurrentTime;

        Calendar calendar=Calendar.getInstance();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat currentDate=new SimpleDateFormat("MMM dd, yyyy");
        saveCurrentDate=currentDate.format(calendar.getTime());

        @SuppressLint("SimpleDateFormat") SimpleDateFormat currentTime=new SimpleDateFormat("HH:mm:ss a");
        saveCurrentTime=currentTime.format(calendar.getTime());

        final DatabaseReference orderRef= FirebaseDatabase.getInstance().getReference()
                .child("Orders")
                .child(agentID)
                .child(Prevalent.currentOnlineUser.getPhone());
        HashMap<String,Object> orderMap=new HashMap<>();
        orderMap.put("oid",Prevalent.currentOnlineUser.getPhone());
        orderMap.put("totalAmount",totalAmount);
        orderMap.put("name",name.getText().toString());
        orderMap.put("phone",phone.getText().toString());
        orderMap.put("address",address.getText().toString());
        orderMap.put("city",city.getText().toString());
        orderMap.put("date",saveCurrentDate);
        orderMap.put("time",saveCurrentTime);
        orderMap.put("state","not shipped");
        orderMap.put("sid",agentID);

        orderRef.updateChildren(orderMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    FirebaseDatabase.getInstance().getReference().child("Cart List")
                            .child("User View")
                            .child(agentID)
                            .child(Prevalent.currentOnlineUser.getPhone())
                            .removeValue()
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        Toast.makeText(getApplicationContext(),"your final order has been placed successfully",Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(getApplicationContext(), HomeActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK));
                                    }
                                }
                            });
                }
            }
        });

    }
}