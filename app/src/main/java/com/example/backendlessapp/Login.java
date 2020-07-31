package com.example.backendlessapp;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.backendless.Backendless;
import com.backendless.BackendlessUser;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.persistence.local.UserIdStorageFactory;

public class Login extends AppCompatActivity {

    private View mProgressView;
    private View mLoginFormView;
    private TextView tvLoad;


    private TextView tvReset;
    private EditText etEmail,etPass,etResetEmail;
    private Button btnLogin,btnRegister;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
        tvLoad = findViewById(R.id.tvLoad);


        etEmail=findViewById(R.id.etEmail);
        etPass=findViewById(R.id.etPass);
        btnLogin=findViewById(R.id.btnLogin);
        btnRegister=findViewById(R.id.btnRegister);
        tvReset=findViewById(R.id.tvReset);

        //on creating app a progress bar will show to authenticate a user
        tvLoad.setText("Authenticating User..Please wait...");
        showProgress(true);

        //checking whether the user was logged in previously, The fourth argument in Login method should be true to use validating
        Backendless.UserService.isValidLogin(new AsyncCallback<Boolean>() {
            @Override
            public void handleResponse(Boolean response) {
                //if true it'll take it to the main activity
                if(response){
                    tvLoad.setText("Signing you in..please wait...");

                    //getting user's data by it's objectId
                    String userObjectId = UserIdStorageFactory.instance().getStorage().get();

                    //parsing in the current object id to take current user in main activity
                    Backendless.Data.of(BackendlessUser.class).findById(userObjectId, new AsyncCallback<BackendlessUser>() {
                        @Override
                        public void handleResponse(BackendlessUser response) {
                            //current user is saved in application class
                            ContactApplication.user=response;
                            Toast.makeText(Login.this, "Sign in successful!", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(Login.this,MainActivity.class));
                            Login.this.finish();
                        }

                        @Override
                        public void handleFault(BackendlessFault fault) {
                            Toast.makeText(Login.this, "Error: "+fault.getMessage(), Toast.LENGTH_SHORT).show();
                            showProgress(false);
                        }
                    });
                }
                //else, Authentication will fail and the user needs to login manually
                else{
                    showProgress(false);
                }
            }

            @Override
            public void handleFault(BackendlessFault fault) {

                Toast.makeText(Login.this, "Error: "+fault.getMessage(), Toast.LENGTH_SHORT).show();
                showProgress(false);
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mail=etEmail.getText().toString().trim();
                String pass=etPass.getText().toString().trim();
                if(etEmail.getText().toString().trim().isEmpty()||etPass.getText().toString().trim().isEmpty()){
                    Toast.makeText(Login.this, "Please Enter all fields!", Toast.LENGTH_SHORT).show();
                }
                else{
                    tvLoad.setText("Loggin in..Please wait...");
                    showProgress(true);

                    //Logging into user's account and taking to main activity if succeeded
                    Backendless.UserService.login(mail, pass, new AsyncCallback<BackendlessUser>() {
                        @Override

                        //on Successfully authenticating
                        public void handleResponse(BackendlessUser response) {
                            //current user saved in application class
                            ContactApplication.user=response;
                            Toast.makeText(Login.this, "Successfully Logged In!", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(Login.this,MainActivity.class));
                            Login.this.finish();
                        }

                        @Override
                        public void handleFault(BackendlessFault fault) {
                            Toast.makeText(Login.this, "Error: "+fault.getMessage(), Toast.LENGTH_SHORT).show();

                            showProgress(false);
                        }
                    }, true);
                }

            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(Login.this,Register.class));

            }
        });


        tvReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(Login.this);
                dialog.setMessage("In order to reset the password you need to enter the Email address.We will send a link to reset password.");
                View dialogView = getLayoutInflater().inflate(R.layout.dialog_view,null);
                dialog.setView(dialogView);
                etResetEmail=dialogView.findViewById(R.id.etResetEmail);

                dialog.setPositiveButton("RESET", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(etResetEmail.getText().toString().trim().isEmpty()){
                            Toast.makeText(Login.this, "Please make sure the Email is entered", Toast.LENGTH_SHORT).show();
                        }
                        else{

                            tvLoad.setText("Email is sending to your mail box,please Check it");
                            showProgress(true);


                            //restoring the password by sending an Email
                            Backendless.UserService.restorePassword(etResetEmail.getText().toString().trim(), new AsyncCallback<Void>() {
                                @Override

                                //On success progress hides and displaying toast
                                public void handleResponse(Void response) {
                                    showProgress(false);
                                    Toast.makeText(Login.this, "Email sent!", Toast.LENGTH_SHORT).show();

                                }

                                @Override
                                public void handleFault(BackendlessFault fault) {
                                    showProgress(false);
                                    Toast.makeText(Login.this, "Error: "+fault.getMessage(), Toast.LENGTH_SHORT).show();

                                }
                            });


                        }

                    }
                });

                dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Nothing to do here bitch, Sundar will automatically handle it
                        dialog.dismiss();

                    }
                });


                dialog.show();

            }
        });
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });

            tvLoad.setVisibility(show ? View.VISIBLE : View.GONE);
            tvLoad.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    tvLoad.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            tvLoad.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }


}
