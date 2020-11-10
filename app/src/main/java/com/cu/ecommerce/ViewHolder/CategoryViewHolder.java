package com.cu.ecommerce.ViewHolder;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cu.ecommerce.R;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class CategoryViewHolder extends RecyclerView.ViewHolder {

    public ImageView image;
    public TextView category;
    public LinearLayout layout;

    public CategoryViewHolder(@NonNull View itemView) {
        super(itemView);
        this.image=itemView.findViewById(R.id.image);
        this.category=itemView.findViewById(R.id.category);
        this.layout=itemView.findViewById(R.id.layout);
    }
}
