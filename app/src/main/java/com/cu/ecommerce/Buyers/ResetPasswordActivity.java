package com.cu.ecommerce.Buyers;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.cu.ecommerce.Activities.HomeActivity;
import com.cu.ecommerce.Activities.LoginActivity;
import com.cu.ecommerce.Prevalent.Prevalent;
import com.cu.ecommerce.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class ResetPasswordActivity extends AppCompatActivity {

    TextView pageTitle,titleQuestion;
    EditText phone,q1,q2;
    Button verify;
    String check="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);
        try {
            check = getIntent().getStringExtra("check");
            pageTitle = findViewById(R.id.pageTitle);
            phone = findViewById(R.id.find_phone_number);
            titleQuestion = findViewById(R.id.titleQuestion);
            q1 = findViewById(R.id.q1);
            q2 = findViewById(R.id.q2);
            verify = findViewById(R.id.verify);

            if(check.equals("login")){
                phone.setVisibility(View.VISIBLE);
                verify.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        verifyUser();
                    }
                });

            }else if(check.equals("settings")){
                phone.setVisibility(View.GONE);

                pageTitle.setText("Set Questions");
                titleQuestion.setText("Please set Answer the following security Questions?");
                verify.setText("Set");
                displayPreviousAnswers();
                verify.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        setAnswers();
                    }
                });
            }

        }catch (Exception e){
           Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
        }

    }

    private void verifyUser() {
        String phoneNumber=phone.getText().toString();
        String answer1=q1.getText().toString().toLowerCase();
        String answer2=q2.getText().toString().toLowerCase();

        if(!phoneNumber.equals("") && !answer1.equals("") && !answer2.equals("")){
            DatabaseReference reference= FirebaseDatabase.getInstance().getReference()
                    .child("Users")
                    .child(phoneNumber);
            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    if(snapshot.exists()){
                        String mPhone=snapshot.child("phone").getValue().toString();
                        if(phoneNumber.equals(mPhone)){

                            if(snapshot.hasChild("Security Questions")){
                                String ans1=snapshot.child("Security Questions").child("answer1").getValue().toString();
                                String ans2=snapshot.child("Security Questions").child("answer2").getValue().toString();
                                if(!ans1.equals(answer1)){
                                    Toast.makeText(getApplicationContext(),"your 1st answer is wrong.", Toast.LENGTH_SHORT).show();
                                }else  if(!ans2.equals(answer2)){
                                    Toast.makeText(getApplicationContext(),"your 2nd answer is wrong.", Toast.LENGTH_SHORT).show();
                                }else{
                                    AlertDialog.Builder builder=new AlertDialog.Builder(ResetPasswordActivity.this);
                                    builder.setTitle("New Password");

                                    final EditText newPassword=new EditText(ResetPasswordActivity.this);
                                    newPassword.setHint("Write Password here...");
                                    builder.setView(newPassword);
                                    builder.setPositiveButton("Change", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            if(!newPassword.getText().toString().equals("")){
                                                reference.child("password")
                                                        .setValue(newPassword.getText().toString())
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if (task.isSuccessful()) {
                                                                    Toast.makeText(getApplicationContext(), "Password changed successfully.", Toast.LENGTH_SHORT).show();
                                                                    startActivity(new Intent(getApplicationContext(), LoginActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK));
                                                                }
                                                            }
                                                        });
                                            }
                                        }
                                    });
                                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.cancel();
                                        }
                                    });
                                    builder.show();

                                }
                            }
                        }else {
                            Toast.makeText(getApplicationContext(),"This user phone number not exists.", Toast.LENGTH_SHORT).show();
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }else {
            Toast.makeText(getApplicationContext(),"Please complete form",Toast.LENGTH_SHORT).show();
        }

    }

    public void setAnswers(){
        String ans1=q1.getText().toString().toLowerCase();
        String ans2=q2.getText().toString().toLowerCase();
        if(ans1.equals("") && ans2.equals("")){
            Toast.makeText(getApplicationContext(),"Please answer both questions",Toast.LENGTH_SHORT).show();
        }else {
            DatabaseReference reference= FirebaseDatabase.getInstance().getReference()
                    .child("Users")
                    .child(Prevalent.currentOnlineUser.getPhone());
            HashMap<String,Object> userDataMap=new HashMap<>();
            userDataMap.put("answer1",ans1);
            userDataMap.put("answer2",ans2);
            reference.child("Security Questions").updateChildren(userDataMap)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(getApplicationContext(),"you have set security questions successfully.",Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                            }
                        }
                    });
        }
    }
    private void displayPreviousAnswers(){
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference()
                .child("Users")
                .child(Prevalent.currentOnlineUser.getPhone());
        reference.child("Security Questions").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.exists()){
                    String ans1=snapshot.child("answer1").getValue().toString();
                    String ans2=snapshot.child("answer2").getValue().toString();
                    q1.setText(ans1);
                    q2.setText(ans2);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}