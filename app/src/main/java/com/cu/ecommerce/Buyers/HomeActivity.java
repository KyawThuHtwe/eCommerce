package com.cu.ecommerce.Buyers;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.cu.ecommerce.Activities.AboutActivity;
import com.cu.ecommerce.Activities.MainActivity;
import com.cu.ecommerce.Adapter.AgentAdapter;
import com.cu.ecommerce.Adapter.BannerProductPagerAdapter;
import com.cu.ecommerce.Adapter.ProductAdapter;
import com.cu.ecommerce.Model.Category;
import com.cu.ecommerce.Model.Product;
import com.cu.ecommerce.Model.Seller;
import com.cu.ecommerce.Prevalent.Prevalent;
import com.cu.ecommerce.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;
import de.hdodenhof.circleimageview.CircleImageView;
import io.paperdb.Paper;

public class HomeActivity extends AppCompatActivity {
    RecyclerView recyclerView,agent_recyclerView,category_recyclerView;
    FloatingActionButton cart;
    TabLayout tabLayout;
    ViewPager viewPager;
    private Handler sliderHandler=new Handler();
    ArrayList<Product> bannerProduct=new ArrayList<>();
    ArrayList<Seller> sellers=new ArrayList<>();
    BannerProductPagerAdapter bannerMoviePagerAdapter;
    String agent_choose="09",category_choose="All";

    CircleImageView account_profile;
    TextView search;
    ImageView favorite,order,settings;
    ArrayList<Category> categories;
    ArrayList<Product> products;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Paper.init(this);

        account_profile=findViewById(R.id.account_profile);
        search=findViewById(R.id.search);
        favorite=findViewById(R.id.favorite);
        order=findViewById(R.id.order);

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getApplicationContext(), SearchProductActivity.class);
                intent.putExtra("sid","09");
                startActivity(intent);
            }
        });
        favorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getApplicationContext(), FavoriteActivity.class);
                intent.putExtra("sid","09");
                startActivity(intent);
            }
        });
        order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getApplicationContext(), OrderActivity.class);
                intent.putExtra("sid","09");
                startActivity(intent);

            }
        });

        account_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), SettingActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
            }
        });

        viewPager=findViewById(R.id.banner_viewPager);
        tabLayout=findViewById(R.id.tabLayout);

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

        agent_recyclerView=findViewById(R.id.agent_recyclerView);
        agent_recyclerView.setHasFixedSize(true);
        agent_recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext(),LinearLayoutManager.HORIZONTAL,false));

        category_recyclerView=findViewById(R.id.category_recyclerView);
        category_recyclerView.setHasFixedSize(true);
        category_recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext(),LinearLayoutManager.HORIZONTAL,false));

        recyclerView=findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(getApplicationContext(),2));

       // agentLoading();
        //loading(category_choose);
        //bannerProductLoading(category_choose);

        cart=findViewById(R.id.cart);
        cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getApplicationContext(), CartActivity.class);
                intent.putExtra("agentID",agent_choose);
                startActivity(intent);
            }
        });

        settings=findViewById(R.id.settings);
        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu=new PopupMenu(HomeActivity.this,v);
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
        //loadingAccount();
    }

    @Override
    protected void onStart() {
        super.onStart();

        loadingCategory();
        loadingAccount();
        agentLoading();
        loading(category_choose);
        bannerProductLoading(category_choose);

    }

    public void agentLoading(){
        final DatabaseReference rootRef;
        rootRef= FirebaseDatabase.getInstance().getReference().child("Sellers");
        rootRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    sellers.clear();
                    for(DataSnapshot snap:snapshot.getChildren()){
                        Seller seller=snap.getValue(Seller.class);
                        sellers.add(seller);
                    }
                    AgentAdapter agentAdapter=new AgentAdapter(getApplicationContext(),sellers);
                    agent_recyclerView.setAdapter(agentAdapter);
                    agentAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void loadingAccount(){
        final DatabaseReference rootRef;
        rootRef= FirebaseDatabase.getInstance().getReference().child("Users").child(Prevalent.currentOnlineUser.getPhone());
        rootRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    if(!snapshot.child("image").getValue().equals("default")){
                        Picasso.get().load(snapshot.child("image").getValue().toString()).into(account_profile);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
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

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void loading(String category){
        products=new ArrayList<>();
        products.clear();
        final DatabaseReference rootRef;
        rootRef= FirebaseDatabase.getInstance().getReference().child("Products").child("09");
        rootRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.exists()){
                    for(DataSnapshot snap:snapshot.getChildren()){
                        Product product=snap.getValue(Product.class);
                        assert product != null;
                        if(category.equals("All")){
                            if(product.getProductState().equals("Approved")) {
                                products.add(product);
                            }

                        }else {
                            if(product.getProductState().equals("Approved") && product.getCategory().equals(category)) {
                                products.add(product);
                            }
                        }

                    }
                    ProductAdapter productAdapter=new ProductAdapter(getApplicationContext(),products);
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
        final DatabaseReference rootRef;
        rootRef= FirebaseDatabase.getInstance().getReference().child("Products").child("09");
        rootRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                bannerProduct=new ArrayList<>();
                bannerProduct.clear();
                if(snapshot.exists()){
                    for(DataSnapshot snap:snapshot.getChildren()){
                        Product product=snap.getValue(Product.class);
                        if(product.getProductState().equals("Approved")){
                            if(product.getCategory().equals(category)){
                                bannerProduct.add(product);
                            }else if(category.equals("All")){
                                bannerProduct.add(product);
                            }
                        }

                    }
                    bannerMoviePagerAdapter=new BannerProductPagerAdapter(getApplicationContext(),bannerProduct,0);
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
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.category_item_layout,parent,false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

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