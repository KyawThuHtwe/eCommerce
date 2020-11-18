package com.cu.ecommerce.Buyers;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.cu.ecommerce.Model.Cart;
import com.cu.ecommerce.Model.Product;
import com.cu.ecommerce.Prevalent.Prevalent;
import com.cu.ecommerce.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class ProductDetailActivity extends AppCompatActivity {

    ImageView productImage,back,favorite,unfavorite;
    ElegantNumberButton numberButton;
    TextView price,description,name;
    TextView addToCart,buyNow;
    boolean is_fav=false;

    String productID="",state="Normal",agentID="",qty="1",getImageString="",getPriceString="",getCategory="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

        back=findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        buyNow=findViewById(R.id.buy_now);

        buyNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(state.equals("Order Shipped") || state.equals("Order Placed")){
                    Toast.makeText(getApplicationContext(),"you can add purchase more products, once your order is shipped or confirmed.",Toast.LENGTH_LONG).show();
                }else {
                    addingToCart("now");
                }

            }
        });


        addToCart=findViewById(R.id.add_to_cart);
        productImage=findViewById(R.id.image);
        price=findViewById(R.id.price);
        description=findViewById(R.id.description);
        name=findViewById(R.id.name);
        numberButton=findViewById(R.id.number);

        agentID=getIntent().getStringExtra("agentID");
        productID=getIntent().getStringExtra("pid");
        qty=getIntent().getStringExtra("qty");

        getProductDetail(agentID,productID,qty);

        favorite=findViewById(R.id.favorite);
        unfavorite=findViewById(R.id.unfavorite);
        favorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeFav(agentID,productID);
                unfavorite.setVisibility(View.VISIBLE);
                favorite.setVisibility(View.GONE);
            }
        });
        unfavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addFav(agentID,productID);
                favorite.setVisibility(View.VISIBLE);
                unfavorite.setVisibility(View.GONE);
            }
        });
        DatabaseReference favRef= FirebaseDatabase.getInstance().getReference().child("Favorite");
        favRef.child(Prevalent.currentOnlineUser.getPhone()).child(agentID).child(productID)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                           is_fav=true;
                            favorite.setVisibility(View.VISIBLE);
                            unfavorite.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
        if(is_fav){
            favorite.setVisibility(View.VISIBLE);
            unfavorite.setVisibility(View.GONE);
        }else {
            unfavorite.setVisibility(View.VISIBLE);
            favorite.setVisibility(View.GONE);
        }


        addToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(state.equals("Order Shipped") || state.equals("Order Placed")){
                    Toast.makeText(getApplicationContext(),"you can add purchase more products, once your order is shipped or confirmed.",Toast.LENGTH_LONG).show();
                }else {
                    addingToCart("later");
                }
            }
        });
    }


    private void removeFav(String agentID, String pid) {
        DatabaseReference favRef= FirebaseDatabase.getInstance().getReference().child("Favorite");
        favRef.child(Prevalent.currentOnlineUser.getPhone()).child(agentID).child(pid)
                .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                     Toast.makeText(getApplicationContext(),"Remove Favorite",Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void addFav(String agentID, String pid) {
        DatabaseReference favRef= FirebaseDatabase.getInstance().getReference().child("Favorite");
        HashMap<String,Object> favMap=new HashMap<>();
        favMap.put(pid,pid);
        favRef.child(Prevalent.currentOnlineUser.getPhone()).child(agentID).updateChildren(favMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                             Toast.makeText(getApplicationContext(),"Favorite",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void addingToCart(String str) {
        String saveCurrentTime,saveCurrentDate;
        Calendar calendar=Calendar.getInstance();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat currentDate=new SimpleDateFormat("MMM dd, yyyy");
        saveCurrentDate=currentDate.format(calendar.getTime());

        @SuppressLint("SimpleDateFormat") SimpleDateFormat currentTime=new SimpleDateFormat("HH:mm:ss a");
        saveCurrentTime=currentTime.format(calendar.getTime());

        final DatabaseReference cartListRef=FirebaseDatabase.getInstance().getReference().child("Cart List");
        final HashMap<String,Object> cartMap=new HashMap<>();
        cartMap.put("sid",agentID);
        cartMap.put("pid",productID);
        cartMap.put("pname",name.getText().toString());
        cartMap.put("image",getImageString);
        cartMap.put("price",getPriceString);
        cartMap.put("date",saveCurrentDate);
        cartMap.put("time",saveCurrentTime);
        cartMap.put("quantity",numberButton.getNumber());
        cartMap.put("discount","");
        cartMap.put("category",getCategory);

        cartListRef.child("User View").child(agentID).child(Prevalent.currentOnlineUser.getPhone()).child("Products")
                .child(productID)
                .updateChildren(cartMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){

                            cartListRef.child("Admin View").child(agentID).child(Prevalent.currentOnlineUser.getPhone()).child("Products")
                                    .child(productID)
                                    .updateChildren(cartMap)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()){
                                                if(str.equals("now")){
                                                    Intent intent = new Intent(getApplicationContext(), CartActivity.class);
                                                    intent.putExtra("agentID", agentID);
                                                    startActivity(intent);
                                                    finish();
                                                }else{
                                                    Toast.makeText(getApplicationContext(),"Added to Cart List.",Toast.LENGTH_SHORT).show();
                                                    finish();
                                                }
                                            }

                                        }
                                    });

                        }
                    }
                });

    }

    public void getProductDetail(String agentID, String productID, String qty) {
        DatabaseReference productRef= FirebaseDatabase.getInstance().getReference().child("Products");
        productRef.child(agentID).child(productID).addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    Product product=snapshot.getValue(Product.class);
                    if(product.getSid().equals(agentID) && product.getPid().equals(productID)){
                        name.setText("Name : "+product.getName());
                        description.setText("Description : "+"\n\n"+product.getDescription());
                        price.setText("Price : "+product.getPrice()+" Kyats");
                        numberButton.setNumber(String.valueOf(qty));
                        Picasso.get().load(product.getImage()).into(productImage);
                        getPriceString=product.getPrice();
                        getImageString=product.getImage();
                        getCategory=product.getCategory();
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void checkOrderState(){
        DatabaseReference orderRef=FirebaseDatabase.getInstance().getReference().child("Orders").child(agentID).child(Prevalent.currentOnlineUser.getPhone());
        orderRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    String shippingState=snapshot.child("state").getValue().toString();
                    if(shippingState.equals("shipped")){
                        state="Order Shipped";
                    }else if(shippingState.equals("not shipped")){
                        state="Order Placed";
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        checkOrderState();
    }
}