package com.cu.ecommerce.ViewHolder;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.cu.ecommerce.R;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ProductViewHolder extends  RecyclerView.ViewHolder {
    public TextView name,description;
    public ImageView image;
    public ProductViewHolder(@NonNull View itemView) {
        super(itemView);
        this.name=itemView.findViewById(R.id.name);
        this.description=itemView.findViewById(R.id.description);
        this.image=itemView.findViewById(R.id.image);
    }

}
