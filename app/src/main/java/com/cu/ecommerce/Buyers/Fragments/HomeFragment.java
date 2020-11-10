package com.cu.ecommerce.Buyers.Fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.cu.ecommerce.Buyers.CartActivity;
import com.cu.ecommerce.Buyers.ProductDetailActivity;
import com.cu.ecommerce.Buyers.SearchProductActivity;
import com.cu.ecommerce.Model.Product;
import com.cu.ecommerce.R;
import com.cu.ecommerce.ViewHolder.ProductViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

public class HomeFragment extends Fragment {

    RecyclerView recyclerView;
    DatabaseReference databaseReference;
    FloatingActionButton cart;
    TabLayout category_tabLayout;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_home, container, false);
        category_tabLayout=view.findViewById(R.id.simpleTabLayout);
        category_tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);

        TabLayout.Tab firstTab = category_tabLayout.newTab();
        firstTab.setText("All");
        category_tabLayout.addTab(firstTab);
        TabLayout.Tab secondTab = category_tabLayout.newTab();
        secondTab.setText("T-Shirt");
        category_tabLayout.addTab(secondTab);
        TabLayout.Tab thirdTab = category_tabLayout.newTab();
        thirdTab.setText("Shirt");
        category_tabLayout.addTab(thirdTab);

        databaseReference= FirebaseDatabase.getInstance().getReference().child("Products");

        recyclerView=view.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
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
        loading("All");

        cart=view.findViewById(R.id.cart);
        cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), CartActivity.class));
            }
        });

        ImageView search=view.findViewById(R.id.search);
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), SearchProductActivity.class));
            }
        });
        return view;
    }
    public void loading(String category){
        FirebaseRecyclerOptions<Product> options=new FirebaseRecyclerOptions
                .Builder<Product>()
                .setQuery(databaseReference.orderByChild("productState").equalTo("Approved"),Product.class)
                .build();
        FirebaseRecyclerAdapter<Product, ProductViewHolder> adapter=
                new FirebaseRecyclerAdapter<Product, ProductViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull ProductViewHolder holder, int i, @NonNull Product product) {
                        holder.name.setText(product.getName());
                        holder.price.setText(product.getPrice());
                        Picasso.get().load(product.getImage()).placeholder(R.drawable.ic_launcher_foreground).error(R.drawable.ic_launcher_background).into(holder.image);
                        holder.description.setText(product.getDescription());

                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                Intent intent=new Intent(getContext(), ProductDetailActivity.class);
                                intent.putExtra("pid",product.getPid());
                                startActivity(intent);
                            }
                        });
                    }

                    @NonNull
                    @Override
                    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view1=LayoutInflater.from(parent.getContext()).inflate(R.layout.product_item_layout,parent,false);
                        return new ProductViewHolder(view1);
                    }
                };
        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }
}