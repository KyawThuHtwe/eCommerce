package com.cu.ecommerce.ViewHolder;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cu.ecommerce.R;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ProductViewHolder extends RecyclerView.ViewHolder{
    public TextView name,price,description;
    public ImageView image;
    public LinearLayout layout;

    public ProductViewHolder(@NonNull View itemView){
        super(itemView);
        this.name=itemView.findViewById(R.id.name);
        this.price=itemView.findViewById(R.id.price);
        this.description=itemView.findViewById(R.id.description);
        this.image=itemView.findViewById(R.id.image);
        this.layout=itemView.findViewById(R.id.layout);
    }
}
