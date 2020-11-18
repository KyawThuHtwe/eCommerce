package com.cu.ecommerce.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cu.ecommerce.Admin.AdminUserProductsActivity;
import com.cu.ecommerce.Model.Order;
import com.cu.ecommerce.R;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class OlderOrderAdapter extends RecyclerView.Adapter<OlderOrderAdapter.ViewHolder> {

    Context context;
    ArrayList<Order> orders;
    int i=0;

    public OlderOrderAdapter(Context context, ArrayList<Order> orders, int i) {
        this.context = context;
        this.orders = orders;
        this.i=i;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.older_order_item_layout,parent,false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        try {

            if(i==0){
                holder.text.setText("Sended");
            }else {
                holder.text.setText("Received");
            }
            holder.agentID.setText("Agent ID : " + orders.get(position).getSid());
            holder.name.setText("Name : " + orders.get(position).getName());
            holder.phone.setText("Phone : " + orders.get(position).getPhone());
            holder.price.setText("Total Amount : " + orders.get(position).getTotalAmount() + " Kyats");
            holder.date_time.setText("Order at : " + orders.get(position).getDate() + " " + orders.get(position).getTime());
            holder.address_city.setText("Shipping Address : " + orders.get(position).getAddress() + ", " + orders.get(position).getCity());
            holder.showOrderBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        Intent intent = new Intent(context, AdminUserProductsActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.putExtra("agentID", orders.get(position).getSid());
                        intent.putExtra("cid", orders.get(position).getCid());
                        intent.putExtra("oid", orders.get(position).getOid());
                        intent.putExtra("type", "older");
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
        return orders.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView name,phone,price,date_time,address_city;
        public TextView showOrderBtn,agentID,text;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.agentID=itemView.findViewById(R.id.agentID);
            this.name=itemView.findViewById(R.id.name);
            this.phone=itemView.findViewById(R.id.phone);
            this.price=itemView.findViewById(R.id.price);
            this.address_city=itemView.findViewById(R.id.address_city);
            this.date_time=itemView.findViewById(R.id.date_time);
            this.showOrderBtn=itemView.findViewById(R.id.show_all_products);
            this.text=itemView.findViewById(R.id.text);
        }
    }
}
