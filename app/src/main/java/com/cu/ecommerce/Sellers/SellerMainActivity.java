package com.cu.ecommerce.Sellers;

import androidx.appcompat.app.AppCompatActivity;
import io.paperdb.Paper;

import android.app.LocalActivityManager;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TabHost;
import android.widget.TextView;

import com.cu.ecommerce.Activities.AboutActivity;
import com.cu.ecommerce.Activities.MainActivity;
import com.cu.ecommerce.Admin.AdminCheckNewProductsActivity;
import com.cu.ecommerce.Admin.AdminHomeActivity;
import com.cu.ecommerce.Admin.AdminMainActivity;
import com.cu.ecommerce.Admin.AdminNewOrdersActivity;
import com.cu.ecommerce.Admin.AdminSellersViewActivity;
import com.cu.ecommerce.Admin.AdminSettingsActivity;
import com.cu.ecommerce.Admin.AdminUsersViewActivity;
import com.cu.ecommerce.R;
import com.google.firebase.auth.FirebaseAuth;

public class SellerMainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_main);
        TabHost tabHost = findViewById(R.id.tabhost);
        LocalActivityManager localActivityManager=new LocalActivityManager(this,false);
        localActivityManager.dispatchCreate(savedInstanceState);
        tabHost.setup(localActivityManager);
        TabHost.TabSpec spec;
        spec=tabHost.newTabSpec("Home");
        spec.setIndicator(createTabIndicater(tabHost,"Home",R.drawable.home));
        spec.setContent(new Intent(getApplicationContext(), SellerHomeActivity.class));
        tabHost.addTab(spec);

        spec=tabHost.newTabSpec("New");
        spec.setIndicator(createTabIndicater(tabHost,"New",R.drawable.news));
        spec.setContent(new Intent(getApplicationContext(), SellerNewActivity.class));
        tabHost.addTab(spec);

        spec=tabHost.newTabSpec("Order");
        spec.setIndicator(createTabIndicater(tabHost,"Order",R.drawable.order));
        spec.setContent(new Intent(getApplicationContext(), SellerNewOrderActivity.class));
        tabHost.addTab(spec);

        spec=tabHost.newTabSpec("More");
        spec.setIndicator(createTabIndicater(tabHost,"More",R.drawable.more));
        spec.setContent(new Intent(getApplicationContext(), SellerMoreActivity.class));
        tabHost.addTab(spec);
    }

    private View createTabIndicater(TabHost tabHost, String str, int iconResource) {
        View view= LayoutInflater.from(getApplicationContext()).inflate(R.layout.tab_indicator,tabHost.getTabWidget(),false);
        ImageView icon=view.findViewById(R.id.icon);
        TextView text=view.findViewById(R.id.text);
        icon.setImageResource(iconResource);
        text.setText(str);
        return view;
    }
}