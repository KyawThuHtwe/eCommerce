package com.cu.ecommerce.Sellers;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.cu.ecommerce.Activities.MainActivity;
import com.cu.ecommerce.R;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;
import de.hdodenhof.circleimageview.CircleImageView;
import io.paperdb.Paper;

public class SellerHomeActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_home);

        Paper.init(this);

        final DrawerLayout drawerLayout=findViewById(R.id.drawerLayout);
        findViewById(R.id.imageMenu).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });
        drawerLayout.close();
        NavigationView navigationView=findViewById(R.id.navigationView);
        navigationView.setItemIconTintList(null);
        NavController navController= Navigation.findNavController(this,R.id.navHostFragment);
        NavigationUI.setupWithNavController(navigationView,navController);
        final TextView textTitle=findViewById(R.id.textTitle);
        navController.addOnDestinationChangedListener(new NavController.OnDestinationChangedListener() {
            @Override
            public void onDestinationChanged(@NonNull NavController controller, @NonNull NavDestination destination, @Nullable Bundle arguments) {
                textTitle.setText(destination.getLabel());
            }
        });

        View headerView=navigationView.getHeaderView(0);
        TextView seller_name=headerView.findViewById(R.id.username);
        CircleImageView profileImage=headerView.findViewById(R.id.profileImage);
        String sID= FirebaseAuth.getInstance().getCurrentUser().getUid();
        final DatabaseReference reference= FirebaseDatabase.getInstance().getReference()
                .child("Sellers")
                .child(sID);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.exists()){
                    String name_value=snapshot.child("name").getValue().toString();
                    if(snapshot.child("image").exists()){
                        String image_url=snapshot.child("image").getValue().toString();
                        Picasso.get().load(image_url).into(profileImage);
                    }
                    seller_name.setText(name_value);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    /*
    private void uploadImage() {
        final ProgressDialog progressDialog=new ProgressDialog(this);
        progressDialog.setTitle("Update Profile");
        progressDialog.setMessage("Please wait, while we are updating your account information");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
        final String sID= FirebaseAuth.getInstance().getCurrentUser().getUid();
        if(imageUri!=null){
            final StorageReference fileRef=storageProfileReference.child(sID+".jpg");
            UploadTask uploadTask=fileRef.putFile(imageUri);
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getApplicationContext(),"Error : "+e.toString(),Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }
            }).addOnSuccessListener(new OnSuccessListener() {
                @Override
                public void onSuccess(Object o) {
                    Toast.makeText(getApplicationContext(),"Profile Image Uploaded Successfully...",Toast.LENGTH_SHORT).show();
                    Task<Uri> urlTask=uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                        @Override
                        public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                            if(!task.isSuccessful()){
                                throw task.getException();
                            }
                            return fileRef.getDownloadUrl();
                        }
                    }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if(task.isSuccessful()){
                                Uri downloadUri=task.getResult();
                                myUri=downloadUri.toString();
                                String sID= FirebaseAuth.getInstance().getCurrentUser().getUid();
                                DatabaseReference reference=FirebaseDatabase.getInstance().getReference().child("Sellers");
                                HashMap<String,Object> sellerMap=new HashMap<>();
                                sellerMap.put("image",myUri);
                                reference.child(sID).updateChildren(sellerMap)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if(task.isSuccessful()){
                                                    progressDialog.dismiss();
                                                    Toast.makeText(getApplicationContext(),"Profile Info update successfully",Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });

                            }else {
                                progressDialog.dismiss();
                                Toast.makeText(getApplicationContext(),"Error",Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            });
        }
        else {
            progressDialog.dismiss();
            Toast.makeText(getApplicationContext(),"Image is not selected.",Toast.LENGTH_SHORT).show();
        }
    }

     */

    public void logout(MenuItem item) {
        FirebaseAuth firebaseAuth=FirebaseAuth.getInstance();
        firebaseAuth.signOut();
        startActivity(new Intent(getApplicationContext(), MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK));
    }

    public void setting(MenuItem item) {
        startActivity(new Intent(getApplicationContext(), SellerSettingsActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
    }
}