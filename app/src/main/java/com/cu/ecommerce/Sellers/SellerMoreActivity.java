package com.cu.ecommerce.Sellers;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import de.hdodenhof.circleimageview.CircleImageView;
import io.paperdb.Paper;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cu.ecommerce.Activities.AboutActivity;
import com.cu.ecommerce.Activities.MainActivity;
import com.cu.ecommerce.Admin.AdminSettingsActivity;
import com.cu.ecommerce.Prevalent.Prevalent;
import com.cu.ecommerce.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class SellerMoreActivity extends AppCompatActivity {

    CircleImageView account_image;
    TextView account_name,account_id;
    LinearLayout account,about,logout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_more);

        account_image=findViewById(R.id.account_profile);
        account_name=findViewById(R.id.account_name);
        account_id=findViewById(R.id.account_id);
        account=findViewById(R.id.account);
        logout=findViewById(R.id.logout);
        about=findViewById(R.id.about);
        about.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), AboutActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
            }
        });
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(getApplicationContext(), MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK));
            }
        });
        account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), SellerSettingsActivity.class));
            }
        });
    }
    @Override
    protected void onStart() {
        super.onStart();
        accountLoading();
    }

    private void accountLoading() {
        DatabaseReference userRef= FirebaseDatabase.getInstance().getReference().child("Sellers").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
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
                    String id_value=snapshot.child("sid").getValue().toString();
                    account_name.setText(name_value);
                    account_id.setText("ID:"+id_value);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}