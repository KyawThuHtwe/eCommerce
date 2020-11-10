package com.cu.ecommerce.Sellers;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import de.hdodenhof.circleimageview.CircleImageView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cu.ecommerce.Activities.MainActivity;
import com.cu.ecommerce.Buyers.HomeActivity;
import com.cu.ecommerce.Buyers.SettingActivity;
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
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import java.util.HashMap;

public class SellerSettingsActivity extends AppCompatActivity {

    CircleImageView profile_image;
    EditText name,phone,address;
    ImageView back;
    TextView update,image_change;
    Uri imageUri;
    String myUri="";
    String checker="";
    StorageReference storageProfileReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_settings);

        storageProfileReference= FirebaseStorage.getInstance().getReference().child("Seller pictures");

        back=findViewById(R.id.back);
        update=findViewById(R.id.update);
        profile_image=findViewById(R.id.setting_profile_image);
        image_change=findViewById(R.id.setting_image_change);
        name=findViewById(R.id.setting_full_name);
        phone=findViewById(R.id.setting_phone_number);
        address=findViewById(R.id.setting_address);

        userInfoDisplay(profile_image,name,phone,address);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (checker.equals("clicked")) {
                        userInfoSave();
                    } else {
                        updateOnlyUserInfo();
                    }
                }catch (Exception e){
                    Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
                }
            }
        });
        image_change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checker="clicked";
                CropImage.activity(imageUri)
                        .setAspectRatio(1,1)
                        .start(SellerSettingsActivity.this);

            }
        });
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK && data!=null) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            imageUri= result.getUri();
            profile_image.setImageURI(imageUri);
        }else {
            Toast.makeText(getApplicationContext(),"Error, try again",Toast.LENGTH_SHORT).show();
            startActivity(new Intent(getApplicationContext(),SettingActivity.class));
            finish();
        }
    }
    private void userInfoSave() {
        if(TextUtils.isEmpty(name.getText().toString())){
            Toast.makeText(getApplicationContext(),"Name write",Toast.LENGTH_SHORT).show();
        }else if(TextUtils.isEmpty(address.getText().toString())){
            Toast.makeText(getApplicationContext(),"Address write",Toast.LENGTH_SHORT).show();
        }else if(TextUtils.isEmpty(phone.getText().toString())){
            Toast.makeText(getApplicationContext(),"Phone write",Toast.LENGTH_SHORT).show();
        }else if(checker.equals("clicked")){
            uploadImage();
        }
    }
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
                                HashMap<String,Object> userMap=new HashMap<>();
                                userMap.put("name",name.getText().toString());
                                userMap.put("address",address.getText().toString());
                                userMap.put("phoneOrder",phone.getText().toString());
                                userMap.put("image",myUri);
                                reference.child(sID).updateChildren(userMap);

                                progressDialog.dismiss();
                                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                                Toast.makeText(getApplicationContext(),"Profile Info update successfully",Toast.LENGTH_SHORT).show();
                                finish();
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
    private void updateOnlyUserInfo() {
        String sID= FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference().child("Sellers");
        HashMap<String,Object> userMap=new HashMap<>();
        userMap.put("name",name.getText().toString());
        userMap.put("address",address.getText().toString());
        userMap.put("phoneOrder",phone.getText().toString());
        reference.child(sID).updateChildren(userMap);

        startActivity(new Intent(getApplicationContext(), SellerHomeActivity.class));
        Toast.makeText(getApplicationContext(),"Profile Info update successfully",Toast.LENGTH_SHORT).show();
        finish();
    }
    private void userInfoDisplay(CircleImageView profile_image, EditText name, EditText phone, EditText address) {
        String sID= FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference userRef= FirebaseDatabase.getInstance().getReference().child("Sellers").child(sID);
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    if(snapshot.child("image").exists()){
                        String image=snapshot.child("image").getValue().toString();
                        String name_value=snapshot.child("name").getValue().toString();
                        String phone_value=snapshot.child("phone").getValue().toString();
                        String address_value=snapshot.child("address").getValue().toString();

                        Picasso.get().load(image).into(profile_image);
                        name.setText(name_value);
                        phone.setText(phone_value);
                        address.setText(address_value);
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}