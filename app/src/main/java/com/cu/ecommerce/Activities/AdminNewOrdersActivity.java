package com.cu.ecommerce.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cu.ecommerce.Model.AdminOrders;
import com.cu.ecommerce.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AdminNewOrdersActivity extends AppCompatActivity {

    RecyclerView orderList;
    DatabaseReference orderRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_new_orders);
        orderRef= FirebaseDatabase.getInstance().getReference().child("Orders");
        orderList=findViewById(R.id.recyclerView);
        orderList.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseRecyclerOptions<AdminOrders> options=
                new FirebaseRecyclerOptions.Builder<AdminOrders>()
                .setQuery(orderRef,AdminOrders.class)
                .build();
        FirebaseRecyclerAdapter<AdminOrders,AdminOrdersViewHolder> adapter=
                new FirebaseRecyclerAdapter<AdminOrders, AdminOrdersViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull AdminOrdersViewHolder holder, int i, @NonNull AdminOrders adminOrders) {
                        holder.name.setText("Name : "+adminOrders.getName());
                        holder.phone.setText("Phone : "+adminOrders.getPhone());
                        holder.price.setText("Total Amount : "+adminOrders.getTotalAmount());
                        holder.date_time.setText("Order at : "+adminOrders.getDate()+" "+adminOrders.getTime());
                        holder.address_city.setText("Shipping Address : "+adminOrders.getAddress()+", "+adminOrders.getCity());

                        holder.showOrderBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                String uID=getRef(i).getKey();
                                Intent intent=new Intent(getApplicationContext(),AdminUserProductsActivity.class);
                                intent.putExtra("uid",uID);
                                startActivity(intent);

                            }
                        });
                    }

                    @NonNull
                    @Override
                    public AdminOrdersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.order_layout,parent,false);
                        return new AdminOrdersViewHolder(view);
                    }
                };
        orderList.setAdapter(adapter);
        adapter.startListening();
    }
    public static class AdminOrdersViewHolder extends RecyclerView.ViewHolder{

        public TextView name,phone,price,date_time,address_city;
        public Button showOrderBtn;

        public AdminOrdersViewHolder(@NonNull View itemView) {
            super(itemView);
            this.name=itemView.findViewById(R.id.name);
            this.phone=itemView.findViewById(R.id.phone);
            this.price=itemView.findViewById(R.id.price);
            this.address_city=itemView.findViewById(R.id.address_city);
            this.date_time=itemView.findViewById(R.id.date_time);
            this.showOrderBtn=itemView.findViewById(R.id.show_all_products);
        }
    }
}