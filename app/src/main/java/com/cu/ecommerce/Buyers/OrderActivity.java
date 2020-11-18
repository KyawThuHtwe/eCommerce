package com.cu.ecommerce.Buyers;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cu.ecommerce.Adapter.OlderOrderAdapter;
import com.cu.ecommerce.Adapter.ProductAdapter;
import com.cu.ecommerce.Adapter.UserOrderAdapter;
import com.cu.ecommerce.Model.Order;
import com.cu.ecommerce.Model.Product;
import com.cu.ecommerce.Prevalent.Prevalent;
import com.cu.ecommerce.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class OrderActivity extends AppCompatActivity {

    RecyclerView new_orderList,older_orderList;
    ArrayList<Order> orders,older_orders;
    ImageView back;
    String agentID="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);

        agentID=getIntent().getStringExtra("sid");

        new_orderList=findViewById(R.id.new_recyclerView);
        new_orderList.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        older_orderList=findViewById(R.id.older_recyclerView);
        older_orderList.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        back=findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        orderLoadingNew(agentID);
        orderLoadingOlder(agentID);
    }
    public void orderLoadingOlder(String agentID){
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
                                if(order.getSid().equals(agentID)){
                                    older_orders.add(order);
                                }
                            }
                            OlderOrderAdapter olderOrderAdapter=new OlderOrderAdapter(getApplicationContext(),older_orders,1);
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

    public void orderLoadingNew(String agentID){
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference().child("Orders");;

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                orders=new ArrayList<>();
                orders.clear();
                if(snapshot.exists()){
                    for(DataSnapshot snap:snapshot.getChildren()){
                        for(DataSnapshot dataSnapshot:snap.getChildren()){
                            Order order=dataSnapshot.getValue(Order.class);
                            if(order.getSid().equals(agentID)){
                                orders.add(order);
                            }

                        }
                        UserOrderAdapter userOrderAdapter=new UserOrderAdapter(getApplicationContext(),orders);
                        new_orderList.setAdapter(userOrderAdapter);
                        userOrderAdapter.notifyDataSetChanged();
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}