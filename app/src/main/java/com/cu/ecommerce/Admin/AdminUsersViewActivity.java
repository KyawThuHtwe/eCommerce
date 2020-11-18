package com.cu.ecommerce.Admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.cu.ecommerce.Model.Seller;
import com.cu.ecommerce.Model.User;
import com.cu.ecommerce.R;
import com.cu.ecommerce.ViewHolder.AccountViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.net.UnknownServiceException;

public class AdminUsersViewActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_users_view);

        databaseReference= FirebaseDatabase.getInstance().getReference().child("Users");

        recyclerView=findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseRecyclerOptions<User> options=new FirebaseRecyclerOptions
                .Builder<User>()
                .setQuery(databaseReference,User.class)
                .build();
        FirebaseRecyclerAdapter<User, AccountViewHolder> adapter=
                new FirebaseRecyclerAdapter<User, AccountViewHolder>(options) {
                    @SuppressLint("SetTextI18n")
                    @Override
                    protected void onBindViewHolder(@NonNull AccountViewHolder holder, int i, @NonNull User user) {
                        holder.username.setText(user.getName());
                        holder.date_time.setText("Created Date : "+user.getDate()+", "+user.getTime());
                        Picasso.get().load(user.getImage()).placeholder(R.drawable.account_circle).error(R.drawable.account_circle).into(holder.user_profile);
                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent=new Intent(getApplicationContext(),DetailsActivity.class);
                                intent.putExtra("view","user");
                                intent.putExtra("sid",user.getPhone());
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