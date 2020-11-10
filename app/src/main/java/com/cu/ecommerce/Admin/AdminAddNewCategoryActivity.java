package com.cu.ecommerce.Admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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

import com.cu.ecommerce.Prevalent.Prevalent;
import com.cu.ecommerce.R;
import com.cu.ecommerce.Sellers.SellerHomeActivity;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class AdminAddNewCategoryActivity extends AppCompatActivity {

    String checker="";
    private Uri imageUri;
    private String productRandomKey,downloadImageUrl;
    ImageView category_image,back;
    Button upload,add_new_category;
    EditText category_name;
    String saveCurrentDate,saveCurrentTime;
    StorageReference categoryImageRef;
    DatabaseReference categoryRef;
    ProgressDialog loadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_add_new_category);
        back=findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        loadingBar=new ProgressDialog(this);

        categoryImageRef= FirebaseStorage.getInstance().getReference().child("Category Images");
        categoryRef=FirebaseDatabase.getInstance().getReference().child("Category");

        category_name=findViewById(R.id.category);
        upload=findViewById(R.id.uploadImage);
        add_new_category=findViewById(R.id.add_new_category);
        category_image=findViewById(R.id.category_image);

        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    checker = "clicked";
                    CropImage.activity(imageUri)
                            .setAspectRatio(1, 1)
                            .start(AdminAddNewCategoryActivity.this);
                }catch (Exception e){
                    Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
                }
            }
        });
        add_new_category.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (checker.equals("clicked")) {
                        addCategoryInformationSave();
                    }
                }catch (Exception e){
                    Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    private void addCategoryInformationSave() {
        if(TextUtils.isEmpty(category_name.getText().toString())){
            Toast.makeText(getApplicationContext(),"Category Name write",Toast.LENGTH_SHORT).show();
        }else if(checker.equals("clicked")){
            storeCategoryInformation();
        }
    }
    private void storeCategoryInformation() {

        loadingBar.setTitle("Add New Category");
        loadingBar.setMessage("Dear Admin, please wait while we are adding the new category.");
        loadingBar.setCanceledOnTouchOutside(false);
        loadingBar.show();

        Calendar calendar=Calendar.getInstance();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat currentDate=new SimpleDateFormat("MMM dd, yyyy");
        saveCurrentDate=currentDate.format(calendar.getTime());

        @SuppressLint("SimpleDateFormat") SimpleDateFormat currentTime=new SimpleDateFormat("HH:mm:ss a");
        saveCurrentTime=currentTime.format(calendar.getTime());

        productRandomKey=saveCurrentDate+" "+saveCurrentTime;
        StorageReference filePaths=categoryImageRef.child(imageUri.getLastPathSegment()+productRandomKey);
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
                            saveNewCategoryToDatabase();
                        }
                    }
                });
            }
        });
    }
    private void saveNewCategoryToDatabase() {
        HashMap<String,Object> categoryMap=new HashMap<>();
        categoryMap.put("cid",productRandomKey);
        categoryMap.put("image",downloadImageUrl);
        categoryMap.put("name",category_name.getText().toString());
        categoryMap.put("date",saveCurrentDate);
        categoryMap.put("time",saveCurrentTime);

        categoryRef.child(productRandomKey).updateChildren(categoryMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    startActivity(new Intent(getApplicationContext(), SellerHomeActivity.class));
                    loadingBar.dismiss();
                    finish();
                    Toast.makeText(getApplicationContext(),"Product is added successfully...",Toast.LENGTH_SHORT).show();
                }else {
                    loadingBar.dismiss();
                    Toast.makeText(getApplicationContext(),"Error : "+task.getException().toString(),Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK && data!=null) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            imageUri= result.getUri();
            category_image.setImageURI(imageUri);
        }else {
            Toast.makeText(getApplicationContext(),"Error, try again",Toast.LENGTH_SHORT).show();
        }
    }
}