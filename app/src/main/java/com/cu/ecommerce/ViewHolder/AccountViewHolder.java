package com.cu.ecommerce.ViewHolder;

import android.view.View;
import android.widget.TextView;

import com.cu.ecommerce.R;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;

public class AccountViewHolder extends RecyclerView.ViewHolder {
    public CircleImageView user_profile;
    public TextView username,date_time;
    public AccountViewHolder(@NonNull View itemView) {
        super(itemView);
        user_profile=itemView.findViewById(R.id.user_profile);
        username=itemView.findViewById(R.id.username);
        date_time=itemView.findViewById(R.id.date_time);
    }
}
