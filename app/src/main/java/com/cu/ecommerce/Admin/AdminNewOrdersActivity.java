package com.cu.ecommerce.Admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cu.ecommerce.Model.AdminOrders;
import com.cu.ecommerce.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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
                    protected void onBindViewHolder(@NonNull AdminOrdersViewHolder holder,final int i, @NonNull AdminOrders adminOrders) {
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

                        holder.layout.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                try {
                                    CharSequence opt[] = new CharSequence[]
                                            {
                                                    "Yes", "No"
                                            };
                                    AlertDialog.Builder builder = new AlertDialog.Builder(AdminNewOrdersActivity.this);
                                    builder.setTitle("Have you shipped this order products?");
                                    builder.setItems(opt, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int con) {
                                            if (con == 0) {
                                                String uID = getRef(i).getKey().toString();
                                                removeOrder(uID);

                                            } else {
                                                finish();
                                            }
                                        }
                                    });
                                    builder.show();
                                }catch (Exception e){
                                    Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
                                }
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

    private void removeOrder(String uID) {
        orderRef.child(uID).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(getApplicationContext(),"Success",Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    public static class AdminOrdersViewHolder extends RecyclerView.ViewHolder{

        public TextView name,phone,price,date_time,address_city;
        public Button showOrderBtn;
        public LinearLayout layout;

        public AdminOrdersViewHolder(@NonNull View itemView) {
            super(itemView);
            this.name=itemView.findViewById(R.id.name);
            this.phone=itemView.findViewById(R.id.phone);
            this.price=itemView.findViewById(R.id.price);
            this.address_city=itemView.findViewById(R.id.address_city);
            this.date_time=itemView.findViewById(R.id.date_time);
            this.showOrderBtn=itemView.findViewById(R.id.show_all_products);
            this.layout=itemView.findViewById(R.id.layout);
        }
    }
}