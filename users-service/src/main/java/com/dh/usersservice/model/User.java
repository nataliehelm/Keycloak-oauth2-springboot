package com.dh.usersservice.model;

public class User {
    private String userId;
    private String username;
    private String email;
    private String firstName;


    public User(String userId, String username, String email, String firstName) {
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.firstName = firstName;
    }

    public String getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getEmail() {
        return email;
    }
}
