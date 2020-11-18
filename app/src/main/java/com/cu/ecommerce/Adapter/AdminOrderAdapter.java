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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class AdminOrderAdapter extends RecyclerView.Adapter<AdminOrderAdapter.ViewHolder> {

    Context context;
    ArrayList<Order> orders;

    public AdminOrderAdapter(Context context, ArrayList<Order> orders) {
        this.context = context;
        this.orders = orders;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.order_layout,parent,false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.agentID.setText("Agent ID : "+orders.get(position).getSid());
        holder.deliver.setText("Delivery : "+orders.get(position).getState());
        holder.name.setText("Name : "+orders.get(position).getName());
        holder.phone.setText("Phone : "+orders.get(position).getPhone());
        holder.price.setText("Total Amount : "+orders.get(position).getTotalAmount()+" Kyats");
        holder.date_time.setText("Order at : "+orders.get(position).getDate()+" "+orders.get(position).getTime());
        holder.address_city.setText("Shipping Address : "+orders.get(position).getAddress()+", "+orders.get(position).getCity());

        holder.showOrderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    Intent intent = new Intent(context, AdminUserProductsActivity.class);
                    intent.putExtra("agentID", orders.get(position).getSid());
                    intent.putExtra("oid", orders.get(position).getOid());
                    intent.putExtra("type","new");
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                }catch (Exception e){
                    Toast.makeText(context,e.getMessage(),Toast.LENGTH_SHORT).show();
                }

            }
        });

        holder.send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendOrder(orders.get(position).getSid(),orders.get(position).getOid());
                holder.itemView.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public int getItemCount() {
        return orders.size();
    }
    private void sendOrder(String sID,String oID) {
        DatabaseReference orderRef= FirebaseDatabase.getInstance().getReference().child("Orders");
        orderRef.child(sID).child(oID).child("state").setValue("Shipped").addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(context,"SuccessFul",Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView showOrderBtn,name,phone,price,date_time,address_city,deliver,agentID;
        public LinearLayout send;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.agentID=itemView.findViewById(R.id.agentID);
            this.name=itemView.findViewById(R.id.name);
            this.phone=itemView.findViewById(R.id.phone);
            this.price=itemView.findViewById(R.id.price);
            this.address_city=itemView.findViewById(R.id.address_city);
            this.date_time=itemView.findViewById(R.id.date_time);
            this.showOrderBtn=itemView.findViewById(R.id.show_all_products);
            this.deliver=itemView.findViewById(R.id.deliver);
            this.send=itemView.findViewById(R.id.send);
        }
    }
}
