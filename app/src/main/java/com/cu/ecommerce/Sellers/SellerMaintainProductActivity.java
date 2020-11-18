package com.cu.ecommerce.Sellers;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cu.ecommerce.Model.Product;
import com.cu.ecommerce.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

public class SellerMaintainProductActivity extends AppCompatActivity {

    EditText name,description,price;
    Button change_apply;
    ImageView imageView,back;
    String productID="",agentID="";
    DatabaseReference productRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_maintain_product);

        back = findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        imageView = findViewById(R.id.imageM);
        name = findViewById(R.id.nameM);
        description = findViewById(R.id.descriptionM);
        price = findViewById(R.id.priceM);
        change_apply = findViewById(R.id.change_apply);

        productID = getIntent().getStringExtra("pid");
        agentID = getIntent().getStringExtra("sid");

        productRef =  FirebaseDatabase.getInstance().getReference().child("Products").child(agentID).child(productID);

        displayProductInfo(agentID,productID);
        Toast.makeText(getApplicationContext(),productID,Toast.LENGTH_SHORT).show();

        change_apply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateProduct();
            }
        });
    }
    private void updateProduct() {
        String pName=name.getText().toString();
        String pDescription=description.getText().toString();
        String pPrice=price.getText().toString();
        if(pName.equals("")){
            Toast.makeText(getApplicationContext(),"Write down Product Name",Toast.LENGTH_SHORT).show();
        }else  if(pDescription.equals("")){
            Toast.makeText(getApplicationContext(),"Write down Product Description",Toast.LENGTH_SHORT).show();
        }else  if(pPrice.equals("")){
            Toast.makeText(getApplicationContext(),"Write down Product Price",Toast.LENGTH_SHORT).show();
        }else {
            HashMap<String,Object> productMap=new HashMap<>();
            productMap.put("pid",productID);
            productMap.put("name",pName);
            productMap.put("description",pDescription);
            productMap.put("price",pPrice);
            productRef.updateChildren(productMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        Toast.makeText(getApplicationContext(),"Change apply successfully", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }
            });

        }

    }
    private void displayProductInfo(String agentID,String productID) {
        DatabaseReference productRef= FirebaseDatabase.getInstance().getReference().child("Products").child(agentID);
        productRef.child(productID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    Product product=snapshot.getValue(Product.class);
                    name.setText(product.getName());
                    description.setText(product.getDescription());
                    price.setText(product.getPrice());
                    Picasso.get().load(product.getImage()).into(imageView);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}