package com.joe.market.login;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.joe.market.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import com.google.firebase.auth.FirebaseAuth;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "SignUpActivity";

    EditText editTextUserName, editTextSignUpEmail, editTextSignUpPassword, editTextConfirmPassword;
    String mName, mEmail;
    ImageView profileImage, mTestImage;

    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference userRef = mDatabase.getReference("user");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        editTextSignUpEmail = (EditText) findViewById(R.id.editTextSignUpEmail);
        editTextSignUpPassword = (EditText) findViewById(R.id.editTextSignUpPassword);
        editTextConfirmPassword = (EditText) findViewById(R.id.editTextConfirmPassword);

        mAuth = FirebaseAuth.getInstance();

        findViewById(R.id.btnSignUp).setOnClickListener(this);
        findViewById(R.id.textViewLogIn).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnSignUp:
                signUpUser();
                break;

            case R.id.textViewLogIn:
                finish();
                startActivity(new Intent(this, LogInActivity.class));
                break;
        }
    }

    public void signUpUser() {

        //user name
        final String name = editTextUserName.getText().toString().trim();
        //user email
        final String email = editTextSignUpEmail.getText().toString().trim();
        //user password
        String password = editTextSignUpPassword.getText().toString().trim();
        String confirmPassword = editTextConfirmPassword.getText().toString().trim();

        boolean validEmail = isValidEmail(email);
        boolean validName = isValidName(name);
        boolean validPassword = isValidPassword(password, confirmPassword);
        if (!validEmail || !validName || !validPassword) return;

        mName = name;
        mEmail = email;

        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (task.isSuccessful()) {
                    Toast.makeText(getApplicationContext(), "Registered! Proceed to Log In", Toast.LENGTH_SHORT).show();
                    //go to Log In Activity
                    //set User Name method
                    startActivity(new Intent(SignUpActivity.this, LogInActivity.class));
                    finish();
                } else {
                    if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                        Toast.makeText(getApplicationContext(), "You are already registered. Please Log In", Toast.LENGTH_SHORT).show();
                        //go to Log In Activity
                        startActivity(new Intent(SignUpActivity.this, LogInActivity.class));
                        finish();
                    } else {
                        Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }

                }
            }
        });

    }

    private boolean isValidEmail(String email) {
        boolean isGoodEmail =
                (email != null && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches());
        if (!isGoodEmail) {
            editTextSignUpEmail.setError("Please enter a valid email address");
            return false;
        }
        return isGoodEmail;
    }

    private boolean isValidName(String name) {
        if (name.equals("")) {
            editTextUserName.setError("Please enter your name");
            return false;
        }
        return true;
    }

    private boolean isValidPassword(String password, String confirmPassword) {
        if (password.length() < 6) {
            editTextSignUpPassword.setError("Please create a password containing at least 6 characters");
            return false;
        } else if (!password.equals(confirmPassword)) {
            editTextSignUpPassword.setError("Passwords do not match");
            return false;
        }
        return true;
    }



}


