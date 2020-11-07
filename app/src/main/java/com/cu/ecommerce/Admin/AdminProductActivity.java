package com.cu.ecommerce.Admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.cu.ecommerce.Buyers.SearchProductActivity;
import com.cu.ecommerce.Model.Product;
import com.cu.ecommerce.R;
import com.cu.ecommerce.ViewHolder.ProductViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

public class AdminProductActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_product);
        databaseReference= FirebaseDatabase.getInstance().getReference().child("Products");
        recyclerView=findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        FirebaseRecyclerOptions<Product> options=new FirebaseRecyclerOptions
                .Builder<Product>()
                .setQuery(databaseReference,Product.class)
                .build();
        FirebaseRecyclerAdapter<Product, ProductViewHolder> adapter=
                new FirebaseRecyclerAdapter<Product, ProductViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull ProductViewHolder holder, int i, @NonNull Product product) {
                        holder.name.setText(product.getName());
                        holder.price.setText(product.getPrice());
                        Picasso.get().load(product.getImage()).placeholder(R.drawable.ic_launcher_foreground).error(R.drawable.ic_launcher_background).into(holder.image);
                        holder.description.setText(product.getDescription());

                        holder.layout.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Toast.makeText(getApplicationContext(),i+"",Toast.LENGTH_SHORT).show();

                                Intent intent=new Intent(AdminProductActivity.this, AdminMaintainProductActivity.class);
                                intent.putExtra("pid",product.getPid());
                                startActivity(intent);

                            }
                        });
                    }

                    @NonNull
                    @Override
                    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view1=LayoutInflater.from(parent.getContext()).inflate(R.layout.product_item_layout,parent,false);
                        return new ProductViewHolder(view1);
                    }
                };
        recyclerView.setAdapter(adapter);
        adapter.startListening();

        Button search=findViewById(R.id.search);
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), SearchProductActivity.class));
            }
        });
    }
}