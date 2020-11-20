package com.cu.ecommerce.Admin;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.cu.ecommerce.Adapter.ApproveProductAdapter;
import com.cu.ecommerce.Adapter.MaintainProductAdapter;
import com.cu.ecommerce.Model.Product;
import com.cu.ecommerce.R;
import com.cu.ecommerce.Sellers.SellerCategoryActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
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

    FloatingActionButton mAddProductFab, mAddCategoryFab,mAddFab;
    TextView addProductActionText, addCategoryActionText;
    Boolean isAllFabsVisible;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_check_new_products);

        unVerifyProductRef= FirebaseDatabase.getInstance().getReference().child("Products");

        recyclerView=findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        mAddFab = findViewById(R.id.add_fab);
        // FAB button
        mAddCategoryFab = findViewById(R.id.add_category_fab);
        mAddProductFab = findViewById(R.id.add_product_fab);

        addCategoryActionText = findViewById(R.id.add_category_action_text);
        addProductActionText = findViewById(R.id.add_product_action_text);

        mAddCategoryFab.setVisibility(View.GONE);
        mAddProductFab.setVisibility(View.GONE);
        addCategoryActionText.setVisibility(View.GONE);
        addProductActionText.setVisibility(View.GONE);
        isAllFabsVisible = false;
        //mAddFab.shrink();
        mAddFab.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (!isAllFabsVisible) {
                            mAddCategoryFab.show();
                            mAddProductFab.show();
                            addCategoryActionText.setVisibility(View.VISIBLE);
                            addProductActionText.setVisibility(View.VISIBLE);
                            //mAddFab.extend();
                            mAddFab.setImageResource(R.drawable.close);
                            isAllFabsVisible = true;
                        } else {
                            mAddCategoryFab.hide();
                            mAddProductFab.hide();
                            addCategoryActionText.setVisibility(View.GONE);
                            addProductActionText.setVisibility(View.GONE);

                            //mAddFab.shrink();
                            mAddFab.setImageResource(R.drawable.add);
                            isAllFabsVisible = false;
                        }
                    }
                });

        mAddProductFab.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(getApplicationContext(), SellerCategoryActivity.class);
                        intent.putExtra("type", "admin");
                        startActivity(intent);
                    }
                });

        mAddCategoryFab.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(getApplicationContext(), AdminCategoryActivity.class);
                        startActivity(intent);
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