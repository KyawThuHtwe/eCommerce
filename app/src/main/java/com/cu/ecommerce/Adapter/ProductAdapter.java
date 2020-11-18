package com.cu.ecommerce.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cu.ecommerce.Buyers.ProductDetailActivity;
import com.cu.ecommerce.Model.Product;
import com.cu.ecommerce.Prevalent.Prevalent;
import com.cu.ecommerce.R;
import com.cu.ecommerce.ViewHolder.ProductViewHolder;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ViewHoler> {

    Context context;
    ArrayList<Product> product;
    boolean is_fav=false;

    public ProductAdapter(Context context, ArrayList<Product> product) {
        this.context = context;
        this.product = product;
    }

    @NonNull
    @Override
    public ViewHoler onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.product_item_layout,parent,false);
        return new ViewHoler(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHoler holder, int position) {

        holder.name.setText(product.get(position).getName());
        holder.price.setText(product.get(position).getPrice()+" Kyats");
        Picasso.get().load(product.get(position).getImage()).placeholder(R.drawable.ic_launcher_foreground).error(R.drawable.ic_launcher_background).into(holder.image);
        //holder.description.setText(product.get(position).getDescription());

        DatabaseReference favRef= FirebaseDatabase.getInstance().getReference().child("Favorite");
        favRef.child(Prevalent.currentOnlineUser.getPhone()).child(product.get(position).getSid()).child(product.get(position).getPid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            is_fav=true;
                            holder.favorite.setVisibility(View.VISIBLE);
                            holder.unfavorite.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
        if(is_fav){
            holder.favorite.setVisibility(View.VISIBLE);
            holder.unfavorite.setVisibility(View.GONE);
        }else {
            holder.unfavorite.setVisibility(View.VISIBLE);
            holder.favorite.setVisibility(View.GONE);
        }

        holder.favorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeFav(product.get(position).getSid(),product.get(position).getPid());
                holder.unfavorite.setVisibility(View.VISIBLE);
                holder.favorite.setVisibility(View.GONE);
            }
        });
        holder.unfavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addFav(product.get(position).getSid(),product.get(position).getPid());
                holder.unfavorite.setVisibility(View.GONE);
                holder.favorite.setVisibility(View.VISIBLE);
            }
        });

          holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    Toast.makeText(context,product.get(position).getSid(),Toast.LENGTH_SHORT).show();
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


    private void removeFav(String sid,String pid) {
        DatabaseReference favRef= FirebaseDatabase.getInstance().getReference().child("Favorite");
        favRef.child(Prevalent.currentOnlineUser.getPhone()).child(sid).child(pid)
                .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(context,"Remove Favorite",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void addFav(String sid, String pid) {
        DatabaseReference favRef= FirebaseDatabase.getInstance().getReference().child("Favorite");
        HashMap<String,Object> favMap=new HashMap<>();
        favMap.put(pid,pid);
        favRef.child(Prevalent.currentOnlineUser.getPhone()).child(sid).updateChildren(favMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(context,"Favorite",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }


    @Override
    public int getItemCount() {
        return product.size();
    }

    public class ViewHoler extends RecyclerView.ViewHolder {
        public TextView name,price,description;
        public ImageView image,favorite,unfavorite;
        public LinearLayout layout;
        public ViewHoler(@NonNull View itemView) {
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
