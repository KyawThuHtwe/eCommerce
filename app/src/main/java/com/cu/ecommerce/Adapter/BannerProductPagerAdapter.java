package com.cu.ecommerce.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.cu.ecommerce.Buyers.ProductDetailActivity;
import com.cu.ecommerce.Model.Product;
import com.cu.ecommerce.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

public class BannerProductPagerAdapter extends PagerAdapter {

    Context context;
    ArrayList<Product> products;
    int i;

    public BannerProductPagerAdapter(Context context, ArrayList<Product> products,int i) {
        this.context = context;
        this.products = products;
        this.i=i;
    }

    @Override
    public int getCount() {
        return products.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view==object;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        View view= LayoutInflater.from(context).inflate(R.layout.banner_product_layout,null);
        ImageView bannerImage=view.findViewById(R.id.bannerImage);
        Picasso.get().load(products.get(position).getImage()).error(R.drawable.online_store_header).into(bannerImage);
        bannerImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if(i==0){
                        Intent intent = new Intent(context, ProductDetailActivity.class);
                        intent.putExtra("agentID", products.get(position).getSid());
                        intent.putExtra("pid", products.get(position).getPid());
                        intent.putExtra("qty", "1");
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);
                    }
                }catch (Exception e){
                    Toast.makeText(context,e.getMessage(),Toast.LENGTH_SHORT).show();
                }
            }
        });
        container.addView(view);
        return view;
    }
}
