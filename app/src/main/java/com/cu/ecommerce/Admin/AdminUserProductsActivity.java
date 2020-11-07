package com.cu.ecommerce.Admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cu.ecommerce.Model.Cart;
import com.cu.ecommerce.Model.Product;
import com.cu.ecommerce.R;
import com.cu.ecommerce.ViewHolder.CartViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AdminUserProductsActivity extends AppCompatActivity {

    RecyclerView productList;
    DatabaseReference productRef;
    String userID="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_user_products);
        userID=getIntent().getStringExtra("uid");
        productList=findViewById(R.id.recyclerView);
        productList.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        productRef= FirebaseDatabase.getInstance().getReference()
                .child("Cart List")
                .child("Admin View")
                .child(userID).child("Products");

    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseRecyclerOptions<Cart> options=
                new FirebaseRecyclerOptions.Builder<Cart>()
                .setQuery(productRef, Cart.class)
                .build();
        FirebaseRecyclerAdapter<Cart, CartViewHolder> adapter=
                new FirebaseRecyclerAdapter<Cart, CartViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull CartViewHolder holder, int i, @NonNull Cart cart) {
                        holder.name.setText(cart.getPname());
                        holder.quantity.setText("Quantity = "+cart.getQuantity());
                        holder.price.setText("Price = "+cart.getPrice()+"$");

                    }

                    @NonNull
                    @Override
                    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.card_item_layout,parent,false);
                        return new CartViewHolder(view);
                    }
                };
        productList.setAdapter(adapter);
        adapter.startListening();
    }
}