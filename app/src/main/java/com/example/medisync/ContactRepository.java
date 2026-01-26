package com.example.medisync;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ContactRepository {

    private final FirebaseFirestore db;
    private final String userId;

    public ContactRepository() {
        db = FirebaseFirestore.getInstance();
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    // ADD CONTACT
    public void addContact(Contact contact) {
        Map<String, Object> data = new HashMap<>();
        data.put("name", contact.getName());
        data.put("number", contact.getNumber());
        data.put("address", contact.getAddress());
        data.put("relation", contact.getRelation());

        db.collection("users")
                .document(userId)
                .collection("contacts")
                .add(data);
    }

    // LOAD CONTACTS
    public void getAllContacts(OnContactsLoaded listener) {
        db.collection("users")
                .document(userId)
                .collection("contacts")
                .get()
                .addOnSuccessListener(query -> {
                    ArrayList<Contact> list = new ArrayList<>();
                    for (QueryDocumentSnapshot doc : query) {
                        Contact c = new Contact(
                                doc.getId().hashCode(), // local ID
                                doc.getString("name"),
                                doc.getString("number"),
                                doc.getString("address"),
                                doc.getString("relation")
                        );
                        list.add(c);
                    }
                    listener.onLoaded(list);
                });
    }

    // DELETE CONTACT
    public void deleteContact(String documentId) {
        db.collection("users")
                .document(userId)
                .collection("contacts")
                .document(documentId)
                .delete();
    }

    // CALLBACK
    public interface OnContactsLoaded {
        void onLoaded(ArrayList<Contact> contacts);
    }
}
