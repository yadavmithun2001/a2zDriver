package com.example.a2zdriver;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.shashank.sony.fancytoastlib.FancyToast;

import java.util.Map;

public class Login extends AppCompatActivity {

    CardView login;
    FirebaseAuth auth;
    FirebaseDatabase firebaseDatabase;
    LinearLayout ltprogress;
    EditText editUserid,editPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        auth = FirebaseAuth.getInstance();

        if(auth.getCurrentUser() != null){
            Intent intent = new Intent(Login.this, StartCollection.class);
            startActivity(intent);
            finish();
        }

        editUserid = findViewById(R.id.editTextUserID);
        editPassword = findViewById(R.id.editTextPassword);

        firebaseDatabase = FirebaseDatabase.getInstance();
        ltprogress = findViewById(R.id.ltprogress);


        login = findViewById(R.id.login);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(editUserid.getText().toString().isEmpty()){
                    FancyToast.makeText(Login.this,"User can't be Empty", FancyToast.LENGTH_SHORT,FancyToast.ERROR,false).show();
                }else if(editUserid.getText().toString().length() < 10){
                    FancyToast.makeText(Login.this,"Please enter a valid USER ID", FancyToast.LENGTH_SHORT,FancyToast.ERROR,false).show();
                }else if(!editUserid.getText().toString().startsWith("DR")){
                    FancyToast.makeText(Login.this,"Invalid User ID", FancyToast.LENGTH_SHORT,FancyToast.ERROR,false).show();
                }
                else if(editPassword.getText().toString().isEmpty()){
                    FancyToast.makeText(Login.this,"Password can't be Empty", FancyToast.LENGTH_SHORT,FancyToast.ERROR,false).show();
                }else if(editPassword.getText().toString().length() < 6){
                    FancyToast.makeText(Login.this,"Password should be 6 characters length ", FancyToast.LENGTH_SHORT,FancyToast.WARNING,false).show();
                }else {
                    _login(editUserid.getText().toString(),editPassword.getText().toString());
                }
            }
        });

    }

    void _login(String userid,String password){
        ltprogress.setVisibility(View.VISIBLE);
        auth.signInWithEmailAndPassword(userid+"@gmail.com",password)
                .addOnCompleteListener(Login.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            FancyToast.makeText(Login.this,"Successfully logged in ", FancyToast.LENGTH_SHORT,FancyToast.SUCCESS,false).show();
                            Intent intent = new Intent(Login.this,StartCollection.class);
                            ltprogress.setVisibility(View.GONE);
                            startActivity(intent);
                            finish();

                        }else {
                            FancyToast.makeText(Login.this,"Wrong Credentials or user not found", FancyToast.LENGTH_SHORT,FancyToast.ERROR,false).show();
                            ltprogress.setVisibility(View.GONE);
                        }
                    }
                });
    }
}