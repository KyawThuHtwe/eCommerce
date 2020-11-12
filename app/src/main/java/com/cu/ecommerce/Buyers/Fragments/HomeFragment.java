package com.cu.ecommerce.Buyers.Fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.cu.ecommerce.Adapter.ProductAdapter;
import com.cu.ecommerce.Buyers.CartActivity;
import com.cu.ecommerce.Buyers.ProductDetailActivity;
import com.cu.ecommerce.Buyers.SearchProductActivity;
import com.cu.ecommerce.Model.Category;
import com.cu.ecommerce.Model.Product;
import com.cu.ecommerce.Adapter.BannerProductPagerAdapter;
import com.cu.ecommerce.R;
import com.cu.ecommerce.ViewHolder.ProductViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Locale;

public class HomeFragment extends Fragment {

    RecyclerView recyclerView;
    FloatingActionButton cart;
    TabLayout category_tabLayout,tabLayout;
    ViewPager viewPager;
    private Handler sliderHandler=new Handler();
    ArrayList<Product> bannerProduct=new ArrayList<>();
    BannerProductPagerAdapter bannerMoviePagerAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_home, container, false);

        viewPager=view.findViewById(R.id.banner_viewPager);
        tabLayout=view.findViewById(R.id.tabLayout);

        bannerProductLoading();

        sliderHandler.postDelayed(sliderRunnable, 3000);
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                sliderHandler.removeCallbacks(sliderRunnable);
                sliderHandler.postDelayed(sliderRunnable, 3000);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        tabLayout.setupWithViewPager(viewPager);

        category_tabLayout=view.findViewById(R.id.simpleTabLayout);
        category_tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);

        TabLayout.Tab firstTab = category_tabLayout.newTab();
        firstTab.setText("All");
        category_tabLayout.addTab(firstTab);

        loadingCategory();

        category_tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                Toast.makeText(getContext(), tab.getText().toString(), Toast.LENGTH_SHORT).show();
                loading(tab.getText().toString());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        recyclerView=view.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        loading("All");
        cart=view.findViewById(R.id.cart);
        cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), CartActivity.class));
            }
        });
        return view;
    }
    public void loadingCategory(){
        final DatabaseReference rootRef;
        rootRef= FirebaseDatabase.getInstance().getReference().child("Category");
        rootRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                bannerProduct=new ArrayList<>();
                bannerProduct.clear();
                if(snapshot.exists()){
                    for(DataSnapshot snap:snapshot.getChildren()){
                        Category category=snap.getValue(Category.class);
                        TabLayout.Tab firstTab= category_tabLayout.newTab();
                        firstTab.setText(category.getName());
                        category_tabLayout.addTab(firstTab);
                    }

                }else {
                    Toast.makeText(getContext(),"Not exist.",Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void loading(String category){
        final DatabaseReference rootRef;
        rootRef= FirebaseDatabase.getInstance().getReference().child("Products");
        rootRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                bannerProduct=new ArrayList<>();
                bannerProduct.clear();
                if(snapshot.exists()){
                    for(DataSnapshot snap:snapshot.getChildren()){
                        Product product=snap.getValue(Product.class);
                        if(category.toLowerCase().equals("all")){
                            bannerProduct.add(product);
                        }else if(product.getCategory().equals(category.toLowerCase())){
                            bannerProduct.add(product);
                        }
                    }
                    ProductAdapter productAdapter=new ProductAdapter(getContext(),bannerProduct);
                    recyclerView.setAdapter(productAdapter);

                }else {
                    Toast.makeText(getContext(),"Not exist.",Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private Runnable sliderRunnable=new Runnable() {
        @Override
        public void run() {
            if(viewPager.getCurrentItem()==bannerProduct.size()-1){
                viewPager.setCurrentItem(0);
            }else{
                viewPager.setCurrentItem(viewPager.getCurrentItem()+1);
            }
        }
    };
    public void bannerProductLoading(){
        final DatabaseReference rootRef;
        rootRef= FirebaseDatabase.getInstance().getReference().child("Products");
        rootRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                bannerProduct=new ArrayList<>();
                bannerProduct.clear();
                if(snapshot.exists()){
                    for(DataSnapshot snap:snapshot.getChildren()){
                        Product product=snap.getValue(Product.class);
                        bannerProduct.add(product);

                    }
                    bannerMoviePagerAdapter=new BannerProductPagerAdapter(getContext(),bannerProduct);
                    viewPager.setAdapter(bannerMoviePagerAdapter);

                }else {
                    Toast.makeText(getContext(),"Not exist.",Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    @Override
    public void onStop() {
        super.onStop();
        sliderHandler.removeCallbacks(sliderRunnable);
    }
}