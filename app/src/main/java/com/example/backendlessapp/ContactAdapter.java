package com.example.backendlessapp;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ViewHolder> {
    private List<Contact> contacts;
    ItemClicked activity;


    public interface ItemClicked{

        void onItemClicked(int index);
    }


    public ContactAdapter(Context context,List<Contact> list){
        contacts=list;
        activity=(ItemClicked) context;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView tvName,tvChar,tvMail;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvChar=itemView.findViewById(R.id.tvChar);
            tvMail=itemView.findViewById(R.id.tvMail);
            tvName=itemView.findViewById(R.id.tvName);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    activity.onItemClicked(contacts.indexOf((Contact)v.getTag()));
                }
            });
        }
    }

    @NonNull
    @Override
    public ContactAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.contacts_row_layout,viewGroup,false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ContactAdapter.ViewHolder viewHolder, int i) {
        viewHolder.itemView.setTag(contacts.get(i));
        viewHolder.tvName.setText(contacts.get(i).getName() +"("+contacts.get(i).getTelNo()+")");
        viewHolder.tvMail.setText(contacts.get(i).getEmail());
        viewHolder.tvChar.setText(contacts.get(i).getName().charAt(0)+"");

    }

    @Override
    public int getItemCount() {
        return contacts.size();
    }
}
