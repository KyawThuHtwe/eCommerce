package com.cu.ecommerce.Sellers;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.cu.ecommerce.Adapter.ApproveProductAdapter;
import com.cu.ecommerce.Adapter.MaintainProductAdapter;
import com.cu.ecommerce.Model.Product;
import com.cu.ecommerce.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class SellerNewActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    DatabaseReference unVerifyProductRef;
    ArrayList<Product> products=new ArrayList<>();
    FloatingActionButton add;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_new);

        unVerifyProductRef= FirebaseDatabase.getInstance().getReference().child("Products").child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        recyclerView=findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        add = findViewById(R.id.add);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent intent = new Intent(getApplicationContext(), SellerCategoryActivity.class);
                    intent.putExtra("type", "seller");
                    startActivity(intent);
                    finish();
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
    @Override
    protected void onStart() {
        super.onStart();
        unVerifyProductRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    products.clear();
                    for(DataSnapshot value:snapshot.getChildren()){
                        Product product=value.getValue(Product.class);

                        if(product.getProductState().equals("Not Approved")){
                            products.add(product);
                        }

                    }
                    MaintainProductAdapter maintainProductAdapter=new MaintainProductAdapter(getApplicationContext(),products);
                    recyclerView.setAdapter(maintainProductAdapter);
                    maintainProductAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}