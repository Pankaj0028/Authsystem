package com.auth.system.dto;

public class ProfileUpdateRequest {
    private String name;
    private String phone;
    private String bio;
    private String password; // ADDED: Field for the new password
    
    // Getters and Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getBio() { return bio; }
    public void setBio(String bio) { this.bio = bio; }
    public String getPassword() { return password; } 
    public void setPassword(String password) { this.password = password; }
}