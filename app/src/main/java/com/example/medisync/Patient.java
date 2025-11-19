package com.example.medisync;

public class Patient {
    private int id;
    private String name;
    private int age;
    private String birthdate;
    private String disease;


    public Patient(String name, int age, String birthdate, String disease) {
        this.name = name;
        this.age = age;
        this.birthdate = birthdate;
        this.disease = disease;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public int getAge() { return age; }
    public void setAge(int age) { this.age = age; }

    public String getBirthdate() { return birthdate; }
    public void setBirthdate(String birthdate) { this.birthdate = birthdate; }

    public String getDisease() { return disease; }
    public void setDisease(String disease) { this.disease = disease; }
}