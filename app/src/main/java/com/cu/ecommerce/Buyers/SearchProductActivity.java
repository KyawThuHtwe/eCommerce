package com.cu.ecommerce.Buyers;

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
import android.widget.EditText;
import android.widget.ImageView;

import com.cu.ecommerce.Model.Product;
import com.cu.ecommerce.R;
import com.cu.ecommerce.ViewHolder.ProductViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

public class SearchProductActivity extends AppCompatActivity {

    RecyclerView searchList;
    EditText search_value;
    Button search;
    String searchInput="";
    ImageView back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_product);
        search_value=findViewById(R.id.search_value);
        search=findViewById(R.id.search);
        searchList=findViewById(R.id.recyclerView);
        searchList.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchInput=search_value.getText().toString();
                onStart();
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

    @Override
    protected void onStart() {
        super.onStart();
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference().child("Products");
        FirebaseRecyclerOptions<Product> options=
                new FirebaseRecyclerOptions.Builder<Product>()
                .setQuery(reference.orderByChild("pname").startAt(searchInput.toLowerCase()),Product.class)
                .build();
        FirebaseRecyclerAdapter<Product, ProductViewHolder> adapter=
                new FirebaseRecyclerAdapter<Product, ProductViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull ProductViewHolder holder, int i, @NonNull Product product) {
                        holder.name.setText(product.getName());
                        holder.price.setText(product.getPrice());
                        Picasso.get().load(product.getImage()).placeholder(R.drawable.ic_launcher_foreground).error(R.drawable.ic_launcher_background).into(holder.image);
                        holder.description.setText(product.getDescription());
                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent=new Intent(getApplicationContext(), ProductDetailActivity.class);
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
        searchList.setAdapter(adapter);
        adapter.startListening();
    }
}