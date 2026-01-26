package com.example.medisync;

public class Patient {

    private String id;        // 🔥 Firestore document ID
    private String name;
    private int age;
    private String birthdate;
    private String disease;

    // REQUIRED empty constructor for Firestore
    public Patient() {}

    public Patient(String name, int age, String birthdate, String disease) {
        this.name = name;
        this.age = age;
        this.birthdate = birthdate;
        this.disease = disease;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public int getAge() { return age; }
    public String getBirthdate() { return birthdate; }
    public String getDisease() { return disease; }

    public void setName(String name) { this.name = name; }
    public void setAge(int age) { this.age = age; }
    public void setBirthdate(String birthdate) { this.birthdate = birthdate; }
    public void setDisease(String disease) { this.disease = disease; }
}
