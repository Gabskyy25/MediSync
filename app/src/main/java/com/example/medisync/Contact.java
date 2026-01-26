package com.example.medisync;

public class Contact {

    private int id;
    private String name;
    private String number;
    private String address;
    private String relation;

    // REQUIRED empty constructor for Firebase
    public Contact() {
    }

    // Used by Firestore / Repository
    public Contact(int id, String name, String number, String address, String relation) {
        this.id = id;
        this.name = name;
        this.number = number;
        this.address = address;
        this.relation = relation;
    }

    // ✅ Used by ContactInfo.java (NO ID YET)
    public Contact(String name, String number, String address, String relation) {
        this.name = name;
        this.number = number;
        this.address = address;
        this.relation = relation;
    }

    // Getters
    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getNumber() {
        return number;
    }

    public String getAddress() {
        return address;
    }

    public String getRelation() {
        return relation;
    }
}
