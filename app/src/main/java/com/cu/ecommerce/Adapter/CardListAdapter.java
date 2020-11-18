package com.cu.ecommerce.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cu.ecommerce.Buyers.CartActivity;
import com.cu.ecommerce.Buyers.ProductDetailActivity;
import com.cu.ecommerce.Model.Cart;
import com.cu.ecommerce.Prevalent.Prevalent;
import com.cu.ecommerce.R;
import com.cu.ecommerce.ViewHolder.CartViewHolder;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

public class CardListAdapter extends RecyclerView.Adapter<CardListAdapter.ViewHolder> {

    Context context;
    ArrayList<Cart> carts;
    private int overTotalPrice=0;

    public CardListAdapter(Context context, ArrayList<Cart> carts) {
        this.context = context;
        this.carts = carts;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.card_item_layout,parent,false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        try {
            holder.name.setText("Name = " + carts.get(position).getPname());
            holder.quantity.setText("Quantity = " + carts.get(position).getQuantity());
            holder.price.setText("Price = " + carts.get(position).getPrice());
            Picasso.get().load(carts.get(position).getImage()).placeholder(R.drawable.ic_launcher_foreground).error(R.drawable.ic_launcher_background).into(holder.image);

            String first=carts.get(position).getPrice().split(" ")[0];

            int oneTypeProductPrice = Integer.parseInt(first) * Integer.parseInt(carts.get(position).getQuantity());
            overTotalPrice += oneTypeProductPrice;

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CharSequence options[] = new CharSequence[]
                            {
                                    "Edit",
                                    "Remove"
                            };
                    AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                    builder.setTitle("Cart Options");
                    builder.setItems(options, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int con) {
                            if (con == 0) {
                                try {
                                    Intent intent = new Intent(context, ProductDetailActivity.class);
                                    intent.putExtra("agentID", carts.get(position).getSid());
                                    intent.putExtra("pid", carts.get(position).getPid());
                                    intent.putExtra("qty", carts.get(position).getQuantity());
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    context.startActivity(intent);
                                }catch (Exception e){
                                    Toast.makeText(context,e.getMessage(),Toast.LENGTH_SHORT).show();
                                }
                            }else if(con==1){
                                final DatabaseReference cartRef = FirebaseDatabase.getInstance().getReference().child("Cart List");
                                cartRef.child("User View")
                                        .child(carts.get(position).getSid())
                                        .child(Prevalent.currentOnlineUser.getPhone())
                                        .child("Products")
                                        .child(carts.get(position).getPid())
                                        .removeValue()
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    try {
                                                        cartRef.child("Admin View")
                                                                .child(carts.get(position).getSid())
                                                                .child(Prevalent.currentOnlineUser.getPhone())
                                                                .child("Products")
                                                                .child(carts.get(position).getPid())
                                                                .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                notifyItemChanged(position);
                                                                Toast.makeText(context, "Item remove successfully", Toast.LENGTH_SHORT).show();

                                                            }
                                                        });


                                                         }catch (Exception e){
                                                        Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(context, "Error : " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });

                            }
                        }
                    });
                    builder.show();

                }
            });
        }catch (Exception e){
            Toast.makeText(context,e.getMessage(),Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public int getItemCount() {
        return carts.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView name,price,quantity;
        public LinearLayout layout;
        public ImageView image;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.name=itemView.findViewById(R.id.name);
            this.image=itemView.findViewById(R.id.image);
            this.price=itemView.findViewById(R.id.price);
            this.quantity=itemView.findViewById(R.id.quantity);
            this.layout=itemView.findViewById(R.id.layout);
        }
    }
}
