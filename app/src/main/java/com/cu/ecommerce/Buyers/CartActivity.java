package com.cu.ecommerce.Buyers;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.cu.ecommerce.Model.Cart;
import com.cu.ecommerce.Prevalent.Prevalent;
import com.cu.ecommerce.R;
import com.cu.ecommerce.ViewHolder.CartViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class CartActivity extends AppCompatActivity {

    RecyclerView cart_list;
    TextView total_price,msg;
    Button next;

    private int overTotalPrice=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        cart_list=findViewById(R.id.recyclerView);
        total_price=findViewById(R.id.totalPrice);
        msg=findViewById(R.id.msg);
        next=findViewById(R.id.next);

        cart_list.setHasFixedSize(true);
        cart_list.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                total_price.setText("Total Price = "+overTotalPrice+"$");
                Intent intent=new Intent(getApplicationContext(),ConfirmFinalOrderActivity.class);
                intent.putExtra("Total Price",String.valueOf(overTotalPrice));
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        checkOrderState();

        final DatabaseReference cartRef= FirebaseDatabase.getInstance().getReference().child("Cart List");
        FirebaseRecyclerOptions<Cart> options=
                new FirebaseRecyclerOptions.Builder<Cart>()
                .setQuery(cartRef.child("User View")
                .child(Prevalent.currentOnlineUser.getPhone())
                .child("Products"),Cart.class)
                .build();
        FirebaseRecyclerAdapter<Cart, CartViewHolder> adapter=
                new FirebaseRecyclerAdapter<Cart, CartViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull CartViewHolder holder, int i, @NonNull Cart cart) {
                        holder.name.setText(cart.getPname());
                        holder.quantity.setText("Quantity = "+cart.getQuantity());
                        holder.price.setText("Price = "+cart.getPrice()+"$");

                        int oneTypeProductPrice=Integer.valueOf(cart.getPrice())*Integer.valueOf(cart.getQuantity());
                        overTotalPrice+=oneTypeProductPrice;

                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                CharSequence options[]=new CharSequence[]{"Edit","Remove"};
                                AlertDialog.Builder builder=new AlertDialog.Builder(CartActivity.this);
                                builder.setTitle("Cart Options");
                                builder.setItems(options, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int con) {
                                        if(con==0){
                                            Intent intent=new Intent(getApplicationContext(), ProductDetailActivity.class);
                                            intent.putExtra("pid",cart.getPid());
                                            startActivity(intent);
                                        }else if(i==1){
                                            cartRef.child("User View")
                                                    .child(Prevalent.currentOnlineUser.getPhone())
                                                    .child("Products")
                                                    .child(cart.getPid())
                                                    .removeValue()
                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if(task.isSuccessful()){
                                                                Toast.makeText(getApplicationContext(),"Item remove successfully",Toast.LENGTH_SHORT).show();
                                                            }
                                                        }
                                                    });
                                        }
                                    }
                                });
                                builder.show();

                            }
                        });
                    }

                    @NonNull
                    @Override
                    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.card_item_layout,parent,false);
                        return new CartViewHolder(view);
                    }
                };

        cart_list.setAdapter(adapter);
        adapter.startListening();
    }

    private void checkOrderState(){
        DatabaseReference orderRef=FirebaseDatabase.getInstance().getReference().child("Orders").child(Prevalent.currentOnlineUser.getPhone());
        orderRef.addValueEventListener(new ValueEventListener() {
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