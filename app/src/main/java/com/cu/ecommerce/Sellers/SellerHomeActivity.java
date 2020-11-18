package com.cu.ecommerce.Sellers;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.cu.ecommerce.Adapter.BannerProductPagerAdapter;
import com.cu.ecommerce.Adapter.MaintainProductAdapter;
import com.cu.ecommerce.Model.Category;
import com.cu.ecommerce.Model.Product;
import com.cu.ecommerce.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;
import de.hdodenhof.circleimageview.CircleImageView;
import io.paperdb.Paper;

public class SellerHomeActivity extends AppCompatActivity {

    TabLayout tabLayout;
    ViewPager viewPager;
    private Handler sliderHandler=new Handler();
    ArrayList<Product> bannerProduct=new ArrayList<>();
    BannerProductPagerAdapter bannerMoviePagerAdapter;
    String category_choose="All";

    RecyclerView recyclerView,category_recyclerView;
    DatabaseReference unVerifyProductRef;
    FloatingActionButton add;
    ArrayList<Category> categories;
    ArrayList<Product> products;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_home);

        Paper.init(this);

        add = findViewById(R.id.add);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent intent = new Intent(getApplicationContext(), SellerCategoryActivity.class);
                    intent.putExtra("type", "seller");
                    startActivity(intent);
                    finish();
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

        unVerifyProductRef = FirebaseDatabase.getInstance().getReference().child("Products").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        //recyclerView.setNestedScrollingEnabled(false);

        viewPager = findViewById(R.id.banner_viewPager);
        tabLayout = findViewById(R.id.tabLayout);

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
        category_recyclerView=findViewById(R.id.category_recyclerView);
        category_recyclerView.setHasFixedSize(true);
        category_recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext(),LinearLayoutManager.HORIZONTAL,false));
        loadingCategory();

        loading(category_choose);
        bannerProductLoading(category_choose);
    }

    public void loadingCategory(){
        categories=new ArrayList<>();
        categories.clear();
        categories.add(new Category("1111","All","R.drawable.more_vert"));
        final DatabaseReference rootRef;
        rootRef= FirebaseDatabase.getInstance().getReference().child("Category");
        rootRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){

                    for(DataSnapshot snap:snapshot.getChildren()){
                        Category category=snap.getValue(Category.class);
                        categories.add(category);
                    }
                    CategoryAdapter categoryAdapter=new CategoryAdapter(getApplicationContext(),categories);
                    category_recyclerView.setAdapter(categoryAdapter);

                }else {
                    Toast.makeText(getApplicationContext(),"Not exist.",Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void loading(String category){

        unVerifyProductRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                products=new ArrayList<>();
                products.clear();
                if(snapshot.exists()){
                    for(DataSnapshot snap:snapshot.getChildren()){
                        Product product=snap.getValue(Product.class);
                        if(product.getProductState().equals("Approved") && category.toLowerCase().equals("all")){
                            products.add(product);
                        }else if(product.getProductState().equals("Approved") && product.getCategory().equals(category)){
                            products.add(product);
                        }
                    }
                    MaintainProductAdapter productAdapter=new MaintainProductAdapter(getApplicationContext(),products);
                    recyclerView.setAdapter(productAdapter);
                    productAdapter.notifyDataSetChanged();

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
    public void bannerProductLoading(String category){
        unVerifyProductRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                bannerProduct=new ArrayList<>();
                bannerProduct.clear();
                if(snapshot.exists()){
                    for(DataSnapshot snap:snapshot.getChildren()){
                        Product product=snap.getValue(Product.class);
                        if(product.getProductState().equals("Approved")){
                            if(product.getCategory().toLowerCase().equals(category.toLowerCase())){
                                bannerProduct.add(product);
                            }else if(category.toLowerCase().equals("all")){
                                bannerProduct.add(product);
                            }
                        }

                    }
                    bannerMoviePagerAdapter=new BannerProductPagerAdapter(getApplicationContext(),bannerProduct,1);
                    viewPager.setAdapter(bannerMoviePagerAdapter);

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

    private void deleteProduct(String pid) {
        unVerifyProductRef.child(pid)
                .removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(getApplicationContext(),"Delete this Product Successfully",Toast.LENGTH_SHORT).show();
                        }

                    }
                });
    }
    public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {

        Context context;
        ArrayList<Category> categories;
        int selected_position = 0;

        public CategoryAdapter(Context context, ArrayList<Category> categories) {
            this.context = context;
            this.categories = categories;
        }

        @NonNull
        @Override
        public CategoryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.category_item_layout,parent,false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull CategoryAdapter.ViewHolder holder, int position) {

            try {

                if(!TextUtils.isEmpty(categories.get(position).getImage())){
                    if(categories.get(position).getImage().equals("R.drawable.more_vert")){
                        holder.category_profile.setImageResource(R.drawable.more_vert);
                        holder.category_profile.setPadding(30,30,30,30);
                    }else{
                        holder.category_profile.setPadding(0,0,0,0);
                        Picasso.get().load(categories.get(position).getImage()).into(holder.category_profile);
                    }
                }

                holder.category_name.setText(categories.get(position).getName());

                holder.category_name.setTextColor(selected_position == position ? Color.BLUE : Color.BLACK);


            }catch (Exception e){
                Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public int getItemCount() {
            return categories.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            public CircleImageView category_profile;
            public TextView category_name;
            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                this.category_profile=itemView.findViewById(R.id.category_profile);
                this.category_name=itemView.findViewById(R.id.category_name);
                itemView.setOnClickListener(this);
            }

            @Override
            public void onClick(View v) {
                if (getAdapterPosition() == RecyclerView.NO_POSITION) return;
                notifyItemChanged(selected_position);
                selected_position = getAdapterPosition();
                notifyItemChanged(selected_position);
                category_choose=categories.get(selected_position).getName();
                loading(category_choose);
                bannerProductLoading(category_choose);

            }
        }
    }

}