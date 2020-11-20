package com.cu.ecommerce.Adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cu.ecommerce.Admin.AdminHomeActivity;
import com.cu.ecommerce.Model.Product;
import com.cu.ecommerce.Prevalent.Prevalent;
import com.cu.ecommerce.R;
import com.cu.ecommerce.Sellers.SellerMaintainProductActivity;
import com.cu.ecommerce.ViewHolder.ItemViewHolder;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

public class MaintainProductAdapter extends RecyclerView.Adapter<MaintainProductAdapter.ViewHolder> {

    Context context;
    ArrayList<Product> products;

    public MaintainProductAdapter(Context context, ArrayList<Product> products) {
        this.context = context;
        this.products = products;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.seller_item_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.productState.setText(products.get(position).getProductState());
        holder.name.setText(products.get(position).getName());
        holder.price.setText(products.get(position).getPrice() + " Kyats");
        Picasso.get().load(products.get(position).getImage()).placeholder(R.drawable.ic_launcher_foreground).error(R.drawable.ic_launcher_background).into(holder.image);
        holder.description.setText(products.get(position).getDescription());

        holder.more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    CharSequence opt[] = new CharSequence[]
                            {
                                    "Edit", "Delete"
                            };
                    AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                    builder.setTitle("Do you want to Options this Product, Are you sure?");
                    builder.setItems(opt, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int con) {
                            if (con == 0) {
                                try {
                                    Intent intent = new Intent(context, SellerMaintainProductActivity.class);
                                    intent.putExtra("pid", products.get(position).getPid());
                                    intent.putExtra("sid", products.get(position).getSid());
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    context.startActivity(intent);
                                }catch (Exception e){
                                    Toast.makeText(context,e.getMessage(),Toast.LENGTH_SHORT).show();
                                }
                            } else if (con == 1) {
                                deleteProduct(products.get(position).getPid(),products.get(position).getImage());
                                holder.itemView.setVisibility(View.GONE);
                            }
                        }
                    });
                    builder.show();
                }catch (Exception e){
                    Toast.makeText(context,e.getMessage(),Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    private void deleteProduct(String pid,String image) {
        DatabaseReference unVerifyProductRef;
        unVerifyProductRef = FirebaseDatabase.getInstance().getReference().child("Products").child(Prevalent.currentOnlineUser.getPhone());
        unVerifyProductRef.child(pid)
                .removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            StorageReference storageReference= FirebaseStorage.getInstance().getReference().child("Product Images");
                            storageReference.child(image).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    Toast.makeText(context,"Delete this Product Successfully",Toast.LENGTH_SHORT).show();
                                }
                            });

                        }

                    }
                });
    }
    @Override
    public int getItemCount() {
        return products.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView name,price,description,productState;
        public ImageView image,more;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.name=itemView.findViewById(R.id.name);
            this.price=itemView.findViewById(R.id.price);
            this.description=itemView.findViewById(R.id.description);
            this.productState=itemView.findViewById(R.id.productState);
            this.image=itemView.findViewById(R.id.image);
            this.more=itemView.findViewById(R.id.more);
        }
    }
}
