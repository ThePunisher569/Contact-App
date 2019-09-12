package com.example.backendlessapp;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.backendless.Backendless;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;

public class CreateContact extends AppCompatActivity {

    EditText etCreateName,etCreateEmail,etCreatePhone;
    Button btnCreate;
    private View mProgressView;
    private View mLoginFormView;
    private TextView tvLoad;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_contact);


        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
        tvLoad = findViewById(R.id.tvLoad);


        etCreateName=findViewById(R.id.etCreateName);
        etCreateEmail=findViewById(R.id.etCreateEmail);
        etCreatePhone=findViewById(R.id.etCreatePhone);
        btnCreate=findViewById(R.id.btnCreate);

        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(etCreateName.getText().toString().trim().isEmpty()||etCreateEmail.getText().toString().trim().isEmpty()||etCreatePhone.getText().toString().trim().isEmpty()){
                    Toast.makeText(CreateContact.this, "Please Enter all fields", Toast.LENGTH_SHORT).show();
                }
                else{
                    tvLoad.setText("Creating a new contact...please wait...");
                    showProgress(true);

                    Contact contact=new Contact();
                    contact.setName(etCreateName.getText().toString().trim());
                    contact.setEmail(etCreateEmail.getText().toString().trim());
                    contact.setTelNo(etCreatePhone.getText().toString().trim());
                    contact.setUserEmail(ContactApplication.user.getEmail());

                    Backendless.Persistence.save(contact, new AsyncCallback<Contact>() {
                        @Override
                        public void handleResponse(Contact response) {
                            Toast.makeText(CreateContact.this, "Contact Created successfully", Toast.LENGTH_SHORT).show();
                            showProgress(false);
                            etCreateEmail.setText(null);
                            etCreateName.setText(null);
                            etCreatePhone.setText(null);
                        }

                        @Override
                        public void handleFault(BackendlessFault fault) {

                            Toast.makeText(CreateContact.this, "Error: "+fault.getMessage(), Toast.LENGTH_SHORT).show();
                            showProgress(false);
                        }
                    });
                }
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
