package com.lords.becomebetter;

public class Student {
    private int id;
    private String name;
    private String email;
    private String password;
    private String phone;
    private int age;
    private String skillLevel;
    private int coachId;
    private String createdAt;

    // Default constructor
    public Student() {}

    // Constructor for registration
    public Student(String name, String email, String password, String phone,
                   int age, String skillLevel) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.phone = phone;
        this.age = age;
        this.skillLevel = skillLevel;
        this.coachId = 0; // Will be assigned later
    }

    // Constructor with coach assignment
    public Student(String name, String email, String password, String phone,
                   int age, String skillLevel, int coachId) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.phone = phone;
        this.age = age;
        this.skillLevel = skillLevel;
        this.coachId = coachId;
    }

    // Full constructor
    public Student(int id, String name, String email, String password, String phone,
                   int age, String skillLevel, int coachId, String createdAt) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.phone = phone;
        this.age = age;
        this.skillLevel = skillLevel;
        this.coachId = coachId;
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

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getSkillLevel() {
        return skillLevel;
    }

    public void setSkillLevel(String skillLevel) {
        this.skillLevel = skillLevel;
    }

    public int getCoachId() {
        return coachId;
    }

    public void setCoachId(int coachId) {
        this.coachId = coachId;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "Student{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", age=" + age +
                ", skillLevel='" + skillLevel + '\'' +
                ", coachId=" + coachId +
                '}';
    }
}