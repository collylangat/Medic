package com.example.medical;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;


public class MainActivity extends AppCompatActivity {
    Button mLogin;
    EditText mEmail, mPassword;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mEmail = findViewById(R.id.log_email);
        mPassword = findViewById(R.id.log_password);
        mLogin = findViewById(R.id.btnLogin);
        mAuth = FirebaseAuth.getInstance();

        //login process
        mLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = mEmail.getText().toString().trim();
                String password = mPassword.getText().toString().trim();

                if (email.isEmpty()){
                    mEmail.setError("Email required");
                    mEmail.requestFocus();
                    return;
                }
                if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                    mEmail.setError("Email invalid");
                    mEmail.requestFocus();
                    return;
                }
                if (password.isEmpty()){
                    mPassword.setError("Password must be greater than 6");
                    mPassword.requestFocus();
                    return;
                }
                mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            FirebaseUser USER = FirebaseAuth.getInstance().getCurrentUser();

                            if (USER.isEmailVerified()){
                                //redirect
                                Toast.makeText(MainActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();


                                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                            }else{
                                USER.sendEmailVerification();
                                Toast.makeText(MainActivity.this, "Please check your email to verify email", Toast.LENGTH_SHORT).show();
                            }
                        }else{
                            Toast.makeText(MainActivity.this, "Check your credentials", Toast.LENGTH_SHORT).show();
                        }

                    }
                });
            }
        });
    }
    //registration form intent
    public void regForm(View view) { startActivity(new Intent(getApplicationContext(), RegistrationForm.class));}
}