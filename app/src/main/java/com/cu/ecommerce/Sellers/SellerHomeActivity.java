package com.cu.ecommerce.Sellers;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.cu.ecommerce.Activities.MainActivity;
import com.cu.ecommerce.Admin.AdminCheckNewProductsActivity;
import com.cu.ecommerce.Model.Product;
import com.cu.ecommerce.R;
import com.cu.ecommerce.ViewHolder.ItemViewHolder;
import com.cu.ecommerce.ViewHolder.ProductViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class SellerHomeActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    DatabaseReference unVerifyProductRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_home);
        BottomNavigationView navView = findViewById(R.id.nav_view);
        navView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){

                    case R.id.navigation_add:
                        //LoadFragment(new SellerAddFragment());
                        Toast.makeText(getApplicationContext(),"Add",Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(getApplicationContext(), SellerCategoryActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK));

                        break;
                    case R.id.navigation_logout:
                        logout();
                        break;
                }
                return true;
            }
        });

        unVerifyProductRef= FirebaseDatabase.getInstance().getReference().child("Products");

        recyclerView=findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
    }
    @Override
    protected void onStart() {
        super.onStart();
        FirebaseRecyclerOptions<Product> options=
                new FirebaseRecyclerOptions.Builder<Product>()
                        .setQuery(unVerifyProductRef.orderByChild("sid").equalTo(FirebaseAuth.getInstance().getCurrentUser().getUid()),Product.class)
                        .build();
        FirebaseRecyclerAdapter<Product, ItemViewHolder> adapter=
                new FirebaseRecyclerAdapter<Product, ItemViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull ItemViewHolder holder, int i, @NonNull Product product) {
                        holder.name.setText(product.getName());
                        holder.price.setText(product.getPrice());
                        Picasso.get().load(product.getImage()).placeholder(R.drawable.ic_launcher_foreground).error(R.drawable.ic_launcher_background).into(holder.image);
                        holder.description.setText(product.getDescription());
                        holder.productState.setText(product.getProductState());
                        holder.layout.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                CharSequence opt[]=new CharSequence[]
                                        {
                                                "Yes","No"
                                        };
                                AlertDialog.Builder builder=new AlertDialog.Builder(SellerHomeActivity.this);
                                builder.setTitle("Do you want to Approved this Product, Are you sure?");
                                builder.setItems(opt, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int con) {
                                        if(con==0){
                                            deleteProduct(product.getPid());
                                        }else if(con==1){
                                            finish();
                                        }
                                    }
                                });
                                builder.show();

                            }
                        });

                    }

                    @NonNull
                    @Override
                    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view1= LayoutInflater.from(parent.getContext()).inflate(R.layout.seller_item_layout,parent,false);
                        return new ItemViewHolder(view1);
                    }
                };
        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }

    private void deleteProduct(String pid) {
        unVerifyProductRef.child(pid)
                .removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(getApplicationContext(),"",Toast.LENGTH_SHORT).show();
                        }

                    }
                });
    }

    public void logout() {
        FirebaseAuth firebaseAuth=FirebaseAuth.getInstance();
        firebaseAuth.signOut();
        startActivity(new Intent(getApplicationContext(), MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK));

    }
}