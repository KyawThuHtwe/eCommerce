package com.cu.ecommerce.Admin.Fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.cu.ecommerce.Activities.MainActivity;
import com.cu.ecommerce.Admin.AdminCategoryActivity;
import com.cu.ecommerce.Admin.AdminCheckNewProductsActivity;
import com.cu.ecommerce.Admin.AdminNewOrdersActivity;
import com.cu.ecommerce.Admin.AdminProductActivity;
import com.cu.ecommerce.Admin.AdminSellersViewActivity;
import com.cu.ecommerce.Admin.AdminUsersViewActivity;
import com.cu.ecommerce.R;
import com.cu.ecommerce.Sellers.SellerCategoryActivity;

public class AdminHomeFragment extends Fragment {

    LinearLayout maintain,order,approve,category,add_new_product,seller,user;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_admin_home, container, false);
        maintain=view.findViewById(R.id.maintain);
        order=view.findViewById(R.id.order);
        approve=view.findViewById(R.id.approve);
        category=view.findViewById(R.id.category);
        add_new_product=view.findViewById(R.id.add_new_product);
        seller=view.findViewById(R.id.seller);
        user=view.findViewById(R.id.user);

        seller.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getContext(), AdminSellersViewActivity.class);
                startActivity(intent);
            }
        });

        user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getContext(), AdminUsersViewActivity.class);
                startActivity(intent);
            }
        });

        order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getContext(), AdminNewOrdersActivity.class);
                startActivity(intent);
            }
        });

        maintain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getContext(), AdminProductActivity.class);
                startActivity(intent);
            }
        });
        approve.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getContext(), AdminCheckNewProductsActivity.class);
                startActivity(intent);
            }
        });
        category.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getContext(), AdminCategoryActivity.class);
                startActivity(intent);
            }
        });
        add_new_product.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getContext(), SellerCategoryActivity.class);
                intent.putExtra("type","admin");
                startActivity(intent);
            }
        });
        return view;
    }
}