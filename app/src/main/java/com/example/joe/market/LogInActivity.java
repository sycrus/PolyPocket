package com.example.joe.market;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class LogInActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "LogInActivity";

    EditText editTextLogInEmail, editTextLogInPassword;
    TextView mTestEmail;
    ImageView mTestImage;
    String mLogInEmail, mLogInPassword;

    private FirebaseAuth mAuth;
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    //private DatabaseReference carRef = database.getReference("car");


    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and proceed to MainActivity
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        editTextLogInEmail = (EditText) findViewById(R.id.editTextLogInEmail);
        editTextLogInPassword = (EditText) findViewById(R.id.editTextLogInPassword);

        mTestEmail = (TextView) findViewById(R.id.testing_email);
        mAuth = FirebaseAuth.getInstance();

        findViewById(R.id.btnLogIn).setOnClickListener(this);
        findViewById(R.id.textViewSignUp).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnLogIn:
                logInUser();
                break;

            case R.id.textViewSignUp:
                finish();
                startActivity(new Intent(getApplicationContext(), SignUpActivity.class));
                break;
        }
    }

    public void logInUser() {

        mLogInEmail = editTextLogInEmail.getText().toString().trim();
        mLogInPassword = editTextLogInPassword.getText().toString().trim();

        mAuth.signInWithEmailAndPassword(mLogInEmail, mLogInPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (task.isSuccessful()) {
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    finish();
                } else {
                    Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
