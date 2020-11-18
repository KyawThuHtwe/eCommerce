package com.cu.ecommerce.Admin;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.cu.ecommerce.Model.Seller;
import com.cu.ecommerce.Model.User;
import com.cu.ecommerce.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import de.hdodenhof.circleimageview.CircleImageView;

public class DetailsActivity extends AppCompatActivity {

    CircleImageView profileImage;
    TextView username,data;
    ImageView back;
    String ID="",view="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        profileImage=findViewById(R.id.profileImage);
        username=findViewById(R.id.username);
        data=findViewById(R.id.data);
        back=findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        ID= getIntent().getStringExtra("sid");
        view= getIntent().getStringExtra("view");

        if(view.equals("seller")){
            displaySellerInformation(ID);
        }else {
            displayUserInformation(ID);
        }


    }

    private void displayUserInformation(String id) {
        DatabaseReference detailRef= FirebaseDatabase.getInstance().getReference().child("Users");
        detailRef.child(id).addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    User user=snapshot.getValue(User.class);
                    username.setText(user.getName());
                    data.setText("Phone Number : "+user.getPhone()+"\n\n"+
                            "Password : "+user.getPassword()+"\n\n"+
                            "Address : "+user.getAddress()+"\n\n"+
                            "Created Date : "+user.getDate()+", "+user.getTime());

                    Picasso.get().load(user.getImage()).into(profileImage);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void displaySellerInformation(String id) {
        DatabaseReference detailRef= FirebaseDatabase.getInstance().getReference().child("Sellers");
        detailRef.child(id).addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    Seller seller=snapshot.getValue(Seller.class);
                    username.setText(seller.getName());
                    data.setText("Phone Number : "+seller.getPhone()+"\n\n"+
                            "Other Phone : "+seller.getPhoneOrder()+"\n\n"+
                            "Email : "+seller.getEmail()+"\n\n"+
                            "Password : "+seller.getPassword()+"\n\n"+
                            "Address : "+seller.getAddress()+"\n\n"+
                            "Created Date : "+seller.getDate()+", "+seller.getTime());

                    Picasso.get().load(seller.getImage()).into(profileImage);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}