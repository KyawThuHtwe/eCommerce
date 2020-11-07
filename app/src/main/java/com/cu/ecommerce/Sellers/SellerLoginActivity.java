package com.cu.ecommerce.Sellers;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.cu.ecommerce.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class SellerLoginActivity extends AppCompatActivity {

    EditText seller_email,seller_password;
    Button seller_login;
    ProgressDialog loadingBar;
    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_login);

        firebaseAuth= FirebaseAuth.getInstance();
        loadingBar=new ProgressDialog(this);

        seller_email=findViewById(R.id.seller_email);
        seller_password=findViewById(R.id.seller_password);
        seller_login=findViewById(R.id.seller_login);
        seller_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUser();
            }
        });
    }

    private void loginUser() {
        String email=seller_email.getText().toString();
        String password=seller_password.getText().toString();
        if(!email.equals("") && !password.equals("")){

            loadingBar.setTitle("Seller Account");
            loadingBar.setMessage("Please wait, while we are checking the credentials.");
            loadingBar.setCanceledOnTouchOutside(false);
            loadingBar.show();

            firebaseAuth.signInWithEmailAndPassword(email,password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                loadingBar.dismiss();
                                Toast.makeText(getApplicationContext(),"you are registered successfully",Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(getApplicationContext(), SellerHomeActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK));

                            }

                        }
                    });
        }else {
            Toast.makeText(getApplicationContext(),"Please complete the login form",Toast.LENGTH_SHORT).show();
        }
    }
}