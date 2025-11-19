package com.example.medisync;

public class Contact {
    private int id; // SQLite primary key
    private String name;
    private String number;
    private String address;
    private String relation;

    public Contact(int id, String name, String number, String address, String relation) {
        this.id = id;
        this.name = name;
        this.number = number;
        this.address = address;
        this.relation = relation;
    }

    public Contact(String name, String number, String address, String relation) {
        this(-1, name, number, address, relation);
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public String getNumber() { return number; }
    public String getAddress() { return address; }
    public String getRelation() { return relation; }

    public void setId(int id) { this.id = id; }
}
