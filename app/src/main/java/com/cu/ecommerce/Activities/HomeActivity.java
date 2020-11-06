package com.cu.ecommerce.Activities;

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

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.cu.ecommerce.Prevalent.Prevalent;
import com.cu.ecommerce.R;
import com.google.android.material.navigation.NavigationView;
import com.squareup.picasso.Picasso;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

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
        username.setText(Prevalent.currentOnlineUser.getName()+"");
        Picasso.get().load(Prevalent.currentOnlineUser.getImage()).placeholder(R.drawable.ic_launcher_foreground).into(profileImage);
    }

    public void logout(MenuItem item) {
        Paper.book().destroy();
        startActivity(new Intent(getApplicationContext(),MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK));
    }

    public void setting(MenuItem item) {
        startActivity(new Intent(getApplicationContext(),SettingActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
    }
}