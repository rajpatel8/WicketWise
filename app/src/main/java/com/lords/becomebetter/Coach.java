package com.lords.becomebetter;

public class Coach {
    private int id;
    private String name;
    private String email;
    private String password;
    private String phone;
    private int experienceYears;
    private String specialization;
    private String certification;
    private String createdAt;

    // Default constructor
    public Coach() {}

    // Constructor for registration
    public Coach(String name, String email, String password, String phone,
                 int experienceYears, String specialization, String certification) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.phone = phone;
        this.experienceYears = experienceYears;
        this.specialization = specialization;
        this.certification = certification;
    }

    // Full constructor
    public Coach(int id, String name, String email, String password, String phone,
                 int experienceYears, String specialization, String certification, String createdAt) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.phone = phone;
        this.experienceYears = experienceYears;
        this.specialization = specialization;
        this.certification = certification;
        this.createdAt = createdAt;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public int getExperienceYears() {
        return experienceYears;
    }

    public void setExperienceYears(int experienceYears) {
        this.experienceYears = experienceYears;
    }

    public String getSpecialization() {
        return specialization;
    }

    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }

    public String getCertification() {
        return certification;
    }

    public void setCertification(String certification) {
        this.certification = certification;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "Coach{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", experienceYears=" + experienceYears +
                ", specialization='" + specialization + '\'' +
                ", certification='" + certification + '\'' +
                '}';
    }
}