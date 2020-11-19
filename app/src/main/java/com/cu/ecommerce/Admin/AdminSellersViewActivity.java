package com.cu.ecommerce.Admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.cu.ecommerce.Model.Product;
import com.cu.ecommerce.Model.Seller;
import com.cu.ecommerce.Prevalent.Prevalent;
import com.cu.ecommerce.R;
import com.cu.ecommerce.Sellers.SellerMaintainProductActivity;
import com.cu.ecommerce.ViewHolder.AccountViewHolder;
import com.cu.ecommerce.ViewHolder.ProductViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

public class AdminSellersViewActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    DatabaseReference databaseReference;
    ImageView back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_sellers_view);

        databaseReference= FirebaseDatabase.getInstance().getReference().child("Sellers");

        recyclerView=findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

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
        FirebaseRecyclerOptions<Seller> options=new FirebaseRecyclerOptions
                .Builder<Seller>()
                .setQuery(databaseReference,Seller.class)
                .build();
        FirebaseRecyclerAdapter<Seller, AccountViewHolder> adapter=
                new FirebaseRecyclerAdapter<Seller, AccountViewHolder>(options) {
                    @SuppressLint("SetTextI18n")
                    @Override
                    protected void onBindViewHolder(@NonNull AccountViewHolder holder, int i, @NonNull Seller seller) {
                        holder.username.setText(seller.getName());
                        holder.date_time.setText("Created Date : "+seller.getDate()+", "+seller.getTime());
                        Picasso.get().load(seller.getImage()).placeholder(R.drawable.account_circle).error(R.drawable.account_circle).into(holder.user_profile);
                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent=new Intent(getApplicationContext(),DetailsActivity.class);
                                intent.putExtra("view","seller");
                                intent.putExtra("sid",seller.getSid());
                                startActivity(intent);
                            }
                        });
                    }

                    @NonNull
                    @Override
                    public AccountViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.account_view_holder,parent,false);
                        return new AccountViewHolder(view);
                    }
                };
        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }
}