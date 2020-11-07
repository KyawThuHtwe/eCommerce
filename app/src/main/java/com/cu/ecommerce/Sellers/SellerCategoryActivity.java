package com.cu.ecommerce.Sellers;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.cu.ecommerce.Activities.MainActivity;
import com.cu.ecommerce.Admin.AdminNewOrdersActivity;
import com.cu.ecommerce.Admin.AdminProductActivity;
import com.cu.ecommerce.R;

public class SellerCategoryActivity extends AppCompatActivity {

    ImageView watch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_category);
        watch=findViewById(R.id.watch);
        watch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getApplicationContext(), SellerAdminAddNewProductActivity.class);
                intent.putExtra("category","watch");
                startActivity(intent);
            }
        });
    }
}