package com.example.medical;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RegistrationForm extends AppCompatActivity {
    Button registrationButton;
    EditText  mFullName, mRegEmail, mPassword, mConfirmPassword;
    TextView mLoginTextView;
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration_form);

        mFullName = findViewById(R.id.reg_fullname);
        mRegEmail = findViewById(R.id.reg_email);
        mPassword = findViewById(R.id.reg_password);
        mConfirmPassword = findViewById(R.id.confirm_password);
        registrationButton = findViewById(R.id.btnRegister);
        mLoginTextView = findViewById(R.id.link_to_login);
        mAuth = FirebaseAuth.getInstance();


// start registration process
        registrationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String fullName = mFullName.getText().toString().trim();
                String email =  mRegEmail.getText().toString().trim();
                String password = mPassword.getText().toString().trim();
                String confirmPassword = mConfirmPassword.getText().toString().trim();

                //validate the inputs
                if(TextUtils.isEmpty(fullName)){
                    mFullName.setError("Fullname is Required");
                    return;}
                if(TextUtils.isEmpty(email)){
                    mRegEmail.setError("Email is Required");
                    return;}

                if(TextUtils.isEmpty(password)){
                    mPassword.setError("Password is Required");
                    return;}
                if(password.length ()<6){
                    mPassword.setError("Password must be greater or equal to 6 characters");
                    return;}
                if (!password.equals(confirmPassword)){
                    mConfirmPassword.setError("Password not matching");
                    return;
                }
                //end of validation
                //start mac address

                String macAddress = "";
                try {
                    List<NetworkInterface> all = Collections.list(NetworkInterface.getNetworkInterfaces());
                    for (NetworkInterface nif : all) {
                        if (!nif.getName().equalsIgnoreCase("wlan0")) continue;

                        byte[] macBytes = nif.getHardwareAddress();
                        if (macBytes == null) {
                            //return "";
                        }

                        StringBuilder res1 = new StringBuilder();
                        for (byte b : macBytes) {
                            res1.append(Integer.toHexString(b & 0xFF) + ":");
                        }

                        if (res1.length() > 0) {
                            res1.deleteCharAt(res1.length() - 1);
                        }
                        macAddress = res1.toString();
                    }
                }catch (Exception ex){
                    Toast.makeText(RegistrationForm.this, (CharSequence) ex, Toast.LENGTH_SHORT).show();

                }

                //end mac address
                //start firebase database

                String finalMacAddress = macAddress;
                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()){
                                    User user = new User(email, fullName);
                                    FirebaseDatabase.getInstance().getReference("User")
                                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                            .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()){
                                                Toast.makeText(RegistrationForm.this, "Registration successful", Toast.LENGTH_SHORT).show();

                                            }else{
                                                Toast.makeText(RegistrationForm.this, "Failed to register", Toast.LENGTH_SHORT).show();
                                                Toast.makeText(RegistrationForm.this, finalMacAddress, Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                }else {
                                    Toast.makeText(RegistrationForm.this, "Failed to be registered", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                //end firebase
            }
        });
//end of registration
// start of login intent
        mLoginTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
            }
        });
//end of login intent
    }
}