package com.cu.ecommerce.Admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.cu.ecommerce.Model.Category;
import com.cu.ecommerce.R;
import com.cu.ecommerce.ViewHolder.CategoryViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

public class AdminCategoryActivity extends AppCompatActivity {

    FloatingActionButton add_category;
    RecyclerView categoryList;
    DatabaseReference categoryRef;
    ImageView back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_category);
        try {
            categoryRef = FirebaseDatabase.getInstance().getReference().child("Category");
            categoryList = findViewById(R.id.recyclerView);
            categoryList.setLayoutManager(new GridLayoutManager(getApplicationContext(), 3));

            add_category = findViewById(R.id.add_category);
            add_category.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(getApplicationContext(), AdminAddNewCategoryActivity.class));
                }
            });
            back=findViewById(R.id.back);
            back.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
        }catch (Exception e){
            Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
        }

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
                                try {
                                    CharSequence opt[] = new CharSequence[]
                                            {
                                                    "Edit", "Delete"
                                            };
                                    AlertDialog.Builder builder = new AlertDialog.Builder(AdminCategoryActivity.this);
                                    builder.setTitle("Have you shipped this order products?");
                                    builder.setItems(opt, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int con) {
                                            if (con == 0) {
                                                dialog.dismiss();
                                            } else if(con==1){
                                                String uID = getRef(i).getKey().toString();
                                                removeCategory(uID);
                                                dialog.dismiss();
                                            }
                                        }
                                    });
                                    builder.show();
                                }catch (Exception e){
                                    Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
                                }
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

    private void removeCategory(String uID) {
        categoryRef.child(uID).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(getApplicationContext(),"Success",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

}