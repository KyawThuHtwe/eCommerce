package com.cu.ecommerce.Sellers.Fragments;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.cu.ecommerce.Sellers.SellerMaintainProductActivity;
import com.cu.ecommerce.Model.Product;
import com.cu.ecommerce.R;
import com.cu.ecommerce.Sellers.SellerCategoryActivity;
import com.cu.ecommerce.ViewHolder.ItemViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

public class SellerHomeFragment extends Fragment {

    RecyclerView recyclerView;
    DatabaseReference unVerifyProductRef;
    FloatingActionButton add;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_seller_home, container, false);

        add=view.findViewById(R.id.add);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent intent=new Intent(getContext(), SellerCategoryActivity.class);
                    intent.putExtra("type","seller");
                    startActivity(intent);
                }catch (Exception e){
                    Toast.makeText(getContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
                }
            }
        });

        unVerifyProductRef= FirebaseDatabase.getInstance().getReference().child("Products");
        recyclerView=view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        return view;
    }

    @Override
    public void onStart() {
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
                        holder.price.setText(product.getPrice()+" Kyats");
                        Picasso.get().load(product.getImage()).placeholder(R.drawable.ic_launcher_foreground).error(R.drawable.ic_launcher_background).into(holder.image);
                        holder.description.setText(product.getDescription());
                        holder.productState.setText(product.getProductState());
                        holder.layout.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                CharSequence opt[]=new CharSequence[]
                                        {
                                                "Edit","Delete"
                                        };
                                AlertDialog.Builder builder=new AlertDialog.Builder(getContext());
                                builder.setTitle("Do you want to Options this Product, Are you sure?");
                                builder.setItems(opt, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int con) {
                                        if(con==0){
                                            Intent intent=new Intent(getContext(), SellerMaintainProductActivity.class);
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
                            Toast.makeText(getContext(),"Delete this Product Successfully",Toast.LENGTH_SHORT).show();
                        }

                    }
                });
    }

}