package com.example.medisync;

import android.app.Dialog;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class contactinfo extends AppCompatActivity {

    private ArrayList<Contact> contactList;
    private ArrayList<Contact> filteredList;
    private ContactAdapter adapter;
    private ContactDBHelper dbHelper;
    private NotificationDBHelper notificationDB; // ✅ ADD

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contactinfo);

        RecyclerView recyclerView = findViewById(R.id.recyclerViewContacts);
        ImageView fabAddInfo = findViewById(R.id.btnAddInfo);
        Toolbar toolbar = findViewById(R.id.toolbarContacts);

        setSupportActionBar(toolbar);

        dbHelper = new ContactDBHelper(this);
        notificationDB = new NotificationDBHelper(this); // ✅ INIT

        contactList = dbHelper.getAllContacts();
        filteredList = new ArrayList<>(contactList);

        adapter = new ContactAdapter(filteredList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        setupRecyclerViewLongPress();

        fabAddInfo.setOnClickListener(v -> showAddContactDialog());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_contacts, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override public boolean onQueryTextSubmit(String query) { return false; }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterContacts(newText);
                return true;
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.action_sort_name) {
            Collections.sort(filteredList, Comparator.comparing(Contact::getName));
            adapter.notifyDataSetChanged();
            return true;
        }

        if (item.getItemId() == R.id.action_sort_relation) {
            Collections.sort(filteredList, Comparator.comparing(Contact::getRelation));
            adapter.notifyDataSetChanged();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void filterContacts(String query) {
        filteredList.clear();

        if (query.isEmpty()) {
            filteredList.addAll(contactList);
        } else {
            for (Contact c : contactList) {
                if (c.getName().toLowerCase().contains(query.toLowerCase()) ||
                        c.getRelation().toLowerCase().contains(query.toLowerCase())) {
                    filteredList.add(c);
                }
            }
        }
        adapter.notifyDataSetChanged();
    }

    private void setupRecyclerViewLongPress() {
        adapter.setOnItemLongClickListener(position -> {
            Contact contact = filteredList.get(position);

            new AlertDialog.Builder(this)
                    .setTitle(contact.getName())
                    .setItems(new CharSequence[]{"Edit", "Delete"}, (d, i) -> {
                        if (i == 0) {
                            showEditContactDialog(contact, position);
                        } else {

                            // ✅ DELETE CONTACT
                            dbHelper.deleteContact(contact.getId());

                            // ✅ ADD DELETE NOTIFICATION
                            notificationDB.addNotification(
                                    "Contact Deleted",
                                    contact.getName() + " was removed",
                                    "CONTACT",
                                    contact.getId()
                            );

                            contactList.remove(contact);
                            filteredList.remove(position);
                            adapter.notifyItemRemoved(position);
                        }
                    })
                    .show();
        });
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

                // ✅ ADD NOTIFICATION
                notificationDB.addNotification(
                        "New Contact Added",
                        name + " was added",
                        "CONTACT",
                        contact.getId()
                );

                contactList.add(contact);
                filteredList.add(contact);
                adapter.notifyItemInserted(filteredList.size() - 1);

                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private void showEditContactDialog(Contact contact, int position) {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.addcontact);

        EditText etName = dialog.findViewById(R.id.etName);
        EditText etNumber = dialog.findViewById(R.id.etNumber);
        EditText etAddress = dialog.findViewById(R.id.etAddress);
        RadioGroup radioGroupRelation = dialog.findViewById(R.id.radioGroupRelation);
        Button btnConfirm = dialog.findViewById(R.id.btnConfirm);

        etName.setText(contact.getName());
        etNumber.setText(contact.getNumber());
        etAddress.setText(contact.getAddress());

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

                Contact updated = new Contact(contact.getId(), name, number, address, relation);
                dbHelper.updateContact(updated);

                int originalIndex = contactList.indexOf(contact);
                contactList.set(originalIndex, updated);
                filteredList.set(position, updated);

                adapter.notifyItemChanged(position);
                dialog.dismiss();
            }
        });

        dialog.show();
    }
}
