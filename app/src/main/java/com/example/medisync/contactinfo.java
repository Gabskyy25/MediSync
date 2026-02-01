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

    private final ArrayList<Contact> contactList = new ArrayList<>();
    private final ArrayList<Contact> filteredList = new ArrayList<>();
    private ContactAdapter adapter;

    private ContactRepository contactRepo;
    private NotificationRepository notificationRepo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contactinfo);

        RecyclerView recyclerView = findViewById(R.id.recyclerViewContacts);
        ImageView fabAddInfo = findViewById(R.id.btnAddInfo);
        Toolbar toolbar = findViewById(R.id.toolbarContacts);
        ImageView backBtn = findViewById(R.id.backbtn);

        setSupportActionBar(toolbar);

        contactRepo = new ContactRepository();
        notificationRepo = new NotificationRepository();

        adapter = new ContactAdapter(filteredList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        setupRecyclerViewLongPress();
        loadContacts();

        backBtn.setOnClickListener(v -> finish());
        fabAddInfo.setOnClickListener(v -> showAddContactDialog());
    }

    /* ================= LOAD ================= */

    private void loadContacts() {
        contactRepo.getAllContacts(contacts -> {
            contactList.clear();
            filteredList.clear();

            contactList.addAll(contacts);
            filteredList.addAll(contacts);

            adapter.notifyDataSetChanged();
        });
    }

    /* ================= MENU ================= */

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

    /* ================= FILTER ================= */

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

    /* ================= LONG PRESS ================= */

    private void setupRecyclerViewLongPress() {
        adapter.setOnItemLongClickListener(position -> {
            Contact contact = filteredList.get(position);

            new AlertDialog.Builder(this)
                    .setTitle(contact.getName())
                    .setItems(new CharSequence[]{"Edit", "Delete"}, (d, i) -> {
                        if (i == 0) {
                            showEditContactDialog(contact, position);
                        } else {

                            notificationRepo.addNotification(
                                    "Contact Removed",
                                    "Contact \"" + contact.getName() + "\" was removed",
                                    "CONTACT",
                                    String.valueOf(contact.getId())
                            );

                            contactRepo.deleteContact(String.valueOf(contact.getId()));

                            contactList.remove(contact);
                            filteredList.remove(position);
                            adapter.notifyItemRemoved(position);
                        }
                    })
                    .show();
        });
    }

    /* ================= ADD ================= */

    private void showAddContactDialog() {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.addcontact);

        EditText etName = dialog.findViewById(R.id.etName);
        EditText etNumber = dialog.findViewById(R.id.etNumber);
        EditText etAddress = dialog.findViewById(R.id.etAddress);
        RadioGroup radioGroupRelation = dialog.findViewById(R.id.radioGroupRelation);
        Button btnConfirm = dialog.findViewById(R.id.btnConfirm);

        btnConfirm.setOnClickListener(v -> {

            String name = etName.getText().toString().trim();
            String number = etNumber.getText().toString().trim();
            String address = etAddress.getText().toString().trim();

            int selectedId = radioGroupRelation.getCheckedRadioButtonId();
            RadioButton rb = dialog.findViewById(selectedId);
            String relation = rb != null ? rb.getText().toString() : "";

            if (!name.isEmpty() && !number.isEmpty()
                    && !address.isEmpty() && !relation.isEmpty()) {

                Contact contact = new Contact(name, number, address, relation);
                contactRepo.addContact(contact);

                notificationRepo.addNotification(
                        "Contact Added",
                        "Contact \"" + name + "\" was added (" + relation + ")",
                        "CONTACT",
                        String.valueOf(contact.getId())
                );

                contactList.add(contact);
                filteredList.add(contact);
                adapter.notifyItemInserted(filteredList.size() - 1);

                dialog.dismiss();
            }
        });

        dialog.show();
    }

    /* ================= EDIT ================= */

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

            String name = etName.getText().toString().trim();
            String number = etNumber.getText().toString().trim();
            String address = etAddress.getText().toString().trim();

            int selectedId = radioGroupRelation.getCheckedRadioButtonId();
            RadioButton rb = dialog.findViewById(selectedId);
            String relation = rb != null ? rb.getText().toString() : "";

            if (!name.isEmpty() && !number.isEmpty()
                    && !address.isEmpty() && !relation.isEmpty()) {

                Contact updated = new Contact(name, number, address, relation);

                // 🔔 UPDATE NOTIFICATION
                notificationRepo.addNotification(
                        "Contact Updated",
                        "Contact \"" + name + "\" was updated\n📞 " + number +
                                "\n👥 " + relation,
                        "CONTACT",
                        String.valueOf(contact.getId())
                );

                contactRepo.deleteContact(String.valueOf(contact.getId()));
                contactRepo.addContact(updated);

                contactList.set(contactList.indexOf(contact), updated);
                filteredList.set(position, updated);

                adapter.notifyItemChanged(position);
                dialog.dismiss();
            }
        });

        dialog.show();
    }
}
