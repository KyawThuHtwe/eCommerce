package com.cu.ecommerce.Admin;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.cu.ecommerce.Model.Cart;
import com.cu.ecommerce.Prevalent.Prevalent;
import com.cu.ecommerce.R;
import com.cu.ecommerce.ViewHolder.CartViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class AdminUserProductsActivity extends AppCompatActivity {

    RecyclerView productList;
    DatabaseReference productRef;
    String oID="",agentID="",type="",cID="";
    ImageView back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_user_products);
        back=findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        agentID=getIntent().getStringExtra("agentID");
        cID=getIntent().getStringExtra("cid");
        oID=getIntent().getStringExtra("oid");
        type=getIntent().getStringExtra("type");

        productList=findViewById(R.id.recyclerView);
        productList.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        if(type.equals("new")){
            productRef= FirebaseDatabase.getInstance().getReference()
                    .child("Cart List")
                    .child("Admin View")
                    .child(agentID)
                    .child(oID).child("Products");
        }else if(type.equals("older")){
            productRef= FirebaseDatabase.getInstance().getReference()
                    .child("Older Order")
                    .child(agentID)
                    .child(cID)
                    .child(oID)
                    .child("Cart List");
        }

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
                    @SuppressLint("SetTextI18n")
                    @Override
                    protected void onBindViewHolder(@NonNull CartViewHolder holder, int i, @NonNull Cart cart) {
                        holder.name.setText("Name = "+cart.getPname());
                        holder.quantity.setText("Quantity = "+cart.getQuantity());
                        holder.price.setText("Price = "+cart.getPrice()+" Kyats");
                        Picasso.get().load(cart.getImage()).placeholder(R.drawable.ic_launcher_foreground).error(R.drawable.ic_launcher_background).into(holder.image);

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