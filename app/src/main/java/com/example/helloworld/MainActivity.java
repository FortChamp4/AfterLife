package com.example.helloworld;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Firebase;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    EditText editFirstName;
    EditText editLastName;
    EditText editEmail;
    EditText editPassword;
    Button registerButton;
    Button loginButton;
    String firstName;
    String lastName;
    String email;
    String password;
    FirebaseAuth mAuth;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();

        editFirstName = (EditText) findViewById(R.id.editFirstName);
        editLastName = (EditText) findViewById(R.id.editLastName);
        editEmail = (EditText) findViewById(R.id.editEmail);
        editPassword = (EditText) findViewById(R.id.editPassword);
        registerButton = (Button) findViewById(R.id.registerButton);
        loginButton = (Button) findViewById(R.id.loginButton);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        registerButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        progressBar.setVisibility(View.VISIBLE);
                        //Get data
                        firstName = String.valueOf(editFirstName.getText());
                        lastName = String.valueOf(editLastName.getText());
                        email = String.valueOf(editEmail.getText());
                        password = String.valueOf(editPassword.getText());

                        if(firstName.isEmpty()) {
                            Toast.makeText(getApplicationContext(), "Please Enter your First Name", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if(lastName.isEmpty()) {
                            Toast.makeText(getApplicationContext(), "Please Enter your Last Name", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if(email.isEmpty()) {
                            Toast.makeText(getApplicationContext(), "Please Enter your Email", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if(password.isEmpty()) {
                            Toast.makeText(getApplicationContext(), "Please Enter a Password", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if(password.length() < 6) {
                            Toast.makeText(getApplicationContext(), "Password should be least 6 Characters", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        mAuth.createUserWithEmailAndPassword(email, password)
                                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        progressBar.setVisibility(View.GONE);
                                        if (task.isSuccessful()) {
                                            Toast.makeText(MainActivity.this, "Registered Successfully!", Toast.LENGTH_SHORT).show();
                                            addUserInformation(firstName, lastName, email);
                                            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                                            startActivity(intent);
                                            finish();
                                        } else {
                                            Toast.makeText(MainActivity.this, "Registration Failed", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                    }
                }
        );

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void addUserInformation(String firstName, String lastName, String email) {
        HashMap<String, Object> userHashMap = new HashMap<>();
        userHashMap.put("First Name", firstName);
        userHashMap.put("Last Name", lastName);
        userHashMap.put("Email", email);

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference("Users");

        String id = databaseReference.push().getKey();
        userHashMap.put("id", id);

        databaseReference.child(id).setValue(userHashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                editFirstName.getText().clear();
                editLastName.getText().clear();
                editLastName.getText().clear();
                editPassword.getText().clear();
            }
        });

    }

}