package com.example.medisync;

import android.app.Dialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class contactinfo extends AppCompatActivity {

    private ArrayList<Contact> contactList;
    private ContactAdapter adapter;
    private ContactDatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contactinfo);

        RecyclerView recyclerView = findViewById(R.id.recyclerViewContacts);
        ImageView fabAddInfo = findViewById(R.id.btnAddInfo);

        dbHelper = new ContactDatabaseHelper(this);
        contactList = dbHelper.getAllContacts();

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
                Contact contact = new Contact(name, number, address, relation);
                long id = dbHelper.addContact(contact);
                contact.setId((int) id);

                contactList.add(contact);
                adapter.notifyItemInserted(contactList.size() - 1);
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private void setupRecyclerViewLongPress() {
        adapter.setOnItemLongClickListener(position -> {
            Contact contact = contactList.get(position);


            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(contact.getName())
                    .setItems(new CharSequence[]{"Edit", "Delete"}, (dialogInterface, i) -> {
                        if (i == 0) {
                            // Edit
                            showEditContactDialog(contact, position);
                        } else {
                            // Delete
                            dbHelper.deleteContact(contact.getId());
                            contactList.remove(position);
                            adapter.notifyItemRemoved(position);
                        }
                    })
                    .show();
        });
    }

    private void showEditContactDialog(Contact contact, int position) {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.addcontact);

        EditText etName = dialog.findViewById(R.id.etName);
        EditText etNumber = dialog.findViewById(R.id.etNumber);
        EditText etAddress = dialog.findViewById(R.id.etAddress);
        RadioGroup radioGroupRelation = dialog.findViewById(R.id.radioGroupRelation);
        Button btnConfirm = dialog.findViewById(R.id.btnConfirm);

        // Pre-fill data
        etName.setText(contact.getName());
        etNumber.setText(contact.getNumber());
        etAddress.setText(contact.getAddress());

        // Pre-select relation
        for (int i = 0; i < radioGroupRelation.getChildCount(); i++) {
            RadioButton rb = (RadioButton) radioGroupRelation.getChildAt(i);
            if (rb.getText().toString().equals(contact.getRelation())) {
                rb.setChecked(true);
                break;
            }
        }

        btnConfirm.setText("Update");

        btnConfirm.setOnClickListener(v -> {
            String name = etName.getText().toString();
            String number = etNumber.getText().toString();
            String address = etAddress.getText().toString();
            int selectedId = radioGroupRelation.getCheckedRadioButtonId();
            RadioButton rb = dialog.findViewById(selectedId);
            String relation = rb != null ? rb.getText().toString() : "";

            if (!name.isEmpty() && !number.isEmpty() && !address.isEmpty() && !relation.isEmpty()) {
                contact = new Contact(contact.getId(), name, number, address, relation);
                dbHelper.updateContact(contact);

                contactList.set(position, contact);
                adapter.notifyItemChanged(position);
                dialog.dismiss();
            }
        });

        dialog.show();
    }
}
