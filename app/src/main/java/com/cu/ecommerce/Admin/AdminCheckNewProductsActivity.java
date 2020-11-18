package com.cu.ecommerce.Admin;

import android.os.Bundle;

import com.cu.ecommerce.Adapter.ApproveProductAdapter;
import com.cu.ecommerce.Adapter.MaintainProductAdapter;
import com.cu.ecommerce.Model.Product;
import com.cu.ecommerce.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class AdminCheckNewProductsActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    DatabaseReference unVerifyProductRef;
    ArrayList<Product> products=new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_check_new_products);

        unVerifyProductRef= FirebaseDatabase.getInstance().getReference().child("Products");

        recyclerView=findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

    }

    @Override
    protected void onStart() {
        super.onStart();
        unVerifyProductRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    products.clear();
                    for(DataSnapshot snap:snapshot.getChildren()){
                        for(DataSnapshot value:snap.getChildren()){
                            Product product=value.getValue(Product.class);

                            if(product.getProductState().equals("Not Approved")){
                                products.add(product);
                            }

                        }
                        ApproveProductAdapter approveProductAdapter=new ApproveProductAdapter(getApplicationContext(),products);
                        recyclerView.setAdapter(approveProductAdapter);
                        approveProductAdapter.notifyDataSetChanged();

                    }
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}