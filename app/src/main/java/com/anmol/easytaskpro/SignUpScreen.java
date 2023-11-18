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


public class SignUpScreen extends AppCompatActivity {

    EditText emailText, passwordText, confirmPasswordText;
    Button signUpButton;
    ProgressBar progressBar;
    TextView alreadySignin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_screen);

        emailText = findViewById(R.id.emailField);
        passwordText = findViewById(R.id.passwordField);
        confirmPasswordText = findViewById(R.id.confirmPasswordField);
        signUpButton = findViewById(R.id.signupButton);
        progressBar = findViewById(R.id.progressBar);
        alreadySignin = findViewById(R.id.alreadySignin);

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createAccount();
            }
        });

        alreadySignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignUpScreen.this, LoginScreen.class));
                finish();
            }
        });

    }

    void createAccount() {
        String email = emailText.getText().toString();
        String password = passwordText.getText().toString();
        String confirmPassword = confirmPasswordText.getText().toString();

        boolean isValidated = validateData(email, password, confirmPassword);

        if(!isValidated){
            return;
        }

        createAccountInFirebase(email, password);

    }

    void createAccountInFirebase(String email, String password){
        changeInProgress(true);

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                changeInProgress(false);
                if(task.isSuccessful()){
                    Toast.makeText(getApplicationContext(), "Account successfully created, check your email to verify", Toast.LENGTH_SHORT).show();
                    firebaseAuth.getCurrentUser().sendEmailVerification();
                    firebaseAuth.signOut();
                    startActivity(new Intent(SignUpScreen.this, LoginScreen.class));
                    finish();
                }
                else{
                    Toast.makeText(getApplicationContext(), task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    emailText.setText("");
                    passwordText.setText("");
                    confirmPasswordText.setText("");
                }
            }
        });

    }

    void changeInProgress(boolean inProgress){
        if(inProgress){
            progressBar.setVisibility(View.VISIBLE);
            signUpButton.setVisibility(View.GONE);
        }
        else{
            progressBar.setVisibility(View.GONE);
            signUpButton.setVisibility(View.VISIBLE);
        }
    }

    boolean validateData(String email, String password, String confirmPassword){

        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            emailText.setError("Email is invalid");
            return false;
        }
        if(password.length() < 6){
            passwordText.setError("Password must be greater than 6 characters");
            return false;
        }
        if(!password.equals(confirmPassword)){
            confirmPasswordText.setError("Password does not match");
            return false;
        }

        return true;
    }
}