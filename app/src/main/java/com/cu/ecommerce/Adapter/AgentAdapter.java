package com.cu.ecommerce.Adapter;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.cu.ecommerce.Buyers.AgentsActivity;
import com.cu.ecommerce.Model.Seller;
import com.cu.ecommerce.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;

public class AgentAdapter extends RecyclerView.Adapter<AgentAdapter.ViewHolder> {

    Context context;
    ArrayList<Seller> sellers;

    public AgentAdapter(Context context, ArrayList<Seller> sellers) {
        this.context = context;
        this.sellers = sellers;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.agent_item_layout,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        try {
            if(!TextUtils.isEmpty(sellers.get(position).getImage())){
                Picasso.get().load(sellers.get(position).getImage()).into(holder.agent_profile);
            }

            holder.agent_name.setText(sellers.get(position).getName());

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        Intent intent = new Intent(context, AgentsActivity.class);
                        intent.putExtra("agentID", sellers.get(position).getSid());
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);
                    } catch (Exception e) {
                        Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }catch (Exception e){
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public int getItemCount() {
        return sellers.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public CircleImageView agent_profile;
        public TextView agent_name;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.agent_profile=itemView.findViewById(R.id.agent_profile);
            this.agent_name=itemView.findViewById(R.id.agent_name);
        }
    }
}
