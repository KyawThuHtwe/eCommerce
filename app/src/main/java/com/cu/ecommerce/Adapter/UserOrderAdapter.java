package com.cu.ecommerce.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cu.ecommerce.Admin.AdminUserProductsActivity;
import com.cu.ecommerce.Buyers.OrderActivity;
import com.cu.ecommerce.Model.Cart;
import com.cu.ecommerce.Model.Order;
import com.cu.ecommerce.Prevalent.Prevalent;
import com.cu.ecommerce.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Handler;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class UserOrderAdapter extends RecyclerView.Adapter<UserOrderAdapter.ViewHolder> {

    Context context;
    ArrayList<Order> orders;

    public UserOrderAdapter(Context context, ArrayList<Order> orders) {
        this.context = context;
        this.orders = orders;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.user_order_layout,parent,false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        holder.agentID.setText("Agent ID : "+orders.get(position).getSid());
        holder.state.setText(orders.get(position).getState());
        holder.name.setText("Name : "+orders.get(position).getName());
        holder.phone.setText("Phone : "+orders.get(position).getPhone());
        holder.price.setText("Total Amount : "+orders.get(position).getTotalAmount()+" Kyats");
        holder.date_time.setText("Order at : "+orders.get(position).getDate()+" "+orders.get(position).getTime());
        holder.address_city.setText("Shipping Address : "+orders.get(position).getAddress()+", "+orders.get(position).getCity());

        holder.receive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(orders.get(position).getState().equals("Shipped")){
                    removeCardList(orders.get(position).getSid(),orders.get(position).getOid());
                    removeOrder(orders.get(position).getSid(),orders.get(position).getOid());
                    holder.itemView.setVisibility(View.GONE);
                }else {
                    Toast.makeText(context,"Not Shipped Order, Later Shipped",Toast.LENGTH_SHORT).show();
                }
            }
        });

        holder.showOrderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent intent = new Intent(context, AdminUserProductsActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("agentID", orders.get(position).getSid());
                    intent.putExtra("oid", orders.get(position).getOid());
                    intent.putExtra("type","new");
                    context.startActivity(intent);
                }catch (Exception e){
                    Toast.makeText(context,e.getMessage(),Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    private void removeCardList(String sid, String oid) {
        String saveCurrentDate,saveCurrentTime;

        Calendar calendar=Calendar.getInstance();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat currentDate=new SimpleDateFormat("MMM dd, yyyy");
        saveCurrentDate=currentDate.format(calendar.getTime());

        @SuppressLint("SimpleDateFormat") SimpleDateFormat currentTime=new SimpleDateFormat("HH:mm:ss a");
        saveCurrentTime=currentTime.format(calendar.getTime());

        DatabaseReference cartListRef=FirebaseDatabase.getInstance().getReference();
        cartListRef.child("Cart List").child("Admin View").child(sid).child(oid).child("Products")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            for(DataSnapshot dataSnapshot:snapshot.getChildren()) {
                                Cart cart = dataSnapshot.getValue(Cart.class);
                                HashMap<String,Object> cartMap=new HashMap<>();
                                cartMap.put("sid",cart.getSid());
                                cartMap.put("pid",cart.getPid());
                                cartMap.put("pname",cart.getPname());
                                cartMap.put("image",cart.getImage());
                                cartMap.put("price",cart.getPrice());
                                cartMap.put("date",cart.getDate());
                                cartMap.put("time",cart.getTime());
                                cartMap.put("quantity",cart.getQuantity());
                                cartMap.put("discount",cart.getDiscount());
                                cartMap.put("category",cart.getCategory());
                                cartListRef.child("Older Order").child(sid).child(oid).child(saveCurrentDate+" "+saveCurrentTime).child("Cart List").child(cart.getPid()).updateChildren(cartMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            cartListRef.child("Cart List").child("Admin View").child(sid).child(oid).removeValue()
                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                         if (task.isSuccessful()) {

                                                            }
                                                        }
                                                    });
                                        }
                                    }
                                });
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

    }

    private void removeOrder(String sid, String oid) {

        String saveCurrentDate,saveCurrentTime;

        Calendar calendar=Calendar.getInstance();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat currentDate=new SimpleDateFormat("MMM dd, yyyy");
        saveCurrentDate=currentDate.format(calendar.getTime());

        @SuppressLint("SimpleDateFormat") SimpleDateFormat currentTime=new SimpleDateFormat("HH:mm:ss a");
        saveCurrentTime=currentTime.format(calendar.getTime());

        DatabaseReference reference= FirebaseDatabase.getInstance().getReference();
        reference.child("Orders").child(sid).child(oid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    Order order=snapshot.getValue(Order.class);
                    HashMap<String,Object> orderMap=new HashMap<>();
                    orderMap.put("cid",oid);
                    orderMap.put("oid",saveCurrentDate+" "+saveCurrentTime);
                    orderMap.put("totalAmount",order.getTotalAmount());
                    orderMap.put("name",order.getName());
                    orderMap.put("phone",order.getPhone());
                    orderMap.put("address",order.getAddress());
                    orderMap.put("city",order.getCity());
                    orderMap.put("date",order.getDate());
                    orderMap.put("time",order.getTime());
                    orderMap.put("state",order.getState());
                    orderMap.put("sid",order.getSid());
                    reference.child("Older Order").child(sid).child(oid).child(saveCurrentDate+" "+saveCurrentTime).child("Order").updateChildren(orderMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(context,"Success",Toast.LENGTH_SHORT).show();
                                reference.child("Orders").child(sid).child(oid).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()){
                                            Toast.makeText(context,"Order Received Successfully",Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    @Override
    public int getItemCount() {
        return orders.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView name,phone,price,date_time,address_city;
        public TextView showOrderBtn,state,agentID;
        public LinearLayout receive;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.agentID=itemView.findViewById(R.id.agentID);
            this.name=itemView.findViewById(R.id.name);
            this.phone=itemView.findViewById(R.id.phone);
            this.price=itemView.findViewById(R.id.price);
            this.address_city=itemView.findViewById(R.id.address_city);
            this.date_time=itemView.findViewById(R.id.date_time);
            this.showOrderBtn=itemView.findViewById(R.id.show_all_products);
            this.receive=itemView.findViewById(R.id.receive);
            this.state=itemView.findViewById(R.id.state);
        }
    }
}
