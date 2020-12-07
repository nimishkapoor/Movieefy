package com.example.android.popularmovies.ui.auth;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.popularmovies.R;
import com.example.android.popularmovies.model.User;
import com.example.android.popularmovies.utilities.AuthenticationHelper;
import com.example.android.popularmovies.utilities.FirebaseHelper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.jaeger.library.StatusBarUtil;

public class SignUpActivity extends AppCompatActivity {

    /**
     * Setting up UI elements using ButterKnife
     */
    private EditText userNameEdittext;
    private EditText emailEdittext;
    private EditText passwordEdittext;
    private EditText confirmPassEdittext;
    private Button signUpButton;
    private TextView signInPage;
    private SharedPreferences sharedPreferences;
    private ProgressDialog progressDialog;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        FirebaseHelper.init();
        statusBarTransparent();
        setUpUI();
    }

    /** Sets the theme of the status bar to transparent */
    private void statusBarTransparent(){
        StatusBarUtil.setTransparent(this);
    }

    /**
     * Specifies behaviour of UI components, specifically the sign up button
     */
    private void setUpUI(){
        userNameEdittext = findViewById(R.id.usernameedittext);
        emailEdittext = findViewById(R.id.ETemail);
        passwordEdittext = findViewById(R.id.ETpassword);
        confirmPassEdittext = findViewById(R.id.Confpassword);
        signUpButton = findViewById(R.id.btnSignUp);
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = userNameEdittext.getText().toString();
                String email = emailEdittext.getText().toString();
                String password = passwordEdittext.getText().toString();
                String confirmPassword = confirmPassEdittext.getText().toString();
                if (name.isEmpty() || email.isEmpty() || password.isEmpty() ||
                        confirmPassword.isEmpty())
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.empty_field_error_mssg), Toast.LENGTH_SHORT).show();
                else {
                    if (!AuthenticationHelper.isEmailValid(email))
                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.invalid_email_error),
                                Toast.LENGTH_SHORT).show();
                    else if (!password.equals(confirmPassword))
                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.pass_confirm_error_mssg),
                                Toast.LENGTH_SHORT).show();
                    else{
                        progressDialog = new ProgressDialog(view.getContext());
                        progressDialog.setMessage("Please wait while we create your account!!");
                        progressDialog.setCancelable(false);
                        progressDialog.show();

                        createNewUser(name, email, password);
                    }
                }
            }
        });
        signInPage = findViewById(R.id.TVSignIn);
        signInPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
    }

    private void createNewUser(final String name, final String email, String password){
        final FirebaseAuth auth = FirebaseHelper.getFirebaseAuthObject();
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            if (progressDialog.isShowing()) progressDialog.dismiss();
                            Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                            startActivity(intent);
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("alalal", "signInWithEmail:success");
                            Toast.makeText(SignUpActivity.this, "New User created successfully. Go to the Login Page to login.",
                                    Toast.LENGTH_SHORT).show();
                            FirebaseAuth firebaseAuth = FirebaseHelper.getFirebaseAuthObject();
                            FirebaseDatabase firebaseDatabase = FirebaseHelper.getFirebaseDatabaseObject();
                            DatabaseReference databaseReference = firebaseDatabase.getReference("users");
                            databaseReference.child(firebaseAuth.getUid()).setValue(new User(name, email));
                            // updateUI(user);
                        } else {
                            if (progressDialog.isShowing()) progressDialog.dismiss();
                            // If sign in fails, display a message to the user.
                            Log.w("lak", "signInWithEmail:failure", task.getException());
                            Toast.makeText(SignUpActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            //  updateUI(null);
                        }
                    }
                });
    }

}
