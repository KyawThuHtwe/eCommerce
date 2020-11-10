package com.cu.ecommerce.Sellers;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.cu.ecommerce.Admin.AdminCategoryActivity;
import com.cu.ecommerce.Model.Category;
import com.cu.ecommerce.R;
import com.cu.ecommerce.ViewHolder.CategoryViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

public class SellerCategoryActivity extends AppCompatActivity {

    ImageView back;
    RecyclerView categoryList;
    DatabaseReference categoryRef;
    private String chooseCategory="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_category);

        back=findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), SellerHomeActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                finish();
            }
        });

        categoryRef = FirebaseDatabase.getInstance().getReference().child("Category");
        categoryList = findViewById(R.id.recyclerView);
        categoryList.setLayoutManager(new GridLayoutManager(getApplicationContext(), 3));
    }

    @Override
    public void onBackPressed() {
        Intent intent=new Intent(getApplicationContext(), SellerHomeActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<Category> options=
                new FirebaseRecyclerOptions.Builder<Category>()
                        .setQuery(categoryRef, Category.class)
                        .build();
        FirebaseRecyclerAdapter<Category, CategoryViewHolder> adapter=
                new FirebaseRecyclerAdapter<Category, CategoryViewHolder>(options) {
                    @SuppressLint("SetTextI18n")
                    @Override
                    protected void onBindViewHolder(CategoryViewHolder holder, final int i, @NonNull Category category) {
                        holder.category.setText(category.getName());
                        Picasso.get().load(category.getImage()).into(holder.image);

                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                chooseCategory=category.getName();
                                Toast.makeText(getApplicationContext(),chooseCategory,Toast.LENGTH_SHORT).show();
                                Intent intent=new Intent(getApplicationContext(), SellerAddNewProductActivity.class);
                                intent.putExtra("category",chooseCategory);
                                startActivity(intent);
                                finish();
                            }
                        });
                    }

                    @NonNull
                    @Override
                    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.category_view_holder,parent,false);
                        return new CategoryViewHolder(view);
                    }
                };
        categoryList.setAdapter(adapter);
        adapter.startListening();
    }
}