package com.example.medisync;

public class Contact {
    private String name;
    private String number;
    private String address;
    private String relation;

    public Contact(String name, String number, String address, String relation) {
        this.name = name;
        this.number = number;
        this.address = address;
        this.relation = relation;
    }

    public String getName() { return name; }
    public String getNumber() { return number; }
    public String getAddress() { return address; }
    public String getRelation() { return relation; }
}
