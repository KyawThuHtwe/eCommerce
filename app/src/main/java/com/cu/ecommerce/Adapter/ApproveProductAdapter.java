package com.cu.ecommerce.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cu.ecommerce.Buyers.AgentDetailActivity;
import com.cu.ecommerce.Model.Product;
import com.cu.ecommerce.Model.Seller;
import com.cu.ecommerce.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;

public class ApproveProductAdapter extends RecyclerView.Adapter<ApproveProductAdapter.ViewHolder> {

    Context context;
    ArrayList<Product> products;

    public ApproveProductAdapter(Context context, ArrayList<Product> products) {
        this.context = context;
        this.products = products;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.approve_product_item_layout,parent,false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        holder.name.setText(products.get(position).getName());
        holder.seller_id.setText("ID:"+products.get(position).getSid());
        holder.price.setText(products.get(position).getPrice()+" Kyats");
        Picasso.get().load(products.get(position).getImage()).placeholder(R.drawable.ic_launcher_foreground).error(R.drawable.ic_launcher_background).into(holder.image);
        holder.description.setText(products.get(position).getDescription());

        holder.approve.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeProductState(products.get(position).getSid(),products.get(position).getPid());
                holder.itemView.setVisibility(View.GONE);
            }
        });
        holder.seller.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent intent = new Intent(context, AgentDetailActivity.class);
                    intent.putExtra("sid", products.get(position).getSid());
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                }catch (Exception e){
                    Toast.makeText(context,e.getMessage(),Toast.LENGTH_SHORT).show();
                }
            }
        });

        DatabaseReference unVerifyProductRef=FirebaseDatabase.getInstance().getReference();
        unVerifyProductRef.child("Sellers").child(products.get(position).getSid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            Seller seller=snapshot.getValue(Seller.class);
                            if(!TextUtils.isEmpty(seller.getImage())){
                                Picasso.get().load(seller.getImage()).error(R.drawable.ic_launcher_background).into(holder.seller_profile);
                            }
                            holder.seller_name.setText(seller.getName());
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

    }



    private void changeProductState(String agentID,String pid) {
        DatabaseReference unVerifyProductRef=FirebaseDatabase.getInstance().getReference();
        unVerifyProductRef.child("Products").child(agentID).child(pid)
                .child("productState")
                .setValue("Approved")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(context,"That item has been approved, and it is now available for sale from the seller. ",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView name,price,description,seller_name,approve,seller_id;
        public ImageView image;
        public CircleImageView seller_profile;
        public LinearLayout seller;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.name=itemView.findViewById(R.id.name);
            this.price=itemView.findViewById(R.id.price);
            this.description=itemView.findViewById(R.id.description);
            this.image=itemView.findViewById(R.id.image);
            this.seller_name=itemView.findViewById(R.id.seller_name);
            this.seller_profile=itemView.findViewById(R.id.seller_profile);
            this.approve=itemView.findViewById(R.id.approve);
            this.seller=itemView.findViewById(R.id.seller);
            this.seller_id=itemView.findViewById(R.id.seller_id);
        }
    }
}
