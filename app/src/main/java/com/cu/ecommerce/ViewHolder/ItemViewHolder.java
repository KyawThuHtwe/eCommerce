package com.cu.ecommerce.ViewHolder;


import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cu.ecommerce.R;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ItemViewHolder extends  RecyclerView.ViewHolder{
    public TextView name,price,description,productState;
    public ImageView image;
    public LinearLayout layout;

    public ItemViewHolder(@NonNull View itemView) {
        super(itemView);
        this.name=itemView.findViewById(R.id.name);
        this.price=itemView.findViewById(R.id.price);
        this.description=itemView.findViewById(R.id.description);
        this.productState=itemView.findViewById(R.id.productState);
        this.image=itemView.findViewById(R.id.image);
        this.layout=itemView.findViewById(R.id.layout);
    }

}