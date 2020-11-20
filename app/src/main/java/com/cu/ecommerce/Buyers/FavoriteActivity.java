package com.cu.ecommerce.Buyers;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cu.ecommerce.Adapter.ProductAdapter;
import com.cu.ecommerce.Model.Product;
import com.cu.ecommerce.Model.Seller;
import com.cu.ecommerce.Prevalent.Prevalent;
import com.cu.ecommerce.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

public class FavoriteActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    ArrayList<Product> products;
    ImageView back;
    String agentID="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite);

        agentID=getIntent().getStringExtra("sid");

        recyclerView=findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(getApplicationContext(),2));
        back=findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
    @Override
    public void onStart() {
        super.onStart();
        loadingFavorite(agentID);
    }
    public void loadingFavorite(String agentID){
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference();
        reference.child("Products").child(agentID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                products=new ArrayList<>();
                products.clear();
                if(snapshot.exists()){
                    for(DataSnapshot snap:snapshot.getChildren()){
                        Product product=snap.getValue(Product.class);
                        String getPid=product.getPid();
                        String getSid=product.getSid();
                        if(getSid.equals(agentID)){
                            reference.child("Favorite").child(Prevalent.currentOnlineUser.getPhone())
                                    .child(getSid)
                                    .child(getPid)
                                    .addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            if(snapshot.exists()){
                                                products.add(product);
                                                FavoriteAdapter favoriteAdapter=new FavoriteAdapter(getApplicationContext(),products);
                                                recyclerView.setAdapter(favoriteAdapter);
                                                favoriteAdapter.notifyDataSetChanged();
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });

                        }else {
                            recyclerView.setAdapter(null);
                        }
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private class FavoriteAdapter extends RecyclerView.Adapter<FavoriteAdapter.ViewHolder> {

        Context context;
        ArrayList<Product> product;

        public FavoriteAdapter(Context context, ArrayList<Product> product) {
            this.context = context;
            this.product = product;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.product_item_layout,parent,false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            holder.name.setText(product.get(position).getName());
            holder.price.setText(product.get(position).getPrice()+" Kyats");
            Picasso.get().load(product.get(position).getImage()).placeholder(R.drawable.ic_launcher_foreground).error(R.drawable.ic_launcher_background).into(holder.image);
            holder.description.setText(product.get(position).getDescription());

            DatabaseReference favRef= FirebaseDatabase.getInstance().getReference().child("Favorite");
            favRef.child(Prevalent.currentOnlineUser.getPhone()).child(product.get(position).getSid()).child(product.get(position).getPid())
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if(snapshot.exists()){
                                holder.favorite.setVisibility(View.VISIBLE);
                                holder.unfavorite.setVisibility(View.GONE);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
            holder.favorite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    removeFav(product.get(position).getSid(),product.get(position).getPid());
                    holder.layout.setVisibility(View.GONE);
                }
            });

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    try {
                        Intent intent = new Intent(context, ProductDetailActivity.class);
                        intent.putExtra("agentID", product.get(position).getSid());
                        intent.putExtra("pid", product.get(position).getPid());
                        intent.putExtra("qty", "1");
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);

                    }catch (Exception e){
                        Toast.makeText(context,e.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

        private void removeFav(String sid, String pid) {
            DatabaseReference favRef= FirebaseDatabase.getInstance().getReference().child("Favorite");
            favRef.child(Prevalent.currentOnlineUser.getPhone()).child(sid).child(pid)
                    .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        loadingFavorite(sid);
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return product.size();
        }
        public class ViewHolder extends RecyclerView.ViewHolder {
            public TextView name,price,description;
            public ImageView image,favorite,unfavorite;
            public LinearLayout layout;
            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                this.name=itemView.findViewById(R.id.name);
                this.price=itemView.findViewById(R.id.price);
                this.description=itemView.findViewById(R.id.description);
                this.image=itemView.findViewById(R.id.image);
                this.favorite=itemView.findViewById(R.id.favorite);
                this.unfavorite=itemView.findViewById(R.id.unfavorite);
                this.layout=itemView.findViewById(R.id.layout);
            }
        }
    }
}