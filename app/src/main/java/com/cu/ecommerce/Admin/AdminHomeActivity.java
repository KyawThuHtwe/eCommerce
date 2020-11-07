package com.cu.ecommerce.Admin;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.cu.ecommerce.Activities.MainActivity;
import com.cu.ecommerce.R;

public class AdminHomeActivity extends AppCompatActivity {

    Button maintain,check_order,logout,check_approve;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_home);
        maintain=findViewById(R.id.maintain);
        check_order=findViewById(R.id.check_order);
        check_approve=findViewById(R.id.check_approve);
        logout=findViewById(R.id.logout);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getApplicationContext(), MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        });
        check_order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getApplicationContext(), AdminNewOrdersActivity.class);
                startActivity(intent);
            }
        });

        maintain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getApplicationContext(), AdminProductActivity.class);
                startActivity(intent);
            }
        });
        check_approve.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getApplicationContext(), AdminCheckNewProductsActivity.class);
                startActivity(intent);
            }
        });
    }
}