package com.cu.ecommerce.Admin;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
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
import com.cu.ecommerce.Adapter.ProductAdapter;
import com.cu.ecommerce.Model.Category;
import com.cu.ecommerce.Model.Product;
import com.cu.ecommerce.Prevalent.Prevalent;
import com.cu.ecommerce.R;
import com.cu.ecommerce.Sellers.SellerCategoryActivity;
import com.cu.ecommerce.Sellers.SellerMaintainProductActivity;
import com.cu.ecommerce.ViewHolder.ItemViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;
import de.hdodenhof.circleimageview.CircleImageView;
import io.paperdb.Paper;

public class AdminHomeActivity extends AppCompatActivity {

    TabLayout tabLayout;
    ViewPager viewPager;
    private Handler sliderHandler=new Handler();
    ArrayList<Product> bannerProduct=new ArrayList<>();

    BannerProductPagerAdapter bannerMoviePagerAdapter;
    String category_choose="All";

    RecyclerView recyclerView,category_recyclerView;
    DatabaseReference unVerifyProductRef;

    FloatingActionButton mAddProductFab, mAddCategoryFab,mAddFab;
    TextView addProductActionText, addCategoryActionText;
    Boolean isAllFabsVisible;

    ArrayList<Category> categories;
    ArrayList<Product> products;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_home);

        try {
            Paper.init(this);

            unVerifyProductRef = FirebaseDatabase.getInstance().getReference().child("Products").child(Prevalent.currentOnlineUser.getPhone());
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

            mAddFab = findViewById(R.id.add_fab);
            // FAB button
            mAddCategoryFab = findViewById(R.id.add_category_fab);
            mAddProductFab = findViewById(R.id.add_product_fab);

            addCategoryActionText = findViewById(R.id.add_category_action_text);
            addProductActionText = findViewById(R.id.add_product_action_text);

            mAddCategoryFab.setVisibility(View.GONE);
            mAddProductFab.setVisibility(View.GONE);
            addCategoryActionText.setVisibility(View.GONE);
            addProductActionText.setVisibility(View.GONE);
            isAllFabsVisible = false;
            //mAddFab.shrink();
            mAddFab.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (!isAllFabsVisible) {
                                mAddCategoryFab.show();
                                mAddProductFab.show();
                                addCategoryActionText.setVisibility(View.VISIBLE);
                                addProductActionText.setVisibility(View.VISIBLE);
                                //mAddFab.extend();
                                mAddFab.setImageResource(R.drawable.close);
                                isAllFabsVisible = true;
                            } else {
                                mAddCategoryFab.hide();
                                mAddProductFab.hide();
                                addCategoryActionText.setVisibility(View.GONE);
                                addProductActionText.setVisibility(View.GONE);

                                //mAddFab.shrink();
                                mAddFab.setImageResource(R.drawable.add);
                                isAllFabsVisible = false;
                            }
                        }
                    });

            mAddProductFab.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(getApplicationContext(), SellerCategoryActivity.class);
                            intent.putExtra("type", "admin");
                            startActivity(intent);
                        }
                    });

            mAddCategoryFab.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(getApplicationContext(), AdminCategoryActivity.class);
                            startActivity(intent);
                        }
                    });


        }catch (Exception e){
            Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
        }

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