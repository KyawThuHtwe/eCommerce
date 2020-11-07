package com.cu.ecommerce.Sellers;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.cu.ecommerce.R;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class SellerAdminAddNewProductActivity extends AppCompatActivity {

    public String category,name,description,price,saveCurrentDate,saveCurrentTime;
    ImageView addProductImage;
    EditText inputProductName,inputProductDescription,inputProductPrice;
    Button addProduct;
    private static final int GalleryPick=1;
    private Uri imageUri;
    private String productRandomKey,downloadImageUrl;
    StorageReference productImageRef;
    DatabaseReference productRef,sellerRef;
    ProgressDialog loadingBar;

    String sName,sAddress,sEmail,sPhone,sID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_add_new_product);
        addProductImage=findViewById(R.id.image);
        inputProductName=findViewById(R.id.name);
        inputProductDescription=findViewById(R.id.description);
        inputProductPrice=findViewById(R.id.price);
        addProduct=findViewById(R.id.addProduct);

        category=getIntent().getExtras().get("category").toString();
        loadingBar=new ProgressDialog(this);

        productImageRef= FirebaseStorage.getInstance().getReference().child("Product Images");
        productRef=FirebaseDatabase.getInstance().getReference().child("Products");
        sellerRef=FirebaseDatabase.getInstance().getReference().child("Sellers");


        addProductImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });

        addProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateProductData();
            }
        });

        sellerRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        if(snapshot.exists()){
                            sName=snapshot.child("name").getValue().toString();
                            sAddress=snapshot.child("address").getValue().toString();
                            sPhone=snapshot.child("phone").getValue().toString();
                            sEmail=snapshot.child("email").getValue().toString();
                            sID=snapshot.child("sid").getValue().toString();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

    }

    private void validateProductData() {
        name=inputProductName.getText().toString();
        description=inputProductDescription.getText().toString();
        price=inputProductPrice.getText().toString();
        if(imageUri==null){
            Toast.makeText(getApplicationContext(),"Product Image is mandatory",Toast.LENGTH_SHORT).show();
        }else if(TextUtils.isEmpty(name)){
            Toast.makeText(getApplicationContext(),"Please write your name",Toast.LENGTH_SHORT).show();
        }else if(TextUtils.isEmpty(description)){
            Toast.makeText(getApplicationContext(),"Please write your product description",Toast.LENGTH_SHORT).show();
        }else if(TextUtils.isEmpty(price)){
            Toast.makeText(getApplicationContext(),"Please write product price",Toast.LENGTH_SHORT).show();
        }else {

            storeProductInformation();
        }
    }

    private void storeProductInformation() {

        loadingBar.setTitle("Add New Product");
        loadingBar.setMessage("Dear Admin, please wait while we are adding the new product.");
        loadingBar.setCanceledOnTouchOutside(false);
        loadingBar.show();

        Calendar calendar=Calendar.getInstance();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat currentDate=new SimpleDateFormat("MMM dd, yyyy");
        saveCurrentDate=currentDate.format(calendar.getTime());

        @SuppressLint("SimpleDateFormat") SimpleDateFormat currentTime=new SimpleDateFormat("HH:mm:ss a");
        saveCurrentTime=currentTime.format(calendar.getTime());

        productRandomKey=saveCurrentDate+" "+saveCurrentTime;
        StorageReference filePaths=productImageRef.child(imageUri.getLastPathSegment()+productRandomKey);
        final UploadTask uploadTask=filePaths.putFile(imageUri);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(),"Error : "+e.toString(),Toast.LENGTH_SHORT).show();
                loadingBar.dismiss();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(getApplicationContext(),"Product Image Uploaded Successfully...",Toast.LENGTH_SHORT).show();
                Task<Uri> urlTask=uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        if(!task.isSuccessful()){
                            throw task.getException();
                        }
                        return filePaths.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if(task.isSuccessful()){
                            downloadImageUrl=task.getResult().toString();
                            Toast.makeText(getApplicationContext(),"got the Product Image Url Successfully..."+task.toString(),Toast.LENGTH_SHORT).show();
                            saveProductInfoToDatabase();
                        }

                    }
                });
            }
        });

    }

    private void saveProductInfoToDatabase() {
        HashMap<String,Object> productMap=new HashMap<>();
        productMap.put("pid",productRandomKey);
        productMap.put("image",downloadImageUrl);
        productMap.put("name",name);
        productMap.put("description",description);
        productMap.put("price",price);
        productMap.put("date",saveCurrentDate);
        productMap.put("time",saveCurrentTime);

        productMap.put("sellerName",sName);
        productMap.put("sellerAddress",sAddress);
        productMap.put("sellerEmail",sEmail);
        productMap.put("sellerPhone",sPhone);
        productMap.put("sid",sID);
        productMap.put("productState","Not Approved");

        productRef.child(productRandomKey).updateChildren(productMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    startActivity(new Intent(getApplicationContext(), SellerHomeActivity.class));
                    loadingBar.dismiss();
                    Toast.makeText(getApplicationContext(),"Product is added successfully...",Toast.LENGTH_SHORT).show();
                }else {
                    loadingBar.dismiss();
                    Toast.makeText(getApplicationContext(),"Error : "+task.getException().toString(),Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void openGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), GalleryPick);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == GalleryPick && resultCode == RESULT_OK
                && data != null && data.getData() != null )
        {
            imageUri = data.getData();
            addProductImage.setImageURI(imageUri);
        }
    }
    private void sellerInformation(){

    }
}