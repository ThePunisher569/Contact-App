package com.example.backendlessapp;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.backendless.Backendless;
import com.backendless.BackendlessUser;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;

public class ContactsInfo extends AppCompatActivity {

    private View mProgressView;
    private View mLoginFormView;
    private TextView tvLoad;
    private ImageButton ibCall,ibMail,ibEdit,ibDelete;
    private TextView tvInfoChar,tvInfoName;
    private EditText etInfoName,etInfoMail,etInfoPhone;
    private Button btnSubmit;

    public boolean edit = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts_info);

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
        tvLoad = findViewById(R.id.tvLoad);

        tvInfoChar=findViewById(R.id.tvInfoChar);
        tvInfoName=findViewById(R.id.tvInfoName);
        ibCall=findViewById(R.id.ibCall);
        ibMail=findViewById(R.id.ibMail);
        ibEdit=findViewById(R.id.ibEdit);
        ibDelete=findViewById(R.id.ibDelete);
        etInfoName=findViewById(R.id.etInfoName);
        etInfoMail=findViewById(R.id.etInfoMail);
        etInfoPhone=findViewById(R.id.etInfoPhone);
        btnSubmit=findViewById(R.id.btnSubmit);

        etInfoName.setVisibility(View.GONE);
        etInfoMail.setVisibility(View.GONE);
        etInfoPhone.setVisibility(View.GONE);
        btnSubmit.setVisibility(View.GONE);

        final Intent intent = getIntent();
        final int index =intent.getIntExtra("index",0);
        tvInfoChar.setText(ContactApplication.list.get(index).getName().charAt(0)+"");
        tvInfoName.setText(ContactApplication.list.get(index).getName()+"");


        ibCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(Intent.ACTION_DIAL,Uri.parse("tel:"+ContactApplication.list.get(index).getTelNo())));

            }
        });

        ibMail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =new Intent(Intent.ACTION_SEND);
                intent.setType("text/html");
                intent.putExtra(Intent.EXTRA_EMAIL,ContactApplication.list.get(index).getEmail());
                startActivity(intent);

            }
        });

        ibEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edit = !edit;
                if(edit){
                    etInfoName.setVisibility(View.VISIBLE);
                    etInfoMail.setVisibility(View.VISIBLE);
                    etInfoPhone.setVisibility(View.VISIBLE);
                    btnSubmit.setVisibility(View.VISIBLE);
                }
                else{
                    etInfoName.setVisibility(View.GONE);
                    etInfoMail.setVisibility(View.GONE);
                    etInfoPhone.setVisibility(View.GONE);
                    btnSubmit.setVisibility(View.GONE);
                }

            }
        });

        ibDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder dialog = new AlertDialog.Builder(ContactsInfo.this);
                dialog.setMessage("are you sure to delete this contact?");
                dialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

                dialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        tvLoad.setText("Deleting the contact... please wait...");
                        showProgress(true);

                        Backendless.Persistence.of(Contact.class).remove(ContactApplication.list.get(index), new AsyncCallback<Long>() {
                            @Override
                            public void handleResponse(Long response) {
                                Toast.makeText(ContactsInfo.this, "Contact deleted successfully", Toast.LENGTH_SHORT).show();
                                setResult(RESULT_OK,intent);
                                finish();
                            }

                            @Override
                            public void handleFault(BackendlessFault fault) {
                                Toast.makeText(ContactsInfo.this, "Error: "+ fault.getMessage(), Toast.LENGTH_SHORT).show();
                                showProgress(false);
                            }
                        });
                    }
                });

                dialog.show();
            }
        });

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = etInfoName.getText().toString().trim();
                String tel = etInfoPhone.getText().toString().trim();
                String mail = etInfoMail.getText().toString().trim();

                if(name.isEmpty()||tel.isEmpty()||mail.isEmpty()){
                    Toast.makeText(ContactsInfo.this, "Please enter all fields!!", Toast.LENGTH_SHORT).show();
                }
                else{
                    ContactApplication.list.get(index).setName(name);
                    ContactApplication.list.get(index).setTelNo(tel);
                    ContactApplication.list.get(index).setEmail(mail);
                    tvLoad.setText("Updating the contact.. please wait..");

                    showProgress(true);
                    Backendless.Data.of(Contact.class).save(ContactApplication.list.get(index), new AsyncCallback<Contact>() {
                        @Override
                        public void handleResponse(Contact response) {
                            Toast.makeText(ContactsInfo.this, "Contact successfully updated", Toast.LENGTH_SHORT).show();
                            showProgress(false);
                            setResult(RESULT_OK);
                            finish();

                        }

                        @Override
                        public void handleFault(BackendlessFault fault) {
                            Toast.makeText(ContactsInfo.this, "Error: "+fault.getMessage(), Toast.LENGTH_SHORT).show();

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


