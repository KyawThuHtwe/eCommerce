package com.cu.ecommerce.Buyers;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;
import de.hdodenhof.circleimageview.CircleImageView;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cu.ecommerce.Adapter.BannerProductPagerAdapter;
import com.cu.ecommerce.Adapter.ProductAdapter;
import com.cu.ecommerce.Model.Category;
import com.cu.ecommerce.Model.Product;
import com.cu.ecommerce.Model.Seller;
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

public class AgentsActivity extends AppCompatActivity {


    RecyclerView recyclerView,category_recyclerView;
    FloatingActionButton cart;
    TabLayout tabLayout;
    ViewPager viewPager;
    private Handler sliderHandler=new Handler();
    ArrayList<Product> bannerProduct=new ArrayList<>();
    BannerProductPagerAdapter bannerMoviePagerAdapter;
    String agentID="",category_choose="All";

    CircleImageView agent_profile;
    TextView agent_name;
    ImageView back;

    TextView search;
    ImageView favorite,order;
    ArrayList<Category> categories;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agents);

        agentID=getIntent().getStringExtra("agentID");

        search=findViewById(R.id.search);
        favorite=findViewById(R.id.favorite);
        order=findViewById(R.id.order);
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getApplicationContext(), SearchProductActivity.class);
                intent.putExtra("sid",agentID);
                startActivity(intent);
            }
        });
        favorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getApplicationContext(), FavoriteActivity.class);
                intent.putExtra("sid",agentID);
                startActivity(intent);
            }
        });
        order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getApplicationContext(), OrderActivity.class);
                intent.putExtra("sid",agentID);
                startActivity(intent);

            }
        });

        agent_profile=findViewById(R.id.agent_profile);
        agent_name=findViewById(R.id.agent_name);
        back=findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        agentLoading(agentID);

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

        category_recyclerView=findViewById(R.id.category_recyclerView);
        category_recyclerView.setHasFixedSize(true);
        category_recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext(),LinearLayoutManager.HORIZONTAL,false));
        loadingCategory();

        recyclerView=findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(getApplicationContext(),2));

        loading(agentID,category_choose);
        bannerProductLoading(agentID,category_choose);

        cart=findViewById(R.id.cart);
        cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getApplicationContext(), CartActivity.class);
                intent.putExtra("agentID",agentID);
                startActivity(intent);
            }
        });
    }
    public void agentLoading(String agentID){
        final DatabaseReference rootRef;
        rootRef= FirebaseDatabase.getInstance().getReference().child("Sellers").child(agentID);
        rootRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    Seller seller=snapshot.getValue(Seller.class);
                    Picasso.get().load(seller.getImage()).into(agent_profile);
                    agent_name.setText(seller.getName());
                }else {
                    Toast.makeText(getApplicationContext(),"Not exist.",Toast.LENGTH_SHORT).show();
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

                        /*
                        try {
                            TabLayout.Tab firstTab = category_tabLayout.newTab();
                            //firstTab.setIcon(Drawable.createFromPath(category.getImage()));
                            firstTab.setText(category.getName());
                            category_tabLayout.addTab(firstTab);
                        }catch (Exception e){
                            Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
                        }

                         */
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

    public void loading(String agent, String category){
        final DatabaseReference rootRef;
        rootRef= FirebaseDatabase.getInstance().getReference().child("Products").child(agent);
        rootRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                bannerProduct=new ArrayList<>();
                bannerProduct.clear();
                if(snapshot.exists()){
                    for(DataSnapshot snap:snapshot.getChildren()){
                        Product product=snap.getValue(Product.class);
                        if(product.getSid().equals(agent) && product.getProductState().equals("Approved") && category.toLowerCase().equals("all")){
                            bannerProduct.add(product);
                        }else if(product.getSid().equals(agent) && product.getProductState().equals("Approved") && product.getCategory().equals(category.toLowerCase())){
                            bannerProduct.add(product);
                        }
                    }
                    ProductAdapter productAdapter=new ProductAdapter(getApplicationContext(),bannerProduct);
                    recyclerView.setAdapter(productAdapter);
                    productAdapter.notifyDataSetChanged();

                }else {
                    Toast.makeText(getApplicationContext(),"Not exist.",Toast.LENGTH_SHORT).show();
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
    public void bannerProductLoading(String agent, String category){
        final DatabaseReference rootRef;
        rootRef= FirebaseDatabase.getInstance().getReference().child("Products").child(agent);
        rootRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                bannerProduct=new ArrayList<>();
                bannerProduct.clear();
                if(snapshot.exists()){
                    for(DataSnapshot snap:snapshot.getChildren()){
                        Product product=snap.getValue(Product.class);
                        if(product.getSid().equals(agent) && product.getProductState().equals("Approved")){
                            if(product.getCategory().toLowerCase().equals(category.toLowerCase())){
                                bannerProduct.add(product);
                            }else if(category.toLowerCase().equals("all")){
                                bannerProduct.add(product);
                            }
                        }

                    }
                    bannerMoviePagerAdapter=new BannerProductPagerAdapter(getApplicationContext(),bannerProduct,0);
                    viewPager.setAdapter(bannerMoviePagerAdapter);

                }else {
                    Toast.makeText(getApplicationContext(),"Not exist.",Toast.LENGTH_SHORT).show();
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
                loading(agentID,category_choose);
                bannerProductLoading(agentID,category_choose);

            }
        }
    }
}