package com.example.backendlessapp;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.backendless.Backendless;
import com.backendless.BackendlessUser;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.files.BackendlessFile;

public class MainActivity extends AppCompatActivity {

    Button btnCreateContact,btnContactList;
    ProgressDialog dialog;
    TextView tvUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        dialog = new ProgressDialog(this);
        btnCreateContact=findViewById(R.id.btnCreateContact);
        btnContactList=findViewById(R.id.btnContactList);
        tvUser=findViewById(R.id.tvUser);


        tvUser.setText("Logged in as "+ ContactApplication.user.getProperty("name")+" ("+ContactApplication.user.getEmail()+" )");
        btnCreateContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,CreateContact.class));
            }
        });


        btnContactList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,ContactList.class));
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.action_bar_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==R.id.menuLogout){
            dialog.setMessage("Logging out...please wait...");
            dialog.show();

            //Logout a user using this method
            Backendless.UserService.logout(new AsyncCallback<Void>() {
                @Override
                public void handleResponse(Void response) {
                    Toast.makeText(MainActivity.this, "Logout Successful!", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(MainActivity.this,Login.class));
                    MainActivity.this.finish();
                }

                @Override
                public void handleFault(BackendlessFault fault) {
                    Toast.makeText(MainActivity.this, "Error: "+fault.getMessage(), Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                }
            });
        }
        return super.onOptionsItemSelected(item);
    }
}
