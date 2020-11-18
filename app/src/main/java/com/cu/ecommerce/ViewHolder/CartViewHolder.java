package com.cu.ecommerce.ViewHolder;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cu.ecommerce.R;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class CartViewHolder extends  RecyclerView.ViewHolder {
    public TextView name,price,quantity;
    public LinearLayout layout;
    public ImageView image;
    public CartViewHolder(@NonNull View itemView) {
        super(itemView);
        this.name=itemView.findViewById(R.id.name);
        this.image=itemView.findViewById(R.id.image);
        this.price=itemView.findViewById(R.id.price);
        this.quantity=itemView.findViewById(R.id.quantity);
        this.layout=itemView.findViewById(R.id.layout);
    }

}