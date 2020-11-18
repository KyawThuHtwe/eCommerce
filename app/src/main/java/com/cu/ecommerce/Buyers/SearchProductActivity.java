package com.cu.ecommerce.Buyers;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cu.ecommerce.Adapter.ProductAdapter;
import com.cu.ecommerce.Model.Product;
import com.cu.ecommerce.Model.Seller;
import com.cu.ecommerce.R;
import com.cu.ecommerce.ViewHolder.ProductViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class SearchProductActivity extends AppCompatActivity {

    RecyclerView searchList;
    EditText search_value;
    TextView search;
    String searchInput="",agentID="";
    ImageView back;
    ArrayList<Product> products;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_product);

        agentID=getIntent().getStringExtra("sid");

        search_value=findViewById(R.id.search_value);
        search=findViewById(R.id.search);

        searchList=findViewById(R.id.recyclerView);
        searchList.setLayoutManager(new GridLayoutManager(getApplicationContext(),2));
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!TextUtils.isEmpty(search_value.getText().toString())){
                    searchInput=search_value.getText().toString();
                    search(agentID,searchInput);
                }

            }
        });
        search_value.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                searchList.setAdapter(null);

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        back=findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }
    public void search(String choose_agent, String searchInput){
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference();
        reference.child("Products").child(agentID)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        products=new ArrayList<>();
                        products.clear();
                        if(snapshot.exists()){
                            for(DataSnapshot snap:snapshot.getChildren()){
                                Product product=snap.getValue(Product.class);
                                if(product.getSid().equals(choose_agent) && product.getName().toLowerCase().contains(searchInput.toLowerCase())){
                                    products.add(product);
                                    ProductAdapter productAdapter=new ProductAdapter(getApplicationContext(),products);
                                    searchList.setAdapter(productAdapter);
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

    }
}