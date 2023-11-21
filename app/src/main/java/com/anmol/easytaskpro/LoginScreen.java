package com.anmol.easytaskpro;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;

public class LoginScreen extends AppCompatActivity {

    EditText emailText, passwordText;
    Button loginButton;
    ProgressBar progressBar;
    TextView newSignup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_screen);

        emailText = findViewById(R.id.emailField);
        passwordText = findViewById(R.id.passwordField);
        loginButton = findViewById(R.id.loginButton);
        progressBar = findViewById(R.id.progressBar);
        newSignup = findViewById(R.id.newSignup);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUser();
            }
        });

        newSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginScreen.this, SignUpScreen.class));
            }
        });

    }

    void loginUser() {
        String email = emailText.getText().toString();
        String password = passwordText.getText().toString();

        boolean isValidated = validateData(email, password);

        if(!isValidated){
            return;
        }

        loginAccountInFirebase(email, password);
    }

    void loginAccountInFirebase(String email, String password){
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        changeInProgress(true);
        firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                changeInProgress(false);
                if(task.isSuccessful()){
                    if(firebaseAuth.getCurrentUser().isEmailVerified()){
                        startActivity(new Intent(LoginScreen.this, MainActivity.class));
                        finish();
                        Toast.makeText(getApplicationContext(), "Login successful", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        Toast.makeText(LoginScreen.this, "Email not verified, Please verify your email.", Toast.LENGTH_SHORT).show();
                    }
                }
                else{
                    if(task.getException() instanceof FirebaseAuthInvalidCredentialsException){
                        Toast.makeText(LoginScreen.this, "Invalid email or password", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        Toast.makeText(LoginScreen.this, task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    void changeInProgress(boolean inProgress){
        if(inProgress){
            progressBar.setVisibility(View.VISIBLE);
            loginButton.setVisibility(View.GONE);
        }
        else{
            progressBar.setVisibility(View.GONE);
            loginButton.setVisibility(View.VISIBLE);
        }
    }

    boolean validateData(String email, String password){

        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            emailText.setError("Email is invalid");
            return false;
        }
        if(password.length() < 6){
            passwordText.setError("Password must be greater than 6 characters");
            return false;
        }
        return true;
    }
}