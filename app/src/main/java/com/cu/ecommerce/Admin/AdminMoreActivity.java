package com.cu.ecommerce.Admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import de.hdodenhof.circleimageview.CircleImageView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cu.ecommerce.Prevalent.Prevalent;
import com.cu.ecommerce.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class AdminMoreActivity extends AppCompatActivity {

    CircleImageView account_image;
    TextView account_name,account_id;
    LinearLayout seller,user,account;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_more);

        account_image=findViewById(R.id.account_profile);
        account_name=findViewById(R.id.account_name);
        account_id=findViewById(R.id.account_id);
        seller=findViewById(R.id.seller);
        user=findViewById(R.id.user);
        account=findViewById(R.id.account);
        account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),AdminSettingsActivity.class));
            }
        });
        seller.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),AdminSellersViewActivity.class));
            }
        });
        user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),AdminUsersViewActivity.class));
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        accountLoading();
    }

    private void accountLoading() {
        DatabaseReference userRef= FirebaseDatabase.getInstance().getReference().child("Admins").child(Prevalent.currentOnlineUser.getPhone());
        userRef.addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    if(!snapshot.child("image").getValue().equals("default")){
                        String image=snapshot.child("image").getValue().toString();
                        Picasso.get().load(image).into(account_image);
                    }
                    String name_value=snapshot.child("name").getValue().toString();
                    account_name.setText(name_value);
                    account_id.setText("Admin");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

}