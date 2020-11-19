package com.cu.ecommerce.Admin;

import androidx.appcompat.app.AppCompatActivity;
import io.paperdb.Paper;

import android.app.LocalActivityManager;
import android.content.Intent;
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
import com.cu.ecommerce.R;

public class AdminMainActivity extends AppCompatActivity {

    ImageView settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_main);
        TabHost tabHost = findViewById(R.id.tabhost);
        LocalActivityManager localActivityManager=new LocalActivityManager(this,false);
        localActivityManager.dispatchCreate(savedInstanceState);
        tabHost.setup(localActivityManager);
        TabHost.TabSpec spec;
        spec=tabHost.newTabSpec("Home");
        spec.setIndicator(createTabIndicater(tabHost,"Home",R.drawable.home));
        spec.setContent(new Intent(getApplicationContext(), AdminHomeActivity.class));
        tabHost.addTab(spec);

        spec=tabHost.newTabSpec("New");
        spec.setIndicator(createTabIndicater(tabHost,"New",R.drawable.news));
        spec.setContent(new Intent(getApplicationContext(), AdminCheckNewProductsActivity.class));
        tabHost.addTab(spec);

        spec=tabHost.newTabSpec("Order");
        spec.setIndicator(createTabIndicater(tabHost,"Order",R.drawable.order));
        spec.setContent(new Intent(getApplicationContext(), AdminNewOrdersActivity.class));
        tabHost.addTab(spec);

        spec=tabHost.newTabSpec("More");
        spec.setIndicator(createTabIndicater(tabHost,"More",R.drawable.more));
        spec.setContent(new Intent(getApplicationContext(), AdminMoreActivity.class));
        tabHost.addTab(spec);


        settings = findViewById(R.id.settings);
        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu=new PopupMenu(AdminMainActivity.this,v);
                popupMenu.inflate(R.menu.home);
                popupMenu.show();
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        if(item.getItemId()==R.id.about){
                            startActivity(new Intent(getApplicationContext(), AboutActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                        }else if(item.getItemId()==R.id.logout){
                            Paper.book().destroy();
                            startActivity(new Intent(getApplicationContext(), MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK));
                        }
                        return true;
                    }
                });
            }
        });
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