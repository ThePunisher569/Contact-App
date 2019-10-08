package com.example.backendlessapp;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.backendless.Backendless;
import com.backendless.BackendlessUser;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.files.BackendlessFile;
import com.backendless.persistence.DataQueryBuilder;

import java.util.List;

public class MainActivity extends AppCompatActivity implements ContactAdapter.ItemClicked {


    ProgressDialog dialog;
    TextView tvUser;
    ImageButton ibCreateContact;

    RecyclerView rvList;
    RecyclerView.Adapter mAdapter;
    RecyclerView.LayoutManager layoutManager;

    DataQueryBuilder queryBuilder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dialog = new ProgressDialog(this);
        tvUser=findViewById(R.id.tvUser);
        ibCreateContact=findViewById(R.id.ibCreateContact);

        tvUser.setText("Logged in as "+ ContactApplication.user.getProperty("name")+" ("+ContactApplication.user.getEmail()+" )");

        ibCreateContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(MainActivity.this,CreateContact.class),2);
            }
        });



        //the recycler view initialization
        rvList=findViewById(R.id.rvList);
        rvList.setHasFixedSize(true);

        layoutManager=new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        rvList.setLayoutManager(layoutManager);



        //a whereClause is used in dataquerybuilder to get the particular user and the contacts that he's created in order to get them
        String whereClause = "userEmail = '" + ContactApplication.user.getEmail() + "'";

        queryBuilder=DataQueryBuilder.create();
        queryBuilder.setWhereClause(whereClause);
        queryBuilder.setPageSize(100);
        //grouping the contents of table by contact name
        queryBuilder.setGroupBy("name");



        dialog.setMessage("Getting contacts...please wait...");
        dialog.show();

        //going to background and getting the table's data
        Backendless.Persistence.of(Contact.class).find(queryBuilder, new AsyncCallback<List<Contact>>() {
            @Override
            public void handleResponse(List<Contact> response) {
                //when the contact list fetched,we'll set it in recycler view,first it's saved into application class
                ContactApplication.list=response;
                mAdapter=new ContactAdapter(MainActivity.this,ContactApplication.list);
                rvList.setAdapter(mAdapter);

                Toast.makeText(MainActivity.this, "Contact List successfully updated!", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }

            @Override
            public void handleFault(BackendlessFault fault) {
                Toast.makeText(MainActivity.this, "Error: "+fault.getMessage(), Toast.LENGTH_SHORT).show();
                dialog.dismiss();
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
    @Override
    public void onItemClicked(int index) {
        Intent intent =new Intent(MainActivity.this,ContactsInfo.class);
        intent.putExtra("index",index);
        startActivityForResult(intent,1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==1){
            if(resultCode==RESULT_OK) {

                mAdapter.notifyDataSetChanged();
            }
        }
        if(requestCode==2){
            if(resultCode==RESULT_OK){
                String whereClause = "userEmail = '" + ContactApplication.user.getEmail() + "'";

                queryBuilder=DataQueryBuilder.create();
                queryBuilder.setWhereClause(whereClause);

                queryBuilder.setGroupBy("name");

                dialog.setMessage("Getting contacts...please wait...");
                dialog.show();

                //going to background and getting the table's data
                Backendless.Persistence.of(Contact.class).find(queryBuilder, new AsyncCallback<List<Contact>>() {
                    @Override
                    public void handleResponse(List<Contact> response) {
                        //when the contact list fetched,we'll set it in recycler view,first it's saved into application class
                        ContactApplication.list=response;
                        mAdapter=new ContactAdapter(MainActivity.this,ContactApplication.list);
                        rvList.setAdapter(mAdapter);

                        Toast.makeText(MainActivity.this, "Contact List successfully updated!", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }

                    @Override
                    public void handleFault(BackendlessFault fault) {
                        Toast.makeText(MainActivity.this, "Error: "+fault.getMessage(), Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                });
            }
        }
    }
}
