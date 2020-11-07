package com.cu.ecommerce.Admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cu.ecommerce.Model.Product;
import com.cu.ecommerce.R;
import com.cu.ecommerce.Sellers.SellerCategoryActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

public class AdminMaintainProductActivity extends AppCompatActivity {

    EditText name,description,price;
    TextView update,delete;
    ImageView imageView;
    String productID="";
    DatabaseReference productRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_maintain_product);

        try {
            imageView = findViewById(R.id.imageM);
            name = findViewById(R.id.nameM);
            description = findViewById(R.id.descriptionM);
            price = findViewById(R.id.priceM);
            update = findViewById(R.id.update_btn);
            delete = findViewById(R.id.delete_btn);

            productID = getIntent().getStringExtra("pid");

            productRef =  FirebaseDatabase.getInstance().getReference().child("Products").child(productID);

            displayProductInfo(productID);
            Toast.makeText(getApplicationContext(),productID,Toast.LENGTH_SHORT).show();

            update.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    updateProduct();
                }
            });

            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deleteThisProduct();
                }
            });
        }catch (Exception e){
            Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
        }
    }

    private void deleteThisProduct() {
        productRef.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(getApplicationContext(),"Delete this product successfully", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(getApplicationContext(), SellerCategoryActivity.class));
                    finish();
                }
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
                        startActivity(new Intent(getApplicationContext(), SellerCategoryActivity.class));
                        finish();
                    }
                }
            });

        }

    }
    private void displayProductInfo(String productID) {
        DatabaseReference productRef= FirebaseDatabase.getInstance().getReference().child("Products");
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