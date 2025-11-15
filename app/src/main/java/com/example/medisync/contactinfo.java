package com.example.medisync;

import android.app.Dialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class contactinfo extends AppCompatActivity {

    private ArrayList<Contact> contactList;
    private ContactAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contactinfo);

        RecyclerView recyclerView = findViewById(R.id.recyclerViewContacts);
        ImageView fabAddInfo = findViewById(R.id.btnAddInfo);

        contactList = new ArrayList<>();
        adapter = new ContactAdapter(contactList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        fabAddInfo.setOnClickListener(v -> showAddContactDialog());
    }

    private void showAddContactDialog() {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.addcontact);

        EditText etName = dialog.findViewById(R.id.etName);
        EditText etNumber = dialog.findViewById(R.id.etNumber);
        EditText etAddress = dialog.findViewById(R.id.etAddress);
        RadioGroup radioGroupRelation = dialog.findViewById(R.id.radioGroupRelation);
        Button btnConfirm = dialog.findViewById(R.id.btnConfirm);

        btnConfirm.setOnClickListener(v -> {
            String name = etName.getText().toString();
            String number = etNumber.getText().toString();
            String address = etAddress.getText().toString();
            int selectedId = radioGroupRelation.getCheckedRadioButtonId();
            RadioButton rb = dialog.findViewById(selectedId);
            String relation = rb != null ? rb.getText().toString() : "";

            if (!name.isEmpty() && !number.isEmpty() && !address.isEmpty() && !relation.isEmpty()) {
                contactList.add(new Contact(name, number, address, relation));
                adapter.notifyItemInserted(contactList.size() - 1);
                dialog.dismiss();
            }
        });

        dialog.show();
    }
}
