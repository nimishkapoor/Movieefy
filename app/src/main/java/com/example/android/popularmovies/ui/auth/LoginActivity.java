package com.example.android.popularmovies.ui.auth;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.popularmovies.R;
import com.example.android.popularmovies.ui.main.MainActivity;
import com.example.android.popularmovies.utilities.AuthenticationHelper;
import com.example.android.popularmovies.utilities.FirebaseHelper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.jaeger.library.StatusBarUtil;

public class LoginActivity extends AppCompatActivity {


    /**
     * Declaring UI widgets
     */
    private EditText emailField;
    private EditText passwordField;
    private TextView forgotPassText;
    private Button signInButton;
    private TextView dontHaveAccountText;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        statusBarTransparent();
        setUpUI();
    }

    /** Sets the theme of the status bar to transparent */
    private void statusBarTransparent(){
        StatusBarUtil.setTransparent(this);
    }

    /**
     * Specifies behaviour of UI components, specifically the sign-In
     */
    private void setUpUI(){
        emailField = findViewById(R.id.loginEmail);
        passwordField = findViewById(R.id.loginpaswd);
        forgotPassText = findViewById(R.id.forgotPass);
        forgotPassText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        signInButton = findViewById(R.id.btnLogIn);
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = emailField.getText().toString();
                String password = passwordField.getText().toString();
                if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)){
                    Toast.makeText(getApplicationContext(), "Please enter valid credentials", Toast.LENGTH_SHORT).show();
                    emailField.requestFocus();
                } else {
                    if (!AuthenticationHelper.isEmailValid(email)){
                        Toast.makeText(getApplicationContext(), "Please enter valid email", Toast.LENGTH_SHORT).show();
                        emailField.requestFocus();
                    } else {
                        progressDialog = new ProgressDialog(view.getContext());
                        progressDialog.setMessage("Please wait while we sign you in!!");
                        progressDialog.setCancelable(false);
                        progressDialog.show();
                        signInUser(email, password);
                    }
                }
            }
        });
        dontHaveAccountText = findViewById(R.id.dont_have_account);
        dontHaveAccountText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Send to SignUp Activity
                Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });
    }

    private void signInUser(String email, String pass){
        FirebaseAuth auth = FirebaseHelper.getFirebaseAuthObject();
        auth.signInWithEmailAndPassword(email, pass)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            progressDialog.dismiss();
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(intent);
                        }
                    }
                });
    }
}
