package com.cu.ecommerce.Sellers;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cu.ecommerce.Adapter.AdminOrderAdapter;
import com.cu.ecommerce.Adapter.OlderOrderAdapter;
import com.cu.ecommerce.Adapter.UserOrderAdapter;
import com.cu.ecommerce.Admin.AdminNewOrdersActivity;
import com.cu.ecommerce.Admin.AdminUserProductsActivity;
import com.cu.ecommerce.Model.Order;
import com.cu.ecommerce.Prevalent.Prevalent;
import com.cu.ecommerce.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class SellerNewOrderActivity extends AppCompatActivity {


    RecyclerView new_orderList,older_orderList;
    ArrayList<Order> orders,older_orders;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_new_order);
        new_orderList=findViewById(R.id.new_recyclerView);
        new_orderList.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        older_orderList=findViewById(R.id.older_recyclerView);
        older_orderList.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

    }
    @Override
    public void onStart() {
        super.onStart();
        orderLoadingNew();
        orderLoadingOlder();
    }
    public void orderLoadingOlder(){
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference().child("Older Order");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                older_orders=new ArrayList<>();
                older_orders.clear();
                if(snapshot.exists()){
                    for(DataSnapshot snap:snapshot.getChildren()) {
                        for(DataSnapshot dataSnapshot:snap.getChildren()){
                            for(DataSnapshot value:dataSnapshot.getChildren()){
                                Order order=value.child("Order").getValue(Order.class);
                                if(order.getSid().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                                    older_orders.add(order);
                                }

                            }
                            OlderOrderAdapter olderOrderAdapter=new OlderOrderAdapter(getApplicationContext(),older_orders,0);
                            older_orderList.setAdapter(olderOrderAdapter);
                            olderOrderAdapter.notifyDataSetChanged();
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void orderLoadingNew(){
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference().child("Orders").child(FirebaseAuth.getInstance().getCurrentUser().getUid());;

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                orders=new ArrayList<>();
                orders.clear();
                if(snapshot.exists()){
                    for(DataSnapshot dataSnapshot:snapshot.getChildren()){
                        Order order=dataSnapshot.getValue(Order.class);
                        orders.add(order);
                    }
                    AdminOrderAdapter adminOrderAdapter=new AdminOrderAdapter(getApplicationContext(),orders);
                    new_orderList.setAdapter(adminOrderAdapter);
                    adminOrderAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}