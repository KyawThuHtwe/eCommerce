package com.cu.ecommerce.Admin;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.cu.ecommerce.Model.Product;
import com.cu.ecommerce.Prevalent.Prevalent;
import com.cu.ecommerce.R;
import com.cu.ecommerce.Sellers.SellerMaintainProductActivity;
import com.cu.ecommerce.ViewHolder.ProductViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class AdminProductActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    DatabaseReference databaseReference;
    ImageView back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_product);
        back=findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        databaseReference= FirebaseDatabase.getInstance().getReference().child("Products");
        recyclerView=findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        FirebaseRecyclerOptions<Product> options=new FirebaseRecyclerOptions
                .Builder<Product>()
                .setQuery(databaseReference.orderByChild("sid").equalTo(Prevalent.currentOnlineUser.getPhone()),Product.class)
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
                                CharSequence opt[]=new CharSequence[]
                                        {
                                                "Edit","Delete"
                                        };
                                AlertDialog.Builder builder=new AlertDialog.Builder(AdminProductActivity.this);
                                builder.setTitle("Do you want to Options this Product, Are you sure?");
                                builder.setItems(opt, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int con) {
                                        if(con==0){
                                            Intent intent=new Intent(getApplicationContext(), SellerMaintainProductActivity.class);
                                            intent.putExtra("pid",product.getPid());
                                            startActivity(intent);
                                        }else if(con==1){
                                            deleteProduct(product.getPid());
                                        }
                                    }
                                });
                                builder.show();
                                builder.setCancelable(true);

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
    }
    private void deleteProduct(String pid) {
        databaseReference.child(pid)
                .removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(getApplicationContext(),"Delete this Product Successfully",Toast.LENGTH_SHORT).show();
                        }

                    }
                });
    }
}