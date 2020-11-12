package com.cu.ecommerce.Admin;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;
import de.hdodenhof.circleimageview.CircleImageView;
import io.paperdb.Paper;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.cu.ecommerce.Activities.MainActivity;
import com.cu.ecommerce.Buyers.SettingActivity;
import com.cu.ecommerce.Prevalent.Prevalent;
import com.cu.ecommerce.R;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class AdminHomeActivity extends AppCompatActivity {

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_home);

        Paper.init(this);

        final DrawerLayout drawerLayout=findViewById(R.id.drawerLayout);
        findViewById(R.id.imageMenu).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });
        drawerLayout.close();
        NavigationView navigationView=findViewById(R.id.navigationView);
        navigationView.setItemIconTintList(null);
        NavController navController= Navigation.findNavController(this,R.id.navHostFragment);
        NavigationUI.setupWithNavController(navigationView,navController);
        final TextView textTitle=findViewById(R.id.textTitle);
        navController.addOnDestinationChangedListener(new NavController.OnDestinationChangedListener() {
            @Override
            public void onDestinationChanged(@NonNull NavController controller, @NonNull NavDestination destination, @Nullable Bundle arguments) {
                textTitle.setText(destination.getLabel());
            }
        });

        View headerView=navigationView.getHeaderView(0);
        TextView username=headerView.findViewById(R.id.username);
        CircleImageView profileImage=headerView.findViewById(R.id.profileImage);
        username.setText(Prevalent.currentOnlineUser.getPhone()+"");
        Picasso.get().load(Prevalent.currentOnlineUser.getImage()).placeholder(R.drawable.account_circle).into(profileImage);

    }

    public void logout(MenuItem item) {
        Intent intent=new Intent(getApplicationContext(), MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    public void setting(MenuItem item) {
        startActivity(new Intent(getApplicationContext(), AdminSettingsActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
    }
}