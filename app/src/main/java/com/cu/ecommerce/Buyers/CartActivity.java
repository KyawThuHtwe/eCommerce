package com.cu.ecommerce.Buyers;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cu.ecommerce.Model.Cart;
import com.cu.ecommerce.Prevalent.Prevalent;
import com.cu.ecommerce.R;
import com.cu.ecommerce.ViewHolder.CartViewHolder;
import com.cu.ecommerce.ViewHolder.UserCardView;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class CartActivity extends AppCompatActivity {

    RecyclerView cart_list;
    TextView msg;
    Button next;
    ImageView back;
    TextView total_price;

    private int overTotalPrice=0;
    String agentID="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        agentID=getIntent().getStringExtra("agentID");

        cart_list=findViewById(R.id.recyclerView);
        cart_list=findViewById(R.id.recyclerView);
        total_price=findViewById(R.id.totalPrice);
        msg=findViewById(R.id.msg);
        next=findViewById(R.id.next);

        cart_list.setHasFixedSize(true);
        cart_list.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getApplicationContext(),ConfirmFinalOrderActivity.class);
                intent.putExtra("agentID",agentID);
                intent.putExtra("Total Price",String.valueOf(overTotalPrice));
                startActivity(intent);
                finish();
            }
        });
        back=findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        try {
            overTotalPrice=0;
            checkOrderState();
            DatabaseReference cartRef= FirebaseDatabase.getInstance().getReference();
            cartRef= FirebaseDatabase.getInstance().getReference()
                    .child("Cart List")
                    .child("User View")
                    .child(agentID)
                    .child(Prevalent.currentOnlineUser.getPhone()).child("Products");
            FirebaseRecyclerOptions<Cart> options =
                    new FirebaseRecyclerOptions.Builder<Cart>()
                            .setQuery(cartRef.orderByChild("sid").equalTo(agentID), Cart.class)
                            .build();
            FirebaseRecyclerAdapter<Cart, UserCardView> adapter =
                    new FirebaseRecyclerAdapter<Cart, UserCardView>(options) {
                        @SuppressLint("SetTextI18n")
                        @Override
                        protected void onBindViewHolder(@NonNull UserCardView holder, int i, @NonNull Cart cart) {
                            try {
                                holder.name.setText(cart.getPname());
                                holder.quantity.setText("Quantity : " + cart.getQuantity());
                                holder.price.setText("Price : " + cart.getPrice() + " Kyats");
                                Picasso.get().load(cart.getImage()).placeholder(R.drawable.ic_launcher_foreground).error(R.drawable.ic_launcher_background).into(holder.image);

                                int p = Integer.parseInt(cart.getPrice().split(" ")[0]);
                                overTotalPrice += p * Integer.parseInt(cart.getQuantity());
                                total_price.setText(overTotalPrice + " Kyats");

                                holder.edit.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        try {
                                            Intent intent = new Intent(getApplicationContext(), ProductDetailActivity.class);
                                            intent.putExtra("agentID", cart.getSid());
                                            intent.putExtra("pid", cart.getPid());
                                            intent.putExtra("qty", cart.getQuantity());
                                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                            startActivity(intent);
                                        } catch (Exception e) {
                                            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });

                                holder.remove.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        final DatabaseReference cartRef = FirebaseDatabase.getInstance().getReference().child("Cart List");
                                        cartRef.child("User View")
                                                .child(cart.getSid())
                                                .child(Prevalent.currentOnlineUser.getPhone())
                                                .child("Products")
                                                .child(cart.getPid())
                                                .removeValue()
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {
                                                            cartRef.child("Admin View")
                                                                    .child(cart.getSid())
                                                                    .child(Prevalent.currentOnlineUser.getPhone())
                                                                    .child("Products")
                                                                    .child(cart.getPid())
                                                                    .removeValue()
                                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                        @Override
                                                                        public void onComplete(@NonNull Task<Void> task) {
                                                                            if (task.isSuccessful()) {
                                                                                onStart();
                                                                                Toast.makeText(getApplicationContext(), "Item remove successfully", Toast.LENGTH_SHORT).show();
                                                                            }
                                                                        }
                                                                    }).addOnFailureListener(new OnFailureListener() {
                                                                @Override
                                                                public void onFailure(@NonNull Exception e) {
                                                                    Toast.makeText(getApplicationContext(), "Error : " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                                                }
                                                            });
                                                        }
                                                    }
                                                }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(getApplicationContext(), "Error : " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }
                                });

                            }catch (Exception e){
                                Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
                            }
                        }

                        @NonNull
                        @Override
                        public UserCardView onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_card_item_layout, parent, false);
                            return new UserCardView(view);
                        }
                    };
            cart_list.setAdapter(adapter);
            adapter.startListening();
        }catch (Exception e){
            Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
        }
    }

    private void checkOrderState(){
        DatabaseReference orderRef=FirebaseDatabase.getInstance().getReference().child("Orders").child(agentID).child(Prevalent.currentOnlineUser.getPhone());
        orderRef.addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    String shippingState=snapshot.child("state").getValue().toString();
                    String userName=snapshot.child("name").getValue().toString();
                    if(shippingState.equals("shipped")){
                        total_price.setText("Dear "+userName+"\n order is shipped successfully.");
                        cart_list.setVisibility(View.GONE);
                        msg.setVisibility(View.VISIBLE);
                        msg.setText("Congratulations, your final order has been shipped successfully. Soon it will be verified.");
                        next.setVisibility(View.GONE);
                        Toast.makeText(getApplicationContext(),"you can purchase more products, once you received your first final order",Toast.LENGTH_SHORT).show();

                    }else if(shippingState.equals("not shipped")){
                        total_price.setText("Shipping State = not Shipped.");
                        cart_list.setVisibility(View.GONE);
                        msg.setVisibility(View.VISIBLE);
                        next.setVisibility(View.GONE);
                        Toast.makeText(getApplicationContext(),"you can purchase more products, once you received your first final order",Toast.LENGTH_SHORT).show();

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

}